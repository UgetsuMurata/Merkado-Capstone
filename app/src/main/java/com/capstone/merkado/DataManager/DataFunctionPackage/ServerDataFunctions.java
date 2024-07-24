package com.capstone.merkado.DataManager.DataFunctionPackage;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.capstone.merkado.DataManager.FirebaseData;
import com.capstone.merkado.Helpers.FirebaseCharacters;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor1;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor2;
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

public class ServerDataFunctions {

    @Nullable
    public static CompletableFuture<String> getServerId(@NonNull Integer playerId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();

        firebaseData.retrieveData(
                String.format(Locale.getDefault(), "player/%d/server", playerId),
                future::complete
        );

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null || !dataSnapshot.exists()) return null;
            return CompletableFuture.completedFuture(dataSnapshot.getValue(String.class));
        });
    }

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
                    .map(playerId -> Map.entry(playerId, getServerId(playerId)))
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
}
