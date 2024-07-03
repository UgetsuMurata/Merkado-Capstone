package com.capstone.merkado.DataManager.StaticData;

import com.capstone.merkado.R;

public class GameResourceCaller {
    public static int getResourcesImage(long resourceId) {
        switch ((int) resourceId) {
            case 0:
                return R.drawable.resource_0_joining_badge;
            case 1:
                return R.drawable.resource_1_merkado_experience;
            case 2:
                return R.drawable.resource_2_turon_sample;
            case 3:
                return R.drawable.resource_3_metal_scraps_sample;
            case 4:
                return R.drawable.resource_4_dog_collar_pendant_sample;
            default:
                return -1;
        }
    }

    public static int getStoreIcons(String storeIconCode) {
        return R.drawable.icon_store;
    }

    public static int getResourceTypeBackgrounds(String type) {
        switch (type.toUpperCase()) {
            case "COLLECTIBLE":
                return R.drawable.resource_collectible_bg;
            case "EDIBLE":
                return R.drawable.resource_edible_bg;
            case "RESOURCE":
                return R.drawable.resource_resource_bg;
            default:
                return -1;
        }
    }
}
