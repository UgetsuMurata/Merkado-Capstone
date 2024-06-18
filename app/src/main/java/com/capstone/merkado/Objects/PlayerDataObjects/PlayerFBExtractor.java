package com.capstone.merkado.Objects.PlayerDataObjects;

import java.util.List;

/**
 * Player data parser for Firebase.
 */
public class PlayerFBExtractor {
    private String accountId;
    private String server;
    private Float money;
    private Long exp;
    private List<Inventory> inventory;
    private List<TaskQueue> taskQueue;
    private List<StoryQueue> storyQueue;
    private History history;

    // No-argument constructor required for Firebase
    public PlayerFBExtractor() {
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

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
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
        private Boolean isTaken;
        private Integer nextLineGroup;
        private Integer nextScene;

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

        public Boolean getTaken() {
            return isTaken;
        }

        public void setTaken(Boolean taken) {
            isTaken = taken;
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
    }
}
