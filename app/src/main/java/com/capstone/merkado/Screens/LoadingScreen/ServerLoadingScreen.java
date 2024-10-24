package com.capstone.merkado.Screens.LoadingScreen;

import static com.capstone.merkado.DataManager.DataFunctionPackage.QASDataFunctions.getAllQuests;
import static com.capstone.merkado.DataManager.DataFunctionPackage.QASDataFunctions.saveAllQuests;
import static com.capstone.merkado.Helpers.OtherProcessors.TimeProcessors.getCurrentDay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
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
import com.capstone.merkado.Objects.ServerDataObjects.BasicServerData;
import com.capstone.merkado.Objects.StoryDataObjects.Chapter;
import com.capstone.merkado.Objects.StoryDataObjects.PlayerStory;
import com.capstone.merkado.Objects.TaskDataObjects.PlayerTask;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.Game.MainMap;
import com.capstone.merkado.Screens.Game.Story.QuizDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerLoadingScreen extends AppCompatActivity {

    private Merkado merkado;
    BasicServerData basicServerData;
    PlayerFBExtractor1 playerFBExtractor;
    List<PlayerStory> playerStoryList;
    Intent intent;

    AtomicInteger process_number;
    long startingTime;


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

        startingTime = System.currentTimeMillis();
        process_number = new AtomicInteger(0);

        process1()
                .thenCompose(result -> {
                    if (this.playerFBExtractor == null) return CompletableFuture.completedFuture(false);
                    return process2();
                })
                .thenCompose(result -> {
                    if (this.playerFBExtractor == null) return CompletableFuture.completedFuture(false);
                    return process3();
                })
                .thenCompose(result -> {
                    if (this.playerFBExtractor == null) return CompletableFuture.completedFuture(false);
                    return process4();
                })
                .thenCompose(result -> {
                    if (this.playerFBExtractor == null) return CompletableFuture.completedFuture(false);
                    return process5();
                })
                .thenCompose(result -> {
                    if (this.playerFBExtractor == null) return CompletableFuture.completedFuture(false);
                    return process6();
                })
                .thenCompose(result -> {
                    if (this.playerFBExtractor == null) return CompletableFuture.completedFuture(false);
                    return process7();
                })
                .thenRun(() -> {
                    if (this.playerFBExtractor == null) return;
                    if (!Merkado.getInstance().getHasTakenPretest()) {
                        Intent intent = new Intent(getApplicationContext(), QuizDisplay.class);
                        intent.putExtras(getIntent());
                        intent.putExtra("QUIZ_ID", -2);
                        takeDiagnosticTool.launch(intent);
                    } else doneLoading();
                });
    }

    /**
     * Process 1 processes all player details.
     */
    private CompletableFuture<Boolean> process1() {
        return PlayerDataFunctions.getPlayerDataFromId(basicServerData.getPlayerId()).thenCompose(playerFBExtractor1 -> {
            this.playerFBExtractor = playerFBExtractor1;
            if (playerFBExtractor != null) {
                merkado.setPlayer(new Player(playerFBExtractor), basicServerData.getPlayerId());
                return CompletableFuture.completedFuture(true);
            } else {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(),
                                "Cannot find player data. Try again later.",
                                Toast.LENGTH_SHORT).show());
                finish();
                return CompletableFuture.completedFuture(false);
            }
        });
    }

    private CompletableFuture<Boolean> process2() {
        return merkado.extractObjectives(getApplicationContext()).thenCompose(ignore ->
                CompletableFuture.completedFuture(null));
    }

    private CompletableFuture<Boolean> process3() {
        CompletableFuture<Boolean> pretestFuture = ServerDataFunctions.checkPlayerPretest(basicServerData.getId(),
                basicServerData.getPlayerId());

        CompletableFuture<Boolean> postTestFuture = ServerDataFunctions.checkPlayerPostTest(basicServerData.getId(),
                basicServerData.getPlayerId());

        return pretestFuture.thenCombine(postTestFuture, (preTestHasTaken, postTestHasTaken) -> {
            merkado.setHasTakenPretest(preTestHasTaken != null && preTestHasTaken);
            merkado.setHasTakenPostTest(postTestHasTaken != null && postTestHasTaken);
            return null;
        });
    }

    /**
     * Process 4 processes all Story queue
     */
    private CompletableFuture<Boolean> process4() {
        if (playerFBExtractor == null) return CompletableFuture.completedFuture(null);

        playerStoryList = new ArrayList<>();
        if (playerFBExtractor.getStoryQueue() == null)
            return CompletableFuture.completedFuture(null);
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
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Process 5 processes Task queue
     */
    private CompletableFuture<Boolean> process5() {
        if (playerFBExtractor == null) return CompletableFuture.completedFuture(null);
        return JsonHelper.getTaskList(getApplicationContext()).thenCompose(taskDataList -> {
            Merkado.getInstance().setTaskDataList(taskDataList);

            // CHECK IF PLAYER TASKS QUEUE IS ALREADY GENERATED FOR TODAY.
            CompletableFuture<Boolean> hasGenerated = QASDataFunctions.getTaskLastUpdate(
                    basicServerData.getPlayerId()).thenCompose(timestamp ->
                    CompletableFuture.completedFuture(timestamp != null && timestamp == getCurrentDay()));

            // IF / IF NOT GENERATED
            CompletableFuture<List<PlayerTask>> qasItems = hasGenerated.thenCompose(hasGeneratedResult -> {
                if (hasGeneratedResult) {
                    return getAllQuests(basicServerData.getPlayerId());
                } else {
                    List<PlayerTask> playerTaskList = OtherProcessors.TaskProcessors.generate5Tasks(taskDataList, playerFBExtractor.getExp());
                    saveAllQuests(basicServerData.getPlayerId(), playerTaskList);
                    return CompletableFuture.completedFuture(playerTaskList);
                }
            });

            // SAVE THE GENERATED TASKS ON APPLICATION CLASS.
            return qasItems.thenCompose(qasItems1 -> {
                merkado.setTaskPlayerList(qasItems1);
                return CompletableFuture.completedFuture(null);
            });
        });
    }

    /**
     * Check for prologue.
     */
    private CompletableFuture<Boolean> process6() {
        return CompletableFuture.supplyAsync(() -> {
            Integer index = 0;
            if (merkado.getPlayer().getPlayerStoryList() == null) {
                return null;
            }
            for (PlayerStory playerStory : merkado.getPlayer().getPlayerStoryList()) {
                if (playerStory.getChapter().getChapter().equals("Prologue") && playerStory.getCurrentScene().getId() == 0) {
                    intent.putExtra("PROLOGUE", true);
                    intent.putExtra("PLAYER_STORY", playerStory);
                    intent.putExtra("CURRENT_QUEUE_INDEX", index);
                }
                index++;
            }
            return null;
        });
    }

    private CompletableFuture<Boolean> process7() {
        if (Objects.equals(merkado.getAccount().getEmail(), basicServerData.getServerOwner())) {
            ServerDataFunctions.getServerKey(basicServerData.getId()).thenAccept(key ->
                    merkado.setServerIdKeyPair(new Pair<>(basicServerData.getId(), key)));
        }
        return CompletableFuture.completedFuture(null);
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

    private void doneLoading() {
        runOnUiThread(() -> new Handler().postDelayed(() -> {
            // if this is not triggered, then the processes will naturally go to StoryMode.class
            merkado.setServer(basicServerData);
            startActivity(intent);
            finish();
        }, 1000 - System.currentTimeMillis() - startingTime));
    }
}