package com.capstone.merkado.Screens.LoadingScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctionPackage.DataFunctions;
import com.capstone.merkado.Objects.PlayerDataObjects.Player;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor1;
import com.capstone.merkado.Objects.StoryDataObjects.PlayerStory;
import com.capstone.merkado.Objects.StoryDataObjects.Chapter;
import com.capstone.merkado.Objects.TaskDataObjects.PlayerTask;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.Game.MainMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerLoadingScreen extends AppCompatActivity {

    private Merkado merkado;
    Integer maxProcesses = 5;
    String serverTitle;
    Integer serverId, playerId;
    PlayerFBExtractor1 playerFBExtractor;
    List<PlayerStory> playerStoryList;
    List<PlayerTask> playerTaskList;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loa_server_loading_screen);

        // initialize this activity's screen.
        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        // get intent data
        serverTitle = getIntent().getStringExtra("TITLE");
        serverId = getIntent().getIntExtra("ID", -1);
        playerId = getIntent().getIntExtra("PLAYER_ID", -1);

        // check if playerId and serverId exists. If not, then finish this loading screen.
        if (serverId == -1 || playerId == -1) {
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
                        if (playerFBExtractor == null) {
                            Toast.makeText(getApplicationContext(), "Cannot find player data. Try again later.", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
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
        playerFBExtractor = DataFunctions.getPlayerDataFromId(playerId);
        if (playerFBExtractor != null) {
            merkado.setPlayer(new Player(playerFBExtractor), playerId);
        }
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
                Chapter chapter = DataFunctions.getChapterFromId(storyQueue.getChapter());
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
                playerStory.setCurrentLineGroup(DataFunctions.getLineGroupFromId(Long.valueOf(storyQueue.getChapter()), Long.valueOf(storyQueue.getCurrentScene()), storyQueue.getCurrentLineGroup()));
                playerStory.setNextLineGroup(DataFunctions.getLineGroupFromId(Long.valueOf(storyQueue.getChapter()), Long.valueOf(storyQueue.getCurrentScene()), storyQueue.getNextLineGroup()));
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
                playerTask.setTask(DataFunctions.getTaskFromId(taskQueue.getTask()));
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

}