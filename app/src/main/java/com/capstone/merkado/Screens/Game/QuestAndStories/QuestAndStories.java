package com.capstone.merkado.Screens.Game.QuestAndStories;

import static com.capstone.merkado.Helpers.StringProcessor.descriptionResourceProcessor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.Adapters.QASAdapter;
import com.capstone.merkado.Adapters.QASObjectivesListAdapter;
import com.capstone.merkado.Adapters.QASRewardsAdapter;
import com.capstone.merkado.Adapters.QASTasksListAdapter;
import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctionPackage.QASDataFunctions;
import com.capstone.merkado.Objects.QASDataObjects.QASItems;
import com.capstone.merkado.Objects.QASDataObjects.QASItems.QASDetail;
import com.capstone.merkado.Objects.QASDataObjects.QASItems.QASDetail.QASReward;
import com.capstone.merkado.Objects.ServerDataObjects.Objectives;
import com.capstone.merkado.Objects.TaskDataObjects.PlayerTask;
import com.capstone.merkado.Objects.TaskDataObjects.TaskData;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.Game.Story.StoryMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@SuppressLint("NotifyDataSetChanged")
public class QuestAndStories extends AppCompatActivity {

    Merkado merkado;

    // VIEWS
    ImageView showQuests, showStories, backButton, showObjectives;
    TextView qasListEmpty, qasName, qasDescription, qasObjectiveTitle, qasObjectiveSubtitle;
    RecyclerView qasList, qasRewards, qasObjectiveList;
    LinearLayout qasRewardsSection, qasDisplayObjectives;
    CardView qasStartStory;

    private final ActivityResultLauncher<Intent> refreshAfterIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent i = new Intent(getApplicationContext(), QuestAndStories.class);
                overridePendingTransition(0, 0);
                startActivity(i);
                overridePendingTransition(0, 0);
                finish();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gam_qas_quest_and_stories);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        showStories = findViewById(R.id.qas_show_stories);
        showQuests = findViewById(R.id.qas_show_quests);
        showObjectives = findViewById(R.id.qas_show_objectives);
        qasListEmpty = findViewById(R.id.qas_list_empty);
        qasList = findViewById(R.id.qas_list);
        qasName = findViewById(R.id.qas_name);
        qasDescription = findViewById(R.id.qas_description);
        qasRewards = findViewById(R.id.qas_rewards);
        qasRewardsSection = findViewById(R.id.qas_rewards_section);
        qasStartStory = findViewById(R.id.qas_start_story);
        backButton = findViewById(R.id.back_button);
        qasDisplayObjectives = findViewById(R.id.qas_display_objectives);
        qasObjectiveTitle = findViewById(R.id.qas_objective_title);
        qasObjectiveSubtitle = findViewById(R.id.qas_objective_subtitle);
        qasObjectiveList = findViewById(R.id.qas_objective_list);

        noSelectedQAS();

        showStories.setOnClickListener(view -> retrieveDataToShow(ShowMode.STORY));
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        showQuests.setOnClickListener(view -> retrieveDataToShow(ShowMode.QUEST));
        showObjectives.setOnClickListener(view -> retrieveDataToShow(ShowMode.OBJECTIVES));
        retrieveDataToShow(ShowMode.STORY);
    }

    private void retrieveDataToShow(ShowMode showMode) {
        noSelectedQAS();
        if (ShowMode.OBJECTIVES.equals(showMode)) displayObjectives();
        if (ShowMode.QUEST.equals(showMode)) displayTasks();
        else
            getItemsByShowMode(showMode).thenAccept(qasItemsList ->
                    runOnUiThread(() -> {
                        if (qasItemsList == null) nothingToShow(showMode, true);
                        else if (qasItemsList.isEmpty()) nothingToShow(showMode, false);
                        else setUpQASList(qasItemsList);
                    })
            ).exceptionally(throwable -> {
                Log.e("retrieveDataToShow", String.format("Problem in retrieving data for %s: %s", showMode, throwable));
                return null;
            });
    }

    private void displayObjectives() {
        qasDisplayObjectives.setVisibility(View.VISIBLE);
        qasListEmpty.setVisibility(View.GONE);
        qasList.setVisibility(View.GONE);

        Objectives.Objective objective = merkado.getPlayerData().getCurrentObjective();
        Objectives objectives = merkado.getPlayerData().getObjectives();
        qasObjectiveTitle.setText(objectives.getTitle());
        qasObjectiveSubtitle.setText(objectives.getSubtitle());
        QASObjectivesListAdapter qasAdapter = new QASObjectivesListAdapter(getApplicationContext(),
                objectives.getObjectives(),
                objective == null ? null : objective.getId(),
                merkado.getPlayerData().getObjectiveDone());
        qasObjectiveList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        qasObjectiveList.setAdapter(qasAdapter);
    }

    private void displayTasks() {
        qasDisplayObjectives.setVisibility(View.GONE);
        qasListEmpty.setVisibility(View.GONE);
        qasList.setVisibility(View.VISIBLE);

        List<PlayerTask> playerTaskList = merkado.getTaskPlayerList();
        List<TaskData> taskDataList = merkado.getTaskDataList();
        QASTasksListAdapter qasAdapter = new QASTasksListAdapter(getApplicationContext(),
                playerTaskList,
                taskDataList);
        qasList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        qasList.setAdapter(qasAdapter);

        qasAdapter.setOnCLickListener(this::setUpTaskDetails);
    }

    private void setUpTaskDetails(@NonNull TaskData taskData, PlayerTask playerTask) {
        qasName.setText(taskData.getTitle());
        qasDescription.setText(descriptionResourceProcessor(taskData.getDescription(),
                playerTask.getTaskNote()));
        qasStartStory.setVisibility(View.GONE);
        qasRewardsSection.setVisibility(View.VISIBLE);
        List<QASReward> qasRewardList = new ArrayList<>();
        taskData.getRewards().forEach(gameRewards -> qasRewardList.add(new QASReward(gameRewards)));
        showRewards(qasRewardList);
    }

    private CompletableFuture<List<QASItems>> getItemsByShowMode(ShowMode showMode) {
        if (Objects.requireNonNull(showMode) == ShowMode.STORY) {
            return QASDataFunctions.getAllStories(merkado.getPlayerId());
        }
        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    private void noSelectedQAS() {
        qasName.setText("");
        qasDescription.setText("");
        qasRewardsSection.setVisibility(View.GONE);
        qasStartStory.setVisibility(View.GONE);
    }

    private void nothingToShow(ShowMode showMode, Boolean isNull) {
        qasListEmpty.setVisibility(View.VISIBLE);
        qasList.setVisibility(View.GONE);
        qasDisplayObjectives.setVisibility(View.GONE);
        switch (showMode) {
            case QUEST:
                qasListEmpty.setText(String.format(getString(isNull ? R.string.qasListNull : R.string.qasListEmpty), "quests"));
                break;
            case STORY:
                qasListEmpty.setText(String.format(getString(isNull ? R.string.qasListNull : R.string.qasListEmpty), "stories"));
                break;
        }
    }

    private void setUpQASList(List<QASItems> qasItemsList) {
        qasListEmpty.setVisibility(View.GONE);
        qasDisplayObjectives.setVisibility(View.GONE);
        qasList.setVisibility(View.VISIBLE);
        QASAdapter qasAdapter = new QASAdapter(getApplicationContext(), qasItemsList);
        qasList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        qasList.setAdapter(qasAdapter);
        qasAdapter.setOnClickListener((qasDetail, history) ->
                runOnUiThread(() -> setUpStoryDetails(qasDetail, history)));
        qasAdapter.notifyDataSetChanged();
    }

    private void setUpStoryDetails(@NonNull QASDetail qasDetail, Boolean history) {
        qasName.setText(qasDetail.getQasName());
        qasDescription.setText(qasDetail.getQasDescription());
        qasStartStory.setVisibility(View.VISIBLE);
        qasStartStory.setOnClickListener(v -> goToStory(qasDetail, history));
        if (qasDetail.getQasRewards().isEmpty() || history)
            qasRewardsSection.setVisibility(View.GONE);
        else {
            qasRewardsSection.setVisibility(View.VISIBLE);
            showRewards(qasDetail.getQasRewards());
        }
    }

    private void showRewards(List<QASReward> qasRewards) {
        QASRewardsAdapter qasRewardsAdapter = new QASRewardsAdapter(this, qasRewards);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        this.qasRewards.setLayoutManager(layoutManager);

        this.qasRewards.setAdapter(qasRewardsAdapter);
    }

    private void goToStory(QASDetail qasDetail, Boolean history) {
        Intent intent = new Intent(getApplicationContext(), StoryMode.class);
        intent.putExtra("PLAYER_STORY", history ?
                merkado.getPlayerData().getStoryHistory().get(qasDetail.getQueueId()) :
                merkado.getPlayerData().getPlayerStory().get(qasDetail.getQueueId()));
        intent.putExtra("CURRENT_QUEUE_INDEX", qasDetail.getQueueId());
        intent.putExtra("HISTORY", history);
        refreshAfterIntent.launch(intent);
    }

    private enum ShowMode {
        QUEST, STORY, OBJECTIVES
    }
}