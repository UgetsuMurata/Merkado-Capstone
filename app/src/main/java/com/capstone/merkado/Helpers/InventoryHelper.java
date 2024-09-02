package com.capstone.merkado.Helpers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.capstone.merkado.Objects.ResourceDataObjects.Inventory;

import java.util.List;

public class InventoryHelper {

    public static @Nullable Inventory finder(@NonNull List<Inventory> inventoryContents, @NonNull Integer resourceId){
        for (Inventory inventoryItem : inventoryContents) {
            if (resourceId.equals(inventoryItem.getResourceId())) {
                return inventoryItem;
            }
        }
        return null;
    }
}
