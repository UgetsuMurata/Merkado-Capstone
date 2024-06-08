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
    private StaticContents staticContents;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Create the receiver object and register it.
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);

        // initialize variables
        staticContents = new StaticContents();
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

    /**
     * Getter of StaticContents Instance.
     * @return staticContent from Application class.
     */
    public StaticContents getStaticContents() {
        return staticContents;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    /**
     * Organized way of storing static contents, which includes:
     * <ul>
     *     <li>About</li>
     *     <li>Terms and Conditions</li>
     * </ul>
     */
    public static class StaticContents {
        String about, termsAndConditions;

        public String getAbout() {
            return about;
        }

        public void setAbout(String about) {
            this.about = about;
        }

        public String getTermsAndConditions() {
            return termsAndConditions;
        }

        public void setTermsAndConditions(String termsAndConditions) {
            this.termsAndConditions = termsAndConditions;
        }
    }
}
