package com.capstone.merkado.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.CustomViews.WoodenButton;
import com.capstone.merkado.DataManager.DataFunctionPackage.FactoryDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.InventoryDataFunctions;
import com.capstone.merkado.DataManager.StaticData.UpgradeBoosters;
import com.capstone.merkado.DataManager.StaticData.UpgradeBoosters.Booster;
import com.capstone.merkado.Helpers.InventoryHelper;
import com.capstone.merkado.Objects.FactoryDataObjects.FactoryTypes;
import com.capstone.merkado.Objects.ResourceDataObjects.Inventory;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceCount;
import com.capstone.merkado.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BoosterUpgradePopupAdapter {

    Context context;
    Integer playerId;

    MaterialCardView layout;
    FactoryTypes factoryTypes;
    List<Inventory> inventoryContents;
    Booster booster;
    Long currentLevel;
    Long nextLevel;
    OnEvent onEvent;
    BoosterUpgradeRequirementsAdapter upgradeReqAdapter;
    List<ResourceCount> resourceCountList;

    Long confirmedLevel, confirmedValue;

    TextView boosterName, currentValue, nextValue, currentLevelText, nextLevelText, cancelUpgrade;
    RecyclerView requirementsList;
    WoodenButton confirmUpgrade;

    public BoosterUpgradePopupAdapter(Activity activity, Integer playerId, MaterialCardView layout, FactoryTypes factoryTypes) {
        this.layout = layout;
        this.factoryTypes = factoryTypes;
        this.playerId = playerId;

        Context context = activity.getApplicationContext();
        this.context = context;

        boosterName = this.layout.findViewById(R.id.booster_name);
        currentValue = this.layout.findViewById(R.id.current_value);
        nextValue = this.layout.findViewById(R.id.next_value);
        currentLevelText = this.layout.findViewById(R.id.current_level);
        nextLevelText = this.layout.findViewById(R.id.next_level);
        requirementsList = this.layout.findViewById(R.id.requirements_list);
        cancelUpgrade = this.layout.findViewById(R.id.cancel_upgrade);
        confirmUpgrade = this.layout.findViewById(R.id.confirm_upgrade);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        upgradeReqAdapter = new BoosterUpgradeRequirementsAdapter(context);
        requirementsList.setLayoutManager(llm);
        requirementsList.setAdapter(upgradeReqAdapter);

        cancelUpgrade.setOnClickListener(v -> hide());
        confirmUpgrade.setOnClickListener(v -> confirm());

        upgradeReqAdapter.setOnEvent(resourceId -> {
            confirmUpgrade.setOnClickListener(null);
            activity.runOnUiThread(() -> confirmUpgrade.disable());
        });

    }

    public void setInventoryContents(List<Inventory> inventoryContents) {
        this.inventoryContents = inventoryContents;
        upgradeReqAdapter.setInventoryContents(this.inventoryContents);
    }

    public void setBooster(Booster booster, Long currentLevel) {
        this.booster = booster;
        this.currentLevel = currentLevel;
        this.nextLevel = currentLevel >= 5 ? null : currentLevel + 1;
        updateView();
    }

    public void setOnEvent(OnEvent onEvent) {
        this.onEvent = onEvent;
    }

    private void updateView() {
        Long currentVal = UpgradeBoosters.getBoosterLevelValue(this.booster, this.currentLevel);
        Long nextVal = UpgradeBoosters.getBoosterLevelValue(this.booster, this.nextLevel);
        confirmedValue = nextVal;
        confirmedLevel = this.nextLevel;
        boosterName.setText(this.booster.name);
        currentValue.setText(UpgradeBoosters.getBoosterUnit(this.booster, currentVal));
        nextValue.setText(UpgradeBoosters.getBoosterUnit(this.booster, nextVal));
        currentLevelText.setText(String.format(Locale.getDefault(), "%d", this.currentLevel));
        nextLevelText.setText(String.format(Locale.getDefault(), "%d", this.nextLevel));

        resourceCountList = UpgradeBoosters.getItemsRequired(this.booster, this.factoryTypes, this.nextLevel);
        upgradeReqAdapter.setResourceCountList(resourceCountList);
    }

    private void hide() {
        layout.setVisibility(View.GONE);
    }

    public void show() {
        layout.setVisibility(View.VISIBLE);
    }

    private void confirm() {
        hide();
        List<Inventory> newInventoryList = new ArrayList<>();
        for (ResourceCount resourceCount : resourceCountList) {
            Inventory currentInventoryItem = InventoryHelper.finder(inventoryContents, resourceCount.getResourceId());
            if (currentInventoryItem == null) {
                Toast.makeText(context, "You have a missing item.", Toast.LENGTH_SHORT).show();
                return;
            }

            Integer currentQTY = currentInventoryItem.getQuantity();
            if (currentQTY < resourceCount.getQuantity()) {
                Toast.makeText(context, "You don't have enough items.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentQTY - resourceCount.getQuantity() == 0) currentInventoryItem.setQuantity(0);
            else currentInventoryItem.setQuantity(Math.toIntExact(resourceCount.getQuantity() * -1));
            newInventoryList.add(currentInventoryItem);
        }
        for (Inventory inventoryItem : newInventoryList) {
            if (inventoryItem.getQuantity() == 0)
                InventoryDataFunctions.removeInventoryItem(playerId, inventoryItem.getResourceId());
            else
                InventoryDataFunctions.setInventoryItem(inventoryItem, this.playerId);
        }
        FactoryDataFunctions.updateFactoryBoosterLevel(playerId, this.booster, confirmedLevel);
        if (onEvent != null) onEvent.onConfirm(this.booster, confirmedLevel, confirmedValue);
    }

    public interface OnEvent {
        void onConfirm(Booster booster, Long currentLevel, Long currentValue);
    }
}
