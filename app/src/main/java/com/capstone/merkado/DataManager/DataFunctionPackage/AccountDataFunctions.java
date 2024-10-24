package com.capstone.merkado.DataManager.DataFunctionPackage;

import android.content.Context;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.FirebaseData;
import com.capstone.merkado.DataManager.SharedPref;
import com.capstone.merkado.DataManager.ValueReturn.ReturnStatus;
import com.capstone.merkado.DataManager.ValueReturn.ValueReturn;
import com.capstone.merkado.DataManager.ValueReturn.ValueReturnWithStatus;
import com.capstone.merkado.Helpers.FirebaseCharacters;
import com.capstone.merkado.Helpers.StringHash;
import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.Objects.AccountData;
import com.capstone.merkado.Objects.VerificationCode;
import com.google.common.reflect.TypeToken;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class AccountDataFunctions {

    /**
     * Checks if email exists in the database.
     *
     * @param email         raw email value.
     * @param booleanReturn return callback.
     */
    public static void emailExists(String email, ValueReturn<Boolean> booleanReturn) {
        // create FirebaseData object
        FirebaseData firebaseData = new FirebaseData();

        // encode the email for Firebase
        String encodedEmail = FirebaseCharacters.encode(email);

        // call isKeyExists method to check if the email is a key in the "accounts" node.
        // return the callback results.
        firebaseData.isKeyExists("accounts", encodedEmail, booleanReturn::valueReturn);
    }

    public static void verifyAccount(String email, String password, ValueReturnWithStatus<Account> accountReturn) {
        // create FirebaseData object
        FirebaseData firebaseData = new FirebaseData();

        // encode the email for Firebase
        String encodedEmail = FirebaseCharacters.encode(email);

        firebaseData.retrieveData(String.format("accounts/%s", encodedEmail), dataSnapshot -> {
            if (dataSnapshot == null) {
                accountReturn.valueReturn(new Account(email, ""), ReturnStatus.CANNOT_RETRIEVE_INFORMATION);
                return;
            }
            if (!dataSnapshot.exists()) {
                accountReturn.valueReturn(new Account(email, ""), ReturnStatus.WRONG_EMAIL);
                return;
            }
            Object passwordObj = dataSnapshot.child("password").getValue();
            Object usernameObj = dataSnapshot.child("username").getValue();
            if (passwordObj != null) {
                String hashedPassword = passwordObj.toString();
                String username = "User";
                if (usernameObj != null) {
                    username = usernameObj.toString();
                }
                if (hashedPassword.equals(StringHash.hashPassword(password))) {
                    accountReturn.valueReturn(new Account(email, username), ReturnStatus.SUCCESS);
                } else {
                    accountReturn.valueReturn(new Account(email, ""), ReturnStatus.WRONG_PASSWORD);
                }
            } else {
                accountReturn.valueReturn(new Account(email, ""), ReturnStatus.CANNOT_RETRIEVE_INFORMATION);
            }
        });
    }

    /**
     * Verifies if password is correct or not.
     *
     * @param email         raw email.
     * @param password      password to compare.
     * @param booleanReturn returns <b>True</b> or <b>False</b> values if the password is correct or not.
     */
    public static void comparePasswords(String email, String password, ValueReturn<Boolean> booleanReturn) {
        // create FirebaseData object
        FirebaseData firebaseData = new FirebaseData();

        // encode the email for Firebase
        String encodedEmail = FirebaseCharacters.encode(email);

        firebaseData.retrieveData(String.format("accounts/%s", encodedEmail), dataSnapshot -> {
            if (dataSnapshot == null) return;
            if (!dataSnapshot.exists()) {
                booleanReturn.valueReturn(false);
                return;
            }
            Object passwordObj = dataSnapshot.child("password").getValue();
            if (passwordObj != null) {
                String hashedPassword = passwordObj.toString();
                if (hashedPassword.equals(StringHash.hashPassword(password))) {
                    booleanReturn.valueReturn(true);
                } else {
                    booleanReturn.valueReturn(false);
                }
            } else {
                booleanReturn.valueReturn(false);
            }
        });
    }

    /**
     * Sign in account in Shared Preferences.
     *
     * @param context  application context
     * @param email    raw email
     * @param username raw username
     */
    private static void signInAccountInSharedPref(Context context, String email, String username) {
        // create a hashmap of account details.
        HashMap<String, String> sharedPrefMap = new HashMap<>();
        sharedPrefMap.put("EMAIL", email);
        sharedPrefMap.put("USERNAME", username);
        sharedPrefMap.put("LAST_LOGGED_IN", String.valueOf(System.currentTimeMillis()));

        // convert to Json string.
        Gson gson = new Gson();
        String sharedPrefValue = gson.toJson(sharedPrefMap);

        // encode to Base64
        String encodedStringValue = StringHash.encodeString(sharedPrefValue);

        // save the string to shared preferences
        SharedPref.write(context, SharedPref.KEEP_SIGNED_IN, encodedStringValue);
    }

    public static void signInAccount(Context context, Account account) {
        // create the string to be saved.
        String email = account.getEmail();
        String username = account.getUsername();

        signInAccountInSharedPref(context, email, username);

        // update the Firebase Realtime Database
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(String.format("accounts/%s/lastOnline", FirebaseCharacters.encode(email)), currentTimeMillis());
    }

    public static CompletableFuture<Account> getSignedIn(Context context) {
        // get the string from shared preferences
        String encodedStringValue = SharedPref.readString(context, SharedPref.KEEP_SIGNED_IN, "");

        // return null if there is no string.
        if (encodedStringValue.isEmpty()) return CompletableFuture.completedFuture(null);

        // decode from Base64
        String sharedPrefValue = StringHash.decodeString(encodedStringValue);

        // convert to HashMap from JSON string.
        Gson gson = new Gson();
        HashMap<String, String> sharedPrefMap = gson.fromJson(sharedPrefValue, new TypeToken<HashMap<String, String>>() {
        }.getType());

        /*
         * EXTRACT THE sharedPrefMap DETAILS
         */

        String lastLoggedInString = sharedPrefMap.get("LAST_LOGGED_IN");
        String email = sharedPrefMap.get("EMAIL");
        String username = sharedPrefMap.get("USERNAME");

        // return null if one of the data is null or empty. this means the user must log in again.
        if (lastLoggedInString == null || lastLoggedInString.isEmpty() ||
                email == null || email.isEmpty() ||
                username == null || username.isEmpty()) return CompletableFuture.completedFuture(null);

        /*
         * GET THE TIME THE USER LAST LOGGED IN
         */
        long lastLoggedIn;

        try {
            lastLoggedIn = Long.parseLong(lastLoggedInString);
        } catch (Exception ignore) {
            // return null if the lastLoggedIn data is not a valid Long. this means the user must log in again.
            return CompletableFuture.completedFuture(null);
        }

        // check if the user's log in is within the past 7 days. Note that lastLoggedIn is millis. 604800000 millis is 7 days.
        if (System.currentTimeMillis() - lastLoggedIn < 604800000) {
            // if the user logged in within the past 7 days, cross-check if the account still exists in the database.
            return checkEmailExistsFuture(email).thenCompose(exists -> {
                if (exists) return CompletableFuture.completedFuture(new Account(email, username));
                else return CompletableFuture.completedFuture(null);
            });
        }

        // if the user did not log in within the past 7 days, require log in.
        return CompletableFuture.completedFuture(null);
    }

    public static CompletableFuture<Boolean> checkEmailExistsFuture(String email) {
        // create FirebaseData object
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();

        // encode the email for Firebase
        String encodedEmail = FirebaseCharacters.encode(email);

        firebaseData.retrieveData(String.format("accounts/%s/username", encodedEmail), future::complete);

        return future.thenCompose(dataSnapshot ->
                CompletableFuture.completedFuture(dataSnapshot != null && dataSnapshot.exists()));
    }

    /**
     * Uses an Email Sending Service to send the code to the email. API: <a href="https://www.mailersend.com/">MailerSend</a>.
     *
     * @param verificationCode VerificationCode instance.
     */
    @SuppressWarnings("unused")
    public static void sendCodeThroughEmail(VerificationCode verificationCode) {
        // TODO: Use the API to send the code through email.
    }

    /**
     * Signs up the user account.
     *
     * @param email    raw email.
     * @param password raw password.
     * @return Account instance.
     */
    public static Account signUpAccount(String email, String password) {
        FirebaseData firebaseData = new FirebaseData();

        // generate temporary username
        String username = String.format(Locale.getDefault(), "User %d", currentTimeMillis());

        // store data
        AccountData accountData = new AccountData();
        accountData.setLastOnline(currentTimeMillis());
        accountData.setPassword(StringHash.hashPassword(password));
        accountData.setUsername(username);

        // save the data to accounts/{encoded email}
        String path = String.format("accounts/%s", FirebaseCharacters.encode(email));
        firebaseData.setValue(path, accountData);

        // return account data
        Account account = new Account();
        account.setEmail(email);
        account.setUsername(username);

        return account;
    }

    /**
     * Signs out the account by updating the "lastOnline" value in Firebase and deleting the SharedPref item for KEEP_SIGNED_IN.
     *
     * @param context application context.
     * @param account user account.
     */
    public static void signOut(Context context, Account account) {
        FirebaseData firebaseData = new FirebaseData();

        // update lastOnline in accounts/{email}.
        firebaseData.setValue(String.format("accounts/%s/lastOnline", FirebaseCharacters.encode(account.getEmail())), currentTimeMillis());

        // delete from sharedPref.
        SharedPref.delete(context, SharedPref.KEEP_SIGNED_IN);
    }

    /**
     * Resets password of the account in Firebase.
     *
     * @param email    raw email.
     * @param password raw password.
     */
    public static void resetPassword(String email, String password) {
        FirebaseData firebaseData = new FirebaseData();
        // save the new password to accounts/{encoded email}/password.
        firebaseData.setValue(String.format("accounts/%s/password", FirebaseCharacters.encode(email)), StringHash.hashPassword(password));
    }

    /**
     * Change username in Firebase and Shared Preferences.
     *
     * @param context  application context.
     * @param username raw username.
     * @param email    raw email.
     */
    public static void changeUsername(Context context, String username, String email) {
        FirebaseData firebaseData = new FirebaseData();

        // save the new password to accounts/{encoded email}/username.
        firebaseData.setValue(String.format("accounts/%s/username", FirebaseCharacters.encode(email)), username);

        getSignedIn(context).thenAccept(account -> {
            if (account != null) {
                signInAccountInSharedPref(context, account.getEmail(), username);
            }
        });
    }

    private static Long currentTimeMillis() {
        Merkado merkado = Merkado.getInstance();
        return merkado.currentTimeMillis();
    }
}
