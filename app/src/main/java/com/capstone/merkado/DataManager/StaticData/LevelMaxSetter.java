package com.capstone.merkado.DataManager.StaticData;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LevelMaxSetter {
    public static Long getProficiencyMaxLevel(Long currentLevel) {
        List<Long> proficiencyMaxLevel = Arrays.asList(50L, 300L, 1000L, 3000L, 5000L);
        return process(currentLevel,
                proficiencyMaxLevel,
                proficiencyMaxLevel.get(proficiencyMaxLevel.size() - 1));
    }

    public static Long getProficiencyLevel(Long currentProficiency) {
        List<Long> proficiencyMaxLevel = Arrays.asList(50L, 300L, 1000L, 3000L, 5000L);
        return processLevel(currentProficiency,
                proficiencyMaxLevel,
                proficiencyMaxLevel.get(proficiencyMaxLevel.size() - 1));
    }

    public static Long getMaxPlayerExperience(Long currentLevel) {
        List<Long> playerMaxLevel = Arrays.asList(8900L, 12600L, 16800L, 21500L);
        return process(currentLevel <= 21500L ? currentLevel : 21500L,
                playerMaxLevel,
                playerMaxLevel.get(playerMaxLevel.size() - 1));
    }

    public static Long getPreviousMaxPlayerExperience(Long currentMaxLevel) {
        List<Long> playerMaxLevel = Arrays.asList(8900L, 12600L, 16800L, 21500L);
        int i = playerMaxLevel.indexOf(currentMaxLevel);
        if (i == 0) return 0L;
        return playerMaxLevel.get(i - 1);
    }

    public static Integer getPlayerLevel(Long currentMaxLevel) {
        List<Long> playerMaxLevel = Arrays.asList(8900L, 12600L, 16800L, 21500L);
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

    private static Long processLevel(Long current, List<Long> levelMax, Long maxLevel) {
        if (Objects.equals(current, maxLevel)) return (long) levelMax.size() - 1;
        for (int i = 0; i < levelMax.size(); i++) {
            if (current < levelMax.get(i)) return (long) i;
        }
        return 0L;
    }
}
