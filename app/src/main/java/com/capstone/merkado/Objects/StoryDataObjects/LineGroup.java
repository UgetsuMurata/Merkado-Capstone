package com.capstone.merkado.Objects.StoryDataObjects;

import java.util.List;
/**
 * Custom object to pass the Firebase LineGroup data.
 */
public class LineGroup {
    private Integer defaultNextLine;
    private List<ImagePlacementData> initialImages;
    private String background;
    private List<DialogueLine> dialogueLines;

    // No-argument constructor required for Firebase
    public LineGroup() {
    }

    // Getters and setters
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

    public static class ImagePlacementData {
        private Integer image;
        private String placement;
        private Boolean toShow;

        // No-argument constructor required for Firebase
        public ImagePlacementData() {
        }

        // Getters and setters
        public Integer getImage() {
            return image;
        }

        public void setImage(Integer image) {
            this.image = image;
        }

        public String getPlacement() {
            return placement;
        }

        public void setPlacement(String placement) {
            this.placement = placement;
        }

        public Boolean getToShow() {
            return toShow;
        }

        public void setToShow(Boolean toShow) {
            this.toShow = toShow;
        }
    }

    public static class DialogueLine {
        private String character;
        private String dialogue;
        private List<ImagePlacementData> imageChanges;
        private List<DialogueChoice> dialogueChoices;

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
    }

    public static class DialogueChoice {
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
    }
}
