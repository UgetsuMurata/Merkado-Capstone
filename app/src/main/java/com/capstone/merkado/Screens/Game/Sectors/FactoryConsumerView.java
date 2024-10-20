package com.capstone.merkado.Screens.Game.Sectors;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.Adapters.StoreViewAdapter;
import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.CustomViews.PlayerBalanceView;
import com.capstone.merkado.DataManager.DataFunctionPackage.FactoryDataFunctions.PlayerFactoryUpdates;
import com.capstone.merkado.DataManager.DataFunctionPackage.InternalDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.StoreDataFunctions;
import com.capstone.merkado.DataManager.StaticData.GameResourceCaller;
import com.capstone.merkado.Helpers.PlayerActions;
import com.capstone.merkado.Helpers.RewardProcessor;
import com.capstone.merkado.Helpers.StringProcessor;
import com.capstone.merkado.Objects.FactoryDataObjects.PlayerFactory;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceData;
import com.capstone.merkado.Objects.ServerDataObjects.MarketStandard.MarketStandardList;
import com.capstone.merkado.Objects.StoresDataObjects.PlayerMarkets.OnSale;
import com.capstone.merkado.Objects.StoresDataObjects.StoreBuyingData;
import com.capstone.merkado.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FactoryConsumerView extends AppCompatActivity {

    Merkado merkado;
    TextView factoryName;
    RecyclerView itemList;
    TextView itemName, itemDescription, itemPrice, itemListEmpty, itemDescriptionContainerEmpty;
    ScrollView itemDescriptionContainer;
    ImageView itemImage;
    ConstraintLayout itemBackground;
    CardView itemPurchaseButton;

    // purchase overlay
    ConstraintLayout purchaseOverlay;
    TextView purchaseOverlayItemName, purchaseOverlayItemQuantity, purchaseOverlayCost;
    ImageView purchaseOverlayImage, purchaseOverlayAddQTY, purchaseOverlaySubtractQTY;
    SeekBar purchaseOverlayQTYSlider;
    CardView purchaseOverlayCancel, purchaseOverlayConfirm;

    // variables
    PlayerBalanceView playerBalanceView;
    StoreViewAdapter storeViewAdapter;
    PlayerFactory playerfactory;
    Integer factoryId;
    PlayerFactoryUpdates playerFactoryUpdates;
    Integer purchaseOverlayQuantity = 0;
    Integer purchaseOverlayQuantityButtonsMode = -1;
    Float purchaseOverlayFinalCost = 0f;
    OnSale currentOnSale;
    Boolean playerMoneyIsEnough = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gam_sec_factory_consumer_view);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        if (!getIntent().hasExtra("PLAYER_FACTORY") || !getIntent().hasExtra("FACTORY_ID")) {
            Toast.makeText(getApplicationContext(), "Problem occurred. Please try again later.", Toast.LENGTH_LONG).show();
            return;
        } else {
            playerfactory = getIntent().getParcelableExtra("PLAYER_FACTORY");
            factoryId = getIntent().getIntExtra("FACTORY_ID", -1);
        }

        factoryName = findViewById(R.id.store_name);
        itemList = findViewById(R.id.item_list);
        itemName = findViewById(R.id.item_name);
        itemPrice = findViewById(R.id.item_price);
        itemDescription = findViewById(R.id.item_description);
        itemImage = findViewById(R.id.item_image);
        itemBackground = findViewById(R.id.item_background);
        itemPurchaseButton = findViewById(R.id.item_purchase);
        playerBalanceView = findViewById(R.id.player_current_balance);
        itemListEmpty = findViewById(R.id.item_list_empty);
        itemDescriptionContainer = findViewById(R.id.item_description_container);
        itemDescriptionContainerEmpty = findViewById(R.id.item_description_container_empty);

        // purchase overlay
        purchaseOverlay = findViewById(R.id.purchase_overlay);
        purchaseOverlayItemName = findViewById(R.id.purchase_overlay_item_name);
        purchaseOverlayImage = findViewById(R.id.purchase_overlay_image);
        purchaseOverlayItemQuantity = findViewById(R.id.purchase_overlay_item_quantity);
        purchaseOverlayAddQTY = findViewById(R.id.purchase_overlay_add_quantity);
        purchaseOverlaySubtractQTY = findViewById(R.id.purchase_overlay_subtract_quantity);
        purchaseOverlayQTYSlider = findViewById(R.id.purchase_overlay_quantity_slider);
        purchaseOverlayCost = findViewById(R.id.purchase_overlay_cost);
        purchaseOverlayCancel = findViewById(R.id.purchase_overlay_cancel);
        purchaseOverlayConfirm = findViewById(R.id.purchase_overlay_confirm);

        factoryName.setText(playerfactory.getFactoryName());
        itemList.setHasFixedSize(true);
        playerFactoryUpdates = new PlayerFactoryUpdates(merkado.getPlayer().getServer(), factoryId);
        playerFactoryUpdates.startListener(pm -> {
            if (pm == null) {
                playerFactoryUpdates.stopListener();
                return;
            }
            playerfactory = pm;

            StoreDataFunctions.getMarketStandardList(merkado.getPlayer().getServer(), msl -> {
                if (msl == null) return;
                processPlayerFactory(msl, playerfactory);
                setUpView(playerfactory.getOnSale());
            });

        });

        purchaseOverlay.setVisibility(View.GONE);
        initializePlayerDataListener();
        playerBalanceView.setBalance(merkado.getPlayer().getMoney());

        merkado.getPlayerActionTask().setOnTaskSuccess(gameRewards ->
                RewardProcessor.processRewards(this,
                        merkado.getPlayer().getServer(),
                        merkado.getPlayerId(),
                        gameRewards));
    }

    private void initializePlayerDataListener() {
        merkado.setPlayerDataListener(player -> {
            // update player money
            playerBalanceView.setBalance(player.getMoney());
        });
    }

    private void setUpView(List<OnSale> onSaleList) {
        if (onSaleList == null || onSaleList.isEmpty()) {
            onSaleList = new ArrayList<>();
        }
        itemList.setLayoutManager(new LinearLayoutManager(this));
        storeViewAdapter = new StoreViewAdapter(this, onSaleList);
        itemList.setAdapter(storeViewAdapter);
        storeViewAdapter.setOnClickListener(this::setUpDetails);
        if (!onSaleList.isEmpty()) {
            itemListEmpty.setVisibility(View.GONE);
            itemList.setVisibility(View.VISIBLE);
            setUpDetails(onSaleList.get(0));
        } else {
            itemListEmpty.setVisibility(View.VISIBLE);
            itemList.setVisibility(View.GONE);
            clearUpDetails();
        }
    }

    private void setUpDetails(OnSale onSale) {
        itemDescriptionContainer.setVisibility(View.VISIBLE);
        itemDescriptionContainerEmpty.setVisibility(View.GONE);
        currentOnSale = onSale;
        ResourceData resourceData = InternalDataFunctions.getResourceData(onSale.getResourceId());
        if (resourceData != null)
            itemDescription.setText(resourceData.getDescription());
        itemName.setText(onSale.getItemName());
        int itemImageResource = GameResourceCaller.getResourcesImage(onSale.getResourceId());
        int itemTypeResource = GameResourceCaller.getResourceTypeBackgrounds(onSale.getType());
        itemPrice.setText(String.format(Locale.getDefault(), "P%.2f", onSale.getPrice()));
        itemImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), itemImageResource));
        itemBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), itemTypeResource));
        itemPurchaseButton.setOnClickListener(v -> buyItem(onSale, onSale.getOnSaleId()));
    }

    private void clearUpDetails() {
        itemDescriptionContainer.setVisibility(View.GONE);
        itemDescriptionContainerEmpty.setVisibility(View.VISIBLE);
    }

    private void processPlayerFactory(MarketStandardList marketStandardList, PlayerFactory playerFactory) {
        List<OnSale> onSaleList = playerFactory.getOnSale();
        List<OnSale> finalizedList = new ArrayList<>();
        for (OnSale onSale : onSaleList) {
            Float price = marketStandardList.getMarketPrice(onSale.getResourceId());
            if (price == null) continue; // do not display products with no price.
            onSale.setPrice(price);
            finalizedList.add(onSale);
        }
        playerFactory.setOnSale(finalizedList);
    }

    private void buyItem(OnSale onSale, Integer onSaleId) {
        // display purchase overlay
        purchaseOverlay.setVisibility(View.VISIBLE);

        // display details
        int itemImageResource = GameResourceCaller.getResourcesImage(onSale.getResourceId());
        purchaseOverlayQuantity = 1;

        purchaseOverlayItemName.setText(onSale.getItemName());
        purchaseOverlayImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), itemImageResource));
        purchaseOverlayItemQuantity.setText(StringProcessor.numberToSpacedString(purchaseOverlayQuantity));
        purchaseOverlayAddQTY.setOnClickListener(v -> quantityChange(purchaseOverlayQuantity + 1));
        purchaseOverlaySubtractQTY.setOnClickListener(v -> quantityChange(purchaseOverlayQuantity - 1));
        purchaseOverlayQTYSlider.setMax(onSale.getQuantity());
        purchaseOverlayQTYSlider.setMin(1);
        purchaseOverlayQTYSlider.setProgress(purchaseOverlayQuantity);
        purchaseOverlayQTYSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == purchaseOverlayQuantity) return; // do not update if the same.
                quantityChange(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        purchaseOverlayCancel.setOnClickListener(v -> {
            quantityChange(1);
            currentOnSale = null;
            purchaseOverlay.setVisibility(View.GONE);
        });
        purchaseOverlayConfirm.setOnClickListener(v -> {
            if (!playerMoneyIsEnough) {
                Toast.makeText(getApplicationContext(), "Not enough money!", Toast.LENGTH_SHORT).show();
                return;
            }
            StoreBuyingData storeBuyingData = new StoreBuyingData(
                    merkado.getPlayer().getServer(),
                    factoryId,
                    playerfactory.getFactoryOwner(),
                    merkado.getPlayerId(),
                    onSaleId,
                    purchaseOverlayQuantity,
                    currentOnSale
            );
            StoreDataFunctions.buyFromPlayerMarket(storeBuyingData).thenAccept(marketError -> {
                switch (marketError) {
                    case NOT_EXIST:
                        Toast.makeText(getApplicationContext(), "Product does not exist. Please check your purchase.", Toast.LENGTH_SHORT).show();
                        break;
                    case NOT_ENOUGH:
                        Toast.makeText(getApplicationContext(), "Product quantity not enough for your request. Please try again.", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        Toast.makeText(getApplicationContext(), "Purchase success. Thank you for buying!", Toast.LENGTH_SHORT).show();
                        new Handler().post(() ->
                                merkado.getPlayerActionTask().taskActivity(PlayerActions.Task.PlayerActivity.BUYING,
                                        purchaseOverlayQuantity,
                                        PlayerActions.Task.generateRequirementCodeFromResource(onSale.getResourceId())));
                        break;
                    case GENERAL_ERROR:
                        Toast.makeText(getApplicationContext(), "Error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                        break;
                }
                quantityChange(1);
                currentOnSale = null;
                purchaseOverlay.setVisibility(View.GONE);
            });

        });

        checkPlayerMoney(onSale.getPrice());
    }

    private void quantityChange(Integer newQuantity) {
        if (currentOnSale == null) return;
        if (newQuantity == 0 || newQuantity > currentOnSale.getQuantity()) return;
        purchaseOverlayQuantity = newQuantity;
        purchaseOverlayItemQuantity.setText(StringProcessor.numberToSpacedString(purchaseOverlayQuantity));
        purchaseOverlayQTYSlider.setProgress(purchaseOverlayQuantity);
        if (newQuantity == 1 && purchaseOverlayQuantityButtonsMode != -1) {
            purchaseOverlayQuantityButtonsMode = -1;
            purchaseOverlaySubtractQTY.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.gray));
        } else if (newQuantity.equals(currentOnSale.getQuantity()) && purchaseOverlayQuantityButtonsMode != 1) {
            purchaseOverlayQuantityButtonsMode = 1;
            purchaseOverlayAddQTY.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.gray));
        } else if (purchaseOverlayQuantityButtonsMode != 0) {
            purchaseOverlayQuantityButtonsMode = 0;
            purchaseOverlaySubtractQTY.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
            purchaseOverlayAddQTY.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
        }
        purchaseOverlayFinalCost = currentOnSale.getPrice() * newQuantity;
        purchaseOverlayCost.setText(String.format(Locale.getDefault(), "%.2f", purchaseOverlayFinalCost));
        checkPlayerMoney(purchaseOverlayFinalCost);
    }

    private void checkPlayerMoney(Float cost) {
        if (merkado.getPlayer().getMoney() < cost) {
            // if player's money is not enough to cover the cost.
            if (!playerMoneyIsEnough) return;
            playerMoneyIsEnough = false;
            purchaseOverlayConfirm.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.merkado_orange_disabled));
        } else {
            if (playerMoneyIsEnough) return;
            playerMoneyIsEnough = true;
            purchaseOverlayConfirm.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.merkado_orange));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerFactoryUpdates.stopListener();
        merkado.setPlayerDataListener(null);
    }
}