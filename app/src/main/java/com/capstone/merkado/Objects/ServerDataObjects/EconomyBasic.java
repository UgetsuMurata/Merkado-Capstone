package com.capstone.merkado.Objects.ServerDataObjects;

import android.graphics.drawable.Drawable;

public class EconomyBasic {
    String title;
    Integer playersOnline;
    Drawable image;
    Integer playerId;

    public EconomyBasic(String title, Integer playersOnline, Drawable image, Integer playerId) {
        this.title = title;
        this.playersOnline = playersOnline;
        this.image = image;
        this.playerId = playerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPlayersOnline() {
        return playersOnline;
    }

    public void setPlayersOnline(Integer playersOnline) {
        this.playersOnline = playersOnline;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }
}