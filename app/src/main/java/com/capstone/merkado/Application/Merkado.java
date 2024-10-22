package com.capstone.merkado.Application;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.capstone.merkado.Broadcast.NetworkChangeReceiver;
import com.capstone.merkado.DataManager.DataFunctionPackage.PlayerDataFunctions.PlayerDataUpdates;
import com.capstone.merkado.DataManager.DataFunctionPackage.PlayerDataFunctions.PlayerListListener;
import com.capstone.merkado.DataManager.DataFunctionPackage.ServerDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.ServerDataFunctions.UpdaterPlayerListener;
import com.capstone.merkado.DataManager.DataFunctionPackage.StoreDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.UtilityDataFunctions;
import com.capstone.merkado.Helpers.Bot;
import com.capstone.merkado.Helpers.JsonHelper;
import com.capstone.merkado.Helpers.PlayerActions;
import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.Objects.FactoryDataObjects.FactoryData;
import com.capstone.merkado.Objects.PlayerDataObjects.Player;
import com.capstone.merkado.Objects.ResourceDataObjects.Inventory;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceData;
import com.capstone.merkado.Objects.ServerDataObjects.BasicServerData;
import com.capstone.merkado.Objects.ServerDataObjects.Objectives;
import com.capstone.merkado.Objects.StoresDataObjects.Market;
import com.capstone.merkado.Objects.StoresDataObjects.MarketData.CompiledData;
import com.capstone.merkado.Objects.StoryDataObjects.Chapter;
import com.capstone.merkado.Objects.StoryDataObjects.PlayerStory;
import com.capstone.merkado.Objects.TaskDataObjects.PlayerTask;
import com.capstone.merkado.Objects.TaskDataObjects.TaskData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class Merkado extends Application implements Application.ActivityLifecycleCallbacks {

    private static Merkado instance;
    private Account account;
    private StaticContents staticContents;
    private List<BasicServerData> basicServerData;
    private List<ResourceData> resourceDataList;
    private List<Chapter> chapterList;
    private List<Objectives> objectivesList;
    private Player player;
    private Integer playerId;
    private MediaPlayer sfxPlayer;
    private MediaPlayer bgmPlayer;
    private PlayerDataUpdates playerDataUpdates;
    private PlayerDataListener playerDataListener;
    private AccountDataFunctions accountDataFunctions;
    private PlayerDataFunctions playerDataFunctions;
    private String currentServer;
    private BasicServerData currentServerData;
    private Long serverTimeOffset;
    private Boolean updaterPlayer;
    private UpdaterPlayerListener updaterPlayerListener;
    private Handler serverDataUpdateHandler;
    private Runnable serverDataUpdateRunnable;
    private CompiledData compiledMarketData;
    private Activity currentActivity;
    private List<TaskData> taskDataList;
    private List<PlayerTask> taskPlayerList;

    private Boolean hasTakenPretest = false;
    private Boolean hasTakenPostTest = false;

    private Map<Bot.BotType, Boolean> hasBotMap;
    private PlayerActions.Task playerActionTask;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Create the receiver object and register it.
        NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);

        registerActivityLifecycleCallbacks(this);

        // initialize variables
        staticContents = new StaticContents();
        playerDataFunctions = new PlayerDataFunctions();

        serverDataUpdateHandler = new Handler();

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> handleUncaughtException(e));
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

    public Long currentTimeMillis() {
        return System.currentTimeMillis() + serverTimeOffset;
    }

    public void setServerTimeOffset(Long serverTimeOffset) {
        this.serverTimeOffset = serverTimeOffset;
    }

    public Long getServerTimeOffset() {
        return serverTimeOffset;
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
        if (account != null) {
            accountDataFunctions = new AccountDataFunctions(account.getEmail());
            setBasicServerList(new ArrayList<>());
        } else {
            if (accountDataFunctions != null) accountDataFunctions.stop();
            accountDataFunctions = null;
        }
    }

    /**
     * This method ensures that all saved data from the account is deleted.
     */
    public void signOutAccount() {
        setAccount(null);
        setPlayer(null, null);
        setServer(null);
        setBasicServerList(null);
    }

    public void loadJSONResources(Context context) {
        CountDownLatch latch = new CountDownLatch(3);
        JsonHelper.getResourceList(context, resourceData -> {
            this.resourceDataList = resourceData;
            latch.countDown();
        });
        JsonHelper.getStoryList(context, chapterList -> {
            this.chapterList = chapterList;
            latch.countDown();
        });
        JsonHelper.getAppData(context, staticContents -> {
            this.staticContents = staticContents;
            latch.countDown();
        });

        try {
            boolean completed = latch.await(5000, TimeUnit.MILLISECONDS);
            if (!completed) {
                Log.w("loadJSONResources", "Loading JSON Resources timed out.");
            }
        } catch (InterruptedException e) {
            Log.e("loadJSONResources",
                    String.format("Error encountered while waiting for JSON Resources: %s", e));
        }
    }

    public List<ResourceData> getResourceDataList() {
        return resourceDataList;
    }

    public List<Chapter> getChapterList() {
        return chapterList;
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

    public Boolean getHasTakenPretest() {
        return hasTakenPretest;
    }

    public void setHasTakenPretest(Boolean hasTakenPretest) {
        this.hasTakenPretest = hasTakenPretest;
    }

    public Boolean getHasTakenPostTest() {
        return hasTakenPostTest;
    }

    public void setHasTakenPostTest(Boolean hasTakenPostTest) {
        this.hasTakenPostTest = hasTakenPostTest;
    }

    public Player getPlayer() {
        return player;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayer(Player player, Integer playerId) {
        if (player == null) logOutToServer();
        else logInToServer();
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

    public void setServer(@Nullable BasicServerData serverData) {
        if (serverData == null) {
            logOutToServer();
            this.currentServer = null;
        } else {
            this.currentServer = serverData.getId();
            this.currentServerData = serverData;
            logInToServer();
        }
    }

    private void logInToServer() {
        if (this.playerId != null && this.currentServer != null) {
            ServerDataFunctions.logInToServer(this.playerId, this.currentServer);
            setUpdaterPlayerListener();
        }
    }

    public String getServerName() {
        if (this.currentServer != null) return this.currentServerData.getName();
        return "";
    }

    public void logOutToServer() {
        serverDataUpdateHandler.removeCallbacks(serverDataUpdateRunnable);
        ServerDataFunctions.logOutUpdaterPlayer(this.currentServer, this.playerId);
        ServerDataFunctions.logOutFromServer(this.playerId, this.currentServer);
        this.playerId = null;
        this.currentServer = null;
    }

    private void setUpdaterPlayerListener() {
        updaterPlayerListener = new UpdaterPlayerListener(this.currentServer);
        updaterPlayerListener.getInitialData().thenAccept(updaterPlayer -> {
            if (updaterPlayer == null || updaterPlayer.equals(this.playerId)) {
                updaterPlayerListener.setUpdaterPlayer(this.playerId);
                setUpdaterPlayer(true);
                serverDataUpdateRunnable = this::serverDataUpdateTime;
                serverDataUpdateTime();
            } else {
                setUpdaterPlayer(false);
                updaterPlayerListener.start(newUpdaterPlayer -> {
                    if (newUpdaterPlayer == null) {
                        updaterPlayerListener.setUpdaterPlayer(this.playerId);
                        setUpdaterPlayer(true);
                        updaterPlayerListener.end();
                    } else if (newUpdaterPlayer.equals(this.playerId)) {
                        setUpdaterPlayer(true);
                        updaterPlayerListener.end();
                    } else setUpdaterPlayer(false);
                });
                serverDataUpdateRunnable = this::getUpdatedCompiledMarketData;
            }
            getUpdatedCompiledMarketData();
        });
    }

    private void serverDataUpdateTime() {
        if (updaterPlayer == null || !updaterPlayer) return;
        StoreDataFunctions.marketDataTimeCheck(this.currentServer, 0.1f).thenAccept(currentHourTime -> {
            long timeUntilNextUpdate = currentHourTime + TimeUnit.HOURS.toMillis(1) - currentTimeMillis();
            timeUntilNextUpdate = Math.max(timeUntilNextUpdate, 0);
            serverDataUpdateHandler.removeCallbacks(serverDataUpdateRunnable);
            serverDataUpdateHandler.postDelayed(serverDataUpdateRunnable, timeUntilNextUpdate);
            getUpdatedCompiledMarketData();
        }).exceptionally(ex -> {
            Log.e("serverDataUpdateTime", String.format("%s", ex.getMessage()));
            return null;
        });
        StoreDataFunctions.getCompiledMarketData(this.currentServer)
                .thenAccept(this::setCompiledMarketData);

        if (this.hasBotMap != null) {
            if (Boolean.TRUE.equals(this.hasBotMap.get(Bot.BotType.STORE)))
                Bot.Store.checkToRestock(this.currentServer);
            if (Boolean.TRUE.equals(this.hasBotMap.get(Bot.BotType.FACTORY)))
                Bot.Factory.checkToRestock(this.currentServer);
        } else {
            getHasBotMapFuture().thenAccept(hasBotMap -> {
                if (Boolean.TRUE.equals(hasBotMap.get(Bot.BotType.STORE)))
                    Bot.Store.checkToRestock(this.currentServer);
                if (Boolean.TRUE.equals(hasBotMap.get(Bot.BotType.FACTORY)))
                    Bot.Factory.checkToRestock(this.currentServer);
            });
        }
    }

    private void getUpdatedCompiledMarketData() {
        StoreDataFunctions.getCompiledMarketData(this.currentServer)
                .thenAccept(this::setCompiledMarketData);
        StoreDataFunctions.getMarketDataUpdateTime(this.currentServer).thenAccept(updateHourTime -> {
            long timeUntilNextUpdate = updateHourTime + TimeUnit.HOURS.toMillis(1) - currentTimeMillis();
            timeUntilNextUpdate = Math.max(timeUntilNextUpdate, 1000);
            serverDataUpdateHandler.removeCallbacks(serverDataUpdateRunnable);
            serverDataUpdateHandler.postDelayed(serverDataUpdateRunnable, timeUntilNextUpdate);
            getUpdatedCompiledMarketData();
        }).exceptionally(ex -> {
            Log.e("getUpdatedCompiledMarketData", String.format("%s", ex.getMessage()));
            return null;
        });
    }

    public CompiledData getCompiledMarketData() {
        return compiledMarketData;
    }

    public void setCompiledMarketData(CompiledData compiledMarketData) {
        this.compiledMarketData = compiledMarketData;
    }

    public Boolean getUpdaterPlayer() {
        return updaterPlayer;
    }

    public void setUpdaterPlayer(Boolean updaterPlayer) {
        this.updaterPlayer = updaterPlayer;
    }

    public PlayerDataFunctions getPlayerData() {
        return playerDataFunctions;
    }

    public AccountDataFunctions getAccountDataFunctions() {
        return accountDataFunctions;
    }

    @SuppressWarnings("unused")
    public static class PlayerDataFunctions {
        private PlayerDataListener<Long> playerExpListener;
        private PlayerDataListener<Float> playerMoneyListener;
        private PlayerDataListener<List<Inventory>> playerInventoryListener;
        private PlayerDataListener<List<PlayerTask>> playerTaskListener;
        private PlayerDataListener<List<PlayerStory>> playerStoryListener;
        private PlayerDataListener<List<PlayerStory>> playerStoryHistoryListener;
        private PlayerDataListener<Market> marketIdListener;
        private PlayerDataListener<FactoryData> playerFactoryListener;
        private PlayerDataListener<Objectives> objectiveListener;
        private Player player;

        public PlayerDataFunctions() {
            this.player = new Player();
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
            if (this.playerStoryHistoryListener != null)
                playerStoryHistoryListener.update(player.getStoryHistory());
            if (this.marketIdListener != null)
                marketIdListener.update(player.getMarket());
            if (this.playerFactoryListener != null)
                playerFactoryListener.update(player.getFactory());
            if (this.objectiveListener != null)
                objectiveListener.update(Merkado.getInstance().getObjectivesList().get(player.getObjectives().getId()));
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

        public void setPlayerStoryHistoryListener(PlayerDataListener<List<PlayerStory>> playerStoryHistoryListener) {
            this.playerStoryHistoryListener = playerStoryHistoryListener;
        }

        public void setPlayerMarketIdListener(PlayerDataListener<Market> marketIdListener) {
            this.marketIdListener = marketIdListener;
        }

        public void setPlayerFactoryListener(PlayerDataListener<FactoryData> playerFactoryListener) {
            this.playerFactoryListener = playerFactoryListener;
        }

        public void setObjectiveListener(PlayerDataListener<Objectives> objectiveListener) {
            this.objectiveListener = objectiveListener;
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

        public List<PlayerStory> getStoryHistory() {
            return player.getStoryHistory();
        }

        public FactoryData getPlayerFactory() {
            return player.getFactory();
        }

        public Market getPlayerMarket() {
            return player.getMarket();
        }

        public Objectives getObjectives() {
            return Merkado.getInstance()
                    .getObjectivesList().get(player.getObjectives().getId());
        }

        public @Nullable Objectives.Objective getCurrentObjective() {
            Objectives objectives = Merkado.getInstance()
                    .getObjectivesList().get(player.getObjectives().getId());
            if (objectives.getObjectives().size() <= player.getObjectives().getCurrentObjectiveId())
                return null;
            return Merkado.getInstance()
                    .getObjectivesList().get(player.getObjectives().getId())
                    .getObjectives().get(player.getObjectives().getCurrentObjectiveId());
        }

        public Boolean getObjectiveDone() {
            return player.getObjectives() != null && player.getObjectives().getDone();
        }

        public Boolean hasStore() {
            return player.getMarket() != null && player.getMarket().getId() != null;
        }

        public Boolean hasFactory() {
            return player.getFactory() != null && player.getFactory().getFactoryMarketId() != null;
        }

        public interface PlayerDataListener<T> {
            void update(T t);
        }
    }

    public static class AccountDataFunctions {

        private ServerBasicDataListener serverBasicDataListener;
        private final Merkado merkado;
        private final PlayerListListener playerListListener;

        public AccountDataFunctions(String email) {
            merkado = Merkado.getInstance();
            playerListListener = new PlayerListListener(email);
            playerListListener.start(basicServerDataList -> {
                merkado.setBasicServerList(basicServerDataList);
                if (serverBasicDataListener != null)
                    serverBasicDataListener.onUpdate(basicServerDataList);
            });
        }

        public void setServerBasicDataListener(ServerBasicDataListener serverBasicDataListener) {
            this.serverBasicDataListener = serverBasicDataListener;
        }

        public void stop() {
            playerListListener.stop();
        }

        public interface ServerBasicDataListener {
            void onUpdate(List<BasicServerData> basicServerDataList);
        }
    }

    public void extractObjectives(Context context) {
        JsonHelper.getObjectivesList(context, this::setObjectivesList);
    }

    public void setObjectivesList(List<Objectives> objectivesList) {
        this.objectivesList = objectivesList;
    }

    public List<Objectives> getObjectivesList() {
        return objectivesList;
    }

    public @Nullable Map<Bot.BotType, Boolean> getHasBotMap() {
        return hasBotMap;
    }

    public CompletableFuture<Map<Bot.BotType, Boolean>> getHasBotMapFuture() {
        return Bot.getBotAvailability(this.currentServer).thenCompose(botTypeBooleanMap -> {
            setHasBotMap(botTypeBooleanMap);
            return CompletableFuture.completedFuture(botTypeBooleanMap);
        });
    }

    public List<TaskData> getTaskDataList() {
        return taskDataList;
    }

    public void setTaskDataList(List<TaskData> taskDataList) {
        this.taskDataList = taskDataList;
    }

    public List<PlayerTask> getTaskPlayerList() {
        return taskPlayerList;
    }

    public void setTaskPlayerList(List<PlayerTask> taskPlayerList) {
        this.taskPlayerList = taskPlayerList;
        setPlayerActionTask(new PlayerActions.Task());
    }

    public PlayerActions.Task getPlayerActionTask() {
        return playerActionTask;
    }

    public void setPlayerActionTask(PlayerActions.Task playerActionTask) {
        this.playerActionTask = playerActionTask;
    }

    public void setHasBotMap(Map<Bot.BotType, Boolean> hasBotMap) {
        this.hasBotMap = hasBotMap;
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
        bgmPlayer.setVolume(1f, 1f);
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

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;
        resumeBGM();
        resumeSFX();
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        currentActivity = null;
        pauseBGM();
        pauseSFX();
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    private void handleUncaughtException(Throwable e) {
        // Log and report the error
        String errorLocation = getAppErrorLocation(e);
        UtilityDataFunctions.reportError(getApplicationContext(), e.getMessage(), errorLocation, String.valueOf(System.currentTimeMillis()));
        logOutToServer();

        // Show a toast on the UI thread (using a Handler)
        new Handler(Looper.getMainLooper()).post(() -> {
            Activity currentActivity = getCurrentActivity();
            if (currentActivity != null) {
                Toast.makeText(currentActivity, "An error has occurred and is sent to the developers.", Toast.LENGTH_SHORT).show();
            }
        });

        // Allow some time for the toast to display
        try {
            Thread.sleep(1000);  // Pause briefly to show the toast
        } catch (InterruptedException interruptedException) {
            // Handle interruption
        }

        // Finish the current activity if available
        Activity currentActivity = getCurrentActivity();
        if (currentActivity != null) {
            currentActivity.finishAndRemoveTask();
        }

        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }

        // Kill the process after finishing all activities
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private String getAppErrorLocation(Throwable e) {
        String packageName = "com.capstone.merkado";  // Your app's package name
        StringBuilder errorLocation = new StringBuilder();

        // Get the stack trace elements
        StackTraceElement[] stackTrace = e.getStackTrace();

        // Loop through the stack trace elements
        for (StackTraceElement element : stackTrace) {
            // Check if the element comes from your app's package
            if (element.getClassName().startsWith(packageName)) {
                // Found an element from your app, format the error location
                errorLocation.append("Exception in class: ").append(element.getClassName())
                        .append(", method: ").append(element.getMethodName())
                        .append(", file: ").append(element.getFileName())
                        .append(", line: ").append(element.getLineNumber());
                break;  // Stop after finding the first occurrence from your app
            }
        }

        return errorLocation.toString();
    }

    @Override
    public void onTerminate() {
        logOutToServer();
        super.onTerminate();
    }
}
