package com.capstone.merkado.DataManager.StaticData;

import com.capstone.merkado.R;

public class BackgroundCaller {

    /**
     * Retrieves background.
     * @param background string from Firebase.
     * @return drawable resource id of the background. returns <b>-1</b> if nothing is retrieved.
     */
    public static int retrieveBackground(String background){
        if (background.equals("HOUSE")) {
            return Background.HOUSE;
        }

        return -1;
    }

    private interface Background{
        int HOUSE = R.drawable.bg_acc_sign_in;
    }
}
