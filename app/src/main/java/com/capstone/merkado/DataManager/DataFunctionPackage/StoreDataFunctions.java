package com.capstone.merkado.DataManager.DataFunctionPackage;

import com.capstone.merkado.DataManager.FirebaseData;
import com.capstone.merkado.Objects.StoresDataObjects.PlayerMarkets;
import com.google.firebase.database.DataSnapshot;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class StoreDataFunctions {

    public static void setUpStore(String server, Integer playerId, String username) {
        // create PlayerMarkets object
        PlayerMarkets playerMarkets = new PlayerMarkets();
        playerMarkets.setMarketOwner(playerId);
        playerMarkets.setOpened(System.currentTimeMillis());
        playerMarkets.setStoreName(String.format("%s's Store", username));

        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();

        firebaseData.retrieveData(String.format("server/%s/market/playerMarkets", server), future::complete);

        future.thenAccept(dataSnapshot -> {
            Integer i = 0;
            for (DataSnapshot store : dataSnapshot.getChildren()) {
                try {
                    store.getValue(PlayerMarkets.class);
                } catch (Exception e) {
                    createMarket(server, playerId, i, playerMarkets);
                    return;
                }
                i++;
            }
            createMarket(server, playerId, i, playerMarkets);
        });
    }

    private static void createMarket(String server, Integer playerId, Integer index, PlayerMarkets playerMarkets){
        FirebaseData firebaseData = new FirebaseData();
        playerMarkets.setMarketId(index);
        firebaseData.setValue(
                String.format(Locale.getDefault(), "player/%d/marketId", playerId), index);
        firebaseData.setValue(
                String.format(Locale.getDefault(), "server/%s/market/playerMarkets/%d", server, index), playerMarkets);

    }
}
