package com.capstone.merkado.DataManager.DataFunctionPackage;

import com.capstone.merkado.DataManager.FirebaseData;
import com.capstone.merkado.Objects.FactoryDataObjects.FactoryData;
import com.capstone.merkado.Objects.FactoryDataObjects.FactoryTypes;
import com.capstone.merkado.Objects.FactoryDataObjects.PlayerFactory;
import com.capstone.merkado.Objects.StoresDataObjects.PlayerMarkets;
import com.google.firebase.database.DataSnapshot;

import java.util.Locale;

public class FactoryDataFunctions {

    public static void setUpFactory(String server, Integer playerId, String username, Integer marketId) {
        FirebaseData firebaseData = new FirebaseData();

        // create FactoryData object
        FactoryData.FactoryDetails factoryDetails = new FactoryData.FactoryDetails();
        factoryDetails.setEnergy(50L);
        factoryDetails.setEnergyMax(50L);
        factoryDetails.setLastUsedEnergy(System.currentTimeMillis());
        factoryDetails.setOnProduction(-1);
        factoryDetails.setFoodProficiency(0L);
        factoryDetails.setManufacturingProficiency(0L);

        FactoryData factoryData = new FactoryData();
        factoryData.setDetails(factoryDetails);

        // create PlayerFactory object
        PlayerFactory playerFactory = new PlayerFactory();
        playerFactory.setFactoryOwner(playerId);
        playerFactory.setFactoryName(String.format("%s's Factory", username));
        playerFactory.setOpened(System.currentTimeMillis());

        firebaseData.retrieveData(
                String.format(Locale.getDefault(), "server/%s/market/playerFactory/", server),
                dataSnapshot -> {
                    if (dataSnapshot == null) return;
                    Integer i = 0;
                    for (DataSnapshot store : dataSnapshot.getChildren()) {
                        try {
                            store.getValue(PlayerMarkets.class);
                        } catch (Exception e) {
                            createFactoryMarketData(server, i, playerFactory);
                            createFactoryPlayerData(playerId, i, factoryData);
                            return;
                        }
                        i++;
                    }
                    createFactoryMarketData(server, i, playerFactory);
                    createFactoryPlayerData(playerId, i, factoryData);
                }
        );

        if (marketId != null) StoreDataFunctions.removeMarket(server, playerId, marketId);
    }

    private static void createFactoryMarketData(String server, Integer index, PlayerFactory playerFactory) {
        FirebaseData firebaseData = new FirebaseData();
        playerFactory.setFactoryId(index);

        firebaseData.setValue(
                String.format(Locale.getDefault(), "server/%s/market/playerFactory/", server),
                playerFactory
        );
    }

    private static void createFactoryPlayerData(Integer playerId, Integer index, FactoryData factoryData) {
        FirebaseData firebaseData = new FirebaseData();
        factoryData.setFactoryMarketId(index);

        firebaseData.setValue(
                String.format(Locale.getDefault(), "player/%d/factory", playerId),
                factoryData
        );
    }

    public static void setFactoryType(String serverId, Integer playerId, Integer factoryId, String username, FactoryTypes factoryType) {
        FirebaseData firebaseData = new FirebaseData();
        String factoryTypeName = factoryType == FactoryTypes.FOOD ? "Food Factory" : "Manufacturing Factory";
        String factoryName = String.format("%s's %s", username, factoryTypeName);
        firebaseData.setValue(
                String.format(
                        Locale.getDefault(),
                        "player/%d/factory/factoryType",
                        playerId
                ),
                factoryType.toString()
        );
        firebaseData.setValue(
                String.format(
                        Locale.getDefault(),
                        "server/%s/market/playerFactory/%d/factoryType",
                        serverId, factoryId
                ),
                factoryTypeName
        );
        firebaseData.setValue(
                String.format(
                        Locale.getDefault(),
                        "server/%s/market/playerFactory/%d/factoryName",
                        serverId, factoryId
                ),
                factoryName
        );
    }

    public static void removeFactory(String serverId, Integer playerId, Integer factoryId) {
        FirebaseData firebaseData = new FirebaseData();

        firebaseData.removeData(
                String.format(Locale.getDefault(), "player/%d/factory/factoryMarketId", playerId)
        );
        firebaseData.removeData(
                String.format(Locale.getDefault(), "server/%s/market/playerFactory/%d",
                        serverId, factoryId)
        );
    }
}
