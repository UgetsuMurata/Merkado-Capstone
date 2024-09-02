package com.capstone.merkado.DataManager.StaticData;

import static com.capstone.merkado.Objects.ResourceDataObjects.RESOURCES.COAL;
import static com.capstone.merkado.Objects.ResourceDataObjects.RESOURCES.GEMS;
import static com.capstone.merkado.Objects.ResourceDataObjects.RESOURCES.GOLD_ORE;
import static com.capstone.merkado.Objects.ResourceDataObjects.RESOURCES.GRAIN;
import static com.capstone.merkado.Objects.ResourceDataObjects.RESOURCES.IRON_INGOTS;
import static com.capstone.merkado.Objects.ResourceDataObjects.RESOURCES.METAL_SCRAPS;
import static com.capstone.merkado.Objects.ResourceDataObjects.RESOURCES.OIL_BARRELS;
import static com.capstone.merkado.Objects.ResourceDataObjects.RESOURCES.SILK;
import static com.capstone.merkado.Objects.ResourceDataObjects.RESOURCES.TIMBER;
import static com.capstone.merkado.Objects.ResourceDataObjects.RESOURCES.TOOLBOX;

import androidx.annotation.Nullable;

import com.capstone.merkado.Objects.FactoryDataObjects.FactoryTypes;
import com.capstone.merkado.Objects.ResourceDataObjects.RESOURCES;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceCount;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UpgradeBoosters {

    public static @Nullable List<ResourceCount> getItemsRequired(Booster booster, FactoryTypes factoryTypes, Long nextLevel) {
        switch (booster) {
            case ProductPerTap:
                return getProductPerTapRequirements(factoryTypes, nextLevel);
            case MaximumEnergy:
                return getMaximumEnergyRequirements(factoryTypes, nextLevel);
            case EnergyRecharge:
                return getEnergyRechargeRequirements(factoryTypes, nextLevel);
        }
        return null;
    }

    public static @Nullable Long getBoosterLevelValue(Booster booster, Long level) {
        switch (booster) {
            case ProductPerTap:
                return getProductPerTapLevelValue(level);
            case MaximumEnergy:
                return getMaximumEnergyLevelValue(level);
            case EnergyRecharge:
                return getEnergyRechargeLevelValue(level);
        }
        return null;
    }

    public static @Nullable String getBoosterUnit(Booster booster, Long value) {
        switch (booster) {
            case ProductPerTap:
                return String.format(Locale.getDefault(), "%dppt", value);
            case MaximumEnergy:
                return String.format(Locale.getDefault(), "%d⚡", value);
            case EnergyRecharge:
                return getEnergyRechargeUnit(value);
        }
        return null;
    }

    private static String getEnergyRechargeUnit(Long energyRecharge) {
        long totalSeconds = energyRecharge / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        StringBuilder timeString = new StringBuilder();

        if (minutes > 0) {
            String minuteString = minutes == 1 ? "minute" : "minutes";
            timeString.append(minutes).append(" ").append(minuteString).append(" ");
        }

        if (seconds > 0 || minutes == 0) {
            String secondString = seconds == 1 ? "second" : "seconds";
            timeString.append(seconds).append(" ").append(secondString);
        }

        return timeString.toString().trim();
    }

    public static Long getProductPerTapLevelValue(Long level) {
        if (level == 1) return 2L;
        if (level == 2) return 3L;
        if (level == 3) return 4L;
        if (level == 4) return 5L;
        if (level == 5) return 6L;
        return 1L; // level 0
    }

    public static Long getMaximumEnergyLevelValue(Long level) {
        if (level == 1) return 200L;
        if (level == 2) return 300L;
        if (level == 3) return 500L;
        if (level == 4) return 1000L;
        if (level == 5) return 1500L;
        return 150L; // level 0
    }

    public static Long getEnergyRechargeLevelValue(Long level) {
        if (level == 1) return (1000L * 60 * 4) + (1000L * 30);
        if (level == 2) return (1000L * 60 * 3) + (1000L * 30);
        if (level == 3) return (1000L * 60 * 2) + (1000L * 30);
        if (level == 4) return 1000L * 60;
        if (level == 5) return 1000L * 10;
        return 1000L * 60 * 5; // level 0
    }

    private static @Nullable List<ResourceCount> getProductPerTapRequirements(FactoryTypes factoryTypes, Long nextLevel) {
        List<ResourceCount> resourceCounts = new ArrayList<>();
        RESOURCES SSUI = FactoryTypes.FOOD.equals(factoryTypes) ? GRAIN : TIMBER;
        if (nextLevel == 1) {
            resourceCounts.add(new ResourceCount(SSUI.value, 30L));
            resourceCounts.add(new ResourceCount(COAL.value, 50L));
        } else if (nextLevel == 2) {
            resourceCounts.add(new ResourceCount(SSUI.value, 50L));
            resourceCounts.add(new ResourceCount(COAL.value, 100L));
            resourceCounts.add(new ResourceCount(OIL_BARRELS.value, 10L));
        } else if (nextLevel == 3) {
            resourceCounts.add(new ResourceCount(SSUI.value, 60L));
            resourceCounts.add(new ResourceCount(OIL_BARRELS.value, 50L));
            resourceCounts.add(new ResourceCount(IRON_INGOTS.value, 10L));
        } else if (nextLevel == 4) {
            resourceCounts.add(new ResourceCount(SSUI.value, 80L));
            resourceCounts.add(new ResourceCount(IRON_INGOTS.value, 50L));
            resourceCounts.add(new ResourceCount(SILK.value, 20L));
        } else if (nextLevel == 5) {
            resourceCounts.add(new ResourceCount(SSUI.value, 90L));
            resourceCounts.add(new ResourceCount(SILK.value, 90L));
            resourceCounts.add(new ResourceCount(GEMS.value, 5L));
        }
        return resourceCounts.isEmpty() ? null : resourceCounts;
    }

    private static @Nullable List<ResourceCount> getMaximumEnergyRequirements(FactoryTypes factoryTypes, Long nextLevel) {
        List<ResourceCount> resourceCounts = new ArrayList<>();
        RESOURCES SSUI = FactoryTypes.FOOD.equals(factoryTypes) ? GRAIN : TIMBER;
        if (nextLevel == 1) {
            resourceCounts.add(new ResourceCount(SSUI.value, 30L));
            resourceCounts.add(new ResourceCount(COAL.value, 50L));
        } else if (nextLevel == 2) {
            resourceCounts.add(new ResourceCount(SSUI.value, 50L));
            resourceCounts.add(new ResourceCount(COAL.value, 100L));
            resourceCounts.add(new ResourceCount(TOOLBOX.value, 10L));
        } else if (nextLevel == 3) {
            resourceCounts.add(new ResourceCount(SSUI.value, 60L));
            resourceCounts.add(new ResourceCount(TOOLBOX.value, 50L));
            resourceCounts.add(new ResourceCount(METAL_SCRAPS.value, 10L));
        } else if (nextLevel == 4) {
            resourceCounts.add(new ResourceCount(SSUI.value, 80L));
            resourceCounts.add(new ResourceCount(METAL_SCRAPS.value, 50L));
            resourceCounts.add(new ResourceCount(GOLD_ORE.value, 10L));
        } else if (nextLevel == 5) {
            resourceCounts.add(new ResourceCount(SSUI.value, 90L));
            resourceCounts.add(new ResourceCount(GOLD_ORE.value, 60L));
            resourceCounts.add(new ResourceCount(GEMS.value, 5L));
        }
        return resourceCounts.isEmpty() ? null : resourceCounts;
    }

    private static @Nullable List<ResourceCount> getEnergyRechargeRequirements(FactoryTypes factoryTypes, Long nextLevel) {
        List<ResourceCount> resourceCounts = new ArrayList<>();
        RESOURCES SSUI = FactoryTypes.FOOD.equals(factoryTypes) ? GRAIN : TIMBER;
        if (nextLevel == 1) {
            resourceCounts.add(new ResourceCount(SSUI.value, 30L));
            resourceCounts.add(new ResourceCount(COAL.value, 50L));
        } else if (nextLevel == 2) {
            resourceCounts.add(new ResourceCount(SSUI.value, 60L));
            resourceCounts.add(new ResourceCount(COAL.value, 120L));
            resourceCounts.add(new ResourceCount(OIL_BARRELS.value, 10L));
        } else if (nextLevel == 3) {
            resourceCounts.add(new ResourceCount(SSUI.value, 80L));
            resourceCounts.add(new ResourceCount(OIL_BARRELS.value, 50L));
            resourceCounts.add(new ResourceCount(METAL_SCRAPS.value, 30L));
        } else if (nextLevel == 4) {
            resourceCounts.add(new ResourceCount(SSUI.value, 100L));
            resourceCounts.add(new ResourceCount(METAL_SCRAPS.value, 60L));
            resourceCounts.add(new ResourceCount(GOLD_ORE.value, 50L));
        } else if (nextLevel == 5) {
            resourceCounts.add(new ResourceCount(SSUI.value, 200L));
            resourceCounts.add(new ResourceCount(GOLD_ORE.value, 120L));
            resourceCounts.add(new ResourceCount(GEMS.value, 20L));
        }
        return resourceCounts.isEmpty() ? null : resourceCounts;
    }

    public enum Booster {
        ProductPerTap("Product Per Tap"),
        MaximumEnergy("Maximum Energy"),
        EnergyRecharge("Energy Recharge");


        public final String name;

        Booster(String name) {
            this.name = name;
        }
    }
}
