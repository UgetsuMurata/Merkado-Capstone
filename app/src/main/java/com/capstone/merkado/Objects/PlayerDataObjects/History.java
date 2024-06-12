package com.capstone.merkado.Objects.PlayerDataObjects;

import java.util.List;

public class History {
    private List<Integer> tasks;
    private List<Integer> stories;

    public History() {
    }

    public List<Integer> getTasks() {
        return tasks;
    }

    public void setTasks(List<Integer> tasks) {
        this.tasks = tasks;
    }

    public List<Integer> getStories() {
        return stories;
    }

    public void setStories(List<Integer> stories) {
        this.stories = stories;
    }
}
