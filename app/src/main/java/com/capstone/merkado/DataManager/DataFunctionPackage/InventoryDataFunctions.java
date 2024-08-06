package com.capstone.merkado.DataManager.DataFunctionPackage;

import com.capstone.merkado.DataManager.FirebaseData;
import com.capstone.merkado.DataManager.ValueReturn.ValueReturn;
import com.capstone.merkado.Objects.ResourceDataObjects.Inventory;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SuppressWarnings("unused")
public class InventoryDataFunctions {

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

    public static CompletableFuture<List<Inventory>> getPlayerInventory(Integer playerId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();

        firebaseData.retrieveData(
                String.format(Locale.getDefault(), "player/%d/inventory", playerId),
                future::complete);

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null) return CompletableFuture.completedFuture(null);
            List<Inventory> inventoryList = new ArrayList<>();
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                inventoryList.add(ds.getValue(Inventory.class));
            }
            return CompletableFuture.completedFuture(inventoryList);
        });
    }

    public static void setInventoryItem(Inventory inventory, Integer playerId) {
        FirebaseData firebaseData = new FirebaseData();

        firebaseData.retrieveData(String.format(Locale.getDefault(), "player/%d/inventory/%d",
                playerId, inventory.getResourceId()), dataSnapshot -> {
            if (dataSnapshot == null) return;
            if (dataSnapshot.exists()) {
                Inventory existingInventory = dataSnapshot.getValue(Inventory.class);
                if (existingInventory != null) {
                    firebaseData.setValue(String.format(Locale.getDefault(), "player/%d/inventory/%d/quantity",
                            playerId, inventory.getResourceId()), existingInventory.getQuantity() + inventory.getQuantity());
                    return;
                }
            }
            inventory.setResourceData(null); // this must not be in the firebase real-time database
            firebaseData.setValue(String.format(Locale.getDefault(), "player/%d/inventory/%d",
                    playerId, inventory.getResourceId()), inventory);
        });
    }

    public static void removeInventoryItem(Integer playerId, Integer resourceId) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.removeData(
                String.format(Locale.getDefault(), "player/%d/inventory/%d", playerId, resourceId)
        );
    }

    public static class InventoryUpdates {
        FirebaseData firebaseData;
        String childPath;

        /**
         * A DataFunction class for real-time data retrieval of <u>Player Markets</u>. This will initialize the object and prepare the variables for data retrieval.
         *
         * @param playerId current player ID.
         */
        public InventoryUpdates(Integer playerId) {
            firebaseData = new FirebaseData();
            childPath = String.format(Locale.getDefault(), "player/%d/inventory", playerId);
        }

        /**
         * Starts the listener and returns real-time updates from the playerMarket.
         *
         * @param listener A PlayerMarketsListener that returns updated values.
         */
        public void startListener(ValueReturn<List<Inventory>> listener) {
            firebaseData.retrieveDataRealTime(childPath, dataSnapshot -> {
                if (dataSnapshot == null || !dataSnapshot.exists()) {
                    listener.valueReturn(null);
                    return;
                }
                List<Inventory> inventoryList = StreamSupport.stream(dataSnapshot.getChildren().spliterator(), false)
                        .map(ds -> ds.getValue(Inventory.class))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                listener.valueReturn(inventoryList.isEmpty() ? null : inventoryList);
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
