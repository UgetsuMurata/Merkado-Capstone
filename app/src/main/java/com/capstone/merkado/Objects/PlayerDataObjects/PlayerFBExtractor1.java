package com.capstone.merkado.Objects.PlayerDataObjects;

import androidx.annotation.Nullable;

import com.capstone.merkado.Objects.FactoryDataObjects.FactoryData;
import com.capstone.merkado.Objects.ResourceDataObjects.Inventory;
import com.capstone.merkado.Objects.StoresDataObjects.Market;

import java.util.ArrayList;
import java.util.List;

/**
 * Player data parser for Firebase.
 */
@SuppressWarnings("unused")
public class PlayerFBExtractor1 {
    private String accountId;
    private String server;
    private Float money;
    private Long exp;
    private List<Inventory> inventory;
    private List<TaskQueue> taskQueue;
    private List<StoryQueue> storyQueue;
    private Market market;
    private List<PlayerFBExtractor1.StoryQueue> storyHistory;
    private FactoryData factory;
    private PlayerObjectives objectives;

    // No-argument constructor required for Firebase
    public PlayerFBExtractor1() {
    }

    public PlayerFBExtractor1(@Nullable PlayerFBExtractor2 playerFBExtractor2) {
        if (playerFBExtractor2 == null) return;
        this.accountId = playerFBExtractor2.getAccountId();
        this.server = playerFBExtractor2.getServer();
        this.money = playerFBExtractor2.getMoney();
        this.exp = playerFBExtractor2.getExp();
        this.inventory = new ArrayList<>(playerFBExtractor2.getInventory().values());
        this.market = playerFBExtractor2.getMarket();
        this.taskQueue = playerFBExtractor2.getTaskQueue();
        this.storyQueue = playerFBExtractor2.getStoryQueue();
        this.storyHistory = playerFBExtractor2.getStoryHistory();
        this.factory = playerFBExtractor2.getFactory();
        this.objectives = playerFBExtractor2.getObjectives();
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

    public List<Inventory> getInventory() {
        return inventory;
    }

    public void setInventory(List<Inventory> inventory) {
        this.inventory = inventory;
    }

    public List<TaskQueue> getTaskQueue() {
        return taskQueue;
    }

    public void setTaskQueue(List<TaskQueue> taskQueue) {
        this.taskQueue = taskQueue;
    }

    public List<StoryQueue> getStoryQueue() {
        return storyQueue;
    }

    public void setStoryQueue(List<StoryQueue> storyQueue) {
        this.storyQueue = storyQueue;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public List<PlayerFBExtractor1.StoryQueue> getStoryHistory() {
        return storyHistory;
    }

    public void setStoryHistory(List<PlayerFBExtractor1.StoryQueue> storyHistory) {
        this.storyHistory = storyHistory;
    }

    public FactoryData getFactory() {
        return factory;
    }

    public void setFactory(FactoryData factory) {
        this.factory = factory;
    }

    public PlayerObjectives getObjectives() {
        return objectives;
    }

    public void setObjectives(PlayerObjectives objectives) {
        this.objectives = objectives;
    }

    // Nested classes for complex fields
    public static class TaskQueue {
        Integer task;
        String taskStatusCode;

        public TaskQueue() {
        }

        public Integer getTask() {
            return task;
        }

        public void setTask(Integer task) {
            this.task = task;
        }

        public String getTaskStatusCode() {
            return taskStatusCode;
        }

        public void setTaskStatusCode(String taskStatusCode) {
            this.taskStatusCode = taskStatusCode;
        }
    }

    public static class StoryQueue {
        private Integer chapter;
        private Integer currentLineGroup;
        private Integer currentScene;
        private Integer nextLineGroup;
        private Integer nextScene;
        private Integer trigger;

        // No-argument constructor required for Firebase
        public StoryQueue() {
        }

        // Getters and setters

        public Integer getChapter() {
            return chapter;
        }

        public void setChapter(Integer chapter) {
            this.chapter = chapter;
        }

        public Integer getCurrentLineGroup() {
            return currentLineGroup;
        }

        public void setCurrentLineGroup(Integer currentLineGroup) {
            this.currentLineGroup = currentLineGroup;
        }

        public Integer getCurrentScene() {
            return currentScene;
        }

        public void setCurrentScene(Integer currentScene) {
            this.currentScene = currentScene;
        }

        public Integer getNextLineGroup() {
            return nextLineGroup;
        }

        public void setNextLineGroup(Integer nextLineGroup) {
            this.nextLineGroup = nextLineGroup;
        }

        public Integer getNextScene() {
            return nextScene;
        }

        public void setNextScene(Integer nextScene) {
            this.nextScene = nextScene;
        }

        public Integer getTrigger() {
            return trigger;
        }

        public void setTrigger(Integer trigger) {
            this.trigger = trigger;
        }
    }

    public static class PlayerObjectives {
        Integer id;
        Integer currentObjectiveId;
        Boolean done;

        public PlayerObjectives() {
        }

        public PlayerObjectives(Integer id, Integer currentObjectiveId, Boolean done) {
            this.id = id;
            this.currentObjectiveId = currentObjectiveId;
            this.done = done;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getCurrentObjectiveId() {
            return currentObjectiveId;
        }

        public void setCurrentObjectiveId(Integer currentObjectiveId) {
            this.currentObjectiveId = currentObjectiveId;
        }

        public Boolean getDone() {
            return done;
        }

        public void setDone(Boolean done) {
            this.done = done;
        }
    }
}
