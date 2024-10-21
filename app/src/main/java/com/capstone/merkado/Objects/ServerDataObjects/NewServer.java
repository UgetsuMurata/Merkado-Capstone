package com.capstone.merkado.Objects.ServerDataObjects;

import com.capstone.merkado.Objects.ServerDataObjects.MarketStandard.MarketStandard;
import com.capstone.merkado.Objects.StoresDataObjects.MarketPrice;

import java.util.List;

@SuppressWarnings("unused")
public class NewServer {
    String name;
    String serverOwner;
    Integer serverImage;
    String key;
    Settings settings;
    Market market;

    public NewServer() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerOwner() {
        return serverOwner;
    }

    public void setServerOwner(String serverOwner) {
        this.serverOwner = serverOwner;
    }

    public Integer getServerImage() {
        return serverImage;
    }

    public void setServerImage(Integer serverImage) {
        this.serverImage = serverImage;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public static class Settings {
        Integer playerLimit;
        Float sensitivityFactor;
        Float requiredPercentage;

        public Settings() {
        }

        public Integer getPlayerLimit() {
            return playerLimit;
        }

        public void setPlayerLimit(Integer playerLimit) {
            this.playerLimit = playerLimit;
        }

        public Float getSensitivityFactor() {
            return sensitivityFactor;
        }

        public void setSensitivityFactor(Float sensitivityFactor) {
            this.sensitivityFactor = sensitivityFactor;
        }

        public Float getRequiredPercentage() {
            return requiredPercentage;
        }

        public void setRequiredPercentage(Float requiredPercentage) {
            this.requiredPercentage = requiredPercentage;
        }
    }

    public static class Market {
        MarketStandard marketStandard;

        public Market() {
        }

        public MarketStandard getMarketStandard() {
            return marketStandard;
        }

        public void setMarketStandard(MarketStandard marketStandard) {
            this.marketStandard = marketStandard;
        }

        public static class MarketStandard {
            List<MarketPrice> marketPrice;

            public MarketStandard() {
            }

            public List<MarketPrice> getMarketPrice() {
                return marketPrice;
            }

            public void setMarketPrice(List<MarketPrice> marketPrice) {
                this.marketPrice = marketPrice;
            }
        }
    }
}
