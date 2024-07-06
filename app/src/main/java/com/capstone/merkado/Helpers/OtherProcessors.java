package com.capstone.merkado.Helpers;

import com.capstone.merkado.Objects.StoresDataObjects.PlayerMarkets;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceDisplayMode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OtherProcessors {
    public static class StoreProcessors {

        public static List<PlayerMarkets.OnSale> filterSaleList(List<PlayerMarkets.OnSale> onSaleList, ResourceDisplayMode resourceDisplayMode) {
            if (onSaleList == null) return new ArrayList<>();
            switch (resourceDisplayMode) {
                case COLLECTIBLES:
                    return onSaleList.stream()
                            .filter(onSale -> "COLLECTIBLE".equalsIgnoreCase(onSale.getType()))
                            .collect(Collectors.toList());
                case EDIBLES:
                    return onSaleList.stream()
                            .filter(onSale -> "EDIBLE".equalsIgnoreCase(onSale.getType()))
                            .collect(Collectors.toList());
                case RESOURCES:
                    return onSaleList.stream()
                            .filter(onSale -> "RESOURCE".equalsIgnoreCase(onSale.getType()))
                            .collect(Collectors.toList());
                default:
                    return new ArrayList<>();
            }
        }
    }
}
