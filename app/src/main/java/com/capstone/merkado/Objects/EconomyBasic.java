package com.capstone.merkado.Objects;

import android.graphics.drawable.Drawable;

public class EconomyBasic {
    String title;
    Integer playersOnline;
    Drawable image;

    public EconomyBasic(String title, Integer playersOnline, Drawable image) {
        this.title = title;
        this.playersOnline = playersOnline;
        this.image = image;
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
}
