package com.capstone.merkado.Screens.Game;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.CustomViews.ConstraintClicker;
import com.capstone.merkado.CustomViews.PlayerBalanceView;
import com.capstone.merkado.CustomViews.PlayerLevelView;
import com.capstone.merkado.CustomViews.WoodenButton;
import com.capstone.merkado.DataManager.DataFunctionPackage.FactoryDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.StoreDataFunctions;
import com.capstone.merkado.DataManager.StaticData.LevelMaxSetter;
import com.capstone.merkado.Objects.FactoryDataObjects.FactoryData;
import com.capstone.merkado.Objects.FactoryDataObjects.FactoryTypes;
import com.capstone.merkado.Objects.StoresDataObjects.Market;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.Game.Inventory.InventoryActivity;
import com.capstone.merkado.Screens.Game.QuestAndStories.QuestAndStories;
import com.capstone.merkado.Screens.Game.Sectors.ChooseSector;
import com.capstone.merkado.Screens.Game.Sectors.Factories;
import com.capstone.merkado.Screens.Game.Sectors.Factory;
import com.capstone.merkado.Screens.Game.Store.StoreSellerView;
import com.capstone.merkado.Screens.Game.Store.Stores;
import com.capstone.merkado.Screens.MainMenu.MainMenu;
import com.google.android.material.card.MaterialCardView;

import java.util.Objects;

public class MainMap extends AppCompatActivity {

    Merkado merkado;
    CardView inventoryNav, questAndStoriesNav, factoriesNav;
    ImageView storesNav, myStore, myFactory;
    ConstraintClicker myStoreClicker, myHouseClicker, myFactoryClicker;
    PlayerBalanceView playerBalanceView;
    PlayerLevelView playerLevelView;
    OpenStorePopup openStorePopup;
    OpenFactoryPopup openFactoryPopup;

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
        myHouseClicker = findViewById(R.id.my_house_clicker);
        myStoreClicker = findViewById(R.id.my_store_clicker);
        myFactoryClicker = findViewById(R.id.my_factory_clicker);
        openStorePopup = new OpenStorePopup(this, findViewById(R.id.layout_open_store_popup),
                merkado.getPlayerData().getPlayerMarket() == null ||
                        !merkado.getPlayerData().getPlayerMarket().getHadMarket());
        openFactoryPopup = new OpenFactoryPopup(this, findViewById(R.id.layout_open_factory_popup),
                merkado.getPlayerData().getPlayerFactory() == null);
        openStorePopup.hide();
        openFactoryPopup.hide();

        // update features
        merkado.getPlayerData().setPlayerMarketIdListener(market -> {
            updateMarketView(market);
            updateAccessibleButtons();
        });
        merkado.getPlayerData().setPlayerFactoryListener(factoryData -> {
            updateFactoryView(factoryData);
            updateAccessibleButtons();
        });

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
        updateAccessibleButtons();
    }

    private void sendFactoryIntent() {
        FactoryDataFunctions.getFactoryData(merkado.getPlayerId()).thenAccept(factoryData -> {
            FactoryTypes factoryTypes = "FOOD".equals(factoryData.getFactoryType()) ?
                    FactoryTypes.FOOD : FactoryTypes.MANUFACTURING;
            Intent intent = new Intent(getApplicationContext(), Factory.class);
            intent.putExtra("FACTORY_DETAILS", factoryData.getDetails());
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

    private void updateMarketView(Market market) {
        myStore.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.gui_my_store_closed));
        if (playerLevel < 2) return;
        if (market == null || market.getId() == null) {
            myStore.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.gui_my_store_vacant));
            myStoreClicker.setOnClickListener(v -> openStore());
        } else {
            myStore.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.gui_my_store_open));
            myStoreClicker.setOnClickListener(v ->
                    refreshAfterIntent.launch(new Intent(getApplicationContext(), StoreSellerView.class)));
        }
        openStorePopup.isFirstTime(market == null || market.getHadMarket() == null || !market.getHadMarket());
    }

    private void updateFactoryView(FactoryData factoryData) {
        myFactory.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.gui_my_factory_closed));
        if (playerLevel < 4) return;
        if (merkado.getPlayerData().getPlayerFactory() == null ||
                merkado.getPlayerData().getPlayerFactory().getFactoryMarketId() == null) {
            myFactory.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.gui_my_factory_vacant));
            myFactoryClicker.setOnClickListener(v -> openFactory());
        } else {
            myFactory.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.gui_my_factory_open));
            if (factoryData.getFactoryType() == null)
                myFactoryClicker.setOnClickListener(v ->
                        startActivity(new Intent(getApplicationContext(), ChooseSector.class)));
            else myFactoryClicker.setOnClickListener(v -> sendFactoryIntent());
        }
        openStorePopup.isFirstTime(factoryData == null);
    }

    private void updatePlayerLevelView(Long exp) {
        Long maxLevel = LevelMaxSetter.getMaxPlayerExperience(exp);
        Long previousMaxLevel = LevelMaxSetter.getPreviousMaxPlayerExperience(maxLevel);
        playerLevel = LevelMaxSetter.getPlayerLevel(maxLevel);
        playerLevelView.setExperience(previousMaxLevel, exp, maxLevel);
        if (!Objects.equals(previousPlayerLevel, playerLevel)) {
            previousPlayerLevel = playerLevel;
            accessibleFunctionalityUpdate();
            updateAccessibleButtons();
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
            if (merkado.getPlayerData().getPlayerMarket() == null || merkado.getPlayerData().getPlayerMarket().getId() == null) {
                myStore.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.gui_my_store_vacant));
                openStorePopup.isFirstTime(true);
                myStoreClicker.setOnClickListener(v -> openStore());
            } else {
                myStore.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.gui_my_store_open));
                myStoreClicker.setOnClickListener(v ->
                        refreshAfterIntent.launch(new Intent(getApplicationContext(), StoreSellerView.class)));
            }
            factoriesNav.setOnClickListener(v ->
                    refreshAfterIntent.launch(new Intent(getApplicationContext(), Factories.class))
            );
        }
        if (playerLevel >= 4) {
            if (merkado.getPlayerData().getPlayerFactory() == null || merkado.getPlayerData().getPlayerFactory().getFactoryMarketId() == null) {
                myFactory.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.gui_my_factory_vacant));
                myFactoryClicker.setOnClickListener(v -> openFactory());
            } else {
                myFactory.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.gui_my_factory_open));
                myFactoryClicker.setOnClickListener(v -> sendFactoryIntent());
            }
        }
    }

    private void updateAccessibleButtons() {
        if (playerLevel == 1) {
            storesNav.setVisibility(View.GONE);
            factoriesNav.setVisibility(View.GONE);
        } else if (playerLevel == 2) {
            storesNav.setVisibility(View.VISIBLE);
            factoriesNav.setVisibility(View.VISIBLE);
        } else if (playerLevel == 4) {
            storesNav.setVisibility(View.VISIBLE);
            factoriesNav.setVisibility(merkado.getPlayerData().hasFactory() ? View.GONE : View.VISIBLE);
        }
    }

    private void disableAll() {
        storesNav.setOnClickListener(null);
        factoriesNav.setOnClickListener(null);
        myStoreClicker.setOnClickListener(null);
        myFactoryClicker.setOnClickListener(null);
        myFactory.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.gui_my_factory_closed));
        myStore.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.gui_my_store_closed));
    }

    private void openStore() {
        openStorePopup.show();
        openStorePopup.setButtonListener(new OpenStorePopup.ButtonListener() {
            @Override
            public void onConfirm() {
                StoreDataFunctions.setUpStore(
                        merkado.getPlayer().getServer(),
                        merkado.getPlayerId(),
                        merkado.getAccount().getUsername(),
                        merkado.getPlayer().getFactory() != null ?
                                merkado.getPlayer().getFactory().getFactoryMarketId() :
                                null);
                Toast.makeText(getApplicationContext(), "Store claimed.", Toast.LENGTH_SHORT).show();
                openStorePopup.hide();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Store not claimed.", Toast.LENGTH_SHORT).show();
                openStorePopup.hide();
            }
        });
    }

    private void openFactory() {
        openFactoryPopup.show();
        openFactoryPopup.setButtonListener(new OpenFactoryPopup.ButtonListener() {
            @Override
            public void onConfirm() {
                FactoryDataFunctions.setUpFactory(
                        merkado.getPlayer().getServer(),
                        merkado.getPlayerId(),
                        merkado.getAccount().getUsername(),
                        merkado.getPlayer().getMarket() != null?
                                merkado.getPlayer().getMarket().getId():
                                null
                );
                Toast.makeText(getApplicationContext(), "Factory claimed.", Toast.LENGTH_SHORT).show();
                openFactoryPopup.hide();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Factory not opened.", Toast.LENGTH_SHORT).show();
                openFactoryPopup.hide();
            }
        });
    }

    private static class OpenStorePopup {
        MaterialCardView master;
        ButtonListener buttonListener;

        Context context;
        TextView popUpText;

        public OpenStorePopup(Context context, MaterialCardView master, Boolean firstTime) {
            this.master = master;
            this.context = context;
            popUpText = master.findViewById(R.id.store_setup_message);
            WoodenButton confirmButton = master.findViewById(R.id.store_setup_confirm);
            TextView cancelButton = master.findViewById(R.id.store_setup_cancel);

            isFirstTime(firstTime);

            confirmButton.setOnClickListener(v -> {
                if (buttonListener != null)
                    buttonListener.onConfirm();
            });

            cancelButton.setOnClickListener(v -> {
                if (buttonListener != null)
                    buttonListener.onCancel();
            });
        }

        public void hide() {
            master.setVisibility(View.GONE);
        }

        public void show() {
            master.setVisibility(View.VISIBLE);
        }

        public void isFirstTime(Boolean firstTime) {
            popUpText.setText(ContextCompat.getString(context, firstTime ?
                    R.string.open_store_popup_first_time :
                    R.string.open_store_popup));
        }

        public void setButtonListener(ButtonListener buttonListener) {
            this.buttonListener = buttonListener;
        }

        public interface ButtonListener {
            void onConfirm();

            void onCancel();
        }
    }

    private static class OpenFactoryPopup {
        MaterialCardView master;
        ButtonListener buttonListener;

        Context context;
        TextView popUpText;

        public OpenFactoryPopup(Context context, MaterialCardView master, Boolean firstTime) {
            this.master = master;
            this.context = context;
            popUpText = master.findViewById(R.id.factory_setup_message);
            WoodenButton confirmButton = master.findViewById(R.id.factory_setup_confirm);
            TextView cancelButton = master.findViewById(R.id.factory_setup_cancel);

            isFirstTime(firstTime);

            confirmButton.setOnClickListener(v -> {
                if (buttonListener != null)
                    buttonListener.onConfirm();
            });

            cancelButton.setOnClickListener(v -> {
                if (buttonListener != null)
                    buttonListener.onCancel();
            });
        }

        public void hide() {
            master.setVisibility(View.GONE);
        }

        public void show() {
            master.setVisibility(View.VISIBLE);
        }

        public void isFirstTime(Boolean firstTime) {
            popUpText.setText(ContextCompat.getString(context, firstTime ?
                    R.string.open_factory_popup_first_time :
                    R.string.open_factory_popup));
        }

        public void setButtonListener(ButtonListener buttonListener) {
            this.buttonListener = buttonListener;
        }

        public interface ButtonListener {
            void onConfirm();

            void onCancel();
        }
    }

    @Override
    protected void onDestroy() {
        merkado.setServer(null);
        merkado.setPlayer(null, null);
        super.onDestroy();
    }
}