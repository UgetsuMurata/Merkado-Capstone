package com.capstone.merkado.Objects.TaskDataObjects;

import com.capstone.merkado.Objects.StoryDataObjects.Chapter.GameRewards;

import java.util.List;

@SuppressWarnings("unused")
public class TaskData {
    String title;
    String description;
    String shortDescription;
    String category;
    String taskCode;
    String triggers;
    List<GameRewards> rewards;

    public TaskData() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getTriggers() {
        return triggers;
    }

    public void setTriggers(String triggers) {
        this.triggers = triggers;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<GameRewards> getRewards() {
        return rewards;
    }

    public void setRewards(List<GameRewards> rewards) {
        this.rewards = rewards;
    }
}
