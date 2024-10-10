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

    private List<PlayerStory> convertToPlayerStory(List<PlayerFBExtractor1.StoryQueue> storyQueueList) {
        List<PlayerStory> playerStories = new ArrayList<>();
        if (storyQueueList == null || storyQueueList.isEmpty()) return null;
        for (PlayerFBExtractor1.StoryQueue storyQueue : storyQueueList) {
            PlayerStory playerStory = new PlayerStory();
            Chapter chapter = StoryDataFunctions.getChapterFromId(storyQueue.getChapter());
            Chapter.Scene currentScene = null;
            LineGroup currentLineGroup = null;
            Chapter.Scene nextScene = null;
            LineGroup nextLineGroup = null;

            if (chapter != null &&
                    storyQueue.getCurrentScene() != null &&
                    storyQueue.getCurrentScene() < chapter.getScenes().size())
                currentScene = chapter.getScenes().get(storyQueue.getCurrentScene());
            if (currentScene != null &&
                    storyQueue.getCurrentLineGroup() != null &&
                    storyQueue.getCurrentLineGroup() < currentScene.getLineGroup().size())
                currentLineGroup = currentScene.getLineGroup().get(storyQueue.getCurrentLineGroup());
            if (currentScene != null &&
                    storyQueue.getNextLineGroup() != null &&
                    storyQueue.getNextLineGroup() < currentScene.getLineGroup().size())
                nextLineGroup = currentScene.getLineGroup().get(storyQueue.getNextLineGroup());
            if (chapter != null &&
                    storyQueue.getNextScene() != null &&
                    storyQueue.getNextScene() < chapter.getScenes().size())
                nextScene = chapter.getScenes().get(storyQueue.getNextScene());

            playerStory.setChapter(chapter);
            playerStory.setCurrentScene(currentScene);
            playerStory.setNextScene(nextScene);
            playerStory.setCurrentLineGroup(currentLineGroup);
            playerStory.setNextLineGroup(nextLineGroup);

            playerStories.add(playerStory);
        }
        return playerStories;
    }
}
