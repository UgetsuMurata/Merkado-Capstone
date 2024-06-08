package com.capstone.merkado.DataManager;

import android.content.Context;

import com.capstone.merkado.Helpers.FirebaseCharacters;
import com.capstone.merkado.Helpers.StringHash;
import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.Objects.VerificationCode;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DataFunctions {

    public interface BooleanReturn {

        /**
         * Callback return for boolean datatype.
         *
         * @param bool return value.
         */
        void booleanReturn(Boolean bool);
    }

    public interface AccountReturn {

        /**
         * Callback return for Account datatype.
         *
         * @param account return value.
         */
        void accountReturn(Account account);
    }

    /**
     * Checks if email exists in the database.
     *
     * @param email         raw email value.
     * @param booleanReturn return callback.
     */
    public static void emailExists(String email, BooleanReturn booleanReturn) {
        // create FirebaseData object
        FirebaseData firebaseData = new FirebaseData();

        // encode the email for Firebase
        String encodedEmail = FirebaseCharacters.encode(email);

        // call isKeyExists method to check if the email is a key in the "accounts" node.
        // return the callback results.
        firebaseData.isKeyExists("accounts", encodedEmail, booleanReturn::booleanReturn);
    }

    public static void verifyAccount(Context context, String email, String password, AccountReturn accountReturn) {
        // create FirebaseData object
        FirebaseData firebaseData = new FirebaseData();

        // encode the email for Firebase
        String encodedEmail = FirebaseCharacters.encode(email);

        firebaseData.retrieveData(context, String.format("accounts/%s", encodedEmail), dataSnapshot -> {
            if (!dataSnapshot.exists()) {
                accountReturn.accountReturn(new Account(email, "[ERROR:WRONG_EMAIL]"));
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
                    accountReturn.accountReturn(new Account(email, username));
                } else {
                    accountReturn.accountReturn(new Account(email, "[ERROR:WRONG_PASSWORD]"));
                }
            } else {
                accountReturn.accountReturn(new Account(email, "[ERROR:CANNOT_RETRIEVE_INFORMATION]"));
            }
        });
    }

    /**
     * Verifies if password is correct or not.
     * @param context application context.
     * @param email raw email.
     * @param password password to compare.
     * @param booleanReturn returns <b>True</b> or <b>False</b> values if the password is correct or not.
     */
    public static void comparePasswords(Context context, String email, String password, BooleanReturn booleanReturn) {
        // create FirebaseData object
        FirebaseData firebaseData = new FirebaseData();

        // encode the email for Firebase
        String encodedEmail = FirebaseCharacters.encode(email);

        firebaseData.retrieveData(context, String.format("accounts/%s", encodedEmail), dataSnapshot -> {
            if (!dataSnapshot.exists()) {
                booleanReturn.booleanReturn(false);
                return;
            }
            Object passwordObj = dataSnapshot.child("password").getValue();
            if (passwordObj != null) {
                String hashedPassword = passwordObj.toString();
                if (hashedPassword.equals(StringHash.hashPassword(password))) {
                    booleanReturn.booleanReturn(true);
                } else {
                    booleanReturn.booleanReturn(false);
                }
            } else {
                booleanReturn.booleanReturn(false);
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
        firebaseData.addValue(String.format("accounts/%s/lastOnline", FirebaseCharacters.encode(email)), System.currentTimeMillis());
    }

    public static Account getSignedIn(Context context) {
        // get the string from shared preferences
        String encodedStringValue = SharedPref.readString(context, SharedPref.KEEP_SIGNED_IN, "");

        // return null if there is no string.
        if (encodedStringValue.isEmpty()) return null;

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
                username == null || username.isEmpty()) return null;

        /*
         * GET THE TIME THE USER LAST LOGGED IN
         */
        long lastLoggedIn;

        try {
            lastLoggedIn = Long.parseLong(lastLoggedInString);
        } catch (Exception ignore) {
            // return null if the lastLoggedIn data is not a valid Long. this means the user must log in again.
            return null;
        }

        // check if the user's log in is within the past 7 days. Note that lastLoggedIn is millis. 604800000 millis is 7 days.
        if (System.currentTimeMillis() - lastLoggedIn < 604800000) {
            // if the user logged in within the past 7 days, return account. No log in required.
            return new Account(email, username);
        }

        // if the user did not log in within the past 7 days, require log in.
        return null;
    }

    /**
     * Uses an Email Sending Service to send the code to the email. API: <a href="https://www.mailersend.com/">MailerSend</a>.
     *
     * @param verificationCode VerificationCode instance.
     */
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
        Map<String, Object> values = new HashMap<>();

        // generate temporary username
        String username = String.format(Locale.getDefault(), "User %d", System.currentTimeMillis());

        // store data
        values.put("lastOnline", System.currentTimeMillis());
        values.put("password", StringHash.hashPassword(password));
        values.put("username", username);

        // save the data to accounts/{encoded email}.
        firebaseData.addValues(String.format("accounts/%s", FirebaseCharacters.encode(email)), values);

        return new Account(email, username);
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
        firebaseData.addValue(String.format("accounts/%s/lastOnline", FirebaseCharacters.encode(account.getEmail())), System.currentTimeMillis());

        // delete from sharedPref.
        SharedPref.delete(context, SharedPref.KEEP_SIGNED_IN);
    }

    /**
     * Resets password of the account in Firebase.
     * @param email raw email.
     * @param password raw password.
     */
    public static void resetPassword(String email, String password) {
        FirebaseData firebaseData = new FirebaseData();
        // save the new password to accounts/{encoded email}/password.
        firebaseData.addValue(String.format("accounts/%s/password", FirebaseCharacters.encode(email)), StringHash.hashPassword(password));
    }

    /**
     * Change username in Firebase and Shared Preferences.
     * @param context application context.
     * @param username raw username.
     * @param email raw email.
     */
    public static void changeUsername(Context context, String username, String email) {
        FirebaseData firebaseData = new FirebaseData();

        // save the new password to accounts/{encoded email}/username.
        firebaseData.addValue(String.format("accounts/%s/username", FirebaseCharacters.encode(email)), username);

        Account account = getSignedIn(context);
        if (account != null) {
            signInAccountInSharedPref(context, account.getEmail(), username);
        }
    }
}
