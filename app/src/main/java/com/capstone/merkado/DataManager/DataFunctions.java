package com.capstone.merkado.DataManager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.capstone.merkado.Helpers.FirebaseCharacters;
import com.capstone.merkado.Helpers.StringHash;
import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.Objects.ServerDataObjects.EconomyBasic;
import com.capstone.merkado.Objects.StoryDataObjects.LineGroup;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor;
import com.capstone.merkado.Objects.StoryDataObjects.Story;
import com.capstone.merkado.Objects.TaskDataObjects.TaskData;
import com.capstone.merkado.Objects.VerificationCode;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.common.reflect.TypeToken;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class DataFunctions {

    public interface BooleanReturn {

        /**
         * Callback return for boolean datatype.
         *
         * @param bool return value.
         */
        void booleanReturn(Boolean bool);
    }

    public interface StringReturn {
        /**
         * Callback return for string datatype.
         *
         * @param string return value.
         */
        void stringReturn(String string);
    }

    public interface EconomyBasicListReturn {
        /**
         * Callback return for EconomyBasic List datatype.
         * @param economyBasicList return value.
         */
        void listReturn(List<EconomyBasic> economyBasicList);
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
     *
     * @param context       application context.
     * @param email         raw email.
     * @param password      password to compare.
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
     *
     * @param email    raw email.
     * @param password raw password.
     */
    public static void resetPassword(String email, String password) {
        FirebaseData firebaseData = new FirebaseData();
        // save the new password to accounts/{encoded email}/password.
        firebaseData.addValue(String.format("accounts/%s/password", FirebaseCharacters.encode(email)), StringHash.hashPassword(password));
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
        firebaseData.addValue(String.format("accounts/%s/username", FirebaseCharacters.encode(email)), username);

        Account account = getSignedIn(context);
        if (account != null) {
            signInAccountInSharedPref(context, account.getEmail(), username);
        }
    }

    public static void getAbout(Context context, StringReturn stringReturn) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData(context, "appData/About", dataSnapshot -> {
            if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                stringReturn.stringReturn(dataSnapshot.getValue().toString());
            } else {
                stringReturn.stringReturn("");
            }
        });
    }

    public static void getTermsAndConditions(Context context, StringReturn stringReturn) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData(context, "appData/TermsAndConditions", dataSnapshot -> {
            if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                stringReturn.stringReturn(dataSnapshot.getValue().toString());
            } else {
                stringReturn.stringReturn("");
            }
        });
    }

    /**
     * Retrieves the EconomyBasic List.
     * @param account currently signed in account.
     * @param economyBasicListReturn callback.
     */
    public static void getEconomyBasic(Account account, EconomyBasicListReturn economyBasicListReturn) {
        FirebaseData firebaseData = new FirebaseData();

        // CHECK THE ACCOUNT FIRST FOR PLAYER INDEX DATA.
        firebaseData.retrieveData("accounts/" + FirebaseCharacters.encode(account.getEmail()) + "/player", dataSnapshot -> {
            // if the snapshot is null, just return null.
            if (dataSnapshot == null) {
                economyBasicListReturn.listReturn(null);
                return;
            }

            // initialize EconomyBasic list (for the return value) and Task<Void> list (for checking if the process is done first).
            List<EconomyBasic> economyBasicList = new ArrayList<>();
            List<Task<Void>> tasks = new ArrayList<>();

            // iterate all the player indices.
            for (DataSnapshot playerIndices : dataSnapshot.getChildren()) {
                // take note of the current index in iteration.
                Integer playerIndex = playerIndices.getValue(Integer.class);

                // add a TaskCompletionSource to tasks.
                TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();
                tasks.add(taskCompletionSource.getTask());

                // SEARCH FOR SERVER INDEX FROM THE PLAYER DATA USING THE PLAYER INDEX.
                firebaseData.retrieveData(String.format(Locale.getDefault(), "player/%d/server", playerIndex), dataSnapshot1 -> {
                    // if dataSnapshot is null, mark the task as completed.
                    if (dataSnapshot1 == null) {
                        taskCompletionSource.setResult(null);
                        return;
                    }

                    // get the server index
                    Integer serverIndex = dataSnapshot1.getValue(Integer.class);

                    // SEARCH FOR THE SERVER USING THE SERVER INDEX.
                    firebaseData.retrieveData(String.format(Locale.getDefault(), "server/%d", serverIndex), dataSnapshot2 -> {
                        // if dataSnapshot is null, mark the task as completed.
                        if (dataSnapshot2 == null) {
                            taskCompletionSource.setResult(null);
                            return;
                        }

                        // create the EconomyBasic instance from this server.
                        Object nameObj = dataSnapshot2.child("name").getValue();
                        String name = nameObj != null ? nameObj.toString() : String.format(Locale.getDefault(), "Server %d", serverIndex);
                        long onlinePlayersCount = dataSnapshot2.child("onlinePlayers").getChildrenCount();

                        EconomyBasic economyBasic = new EconomyBasic(name, Math.toIntExact(onlinePlayersCount), null, playerIndex);
                        economyBasicList.add(economyBasic);

                        // mark the task as completed.
                        taskCompletionSource.setResult(null);
                    });
                });
            }

            // Wait for all tasks to complete
            Tasks.whenAll(tasks).addOnCompleteListener(task -> {
                economyBasicListReturn.listReturn(economyBasicList);
            });
        });
    }

    /**
     * Gets the player's data using the playerId.
     * @param playerId player's id.
     * @return Player instance.
     */
    public static PlayerFBExtractor getPlayerDataFromId(Integer playerId) {
        final CompletableFuture<PlayerFBExtractor> future = new CompletableFuture<>();

        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData(String.format(Locale.getDefault(), "player/%d", playerId), new FirebaseData.FirebaseDataCallback() {
            @Override
            public void onDataReceived(DataSnapshot dataSnapshot) {
                PlayerFBExtractor playerFBExtractor = dataSnapshot.getValue(PlayerFBExtractor.class);
                future.complete(playerFBExtractor);
            }
        });

        try {
            return future.get();  // This will block until the data is received
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets line groups data using its index or id. This is used for story-mode.
     * @param lineGroupIndex index or id of line group.
     * @return LineGroup instance.
     */
    public static LineGroup getLineGroupFromId(Integer lineGroupIndex){
        final CompletableFuture<LineGroup> future = new CompletableFuture<>();

        FirebaseData firebaseData = new FirebaseData();

        firebaseData.retrieveData(String.format(Locale.getDefault(), "story/lineGroup/%d", lineGroupIndex), dataSnapshot -> {
            LineGroup lineGroup = dataSnapshot.getValue(LineGroup.class);
            future.complete(lineGroup);
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets story data using its index or id. This is used for story-mode.
     * @param storyId index or id of the story.
     * @return Story instance.
     */
    public static Story getStoryFromId(Integer storyId) {
        final CompletableFuture<Story> future = new CompletableFuture<>();

        FirebaseData firebaseData = new FirebaseData();

        firebaseData.retrieveData(String.format(Locale.getDefault(), "story/story/%d", storyId), dataSnapshot -> {
            Story story = dataSnapshot.getValue(Story.class);
            future.complete(story);
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets task data using its index or id. This is used for story-mode.
     * @param taskId index or id of the task.
     * @return Task instance.
     */
    public static TaskData getTaskFromId(Integer taskId) {
        final CompletableFuture<TaskData> future = new CompletableFuture<>();

        FirebaseData firebaseData = new FirebaseData();

        firebaseData.retrieveData(String.format(Locale.getDefault(), "tasks/%d", taskId), dataSnapshot -> {
            TaskData task = dataSnapshot.getValue(TaskData.class);
            future.complete(task);
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void checkServerExistence(Context context, String serverCode, ServerExistenceCallback callback) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData(context, String.format("server/%s", serverCode), new FirebaseData.FirebaseDataCallback() {
            @Override
            public void onDataReceived(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    callback.onServerExists();
                } else {
                    callback.onServerDoesNotExist();
                }
            }
        });
    }

    public static void getCurrentAccount(Context context, AccountReturn accountReturn) {
        // Get the signed-in account using the getSignedIn method
        Account signedInAccount = getSignedIn(context);

        // Check if the signed-in account is not null
        if (signedInAccount != null) {
            String currentUserEmail = signedInAccount.getEmail();
            String encodedEmail = FirebaseCharacters.encode(currentUserEmail);

            FirebaseData firebaseData = new FirebaseData();

            // Retrieve the account details from Firebase using the encoded email
            firebaseData.retrieveData(context, String.format("accounts/%s", encodedEmail), dataSnapshot -> {
                if (dataSnapshot.exists()) {
                    // Extract the username from the retrieved data
                    String username = dataSnapshot.child("username").getValue(String.class);
                    // Return the Account object with the current email and username
                    accountReturn.accountReturn(new Account(currentUserEmail, username));
                } else {
                    // Return null if the account does not exist
                    accountReturn.accountReturn(null);
                }
            });
        } else {
            // Return null if no user is signed in
            accountReturn.accountReturn(null);
        }
    }

    public static void addPlayerToServer(Context context, String serverCode, Account account) {
        FirebaseData firebaseData = new FirebaseData();
        String playerId = UUID.randomUUID().toString(); // Generate a unique player ID

        // Create player data
        Map<String, Object> playerData = new HashMap<>();
        playerData.put("accountId", account.getEmail());
        playerData.put("exp", 0); // Assuming initial exp is 0
        playerData.put("money", 0); // Assuming initial money is 0
        playerData.put("server", serverCode);
        playerData.put("taskQueue", new HashMap<>()); // Assuming empty taskQueue
        playerData.put("storyQueue", new HashMap<>()); // Assuming empty storyQueue
        playerData.put("history", new HashMap<>()); // Assuming empty history
        playerData.put("inventory", new HashMap<>()); // Assuming empty inventory

        // Add player data to Firebase under player/{playerId}
        firebaseData.addValues(String.format("player/%s", playerId), playerData);

        // Add playerId to server/{serverCode}/players
        firebaseData.retrieveData(context, String.format("server/%s/players", serverCode), dataSnapshot -> {
            if (dataSnapshot.exists()) {
                long playerCount = dataSnapshot.getChildrenCount();
                firebaseData.addValue(String.format("server/%s/players/%d", serverCode, playerCount), playerId);
            } else {
                firebaseData.addValue(String.format("server/%s/players/0", serverCode), playerId);
            }
        });

        // Add playerId to accounts/{encodedEmail}/player
        String encodedEmail = FirebaseCharacters.encode(account.getEmail());
        firebaseData.retrieveData(context, String.format("accounts/%s", encodedEmail), dataSnapshot -> {
            if (dataSnapshot.exists()) {
                long playerCount = dataSnapshot.child("player").getChildrenCount();
                firebaseData.addValue(String.format("accounts/%s/player/%d", encodedEmail, playerCount), playerId);
            } else {
                // Handle case where account data does not exist (should not happen ideally)
            }
        });
    }

    public interface ServerExistenceCallback {
        void onServerExists();
        void onServerDoesNotExist();
    }


}
