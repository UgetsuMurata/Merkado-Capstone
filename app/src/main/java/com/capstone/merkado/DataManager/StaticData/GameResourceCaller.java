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
}
