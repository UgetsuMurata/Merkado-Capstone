package com.capstone.merkado.Helpers;

import com.capstone.merkado.Objects.ResourceDataObjects.Inventory;
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
                            .filter(onSale -> {
                                if (onSale == null) return false;
                                return "COLLECTIBLE".equalsIgnoreCase(onSale.getType());
                            })
                            .collect(Collectors.toList());
                case EDIBLES:
                    return onSaleList.stream()
                            .filter(onSale -> {
                                if (onSale == null) return false;
                                return "EDIBLE".equalsIgnoreCase(onSale.getType());
                            })
                            .collect(Collectors.toList());
                case RESOURCES:
                    return onSaleList.stream()
                            .filter(onSale -> {
                                if (onSale == null) return false;
                                return "RESOURCE".equalsIgnoreCase(onSale.getType());
                            })
                            .collect(Collectors.toList());
                default:
                    return new ArrayList<>();
            }
        }
    }

    public static class InventoryProcessors {
        public static Boolean isInventoryDisabled(Inventory inventory, Disable disable) {
            switch (disable) {
                case SELLABLE:
                    return inventory.getSellable();
                case UNSELLABLE:
                    return !inventory.getSellable();
                case NONE:
                default:
                    return false;
            }
        }

        public enum Disable {
            NONE, SELLABLE, UNSELLABLE
        }
    }
}
