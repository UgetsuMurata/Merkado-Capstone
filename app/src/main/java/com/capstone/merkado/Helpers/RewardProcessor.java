package com.capstone.merkado.Helpers;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctionPackage.InternalDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.InventoryDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.PlayerDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.ServerDataFunctions;
import com.capstone.merkado.DataManager.StaticData.LevelMaxSetter;
import com.capstone.merkado.Objects.ResourceDataObjects.Inventory;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceData;
import com.capstone.merkado.Objects.StoryDataObjects.Chapter;

import java.util.ArrayList;
import java.util.List;

public class RewardProcessor {
    public static void processRewards(Activity activity, String serverId, Integer playerId, @NonNull List<Chapter.GameRewards> rewards) {
        List<Chapter.GameRewards> remainingRewards = new ArrayList<>(specialRewards(activity, serverId, playerId, rewards));

        for (Chapter.GameRewards reward : remainingRewards) {
            // get resource data
            ResourceData resourceData =
                    InternalDataFunctions.getResourceData(reward.getResourceId());

            if (resourceData == null) continue;

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

    private static List<Chapter.GameRewards> specialRewards(Activity activity, String serverId, Integer playerId, @NonNull List<Chapter.GameRewards> rewards) {
        List<Chapter.GameRewards> gameRewardsCopy = new ArrayList<>();
        for (Chapter.GameRewards reward : rewards) {
            Long rewardId = reward.getResourceId();
            if (rewardId == 1) {
                Long quantity = reward.getResourceQuantity();
                Long currentExp = Merkado.getInstance().getPlayer().getExp();
                PlayerDataFunctions.addPlayerExperience(playerId, quantity,
                        totalExp -> playerLevelTriggers(activity, serverId, playerId, totalExp, currentExp));
            }
            else if (rewardId == 24) {
                Long quantity = reward.getResourceQuantity();
                PlayerDataFunctions.addPlayerMoney(playerId, quantity);
            }
            else {
                gameRewardsCopy.add(reward);
            }
        }
        return gameRewardsCopy;
    }

    private static void playerLevelTriggers(Activity activity, String serverId, Integer playerId, Long totalExp, Long currentExp) {
        Long maxLevel = LevelMaxSetter.getMaxPlayerExperience(totalExp);
        Long prevMaxLevel = LevelMaxSetter.getMaxPlayerExperience(currentExp);
        Integer playerLevel = LevelMaxSetter.getPlayerLevel(maxLevel);
        // check if playerLevel changed.
        if (maxLevel > prevMaxLevel) {
            ServerDataFunctions.getReachedLevels(serverId).thenAccept(reachedLevels -> {
                Integer totalCount = null;
                switch (playerLevel){
                    case 2:
                        totalCount = reachedLevels.getLvl2() + 1;
                        reachedLevels.setLvl2(totalCount);
                        break;
                    case 3:
                        totalCount = reachedLevels.getLvl3() + 1;
                        reachedLevels.setLvl3(totalCount);
                        break;
                    case 4:
                        totalCount = reachedLevels.getLvl4() + 1;
                        reachedLevels.setLvl4(totalCount);
                        break;
                }
                ServerDataFunctions.setReachedLevels(serverId, reachedLevels);
                TriggerProcessor.checkForLevelTriggers(serverId, playerId, playerLevel, totalCount);
                TriggerProcessor.objectives(activity, playerLevel, null);
            });
        }
    }
}
