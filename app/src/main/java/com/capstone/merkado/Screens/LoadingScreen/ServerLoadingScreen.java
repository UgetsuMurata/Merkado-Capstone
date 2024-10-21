package com.capstone.merkado.Screens.LoadingScreen;

import static com.capstone.merkado.DataManager.DataFunctionPackage.QASDataFunctions.getAllQuests;
import static com.capstone.merkado.DataManager.DataFunctionPackage.QASDataFunctions.saveAllQuests;
import static com.capstone.merkado.Helpers.OtherProcessors.TimeProcessors.getCurrentDay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctionPackage.PlayerDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.QASDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.ServerDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.StoryDataFunctions;
import com.capstone.merkado.Helpers.JsonHelper;
import com.capstone.merkado.Helpers.OtherProcessors;
import com.capstone.merkado.Objects.PlayerDataObjects.Player;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor1;
import com.capstone.merkado.Objects.QASDataObjects.QASItems;
import com.capstone.merkado.Objects.ServerDataObjects.BasicServerData;
import com.capstone.merkado.Objects.StoryDataObjects.Chapter;
import com.capstone.merkado.Objects.StoryDataObjects.PlayerStory;
import com.capstone.merkado.Objects.TaskDataObjects.PlayerTask;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.Game.MainMap;
import com.capstone.merkado.Screens.Game.Story.QuizDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("StatementWithEmptyBody")
public class ServerLoadingScreen extends AppCompatActivity {

    private Merkado merkado;
    Integer maxProcesses = 7;
    BasicServerData basicServerData;
    PlayerFBExtractor1 playerFBExtractor;
    List<PlayerStory> playerStoryList;
    Intent intent;

    AtomicInteger process_number;
    long startingTime;

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
            startingTime = System.currentTimeMillis();
            process_number = new AtomicInteger(0);
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
                    case 5:
                        process5();
                        break;
                    case 6:
                        process6();
                    default:
                        break;
                }
            }
        }).start();
    }

    private void doneLoading() {
        runOnUiThread(() -> new Handler().postDelayed(() -> {
            if (process_number.get() == maxProcesses && playerFBExtractor != null) {
                // if this is not triggered, then the processes will naturally go to StoryMode.class
                merkado.setServer(basicServerData);
                startActivity(intent);
                finish();
            }
        }, 1000 - System.currentTimeMillis() - startingTime));
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

    private void process2() {
        long currentTimeMillis = System.currentTimeMillis();
        merkado.extractObjectives(getApplicationContext());
        while (System.currentTimeMillis() - currentTimeMillis < 1000) ;
    }

    private void process3() {
        long currentTimeMillis = System.currentTimeMillis();
        ServerDataFunctions.checkPlayerPretest(basicServerData.getId(),
                basicServerData.getPlayerId(),
                hasTaken -> merkado.setHasTakenPretest(hasTaken != null && hasTaken));
        ServerDataFunctions.checkPlayerPostTest(basicServerData.getId(),
                basicServerData.getPlayerId(),
                hasTaken -> merkado.setHasTakenPostTest(hasTaken != null && hasTaken));
        while (System.currentTimeMillis() - currentTimeMillis < 1000) ;
    }

    /**
     * Process 4 processes all Story queue
     */
    private void process4() {
        if (playerFBExtractor != null) {
            playerStoryList = new ArrayList<>();
            if (playerFBExtractor.getStoryQueue() == null) return;
            for (PlayerFBExtractor1.StoryQueue storyQueue : playerFBExtractor.getStoryQueue()) {
                if (storyQueue == null) continue;
                PlayerStory playerStory = new PlayerStory();
                Chapter chapter = StoryDataFunctions.getChapterFromId(storyQueue.getChapter());
                if (chapter == null) continue;

                playerStory.setChapter(chapter);
                playerStory.setTrigger(chapter.getTriggers());
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
     * Process 5 processes Task queue
     */
    private void process5() {
        if (playerFBExtractor != null) {
            JsonHelper.getTaskList(getApplicationContext(), taskDataList -> {
                Merkado.getInstance().setTaskDataList(taskDataList);

                // CHECK IF PLAYER TASKS QUEUE IS ALREADY GENERATED FOR TODAY.
                CompletableFuture<Boolean> hasGenerated = QASDataFunctions.getTaskLastUpdate(
                        basicServerData.getPlayerId()).thenCompose(timestamp ->
                        CompletableFuture.completedFuture(timestamp != null && timestamp == getCurrentDay()));

                // IF / IF NOT GENERATED
                CompletableFuture<List<QASItems>> qasItems = hasGenerated.thenCompose(hasGeneratedResult -> {
                    if (hasGeneratedResult) {
                        return getAllQuests(basicServerData.getPlayerId());
                    } else {
                        List<PlayerTask> playerTaskList = OtherProcessors.TaskProcessors.generate5Tasks(taskDataList);
                        saveAllQuests(basicServerData.getPlayerId(), playerTaskList);
                        return OtherProcessors.TaskProcessors.PlayerTaskToQASItems(playerTaskList);
                    }
                });

                // SAVE THE GENERATED TASKS ON APPLICATION CLASS.
                qasItems.thenAccept(qasItems1 -> merkado.setTaskQASList(qasItems1));
            });
        }
    }

    /**
     * Check for prologue.
     */
    private void process6() {
        Integer index = 0;
        if (merkado.getPlayer().getPlayerStoryList() == null) {
            processEnd();
            return;
        }
        for (PlayerStory playerStory : merkado.getPlayer().getPlayerStoryList()) {
            if (playerStory.getChapter().getChapter().equals("Prologue") && playerStory.getCurrentScene().getId() == 0) {
                intent.putExtra("PROLOGUE", true);
                intent.putExtra("PLAYER_STORY", playerStory);
                intent.putExtra("CURRENT_QUEUE_INDEX", index);
            }
            index++;
        }
        processEnd();
    }

    private void processEnd() {
        if (!Merkado.getInstance().getHasTakenPretest()) {
            Intent intent = new Intent(getApplicationContext(), QuizDisplay.class);
            intent.putExtras(getIntent());
            intent.putExtra("QUIZ_ID", -2);
            takeDiagnosticTool.launch(intent);
        } else doneLoading();
    }

    private final ActivityResultLauncher<Intent> takeDiagnosticTool = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Integer score = result.getData().getIntExtra("SCORE", -1);
                        ServerDataFunctions.setPlayerPretest(
                                Merkado.getInstance().getPlayer().getServer(),
                                Merkado.getInstance().getPlayerId(),
                                score);
                        Merkado.getInstance().setHasTakenPretest(true);
                    } else {
                        Toast.makeText(getApplicationContext(), "Pre-test results not saved.", Toast.LENGTH_SHORT);
                    }
                }
                doneLoading();
            });

    private void onTimeout() {
        // This function is triggered if the CompletableFuture does not complete in time
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(),
                        "Connection timeout. Please check your internet connection and try again.",
                        Toast.LENGTH_SHORT).show());
        finish();
    }
}