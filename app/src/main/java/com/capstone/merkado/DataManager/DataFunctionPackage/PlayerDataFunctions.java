package com.capstone.merkado.DataManager.DataFunctionPackage;

import android.util.Log;

import androidx.annotation.NonNull;

import com.capstone.merkado.DataManager.FirebaseData;
import com.capstone.merkado.DataManager.ValueReturn.ValueReturn;
import com.capstone.merkado.Helpers.FirebaseCharacters;
import com.capstone.merkado.Helpers.StringHash;
import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.Objects.PlayerDataObjects.Player;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor1;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor2;
import com.capstone.merkado.Objects.ResourceDataObjects.Inventory;
import com.capstone.merkado.Objects.ServerDataObjects.BasicServerData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class PlayerDataFunctions {

    /**
     * Gets the player's data using the playerId.
     *
     * @param playerId player's id.
     * @return Player instance.
     */
    public static CompletableFuture<PlayerFBExtractor1> getPlayerDataFromId(Integer playerId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();

        firebaseData.retrieveData(
                String.format(Locale.getDefault(), "player/%d", playerId),
                future::complete);

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null) return CompletableFuture.completedFuture(null);
            try {
                PlayerFBExtractor1 playerFBExtractor1 = dataSnapshot.getValue(PlayerFBExtractor1.class);
                return CompletableFuture.completedFuture(playerFBExtractor1);
            } catch (DatabaseException ignore) {
                PlayerFBExtractor2 playerFBExtractor2 = dataSnapshot.getValue(PlayerFBExtractor2.class);
                return CompletableFuture.completedFuture(new PlayerFBExtractor1(playerFBExtractor2));
            }
        }).exceptionally(ex -> {
            Log.e("PlayerDataFunctions", "Unexpected error occurred", ex);
            return null;
        });
    }

    public static class PlayerListListener {
        List<Integer> playerIdList;
        Map<Integer, String> serverIdList;
        Map<String, FirebaseData> firebaseDataList;
        List<BasicServerData> basicServerDataList;
        String dataPath;
        FirebaseData firebaseData;
        DataListener dataListener;

        public PlayerListListener(String email) {
            firebaseData = new FirebaseData();
            playerIdList = new ArrayList<>();
            serverIdList = new HashMap<>();
            firebaseDataList = new HashMap<>();
            basicServerDataList = new ArrayList<>();
            dataPath = String.format("accounts/%s/player", FirebaseCharacters.encode(email));
            getAllPlayerIdList();
        }

        public void getAllPlayerIdList() {
            firebaseData.retrieveDataRealTime(dataPath, dataSnapshot -> {
                if (dataSnapshot == null || !dataSnapshot.exists()) return;

                // GET PLAYER IDS
                List<Integer> playerIds = new ArrayList<>();
                for (DataSnapshot playerId : dataSnapshot.getChildren()) {
                    try {
                        playerIds.add(playerId.getValue(Integer.class));
                    } catch (ClassCastException e) {
                        Log.e("getEconomyBasic",
                                String.format("Error in parsing id for %s with error message %s",
                                        playerId, e));
                    }
                }

                setPlayerIdList(playerIds);
            });
        }

        private CompletableFuture<Map<Integer, String>> getAllServerIdList() {
            // Retrieve server IDs for all player IDs
            Map<Integer, CompletableFuture<String>> serverIdFutures = playerIdList.stream()
                    .map(playerId -> Map.entry(playerId, ServerDataFunctions.getServerId(playerId)))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            return CompletableFuture.allOf(serverIdFutures.values().toArray(new CompletableFuture[0]))
                    .thenApply(v -> serverIdFutures.keySet().stream()
                            .collect(Collectors.toMap(
                                    playerId -> playerId,
                                    playerId -> {
                                        CompletableFuture<String> value = serverIdFutures.get(playerId);
                                        if (value == null) return "";
                                        return value.join();
                                    }
                            )));
        }

        private void getAllBasicServerData() {
            // Retrieve BasicServerData for all server IDs
            stopRealTimeUpdates();

            for (Map.Entry<Integer, String> serverId : serverIdList.entrySet()) {
                FirebaseData firebaseData = new FirebaseData();
                String dataPath = String.format("server/%s", serverId.getValue());
                firebaseData.retrieveDataRealTime(
                        dataPath,
                        dataSnapshot -> {
                            if (dataSnapshot == null || !dataSnapshot.exists()) return;
                            BasicServerData basicServerData = dataSnapshot.getValue(BasicServerData.class);
                            if (basicServerData == null) return;
                            basicServerData.setPlayerId(serverId.getKey());
                            basicServerData.setId(serverId.getValue());
                            addServerToList(basicServerData);
                        }
                );
                firebaseDataList.put(dataPath, firebaseData);
            }
        }

        private void addServerToList(BasicServerData basicServerData) {
            int index = 0;
            for (BasicServerData bsd : basicServerDataList) {
                if (Objects.equals(bsd.getId(), basicServerData.getId())) break;
                index++;
            }
            if (basicServerDataList.size() <= index) {
                basicServerDataList.add(basicServerData);
            } else {
                basicServerDataList.remove(index);
                basicServerDataList.add(index, basicServerData);
            }
            if (this.dataListener != null) this.dataListener.update(basicServerDataList);
        }

        private void setPlayerIdList(List<Integer> playerIdList) {
            this.playerIdList = playerIdList;
            getAllServerIdList().thenAccept(idList -> {
                serverIdList = idList;
                getAllBasicServerData();
            });
        }

        public void start(DataListener dataListener) {
            this.dataListener = dataListener;
        }

        public void stop() {
            this.firebaseData.stopRealTimeUpdates(dataPath);
            stopRealTimeUpdates();
            this.dataListener = null;
        }

        private void stopRealTimeUpdates() {
            this.firebaseDataList.keySet()
                    .forEach(s -> {
                        FirebaseData fbData = firebaseDataList.get(s);
                        if (fbData != null) fbData.stopRealTimeUpdates(s);
                    });
            this.firebaseDataList.clear();
        }

        public interface DataListener {
            void update(List<BasicServerData> basicServerDataList);
        }
    }


    //For adding the player to the server
    public static CompletableFuture<Integer> addPlayerToServer(String serverId, String serverKey, Account account) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        firebaseData.retrieveData("server/" + serverId + "/key", future::complete);

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null) return CompletableFuture.completedFuture(-1); // general error

            String savedKey = dataSnapshot.getValue(String.class);
            if (savedKey == null) return CompletableFuture.completedFuture(-1); // cannot retrieve server credentials
            if (!savedKey.equals(StringHash.hashPassword(serverKey))) return CompletableFuture.completedFuture(-2); // incorrect key

            return getNextPlayerIndex().thenCompose(playerId -> {
                if (playerId == -1L) return CompletableFuture.completedFuture(-1); // cannot generate player id

                // populate storyQueueList
                Map<String, Object> playerData = createNewPlayerData(serverId, account);

                // Add player data to Firebase under player/{playerId}
                return firebaseData.setValues(String.format("player/%s", playerId), playerData).thenCompose(success->{
                    // return false if not successful.
                    if (success == null || !success) return CompletableFuture.completedFuture(-1);

                    // ADD PLAYER ID TO SERVER PLAYERS LIST
                    addPlayerToServer(serverId, playerId);

                    // ADD PLAYER ID TO ACCOUNT
                    addPlayerToAccount(account.getEmail(), playerId);
                    return CompletableFuture.completedFuture(0);
                });
            });
        });
    }

    private static CompletableFuture<Long> getNextPlayerIndex() {
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData("player", future::complete);

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null) {
                return CompletableFuture.completedFuture(-1L);
            }
            return CompletableFuture.completedFuture(dataSnapshot.getChildrenCount());
        });
    }

    private static @NonNull Map<String, Object> createNewPlayerData(String serverCode, Account account) {
        List<PlayerFBExtractor1.StoryQueue> storyQueueList = new ArrayList<>();
        PlayerFBExtractor1.StoryQueue storyQueue = new PlayerFBExtractor1.StoryQueue();
        storyQueue.setChapter(0);
        storyQueue.setCurrentLineGroup(0);
        storyQueue.setCurrentScene(0);
        storyQueue.setNextLineGroup(1);
        storyQueue.setNextScene(1);
        storyQueueList.add(storyQueue);

        // populate inventoryList
        List<Inventory> inventoryList = new ArrayList<>();

        // Create player data
        Map<String, Object> playerData = new HashMap<>();
        playerData.put("accountId", account.getEmail());
        playerData.put("exp", 0);
        playerData.put("inventory", inventoryList);
        playerData.put("money", 2000);
        playerData.put("server", serverCode);
        playerData.put("storyQueue", storyQueueList);
        return playerData;
    }

    private static void addPlayerToServer(String serverCode, Long playerId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        FirebaseData firebaseData = new FirebaseData();

        firebaseData.retrieveData(String.format("server/%s/players", serverCode), dataSnapshot -> {
            if (dataSnapshot == null) return;
            if (dataSnapshot.exists()) {
                long playerCount = dataSnapshot.getChildrenCount();
                firebaseData.setValue(String.format("server/%s/players/%s", serverCode, playerCount), playerId);
            } else {
                firebaseData.setValue(String.format("server/%s/players/0", serverCode), playerId);
            }
        });
    }

    private static void addPlayerToAccount(String email, Long playerId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        FirebaseData firebaseData = new FirebaseData();
        String encodedEmail = FirebaseCharacters.encode(email);

        firebaseData.retrieveData(String.format("accounts/%s/player", encodedEmail), dataSnapshot -> {
            if (dataSnapshot == null) {
                future.complete(null);
                return;
            }
            long index = dataSnapshot.getChildrenCount();
            firebaseData.setValue(String.format("accounts/%s/player/%s", encodedEmail, index), playerId);
            future.complete(null);
        });
    }

    public static void addPlayerExperience(Integer playerId, Long quantity, ValueReturn<Long> longValue) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        String dataPath = String.format(Locale.getDefault(), "player/%d/exp", playerId);

        firebaseData.retrieveData(dataPath, future::complete);
        future.thenAccept(dataSnapshot -> {
            long totalEXP;
            if (dataSnapshot == null) return;
            if (!dataSnapshot.exists()) {
                firebaseData.setValue(dataPath, quantity);
                longValue.valueReturn(quantity);
            } else {
                Long currentEXP = dataSnapshot.getValue(Long.class);
                if (currentEXP == null) {
                    firebaseData.setValue(dataPath, quantity);
                    longValue.valueReturn(quantity);
                } else {
                    totalEXP = quantity + currentEXP;
                    firebaseData.setValue(dataPath, totalEXP);
                    longValue.valueReturn(totalEXP);
                }
            }
        });
    }

    public static void setCurrentObjective(Integer playerId, PlayerFBExtractor1.PlayerObjectives objectives) {
        FirebaseData firebaseData = new FirebaseData();
        String dataPath = String.format(Locale.getDefault(), "player/%d/objectives", playerId);
        firebaseData.setValue(dataPath, objectives);
    }

    public static class PlayerDataUpdates {
        FirebaseData firebaseData;
        String childPath;

        /**
         * A DataFunction class for real-time data retrieval of <u>Player Markets</u>. This will initialize the object and prepare the variables for data retrieval.
         *
         * @param playerId current player ID.
         */
        public PlayerDataUpdates(Integer playerId) {
            firebaseData = new FirebaseData();
            childPath = String.format(Locale.getDefault(), "player/%d", playerId);
        }

        /**
         * Starts the listener and returns real-time updates from the playerMarket.
         *
         * @param listener A PlayerMarketsListener that returns updated values.
         */
        public void startListener(ValueReturn<Player> listener) {
            firebaseData.retrieveDataRealTime(childPath, dataSnapshot -> {
                        if (dataSnapshot == null || !dataSnapshot.exists())
                            listener.valueReturn(null);
                        else {
                            PlayerFBExtractor1 playerFBExtractor1;
                            try {
                                playerFBExtractor1 = dataSnapshot.getValue(PlayerFBExtractor1.class);
                            } catch (DatabaseException ignore) {
                                playerFBExtractor1 = new PlayerFBExtractor1(dataSnapshot.getValue(PlayerFBExtractor2.class));
                            }
                            if (playerFBExtractor1 == null) {
                                listener.valueReturn(null);
                                return;
                            }
                            listener.valueReturn(new Player(playerFBExtractor1));
                        }
                    }
            );
        }

        /**
         * Stops the listener.
         */
        public void stopListener() {
            firebaseData.stopRealTimeUpdates(childPath);
        }
    }
}
