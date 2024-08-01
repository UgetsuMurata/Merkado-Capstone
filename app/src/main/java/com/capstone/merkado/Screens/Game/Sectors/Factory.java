package com.capstone.merkado.Screens.Game.Sectors;

import static com.capstone.merkado.Screens.Game.Sectors.Factory.FactoryActivityMode.BOOSTER;
import static com.capstone.merkado.Screens.Game.Sectors.Factory.FactoryActivityMode.PRODUCT;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
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

import com.capstone.merkado.Adapters.FactoryChoiceAdapter;
import com.capstone.merkado.Adapters.FactoryChoiceAdapter.ReturnChoiceStatus;
import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.CustomViews.IconLevels;
import com.capstone.merkado.CustomViews.WoodenButton;
import com.capstone.merkado.DataManager.DataFunctionPackage.DataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.DataFunctions.FactoryDataUpdates;
import com.capstone.merkado.DataManager.StaticData.GameResourceCaller;
import com.capstone.merkado.DataManager.StaticData.LevelMaxSetter;
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
    ImageView onProduction;
    RecyclerView productionChoices;

    // PANELS
    LinearLayout productPanel, boosterPanel;

    // CLICKER
    ImageView clicker, factoryBackground;
    WoodenButton boosterButton;

    // PLAYER DETAILS
    IconLevels proficiencyLevel, energyLevel;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gam_sec_factory);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        if (merkado.getPlayerData().getPlayerFactory() == null ||
                merkado.getPlayerData().getPlayerFactory().getFactoryMarketId() == null) {
            Toast.makeText(getApplicationContext(), "Cannot find factory ID. Please check if you claimed one.", Toast.LENGTH_SHORT).show();
            finish();
        }

        factoryDetails = merkado.getPlayerData().getPlayerFactory().getDetails();
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

        clicker.setOnClickListener(v -> generateResource());

        DataFunctions.getFactoryChoices(isFoodFactory ? FactoryTypes.FOOD : FactoryTypes.MANUFACTURING)
                .thenAccept(this::updateResourceList);
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
        energyCountLimit = factoryDetails.getEnergyMax();
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
    }

    private Long calculateAddedEnergy() {
        return (System.currentTimeMillis() - energyLastRecharged) / energyRecharge;
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
        startEnergyRecharge();
        updateProficiencyLevel(currentResourceData.getFactoryDefaults().getProficiencyReward());
        updateEnergyLevel(energyCount);
    }

    private void saveAddedResource() {
        if (addedResource == 0) return;
        DataFunctions.addFactoryProducts(
                getApplicationContext(),
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
        energyLastRecharged = System.currentTimeMillis();
        energyRechargeRunnable = () -> {
            energyCount++;
            energyLastRecharged = System.currentTimeMillis();
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
        DataFunctions.updateFactoryDetails(factoryDetails, merkado.getPlayerId());
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
        factoryDataUpdates.stopListener();
        saveAddedResource();
        updateUserFactoryDetails();
        super.onDestroy();
    }
}