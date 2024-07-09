package com.capstone.merkado.Screens.Game.Store;

import static com.capstone.merkado.Helpers.OtherProcessors.StoreProcessors.filterSaleList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.Adapters.StoreViewAdapter;
import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.CustomViews.PlayerBalanceView;
import com.capstone.merkado.DataManager.DataFunctions;
import com.capstone.merkado.DataManager.DataFunctions.PlayerMarketUpdates;
import com.capstone.merkado.DataManager.StaticData.GameResourceCaller;
import com.capstone.merkado.Helpers.StringProcessor;
import com.capstone.merkado.Objects.PlayerDataObjects.Player;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceDisplayMode;
import com.capstone.merkado.Objects.StoresDataObjects.PlayerMarkets;
import com.capstone.merkado.Objects.StoresDataObjects.PlayerMarkets.OnSale;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.Game.MainMap;

import java.util.List;
import java.util.Locale;

public class StoreSellerView extends AppCompatActivity {

    Merkado merkado;

    // HEADER
    TextView storeName;
    PlayerBalanceView currentBalance;

    // SIDEBAR
    ImageView sbStoreShowCollectibles, sbStoreShowEdibles, sbStoreShowResources;

    // CONTENTS
    TextView cItemCount, cAddItem, cItemListEmpty;
    RecyclerView cItemList;

    // DESCRIPTION
    ScrollView dDescriptionContainer;
    TextView dResourceName, dItemPrice, dResourceDescription,
            dItemRemove, dItemEdit, dDescriptionContainerEmpty;
    ImageView dResourceImage, dResourceImageBG;

    // LAYOUT SELL POPUP
    ConstraintLayout layoutSellPopup;
    TextView lspItemName, lspItemQuantity, lspItemMarketPrice, lspItemCancel, lspItemUpdate;
    ImageView lspItemImage, lspItemImageBG, lspItemQuantitySubtract, lspItemQuantityAdd;
    SeekBar lspItemQuantitySlider;
    EditText lspItemCost;

    // VARIABLES
    Player player;
    PlayerMarketUpdates playerMarketUpdates;
    PlayerMarkets playerMarkets;
    ResourceDisplayMode currentResourceDisplayMode;
    StoreViewAdapter storeViewAdapter;
    OnSale currentOnSale;
    Integer lspItemQuantityCount;
    Integer lspItemMaxQuantity;
    Integer lspQuantityButtonsMode = 0;
    Float LSPItemSetPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gam_sto_store_seller_view);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        storeName = findViewById(R.id.store_name);
        currentBalance = findViewById(R.id.player_current_balance);

        player = merkado.getPlayer();
        playerMarketUpdates = new PlayerMarketUpdates(player.getServer(), player.getMarketId());
        playerMarketUpdates.startListener(playerMarkets -> {
            if (playerMarkets == null)
                setPlayerMarkets(
                        DataFunctions.setUpPlayerMarket(player.getServer(), merkado.getAccount().getUsername(), merkado.getPlayerId()),
                        ResourceDisplayMode.COLLECTIBLES);
            else
                if (currentResourceDisplayMode == null) currentResourceDisplayMode = ResourceDisplayMode.COLLECTIBLES;
                setPlayerMarkets(playerMarkets, currentResourceDisplayMode);
        });
        initializeViews();

        layoutSellPopup.setVisibility(View.GONE);
        initializePlayerDataListener();
        currentBalance.setBalance(player.getMoney());
    }

    private void setPlayerMarkets(PlayerMarkets playerMarkets, ResourceDisplayMode mode) {
        this.playerMarkets = playerMarkets;
        // trigger an update to relevant views.
        setCurrentDisplayMode(mode);
    }

    private void initializeViews() {
        initializeTabViews();
        initializeContentViews();
        initializeDetailViews();
        initializeLSP();
    }

    private void initializeTabViews() {
        sbStoreShowCollectibles = findViewById(R.id.store_show_collectibles);
        sbStoreShowEdibles = findViewById(R.id.store_show_edibles);
        sbStoreShowResources = findViewById(R.id.store_show_resources);

        sbStoreShowCollectibles.setOnClickListener(v -> setCurrentDisplayMode(ResourceDisplayMode.COLLECTIBLES));
        sbStoreShowEdibles.setOnClickListener(v -> setCurrentDisplayMode(ResourceDisplayMode.EDIBLES));
        sbStoreShowResources.setOnClickListener(v -> setCurrentDisplayMode(ResourceDisplayMode.RESOURCES));
    }

    private void initializeContentViews() {
        cItemCount = findViewById(R.id.sale_count);
        cAddItem = findViewById(R.id.item_add);
        cItemList = findViewById(R.id.item_list);
        cItemListEmpty = findViewById(R.id.item_list_empty);

        addItem();
    }

    private void initializeDetailViews() {
        dDescriptionContainer = findViewById(R.id.item_description_container);
        dDescriptionContainerEmpty = findViewById(R.id.item_description_container_empty);
        dResourceName = findViewById(R.id.item_name);
        dItemPrice = findViewById(R.id.item_price);
        dResourceImage = findViewById(R.id.item_image);
        dResourceImageBG = findViewById(R.id.item_image_bg);
        dResourceDescription = findViewById(R.id.item_description);
        dItemRemove = findViewById(R.id.item_remove);
        dItemEdit = findViewById(R.id.item_edit);
    }

    private void addItem() {
        final ActivityResultLauncher<Intent> refreshAfterIntent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                o -> {
                    Intent i = new Intent(getApplicationContext(), StoreSellerView.class);
                    overridePendingTransition(0, 0);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                    finish();
                });
        cAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), StoreSellerSelectItem.class);
            intent.putExtra("PLAYER_MARKET", playerMarkets);
            refreshAfterIntent.launch(intent);
        });
    }

    private void initializeLSP() {
        layoutSellPopup = findViewById(R.id.layout_sell_popup);
        lspItemName = layoutSellPopup.findViewById(R.id.item_name);
        lspItemImage = layoutSellPopup.findViewById(R.id.item_image);
        lspItemImageBG = layoutSellPopup.findViewById(R.id.item_image_bg);
        lspItemQuantity = layoutSellPopup.findViewById(R.id.item_quantity);
        lspItemQuantitySubtract = layoutSellPopup.findViewById(R.id.quantity_subtract);
        lspItemQuantityAdd = layoutSellPopup.findViewById(R.id.quantity_add);
        lspItemQuantitySlider = layoutSellPopup.findViewById(R.id.quantity_slider);
        lspItemMarketPrice = layoutSellPopup.findViewById(R.id.item_market_price);
        lspItemCost = layoutSellPopup.findViewById(R.id.item_cost);
        lspItemCancel = layoutSellPopup.findViewById(R.id.item_cancel);
        lspItemUpdate = layoutSellPopup.findViewById(R.id.item_confirm);

        lspItemUpdate.setText("Update");
    }

    private void initializePlayerDataListener() {
        merkado.setPlayerDataListener(player -> {
            // update player money
            currentBalance.setBalance(player.getMoney());
            this.player = player;
        });
    }

    private void stopPlayerDataListener() {
        merkado.setPlayerDataListener(null);
    }

    private void setCurrentDisplayMode(ResourceDisplayMode resourceDisplayMode) {
        currentResourceDisplayMode = resourceDisplayMode;
        setUpView(playerMarkets.getOnSale(), currentResourceDisplayMode);
    }

    private void setUpView(List<OnSale> onSaleList, ResourceDisplayMode resourceDisplayMode) {
        storeName.setText(playerMarkets.getStoreName());
        List<OnSale> displayOnSale = filterSaleList(onSaleList, resourceDisplayMode);
        cItemCount.setText(StringProcessor.numberToSpacedString(displayOnSale.size()));
        cItemList.setLayoutManager(new LinearLayoutManager(this));
        storeViewAdapter = new StoreViewAdapter(this, displayOnSale);
        cItemList.setAdapter(storeViewAdapter);
        storeViewAdapter.setOnClickListener(this::setUpDetails);
        if (displayOnSale.size() > 0) {
            cItemListEmpty.setVisibility(View.GONE);
            cItemList.setVisibility(View.VISIBLE);
            setUpDetails(displayOnSale.get(0));
        } else {
            cItemListEmpty.setVisibility(View.VISIBLE);
            cItemList.setVisibility(View.GONE);
            switch (resourceDisplayMode) {
                case COLLECTIBLES:
                    cItemListEmpty.setText(String.format(getString(R.string.storeItemListEmptySeller), " collectibles"));
                    break;
                case EDIBLES:
                    cItemListEmpty.setText(String.format(getString(R.string.storeItemListEmptySeller), " edibles"));
                    break;
                case RESOURCES:
                    cItemListEmpty.setText(String.format(getString(R.string.storeItemListEmptySeller), " resources"));
                    break;
                default:
                    cItemListEmpty.setText(String.format(getString(R.string.storeItemListEmptySeller), "thing"));
            }
            clearUpDetails();
        }
    }

    private void setUpDetails(OnSale onSale) {
        dDescriptionContainer.setVisibility(View.VISIBLE);
        dDescriptionContainerEmpty.setVisibility(View.GONE);
        currentOnSale = onSale;
        DataFunctions.getResourceDataById(onSale.getResourceId()).thenAccept(resourceData -> {
            runOnUiThread(() -> dResourceDescription.setText(resourceData.getDescription()));
        });
        dResourceName.setText(onSale.getItemName());
        int itemImageResource = GameResourceCaller.getResourcesImage(onSale.getResourceId());
        int itemTypeResource = GameResourceCaller.getResourceTypeBackgrounds(onSale.getType());
        dItemPrice.setText(String.format(Locale.getDefault(), "P%.2f", onSale.getPrice()));
        dResourceImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), itemImageResource));
        dResourceImageBG.setBackground(ContextCompat.getDrawable(getApplicationContext(), itemTypeResource));
        dItemEdit.setOnClickListener(v -> editItem(onSale));
        dItemRemove.setOnClickListener(v -> {
            lspItemQuantityCount = 0;
            LSPItemSetPrice = onSale.getPrice();
            updateSale(onSale);
            Toast.makeText(getApplicationContext(), "Product removed from store and added to your inventory!", Toast.LENGTH_SHORT).show();
        });
    }

    private void clearUpDetails() {
        dDescriptionContainer.setVisibility(View.GONE);
        dDescriptionContainerEmpty.setVisibility(View.VISIBLE);
    }

    /**
     * LayoutSellPopup details inflater.
     *
     * @param onSale OnSale instance of the product to edit.
     */
    private void editItem(OnSale onSale) {
        layoutSellPopup.setVisibility(View.VISIBLE);

        // DETAILS
        lspItemName.setText(onSale.getItemName());
        lspItemImageBG.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                GameResourceCaller.getResourceTypeBackgrounds(onSale.getType())));
        lspItemImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                GameResourceCaller.getResourcesImage(onSale.getResourceId())));
        DataFunctions.getMarketPrice(player.getServer(), onSale.getResourceId())
                .thenAccept(
                        marketPrice -> {
                            lspItemMarketPrice.setText(
                                    String.format(Locale.getDefault(), "%.2f", marketPrice)
                            );
                            LSPItemSetPrice = marketPrice;
                        }
                );

        // SET QUANTITY
        DataFunctions.getInventoryItem(merkado.getPlayerId(), onSale.getResourceId()).thenAccept(inventory -> {
            lspItemMaxQuantity = onSale.getQuantity() + inventory.getQuantity();
            lspItemQuantitySlider.setMax(lspItemMaxQuantity);
            lspItemQuantitySlider.setMin(0);
            setLSPItemQuantity(onSale.getQuantity());
        });

        // CONTROLLERS
        lspItemQuantityAdd.setOnClickListener(v -> setLSPItemQuantity(lspItemQuantityCount + 1));
        lspItemQuantitySubtract.setOnClickListener(v -> setLSPItemQuantity(lspItemQuantityCount - 1));
        lspItemQuantitySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == lspItemQuantityCount) return; // no changes made.
                setLSPItemQuantity(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // OTHER INPUT
        lspItemCost.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (lspItemCost.getText() == null || lspItemCost.getText().toString().isEmpty())
                    return;
                String rawText = lspItemCost.getText().toString();
                try {
                    LSPItemSetPrice = Float.parseFloat(rawText);
                    lspItemCost.setText(String.format(Locale.getDefault(), "%.2f", Float.parseFloat(rawText)));
                } catch (NumberFormatException e) {
                    lspItemCost.setText(String.format(Locale.getDefault(), "%.2f", LSPItemSetPrice));
                    Toast.makeText(getApplicationContext(), "Invalid cost!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // BUTTONS
        lspItemCancel.setOnClickListener(v -> layoutSellPopup.setVisibility(View.GONE));
        lspItemUpdate.setOnClickListener(v -> {
            updateSale(onSale);
            Toast.makeText(getApplicationContext(), "Product updated successfully!", Toast.LENGTH_SHORT).show();
        });
    }

    private void setLSPItemQuantity(Integer qty) {
        if (qty == null) return;
        if (qty == 0 || qty > lspItemMaxQuantity) return;
        lspItemQuantityCount = qty;
        lspItemQuantitySlider.setProgress(lspItemQuantityCount);
        lspItemQuantity.setText(StringProcessor.numberToSpacedString(qty));

        if (qty == 1 && lspQuantityButtonsMode != -1) {
            lspQuantityButtonsMode = -1;
            lspItemQuantitySubtract.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.gray));
        } else if (qty.equals(lspItemMaxQuantity) && lspQuantityButtonsMode != 1) {
            lspQuantityButtonsMode = 1;
            lspItemQuantityAdd.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.gray));
        } else if (lspQuantityButtonsMode != 0) {
            lspQuantityButtonsMode = 0;
            lspItemQuantitySubtract.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
            lspItemQuantityAdd.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
        }
    }

    private void updateSale(OnSale onSale) {
        // update the OnSale object.
        onSale.setQuantity(lspItemQuantityCount - onSale.getQuantity());
        onSale.setPrice(LSPItemSetPrice);
        DataFunctions.editSale(onSale, player, merkado.getPlayerId());
        layoutSellPopup.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlayerDataListener();
    }
}