package com.capstone.merkado.Objects.StoryDataObjects;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayerStory implements Parcelable {
    private Chapter chapter;
    private LineGroup currentLineGroup;
    private Chapter.Scene currentScene;
    private LineGroup nextLineGroup;
    private Chapter.Scene nextScene;
    private Integer trigger;

    public PlayerStory() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(chapter, flags);
        dest.writeParcelable(currentLineGroup, flags);
        dest.writeParcelable(currentScene, flags);
        dest.writeParcelable(nextLineGroup, flags);
        dest.writeParcelable(nextScene, flags);
        if (trigger == null) dest.writeByte((byte) 0);
        else {
            dest.writeByte((byte) 1);
            dest.writeInt(trigger);
        }
    }

    protected PlayerStory(Parcel in) {
        chapter = in.readParcelable(Chapter.class.getClassLoader());
        currentLineGroup = in.readParcelable(LineGroup.class.getClassLoader());
        currentScene = in.readParcelable(Chapter.Scene.class.getClassLoader());
        nextLineGroup = in.readParcelable(LineGroup.class.getClassLoader());
        nextScene = in.readParcelable(Chapter.Scene.class.getClassLoader());
        if (in.readByte() == 1) trigger = in.readInt();
    }

    public static final Parcelable.Creator<PlayerStory> CREATOR = new Creator<PlayerStory>() {
        @Override
        public PlayerStory createFromParcel(Parcel in) {
            return new PlayerStory(in);
        }

        @Override
        public PlayerStory[] newArray(int size) {
            return new PlayerStory[size];
        }
    };

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public LineGroup getCurrentLineGroup() {
        return currentLineGroup;
    }

    public void setCurrentLineGroup(LineGroup currentLineGroup) {
        this.currentLineGroup = currentLineGroup;
    }

    public Chapter.Scene getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(Chapter.Scene currentScene) {
        this.currentScene = currentScene;
    }

    public Chapter.Scene getNextScene() {
        return nextScene;
    }

    public void setNextScene(Chapter.Scene nextScene) {
        this.nextScene = nextScene;
    }

    public LineGroup getNextLineGroup() {
        return nextLineGroup;
    }

    public void setNextLineGroup(LineGroup nextLineGroup) {
        this.nextLineGroup = nextLineGroup;
    }

    public Integer getTrigger() {
        return trigger;
    }

    public void setTrigger(Integer trigger) {
        this.trigger = trigger;
    }
}
