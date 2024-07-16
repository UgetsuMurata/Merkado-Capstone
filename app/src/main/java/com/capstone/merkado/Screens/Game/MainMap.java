package com.capstone.merkado.Screens.Game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.CustomViews.PlayerBalanceView;
import com.capstone.merkado.CustomViews.PlayerLevelView;
import com.capstone.merkado.DataManager.DataFunctions;
import com.capstone.merkado.Objects.FactoryDataObjects.FactoryTypes;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.Game.Inventory.InventoryActivity;
import com.capstone.merkado.Screens.Game.QuestAndStories.QuestAndStories;
import com.capstone.merkado.Screens.Game.Sectors.Factories;
import com.capstone.merkado.Screens.Game.Sectors.Factory;
import com.capstone.merkado.Screens.Game.Store.StoreSellerView;
import com.capstone.merkado.Screens.Game.Store.Stores;
import com.capstone.merkado.Screens.MainMenu.MainMenu;

public class MainMap extends AppCompatActivity {

    Merkado merkado;
    CardView inventoryNav, questAndStoriesNav, storesNav, factoriesNav;
    ImageView myStore, myFactory;
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
        factoriesNav = findViewById(R.id.factories);
        playerBalanceView = findViewById(R.id.player_balance);
        playerLevelView = findViewById(R.id.player_level);
        myStore = findViewById(R.id.my_store);
        myFactory = findViewById(R.id.my_factory);

        // onBackPressed
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        inventoryNav.setOnClickListener(v -> refreshAfterIntent.launch(new Intent(getApplicationContext(), InventoryActivity.class)));
        questAndStoriesNav.setOnClickListener(v -> refreshAfterIntent.launch(new Intent(getApplicationContext(), QuestAndStories.class)));
        storesNav.setOnClickListener(v -> refreshAfterIntent.launch(new Intent(getApplicationContext(), Stores.class)));
        factoriesNav.setOnClickListener(v -> refreshAfterIntent.launch(new Intent(getApplicationContext(), Factories.class)));

        myStore.setOnClickListener(v -> refreshAfterIntent.launch(new Intent(getApplicationContext(), StoreSellerView.class)));
        myFactory.setOnClickListener(v -> sendFactoryIntent());
    }

    private void sendFactoryIntent() {
        DataFunctions.getFactoryData(merkado.getPlayerId()).thenAccept(factoryData -> {
            FactoryTypes factoryTypes = "FOOD".equals(factoryData.getFactoryType()) ?
                    FactoryTypes.FOOD : FactoryTypes.INDUSTRIAL;
            Intent intent = new Intent(getApplicationContext(), Factory.class);
            intent.putExtra("FACTORY_DETAILS", factoryData.getDetails());
            intent.putExtra("FACTORY_ID", factoryData.getFactoryId());
            intent.putExtra("FACTORY_TYPE", factoryTypes);
            refreshAfterIntent.launch(intent);
        });
    }

}