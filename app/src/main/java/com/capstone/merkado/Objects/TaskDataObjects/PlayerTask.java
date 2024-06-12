package com.capstone.merkado.Objects.TaskDataObjects;

public class PlayerTask {
    TaskData taskData;
    String taskStatusCode;

    public PlayerTask() {
    }

    public TaskData getTask() {
        return taskData;
    }

    public void setTask(TaskData taskData) {
        this.taskData = taskData;
    }

    public String getTaskStatusCode() {
        return taskStatusCode;
    }

    public void setTaskStatusCode(String taskStatusCode) {
        this.taskStatusCode = taskStatusCode;
    }
}
