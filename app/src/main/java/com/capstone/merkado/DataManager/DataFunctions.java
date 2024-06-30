package com.capstone.merkado.DataManager;

import android.content.Context;

import androidx.annotation.Nullable;

import com.capstone.merkado.DataManager.StaticData.GameResourceCaller;
import com.capstone.merkado.Helpers.FirebaseCharacters;
import com.capstone.merkado.Helpers.StringHash;
import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.Objects.PlayerDataObjects.Inventory;
import com.capstone.merkado.Objects.PlayerDataObjects.Player;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor.StoryQueue;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor.TaskQueue;
import com.capstone.merkado.Objects.QASDataObjects.QASItems;
import com.capstone.merkado.Objects.QASDataObjects.QASItems.QASDetail;
import com.capstone.merkado.Objects.ServerDataObjects.EconomyBasic;
import com.capstone.merkado.Objects.ServerDataObjects.ResourceData;
import com.capstone.merkado.Objects.StoresDataObjects.PlayerMarkets;
import com.capstone.merkado.Objects.StoresDataObjects.PlayerMarkets.OnSale;
import com.capstone.merkado.Objects.StoresDataObjects.StoreBuyingData;
import com.capstone.merkado.Objects.StoryDataObjects.Chapter;
import com.capstone.merkado.Objects.StoryDataObjects.LineGroup;
import com.capstone.merkado.Objects.TaskDataObjects.TaskData;
import com.capstone.merkado.Objects.VerificationCode;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.common.reflect.TypeToken;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

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
         *
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

    public interface PlayerMarketsListener {
        /**
         * Callback listener for PlayerMarkets datatype.
         *
         * @param playerMarkets return value.
         */
        void onMarketUpdate(PlayerMarkets playerMarkets);
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

    public static void verifyAccount(String email, String password, AccountReturn accountReturn) {
        // create FirebaseData object
        FirebaseData firebaseData = new FirebaseData();

        // encode the email for Firebase
        String encodedEmail = FirebaseCharacters.encode(email);

        firebaseData.retrieveData(String.format("accounts/%s", encodedEmail), dataSnapshot -> {
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
     * @param email         raw email.
     * @param password      password to compare.
     * @param booleanReturn returns <b>True</b> or <b>False</b> values if the password is correct or not.
     */
    public static void comparePasswords(String email, String password, BooleanReturn booleanReturn) {
        // create FirebaseData object
        FirebaseData firebaseData = new FirebaseData();

        // encode the email for Firebase
        String encodedEmail = FirebaseCharacters.encode(email);

        firebaseData.retrieveData(String.format("accounts/%s", encodedEmail), dataSnapshot -> {
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
        firebaseData.setValue(String.format("accounts/%s/lastOnline", FirebaseCharacters.encode(email)), System.currentTimeMillis());
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
        firebaseData.setValues(String.format("accounts/%s", FirebaseCharacters.encode(email)), values);

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
        firebaseData.setValue(String.format("accounts/%s/lastOnline", FirebaseCharacters.encode(account.getEmail())), System.currentTimeMillis());

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

        Account account = getSignedIn(context);
        if (account != null) {
            signInAccountInSharedPref(context, account.getEmail(), username);
        }
    }

    public static void getAbout(StringReturn stringReturn) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData("appData/About", dataSnapshot -> {
            if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                stringReturn.stringReturn(dataSnapshot.getValue().toString());
            } else {
                stringReturn.stringReturn("");
            }
        });
    }

    public static void getTermsAndConditions(StringReturn stringReturn) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData("appData/TermsAndConditions", dataSnapshot -> {
            if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                stringReturn.stringReturn(dataSnapshot.getValue().toString());
            } else {
                stringReturn.stringReturn("");
            }
        });
    }

    /**
     * Retrieves the EconomyBasic List.
     *
     * @param account                currently signed in account.
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

                    if (dataSnapshot1.getValue() == null) {
                        taskCompletionSource.setResult(null);
                        return;
                    }

                    // get the server index
                    String serverIndex = dataSnapshot1.getValue().toString();

                    // SEARCH FOR THE SERVER USING THE SERVER INDEX.
                    firebaseData.retrieveData(String.format("server/%s", serverIndex), dataSnapshot2 -> {
                        // if dataSnapshot is null, mark the task as completed.
                        if (dataSnapshot2 == null) {
                            taskCompletionSource.setResult(null);
                            return;
                        }

                        // create the EconomyBasic instance from this server.
                        Object nameObj = dataSnapshot2.child("name").getValue();
                        String name = nameObj != null ? nameObj.toString() : String.format("Server %s", serverIndex);
                        long onlinePlayersCount = dataSnapshot2.child("onlinePlayers").getChildrenCount();

                        EconomyBasic economyBasic = new EconomyBasic(serverIndex, name, Math.toIntExact(onlinePlayersCount), null, playerIndex);
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
     *
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
     *
     * @param lineGroupIndex index or id of line group.
     * @return LineGroup instance.
     */
    public static LineGroup getLineGroupFromId(Long chapterId, Long sceneId, Integer lineGroupIndex) {
        final CompletableFuture<LineGroup> future = new CompletableFuture<>();

        FirebaseData firebaseData = new FirebaseData();

        firebaseData.retrieveData(String.format(Locale.getDefault(), "story/%d/scenes/%d/lineGroup/%d", chapterId, sceneId, lineGroupIndex), dataSnapshot -> {
            if (!dataSnapshot.exists())
                future.complete(null);
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
     *
     * @param chapterId index or id of the story.
     * @return Story instance.
     */
    public static Chapter getChapterFromId(Integer chapterId) {
        final CompletableFuture<Chapter> future = new CompletableFuture<>();

        FirebaseData firebaseData = new FirebaseData();

        firebaseData.retrieveData(String.format(Locale.getDefault(), "story/%d/", chapterId), dataSnapshot -> {
            Chapter chapter = dataSnapshot.getValue(Chapter.class);
            future.complete(chapter);
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
     *
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

    public static Boolean checkServerExistence(String serverCode) {
        final CompletableFuture<Boolean> future = new CompletableFuture<>();
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData(String.format("server/%s", serverCode), dataSnapshot -> {
            future.complete(dataSnapshot.exists());
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Long getNextPlayerIndex() {
        CompletableFuture<Long> future = new CompletableFuture<>();
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData("player", dataSnapshot -> future.complete(dataSnapshot.getChildrenCount()));

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    //For adding the player to the server
    public static Boolean addPlayerToServer(String serverCode, Account account) {
        FirebaseData firebaseData = new FirebaseData();
        Long playerId = getNextPlayerIndex();

        if (playerId == -1L) return false;

        // populate storyQueueList
        List<StoryQueue> storyQueueList = new ArrayList<>();
        StoryQueue storyQueue = new StoryQueue();
        storyQueue.setChapter(0);
        storyQueue.setCurrentLineGroup(0);
        storyQueue.setCurrentScene(0);
        storyQueue.setTaken(false);
        storyQueue.setNextLineGroup(1);
        storyQueue.setNextScene(1);
        storyQueueList.add(storyQueue);

        // populate inventoryList
        List<Inventory> inventoryList = new ArrayList<>();
        Inventory inventory = new Inventory();
        inventory.setResourceId(0);
        inventory.setQuantity(1);
        inventoryList.add(inventory);

        // Create player data
        Map<String, Object> playerData = new HashMap<>();
        playerData.put("accountId", account.getEmail());
        playerData.put("exp", 0);
        playerData.put("money", 2000);
        playerData.put("server", serverCode);
        playerData.put("inventory", inventoryList);
        playerData.put("storyQueue", storyQueueList);

        // Add player data to Firebase under player/{playerId}
        Boolean success = firebaseData.setValues(String.format("player/%s", playerId), playerData);

        // return false if not successful.
        if (success == null || !success) return false;

        // ADD PLAYER ID TO SERVER PLAYERS LIST
        addPlayerToServer(serverCode, playerId);

        // ADD PLAYER ID TO ACCOUNT
        addPlayerToAccount(account.getEmail(), playerId);
        return true;
    }

    private static void addPlayerToServer(String serverCode, Long playerId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        FirebaseData firebaseData = new FirebaseData();

        firebaseData.retrieveData(String.format("server/%s/players", serverCode), dataSnapshot -> {
            if (dataSnapshot.exists()) {
                long playerCount = dataSnapshot.getChildrenCount();
                firebaseData.setValue(String.format("server/%s/players/%s", serverCode, playerCount), playerId);
            } else {
                firebaseData.setValue(String.format("server/%s/players/0", serverCode), playerId);
            }

            future.complete(null);
        });

        try {
            future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void addPlayerToAccount(String email, Long playerId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        FirebaseData firebaseData = new FirebaseData();
        String encodedEmail = FirebaseCharacters.encode(email);

        firebaseData.retrieveData(String.format("accounts/%s/player", encodedEmail), new FirebaseData.FirebaseDataCallback() {
            @Override
            public void onDataReceived(DataSnapshot dataSnapshot) {
                long index = dataSnapshot.getChildrenCount();
                firebaseData.setValue(String.format("accounts/%s/player/%s", encodedEmail, index), playerId);
                future.complete(null);
            }
        });

        try {
            future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void changeCurrentLineGroup(Integer lineGroupId, Integer playerId, Integer storyQueueId) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(String.format(Locale.getDefault(), "player/%d/storyQueue/%d/currentLineGroup", playerId, storyQueueId), lineGroupId);
    }

    public static void changeNextLineGroup(Integer lineGroupId, Integer playerId, Integer storyQueueId) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(String.format(Locale.getDefault(), "player/%d/storyQueue/%d/nextLineGroup", playerId, storyQueueId), lineGroupId);
    }

    public static void changeCurrentScene(Integer sceneId, Integer playerId, Integer storyQueueId) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(String.format(Locale.getDefault(), "player/%d/storyQueue/%d/currentScene", playerId, storyQueueId), sceneId);
    }

    public static void changeNextScene(Integer sceneId, Integer playerId, Integer storyQueueId) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(String.format(Locale.getDefault(), "player/%d/storyQueue/%d/nextScene", playerId, storyQueueId), sceneId);
    }

    public static CompletableFuture<List<QASItems>> getAllQuests(Integer playerId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> dataSnapshotFuture = new CompletableFuture<>();

        firebaseData.retrieveData(String.format(Locale.getDefault(), "player/%d/storyQueue", playerId), dataSnapshotFuture::complete);

        return dataSnapshotFuture.thenCompose(dataSnapshot -> {
            Map<Integer, TaskQueue> questsQueueMap = new HashMap<>();
            Integer currentIndex = 0;
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                TaskQueue taskQueue = ds.getValue(TaskQueue.class);
                questsQueueMap.put(currentIndex, taskQueue);
            }
            return processTasksQueue(questsQueueMap);
        });
    }

    public static CompletableFuture<List<QASItems>> getAllStories(Integer playerId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> dataSnapshotFuture = new CompletableFuture<>();

        firebaseData.retrieveData(String.format(Locale.getDefault(), "player/%d/storyQueue", playerId), dataSnapshotFuture::complete);

        return dataSnapshotFuture.thenCompose(dataSnapshot -> {
            Map<Integer, StoryQueue> storyQueueMap = new HashMap<>();
            Integer currentIndex = 0;
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                StoryQueue storyQueue = ds.getValue(StoryQueue.class);
                storyQueueMap.put(currentIndex, storyQueue);
                currentIndex++;
            }
            return processStoryQueue(storyQueueMap);
        });
    }

    public static CompletableFuture<List<QASItems>> getAllQuestsAndStories(Integer playerId) {
        CompletableFuture<List<QASItems>> allStoriesFuture = getAllStories(playerId);
        CompletableFuture<List<QASItems>> allQuestsFuture = getAllQuests(playerId);

        return allStoriesFuture.thenCombine(allQuestsFuture, (allStories, allQuests) -> {
            List<QASItems> allItems = new ArrayList<>();
            if (allStories != null) {
                allItems.addAll(allStories);
            }
            if (allQuests != null) {
                allItems.addAll(allQuests);
            }
            return allItems;
        });
    }

    private static CompletableFuture<List<QASItems>> processStoryQueue(Map<Integer, StoryQueue> storyQueueMap) {
        Map<String, QASDetail> qasDetailsMapping = new HashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Map.Entry<Integer, StoryQueue> entry : storyQueueMap.entrySet()) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Chapter chapter = getChapterFromId(entry.getValue().getChapter());
                if (chapter == null) return;
                Chapter.Scene scene = chapter.getScenes().get(entry.getValue().getCurrentScene());

                List<QASDetail.QASReward> qasRewards = new ArrayList<>();
                for (Chapter.GameRewards gameRewards : scene.getRewards()) {
                    QASDetail.QASReward qasReward = new QASDetail.QASReward();
                    qasReward.setResourceId(gameRewards.getResourceId());
                    qasReward.setResourceQuantity(gameRewards.getResourceQuantity());
                    qasReward.setResourceImage(GameResourceCaller.getResourcesImage(gameRewards.getResourceId()));
                    qasRewards.add(qasReward);
                }

                QASDetail qasDetail = new QASDetail();
                qasDetail.setQasName(String.format("%s: %s", chapter.getChapter(), scene.getScene()));
                qasDetail.setQueueId(entry.getKey());
                qasDetail.setQasShortDescription(chapter.getShortDescription());
                qasDetail.setQasDescription(scene.getDescription());
                qasDetail.setQasRewards(qasRewards);

                qasDetailsMapping.put(chapter.getCategory(), qasDetail);
            });
            futures.add(future);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    Map<String, QASItems> qasItemsMapping = groupQASDetailsToQASItems("STORIES", qasDetailsMapping);
                    return new ArrayList<>(qasItemsMapping.values());
                });
    }

    private static CompletableFuture<List<QASItems>> processTasksQueue(Map<Integer, TaskQueue> storyQueueMap) {
        Map<String, QASDetail> qasDetailsMapping = new HashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Map.Entry<Integer, TaskQueue> entry : storyQueueMap.entrySet()) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                // retrieve the TaskData.
                TaskData taskData = getTaskFromId(entry.getValue().getTask());

                // process the TaskData into QASDetail.
                if (taskData == null)
                    return; // do not include from final list if it cannot be retrieved.

                // process the rewards
                List<QASDetail.QASReward> qasRewards = new ArrayList<>();
                for (Chapter.GameRewards gameRewards : taskData.getRewards()) {
                    QASDetail.QASReward qasReward = new QASDetail.QASReward();
                    qasReward.setResourceId(gameRewards.getResourceId());
                    qasReward.setResourceQuantity(gameRewards.getResourceQuantity());
                    qasReward.setResourceImage(GameResourceCaller.getResourcesImage(gameRewards.getResourceId()));
                    qasRewards.add(qasReward);
                }

                // create the QASDetail
                QASDetail qasDetail = new QASDetail();
                qasDetail.setQasName(taskData.getTitle());
                qasDetail.setQueueId(entry.getKey());
                qasDetail.setQasShortDescription(taskData.getShortDescription());
                qasDetail.setQasDescription(taskData.getDescription());
                qasDetail.setQasRewards(qasRewards);

                // add the detail inside the map.
                qasDetailsMapping.put(taskData.getCategory(), qasDetail);
            });
            futures.add(future);
        }

        // group the QASDetails into its bigger list, QASItems. Keep in mind that we know this group is "QUESTS".
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    Map<String, QASItems> qasItemsMapping = groupQASDetailsToQASItems("STORIES", qasDetailsMapping);
                    return new ArrayList<>(qasItemsMapping.values());
                });
    }

    private static Map<String, QASItems> groupQASDetailsToQASItems(String group, Map<String, QASDetail> qasDetailMap) {
        Map<String, QASItems> qasItemsMapping = new HashMap<>();
        for (Map.Entry<String, QASDetail> qasDetailEntry : qasDetailMap.entrySet()) {
            if (qasItemsMapping.containsKey(qasDetailEntry.getKey())) {
                QASItems qasItems = qasItemsMapping.get(qasDetailEntry.getKey());
                if (qasItems != null) {
                    // add the QASDetails inside the QASItems.
                    qasItems.getQasDetails().add(qasDetailEntry.getValue());

                    // update the value of the map.
                    qasItemsMapping.put(qasDetailEntry.getKey(), qasItems);
                    continue;
                }
            }
            // create the QASItems.
            QASItems qasItems = new QASItems();
            qasItems.setQasCategory(qasDetailEntry.getKey());
            qasItems.setQasGroup(group);
            qasItems.setQasDetails(new ArrayList<>());
            qasItems.getQasDetails().add(qasDetailEntry.getValue());

            // put it in the map.
            qasItemsMapping.put(qasDetailEntry.getKey(), qasItems);
        }
        return qasItemsMapping;
    }

    public static CompletableFuture<ResourceData> getResourceDataById(Integer id) {
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        FirebaseData firebaseData = new FirebaseData();

        firebaseData.retrieveData(String.format(Locale.getDefault(), "resource/%d", id),
                future::complete);

        return future.thenCompose(dataSnapshot -> {
           if (dataSnapshot == null) return CompletableFuture.completedFuture(null);
           return CompletableFuture.completedFuture(dataSnapshot.getValue(ResourceData.class));
        });
    }

    public static CompletableFuture<List<PlayerMarkets>> getAllPlayerMarkets(String serverId) {
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        FirebaseData firebaseData = new FirebaseData();

        firebaseData.retrieveData(String.format("server/%s/market/playerMarkets", serverId), future::complete);
        return future.thenCompose(dataSnapshot -> {
            List<PlayerMarkets> playerMarketsList = new ArrayList<>();
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                playerMarketsList.add(ds.getValue(PlayerMarkets.class));
            }
            return CompletableFuture.completedFuture(playerMarketsList);
        });
    }

    public static CompletableFuture<MarketError> buyFromPlayerMarket(StoreBuyingData storeBuyingData) {
        FirebaseData firebaseData = new FirebaseData();
        String childPath = String.format(Locale.getDefault(),
                "server/%s/market/playerMarkets/%d/onSale/%d",
                storeBuyingData.getServerId(),
                storeBuyingData.getPlayerMarketId(),
                storeBuyingData.getOnSaleId());


        CompletableFuture<DataSnapshot> onSaleFuture = new CompletableFuture<>();

        firebaseData.retrieveData(childPath, onSaleFuture::complete);
        return onSaleFuture.thenCompose(ignored -> {
            DataSnapshot onSaleSnapshot = onSaleFuture.join();

            if (onSaleSnapshot == null)
                return CompletableFuture.completedFuture(MarketError.NOT_EXIST);

            OnSale onSale = onSaleSnapshot.getValue(OnSale.class);
            if (onSale == null)
                return CompletableFuture.completedFuture(MarketError.NOT_EXIST);

            if (!storeBuyingData.isSameResource(onSale))
                return CompletableFuture.completedFuture(MarketError.GENERAL_ERROR);

            int newQuantity = onSale.getQuantity() - storeBuyingData.getQuantity();
            if (newQuantity > 0) {
                // update the quantity.
                firebaseData.setValue(String.format("%s/quantity", childPath), newQuantity);
            } else if (newQuantity == 0) {
                firebaseData.removeData(childPath);
            } else {
                return CompletableFuture.completedFuture(MarketError.NOT_ENOUGH);
            }
            // update seller's market's resource quantity.
            firebaseData.setValue(String.format("%s/quantity", childPath), newQuantity);

            float cost = onSale.getPrice() * onSale.getQuantity();

            // update seller's money
            updateMarketMoney(String.format(Locale.getDefault(), "player/%d/money", storeBuyingData.getSellerId()), cost);

            // update buyer's money.
            updateMarketMoney(String.format(Locale.getDefault(), "player/%d/money", storeBuyingData.getPlayerId()), -1 * cost);

            return CompletableFuture.completedFuture(MarketError.SUCCESS);
        });
    }

    private static void updateMarketMoney(String path, Float cost) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData(path, dataSnapshot -> {
            if (dataSnapshot == null) return;
            Float money = dataSnapshot.getValue(Float.class);

            if (money == null) return;
            Float finalMoney = money + cost;

            firebaseData.setValue(path, finalMoney);
        });
    }

    public static CompletableFuture<List<OnSale>> getPlayerMarket(Integer serverId, Integer playerMarketId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        firebaseData.retrieveData(
                String.format(Locale.getDefault(), "server/%d/market/playerMarkets/%d", serverId, playerMarketId),
                future::complete);

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null) return CompletableFuture.completedFuture(null);
            PlayerMarkets playerMarkets = dataSnapshot.getValue(PlayerMarkets.class);

            if (playerMarkets == null) return CompletableFuture.completedFuture(null);
            List<OnSale> onSaleList = new ArrayList<>(playerMarkets.getOnSale());

            return CompletableFuture.completedFuture(onSaleList);
        });
    }

    public static CompletableFuture<Inventory> getInventoryItem(Integer playerId, Integer resourceId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();

        firebaseData.retrieveData(
                String.format(Locale.getDefault(), "player/%d/inventory/%d", playerId, resourceId),
                future::complete);

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null) return CompletableFuture.completedFuture(null);
            return CompletableFuture.completedFuture(dataSnapshot.getValue(Inventory.class));
        });
    }

    /**
     * This <i>sell</i> method inserts a resource for sale in the market. It also updates the inventory quantity of said resource.
     * But this does not cross-check the following: <br/>
     * <ul>
     *     <li>Quantity in the inventory and the resource being sold.</li>
     *     <li>Existence of the resource in the inventory.</li>
     * </ul>
     * @param onSale OnSale object that contains the data of the resource being sold.
     * @param serverId current server id.
     * @param playerMarketId current player's market id.
     * @param playerId current player's id.
     */
    public static void sell(OnSale onSale, Integer serverId, Integer playerMarketId, Integer playerId) {
        FirebaseData firebaseData = new FirebaseData();
        getPlayerMarket(serverId, playerMarketId).thenAccept(onSales -> {
            boolean existing = false;
            int index = 0;
            OnSale onSaleCopy = new OnSale();
            for (OnSale onSaleItem : onSales) {
                if (onSaleItem.equals(onSale)) {
                    onSaleCopy = onSaleItem;
                    existing = true;
                    break;
                }
                index++;
            }
            if (existing) {
                Integer qty = onSaleCopy.getQuantity() + onSale.getQuantity();
                onSaleCopy.setQuantity(qty);
                firebaseData.setValue(String.format(Locale.getDefault(),
                        "server/%d/playerMarkets/%d/onSale/%d",
                        serverId, playerMarketId, index), onSaleCopy);
            } else {
                onSales.add(onSale);
                firebaseData.setValue(String.format(Locale.getDefault(),
                        "server/%d/playerMarkets/%d/onSale",
                        serverId, playerMarketId), onSales);
            }
        });

        getInventoryItem(playerId, onSale.getInventoryResourceReference()).thenAccept(inventory -> {
            Integer finalQuantity = inventory.getQuantity() - onSale.getQuantity();
            inventory.setQuantity(finalQuantity);
            firebaseData.setValue(
                    String.format(Locale.getDefault(), "player/%d/inventory/%d", playerId, onSale.getInventoryResourceReference()),
                    inventory
            );
        });
    }

    public enum MarketError {
        NOT_EXIST, NOT_ENOUGH, SUCCESS, GENERAL_ERROR
    }

    /**
     * A DataFunction class for real-time data retrieval of <u>Player Markets</u>.
     */
    public static class PlayerMarketUpdates {
        FirebaseData firebaseData;
        String childPath;

        /**
         * A DataFunction class for real-time data retrieval of <u>Player Markets</u>. This will initialize the object and prepare the variables for data retrieval.
         *
         * @param serverId       current server ID.
         * @param playerMarketId player market to be observed.
         */
        public PlayerMarketUpdates(String serverId, Integer playerMarketId) {
            firebaseData = new FirebaseData();
            childPath = String.format(Locale.getDefault(), "server/%s/market/playerMarkets/%d", serverId, playerMarketId);
        }

        /**
         * Starts the listener and returns real-time updates from the playerMarket.
         *
         * @param listener A PlayerMarketsListener that returns updated values.
         */
        public void startListener(PlayerMarketsListener listener) {
            firebaseData.retrieveDataRealTime(childPath, dataSnapshot -> {
                if (dataSnapshot == null || !dataSnapshot.exists())
                    listener.onMarketUpdate(null);
                else {
                    listener.onMarketUpdate(dataSnapshot.getValue(PlayerMarkets.class));
                }
            });
        }

        /**
         * Stops the listener.
         */
        public void stopListener() {
            firebaseData.stopRealTimeUpdates(childPath);
        }
    }
}
