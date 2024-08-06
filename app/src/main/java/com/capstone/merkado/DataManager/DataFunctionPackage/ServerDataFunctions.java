package com.capstone.merkado.DataManager.DataFunctionPackage;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.capstone.merkado.DataManager.FirebaseData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

    public static Boolean checkServerExistence(String serverCode) {
        final CompletableFuture<Boolean> future = new CompletableFuture<>();
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData(String.format("server/%s", serverCode), dataSnapshot -> {
            if (dataSnapshot == null) {
                future.complete(false);
                return;
            }
            future.complete(dataSnapshot.exists());
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("checkServerExistence", String.format("Error occurred when getting future: %s", e));
            return null;
        }
    }
}
