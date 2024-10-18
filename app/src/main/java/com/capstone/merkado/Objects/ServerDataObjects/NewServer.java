package com.capstone.merkado.Objects.ServerDataObjects;

public class NewServer {
    String name;
    String serverOwner;
    Integer serverImage;
    String key;
    Settings settings;

    public NewServer() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerOwner() {
        return serverOwner;
    }

    public void setServerOwner(String serverOwner) {
        this.serverOwner = serverOwner;
    }

    public Integer getServerImage() {
        return serverImage;
    }

    public void setServerImage(Integer serverImage) {
        this.serverImage = serverImage;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public static class Settings {
        Integer playerLimit;
        Float sensitivityFactor;

        public Settings() {
        }

        public Integer getPlayerLimit() {
            return playerLimit;
        }

        public void setPlayerLimit(Integer playerLimit) {
            this.playerLimit = playerLimit;
        }

        public Float getSensitivityFactor() {
            return sensitivityFactor;
        }

        public void setSensitivityFactor(Float sensitivityFactor) {
            this.sensitivityFactor = sensitivityFactor;
        }
    }
}
