package com.capstone.merkado.Screens.Game;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.CustomViews.PlayerBalanceView;
import com.capstone.merkado.CustomViews.PlayerLevelView;
import com.capstone.merkado.DataManager.DataFunctionPackage.DataFunctions;
import com.capstone.merkado.DataManager.StaticData.LevelMaxSetter;
import com.capstone.merkado.Objects.FactoryDataObjects.FactoryTypes;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.Game.Inventory.InventoryActivity;
import com.capstone.merkado.Screens.Game.QuestAndStories.QuestAndStories;
import com.capstone.merkado.Screens.Game.Sectors.Factories;
import com.capstone.merkado.Screens.Game.Sectors.Factory;
import com.capstone.merkado.Screens.Game.Store.StoreSellerView;
import com.capstone.merkado.Screens.Game.Store.Stores;
import com.capstone.merkado.Screens.MainMenu.MainMenu;

import java.util.Objects;

public class MainMap extends AppCompatActivity {

    Merkado merkado;
    CardView inventoryNav, questAndStoriesNav, factoriesNav, storeLock, factoryLock;
    ImageView storesNav, myStore, myFactory;
    PlayerBalanceView playerBalanceView;
    PlayerLevelView playerLevelView;

    // VARIABLES
    Long playerExp;
    Float playerMoney;
    Integer playerLevel = 0;
    Integer previousPlayerLevel = -1;

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
        storeLock = findViewById(R.id.store_lock);
        factoryLock = findViewById(R.id.factory_lock);

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

        // setup ui data
        setupPlayerLevelView();
        setupPlayerBalanceView();
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

    private void setupPlayerLevelView() {
        try {
            this.playerExp = merkado.getPlayerData().getPlayerExp();
        } catch (NullPointerException error) {
            this.playerExp = 0L;
            Log.e("setupPlayerLevelView", String.format("An error occurred: %s", error));
        }
        updatePlayerLevelView(playerExp);
        merkado.getPlayerData().setPlayerExpListener(this::updatePlayerLevelView);
    }

    private void setupPlayerBalanceView() {
        this.playerMoney = merkado.getPlayerData().getPlayerMoney();
        updatePlayerBalanceView(this.playerMoney);
        merkado.getPlayerData().setPlayerMoneyListener(this::updatePlayerBalanceView);
    }

    private void updatePlayerLevelView(Long exp) {
        Long maxLevel = LevelMaxSetter.getMaxPlayerExperience(exp);
        Long previousMaxLevel = LevelMaxSetter.getPreviousMaxPlayerExperience(maxLevel);
        playerLevel = LevelMaxSetter.getPlayerLevel(maxLevel);
        playerLevelView.setExperience(previousMaxLevel, exp, maxLevel);
        if (!Objects.equals(previousPlayerLevel, playerLevel)) {
            previousPlayerLevel = playerLevel;
            accessibleFunctionalityUpdate();
            playerLevelView.setPlayerLevel(playerLevel);
        }
    }

    private void updatePlayerBalanceView(Float money) {
        playerBalanceView.setBalance(money);
    }

    private void accessibleFunctionalityUpdate() {
        disableAll();
        if (playerLevel >= 2) {
            storesNav.setOnClickListener(v -> {
                storesNav.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gui_gamemap_stores_active));
                new Handler().postDelayed(() -> {
                    storesNav.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gui_gamemap_stores_idle));
                    refreshAfterIntent.launch(new Intent(getApplicationContext(), Stores.class));
                }, 100);
            });
        }
        if (playerLevel >= 3) {
            myStore.setOnClickListener(v -> refreshAfterIntent.launch(new Intent(getApplicationContext(), StoreSellerView.class)));
            factoriesNav.setOnClickListener(v ->
                    refreshAfterIntent.launch(new Intent(getApplicationContext(), Factories.class))
            );
            storeLock.setVisibility(View.GONE);
        }
        if (playerLevel >= 4) {
            myFactory.setOnClickListener(v -> sendFactoryIntent());
            factoryLock.setVisibility(View.GONE);
        }
    }

    private void disableAll() {
        storesNav.setOnClickListener(null);
        factoriesNav.setOnClickListener(null);
        myStore.setOnClickListener(null);
        myFactory.setOnClickListener(null);
        storeLock.setVisibility(View.VISIBLE);
        factoryLock.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        merkado.setServer(null);
        merkado.setPlayer(null, null);
        super.onDestroy();
    }
}