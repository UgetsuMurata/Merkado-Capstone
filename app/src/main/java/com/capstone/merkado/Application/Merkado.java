package com.capstone.merkado.Application;

import static android.content.pm.ActivityInfo.*;

import android.app.Activity;
import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.Broadcast.NetworkChangeReceiver;

public class Merkado extends Application {

    private static Merkado instance;
    private NetworkChangeReceiver networkChangeReceiver;
    private Account account;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Create the receiver object and register it.
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);
    }
    /**
     * Get the working instance of the Application class.
     * @return Merkado
     */
    public static Merkado getInstance() {
        return instance;
    }

    /**
     * Prepares the screen for the activity.
     * @param activity Activity context.
     */
    public void initializeScreen(Activity activity) {
        // force the screen's orientation landscape.
        activity.setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * Get current user's account details.
     * @return User's account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Set current user's account details
     * @param account User's account
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
