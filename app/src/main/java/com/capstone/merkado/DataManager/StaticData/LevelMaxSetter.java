package com.capstone.merkado.DataManager.StaticData;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LevelMaxSetter {
    static List<Long> proficiencyMaxLevel = Arrays.asList(50L, 150L, 300L, 500L, 750L, 1000L, 1500L);
    static Long maxProficiency = 1500L;
    public static Long getProficiencyMaxLevel(Long currentLevel) {
        if (Objects.equals(currentLevel, maxProficiency)) return maxProficiency;
        for (Long level : proficiencyMaxLevel) {
            if (currentLevel < level) return level;
        }
        return 0L;
    }
}
