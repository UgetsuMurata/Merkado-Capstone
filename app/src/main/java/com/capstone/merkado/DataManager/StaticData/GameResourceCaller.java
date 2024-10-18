package com.capstone.merkado.DataManager.StaticData;

import com.capstone.merkado.R;

public class GameResourceCaller {
    public static int getResourcesImage(long resourceId) {
        switch ((int) resourceId) {
            case 0: return R.drawable.resource_0_joining_badge;
            case 1: return R.drawable.resource_1_merkado_experience;
            case 2: return R.drawable.resource_2_kutsinta;
            case 3: return R.drawable.resource_3_taho;
            case 4: return R.drawable.resource_4_turon_sample;
            case 5: return R.drawable.resource_5_balut;
            case 6: return R.drawable.resource_6_lumpia;
            case 7: return R.drawable.resource_7_halo_halo;
            case 8: return R.drawable.resource_8_bibingka;
            case 9: return R.drawable.resource_9_pancit;
            case 10: return R.drawable.resource_10_sinigang;
            case 11: return R.drawable.resource_11_adobo;
            case 12: return R.drawable.resource_12_kare_kare;
            case 13: return R.drawable.resource_13_lechon;
            case 14: return R.drawable.resource_14_grain;
            case 15: return R.drawable.resource_15_coal;
            case 16: return R.drawable.resource_16_timber;
            case 17: return R.drawable.resource_17_oil_barrel;
            case 18: return R.drawable.resource_18_toolbox;
            case 19: return R.drawable.resource_19_metal_scraps_sample;
            case 20: return R.drawable.resource_20_iron_ingot;
            case 21: return R.drawable.resource_21_silk;
            case 22: return R.drawable.resource_22_gold_ore;
            case 23: return R.drawable.resource_23_gems;
            default: return -1;
        }
    }

    @SuppressWarnings("unused")
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

    public static int getResourceTypeIcons(String type) {
        switch (type.toUpperCase()) {
            case "COLLECTIBLE":
                return R.drawable.icon_collectibles;
            case "EDIBLE":
                return R.drawable.icon_edibles;
            case "RESOURCE":
                return R.drawable.icon_resources;
            default:
                return -1;
        }
    }

    public static int getServerImage(Integer image) {
        switch (image) {
            case 1:
                return R.drawable.icon_default_pfp1;
            case 2:
                return R.drawable.icon_default_pfp2;
            case 3:
                return R.drawable.icon_default_pfp3;
            case 4:
                return R.drawable.icon_default_pfp4;
        }
        return -1;
    }
}
