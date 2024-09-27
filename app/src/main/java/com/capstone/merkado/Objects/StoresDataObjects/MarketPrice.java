package com.capstone.merkado.Objects.StoresDataObjects;

import com.capstone.merkado.Objects.StoresDataObjects.MarketData.CurrentSupplyDemand;

@SuppressWarnings("unused")
public class MarketPrice {
    Integer resourceId;
    Float marketPrice;
    CurrentSupplyDemand metadata;

    public MarketPrice() {
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

    public CurrentSupplyDemand getMetadata() {
        return metadata;
    }

    public void setMetadata(CurrentSupplyDemand metadata) {
        this.metadata = metadata;
    }
}
