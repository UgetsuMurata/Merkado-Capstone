package com.capstone.merkado.Screens.Game.QuestAndStories;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.Adapters.QASAdapter;
import com.capstone.merkado.Adapters.QASRewardsAdapter;
import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctionPackage.QASDataFunctions;
import com.capstone.merkado.Objects.QASDataObjects.QASItems;
import com.capstone.merkado.Objects.QASDataObjects.QASItems.QASDetail;
import com.capstone.merkado.Objects.QASDataObjects.QASItems.QASDetail.QASReward;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.Game.StoryMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SuppressLint("NotifyDataSetChanged")
public class QuestAndStories extends AppCompatActivity {

    Merkado merkado;

    // VIEWS
    ImageView showAll, showQuests, showStories;
    TextView qasListEmpty, qasName, qasDescription;
    RecyclerView qasList, qasRewards;
    LinearLayout qasRewardsSection;
    CardView qasStartStory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gam_qas_quest_and_stories);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        showAll = findViewById(R.id.qas_show_all);
        showQuests = findViewById(R.id.qas_show_quests);
        showStories = findViewById(R.id.qas_show_stories);
        qasListEmpty = findViewById(R.id.qas_list_empty);
        qasList = findViewById(R.id.qas_list);
        qasName = findViewById(R.id.qas_name);
        qasDescription = findViewById(R.id.qas_description);
        qasRewards = findViewById(R.id.qas_rewards);
        qasRewardsSection = findViewById(R.id.qas_rewards_section);
        qasStartStory = findViewById(R.id.qas_start_story);

        noSelectedQAS();

        retrieveDataToShow(ShowMode.ALL);
        showAll.setOnClickListener(view -> retrieveDataToShow(ShowMode.ALL));
        showQuests.setOnClickListener(view -> retrieveDataToShow(ShowMode.QUEST));
        showStories.setOnClickListener(view -> retrieveDataToShow(ShowMode.STORY));

    }

    private void retrieveDataToShow(ShowMode showMode) {
        noSelectedQAS();
        getItemsByShowMode(showMode).thenAccept(qasItemsList ->
                runOnUiThread(()->{
                    if (qasItemsList.isEmpty()) nothingToShow(showMode);
                    else setUpQASList(qasItemsList);
                })
        ).exceptionally(throwable -> {
            Log.e("retrieveDataToShow", String.format("Problem in retrieving data for %s: %s", showMode, throwable));
            return null;
        });
    }

    private CompletableFuture<List<QASItems>> getItemsByShowMode(ShowMode showMode) {
        switch (showMode) {
            case ALL:
                return QASDataFunctions.getAllQuestsAndStories(merkado.getPlayerId());
            case QUEST:
                return QASDataFunctions.getAllQuests(merkado.getPlayerId());
            case STORY:
                return QASDataFunctions.getAllStories(merkado.getPlayerId());
            default:
                return CompletableFuture.completedFuture(new ArrayList<>());
        }
    }

    private void noSelectedQAS() {
        qasName.setText("");
        qasDescription.setText("");
        qasRewardsSection.setVisibility(View.GONE);
        qasStartStory.setVisibility(View.GONE);
    }

    private void nothingToShow(ShowMode showMode) {
        qasListEmpty.setVisibility(View.VISIBLE);
        qasList.setVisibility(View.GONE);

        switch (showMode) {
            case ALL:
                qasListEmpty.setText(String.format(getString(R.string.qasListEmpty), "quests and stories"));
                break;
            case QUEST:
                qasListEmpty.setText(String.format(getString(R.string.qasListEmpty), "quests"));
                break;
            case STORY:
                qasListEmpty.setText(String.format(getString(R.string.qasListEmpty), "stories"));
                break;
        }
    }

    private void setUpQASList(List<QASItems> qasItemsList) {
        qasListEmpty.setVisibility(View.GONE);
        qasList.setVisibility(View.VISIBLE);
        QASAdapter qasAdapter = new QASAdapter(getApplicationContext(), qasItemsList);
        qasList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        qasList.setAdapter(qasAdapter);
        qasAdapter.setOnClickListener((qasDetail, qasGroup) ->
                runOnUiThread(() -> setUpDetails(qasDetail, qasGroup)));
        qasAdapter.notifyDataSetChanged();
    }

    private void setUpDetails(@NonNull QASDetail qasDetail, String group) {
        qasName.setText(qasDetail.getQasName());
        qasDescription.setText(qasDetail.getQasDescription());
        if ("STORIES".equals(group)) {
            qasStartStory.setVisibility(View.VISIBLE);
            qasStartStory.setOnClickListener(v -> goToStory(qasDetail));
        } else qasStartStory.setVisibility(View.GONE);
        if (qasDetail.getQasRewards().isEmpty()) qasRewardsSection.setVisibility(View.GONE);
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

    private void goToStory(QASDetail qasDetail) {
        Intent intent = new Intent(getApplicationContext(), StoryMode.class);
        intent.putExtra("PLAYER_STORY", merkado.getPlayer().getPlayerStoryList().get(qasDetail.getQueueId()));
        intent.putExtra("CURRENT_QUEUE_INDEX", qasDetail.getQueueId());
        startActivity(intent);
    }

    private enum ShowMode {
        ALL, QUEST, STORY
    }
}