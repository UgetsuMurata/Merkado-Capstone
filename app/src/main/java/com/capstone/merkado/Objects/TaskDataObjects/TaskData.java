package com.capstone.merkado.Objects.TaskDataObjects;

import com.capstone.merkado.Objects.StoryDataObjects.Chapter.GameRewards;

import java.util.List;

@SuppressWarnings("unused")
public class TaskData {
    Integer taskID;
    String category;
    String title;
    String shortDescription;
    String description;
    String note;
    List<GameRewards> rewards;

    public TaskData() {
    }

    public Integer getTaskID() {
        return taskID;
    }

    public void setTaskID(Integer taskID) {
        this.taskID = taskID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<GameRewards> getRewards() {
        return rewards;
    }

    public void setRewards(List<GameRewards> rewards) {
        this.rewards = rewards;
    }
}
