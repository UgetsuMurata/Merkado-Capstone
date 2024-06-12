package com.capstone.merkado.Objects.StoryDataObjects;

public class Story {
    /**
     * Chapter name
     */
    String chapter;
    /**
     * Lesson name
     */
    String scene;
    Integer startingLineGroup;
    String triggers;

    public Story() {
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public Integer getStartingLineGroup() {
        return startingLineGroup;
    }

    public void setStartingLineGroup(Integer startingLineGroup) {
        this.startingLineGroup = startingLineGroup;
    }

    public String getTriggers() {
        return triggers;
    }

    public void setTriggers(String triggers) {
        this.triggers = triggers;
    }
}
