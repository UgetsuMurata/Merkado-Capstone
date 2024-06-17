package com.capstone.merkado.Objects.PlayerDataObjects;

import com.capstone.merkado.Objects.StoryDataObjects.PlayerStory;
import com.capstone.merkado.Objects.TaskDataObjects.PlayerTask;

import java.util.List;

public class Player {
    private String accountId;
    private String server;
    private Float money;
    private Long exp;
    private List<Inventory> inventory;
    private List<PlayerTask> playerTaskList;
    private List<PlayerStory> playerStoryList;
    private History history;

    /**
     * Converts the Firebase extractor for player data into a workable data object. Note that PlayerTask and PlayerStory should be put manually.
     * @param playerFBExtractor raw PlayerFBExtractor.
     */
    public Player(PlayerFBExtractor playerFBExtractor) {
        this.accountId = playerFBExtractor.getAccountId();
        this.server = playerFBExtractor.getServer();
        this.money = playerFBExtractor.getMoney();
        this.exp = playerFBExtractor.getExp();
        this.inventory = playerFBExtractor.getInventory();
        this.history = playerFBExtractor.getHistory();
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

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }
}
