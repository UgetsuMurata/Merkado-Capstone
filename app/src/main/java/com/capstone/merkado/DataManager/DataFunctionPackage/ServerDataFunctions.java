package com.capstone.merkado.DataManager.DataFunctionPackage;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.capstone.merkado.DataManager.FirebaseData;
import com.capstone.merkado.DataManager.ValueReturn.ValueReturn;
import com.capstone.merkado.Helpers.Bot;
import com.capstone.merkado.Objects.ServerDataObjects.NewServer;
import com.capstone.merkado.Objects.ServerDataObjects.OtherServerDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

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

    public static void logInToServer(Integer playerId, String serverId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        String dataPath = String.format("server/%s/onlinePlayers", serverId);

        firebaseData.retrieveData(dataPath, future::complete);

        future.thenAccept(dataSnapshot -> {
            if (dataSnapshot == null) return;
            List<Integer> playerIds = new ArrayList<>();
            dataSnapshot.getChildren().forEach(ds -> {
                try {
                    playerIds.add(ds.getValue(Integer.class));
                } catch (DatabaseException | NumberFormatException e) {
                    Log.e("logInToServer", String.format("Error occurred: %s", e));
                }
            });
            playerIds.add(playerId);

            // remove duplicates
            Set<Integer> set = new HashSet<>(playerIds);
            playerIds.clear();
            playerIds.addAll(set);

            firebaseData.setValue(dataPath, playerIds);
        });
    }

    public static void logOutFromServer(Integer playerId, String serverId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        String dataPath = String.format("server/%s/onlinePlayers", serverId);

        firebaseData.retrieveData(dataPath, future::complete);

        future.thenAccept(dataSnapshot -> {
            if (dataSnapshot == null || !dataSnapshot.exists()) return;
            List<Integer> playerIds = new ArrayList<>();
            dataSnapshot.getChildren().forEach(ds -> {
                try {
                    Integer playerIdData = ds.getValue(Integer.class);
                    if (!Objects.equals(playerIdData, playerId)) playerIds.add(playerIdData);
                } catch (DatabaseException | NumberFormatException e) {
                    Log.e("logInToServer", String.format("Error occurred: %s", e));
                }
            });

            // remove duplicates
            Set<Integer> set = new HashSet<>(playerIds);
            playerIds.clear();
            playerIds.addAll(set);

            firebaseData.setValue(dataPath, playerIds);
        });
    }

    public static CompletableFuture<Boolean> checkServerExistence(String serverCode) {
        final CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData(String.format("server/%s", serverCode), future::complete);

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null) {
                return CompletableFuture.completedFuture(false);
            }
            return CompletableFuture.completedFuture(dataSnapshot.exists());
        });
    }

    public static CompletableFuture<Integer> createNewServer(@NonNull String id, @NonNull NewServer newServer) {
        return checkServerExistence(id).thenCompose(aBoolean -> {
            if (Boolean.TRUE.equals(aBoolean))
                return CompletableFuture.completedFuture(-1);
            FirebaseData firebaseData = new FirebaseData();
            firebaseData.setValue(String.format("server/%s", id), newServer);
            Bot.Store.setUpStore(id);
            setOtherServerDetails(id, new OtherServerDetails.ReachedLevels(0, 0, 0, 0), new OtherServerDetails.Bots(true, false));
            return CompletableFuture.completedFuture(0);
        });
    }

    public static void setSettings(@NonNull String id, @NonNull String user, @NonNull NewServer.Settings settings) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData(String.format("server/%s", id), dataSnapshot -> {
            if (dataSnapshot == null || !dataSnapshot.exists()) return;
            String serverOwner = dataSnapshot.child("/serverOwner").getValue(String.class);
            if (serverOwner == null) return;
            if (serverOwner.equals(user)) {
                firebaseData.setValue(String.format("server/%s/settings", id), settings);
            }
        });
    }

    public static CompletableFuture<NewServer.Settings> getSettings(@NonNull String serverId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        firebaseData.retrieveData(
                String.format("server/%s/settings", serverId),
                future::complete
        );

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null || !dataSnapshot.exists())
                return CompletableFuture.completedFuture(null);
            return CompletableFuture.completedFuture(dataSnapshot.getValue(NewServer.Settings.class));
        });
    }

    public static void setOtherServerDetails(String serverId, OtherServerDetails.ReachedLevels reachedLevels, OtherServerDetails.Bots bots) {
        OtherServerDetails otherServerDetails = new OtherServerDetails();
        otherServerDetails.setReachedLevels(reachedLevels);
        otherServerDetails.setBots(bots);

        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(
                String.format("server/%s/otherDetails", serverId),
                otherServerDetails
        );
    }

    public static CompletableFuture<OtherServerDetails> getOtherServerDetails(String serverId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        firebaseData.retrieveData(
                String.format("server/%s/otherDetails", serverId),
                future::complete
        );

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null) return CompletableFuture.completedFuture(null);
            return CompletableFuture.completedFuture(dataSnapshot.getValue(OtherServerDetails.class));
        });
    }

    public static CompletableFuture<OtherServerDetails.ReachedLevels> getReachedLevels(String serverId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        firebaseData.retrieveData(
                String.format("server/%s/otherDetails/reachedLevels", serverId),
                future::complete
        );

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null) return CompletableFuture.completedFuture(null);
            return CompletableFuture.completedFuture(dataSnapshot.getValue(OtherServerDetails.ReachedLevels.class));
        });
    }

    public static void setReachedLevels(String serverId, OtherServerDetails.ReachedLevels reachedLevels) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(
                String.format("server/%s/otherDetails/reachedLevels", serverId),
                reachedLevels
        );
    }

    public static void setBots(String serverId, OtherServerDetails.Bots bots) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(
                String.format("server/%s/otherDetails/bots", serverId),
                bots
        );
    }

    public static void logOutUpdaterPlayer(String serverId, Integer playerId) {
        UpdaterPlayerListener updaterPlayerListener = new UpdaterPlayerListener(serverId);
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData(
                String.format("server/%s/onlinePlayers", serverId),
                dataSnapshot -> {
                    if (dataSnapshot == null) return;
                    if (!dataSnapshot.exists()) {
                        updaterPlayerListener.setUpdaterPlayer(null);
                    } else {
                        for (DataSnapshot onlinePlayer : dataSnapshot.getChildren()) {
                            Integer onlinePlayerId = onlinePlayer.getValue(Integer.class);
                            if (onlinePlayerId != null && !onlinePlayerId.equals(playerId)) {
                                // set updaterPlayer to the first player id that is not this player's id.
                                updaterPlayerListener.setUpdaterPlayer(onlinePlayerId);
                                return;
                            }
                        }
                        updaterPlayerListener.setUpdaterPlayer(null);
                    }
                }
        );
    }

    public static void checkPlayerPretest(String serverId, Integer playerId, ValueReturn<Boolean> hasTaken) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData(
                String.format(Locale.getDefault(), "server/%s/diagnostic_tests/pre_test_scores/%d", serverId, playerId),
                dataSnapshot -> {
                    if (dataSnapshot == null) return;
                    hasTaken.valueReturn(dataSnapshot.exists()); // if it does not exist, then there is no existing data.
                }
        );
    }

    public static void setPlayerPretest(String serverId, Integer playerId, Integer score) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(
                String.format(Locale.getDefault(), "server/%s/diagnostic_tests/pre_test_scores/%d", serverId, playerId),
                score
        );
    }

    public static void checkPlayerPostTest(String serverId, Integer playerId, ValueReturn<Boolean> hasTaken) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData(
                String.format(Locale.getDefault(), "server/%s/diagnostic_tests/post_test_scores/%d", serverId, playerId),
                dataSnapshot -> {
                    if (dataSnapshot == null) return;
                    hasTaken.valueReturn(dataSnapshot.exists()); // if it does not exist, then there is no existing data.
                }
        );
    }

    public static void setPlayerPostTest(String serverId, Integer playerId, Integer score) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(
                String.format(Locale.getDefault(), "server/%s/diagnostic_tests/post_test_scores/%d", serverId, playerId),
                score
        );
    }

    public static class UpdaterPlayerListener {
        ValueReturn<Integer> returnValue;
        FirebaseData firebaseData;
        Integer initialData;
        String serverId;

        public UpdaterPlayerListener(String serverId) {
            this.serverId = serverId;
            firebaseData = new FirebaseData();
        }

        public CompletableFuture<Integer> getInitialData() {
            CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
            firebaseData.retrieveData(String.format("server/%s/updaterPlayer", serverId),
                    future::complete);
            return future.thenCompose(dataSnapshot -> {
                if (dataSnapshot == null) return CompletableFuture.completedFuture(null);
                initialData = dataSnapshot.getValue(Integer.class);
                return CompletableFuture.completedFuture(initialData);
            });
        }

        public void setUpdaterPlayer(Integer playerId) {
            FirebaseData fbData = new FirebaseData();
            fbData.setValue(String.format("server/%s/updaterPlayer", serverId), playerId);
        }

        public void start(ValueReturn<Integer> returnValue) {
            firebaseData.retrieveDataRealTime(
                    String.format("server/%s/updaterPlayer", serverId),
                    dataSnapshot -> {
                        if (dataSnapshot == null) {
                            if (returnValue != null)
                                returnValue.valueReturn(null);
                        } else {
                            if (returnValue != null)
                                returnValue.valueReturn(dataSnapshot.getValue(Integer.class));
                        }
                    }
            );
            this.returnValue = returnValue;
        }

        public void end() {
            firebaseData.stopRealTimeUpdates(
                    String.format("server/%s/updaterPlayer", this.serverId)
            );
            this.returnValue = null;
        }
    }
}
