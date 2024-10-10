package com.capstone.merkado.Objects.ServerDataObjects.MarketStandard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MarketStandardList{
    List<MarketStandard> marketStandardList;

    public MarketStandardList() {
        marketStandardList = new ArrayList<>();
    }

    public List<MarketStandard> getRaw() {
        return marketStandardList;
    }

    @Nullable
    public Float getMarketPrice(int resId) {
        Optional<MarketStandard> ms = marketStandardList.stream()
                .filter(marketStandard -> marketStandard.getResourceId() == resId)
                .findFirst();
        return ms.map(MarketStandard::getMarketPrice).orElse(null);
    }

    public void add(@NonNull MarketStandard marketStandard) {
        marketStandardList.add(marketStandard);
    }

}
