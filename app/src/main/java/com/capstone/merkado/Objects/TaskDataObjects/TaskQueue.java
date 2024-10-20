package com.capstone.merkado.Objects.TaskDataObjects;

import java.util.List;

@SuppressWarnings("unused")
public class TaskQueue {
    Long lastUpdate;
    List<PlayerTask> tasks;

    public TaskQueue() {
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public List<PlayerTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<PlayerTask> tasks) {
        this.tasks = tasks;
    }
}
