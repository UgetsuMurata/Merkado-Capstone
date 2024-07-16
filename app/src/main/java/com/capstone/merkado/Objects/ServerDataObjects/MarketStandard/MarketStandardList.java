package com.capstone.merkado.Objects.ServerDataObjects.MarketStandard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.capstone.merkado.Objects.FactoryDataObjects.PlayerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Predicate;

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
