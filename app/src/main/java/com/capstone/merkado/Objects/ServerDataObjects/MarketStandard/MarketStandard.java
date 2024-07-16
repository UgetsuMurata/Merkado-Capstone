package com.capstone.merkado.Objects.ServerDataObjects.MarketStandard;

public class MarketStandard {
    Integer resourceId;
    Float marketPrice;

    public MarketStandard() {
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Float getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Float marketPrice) {
        this.marketPrice = marketPrice;
    }
}
