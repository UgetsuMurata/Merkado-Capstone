package com.capstone.merkado.DataManager.DataFunctionPackage;

import com.capstone.merkado.DataManager.FirebaseData;
import com.capstone.merkado.DataManager.ValueReturn.ValueReturn;
import com.capstone.merkado.Objects.FactoryDataObjects.FactoryData;
import com.capstone.merkado.Objects.FactoryDataObjects.FactoryTypes;
import com.capstone.merkado.Objects.FactoryDataObjects.PlayerFactory;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceData;
import com.capstone.merkado.Objects.StoresDataObjects.PlayerMarkets;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
                String.format(Locale.getDefault(), "server/%s/market/playerFactory/%d", server, index),
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
        ResourceData resourceData = FactoryDataFunctions.getFactoryChoices(
                factoryType == FactoryTypes.FOOD ?
                        FactoryTypes.FOOD :
                        FactoryTypes.MANUFACTURING).get(0);
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
                        "player/%d/factory/details/onProduction",
                        playerId
                ),
                resourceData.getResourceId()
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

    public static CompletableFuture<FactoryData> getFactoryData(Integer playerId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();

        firebaseData.retrieveData(String.format(Locale.getDefault(), "player/%d/factory", playerId),
                future::complete);

        return future.thenCompose(dataSnapshot ->
                CompletableFuture.completedFuture(dataSnapshot.getValue(FactoryData.class)));
    }

    public static List<ResourceData> getFactoryChoices(FactoryTypes type) {
        List<ResourceData> resourceDataList = InternalDataFunctions.getAllResources();
        return filter(type, resourceDataList);
    }

    public static void updateFactoryDetails(FactoryData.FactoryDetails factoryDetails, Integer playerId) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(String.format(Locale.getDefault(), "player/%d/factory/details", playerId), factoryDetails);
    }

    public static void addFactoryProducts(String serverId, Integer factoryMarketId, Integer resourceId, Long quantity) {
        FirebaseData firebaseData = new FirebaseData();
        String childPath = String.format(Locale.getDefault(),
                "server/%s/market/playerFactory/%d/onSale", serverId, factoryMarketId);
        firebaseData.retrieveData(childPath, dataSnapshot -> {
            if (dataSnapshot == null) return;
            List<PlayerMarkets.OnSale> onSaleList = new ArrayList<>();
            int i = -1;
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                i++;
                PlayerMarkets.OnSale onSale = ds.getValue(PlayerMarkets.OnSale.class);
                if (onSale == null) return;
                if (Objects.equals(onSale.getResourceId(), resourceId)) {
                    Long finalQuantity = onSale.getQuantity() + quantity;
                    firebaseData.setValue(
                            String.format(Locale.getDefault(), "%s/%d/quantity", childPath, i),
                            finalQuantity);
                    return;
                }
                onSaleList.add(onSale);
            }
            ResourceData resourceData = InternalDataFunctions.getResourceData(resourceId);
            if (resourceData == null) return;
            PlayerMarkets.OnSale newOnSale = new PlayerMarkets.OnSale();
            newOnSale.setOnSaleId(onSaleList.size());
            newOnSale.setItemName(resourceData.getName());
            newOnSale.setResourceId(resourceId);
            newOnSale.setType(resourceData.getType());
            newOnSale.setQuantity(Math.toIntExact(quantity));
            onSaleList.add(newOnSale);
            firebaseData.setValue(childPath, onSaleList);
        });
    }

    private static List<ResourceData> filter(FactoryTypes type, List<ResourceData> resourceData) {
        return resourceData.stream()
                .filter(rd -> {
                    if (rd == null) return false;
                    return type == FactoryTypes.FOOD && "EDIBLE".equalsIgnoreCase(rd.getType()) ||
                            type == FactoryTypes.MANUFACTURING && "RESOURCE".equalsIgnoreCase(rd.getType());
                })
                .collect(Collectors.toList());
    }

    public static CompletableFuture<List<PlayerFactory>> getAllPlayerFactory(String serverId) {
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        FirebaseData firebaseData = new FirebaseData();

        firebaseData.retrieveData(String.format("server/%s/market/playerFactory", serverId), future::complete);
        return future.thenCompose(dataSnapshot -> {
            List<PlayerFactory> playerFactoryList = new ArrayList<>();
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                PlayerFactory playerFactory = ds.getValue(PlayerFactory.class);
                playerFactoryList.add(playerFactory);
            }
            return CompletableFuture.completedFuture(playerFactoryList);
        });
    }

    public static class FactoryDataUpdates {
        FirebaseData firebaseData;
        String childPath;

        public FactoryDataUpdates(Integer playerId) {
            firebaseData = new FirebaseData();
            childPath = String.format(Locale.getDefault(), "player/%d/factory/details", playerId);
        }

        public void startListener(ValueReturn<FactoryData.FactoryDetails> factoryDataValueReturn) {
            firebaseData.retrieveDataRealTime(childPath, dataSnapshot -> {
                if (dataSnapshot == null) {
                    factoryDataValueReturn.valueReturn(null);
                    return;
                }
                factoryDataValueReturn.valueReturn(dataSnapshot.getValue(FactoryData.FactoryDetails.class));
            });
        }

        public void stopListener() {
            firebaseData.stopRealTimeUpdates(childPath);
        }
    }

    public static class PlayerFactoryUpdates {
        FirebaseData firebaseData;
        String childPath;

        /**
         * A DataFunction class for real-time data retrieval of <u>Player Factory</u>. This will initialize the object and prepare the variables for data retrieval.
         *
         * @param serverId       current server ID.
         * @param playerMarketId player market to be observed.
         */
        public PlayerFactoryUpdates(String serverId, Integer playerMarketId) {
            firebaseData = new FirebaseData();
            childPath = String.format(Locale.getDefault(), "server/%s/market/playerFactory/%d", serverId, playerMarketId);
        }

        /**
         * Starts the listener and returns real-time updates from the playerMarket.
         *
         * @param listener A PlayerMarketsListener that returns updated values.
         */
        public void startListener(ValueReturn<PlayerFactory> listener) {
            firebaseData.retrieveDataRealTime(childPath, dataSnapshot -> {
                if (dataSnapshot == null || !dataSnapshot.exists())
                    listener.valueReturn(null);
                else {
                    listener.valueReturn(dataSnapshot.getValue(PlayerFactory.class));
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
