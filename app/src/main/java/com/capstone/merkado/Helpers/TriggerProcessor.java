package com.capstone.merkado.Helpers;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctionPackage.PlayerDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.StoryDataFunctions;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor1;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor1.StoryQueue;
import com.capstone.merkado.Screens.Game.ObjectivesDisplay;

import java.util.Map;

public class TriggerProcessor {

    public static void objectives(Activity activity, Integer trigger, @Nullable ActivityResultLauncher<Intent> objectiveDisplayLauncher) {
        Intent intent = new Intent(activity.getApplicationContext(), ObjectivesDisplay.class);
        intent.putExtra("OBJECTIVE", Merkado.getInstance().getObjectivesList().get(trigger - 1));
        PlayerDataFunctions.setCurrentObjective(Merkado.getInstance().getPlayerId(), setUpPlayerObjectives(trigger - 1));
        if (objectiveDisplayLauncher != null) objectiveDisplayLauncher.launch(intent);
        else activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private static PlayerFBExtractor1.PlayerObjectives setUpPlayerObjectives(Integer id){
        PlayerFBExtractor1.PlayerObjectives playerObjectives = new PlayerFBExtractor1.PlayerObjectives();
        playerObjectives.setId(id);
        playerObjectives.setCurrentObjectiveId(0);
        playerObjectives.setDone(false);
        return playerObjectives;
    }

    public static void checkForLevelTriggers(String serverId, Integer playerId, Integer level, Integer totalPlayersInLevel) {
        switch (level) {
            case 2:
                storyTrigger(playerId, 2);
                botTrigger(serverId, Bot.BotType.STORE, totalPlayersInLevel);
                break;
            case 3:
                storyTrigger(playerId, 4);
                break;
            case 4:
                storyTrigger(playerId, 5);
                botTrigger(serverId, Bot.BotType.FACTORY, totalPlayersInLevel);
                break;
        }
    }

    public static void storyTrigger(Integer playerId, Integer trigger) {
        StoryDataFunctions.addStoryToPlayer(playerId, createObject(trigger));
    }

    public static void botTrigger(String serverId, Bot.BotType botType, Integer currentPlayerInLevel) {
        Map<Bot.BotType, Boolean> hasBotMap = Merkado.getInstance().getHasBotMap();
        if (hasBotMap != null) {
            if (Boolean.TRUE.equals(hasBotMap.get(botType)))
                Bot.checkForBotRemoval(botType, serverId, currentPlayerInLevel).thenAccept(remove -> {
                    if (remove) {
                        if (Bot.BotType.STORE.equals(botType)) Bot.removeStoreBot(serverId);
                        else Bot.removeFactoryBot(serverId);
                    }
                });
        }
        else {
            Merkado.getInstance().getHasBotMapFuture().thenAccept(hasBotMapResult -> {
                if (Boolean.TRUE.equals(hasBotMapResult.get(botType)))
                    Bot.checkForBotRemoval(botType, serverId, currentPlayerInLevel).thenAccept(remove -> {
                        if (remove) {
                            if (Bot.BotType.STORE.equals(botType)) Bot.removeStoreBot(serverId);
                            else Bot.removeFactoryBot(serverId);
                        }
                    });
            });
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
