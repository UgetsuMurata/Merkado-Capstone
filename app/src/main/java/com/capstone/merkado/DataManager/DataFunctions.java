package com.capstone.merkado.DataManager;

import android.content.Context;

import com.capstone.merkado.Helpers.FirebaseCharacters;
import com.capstone.merkado.Helpers.StringHash;
import com.capstone.merkado.Objects.Account;
import com.google.common.reflect.TypeToken;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;

import java.util.HashMap;
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
        firebaseData.isKeyExists("accounts", encodedEmail, new FirebaseData.BooleanCallback() {
            @Override
            public void callback(Boolean bool) {
                // return the callback results.
                booleanReturn.booleanReturn(bool);
            }
        });
    }

    public static void verifyAccount(Context context, String email, String password, AccountReturn accountReturn) {
        // create FirebaseData object
        FirebaseData firebaseData = new FirebaseData();

        // encode the email for Firebase
        String encodedEmail = FirebaseCharacters.encode(email);

        firebaseData.retrieveData(context, String.format("accounts/%s", encodedEmail), new FirebaseData.FirebaseDataCallback() {
            @Override
            public void onDataReceived(DataSnapshot dataSnapshot) {
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
            }
        });
    }

    public static void signInAccount(Context context, Account account) {
        // create the string to be saved.
        String email = account.getEmail();
        String username = account.getUsername();

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

        // update the Firebase RTDB
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.addValue(String.format("accounts/%s/lastOnline", FirebaseCharacters.encode(email)), System.currentTimeMillis());
    }

    public static Account getSignedIn(Context context) {
        /*
         * GET THE DATA FROM SHAREDPREF.
         */
        // get the string from shared preferences
        String encodedStringValue = SharedPref.readString(context, SharedPref.KEEP_SIGNED_IN, "");

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
}
