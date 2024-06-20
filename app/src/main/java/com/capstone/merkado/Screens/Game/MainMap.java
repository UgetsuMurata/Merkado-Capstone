package com.capstone.merkado.Screens.Game;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.CustomViews.PlayerBalanceView;
import com.capstone.merkado.CustomViews.PlayerLevelView;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.Game.Inventory.Inventory;
import com.capstone.merkado.Screens.Game.QuestAndStories.QuestAndStories;
import com.capstone.merkado.Screens.Game.Store.Stores;

public class MainMap extends AppCompatActivity {

    Merkado merkado;
    CardView inventoryNav, questAndStoriesNav, storesNav;
    PlayerBalanceView playerBalanceView;
    PlayerLevelView playerLevelView;

    private final ActivityResultLauncher<Intent> refreshAfterIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent i = new Intent(getApplicationContext(), MainMap.class);
                    overridePendingTransition(0, 0);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gam_main_map);

        // check for an intent that says there's prologue
        if (getIntent().hasExtra("PROLOGUE")) {
            Intent intent = new Intent(getApplicationContext(), StoryMode.class);
            intent.putExtras(getIntent());
            refreshAfterIntent.launch(intent);
        }

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        inventoryNav = findViewById(R.id.inventory_nav);
        questAndStoriesNav = findViewById(R.id.quests_and_stories_nav);
        storesNav = findViewById(R.id.store_nav);
        playerBalanceView = findViewById(R.id.player_balance);
        playerLevelView = findViewById(R.id.player_level);

        inventoryNav.setOnClickListener(v -> refreshAfterIntent.launch(new Intent(getApplicationContext(), Inventory.class)));
        questAndStoriesNav.setOnClickListener(v -> refreshAfterIntent.launch(new Intent(getApplicationContext(), QuestAndStories.class)));
        storesNav.setOnClickListener(v -> refreshAfterIntent.launch(new Intent(getApplicationContext(), Stores.class)));
    }

}