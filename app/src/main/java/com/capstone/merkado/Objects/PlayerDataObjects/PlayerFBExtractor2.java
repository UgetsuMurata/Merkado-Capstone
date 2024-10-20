package com.capstone.merkado.Objects.PlayerDataObjects;

import com.capstone.merkado.Objects.FactoryDataObjects.FactoryData;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor1.StoryQueue;
import com.capstone.merkado.Objects.ResourceDataObjects.Inventory;
import com.capstone.merkado.Objects.StoresDataObjects.Market;
import com.capstone.merkado.Objects.TaskDataObjects.TaskQueue;

import java.util.HashMap;
import java.util.List;

/**
 * Player data parser for Firebase.
 */
@SuppressWarnings("unused")
public class PlayerFBExtractor2 {
    private String accountId;
    private String server;
    private Float money;
    private Long exp;
    private HashMap<String, Inventory> inventory;
    private TaskQueue taskQueue;
    private List<StoryQueue> storyQueue;
    private List<StoryQueue> storyHistory;
    private Market market;
    private FactoryData factory;
    private PlayerFBExtractor1.PlayerObjectives objectives;

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

    public TaskQueue getTaskQueue() {
        return taskQueue;
    }

    public void setTaskQueue(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    public List<StoryQueue> getStoryQueue() {
        return storyQueue;
    }

    public void setStoryQueue(List<StoryQueue> storyQueue) {
        this.storyQueue = storyQueue;
    }

    public List<StoryQueue> getStoryHistory() {
        return storyHistory;
    }

    public void setStoryHistory(List<StoryQueue> storyHistory) {
        this.storyHistory = storyHistory;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
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
}
