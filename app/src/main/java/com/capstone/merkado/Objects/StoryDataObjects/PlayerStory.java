package com.capstone.merkado.Objects.StoryDataObjects;

public class PlayerStory {
    private Story story;
    private Story nextStory;
    private LineGroup currentLineGroup;
    private LineGroup nextLineGroup;
    private Boolean isTaken;

    public PlayerStory() {
    }

    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public Story getNextStory() {
        return nextStory;
    }

    public void setNextStory(Story nextStory) {
        this.nextStory = nextStory;
    }

    public LineGroup getCurrentLineGroup() {
        return currentLineGroup;
    }

    public void setCurrentLineGroup(LineGroup currentLineGroup) {
        this.currentLineGroup = currentLineGroup;
    }

    public LineGroup getNextLineGroup() {
        return nextLineGroup;
    }

    public void setNextLineGroup(LineGroup nextLineGroup) {
        this.nextLineGroup = nextLineGroup;
    }

    public Boolean getTaken() {
        return isTaken;
    }

    public void setTaken(Boolean taken) {
        isTaken = taken;
    }
}
