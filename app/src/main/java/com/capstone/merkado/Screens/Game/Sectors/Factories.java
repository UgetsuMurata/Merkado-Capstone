package com.capstone.merkado.Screens.Game.Sectors;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.Adapters.FactoriesGridAdapter;
import com.capstone.merkado.Adapters.StoreProductAdapter;
import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.CustomViews.WoodenButton;
import com.capstone.merkado.DataManager.DataFunctionPackage.FactoryDataFunctions;
import com.capstone.merkado.Helpers.Bot;
import com.capstone.merkado.Objects.FactoryDataObjects.PlayerFactory;
import com.capstone.merkado.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public class Factories extends AppCompatActivity {

    Merkado merkado;

    RecyclerView factoriesGrid, factoryProductPreview;
    FactoriesGridAdapter factoriesGridAdapter;
    StoreProductAdapter factoryProductAdapter;
    TextView factoryName, factoryType, showNoProducts;
    LinearLayout showProducts;
    WoodenButton goToFactory;

    List<PlayerFactory> playerFactories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gam_sec_factories);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        factoriesGrid = findViewById(R.id.factories_grid);
        factoryProductPreview = findViewById(R.id.factory_product_display);
        factoryName = findViewById(R.id.factory_name);
        factoryType = findViewById(R.id.factory_type);
        goToFactory = findViewById(R.id.go_to_supplier);
        showProducts = findViewById(R.id.show_products);
        showNoProducts = findViewById(R.id.show_products_none);

        playerFactories = new ArrayList<>();

        // Set up the market grid
        factoriesGrid.setLayoutManager(new GridLayoutManager(this, 5));
        factoriesGridAdapter = new FactoriesGridAdapter(getApplicationContext(), playerFactories);
        factoriesGrid.setAdapter(factoriesGridAdapter);
        factoriesGridAdapter.setOnClickListener(this::setUpFactoryDetails);

        // get all markets
        FactoryDataFunctions.getAllPlayerFactory(merkado.getPlayer().getServer())
                .thenAccept(playerMarketsList -> {
                    playerFactories.clear();
                    playerFactories.addAll(playerMarketsList);
                    if (!playerFactories.isEmpty())
                        setUpFactoryDetails(playerFactories.get(0),
                                playerFactories.get(0).getFactoryId());
                    else Bot.createBotStores();
                    factoriesGridAdapter.notifyDataSetChanged();
                });
    }

    private void setUpFactoryDetails(PlayerFactory playerFactory, Integer marketId) {
        // store name
        factoryName.setText(playerFactory.getFactoryName());
        factoryType.setText(playerFactory.getFactoryType());

        if (playerFactory.getOnSale() == null || playerFactory.getOnSale().isEmpty()) {
            showNoProducts.setVisibility(View.VISIBLE);
            showProducts.setVisibility(View.GONE);
        } else {
            showNoProducts.setVisibility(View.GONE);
            showProducts.setVisibility(View.VISIBLE);
            // Set up product preview
            factoryProductPreview.setLayoutManager(new GridLayoutManager(this, 3));
            factoryProductAdapter = new StoreProductAdapter(getApplicationContext(), playerFactory.getOnSale(), false);
            factoryProductPreview.setAdapter(factoryProductAdapter);
        }

        // setup click
        goToFactory.setOnClickListener(v -> goToFactory(marketId, playerFactory));
    }

    private void goToFactory(Integer marketId, PlayerFactory playerFactory) {
        Intent intent = new Intent(getApplicationContext(), FactoryConsumerView.class);
        intent.putExtra("PLAYER_FACTORY", playerFactory);
        intent.putExtra("FACTORY_ID", marketId);
        startActivity(intent);
    }
}