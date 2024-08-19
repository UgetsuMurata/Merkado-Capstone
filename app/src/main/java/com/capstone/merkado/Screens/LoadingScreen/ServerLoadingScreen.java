package com.capstone.merkado.Screens.LoadingScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctionPackage.PlayerDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.QASDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.StoryDataFunctions;
import com.capstone.merkado.Objects.PlayerDataObjects.Player;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor1;
import com.capstone.merkado.Objects.ServerDataObjects.BasicServerData;
import com.capstone.merkado.Objects.StoryDataObjects.Chapter;
import com.capstone.merkado.Objects.StoryDataObjects.PlayerStory;
import com.capstone.merkado.Objects.TaskDataObjects.PlayerTask;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.Game.MainMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerLoadingScreen extends AppCompatActivity {

    private Merkado merkado;
    Integer maxProcesses = 5;
    BasicServerData basicServerData;
    PlayerFBExtractor1 playerFBExtractor;
    List<PlayerStory> playerStoryList;
    List<PlayerTask> playerTaskList;
    Intent intent;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loa_server_loading_screen);

        // initialize this activity's screen.
        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        // get intent data
        basicServerData = getIntent().getParcelableExtra("BASIC_SERVER_DATA");

        // check if playerId and serverId exists. If not, then finish this loading screen.
        if (basicServerData == null ||
                basicServerData.getId() == null ||
                basicServerData.getPlayerId() == -1) {
            Toast.makeText(getApplicationContext(), "Problem retrieving server data.", Toast.LENGTH_SHORT).show();
            finish();
        }

        intent = new Intent(getApplicationContext(), MainMap.class);

        new Thread(() -> {
            long startingTime = System.currentTimeMillis();
            AtomicInteger process_number = new AtomicInteger(0);
            for (int i = 0; i < maxProcesses; i++) {
                process_number.getAndIncrement();
                switch (i) {
                    case 1:
                        process1();
                        break;
                    case 2:
                        process2();
                        break;
                    case 3:
                        process3();
                        break;
                    case 4:
                        process4();
                        break;
                    default:
                        break;
                }
            }
            runOnUiThread(() -> new Handler().postDelayed(() -> {
                if (process_number.get() == maxProcesses && playerFBExtractor != null) {
                    // if this is not triggered, then the processes will naturally go to StoryMode.class
                    merkado.setServer(basicServerData);
                    startActivity(intent);
                    finish();
                }
            }, 1000 - System.currentTimeMillis() - startingTime));
        }).start();
    }

    /**
     * Process 1 processes all player details.
     */
    private void process1() {
        CompletableFuture<Void> future = PlayerDataFunctions.getPlayerDataFromId(basicServerData.getPlayerId()).thenAccept(playerFBExtractor1 -> {
            this.playerFBExtractor = playerFBExtractor1;
            if (playerFBExtractor != null) {
                merkado.setPlayer(new Player(playerFBExtractor), basicServerData.getPlayerId());
                if (playerFBExtractor == null) {
                    runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(),
                                    "Cannot find player data. Try again later.",
                                    Toast.LENGTH_SHORT).show());
                    finish();
                }
            }
        });

        // Set a timeout for the CompletableFuture
        scheduler.schedule(() -> {
            if (!future.isDone()) {
                // If the CompletableFuture is not done, trigger the timeout function
                onTimeout();
            }
        }, 5, TimeUnit.SECONDS); // Set your desired timeout here

        // Wait for the CompletableFuture to complete
        future.join();
    }

    /**
     * Process 2 processes all Story queue
     */
    private void process2() {
        if (playerFBExtractor != null) {
            playerStoryList = new ArrayList<>();
            if (playerFBExtractor.getStoryQueue() == null) return;
            for (PlayerFBExtractor1.StoryQueue storyQueue : playerFBExtractor.getStoryQueue()) {
                PlayerStory playerStory = new PlayerStory();
                Chapter chapter = StoryDataFunctions.getChapterFromId(storyQueue.getChapter());
                if (chapter == null) continue;

                playerStory.setChapter(chapter);
                playerStory.setCurrentScene(chapter.getScenes().get(storyQueue.getCurrentScene()));
                if (playerStory.getCurrentScene().getNextScene() != null) {
                    for (Chapter.Scene scene : playerStory.getChapter().getScenes()) {
                        if (Math.toIntExact(scene.getId()) == playerStory.getCurrentScene().getNextScene()) {
                            playerStory.setNextScene(scene);
                            break;
                        }
                    }
                }
                playerStory.setCurrentLineGroup(StoryDataFunctions.getLineGroupFromId(Long.valueOf(storyQueue.getChapter()), Long.valueOf(storyQueue.getCurrentScene()), storyQueue.getCurrentLineGroup()));
                playerStory.setNextLineGroup(StoryDataFunctions.getLineGroupFromId(Long.valueOf(storyQueue.getChapter()), Long.valueOf(storyQueue.getCurrentScene()), storyQueue.getNextLineGroup()));
                playerStoryList.add(playerStory);
            }
            merkado.getPlayer().setPlayerStoryList(playerStoryList);
        }
    }

    /**
     * Process 3 processes Task queue
     */
    private void process3() {
        if (playerFBExtractor != null) {
            playerTaskList = new ArrayList<>();
            if (playerFBExtractor.getTaskQueue() == null) return;
            for (PlayerFBExtractor1.TaskQueue taskQueue : playerFBExtractor.getTaskQueue()) {
                PlayerTask playerTask = new PlayerTask();
                playerTask.setTask(QASDataFunctions.getTaskFromId(taskQueue.getTask()));
                playerTask.setTaskStatusCode(taskQueue.getTaskStatusCode());
                playerTaskList.add(playerTask);
            }
            merkado.getPlayer().setPlayerTaskList(playerTaskList);
        }
    }

    /**
     * Check for prologue.
     */
    private void process4() {
        Integer index = 0;
        if (merkado.getPlayer().getPlayerStoryList() == null) return;
        for (PlayerStory playerStory : merkado.getPlayer().getPlayerStoryList()) {
            if (playerStory.getChapter().getChapter().equals("Prologue")) {
                intent.putExtra("PROLOGUE", true);
                intent.putExtra("PLAYER_STORY", playerStory);
                intent.putExtra("CURRENT_QUEUE_INDEX", index);
            }
            index++;
        }
    }

    private void onTimeout() {
        // This function is triggered if the CompletableFuture does not complete in time
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(),
                        "Connection timeout. Please check your internet connection and try again.",
                        Toast.LENGTH_SHORT).show());
        finish();
    }
}