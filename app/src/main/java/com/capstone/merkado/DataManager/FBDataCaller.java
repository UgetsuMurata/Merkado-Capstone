package com.capstone.merkado.DataManager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.capstone.merkado.Helpers.FirebaseCharacters;
import com.capstone.merkado.Helpers.StringHash;
import com.capstone.merkado.Objects.Account;
import com.google.firebase.database.DataSnapshot;

public class FBDataCaller {

    public interface BooleanReturn {

        /**
         * Callback return for boolean datatype.
         * @param bool return value.
         */
        void booleanReturn(Boolean bool);
    }

    public interface AccountReturn {

        /**
         * Callback return for Account datatype.
         * @param account return value.
         */
        void accountReturn(Account account);
    }

    /**
     * Checks if email exists in the database.
     * @param email raw email value.
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

    public static void verifyAccount(Context context, String email, String password, AccountReturn accountReturn){
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
                    if (usernameObj != null){
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
}
