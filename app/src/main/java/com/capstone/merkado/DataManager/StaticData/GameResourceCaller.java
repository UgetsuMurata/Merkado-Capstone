package com.capstone.merkado.DataManager.StaticData;

import com.capstone.merkado.R;

public class GameResourceCaller {
    public static int getResourcesImage(long resourceId) {
        if (resourceId == 0){
            return R.drawable.resource_0_joining_badge;
        } else if (resourceId == 1) {
            return R.drawable.resource_1_merkado_experience;
        }
        return -1;
    }

    public static int getStoreIcons(String storeIconCode) {
        return R.drawable.icon_store;
    }

    public static int getResourceTypeIcons(String type) {
        switch (type) {
            case "COLLECTIBLES":
                return R.drawable.icon_collectibles;
            case "EDIBLES":
                return R.drawable.icon_edibles;
            case "RESOURCES":
                return R.drawable.icon_resources;
            default:
                return -1;
        }
    }
}
