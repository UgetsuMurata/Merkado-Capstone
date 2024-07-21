package com.capstone.merkado.DataManager.StaticData;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LevelMaxSetter {
    public static Long getProficiencyMaxLevel(Long currentLevel) {
        List<Long> proficiencyMaxLevel = Arrays.asList(50L, 150L, 300L, 500L, 750L, 1000L, 1500L);
        return process(currentLevel,
                proficiencyMaxLevel,
                proficiencyMaxLevel.get(proficiencyMaxLevel.size() - 1));
    }

    public static Long getMaxPlayerExperience(Long currentLevel) {
        List<Long> playerMaxLevel = Arrays.asList(1200L, 2500L, 4100L, 6000L);
        return process(currentLevel,
                playerMaxLevel,
                playerMaxLevel.get(playerMaxLevel.size() - 1));
    }

    public static Long getPreviousMaxPlayerExperience(Long currentMaxLevel) {
        List<Long> playerMaxLevel = Arrays.asList(1200L, 2500L, 4100L, 6000L);
        int i = playerMaxLevel.indexOf(currentMaxLevel);
        if (i == 0) return 0L;
        return playerMaxLevel.get(i - 1);
    }

    public static Integer getPlayerLevel(Long currentMaxLevel) {
        List<Long> playerMaxLevel = Arrays.asList(1200L, 2500L, 4100L, 6000L);
        return playerMaxLevel.indexOf(currentMaxLevel) + 1;
    }

    /**
     * This processes the request and returns the desired maximum value.
     * @param current current level of the player's status.
     * @param levelMax maximum level for each milestone in player's status.
     * @param maxLevel maximum level for the player's status.
     * @return maximum level for the milestone.
     */
    private static Long process(Long current, List<Long> levelMax, Long maxLevel) {
        if (Objects.equals(current, maxLevel)) return maxLevel;
        for (Long level : levelMax) {
            if (current < level) return level;
        }
        return 0L;
    }
}
