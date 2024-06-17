package com.capstone.merkado.Objects.StoryDataObjects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class LineGroup implements Parcelable {
    private Integer id;
    private Integer defaultNextLine;
    private List<ImagePlacementData> initialImages;
    private String background;
    private List<DialogueLine> dialogueLines;
    private String transition;
    private String bgm;

    // No-argument constructor required for Firebase
    public LineGroup() {
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDefaultNextLine() {
        return defaultNextLine;
    }

    public void setDefaultNextLine(Integer defaultNextLine) {
        this.defaultNextLine = defaultNextLine;
    }

    public List<ImagePlacementData> getInitialImages() {
        return initialImages;
    }

    public void setInitialImages(List<ImagePlacementData> initialImages) {
        this.initialImages = initialImages;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public List<DialogueLine> getDialogueLines() {
        return dialogueLines;
    }

    public void setDialogueLines(List<DialogueLine> dialogueLines) {
        this.dialogueLines = dialogueLines;
    }

    public String getTransition() {
        return transition;
    }

    public void setTransition(String transition) {
        this.transition = transition;
    }

    public String getBgm() {
        return bgm;
    }

    public void setBgm(String bgm) {
        this.bgm = bgm;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (id == null) {
            dest.writeInt(-1);
        } else {
            dest.writeInt(id);
        }
        if (defaultNextLine == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(defaultNextLine);
        }
        dest.writeTypedList(initialImages);
        dest.writeString(background);
        dest.writeTypedList(dialogueLines);
        dest.writeString(transition);
        dest.writeString(bgm);
    }

    protected LineGroup(Parcel in) {
        id = in.readInt();
        if (in.readByte() == 0) {
            defaultNextLine = null;
        } else {
            defaultNextLine = in.readInt();
        }
        initialImages = in.createTypedArrayList(ImagePlacementData.CREATOR);
        background = in.readString();
        dialogueLines = in.createTypedArrayList(DialogueLine.CREATOR);
        transition = in.readString();
        bgm = in.readString();
    }

    public static final Creator<LineGroup> CREATOR = new Creator<LineGroup>() {
        @Override
        public LineGroup createFromParcel(Parcel in) {
            return new LineGroup(in);
        }

        @Override
        public LineGroup[] newArray(int size) {
            return new LineGroup[size];
        }
    };

    public static class DialogueLine implements Parcelable {
        private String character;
        private String dialogue;
        private List<ImagePlacementData> imageChanges;
        private List<DialogueChoice> dialogueChoices;
        private String sfx;

        // No-argument constructor required for Firebase
        public DialogueLine() {
        }

        // Getters and setters
        public String getCharacter() {
            return character;
        }

        public void setCharacter(String character) {
            this.character = character;
        }

        public String getDialogue() {
            return dialogue;
        }

        public void setDialogue(String dialogue) {
            this.dialogue = dialogue;
        }

        public List<ImagePlacementData> getImageChanges() {
            return imageChanges;
        }

        public void setImageChanges(List<ImagePlacementData> imageChanges) {
            this.imageChanges = imageChanges;
        }

        public List<DialogueChoice> getDialogueChoices() {
            return dialogueChoices;
        }

        public void setDialogueChoices(List<DialogueChoice> dialogueChoices) {
            this.dialogueChoices = dialogueChoices;
        }

        public String getSfx() {
            return sfx;
        }

        public void setSfx(String sfx) {
            this.sfx = sfx;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(character);
            dest.writeString(dialogue);
            dest.writeTypedList(imageChanges);
            dest.writeTypedList(dialogueChoices);
            dest.writeString(sfx);
        }

        protected DialogueLine(Parcel in) {
            character = in.readString();
            dialogue = in.readString();
            imageChanges = in.createTypedArrayList(ImagePlacementData.CREATOR);
            dialogueChoices = in.createTypedArrayList(DialogueChoice.CREATOR);
            sfx = in.readString();
        }

        public static final Creator<DialogueLine> CREATOR = new Creator<DialogueLine>() {
            @Override
            public DialogueLine createFromParcel(Parcel in) {
                return new DialogueLine(in);
            }

            @Override
            public DialogueLine[] newArray(int size) {
                return new DialogueLine[size];
            }
        };
    }

    public static class DialogueChoice implements Parcelable {
        private String choice;
        private Integer nextLineGroup;

        // No-argument constructor required for Firebase
        public DialogueChoice() {
        }

        // Getters and setters
        public String getChoice() {
            return choice;
        }

        public void setChoice(String choice) {
            this.choice = choice;
        }

        public Integer getNextLineGroup() {
            return nextLineGroup;
        }

        public void setNextLineGroup(Integer nextLineGroup) {
            this.nextLineGroup = nextLineGroup;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(choice);
            if (nextLineGroup == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(nextLineGroup);
            }
        }

        protected DialogueChoice(Parcel in) {
            choice = in.readString();
            if (in.readByte() == 0) {
                nextLineGroup = null;
            } else {
                nextLineGroup = in.readInt();
            }
        }

        public static final Creator<DialogueChoice> CREATOR = new Creator<DialogueChoice>() {
            @Override
            public DialogueChoice createFromParcel(Parcel in) {
                return new DialogueChoice(in);
            }

            @Override
            public DialogueChoice[] newArray(int size) {
                return new DialogueChoice[size];
            }
        };
    }
}
