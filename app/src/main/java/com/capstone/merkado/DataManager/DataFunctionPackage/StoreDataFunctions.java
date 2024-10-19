package com.capstone.merkado.DataManager.DataFunctionPackage;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.FirebaseData;
import com.capstone.merkado.DataManager.ValueReturn.ValueReturn;
import com.capstone.merkado.Helpers.MathFormula;
import com.capstone.merkado.Helpers.PlayerActions;
import com.capstone.merkado.Helpers.StringProcessor;
import com.capstone.merkado.Objects.PlayerDataObjects.Player;
import com.capstone.merkado.Objects.ResourceDataObjects.Inventory;
import com.capstone.merkado.Objects.ServerDataObjects.MarketStandard.MarketStandard;
import com.capstone.merkado.Objects.ServerDataObjects.MarketStandard.MarketStandardList;
import com.capstone.merkado.Objects.StoresDataObjects.Market;
import com.capstone.merkado.Objects.StoresDataObjects.MarketData;
import com.capstone.merkado.Objects.StoresDataObjects.MarketData.CurrentSupplyDemand;
import com.capstone.merkado.Objects.StoresDataObjects.MarketData.InflationRate;
import com.capstone.merkado.Objects.StoresDataObjects.MarketPrice;
import com.capstone.merkado.Objects.StoresDataObjects.PlayerMarkets;
import com.capstone.merkado.Objects.StoresDataObjects.StoreBuyingData;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;

//@SuppressWarnings("unused")
public class StoreDataFunctions {

    public static void setUpStore(String server, Integer playerId, String username, Integer factoryId) {
        // create PlayerMarkets object
        PlayerMarkets playerMarkets = new PlayerMarkets();
        playerMarkets.setMarketOwner(playerId);
        playerMarkets.setOpened(currentTimeMillis());
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

        Market market = new Market();
        market.setId(index);
        market.setHadMarket(true);

        firebaseData.setValue(
                String.format(Locale.getDefault(), "player/%d/market/", playerId), market);
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

            // update actions
            PlayerActions.Store.buying(onSale.getResourceId(), storeBuyingData.getQuantity());

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
        if (playerId != -1)
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
                if (newInventory.getQuantity() > 0)
                    InventoryDataFunctions.setInventoryItem(newInventory, playerId);
                else
                    InventoryDataFunctions.removeInventoryItem(playerId, newInventory.getResourceId());
            });
        });
    }

    public static PlayerMarkets setUpPlayerMarket(String server, String username, Integer playerId) {
        FirebaseData firebaseData = new FirebaseData();
        PlayerMarkets playerMarkets = new PlayerMarkets();
        playerMarkets.setMarketOwner(playerId);
        playerMarkets.setStoreName(String.format("%s's Store", username));
        playerMarkets.setOpened(currentTimeMillis());

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
                "server/%s/market/marketStandard/marketPrice/%d/marketPrice",
                serverId, resourceId), future::complete);

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null || dataSnapshot.getValue(Float.class) == null)
                return CompletableFuture.completedFuture(null);
            return CompletableFuture.completedFuture(dataSnapshot.getValue(Float.class));
        });
    }

    // MARKET DATA

    /**
     * Checks for <b>Market Data</b> update time and tests if it is an hour (or more) later. If
     * it is, then the market data will process a reset. It will then return a long value of
     * the exact written update time.
     * @param serverId Server id.
     * @return A Long CompletableFuture of the updateTime.
     */
    public static CompletableFuture<Long> marketDataTimeCheck(String serverId, Float sensitivityFactor) {
        if (!getUpdaterPlayer()) return CompletableFuture.completedFuture(null);
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        firebaseData.retrieveData(
                String.format(Locale.getDefault(),
                        "server/%s/market/marketStandard/metadata/updateTime",
                        serverId),
                future::complete
        );
        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null) return CompletableFuture.completedFuture(null);
            if (!dataSnapshot.exists())
                // there are no updateTimes yet. get the defaults to the history and get last hour's time as history timestamp.
                processResets(
                        serverId,
                        sensitivityFactor,
                        StringProcessor.millisToServerHourString(currentTimeHourInMillis() - (1000 * 60 * 60)));
            else {
                // there are updateTimes.
                String updateTimeString = dataSnapshot.getValue(String.class);
                Long updateTime = StringProcessor.serverHourStringToMillis(updateTimeString);
                if (currentTimeHourInMillis() > updateTime) {
                    // if current hour exceeded updateTime.
                    processResets(serverId, sensitivityFactor, updateTime, currentTimeHourInMillis());
                } else return CompletableFuture.completedFuture(updateTime);
            }
            return CompletableFuture.completedFuture(currentTimeHourInMillis());
        });
    }

    public static CompletableFuture<Long> getMarketDataUpdateTime(String serverId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        firebaseData.retrieveData(
                String.format(Locale.getDefault(),
                        "server/%s/market/marketStandard/metadata/updateTime",
                        serverId),
                future::complete
        );
        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null || !dataSnapshot.exists())
                return CompletableFuture.completedFuture(null);

            String updateTimeString = dataSnapshot.getValue(String.class);
            Long updateTime = StringProcessor.serverHourStringToMillis(updateTimeString);
            return CompletableFuture.completedFuture(updateTime);
        });
    }

    public static void addDemandToResource(String serverId, Integer resourceId, Integer demandCount) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData(
                String.format(Locale.getDefault(),
                        "server/%s/market/marketStandard/marketPrice/%d/metadata",
                        serverId, resourceId),
                dataSnapshot -> {
                    if (dataSnapshot == null) return;
                    CurrentSupplyDemand supplyDemand = dataSnapshot.getValue(CurrentSupplyDemand.class);
                    Integer hour1demand = demandCount;
                    Integer hour24demand = demandCount;
                    if (supplyDemand != null) {
                        hour1demand = Objects.requireNonNullElse(supplyDemand.getDemand().getHour1(), 0) + hour1demand;
                        hour24demand = Objects.requireNonNullElse(supplyDemand.getDemand().getHour24(), 0) + hour24demand;
                    }
                    setCurrentDemand(serverId, resourceId, hour1demand, hour24demand);
                }
        );
    }

    public static void addSupplyToResource(String serverId, Integer resourceId, Integer supplyCount) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData(
                String.format(Locale.getDefault(),
                        "server/%s/market/marketStandard/marketPrice/%d/metadata",
                        serverId, resourceId),
                dataSnapshot -> {
                    if (dataSnapshot == null) return;
                    CurrentSupplyDemand supplyDemand = dataSnapshot.getValue(CurrentSupplyDemand.class);
                    Integer newSupply = supplyCount;
                    if (supplyDemand != null)
                        newSupply = Objects.requireNonNullElse(supplyDemand.getSupply(), 0) + newSupply;
                    setCurrentSupply(serverId, resourceId, newSupply);
                }
        );
    }

    public static CompletableFuture<MarketData.CompiledData> getCompiledMarketData(String serverId) {
        return getCurrentMarketPrices(serverId).thenCombine(getGeneralMetadata(serverId),
                ((marketPrices, inflationRate) ->
                        new MarketData.CompiledData(inflationRate, marketPrices)));
    }

    private static void processResets(String serverId, Float sensitivityFactor, Long updateTime, Long currentTime) {
        long hoursPassed = (currentTime - updateTime) / (1000 * 60 * 60);

        List<String> hourStringList = new ArrayList<>();
        for (int i = 1; i <= hoursPassed; i++) {
            String hourString = StringProcessor.millisToServerHourString(updateTime + (1000L * 60 * 60 * i));
            hourStringList.add(hourString);
        }

        recalculatePrice(serverId, sensitivityFactor);

        hourStringList.forEach(hourString -> transferDemandToHistory(serverId, hourString));
        resetDemand(serverId);

        hourStringList.forEach(hourString -> transferGeneralMetadataToHistory(serverId, hourString));
        calculateGeneralMetadata(serverId);

        setMarketDataUpdateTime(serverId);
    }

    /**
     * Calls functions that is used to reset multiple parts of market data.
     * @param serverId server id.
     * @param sensitivityFactor sensitivity factor from the server owner.
     * @param updateTime update time string before the reset.
     */
    private static void processResets(String serverId, Float sensitivityFactor, String updateTime) {
        recalculatePrice(serverId, sensitivityFactor);

        transferDemandToHistory(serverId, updateTime);
        resetDemand(serverId);

        transferGeneralMetadataToHistory(serverId, updateTime);
        calculateGeneralMetadata(serverId);

        setMarketDataUpdateTime(serverId);
    }

    /**
     * Calculates the new prices for each resources for sale.
     * @param serverId server id.
     * @param sensitivityFactor sensitivity factor from server settings.
     */
    private static void recalculatePrice(String serverId, Float sensitivityFactor) {
        getCurrentMarketPrices(serverId).thenAccept(marketPrices -> {
            if (marketPrices == null) return;
            for (MarketPrice marketPrice : marketPrices) {
                Float newPrice = MathFormula.getNewPrice(
                        marketPrice.getMarketPrice(),
                        sensitivityFactor,
                        marketPrice.getMetadata().getDemand().getHour24(),
                        marketPrice.getMetadata().getSupply());
                setMarketPrice(serverId, marketPrice.getResourceId(), newPrice);
            }
        });
    }

    /**
     * Resets the demand data for each resources. <i>Note that price
     * recalculation and data transfer should go first before the reset.</i>
     * @param serverId server id.
     */
    private static void resetDemand(String serverId) {
        cleanHistoryDemand(serverId);
        getHistoryDemandSum(serverId).thenAccept(demandSums -> {
            // if there are no returned sums. Set hour24 to 0.
            if (demandSums == null) {
                for (int resourceId = 2; resourceId <= 23; resourceId++) {
                    setCurrentDemand(serverId, resourceId, 0, 0);
                }
                return;
            }

            // if there are returned sums. Set hour24 to the corresponding values (default: 0).
            for (int resourceId = 2; resourceId <= 23; resourceId++) {
                setCurrentDemand(
                        serverId,
                        resourceId,
                        0,
                        demandSums.getOrDefault(resourceId, 0));
            }
        });
    }

    /**
     * Transfers the demand data gathered from current copy of resources to the history. <i>Note: This should go first before resetting the values in current marketStandard price.</i>
     * @param serverId Server id.
     * @param updateTime Formatted string for the timestamp of this data.
     */
    private static void transferDemandToHistory(String serverId, String updateTime) {
        getCurrentMarketPrices(serverId).thenAccept(marketPrices -> {
            for (MarketPrice marketPrice : marketPrices) {
                setHistoryDemand(serverId,
                        marketPrice.getResourceId(),
                        updateTime,
                        marketPrice.getMetadata().getDemand().getHour1());
            }
        });
    }

    /**
     * Calculates for the Total Weighted Price and Inflation Rate.
     * <i>Note: price recalculation should go first before
     * recalculation of total weighted price, inflation rate,
     * and purchasing power. Also, make sure that the previous
     * TWP must be transferred to history.</i>
     * @param serverId server id.
     */
    private static void calculateGeneralMetadata(String serverId) {
        getCurrentMarketPrices(serverId).thenAccept(marketPrices -> {
            Float twp = MathFormula.getTotalWeightedPrice(marketPrices);
            Long hour24 = currentTimeHourInMillis() - (1000 * 60 * 60 * 24);
            String hour24String = StringProcessor.millisToServerHourString(hour24);
            getHistoryTotalWeightedPrice(serverId, hour24String).thenAccept(twp24 -> {
                Float inflationRate = MathFormula.getInflationRate(twp, twp24);
                Float purchasingPower = MathFormula.getPurchasingPower(twp, twp24);
                setMarketStandardMetadata(serverId, twp, inflationRate, purchasingPower);
            });
        });
    }

    private static CompletableFuture<InflationRate> getGeneralMetadata(String serverId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();

        firebaseData.retrieveData(
                String.format("server/%s/market/marketStandard/metadata",
                        serverId),
                future::complete
        );

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null || !dataSnapshot.exists())
                return CompletableFuture.completedFuture(null);
            return CompletableFuture.completedFuture(dataSnapshot.getValue(InflationRate.class));
        });
    }

    private static void transferGeneralMetadataToHistory(String serverId, String updateTime) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData(
                String.format("server/%s/market/marketStandard/metadata/totalWeightedPrice",
                        serverId),
                dataSnapshot -> {
                    Float totalWeightedPrice;
                    if (dataSnapshot == null || !dataSnapshot.exists()) {
                        totalWeightedPrice = MathFormula.getTotalWeightedPrice(getSavedMarketPrice());
                    } else totalWeightedPrice = dataSnapshot.getValue(Float.class);
                    if (totalWeightedPrice == null) return;
                    setHistoryTotalWeightedPrice(serverId, updateTime, totalWeightedPrice);
                    cleanHistoryTWP(serverId);
                }
        );
    }

    private static void setMarketDataUpdateTime(String serverId) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(
                String.format("server/%s/market/marketStandard/metadata/updateTime",
                        serverId),
                StringProcessor.millisToServerHourString(currentTimeMillis())
        );
    }

    private static void cleanHistoryDemand(String serverId) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData(
                String.format("server/%s/market/marketHistory/demand",
                        serverId),
                dataSnapshot -> {
                    if (dataSnapshot == null || !dataSnapshot.exists()) return;
                    // get current time and get 24-hour time
                    long currentTime = currentTimeHourInMillis();
                    long time24hours = currentTime - (1000 * 60 * 60 * 25);
                    for (DataSnapshot resourceDS : dataSnapshot.getChildren()) {
                        // get resourceId
                        String resourceIdString = resourceDS.getKey();
                        Integer resourceId;

                        // parse resourceId from string. if not parseable, skip.
                        try {
                            resourceId = Integer.parseInt(resourceIdString);
                        } catch (NumberFormatException ignore) {
                            continue;
                        }

                        if (!resourceDS.hasChildren())
                            continue; // skip if it does not contain anything.

                        for (DataSnapshot demandDS : resourceDS.getChildren()) {
                            String demandTimeString = demandDS.getKey();
                            Long demandTime = StringProcessor.serverHourStringToMillis(demandTimeString);
                            if (time24hours >= demandTime) {
                                deleteHistoryDemand(serverId, resourceId, demandTimeString);
                            }
                        }
                    }
                }
        );
    }

    private static void cleanHistoryTWP(String serverId) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.retrieveData(
                String.format("server/%s/market/marketHistory/totalWeightedPrice",
                        serverId),
                dataSnapshot -> {
                    if (dataSnapshot == null || !dataSnapshot.exists()) return;
                    // get current time and get 24-hour time
                    long currentTime = currentTimeHourInMillis();
                    long time24hours = currentTime - (1000 * 60 * 60 * 25);

                    for (DataSnapshot twpDS : dataSnapshot.getChildren()) {
                        String timeString = twpDS.getKey();
                        Long time = StringProcessor.serverHourStringToMillis(timeString);
                        if (time24hours >= time) {
                            deleteHistoryTWP(serverId, timeString);
                        }
                    }
                }
        );
    }

    private static void setMarketStandardMetadata(String serverId, Float totalWeightedPrice, Float inflationRate, Float purchasingPower) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(
                String.format(Locale.getDefault(),
                        "server/%s/market/marketStandard/metadata/totalWeightedPrice",
                        serverId),
                totalWeightedPrice
        );
        firebaseData.setValue(
                String.format(Locale.getDefault(),
                        "server/%s/market/marketStandard/metadata/inflationRate",
                        serverId),
                inflationRate
        );
        firebaseData.setValue(
                String.format(Locale.getDefault(),
                        "server/%s/market/marketStandard/metadata/purchasingPower",
                        serverId),
                purchasingPower
        );
    }

    private static void setCurrentDemand(String serverId, Integer resourceId, Integer demand1hour, Integer demand24hours) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(
                String.format(Locale.getDefault(),
                        "server/%s/market/marketStandard/marketPrice/%d/metadata/demand/hour1",
                        serverId, resourceId),
                demand1hour);
        firebaseData.setValue(
                String.format(Locale.getDefault(),
                        "server/%s/market/marketStandard/marketPrice/%d/metadata/demand/hour24",
                        serverId, resourceId),
                demand24hours);
    }

    private static void setCurrentSupply(String serverId, Integer resourceId, Integer newSupply) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(
                String.format(Locale.getDefault(),
                        "server/%s/market/marketStandard/marketPrice/%d/metadata/supply",
                        serverId, resourceId),
                newSupply);
    }

    /**
     * Retrieves the sum of all demand within the current 24-hour time period.
     * @param serverId id of the server.
     * @return <b>CompletableFuture</b> of a mapped <b>Resource Id</b> and <b>Sum</b>.
     */
    private static CompletableFuture<Map<Integer, Integer>> getHistoryDemandSum(String serverId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
        firebaseData.retrieveData(
                String.format("server/%s/market/marketHistory/demand",
                        serverId),
                future::complete
        );

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null || !dataSnapshot.exists())
                return CompletableFuture.completedFuture(null);

            // get current time and get 24-hour time
            long currentTime = currentTimeHourInMillis();
            long time24hours = currentTime - (1000 * 60 * 60 * 24);

            Map<Integer, Integer> demandSums = new HashMap<>();

            // iterate through each resources
            for (DataSnapshot resourceDS : dataSnapshot.getChildren()) {
                // get resourceId
                String resourceIdString = resourceDS.getKey();
                int resourceId;

                // parse resourceId from string. if not parseable, skip.
                try {
                    resourceId = Integer.parseInt(resourceIdString);
                } catch (NumberFormatException ignore) {
                    continue;
                }

                if (!resourceDS.hasChildren())
                    continue; // skip if it does not contain anything.


                Integer currentSum = 0;

                // iterate through each demand entry
                for (DataSnapshot demandDS : resourceDS.getChildren()) {
                    String demandTimeString = demandDS.getKey();
                    Long demandTime = StringProcessor.serverHourStringToMillis(demandTimeString);

                    if (time24hours <= demandTime) currentSum += demandDS.getValue(Integer.class);
                }

                demandSums.put(resourceId, currentSum);
            }

            return CompletableFuture.completedFuture(demandSums);
        });
    }

    private static void deleteHistoryDemand(String serverId, Integer resourceId, String time) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.removeData(
                String.format(Locale.getDefault(),
                        "server/%s/market/marketHistory/demand/%d/%s",
                        serverId, resourceId, time)
        );
    }

    private static void deleteHistoryTWP(String serverId, String time) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.removeData(
                String.format(Locale.getDefault(),
                        "server/%s/market/marketHistory/totalWeightedPrice/%s",
                        serverId, time)
        );
    }

    private static void setHistoryDemand(String serverId, Integer resourceId, String time, Integer demand) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(
                String.format(Locale.getDefault(),
                        "server/%s/market/marketHistory/demand/%d/%s",
                        serverId, resourceId, time),
                demand);
    }

    private static void setHistoryTotalWeightedPrice(String serverId, String time, Float twp) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(
                String.format(Locale.getDefault(),
                        "server/%s/market/marketHistory/totalWeightedPrice/%s",
                        serverId, time),
                twp);
    }

    private static CompletableFuture<Float> getHistoryTotalWeightedPrice(String serverId, String time) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();

        firebaseData.retrieveData(
                String.format(
                        "server/%s/market/marketHistory/totalWeightedPrice/%s",
                        serverId, time
                ),
                future::complete
        );

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null || !dataSnapshot.exists())
                return CompletableFuture.completedFuture(null);
            return CompletableFuture.completedFuture(dataSnapshot.getValue(Float.class));
        });
    }

    private static CompletableFuture<List<MarketPrice>> getCurrentMarketPrices(String serverId) {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();

        firebaseData.retrieveData(
                String.format(
                        "server/%s/market/marketStandard/marketPrice",
                        serverId
                ),
                future::complete
        );

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null) return CompletableFuture.completedFuture(null);
            List<MarketPrice> marketPriceList = new ArrayList<>();
            for (DataSnapshot resource : dataSnapshot.getChildren()) {
                MarketPrice marketPrice = resource.getValue(MarketPrice.class);
                if (marketPrice == null) continue;
                marketPriceList.add(marketPrice);
            }

            return CompletableFuture.completedFuture(marketPriceList);
        });
    }

    private static void setMarketPrice(String serverId, Integer resourceId, Float newPrice) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setValue(
                String.format(
                        Locale.getDefault(),
                        "server/%s/market/marketStandard/marketPrice/%d/marketPrice",
                        serverId, resourceId
                ),
                newPrice
        );
    }

    private static Long currentTimeMillis() {
        Merkado merkado = Merkado.getInstance();
        return merkado.currentTimeMillis();
    }

    private static Long currentTimeHourInMillis() {
        // Create a Calendar instance and set it to the current time
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.setTimeInMillis(currentTimeMillis());

        // Round down to the nearest hour (set minutes, seconds, and milliseconds to zero)
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Get the rounded current time in milliseconds
        return calendar.getTimeInMillis();
    }

    private static Boolean getUpdaterPlayer() {
        Merkado merkado = Merkado.getInstance();
        return merkado.getUpdaterPlayer();
    }

    private static List<MarketPrice> getSavedMarketPrice() {
        Merkado merkado = Merkado.getInstance();
        return merkado.getCompiledMarketData().getSupplyDemands();
    }
}
