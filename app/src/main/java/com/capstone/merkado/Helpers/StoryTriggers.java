package com.capstone.merkado.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctionPackage.StoryDataFunctions;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor1;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor1.StoryQueue;
import com.capstone.merkado.Screens.Game.ObjectivesDisplay;

public class StoryTriggers {

    public static void trigger(Integer playerId, Integer trigger) {
        StoryDataFunctions.addStoryToPlayer(playerId, createObject(trigger));
    }

    public static void objectives(Activity activity, Integer trigger) {
        Intent intent = new Intent(activity.getApplicationContext(), ObjectivesDisplay.class);
        intent.putExtra("OBJECTIVE", Merkado.getInstance().getObjectivesList().get(trigger - 1));
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
