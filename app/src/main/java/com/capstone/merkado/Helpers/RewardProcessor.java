package com.capstone.merkado.Helpers;

import androidx.annotation.Nullable;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctionPackage.InternalDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.InventoryDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.PlayerDataFunctions;
import com.capstone.merkado.DataManager.StaticData.LevelMaxSetter;
import com.capstone.merkado.DataManager.ValueReturn.ValueReturn;
import com.capstone.merkado.Objects.ResourceDataObjects.Inventory;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceData;
import com.capstone.merkado.Objects.StoryDataObjects.Chapter;

import java.util.ArrayList;
import java.util.List;

public class RewardProcessor {
    public static void processRewards(Integer playerId, List<Chapter.GameRewards> rewards) {
        List<Chapter.GameRewards> remainingRewards = new ArrayList<>(specialRewards(playerId, rewards));

        for (Chapter.GameRewards reward : remainingRewards) {
            // get resource data
            ResourceData resourceData =
                    InternalDataFunctions.getResourceData(reward.getResourceId());

            // create inventory instance
            Inventory inventory = new Inventory();
            inventory.setResourceId(Math.toIntExact(reward.getResourceId()));
            inventory.setQuantity(Math.toIntExact(reward.getResourceQuantity()));
            inventory.setType(resourceData.getType());
            inventory.setSellable(resourceData.getSellable());

            // add to inventory
            InventoryDataFunctions.setInventoryItem(inventory, playerId);
        }
    }

    private static List<Chapter.GameRewards> specialRewards(Integer playerId, List<Chapter.GameRewards> rewards) {
        List<Chapter.GameRewards> gameRewardsCopy = new ArrayList<>();
        for (Chapter.GameRewards reward : rewards) {
            Long rewardId = reward.getResourceId();
            if (rewardId == 1) {
                Long quantity = reward.getResourceQuantity();
                Long currentExp = Merkado.getInstance().getPlayer().getExp();
                PlayerDataFunctions.addPlayerExperience(playerId, quantity,
                        totalExp -> playerLevelTriggers(playerId, totalExp, currentExp));
            } else {
                gameRewardsCopy.add(reward);
            }
        }
        return gameRewardsCopy;
    }

    private static void playerLevelTriggers(Integer playerId, Long totalExp, Long currentExp) {
        Long maxLevel = LevelMaxSetter.getMaxPlayerExperience(totalExp);
        Long prevMaxLevel = LevelMaxSetter.getMaxPlayerExperience(currentExp);
        Integer playerLevel = LevelMaxSetter.getPlayerLevel(maxLevel);
        // check if playerLevel changed.
        if (maxLevel > prevMaxLevel)
            StoryTriggers.checkForLevelTriggers(playerId, playerLevel);
    }
}
