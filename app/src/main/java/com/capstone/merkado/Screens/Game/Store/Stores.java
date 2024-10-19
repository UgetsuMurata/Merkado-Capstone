package com.capstone.merkado.Screens.Game.Store;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.Adapters.StoreProductAdapter;
import com.capstone.merkado.Adapters.StoresGridAdapter;
import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctionPackage.StoreDataFunctions;
import com.capstone.merkado.Objects.StoresDataObjects.PlayerMarkets;
import com.capstone.merkado.R;

import java.util.ArrayList;
import java.util.List;

public class Stores extends AppCompatActivity {

    Merkado merkado;
    RecyclerView storesGrid, storeProductPreview;
    StoresGridAdapter storesGridAdapter;
    StoreProductAdapter storeProductAdapter;
    TextView storeName, showNoProducts;
    LinearLayout showProducts;
    CardView goToStore;
    ImageView backButton;

    List<PlayerMarkets> playerMarkets;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gam_sto_stores);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        storesGrid = findViewById(R.id.stores_grid);
        storeProductPreview = findViewById(R.id.store_product_preview);
        storeName = findViewById(R.id.store_name);
        goToStore = findViewById(R.id.go_to_store);
        showProducts = findViewById(R.id.show_products);
        showNoProducts = findViewById(R.id.show_products_none);
        backButton = findViewById(R.id.back_button);

        playerMarkets = new ArrayList<>();

        // Set up the market grid
        storesGrid.setLayoutManager(new GridLayoutManager(this, 5));
        storesGridAdapter = new StoresGridAdapter(getApplicationContext(), playerMarkets);
        storesGrid.setAdapter(storesGridAdapter);
        storesGridAdapter.setOnClickListener(this::setUpStoreDetails);

        // get all markets
        StoreDataFunctions.getAllPlayerMarkets(merkado.getPlayer().getServer(), merkado.getPlayerId())
                .thenAccept(playerMarketsList -> {
                    playerMarkets.clear();
                    playerMarkets.addAll(playerMarketsList);
                    if (!playerMarkets.isEmpty()) setUpStoreDetails(playerMarkets.get(0), playerMarkets.get(0).getMarketId());
                    storesGridAdapter.notifyDataSetChanged();
                });
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void setUpStoreDetails(PlayerMarkets playerMarket, Integer marketId) {
        // store name
        storeName.setText(playerMarket.getStoreName());

        if (playerMarket.getOnSale() == null || playerMarket.getOnSale().isEmpty()) {
            showNoProducts.setVisibility(View.VISIBLE);
            showProducts.setVisibility(View.GONE);
        } else {
            showNoProducts.setVisibility(View.GONE);
            showProducts.setVisibility(View.VISIBLE);
            // Set up product preview
            storeProductPreview.setLayoutManager(new GridLayoutManager(this, 3));
            storeProductAdapter = new StoreProductAdapter(getApplicationContext(), playerMarket.getOnSale());
            storeProductPreview.setAdapter(storeProductAdapter);
        }
        // setup click
        goToStore.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), StoreConsumerView.class);
            intent.putExtra("PLAYER_MARKET", playerMarket);
            intent.putExtra("MARKET_ID", marketId);
            startActivity(intent);
        });
    }

}