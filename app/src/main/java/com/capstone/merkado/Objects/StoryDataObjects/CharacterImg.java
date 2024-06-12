package com.capstone.merkado.Objects.StoryDataObjects;

public class CharacterImg {

    Character character;
    String description;
    int imgDir;

    public CharacterImg(Character character, String description, int imgDir) {
        this.character = character;
        this.description = description;
        this.imgDir = imgDir;
    }

    public Character getCharacter() {
        return character;
    }

    public String getDescription() {
        return description;
    }

    public int getImgDir() {
        return imgDir;
    }

    public static class Character {
        String name, role;

        public Character(String name, String role) {
            this.name = name;
            this.role = role;
        }

        public String getName() {
            return name;
        }

        public String getRole() {
            return role;
        }
    }
}
