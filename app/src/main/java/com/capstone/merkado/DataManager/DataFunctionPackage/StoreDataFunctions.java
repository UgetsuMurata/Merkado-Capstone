package com.capstone.merkado.DataManager.DataFunctionPackage;

import com.capstone.merkado.DataManager.FirebaseData;
import com.capstone.merkado.DataManager.ValueReturn.ValueReturn;
import com.capstone.merkado.Objects.PlayerDataObjects.Player;
import com.capstone.merkado.Objects.ResourceDataObjects.Inventory;
import com.capstone.merkado.Objects.ServerDataObjects.MarketStandard.MarketStandard;
import com.capstone.merkado.Objects.ServerDataObjects.MarketStandard.MarketStandardList;
import com.capstone.merkado.Objects.StoresDataObjects.PlayerMarkets;
import com.capstone.merkado.Objects.StoresDataObjects.StoreBuyingData;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class StoreDataFunctions {

    public static void setUpStore(String server, Integer playerId, String username, Integer factoryId) {
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

        if (factoryId != null) FactoryDataFunctions.removeFactory(server, playerId, factoryId);
    }

    private static void createMarket(String server, Integer playerId, Integer index, PlayerMarkets playerMarkets) {
        FirebaseData firebaseData = new FirebaseData();
        playerMarkets.setMarketId(index);
        firebaseData.setValue(
                String.format(Locale.getDefault(), "player/%d/market/id", playerId), index);
        firebaseData.setValue(
                String.format(Locale.getDefault(), "player/%d/market/hadMarket", playerId), true);
        firebaseData.setValue(
                String.format(Locale.getDefault(), "server/%s/market/playerMarkets/%d", server, index), playerMarkets);
    }

    public static void removeMarket(String serverId, Integer playerId, Integer marketId) {
        FirebaseData firebaseData = new FirebaseData();

        firebaseData.removeData(
                String.format(Locale.getDefault(), "player/%d/market/id", playerId)
        );
        firebaseData.removeData(
                String.format(Locale.getDefault(), "server/%s/market/playerMarkets/%d",
                        serverId, marketId)
        );
    }

    public static CompletableFuture<List<PlayerMarkets>> getAllPlayerMarkets(String serverId, Integer playerId) {
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        FirebaseData firebaseData = new FirebaseData();

        firebaseData.retrieveData(String.format("server/%s/market/playerMarkets", serverId), future::complete);
        return future.thenCompose(dataSnapshot -> {
            List<PlayerMarkets> playerMarketsList = new ArrayList<>();
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                PlayerMarkets playerMarkets = ds.getValue(PlayerMarkets.class);
                // do not display the current player's own store.
                if (playerMarkets == null || Objects.equals(playerMarkets.getMarketOwner(), playerId))
                    continue;
                playerMarketsList.add(ds.getValue(PlayerMarkets.class));
            }
            return CompletableFuture.completedFuture(playerMarketsList);
        });
    }

    public static CompletableFuture<StoreDataFunctions.MarketError> buyFromPlayerMarket(StoreBuyingData storeBuyingData) {
        FirebaseData firebaseData = new FirebaseData();
        String childPath = String.format(Locale.getDefault(),
                "server/%s/market/playerMarkets/%d/onSale/%d",
                storeBuyingData.getServerId(),
                storeBuyingData.getPlayerMarketId(),
                storeBuyingData.getOnSaleId());

        CompletableFuture<DataSnapshot> onSaleFuture = new CompletableFuture<>();

        firebaseData.retrieveData(childPath, onSaleFuture::complete);
        return onSaleFuture.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null)
                return CompletableFuture.completedFuture(StoreDataFunctions.MarketError.NOT_EXIST);

            PlayerMarkets.OnSale onSale = dataSnapshot.getValue(PlayerMarkets.OnSale.class);
            if (onSale == null)
                return CompletableFuture.completedFuture(StoreDataFunctions.MarketError.NOT_EXIST);

            if (!storeBuyingData.isSameResource(onSale))
                return CompletableFuture.completedFuture(StoreDataFunctions.MarketError.GENERAL_ERROR);

            // update seller's market's resource quantity.
            int newQuantity = onSale.getQuantity() - storeBuyingData.getQuantity();
            if (newQuantity > 0) {
                // update the quantity.
                firebaseData.setValue(String.format("%s/quantity", childPath), newQuantity);
            } else if (newQuantity == 0) {
                firebaseData.removeData(childPath);
                readjustOnSaleListKeys(String.format(Locale.getDefault(),
                        "server/%s/market/playerMarkets/%d/onSale",
                        storeBuyingData.getServerId(),
                        storeBuyingData.getPlayerMarketId()));
            } else {
                return CompletableFuture.completedFuture(StoreDataFunctions.MarketError.NOT_ENOUGH);
            }

            // get total cost
            float cost = onSale.getPrice() * storeBuyingData.getQuantity();

            // update seller's money
            if (storeBuyingData.getSellerId() != -1) // -1 is a player id for bots.
                updateMarketMoney(String.format(Locale.getDefault(), "player/%d/money", storeBuyingData.getSellerId()), cost);

            // update buyer's money.
            updateMarketMoney(String.format(Locale.getDefault(), "player/%d/money", storeBuyingData.getPlayerId()), -1 * cost);
            updateMarketInventory(onSale, storeBuyingData.getQuantity(), storeBuyingData.getPlayerId());

            return CompletableFuture.completedFuture(StoreDataFunctions.MarketError.SUCCESS);
        });
    }

    private static void readjustOnSaleListKeys(String path) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData(path, dataSnapshot -> {
            List<PlayerMarkets.OnSale> onSaleList = new ArrayList<>();
            if (dataSnapshot == null || !dataSnapshot.exists()) return;
            int i = 0;
            for (DataSnapshot onSaleDS : dataSnapshot.getChildren()) {
                PlayerMarkets.OnSale onSale = onSaleDS.getValue(PlayerMarkets.OnSale.class);
                if (onSale == null) continue;
                onSale.setOnSaleId(i);
                onSaleList.add(onSale);
                i++;
            }
            firebaseData.setValue(path, onSaleList);
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

    private static void updateMarketInventory(PlayerMarkets.OnSale onSale, Integer quantity, Integer playerId) {
        Inventory inventory = new Inventory();
        inventory.setResourceId(onSale.getResourceId());
        inventory.setQuantity(quantity);
        inventory.setType(onSale.getType());
        inventory.setSellable(true);
        InventoryDataFunctions.setInventoryItem(inventory, playerId);
    }

    public static CompletableFuture<List<PlayerMarkets.OnSale>> getPlayerMarket(String serverId, Integer playerMarketId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        firebaseData.retrieveData(
                String.format(Locale.getDefault(), "server/%s/market/playerMarkets/%d", serverId, playerMarketId),
                future::complete);

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null) return CompletableFuture.completedFuture(null);
            PlayerMarkets playerMarkets = dataSnapshot.getValue(PlayerMarkets.class);

            if (playerMarkets == null || playerMarkets.getOnSale() == null)
                return CompletableFuture.completedFuture(null);
            List<PlayerMarkets.OnSale> onSaleList = new ArrayList<>(playerMarkets.getOnSale());

            return CompletableFuture.completedFuture(onSaleList);
        });
    }

    public static void getMarketStandardList(String serverId, ValueReturn<MarketStandardList> marketStandardListValueReturn) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData(String.format("server/%s/market/marketStandard", serverId),
                dataSnapshot -> {
                    if (dataSnapshot == null) {
                        marketStandardListValueReturn.valueReturn(null);
                        return;
                    }

                    MarketStandardList marketStandardList = new MarketStandardList();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        MarketStandard marketStandard = ds.getValue(MarketStandard.class);
                        if (marketStandard != null) marketStandardList.add(marketStandard);
                    }

                    marketStandardListValueReturn.valueReturn(marketStandardList);
                });
    }

    /**
     * This <i>sell</i> method inserts a resource for sale in the market. It also updates the inventory quantity of said resource.
     * But this does not cross-check the following: <br/>
     * <ul>
     *     <li>Quantity in the inventory and the resource being sold.</li>
     *     <li>Existence of the resource in the inventory.</li>
     * </ul>
     *
     * @param onSale   OnSale object that contains the data of the resource being sold.
     * @param player   Current Player
     * @param playerId Current player's id.
     */
    public static void sell(PlayerMarkets.OnSale onSale, Player player, Integer playerId) {
        FirebaseData firebaseData = new FirebaseData();

        StoreDataFunctions.getPlayerMarket(player.getServer(), player.getMarket().getId()).thenAccept(onSales -> {
            boolean existing = false; // for checking if a sale exists already.
            int index = -1; // for keeping track of the location of the onSale.
            PlayerMarkets.OnSale onSaleCopy = new PlayerMarkets.OnSale(); // for taking note of the existing onSale, in case it EXISTS.

            // iterate through existing sales
            if (onSales != null) {
                for (PlayerMarkets.OnSale onSaleItem : onSales) {
                    index++;
                    if (onSaleItem.equals(onSale)) {
                        onSaleCopy = onSaleItem;
                        existing = true;
                        break;
                    }
                }
            }

            if (existing) {
                // if sale exists, just add the quantity to that same sale.
                Integer qty = onSaleCopy.getQuantity() + onSale.getQuantity();
                onSaleCopy.setQuantity(qty);
                firebaseData.setValue(String.format(Locale.getDefault(),
                        "server/%s/market/playerMarkets/%d/onSale/%d",
                        player.getServer(), player.getMarket().getId(), index), onSaleCopy);
            } else {
                // if it doesn't, add the new onSale among the existing sales. use index + 1 as its id.
                onSale.setOnSaleId(index + 1);
                if (onSales != null) {
                    onSales.add(onSale);
                    firebaseData.setValue(String.format(Locale.getDefault(),
                            "server/%s/market/playerMarkets/%d/onSale",
                            player.getServer(), player.getMarket().getId()), onSales);
                } else {
                    firebaseData.setValue(String.format(Locale.getDefault(),
                            "server/%s/market/playerMarkets/%d/onSale/0",
                            player.getServer(), player.getMarket().getId()), onSale);
                }

            }
        });

        // update the inventory for its new quantity.
        InventoryDataFunctions.getInventoryItem(playerId, onSale.getInventoryResourceReference()).thenAccept(inventory -> {
            int finalQuantity = inventory.getQuantity() - onSale.getQuantity();
            if (finalQuantity > 0) {
                inventory.setQuantity(finalQuantity);
                firebaseData.setValue(
                        String.format(Locale.getDefault(), "player/%d/inventory/%d", playerId, onSale.getInventoryResourceReference()),
                        inventory
                );
            } else {
                firebaseData.removeData(
                        String.format(Locale.getDefault(), "player/%d/inventory/%d", playerId, onSale.getInventoryResourceReference()));
            }
        });
    }

    public static void editSale(PlayerMarkets.OnSale onSale, Player player, Integer playerId) {
        FirebaseData firebaseData = new FirebaseData();
        StoreDataFunctions.getPlayerMarket(player.getServer(), player.getMarket().getId()).thenAccept(onSales -> {
            PlayerMarkets.OnSale onSaleTarget = null;
            for (PlayerMarkets.OnSale onSaleItem : onSales) {
                if (onSaleItem.getOnSaleId().equals(onSale.getOnSaleId())) {
                    onSaleTarget = onSaleItem;
                    break;
                }
            }

            if (onSaleTarget == null) {
                if (onSale.getQuantity() > 0) sell(onSale, player, playerId);
                return;
            }

            if (onSaleTarget.getQuantity() > onSale.getQuantity() * -1) {
                PlayerMarkets.OnSale finalOnSale = new PlayerMarkets.OnSale(onSale);
                finalOnSale.setQuantity(onSaleTarget.getQuantity() + onSale.getQuantity());
                firebaseData.setValue(String.format(Locale.getDefault(),
                        "server/%s/market/playerMarkets/%d/onSale/%d",
                        player.getServer(), player.getMarket().getId(), onSale.getOnSaleId()), finalOnSale);
            } else if (onSaleTarget.getQuantity() < onSale.getQuantity() * -1) {
                onSale.setQuantity(onSaleTarget.getQuantity());
                firebaseData.removeData(String.format(Locale.getDefault(),
                        "server/%s/market/playerMarkets/%d/onSale/%d",
                        player.getServer(), player.getMarket().getId(), onSale.getOnSaleId()));
            } else {
                firebaseData.removeData(String.format(Locale.getDefault(),
                        "server/%s/market/playerMarkets/%d/onSale/%d",
                        player.getServer(), player.getMarket().getId(), onSale.getOnSaleId()));
            }
            InventoryDataFunctions.getInventoryItem(playerId, onSale.getResourceId()).thenAccept(inventory -> {
                if (inventory == null) {
                    if (onSale.getQuantity() < 0) {
                        Inventory newInventory = new Inventory();
                        newInventory.setResourceId(onSale.getResourceId());
                        newInventory.setQuantity(onSale.getQuantity() * -1);
                        newInventory.setType(onSale.getType());
                        newInventory.setSellable(true);
                        InventoryDataFunctions.setInventoryItem(newInventory, playerId);
                    }
                    return;
                }
                Inventory newInventory = new Inventory(inventory); // copy
                newInventory.setQuantity(inventory.getQuantity() - onSale.getQuantity());
                if (newInventory.getQuantity() > 0) InventoryDataFunctions.setInventoryItem(newInventory, playerId);
                else InventoryDataFunctions.removeInventoryItem(playerId, newInventory.getResourceId());
            });
        });
    }

    public static PlayerMarkets setUpPlayerMarket(String server, String username, Integer playerId) {
        FirebaseData firebaseData = new FirebaseData();
        PlayerMarkets playerMarkets = new PlayerMarkets();
        playerMarkets.setMarketOwner(playerId);
        playerMarkets.setStoreName(String.format("%s's Store", username));
        playerMarkets.setOpened(System.currentTimeMillis());

        firebaseData.retrieveData(String.format("server/%s/market/playerMarkets", server), dataSnapshot -> {
            if (dataSnapshot == null) return;
            long currentIndex = dataSnapshot.getChildrenCount();
            playerMarkets.setMarketId(Math.toIntExact(currentIndex));
            firebaseData.setValue(String.format(Locale.getDefault(), "server/%s/market/playerMarkets/%d", server, currentIndex), playerMarkets);
            firebaseData.setValue(String.format(Locale.getDefault(), "player/%d/market/id", playerId), currentIndex);
        });

        return playerMarkets;
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
        public void startListener(ValueReturn<PlayerMarkets> listener) {
            firebaseData.retrieveDataRealTime(childPath, dataSnapshot -> {
                if (dataSnapshot == null || !dataSnapshot.exists())
                    listener.valueReturn(null);
                else {
                    listener.valueReturn(dataSnapshot.getValue(PlayerMarkets.class));
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

    public static CompletableFuture<Float> getMarketPrice(String serverId, Integer resourceId) {
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        FirebaseData firebaseData = new FirebaseData();

        firebaseData.retrieveData(String.format(Locale.getDefault(),
                "server/%s/market/marketStandard/%d/marketPrice",
                serverId, resourceId), future::complete);

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null || dataSnapshot.getValue(Float.class) == null)
                return CompletableFuture.completedFuture(null);
            return CompletableFuture.completedFuture(dataSnapshot.getValue(Float.class));
        });
    }

}
