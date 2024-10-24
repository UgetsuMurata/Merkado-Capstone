package com.capstone.merkado.Objects.StoryDataObjects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

@SuppressWarnings("unused")
public class Chapter implements Parcelable {
    private Long id;
    private String chapter;
    private String category;
    private String shortDescription;
    private List<Scene> scenes;
    private Integer trigger;

    public Chapter() {
    }

    protected Chapter(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        chapter = in.readString();
        category = in.readString();
        shortDescription = in.readString();
        scenes = in.createTypedArrayList(Scene.CREATOR);
        if (in.readByte() == 0) {
            trigger = null;
        } else {
            trigger = in.readInt();
        }
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

    public Integer getTriggers() {
        return trigger;
    }

    public void setTriggers(Integer trigger) {
        this.trigger = trigger;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(chapter);
        dest.writeString(category);
        dest.writeString(shortDescription);
        dest.writeTypedList(scenes);
        if (trigger == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(trigger);
        }
    }

    public static class Scene implements Parcelable {
        private Long id;
        private List<LineGroup> lineGroup;
        private String scene;
        private Integer nextScene;
        private List<GameRewards> rewards;
        private String description;
        private String bgm;

        public Scene() {
        }

        protected Scene(Parcel in) {
            if (in.readByte() == 0) {
                id = null;
            } else {
                id = in.readLong();
            }
            lineGroup = in.createTypedArrayList(LineGroup.CREATOR);
            scene = in.readString();
            if (in.readByte() == 0) {
                nextScene = null;
            } else {
                nextScene = in.readInt();
            }
            rewards = in.createTypedArrayList(GameRewards.CREATOR);
            description = in.readString();
            bgm = in.readString();
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            if (id == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeLong(id);
            }
            dest.writeTypedList(lineGroup);
            dest.writeString(scene);
            if (nextScene == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(nextScene);
            }
            dest.writeTypedList(rewards);
            dest.writeString(description);
            dest.writeString(bgm);
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

        public List<GameRewards> getRewards() {
            return rewards;
        }

        public void setRewards(List<GameRewards> rewards) {
            this.rewards = rewards;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getBgm() {
            return bgm;
        }

        public void setBgm(String bgm) {
            this.bgm = bgm;
        }
    }

    public static class GameRewards implements Parcelable {
        Long resourceId;
        Long resourceQuantity;

        public GameRewards() {
        }

        public GameRewards(Long resourceId, Long resourceQuantity) {
            this.resourceId = resourceId;
            this.resourceQuantity = resourceQuantity;
        }

        protected GameRewards(Parcel in) {
            if (in.readByte() == 0) {
                resourceId = null;
            } else {
                resourceId = in.readLong();
            }
            if (in.readByte() == 0) {
                resourceQuantity = null;
            } else {
                resourceQuantity = in.readLong();
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            if (resourceId == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeLong(resourceId);
            }
            if (resourceQuantity == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeLong(resourceQuantity);
            }
        }

        public static final Creator<GameRewards> CREATOR = new Creator<GameRewards>() {
            @Override
            public GameRewards createFromParcel(Parcel in) {
                return new GameRewards(in);
            }

            @Override
            public GameRewards[] newArray(int size) {
                return new GameRewards[size];
            }
        };

        public Long getResourceId() {
            return resourceId;
        }

        public void setResourceId(Long resourceId) {
            this.resourceId = resourceId;
        }

        public Long getResourceQuantity() {
            return resourceQuantity;
        }

        public void setResourceQuantity(Long resourceQuantity) {
            this.resourceQuantity = resourceQuantity;
        }
    }
}
