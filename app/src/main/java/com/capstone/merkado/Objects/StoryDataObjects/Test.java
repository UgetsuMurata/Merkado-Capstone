package com.capstone.merkado.Objects.StoryDataObjects;

import java.util.List;

public class Test {
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public static class Item {
        private String question;
        private String questionImage;
        private List<Integer> correctChoices;
        private List<Choice> choices;

        public String getQuestion() {
            return question;
        }

        public String getQuestionImage() {
            return questionImage;
        }

        public List<Integer> getCorrectChoices() {
            return correctChoices;
        }

        public List<Choice> getChoices() {
            return choices;
        }

        public static class Choice {
            private int id;
            private String choice;
            private String image;

            public int getId() {
                return id;
            }

            public String getChoice() {
                return choice;
            }

            public String getImage() {
                return image;
            }
        }
    }
}
