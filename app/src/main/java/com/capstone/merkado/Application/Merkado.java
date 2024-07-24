package com.capstone.merkado.Application;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatDelegate;

import com.capstone.merkado.Broadcast.NetworkChangeReceiver;
import com.capstone.merkado.DataManager.DataFunctionPackage.DataFunctions.PlayerDataUpdates;
import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.Objects.FactoryDataObjects.FactoryData;
import com.capstone.merkado.Objects.PlayerDataObjects.Player;
import com.capstone.merkado.Objects.ResourceDataObjects.Inventory;
import com.capstone.merkado.Objects.ServerDataObjects.BasicServerData;
import com.capstone.merkado.Objects.StoryDataObjects.PlayerStory;
import com.capstone.merkado.Objects.TaskDataObjects.PlayerTask;

import java.util.List;

@SuppressWarnings("unused")
public class Merkado extends Application {

    private static Merkado instance;
    private Account account;
    private StaticContents staticContents;
    private List<BasicServerData> basicServerData;
    private Player player;
    private Integer playerId;
    private MediaPlayer sfxPlayer;
    private MediaPlayer bgmPlayer;
    private PlayerDataUpdates playerDataUpdates;
    private PlayerDataListener playerDataListener;
    private PlayerDataFunctions playerDataFunctions;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Create the receiver object and register it.
        NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);

        // initialize variables
        staticContents = new StaticContents();
        playerDataFunctions = new PlayerDataFunctions();
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
        setBasicServerList(null);
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
    public List<BasicServerData> getEconomyBasicList() {
        return basicServerData;
    }

    /**
     * Set the list of economies to be shown in the lobby.
     *
     * @param basicServerData List of EconomyBasic.
     */
    public void setBasicServerList(List<BasicServerData> basicServerData) {
        this.basicServerData = basicServerData;
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
        if (playerDataUpdates != null) {
            playerDataUpdates.stopListener();
            playerDataUpdates = null;
        }
        if (playerId == null) return;
        playerDataUpdates = new PlayerDataUpdates(playerId);
        playerDataUpdates.startListener(this::changePlayer);
    }

    private void changePlayer(Player player) {
        this.player = player;
        this.playerDataFunctions.updatePlayer(player);
        if (playerDataListener != null)
            playerDataListener.onPlayerDataListenerReceived(this.player);
    }

    public void setPlayerDataListener(PlayerDataListener playerDataListener) {
        this.playerDataListener = playerDataListener;
    }

    public PlayerDataFunctions getPlayerData() {
        return playerDataFunctions;
    }

    @SuppressWarnings("unused")
    public static class PlayerDataFunctions {
        private PlayerDataListener<Long> playerExpListener;
        private PlayerDataListener<Float> playerMoneyListener;
        private PlayerDataListener<List<Inventory>> playerInventoryListener;
        private PlayerDataListener<List<PlayerTask>> playerTaskListener;
        private PlayerDataListener<List<PlayerStory>> playerStoryListener;
        private PlayerDataListener<FactoryData> playerFactoryListener;
        private Player player;

        public PlayerDataFunctions() {
        }

        public void updatePlayer(Player player) {
            this.player = player;
            if (this.playerMoneyListener != null)
                playerMoneyListener.update(player.getMoney());
            if (this.playerExpListener != null)
                playerExpListener.update(player.getExp());
            if (this.playerInventoryListener != null)
                playerInventoryListener.update(player.getInventory());
            if (this.playerTaskListener != null)
                playerTaskListener.update(player.getPlayerTaskList());
            if (this.playerStoryListener != null)
                playerStoryListener.update(player.getPlayerStoryList());
            if (this.playerFactoryListener != null)
                playerFactoryListener.update(player.getFactory());
        }

        public void setPlayerMoneyListener(PlayerDataListener<Float> playerMoneyListener) {
            this.playerMoneyListener = playerMoneyListener;
        }

        public void setPlayerExpListener(PlayerDataListener<Long> playerExpListener) {
            this.playerExpListener = playerExpListener;
        }

        public void setPlayerInventoryListener(PlayerDataListener<List<Inventory>> playerInventoryListener) {
            this.playerInventoryListener = playerInventoryListener;
        }

        public void setPlayerTaskListener(PlayerDataListener<List<PlayerTask>> playerTaskListener) {
            this.playerTaskListener = playerTaskListener;
        }

        public void setPlayerStoryListener(PlayerDataListener<List<PlayerStory>> playerStoryListener) {
            this.playerStoryListener = playerStoryListener;
        }

        public void setPlayerFactoryListener(PlayerDataListener<FactoryData> playerFactoryListener) {
            this.playerFactoryListener = playerFactoryListener;
        }

        public Long getPlayerExp() {
            return player.getExp();
        }

        public Float getPlayerMoney() {
            return player.getMoney();
        }

        public List<Inventory> getPlayerInventory() {
            return player.getInventory();
        }

        public List<PlayerTask> getPlayerTask() {
            return player.getPlayerTaskList();
        }

        public List<PlayerStory> getPlayerStory() {
            return player.getPlayerStoryList();
        }

        public FactoryData getPlayerFactory() {
            return player.getFactory();
        }

        public interface PlayerDataListener<T> {
            void update(T t);
        }
    }

    public void setBGM(Context context, int file, boolean loop) {
        if (bgmPlayer != null) {
            releaseBGM();
        }
        if (file == -1) {
            if (bgmPlayer != null) {
                pauseBGM();
                releaseBGM();
            }
            return;
        }
        bgmPlayer = MediaPlayer.create(context, file);
        bgmPlayer.setLooping(loop);
        bgmPlayer.start();
    }

    public void pauseBGM() {
        if (bgmPlayer == null || !bgmPlayer.isPlaying()) return;
        bgmPlayer.pause();
    }

    public void resumeBGM() {
        if (bgmPlayer == null) return;
        bgmPlayer.start();
    }

    public void releaseBGM() {
        if (bgmPlayer == null) return;
        bgmPlayer.release();
        bgmPlayer = null;
    }

    public void setSFX(Context context, int file) {
        if (sfxPlayer != null) releaseSFX();
        sfxPlayer = MediaPlayer.create(context, file);
        sfxPlayer.start();
        sfxPlayer.setOnCompletionListener(mp -> releaseSFX());
    }

    public void pauseSFX() {
        if (sfxPlayer == null || !sfxPlayer.isPlaying()) return;
        sfxPlayer.pause();
    }

    public void resumeSFX() {
        if (sfxPlayer == null) return;
        sfxPlayer.start();
    }

    public void releaseSFX() {
        if (sfxPlayer == null) return;
        sfxPlayer.release();
        sfxPlayer = null;
    }

    public void pauseAllPlayer() {
        if (sfxPlayer != null && sfxPlayer.isPlaying()) {
            sfxPlayer.pause();
        }
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.pause();
        }
    }

    public void resumeAllPlayers() {
        if (sfxPlayer != null) {
            sfxPlayer.start();
        }
        if (bgmPlayer != null) {
            bgmPlayer.start();
        }
    }

    public void releaseAllPlayers() {
        if (sfxPlayer != null) {
            sfxPlayer.release();
            sfxPlayer = null;
        }
        if (bgmPlayer != null) {
            bgmPlayer.release();
            bgmPlayer = null;
        }
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

    public interface PlayerDataListener {
        void onPlayerDataListenerReceived(Player player);
    }
}
