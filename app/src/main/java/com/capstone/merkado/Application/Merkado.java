package com.capstone.merkado.Application;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

import android.app.Activity;
import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatDelegate;

import com.capstone.merkado.Broadcast.NetworkChangeReceiver;
import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.Objects.PlayerDataObjects.Player;
import com.capstone.merkado.Objects.ServerDataObjects.EconomyBasic;

import java.util.List;

public class Merkado extends Application {

    private static Merkado instance;
    private NetworkChangeReceiver networkChangeReceiver;
    private Account account;
    private StaticContents staticContents;
    private List<EconomyBasic> economyBasicList;
    private Player player;
    private Integer playerId;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Create the receiver object and register it.
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);

        // initialize variables
        staticContents = new StaticContents();
    }

    /**
     * Get the working instance of the Application class.
     *
     * @return Merkado
     */
    public static Merkado getInstance() {
        return instance;
    }

    /**
     * Prepares the screen for the activity.
     *
     * @param activity Activity context.
     */
    public void initializeScreen(Activity activity) {
        // force the screen's orientation landscape.
        activity.setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);

        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    /**
     * Get current user's account details.
     *
     * @return User's account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Set current user's account details
     *
     * @param account User's account
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * This method ensures that all saved data from the account is deleted.
     */
    public void signOutAccount() {
        setAccount(null);
        setPlayer(null, null);
        setEconomyBasicList(null);
    }

    /**
     * Getter of StaticContents Instance.
     *
     * @return staticContent from Application class.
     */
    public StaticContents getStaticContents() {
        return staticContents;
    }

    /**
     * Get the list of economies to be shown in the lobby.
     *
     * @return List of EconomyBasic.
     */
    public List<EconomyBasic> getEconomyBasicList() {
        return economyBasicList;
    }

    /**
     * Set the list of economies to be shown in the lobby.
     *
     * @param economyBasicList List of EconomyBasic.
     */
    public void setEconomyBasicList(List<EconomyBasic> economyBasicList) {
        this.economyBasicList = economyBasicList;
    }

    public Player getPlayer() {
        return player;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayer(Player player, Integer playerId) {
        this.player = player;
        this.playerId = playerId;
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
