package com.capstone.merkado.Objects.PlayerDataObjects;

import com.capstone.merkado.Objects.ResourceDataObjects.Inventory;

import java.util.HashMap;
import java.util.List;

/**
 * Player data parser for Firebase.
 */
public class PlayerFBExtractor2 {
    private String accountId;
    private String server;
    private Float money;
    private Long exp;
    private HashMap<String, Inventory> inventory;
    private List<PlayerFBExtractor1.TaskQueue> taskQueue;
    private List<PlayerFBExtractor1.StoryQueue> storyQueue;
    private History history;
    private Integer marketId;

    // No-argument constructor required for Firebase
    public PlayerFBExtractor2() {
    }

    // Getters and setters
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

    public HashMap<String, Inventory> getInventory() {
        return inventory;
    }

    public void setInventory(HashMap<String, Inventory> inventory) {
        this.inventory = inventory;
    }

    public List<PlayerFBExtractor1.TaskQueue> getTaskQueue() {
        return taskQueue;
    }

    public void setTaskQueue(List<PlayerFBExtractor1.TaskQueue> taskQueue) {
        this.taskQueue = taskQueue;
    }

    public List<PlayerFBExtractor1.StoryQueue> getStoryQueue() {
        return storyQueue;
    }

    public void setStoryQueue(List<PlayerFBExtractor1.StoryQueue> storyQueue) {
        this.storyQueue = storyQueue;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public Integer getMarketId() {
        return marketId;
    }

    public void setMarketId(Integer marketId) {
        this.marketId = marketId;
    }
}
