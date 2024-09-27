package com.capstone.merkado.Objects.StoresDataObjects;

import java.util.List;

public class MarketData {

    /**
     * <b>CurrentSupplyDemand</b>. Contains supply and demand data used to keep track the existing supply count and demand data: demand for the current hour and within 24 hours, as well as the timestamps for the two. <i>Model Class</i>.
     */
    public static class CurrentSupplyDemand {
        CurrentDemand demand;
        Integer supply;

        public CurrentSupplyDemand() {
        }

        public CurrentDemand getDemand() {
            return demand;
        }

        public void setDemand(CurrentDemand demand) {
            this.demand = demand;
        }

        public Integer getSupply() {
            return supply;
        }

        public void setSupply(Integer supply) {
            this.supply = supply;
        }

        public static class CurrentDemand {

            Integer hour1;
            Integer hour24;

            public CurrentDemand() {
            }

            public Integer getHour1() {
                return hour1;
            }

            public void setHour1(Integer hour1) {
                this.hour1 = hour1;
            }

            public Integer getHour24() {
                return hour24;
            }

            public void setHour24(Integer hour24) {
                this.hour24 = hour24;
            }

        }
    }

    /**
     * <b>InflationRate</b>. Contains inflation rate data: total weighted price, current time, inflation rate. <i>Model Class</i>.
     */
    public static class InflationRate {
        Integer totalWeightedPrice;
        String currentTime;
        Float inflationRate;
        Float purchasingPower;
        String updateTime;

        public InflationRate() {
        }

        public Integer getTotalWeightedPrice() {
            return totalWeightedPrice;
        }

        public void setTotalWeightedPrice(Integer totalWeightedPrice) {
            this.totalWeightedPrice = totalWeightedPrice;
        }

        public String getCurrentTime() {
            return currentTime;
        }

        public void setCurrentTime(String currentTime) {
            this.currentTime = currentTime;
        }

        public Float getInflationRate() {
            return inflationRate;
        }

        public void setInflationRate(Float inflationRate) {
            this.inflationRate = inflationRate;
        }

        public Float getPurchasingPower() {
            return purchasingPower;
        }

        public void setPurchasingPower(Float purchasingPower) {
            this.purchasingPower = purchasingPower;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }

    /**
     * <b>MarketHistory</b>. Contains historical data to calculate for the new price and inflation rate:
     * demand and total weighted price. <i>Container Class</i>.
     */
    public static class MarketHistory {
        List<Integer> totalWeightedPrice;
        List<Demand> demand;

        public MarketHistory() {
        }

        public List<Integer> getTotalWeightedPrice() {
            return totalWeightedPrice;
        }

        public void setTotalWeightedPrice(List<Integer> totalWeightedPrice) {
            this.totalWeightedPrice = totalWeightedPrice;
        }

        public List<Demand> getDemand() {
            return demand;
        }

        public void setDemand(List<Demand> demand) {
            this.demand = demand;
        }

        public static class Demand {
            Integer resourceId;
            String time;
            Integer count;

            public Demand() {
            }

            public Integer getResourceId() {
                return resourceId;
            }

            public void setResourceId(Integer resourceId) {
                this.resourceId = resourceId;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public Integer getCount() {
                return count;
            }

            public void setCount(Integer count) {
                this.count = count;
            }
        }
    }

    public static class CompiledData {
        InflationRate inflationRate;
        List<MarketPrice> supplyDemands;

        public CompiledData(InflationRate inflationRate, List<MarketPrice> supplyDemands) {
            this.inflationRate = inflationRate;
            this.supplyDemands = supplyDemands;
        }

        public InflationRate getInflationRate() {
            return inflationRate;
        }

        public void setInflationRate(InflationRate inflationRate) {
            this.inflationRate = inflationRate;
        }

        public List<MarketPrice> getSupplyDemands() {
            return supplyDemands;
        }

        public void setSupplyDemands(List<MarketPrice> supplyDemands) {
            this.supplyDemands = supplyDemands;
        }
    }
}
