package com.capstone.merkado.Application;

import static android.content.pm.ActivityInfo.*;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;

public class Merkado extends Application {

    private static Merkado instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Merkado getInstance() {
        return instance;
    }

    public void initializeScreen(Activity activity) {
        // force the screen's orientation landscape.
        activity.setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
