package com.capstone.merkado.Screens.Game.Sectors;

import static com.capstone.merkado.Screens.Game.Sectors.Factory.FactoryActivityMode.BOOSTER;
import static com.capstone.merkado.Screens.Game.Sectors.Factory.FactoryActivityMode.PRODUCT;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.Adapters.BoosterUpgradePopupAdapter;
import com.capstone.merkado.Adapters.FactoryChoiceAdapter;
import com.capstone.merkado.Adapters.FactoryChoiceAdapter.ReturnChoiceStatus;
import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.CustomViews.IconLevels;
import com.capstone.merkado.CustomViews.WoodenButton;
import com.capstone.merkado.DataManager.DataFunctionPackage.FactoryDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.FactoryDataFunctions.FactoryDataUpdates;
import com.capstone.merkado.DataManager.StaticData.GameResourceCaller;
import com.capstone.merkado.DataManager.StaticData.LevelMaxSetter;
import com.capstone.merkado.DataManager.StaticData.UpgradeBoosters;
import com.capstone.merkado.DataManager.StaticData.UpgradeBoosters.Booster;
import com.capstone.merkado.Objects.FactoryDataObjects.FactoryData.FactoryDetails;
import com.capstone.merkado.Objects.FactoryDataObjects.FactoryTypes;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceData;
import com.capstone.merkado.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Factory extends AppCompatActivity {
    Merkado merkado;

    // HEADER
    TextView factoryHeader;

    // SIDE MENU
    ImageView onProduction, backButton;
    RecyclerView productionChoices;

    // PANELS
    LinearLayout productPanel, boosterPanel;

    // CLICKER
    ImageView clicker, factoryBackground;
    WoodenButton boosterButton;

    // PLAYER DETAILS
    IconLevels proficiencyLevel, energyLevel;

    // UPGRADE
    WoodenButton pptUpgrade, meUpgrade, erUpgrade;
    TextView pptLevelText, pptValueText, meLevelText, meValueText, erLevelText, erValueText;
    BoosterUpgradePopupAdapter popupAdapter;

    // VARIABLES
    Boolean isFoodFactory = true;
    Integer currentProduction;
    ResourceData currentResourceData;
    FactoryDetails factoryDetails;
    FactoryChoiceAdapter factoryChoiceAdapter;
    List<ResourceData> resourceDataList;
    FactoryDataUpdates factoryDataUpdates;
    Long lastClick;
    Long clickCoolDown = 100L;
    Long energyRecharge = 10000L;
    Long energyLastRecharged;
    Boolean startEnergyRecharge = false;
    Handler energyRechargeHandler;
    Runnable energyRechargeRunnable;
    Handler clickHandler;
    Runnable clickUIRunnable, clickSaveRunnable;
    Long energyCount = 0L;
    Long energyCountLimit = 0L;
    Integer addedResource = 0;
    Integer resourcePerTap = 1;
    Integer idleClickerRes, activeClickerRes;
    FactoryActivityMode factoryActivityMode = PRODUCT;
    Long pptLevel, meLevel, erLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gam_sec_factory);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);
        backButton = findViewById(R.id.back_button);

        if (merkado.getPlayerData().getPlayerFactory() == null ||
                merkado.getPlayerData().getPlayerFactory().getFactoryMarketId() == null) {
            Toast.makeText(getApplicationContext(), "Cannot find factory ID. Please check if you claimed one.", Toast.LENGTH_SHORT).show();
            finish();
        }

        factoryDetails = merkado.getPlayerData().getPlayerFactory().getDetails();
        pptLevel = factoryDetails.getProductPerTap();
        meLevel = factoryDetails.getEnergyMax();
        erLevel = factoryDetails.getEnergyRecharge();
        isFoodFactory = Objects.equals(merkado.getPlayerData().getPlayerFactory().getFactoryType(),
                FactoryTypes.FOOD.toString());

        idleClickerRes = isFoodFactory ?
                R.drawable.gui_farming_scythe_idle :
                R.drawable.gui_manufacturing_lever_idle;
        activeClickerRes = isFoodFactory ?
                R.drawable.gui_farming_scythe_active :
                R.drawable.gui_manufacturing_lever_active;

        productPanel = findViewById(R.id.product_panel);
        boosterPanel = findViewById(R.id.booster_panel);
        onProduction = findViewById(R.id.on_production);
        productionChoices = findViewById(R.id.production_choices);
        clicker = findViewById(R.id.clicker);
        factoryBackground = findViewById(R.id.factory_background);
        energyLevel = findViewById(R.id.energy_level);
        proficiencyLevel = findViewById(R.id.proficiency_level);
        factoryHeader = findViewById(R.id.factory_header);
        boosterButton = findViewById(R.id.booster_button);

        pptUpgrade = findViewById(R.id.ppt_upgrade);
        pptLevelText = findViewById(R.id.ppt_level);
        pptValueText = findViewById(R.id.ppt_value);
        meUpgrade = findViewById(R.id.me_upgrade);
        meLevelText = findViewById(R.id.me_level);
        meValueText = findViewById(R.id.me_value);
        erUpgrade = findViewById(R.id.er_upgrade);
        erLevelText = findViewById(R.id.er_level);
        erValueText = findViewById(R.id.er_value);
        popupAdapter = new BoosterUpgradePopupAdapter(
                this,
                merkado.getPlayerId(),
                findViewById(R.id.layout_booster_upgrade_popup),
                isFoodFactory ? FactoryTypes.FOOD : FactoryTypes.MANUFACTURING);

        factoryHeader.setText(isFoodFactory ? "Food Factory" : "Manufacturing Factory");
        clicker.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), idleClickerRes));
        factoryBackground.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                isFoodFactory ?
                        R.drawable.bg_food_factory :
                        R.drawable.bg_manufacturing_factory));


        resourceDataList = new ArrayList<>();
        productionChoices.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        factoryChoiceAdapter = new FactoryChoiceAdapter(this);
        factoryChoiceAdapter.setOnChoiceSet(this::onChoiceSet);
        productionChoices.setAdapter(factoryChoiceAdapter);

        updateResourceList(FactoryDataFunctions.getFactoryChoices(isFoodFactory ? FactoryTypes.FOOD : FactoryTypes.MANUFACTURING));

        clicker.setOnClickListener(v -> generateResource());

        factoryDataUpdates = new FactoryDataUpdates(merkado.getPlayerId());
        factoryDataUpdates.startListener(this::updateFactoryDetails);

        lastClick = System.currentTimeMillis();
        clickHandler = new Handler();
        energyRechargeHandler = new Handler();
        clickUIRunnable = () -> { // initialize runnable.
        };
        clickSaveRunnable = () -> { // initialize runnable.
        };
        energyRechargeRunnable = () -> { // initialize runnable.
        };

        energyCount = factoryDetails.getEnergy();
        energyCountLimit = UpgradeBoosters.getMaximumEnergyLevelValue(factoryDetails.getEnergyMax());
        energyRecharge = UpgradeBoosters.getEnergyRechargeLevelValue(factoryDetails.getEnergyRecharge());
        resourcePerTap = Math.toIntExact(UpgradeBoosters.getProductPerTapLevelValue(factoryDetails.getProductPerTap()));
        energyLastRecharged = factoryDetails.getLastUsedEnergy();

        if (!energyCount.equals(energyCountLimit)) {
            energyCount += calculateAddedEnergy();
            if (energyCount > energyCountLimit) energyCount = energyCountLimit;
        }
        if (!energyCount.equals(energyCountLimit)) startEnergyRecharge();

        energyLevel.changeLimitValue(Math.toIntExact(energyCountLimit));
        updateEnergyLevel(energyCount);

        boosterButton.setOnClickListener(v ->
                changePanels(factoryActivityMode == BOOSTER ? PRODUCT : BOOSTER));

        setUpBoosterUpgrades();
        merkado.getPlayerData().setPlayerInventoryListener(inventories -> popupAdapter.setInventoryContents(inventories));
        popupAdapter.setInventoryContents(merkado.getPlayerData().getPlayerInventory());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private Long calculateAddedEnergy() {
        return (merkado.currentTimeMillis() - energyLastRecharged) / energyRecharge;
    }

    private void setOnProduction(ResourceData resourceData) {
        this.currentResourceData = resourceData;
        currentProduction = resourceData.getResourceId();
        onProduction.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), GameResourceCaller.getResourcesImage(currentProduction)));
        if (!Objects.equals(factoryDetails.getOnProduction(), currentProduction)) {
            factoryDetails.setOnProduction(resourceData.getResourceId());
            updateFactoryDetails(factoryDetails);
        }
    }

    private void updateEnergyLevel(Long energyCount) {
        energyLevel.changeCurrentValue(Math.toIntExact(energyCount));
    }

    private void updateFactoryDetails(@Nullable FactoryDetails factoryDetails) {
        if (factoryDetails == null) return;

        long prof = isFoodFactory ? factoryDetails.getFoodProficiency() : factoryDetails.getManufacturingProficiency();
        this.factoryDetails = factoryDetails;
        factoryChoiceAdapter.updateFactoryDetails(prof, factoryDetails.getOnProduction());
        proficiencyLevel.changeCurrentValue(Math.toIntExact(prof));
        proficiencyLevel.changeLimitValue(
                Math.toIntExact(LevelMaxSetter.getProficiencyMaxLevel(prof)));
    }

    private void updateProficiencyLevel(Long addedProficiency) {
        Long newProficiency = (isFoodFactory ? factoryDetails.getFoodProficiency() : factoryDetails.getManufacturingProficiency()) + addedProficiency;
        if (isFoodFactory) factoryDetails.setFoodProficiency(newProficiency);
        else factoryDetails.setManufacturingProficiency(newProficiency);
        updateFactoryDetails(factoryDetails);
    }

    private void updateResourceList(List<ResourceData> resourceDataList) {
        this.resourceDataList = resourceDataList;
        factoryChoiceAdapter.updateResourceData(resourceDataList);
    }

    private void onChoiceSet(ReturnChoiceStatus returnChoiceStatus, @Nullable ResourceData resourceData) {
        switch (returnChoiceStatus) {
            case NOT_ENOUGH_PROFICIENCY:
                Toast.makeText(getApplicationContext(), "You need more proficiency to unlock this item.", Toast.LENGTH_SHORT).show();
                break;
            case GENERAL_ERROR:
                Toast.makeText(getApplicationContext(), "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                break;
            case CREATED:
            case SUCCESS:
                if (resourceData == null) break;
                saveAddedResource();
                setOnProduction(resourceData);
                break;
        }
    }

    private void generateResource() {
        if (factoryActivityMode == BOOSTER) changePanels(PRODUCT);
        if (energyCount == 0 || energyCount < resourcePerTap) {
            Toast.makeText(getApplicationContext(), "Not enough energy!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (System.currentTimeMillis() - lastClick < clickCoolDown) {
            Toast.makeText(getApplicationContext(), "Don't click too fast!", Toast.LENGTH_SHORT).show();
            return;
        }
        lastClick = System.currentTimeMillis();
        clicker.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), activeClickerRes));
        clickHandler.removeCallbacks(clickUIRunnable);
        clickHandler.removeCallbacks(clickSaveRunnable);
        clickUIRunnable = () -> runOnUiThread(() -> clicker.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), idleClickerRes)));
        clickSaveRunnable = this::saveAddedResource;
        clickHandler.postDelayed(clickUIRunnable, 100);
        clickHandler.postDelayed(clickSaveRunnable, 2000);
        addedResource += resourcePerTap;
        energyCount -= resourcePerTap;
        Long addedProficiency = currentResourceData.getFactoryDefaults().getProficiencyReward() * resourcePerTap;
        startEnergyRecharge();
        updateProficiencyLevel(addedProficiency);
        updateEnergyLevel(energyCount);
    }

    private void saveAddedResource() {
        if (addedResource == 0) return;
        FactoryDataFunctions.addFactoryProducts(
                merkado.getPlayer().getServer(),
                merkado.getPlayer().getFactory().getFactoryMarketId(),
                currentResourceData.getResourceId(),
                ((long) addedResource)
        );
        addedResource = 0;
    }

    private void startEnergyRecharge() {
        if (startEnergyRecharge) return;
        startEnergyRecharge = true;
        energyLastRecharged = merkado.currentTimeMillis();
        energyRechargeRunnable = () -> {
            energyCount++;
            energyLastRecharged = merkado.currentTimeMillis();
            updateEnergyLevel(energyCount);
            if (energyCount.equals(energyCountLimit)) {
                startEnergyRecharge = false;
                return;
            }
            energyRechargeHandler.postDelayed(energyRechargeRunnable, energyRecharge);
        };
        energyRechargeHandler.postDelayed(energyRechargeRunnable, energyRecharge);
    }

    private void updateUserFactoryDetails() {
        factoryDetails.setLastUsedEnergy(energyLastRecharged);
        factoryDetails.setEnergy(energyCount);
        factoryDetails.setOnProduction(currentProduction);
        FactoryDataFunctions.updateFactoryDetails(factoryDetails, merkado.getPlayerId());
    }

    private void changePanels(FactoryActivityMode mode) {
        factoryActivityMode = mode;
        int startMargin = 0;
        int endMargin = 0;

        switch (mode) {
            case PRODUCT:
                endMargin = 30;
                break;
            case BOOSTER:
                startMargin = 30;
                break;
        }

        ValueAnimator animator = ValueAnimator.ofInt(startMargin, endMargin);
        animator.addUpdateListener(animation -> {
            int margin = (int) animation.getAnimatedValue();
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    margin
            );
            productPanel.setLayoutParams(param);
        });
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    private void setUpBoosterUpgrades() {
        pptUpgrade.setOnClickListener(v -> {
            popupAdapter.setBooster(Booster.ProductPerTap, pptLevel);
            popupAdapter.show();
        });
        updateBooster(Booster.ProductPerTap);
        meUpgrade.setOnClickListener(v -> {
            popupAdapter.setBooster(Booster.MaximumEnergy, meLevel);
            popupAdapter.show();
        });
        updateBooster(Booster.MaximumEnergy);
        erUpgrade.setOnClickListener(v -> {
            popupAdapter.setBooster(Booster.EnergyRecharge, erLevel);
            popupAdapter.show();
        });
        updateBooster(Booster.EnergyRecharge);
        popupAdapter.setOnEvent((booster, nextLevel, nextValue) -> {
            switch (booster) {
                case ProductPerTap:
                    pptLevel = nextLevel;
                    resourcePerTap = Math.toIntExact(nextValue);
                    break;
                case MaximumEnergy:
                    meLevel = nextLevel;
                    energyCountLimit = nextValue;
                    runOnUiThread(() -> energyLevel.changeLimitValue(Math.toIntExact(energyCountLimit)));
                    break;
                case EnergyRecharge:
                    erLevel = nextLevel;
                    energyRecharge = nextValue;
                    break;
            }
            updateBooster(booster);
        });
    }

    private void updateBooster(Booster booster) {
        switch (booster) {
            case ProductPerTap:
                runOnUiThread(() -> updateBoosterValues(booster, pptLevelText, pptValueText, pptLevel));
                break;
            case MaximumEnergy:
                runOnUiThread(() -> updateBoosterValues(booster, meLevelText, meValueText, meLevel));
                break;
            case EnergyRecharge:
                runOnUiThread(() -> updateBoosterValues(booster, erLevelText, erValueText, erLevel));
                break;
        }
    }

    private void updateBoosterValues(Booster booster, TextView level, TextView value, Long levelVal) {
        level.setText(String.valueOf(levelVal));
        Long valueVal = UpgradeBoosters.getBoosterLevelValue(booster, levelVal);
        if (Booster.EnergyRecharge.equals(booster))
            value.setText(UpgradeBoosters.getBoosterUnit(booster, valueVal));
        else value.setText(String.valueOf(valueVal));
    }

    /**
     * This is used to indicate which panel is currently displayed.
     */
    public enum FactoryActivityMode {
        /**
         * Product panel is displayed.
         */
        PRODUCT,
        /**
         * Booster panel is displayed.
         */
        BOOSTER
    }

    @Override
    protected void onDestroy() {
        if (factoryDataUpdates != null) factoryDataUpdates.stopListener();
        saveAddedResource();
        updateUserFactoryDetails();
        super.onDestroy();
    }
}