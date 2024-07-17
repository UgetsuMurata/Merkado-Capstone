package com.capstone.merkado.Screens.Game.Inventory;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.Adapters.InventoryAdapter;
import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctionPackage.DataFunctions;
import com.capstone.merkado.DataManager.StaticData.GameResourceCaller;
import com.capstone.merkado.Helpers.StringProcessor;
import com.capstone.merkado.Objects.ResourceDataObjects.Inventory;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceData;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceDisplayMode;
import com.capstone.merkado.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InventoryActivity extends AppCompatActivity {

    Merkado merkado;

    // SIDEBAR
    ImageView sbInventoryShowCollectibles, sbInventoryShowEdibles, sbInventoryShowResources;

    // CONTENTS
    ImageView cResourceGroupIcon;
    TextView cResourceGroup, cInventoryListEmpty;
    RecyclerView cInventoryList;

    // DESCRIPTION
    ScrollView dDescriptionContainer;
    TextView dResourceName, dResourceDescription, dUseButton, dDescriptionContainerEmpty;
    ImageView dResourceImage, dResourceImageBG;

    // VARIABLES
    ResourceDisplayMode currentMode;
    List<Inventory> inventoryList;
    LinkedHashMap<Integer, Inventory> inventoryMap;
    InventoryAdapter inventoryAdapter;
    DataFunctions.InventoryUpdates inventoryUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gam_inv_inventory);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        initializeViews();

        inventoryUpdates = new DataFunctions.InventoryUpdates(merkado.getPlayerId());
        inventoryUpdates.startListener(inventoryList -> {
            if (inventoryList == null) return;
            this.inventoryList = inventoryList;
            this.inventoryMap = mapInventoryList(inventoryList);
            filterInventoryAndShow(currentMode);
        });
    }

    /**
     * Initializes all views.
     */
    private void initializeViews() {
        // initialize main views
        initializeSidebar();
        initializeContents();
        initializeDescription();
    }

    /**
     * Initialized the sidebar, as well as assigns its onClick callbacks.
     */
    private void initializeSidebar() {
        // find views
        sbInventoryShowCollectibles = findViewById(R.id.inventory_show_collectibles);
        sbInventoryShowEdibles = findViewById(R.id.inventory_show_edibles);
        sbInventoryShowResources = findViewById(R.id.inventory_show_resources);

        // set onClick callbacks
        sbInventoryShowCollectibles.setOnClickListener(v -> {
            if (currentMode == ResourceDisplayMode.COLLECTIBLES) return;
            filterInventoryAndShow(ResourceDisplayMode.COLLECTIBLES);
        });
        sbInventoryShowEdibles.setOnClickListener(v -> {
            if (currentMode == ResourceDisplayMode.EDIBLES) return;
            filterInventoryAndShow(ResourceDisplayMode.EDIBLES);
        });
        sbInventoryShowResources.setOnClickListener(v -> {
            if (currentMode == ResourceDisplayMode.RESOURCES) return;
            filterInventoryAndShow(ResourceDisplayMode.RESOURCES);
        });
    }

    /**
     * Initializes the contents views, retrieves the player inventory from Firebase RTDB, and calls {@code filterInventoryAndShow()}.
     */
    private void initializeContents() {
        // find views
        cResourceGroupIcon = findViewById(R.id.resource_group_icon);
        cResourceGroup = findViewById(R.id.resource_group);
        cInventoryList = findViewById(R.id.inventory_list);
        cInventoryListEmpty = findViewById(R.id.inventory_list_empty);

        DataFunctions.getPlayerInventory(merkado.getPlayerId()).thenAccept(inventoryList -> {
            this.inventoryList = inventoryList;
            this.inventoryMap = mapInventoryList(inventoryList);
            filterInventoryAndShow(ResourceDisplayMode.COLLECTIBLES);
        });
    }

    /**
     * Initializes the description views.
     */
    private void initializeDescription() {
        dDescriptionContainer = findViewById(R.id.item_description_container);
        dResourceName = findViewById(R.id.resource_name);
        dResourceImage = findViewById(R.id.resource_image);
        dResourceImageBG = findViewById(R.id.resource_image_bg);
        dResourceDescription = findViewById(R.id.resource_description);
        dUseButton = findViewById(R.id.use_button);
        dDescriptionContainerEmpty = findViewById(R.id.item_description_container_empty);
    }

    /**
     * Converts the <i>inventoryList</i> into a LinkedHashMap, keeping its original index in the original inventoryList.
     * @param inventoryList original inventoryList.
     * @return {@code LinkedHashMap<Integer, Inventory>}
     */
    private LinkedHashMap<Integer, Inventory> mapInventoryList(List<Inventory> inventoryList) {
        LinkedHashMap<Integer, Inventory> mappedInventory = new LinkedHashMap<>();
        for (int i = 0; i < inventoryList.size(); i++) {
            mappedInventory.put(i, inventoryList.get(i));
        }
        return mappedInventory;
    }

    /**
     * Filters the inventory display to show only those that fits the resource type.
     * @param mode resource type.
     */
    private void filterInventoryAndShow(ResourceDisplayMode mode) {
        currentMode = mode;

        // filter inventoryList and turn it into LinkedHashMap.
        LinkedHashMap<Integer, Inventory> filteredMap = new LinkedHashMap<>();
        switch (mode) {
            case COLLECTIBLES:
                filteredMap = inventoryMap.entrySet()
                        .stream()
                        .filter(entry -> "COLLECTIBLE".equals(entry.getValue().getType()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e1,
                                LinkedHashMap::new
                        ));
                break;
            case EDIBLES:
                filteredMap = inventoryMap.entrySet()
                        .stream()
                        .filter(entry -> "EDIBLE".equals(entry.getValue().getType()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e1,
                                LinkedHashMap::new
                        ));
                break;
            case RESOURCES:
                filteredMap = inventoryMap.entrySet()
                        .stream()
                        .filter(entry -> "RESOURCE".equals(entry.getValue().getType()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e1,
                                LinkedHashMap::new
                        ));
                break;
        }
        showContents(filteredMap, currentMode);
    }

    /**
     * This shows the contents in a grid form.
     * @param mappedInventory Map of inventory.
     * @param mode Resource type.
     */
    private void showContents(LinkedHashMap<Integer, Inventory> mappedInventory, ResourceDisplayMode mode) {
        cResourceGroup.setText(StringProcessor.titleCase(mode.toString()));
        cResourceGroupIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                GameResourceCaller.getResourceTypeIcons(mode.toString())));

        cInventoryList.setLayoutManager(new GridLayoutManager(this, 5));
        inventoryAdapter = new InventoryAdapter(this, mappedInventory);
        cInventoryList.setAdapter(inventoryAdapter);

        if (mappedInventory.size() > 0) {
            cInventoryListEmpty.setVisibility(View.GONE);
            cInventoryList.setVisibility(View.VISIBLE);

            cInventoryList.setLayoutManager(new GridLayoutManager(this, 5));
            inventoryAdapter = new InventoryAdapter(this, mappedInventory);
            cInventoryList.setAdapter(inventoryAdapter);
            inventoryAdapter.setOnClickListener(this::verifyDetails);

            Integer firstKey = new ArrayList<>(mappedInventory.keySet()).get(0);
            verifyDetails(mappedInventory.get(firstKey), firstKey);
        } else {
            cInventoryListEmpty.setVisibility(View.VISIBLE);
            cInventoryList.setVisibility(View.GONE);
            verifyDetails(null, null);
        }
    }

    /**
     * This is the method used to verify the inventory data before displaying it. It retrieves the ResourceData needed for <i>showDetails</i> method.
     *
     * @param inventory inventory to be displayed.
     * @param index     index of the inventory in the <i>inventoryList</i>.
     */
    private void verifyDetails(@Nullable Inventory inventory, Integer index) {
        // if resource data are not yet retrieved, retrieve it and save it.
        if (inventory == null) noDetails();
        else if (inventory.getResourceData() == null) {
            DataFunctions.getResourceData(inventory.getResourceId()).thenAccept(resourceData -> {
                // retrieve and remove the inventory chosen
                Inventory inv = inventoryList.get(index);
                inventoryList.remove(inv);

                // add the new inventory data in the same index
                inv.setResourceData(resourceData);
                inventoryList.add(index, inv);
                showDetails(inv);
            });
        } else showDetails(inventory);
    }

    /**
     * This is where the displaying of data actually happens after verifying the inventory data.
     *
     * @param inventory inventory instance to be displayed.
     */
    private void showDetails(Inventory inventory) {
        dDescriptionContainerEmpty.setVisibility(View.GONE);
        dDescriptionContainer.setVisibility(View.VISIBLE);

        ResourceData resourceData = inventory.getResourceData();
        dResourceName.setText(resourceData.getName());
        dResourceDescription.setText(resourceData.getDescription());
        dResourceImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                GameResourceCaller.getResourcesImage(resourceData.getResourceId())));
        dResourceImageBG.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                GameResourceCaller.getResourceTypeBackgrounds(resourceData.getType())));
        dUseButton.setOnClickListener(v -> {
            // TODO: this. HAHAHA.
        });
    }

    /**
     * This is what will be shown if there are no details to be displayed.
     */
    private void noDetails() {
        dDescriptionContainerEmpty.setVisibility(View.VISIBLE);
        dDescriptionContainer.setVisibility(View.GONE);
    }
}