package com.capstone.merkado.Objects.PlayerDataObjects;

import androidx.annotation.Nullable;

import com.capstone.merkado.DataManager.DataFunctionPackage.StoryDataFunctions;
import com.capstone.merkado.Objects.FactoryDataObjects.FactoryData;
import com.capstone.merkado.Objects.ResourceDataObjects.Inventory;
import com.capstone.merkado.Objects.StoresDataObjects.Market;
import com.capstone.merkado.Objects.StoryDataObjects.Chapter;
import com.capstone.merkado.Objects.StoryDataObjects.LineGroup;
import com.capstone.merkado.Objects.StoryDataObjects.PlayerStory;
import com.capstone.merkado.Objects.TaskDataObjects.PlayerTask;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Player {
    private String accountId;
    private String server;
    private Float money;
    private Long exp;
    private List<Inventory> inventory;
    private List<PlayerTask> playerTaskList;
    private List<PlayerStory> playerStoryList;
    private Market market;
    private List<PlayerStory> storyHistory;
    private FactoryData factory;
    private PlayerFBExtractor1.PlayerObjectives objectives;

    public Player() {
        this.accountId = "";
        this.server = "";
        this.money = 0F;
        this.exp = 0L;
        this.inventory = new ArrayList<>();
        this.playerTaskList = new ArrayList<>();
        this.playerStoryList = new ArrayList<>();
        this.market = null;
        this.storyHistory = null;
        this.factory = null;
        this.objectives = new PlayerFBExtractor1.PlayerObjectives();
    }

    /**
     * Converts the Firebase extractor for player data into a workable data object. Note that PlayerTask and PlayerStory should be put manually.
     * @param playerFBExtractor raw PlayerFBExtractor.
     */
    public Player(@Nullable PlayerFBExtractor1 playerFBExtractor) {
        if (playerFBExtractor == null) return;
        this.accountId = playerFBExtractor.getAccountId();
        this.server = playerFBExtractor.getServer();
        this.money = playerFBExtractor.getMoney();
        this.exp = playerFBExtractor.getExp();
        this.inventory = playerFBExtractor.getInventory();
        this.storyHistory = convertToPlayerStory(playerFBExtractor.getStoryHistory());
        this.market = playerFBExtractor.getMarket();
        this.factory = playerFBExtractor.getFactory();
        this.playerStoryList = convertToPlayerStory(playerFBExtractor.getStoryQueue());
        this.objectives = playerFBExtractor.getObjectives();
    }

    public Player(@Nullable PlayerFBExtractor2 playerFBExtractor) {
        if (playerFBExtractor == null) return;
        this.accountId = playerFBExtractor.getAccountId();
        this.server = playerFBExtractor.getServer();
        this.money = playerFBExtractor.getMoney();
        this.storyHistory = convertToPlayerStory(playerFBExtractor.getStoryHistory());
        this.inventory = new ArrayList<>(playerFBExtractor.getInventory().values());
        this.exp = playerFBExtractor.getExp();
        this.market = playerFBExtractor.getMarket();
        this.factory = playerFBExtractor.getFactory();
        this.playerStoryList = convertToPlayerStory(new PlayerFBExtractor1(playerFBExtractor).getStoryQueue());
        this.objectives = playerFBExtractor.getObjectives();
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Float getMoney() {
        return money;
    }

    public void setMoney(Float money) {
        this.money = money;
    }

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }

    public List<Inventory> getInventory() {
        return inventory;
    }

    public void setInventory(List<Inventory> inventory) {
        this.inventory = inventory;
    }

    public List<PlayerTask> getPlayerTaskList() {
        return playerTaskList;
    }

    public void setPlayerTaskList(List<PlayerTask> playerTaskList) {
        this.playerTaskList = playerTaskList;
    }

    public List<PlayerStory> getPlayerStoryList() {
        return playerStoryList;
    }

    public void setPlayerStoryList(List<PlayerStory> playerStoryList) {
        this.playerStoryList = playerStoryList;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public List<PlayerStory> getStoryHistory() {
        return storyHistory;
    }

    public void setStoryHistory(List<PlayerStory> storyHistory) {
        this.storyHistory = storyHistory;
    }

    public FactoryData getFactory() {
        return factory;
    }

    public void setFactory(FactoryData factory) {
        this.factory = factory;
    }

    public PlayerFBExtractor1.PlayerObjectives getObjectives() {
        return objectives;
    }

    public void setObjectives(PlayerFBExtractor1.PlayerObjectives objectives) {
        this.objectives = objectives;
    }

    private List<PlayerStory> convertToPlayerStory(List<PlayerFBExtractor1.StoryQueue> storyQueueList) {
        if (storyQueueList == null || storyQueueList.isEmpty()) return null;

        List<PlayerStory> playerStories = new ArrayList<>();

        for (PlayerFBExtractor1.StoryQueue storyQueue : storyQueueList) {
            if (storyQueue == null) continue;

            PlayerStory playerStory = new PlayerStory();
            Chapter chapter = StoryDataFunctions.getChapterFromId(storyQueue.getChapter());
            if (chapter == null) continue;

            // Set chapter triggers
            playerStory.setTrigger(chapter.getTriggers());
            playerStory.setChapter(chapter);

            List<Chapter.Scene> scenes = chapter.getScenes();

            // Get current scene and line groups
            Integer currentSceneIndex = storyQueue.getCurrentScene();
            if (currentSceneIndex != null && currentSceneIndex < scenes.size()) {
                Chapter.Scene currentScene = scenes.get(currentSceneIndex);
                playerStory.setCurrentScene(currentScene);

                List<LineGroup> lineGroups = currentScene.getLineGroup();

                Integer currentLineGroupIndex = storyQueue.getCurrentLineGroup();
                if (currentLineGroupIndex != null && currentLineGroupIndex < lineGroups.size()) {
                    playerStory.setCurrentLineGroup(lineGroups.get(currentLineGroupIndex));
                }

                Integer nextLineGroupIndex = storyQueue.getNextLineGroup();
                if (nextLineGroupIndex != null && nextLineGroupIndex < lineGroups.size()) {
                    playerStory.setNextLineGroup(lineGroups.get(nextLineGroupIndex));
                }
            }

            // Get next scene
            Integer nextSceneIndex = storyQueue.getNextScene();
            if (nextSceneIndex != null && nextSceneIndex < scenes.size()) {
                playerStory.setNextScene(scenes.get(nextSceneIndex));
            }

            playerStories.add(playerStory);
        }
        return playerStories;
    }
}
