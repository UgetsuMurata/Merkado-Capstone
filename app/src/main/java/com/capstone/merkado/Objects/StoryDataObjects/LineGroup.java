package com.capstone.merkado.Objects.StoryDataObjects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class LineGroup implements Parcelable {
    private Integer id;
    private Integer defaultNextLine;
    private List<ImagePlacementData> initialImages;
    private String background;
    private List<DialogueLine> dialogueLines;
    private String transition;
    private String bgm;
    private Boolean isQuiz;
    private Integer gradedQuiz;
    private String nextLineCode;

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

    public Boolean getIsQuiz() {
        return isQuiz;
    }

    public void setIsQuiz(Boolean isQuiz) {
        this.isQuiz = isQuiz;
    }

    public Integer getGradedQuiz() {
        return gradedQuiz;
    }

    public void setGradedQuiz(Integer gradedQuiz) {
        this.gradedQuiz = gradedQuiz;
    }

    public String getNextLineCode() {
        return nextLineCode;
    }

    public void setNextLineCode(String nextLineCode) {
        this.nextLineCode = nextLineCode;
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
        dest.writeByte((byte) (isQuiz == null ? 0 : isQuiz ? 1 : 2));
        if (gradedQuiz == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(gradedQuiz);
        }
        dest.writeString(nextLineCode);
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
        byte tmpIsQuiz = in.readByte();
        isQuiz = tmpIsQuiz == 0 ? null : tmpIsQuiz == 1;
        if (in.readByte() == 1) gradedQuiz = in.readInt();
        nextLineCode = in.readString();
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
        private List<QuizChoice> quizChoices;
        private String sfx;
        private Variable variable;

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

        public Variable getVariable() {
            return variable;
        }

        public void setVariable(Variable variable) {
            this.variable = variable;
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
            dest.writeTypedList(quizChoices);
            dest.writeString(sfx);
            dest.writeParcelable(variable, flags);
        }

        protected DialogueLine(Parcel in) {
            character = in.readString();
            dialogue = in.readString();
            imageChanges = in.createTypedArrayList(ImagePlacementData.CREATOR);
            dialogueChoices = in.createTypedArrayList(DialogueChoice.CREATOR);
            quizChoices = in.createTypedArrayList(QuizChoice.CREATOR);
            sfx = in.readString();
            variable = in.readParcelable(Variable.class.getClassLoader());
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

        public List<QuizChoice> getQuizChoices() {
            return quizChoices;
        }

        public void setQuizChoices(List<QuizChoice> quizChoices) {
            this.quizChoices = quizChoices;
        }
    }

    public static class QuizChoice implements Parcelable {
        String choice;
        DialogueLine dialogueLine;
        Integer points;

        public QuizChoice() {
        }

        public String getChoice() {
            return choice;
        }

        public void setChoice(String choice) {
            this.choice = choice;
        }

        public DialogueLine getDialogueLine() {
            return dialogueLine;
        }

        public void setDialogueLine(DialogueLine dialogueLine) {
            this.dialogueLine = dialogueLine;
        }

        public Integer getPoints() {
            return points;
        }

        public void setPoints(Integer points) {
            this.points = points;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        protected QuizChoice(Parcel in) {
            choice = in.readString();
            points = (in.readByte() == 0) ? null : in.readInt();
            dialogueLine = in.readParcelable(DialogueLine.class.getClassLoader());
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeString(choice);
            if (points == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(points);
            }
            dest.writeParcelable(dialogueLine, flags);
        }

        public static final Creator<QuizChoice> CREATOR = new Creator<QuizChoice>() {
            @Override
            public QuizChoice createFromParcel(Parcel in) {
                return new QuizChoice(in);
            }

            @Override
            public QuizChoice[] newArray(int size) {
                return new QuizChoice[size];
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
