package com.capstone.merkado.Helpers;

import com.capstone.merkado.DataManager.DataFunctionPackage.PlayerDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.StoryDataFunctions;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor1;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor1.StoryQueue;

public class StoryTriggers {

    public static void trigger(Integer playerId, Integer trigger) {
        StoryDataFunctions.addStoryToPlayer(playerId, createObject(trigger));
    }

    public static void checkForLevelTriggers(Integer playerId, Integer level) {
        switch (level) {
            case 2:
                trigger(playerId, 2);
            case 3:
                trigger(playerId, 4);
            case 4:
                trigger(playerId, 5);
        }
    }

    private static StoryQueue createObject(Integer chapter) {
        PlayerFBExtractor1.StoryQueue storyQueue = new PlayerFBExtractor1.StoryQueue();
        storyQueue.setChapter(chapter);
        storyQueue.setCurrentLineGroup(0);
        storyQueue.setCurrentScene(0);
        storyQueue.setNextLineGroup(1);
        storyQueue.setNextScene(1);
        return storyQueue;
    }
}
