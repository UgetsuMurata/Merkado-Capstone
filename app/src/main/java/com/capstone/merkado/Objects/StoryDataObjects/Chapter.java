package com.capstone.merkado.Objects.StoryDataObjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Chapter implements Parcelable {
    private Long id;
    private String chapter;
    private List<Scene> scenes;
    private String triggers;

    public Chapter() {
    }

    protected Chapter(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        chapter = in.readString();
        triggers = in.readString();
        scenes = new ArrayList<>();
        in.readList(scenes, Scene.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(chapter);
        dest.writeString(triggers);
        dest.writeList(scenes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Chapter> CREATOR = new Creator<Chapter>() {
        @Override
        public Chapter createFromParcel(Parcel in) {
            return new Chapter(in);
        }

        @Override
        public Chapter[] newArray(int size) {
            return new Chapter[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public List<Scene> getScenes() {
        return scenes;
    }

    public void setScenes(List<Scene> scenes) {
        this.scenes = scenes;
    }

    public String getTriggers() {
        return triggers;
    }

    public void setTriggers(String triggers) {
        this.triggers = triggers;
    }

    public static class Scene implements Parcelable {
        private Long id;
        private List<LineGroup> lineGroup;
        private String scene;
        private Integer nextScene;

        public Scene() {
        }

        protected Scene(Parcel in) {
            if (in.readByte() == 0) {
                id = null;
            } else {
                id = in.readLong();
            }
            lineGroup = new ArrayList<>();
            in.readList(lineGroup, LineGroup.class.getClassLoader());
            scene = in.readString();
            if (in.readByte() == 0) {
                nextScene = null;
            } else {
                nextScene = in.readInt();
            }
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (id == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeLong(id);
            }
            dest.writeList(lineGroup);
            dest.writeString(scene);
            if (nextScene == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(nextScene);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Scene> CREATOR = new Creator<Scene>() {
            @Override
            public Scene createFromParcel(Parcel in) {
                return new Scene(in);
            }

            @Override
            public Scene[] newArray(int size) {
                return new Scene[size];
            }
        };

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public List<LineGroup> getLineGroup() {
            return lineGroup;
        }

        public void setLineGroup(List<LineGroup> lineGroup) {
            this.lineGroup = lineGroup;
        }

        public String getScene() {
            return scene;
        }

        public void setScene(String scene) {
            this.scene = scene;
        }

        public Integer getNextScene() {
            return nextScene;
        }

        public void setNextScene(Integer nextScene) {
            this.nextScene = nextScene;
        }
    }
}
