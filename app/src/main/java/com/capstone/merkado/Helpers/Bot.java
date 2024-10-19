package com.capstone.merkado.Helpers;

import static com.capstone.merkado.DataManager.DataFunctionPackage.StoreDataFunctions.getMarketPrice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctionPackage.InternalDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.ServerDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.StoreDataFunctions;
import com.capstone.merkado.DataManager.FirebaseData;
import com.capstone.merkado.Objects.FactoryDataObjects.PlayerFactory;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceData;
import com.capstone.merkado.Objects.ServerDataObjects.OtherServerDetails;
import com.capstone.merkado.Objects.StoresDataObjects.PlayerMarkets;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * This class contains the methods necessary for making the bot (computer-mode) work.
 */
public class Bot {

    public static CompletableFuture<Map<BotType, Boolean>> getBotAvailability(String serverId) {
        return ServerDataFunctions.getOtherServerDetails(serverId).thenCompose(otherServerDetails -> {
            Boolean store = otherServerDetails.getBots().getStore();
            Boolean factory = otherServerDetails.getBots().getFactory();
            Map<BotType, Boolean> botTypeBooleanHashMap = new HashMap<>();
            botTypeBooleanHashMap.put(BotType.STORE, store);
            botTypeBooleanHashMap.put(BotType.FACTORY, factory);
            return CompletableFuture.completedFuture(botTypeBooleanHashMap);
        });
    }

    /**
     * Checks for the removal of the specified bot. Level 2 for Store, and Level 4 for Factory.
     * @param botType [BotType.STORE || BotType.FACTORY]
     * @param serverId Server's id
     * @param currentPlayerInLevel The current count of the player in either Level 2 or 4.
     * @return CompletableFuture&lt;Boolean&gt;
     */
    public static CompletableFuture<Boolean> checkForBotRemoval(@NonNull BotType botType, @NonNull String serverId, @Nullable Integer currentPlayerInLevel) {
        if (currentPlayerInLevel == null) {
            return ServerDataFunctions.getOtherServerDetails(serverId).thenCombine(
                    ServerDataFunctions.getSettings(serverId), (otherServerDetails, settings) -> {
                        if (otherServerDetails == null) return false;
                        Integer currentCount = BotType.STORE.equals(botType) ?
                                otherServerDetails.getReachedLevels().getLvl2() :
                                otherServerDetails.getReachedLevels().getLvl4();
                        return currentCount != null &&
                                currentCount >= Math.round(settings.getPlayerLimit() * settings.getRequiredPercentage());
                    }
            );
        } else {
            return ServerDataFunctions.getSettings(serverId).thenCompose(settings -> CompletableFuture.completedFuture(
                    currentPlayerInLevel >= Math.round(settings.getPlayerLimit() * settings.getRequiredPercentage())
            ));
        }
    }

    /**
     * Removes Nibble's Store and adds Nibble's factory.
     * @param serverId Server's Id.5
     */
    public static void removeStoreBot(String serverId) {
        DataFunctions.removeBotStore(serverId, BotType.STORE);
        Factory.setUpFactory(serverId);
        ServerDataFunctions.setBots(serverId, new OtherServerDetails.Bots(false, true));
    }

    public static void removeFactoryBot(String serverId) {
        DataFunctions.removeBotStore(serverId, BotType.FACTORY);
        ServerDataFunctions.setBots(serverId, new OtherServerDetails.Bots(false, true));
    }

    public enum BotType {
        STORE, FACTORY
    }

    public static class Store {
        public static void setUpStore(String serverId) {
            DataFunctions.setUpStore(serverId).thenAccept(success -> {
                if (success) restockSales(serverId);
            });
        }

        public static void checkToRestock(String serverId) {
            DataFunctions.isTimeToRestock(serverId, BotType.STORE).thenAccept(isTimeToRestock -> {
                if (isTimeToRestock) {
                    restockSales(serverId);
                }
            });
        }

        private static void restockSales(String serverId) {
            StockCalculator stockCalculator = new StockCalculator();
            DataFunctions.getStocks(serverId, BotType.STORE).thenAccept(stocks -> {
                if (stocks == null) return;
                List<Stock> currentStock = addMissingStock(serverId, stockCalculator,
                        countNeededStock(serverId, stockCalculator, stocks));
                addStockToStore(serverId, currentStock);
                DataFunctions.updateStockingTime(serverId, BotType.STORE);
            });
        }

        private static List<Stock> countNeededStock(String serverId, StockCalculator stockCalculator, List<Stock> currentStock) {
            List<Stock> finalStock = new ArrayList<>();
            for (Stock stock : currentStock) {
                Integer neededStock = stockCalculator.getNeededStock(stock);
                StoreDataFunctions.addSupplyToResource(serverId, stock.getResourceId(), neededStock);
                Integer totalStock = neededStock + stock.getStock();
                finalStock.add(new Stock(stock.getResourceId(), totalStock));
            }

            return finalStock;
        }

        private static List<Stock> addMissingStock(String serverId, StockCalculator stockCalculator, List<Stock> currentStock) {
            List<Stock> missingStock = stockCalculator.getMissingStock(currentStock);
            for (Stock stock : missingStock) {
                StoreDataFunctions.addSupplyToResource(serverId, stock.getResourceId(), stock.getStock());
            }
            currentStock.addAll(missingStock);
            return currentStock;
        }

        private static void addStockToStore(String serverId, List<Stock> currentStock) {
            DataFunctions.setStock(serverId, currentStock, BotType.STORE);
        }
    }

    public static class Factory {
        public static void setUpFactory(String serverId) {
            DataFunctions.setUpFactory(serverId).thenAccept(success -> {
                if (success) restockSales(serverId);
            });
        }

        public static void checkToRestock(String serverId) {
            DataFunctions.isTimeToRestock(serverId, BotType.FACTORY).thenAccept(isTimeToRestock -> {
                if (isTimeToRestock) {
                    restockSales(serverId);
                }
            });
        }

        private static void restockSales(String serverId) {
            StockCalculator stockCalculator = new StockCalculator();
            DataFunctions.getStocks(serverId, BotType.FACTORY).thenAccept(stocks -> {
                if (stocks == null) return;
                List<Stock> currentStock = addMissingStock(serverId, stockCalculator,
                        countNeededStock(serverId, stockCalculator, stocks));
                addStockToStore(serverId, currentStock);
                DataFunctions.updateStockingTime(serverId, BotType.FACTORY);
            });
        }

        private static List<Stock> countNeededStock(String serverId, StockCalculator stockCalculator, List<Stock> currentStock) {
            List<Stock> finalStock = new ArrayList<>();
            for (Stock stock : currentStock) {
                Integer neededStock = stockCalculator.getNeededStock(stock);
                StoreDataFunctions.addSupplyToResource(serverId, stock.getResourceId(), neededStock);
                Integer totalStock = neededStock + stock.getStock();
                finalStock.add(new Stock(stock.getResourceId(), totalStock));
            }

            return finalStock;
        }

        private static List<Stock> addMissingStock(String serverId, StockCalculator stockCalculator, List<Stock> currentStock) {
            List<Stock> missingStock = stockCalculator.getMissingStock(currentStock);
            for (Stock stock : missingStock) {
                StoreDataFunctions.addSupplyToResource(serverId, stock.getResourceId(), stock.getStock());
            }
            currentStock.addAll(missingStock);
            return currentStock;
        }

        private static void addStockToStore(String serverId, List<Stock> currentStock) {
            DataFunctions.setStock(serverId, currentStock, BotType.FACTORY);
        }
    }

    public static class DataFunctions {
        public static CompletableFuture<Boolean> setUpStore(String serverId) {
            FirebaseData firebaseData = new FirebaseData();
            CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
            PlayerMarkets playerMarkets = new PlayerMarkets();

            playerMarkets.setMarketOwner(-1);
            playerMarkets.setOpened(getCurrentDay());
            playerMarkets.setStoreName("Nibble's Store");
            firebaseData.retrieveData(String.format("server/%s/market/playerMarkets", serverId), future::complete);

            return future.thenCompose(dataSnapshot -> {
                if (dataSnapshot == null) return CompletableFuture.completedFuture(false);
                Integer i = 0;
                for (DataSnapshot store : dataSnapshot.getChildren()) {
                    try {
                        store.getValue(PlayerMarkets.class);
                    } catch (Exception e) {
                        createMarket(serverId, i, playerMarkets).thenCompose(
                                unused -> CompletableFuture.completedFuture(true));
                    }
                    i++;
                }
                return createMarket(serverId, i, playerMarkets).thenCompose(
                        unused -> CompletableFuture.completedFuture(true));
            });
        }

        public static CompletableFuture<Boolean> setUpFactory(String serverId) {
            FirebaseData firebaseData = new FirebaseData();
            CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
            PlayerFactory playerFactory = new PlayerFactory();

            playerFactory.setFactoryOwner(-1);
            playerFactory.setOpened(getCurrentDay());
            playerFactory.setFactoryName("Nibble's Universal Factory");
            playerFactory.setFactoryType("BOT");

            firebaseData.retrieveData(String.format("server/%s/market/playerFactory", serverId), future::complete);

            return future.thenCompose(dataSnapshot -> {
                if (dataSnapshot == null) return CompletableFuture.completedFuture(false);
                Integer i = 0;
                for (DataSnapshot store : dataSnapshot.getChildren()) {
                    try {
                        store.getValue(PlayerFactory.class);
                    } catch (Exception e) {
                        createFactory(serverId, i, playerFactory).thenCompose(
                                unused -> CompletableFuture.completedFuture(true));
                    }
                    i++;
                }
                return createFactory(serverId, i, playerFactory).thenCompose(
                        unused -> CompletableFuture.completedFuture(true));
            });
        }

        private static CompletableFuture<Void> createMarket(String serverId, Integer marketId, PlayerMarkets playerMarkets) {
            FirebaseData firebaseData = new FirebaseData();
            playerMarkets.setMarketId(marketId);

            firebaseData.setValue(
                    String.format(Locale.getDefault(),
                            "server/%s/market/playerMarkets/%d", serverId, marketId),
                    playerMarkets);

            return CompletableFuture.completedFuture(null);
        }

        private static CompletableFuture<Void> createFactory(String serverId, Integer factoryId, PlayerFactory playerFactory) {
            FirebaseData firebaseData = new FirebaseData();
            playerFactory.setFactoryId(factoryId);

            firebaseData.setValue(
                    String.format(Locale.getDefault(),
                            "server/%s/market/playerFactory/%d", serverId, factoryId),
                    playerFactory);

            return CompletableFuture.completedFuture(null);
        }

        public static CompletableFuture<List<Stock>> getStocks(String serverId, BotType botType) {
            CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
            FirebaseData firebaseData = new FirebaseData();
            firebaseData.retrieveData(
                    String.format(Locale.getDefault(), "server/%s/market/%s/-1", serverId,
                            BotType.STORE.equals(botType)?"playerMarkets":"playerFactory"),
                    future::complete
            );

            return future.thenCompose(dataSnapshot -> {
                if (dataSnapshot == null) return CompletableFuture.completedFuture(null);
                PlayerMarkets botMarket = dataSnapshot.getValue(PlayerMarkets.class);
                if (botMarket == null) return CompletableFuture.completedFuture(null);
                List<Stock> stockList = new ArrayList<>();
                for (PlayerMarkets.OnSale onSale : botMarket.getOnSale()) {
                    stockList.add(new Stock(onSale.getResourceId(), onSale.getQuantity()));
                }
                return CompletableFuture.completedFuture(stockList);
            });
        }

        public static void setStock(String serverId, List<Stock> stockList, BotType botType) {
            List<PlayerMarkets.OnSale> onSaleList = new ArrayList<>();
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (Stock stock : stockList) {
                ResourceData resourceData = InternalDataFunctions.getResourceData(stock.getResourceId());

                CompletableFuture<Void> future = getMarketPrice(serverId, resourceData.getResourceId())
                        .thenAccept(marketPrice -> {
                            synchronized (onSaleList) { // Ensures thread safety when adding to the list
                                onSaleList.add(new PlayerMarkets.OnSale(
                                        resourceData.getName(),
                                        resourceData.getResourceId(),
                                        resourceData.getType(),
                                        marketPrice,
                                        stock.getStock()
                                ));
                            }
                        });

                // Add the future to the list of futures
                futures.add(future);
            }

            CompletableFuture<Void> allDone = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allDone.thenRun(() -> {
                FirebaseData firebaseData = new FirebaseData();
                firebaseData.setValue(
                        String.format(Locale.getDefault(), "server/%s/market/%s/-1/onSale", serverId,
                                BotType.STORE.equals(botType)?"playerMarkets":"playerFactory"),
                        onSaleList);
                updateStockingTime(serverId, botType);
            });
        }

        public static CompletableFuture<Boolean> isTimeToRestock(String serverId, BotType botType) {
            FirebaseData firebaseData = new FirebaseData();
            CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
            firebaseData.retrieveData(
                    String.format(Locale.getDefault(), "server/%s/market/%s/-1/opened",
                            serverId,
                            BotType.STORE.equals(botType)?"playerMarkets":"playerFactory"),
                    future::complete
            );
            return future.thenCompose(dataSnapshot -> {
                if (dataSnapshot == null) return CompletableFuture.completedFuture(false);
                Long dayMillis = dataSnapshot.getValue(Long.class);
                return CompletableFuture.completedFuture(dayMillis == null || dayMillis < getCurrentDay());
            });
        }

        public static void removeBotStore(String serverId, BotType botType) {
            FirebaseData firebaseData = new FirebaseData();
            firebaseData.removeData(String.format(Locale.getDefault(), "server/%s/market/%s/-1", serverId,
                    BotType.STORE.equals(botType)?"playerMarkets":"playerFactory"));

        }

        private static void updateStockingTime(String serverId, BotType botType) {
            FirebaseData firebaseData = new FirebaseData();
            firebaseData.setValue(
                    String.format(Locale.getDefault(), "server/%s/market/%s/-1/opened", serverId,
                            BotType.STORE.equals(botType)?"playerMarkets":"playerFactory"),
                    getCurrentDay()
            );
        }

        private static long getCurrentDay() {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MILLISECOND, Math.toIntExact(Merkado.getInstance().getServerTimeOffset()));
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTimeInMillis();
        }
    }

    public static class StockCalculator {
        // <resourceId, stockCount>
        private final Map<Integer, Integer> stockList;
        private final List<Stock> stocks;

        public StockCalculator() {
            stockList = new HashMap<>();
            stockList.put(2, 40);
            stockList.put(3, 40);
            stockList.put(4, 40);
            stockList.put(5, 50);
            stockList.put(6, 50);
            stockList.put(14, 40);
            stockList.put(15, 40);
            stockList.put(16, 40);
            stockList.put(17, 50);
            stockList.put(18, 50);

            stocks = new ArrayList<>();
            stocks.add(new Stock(2, 40));
            stocks.add(new Stock(3, 40));
            stocks.add(new Stock(4, 40));
            stocks.add(new Stock(5, 50));
            stocks.add(new Stock(6, 50));
            stocks.add(new Stock(14, 40));
            stocks.add(new Stock(15, 40));
            stocks.add(new Stock(16, 40));
            stocks.add(new Stock(17, 50));
            stocks.add(new Stock(18, 50));
        }

        public Integer getNeededStock(Stock stock) {
            Integer stockCount = stockList.get(stock.getResourceId());
            if (stockCount != null) return stockCount - stock.getStock();
            return 0;
        }

        public List<Stock> getMissingStock(List<Stock> stocks) {
            List<Integer> existingIds = new ArrayList<>();
            for (Stock stock : stocks) {
                existingIds.add(stock.getResourceId());
            }

            List<Stock> stocksCopy = new ArrayList<>(this.stocks);
            for (int i = 0; i < this.stocks.size(); i++) {
                if (existingIds.contains(this.stocks.get(i).getResourceId())) {
                    stocksCopy.remove(this.stocks.get(i));
                }
            }

            return stocksCopy;
        }
    }

    public static class Stock {
        Integer resourceId;
        Integer stock;

        public Stock(Integer resourceId, Integer stock) {
            this.resourceId = resourceId;
            this.stock = stock;
        }

        public Integer getResourceId() {
            return resourceId;
        }

        public Integer getStock() {
            return stock;
        }
    }
}
