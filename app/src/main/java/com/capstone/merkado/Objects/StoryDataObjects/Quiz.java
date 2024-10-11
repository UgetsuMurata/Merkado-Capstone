package com.capstone.merkado.Objects.StoryDataObjects;

import java.util.List;

public class Quiz {
    Integer id;
    List<Item> items;

    public Quiz() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {
        String question;
        List<Integer> correctChoices;
        List<Choice> choices;

        public Item() {
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public List<Integer> getCorrectChoices() {
            return correctChoices;
        }

        public void setCorrectChoices(List<Integer> correctChoices) {
            this.correctChoices = correctChoices;
        }

        public List<Choice> getChoices() {
            return choices;
        }

        public void setChoices(List<Choice> choices) {
            this.choices = choices;
        }

        public static class Choice {
            Integer id;
            String choice;

            public Choice() {
            }

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getChoice() {
                return choice;
            }

            public void setChoice(String choice) {
                this.choice = choice;
            }
        }
    }
}
