package com.capstone.merkado.Objects.ResourceDataObjects;


import androidx.annotation.NonNull;

public enum ResourceDisplayMode {
    COLLECTIBLES, EDIBLES, RESOURCES;

    @NonNull
    @Override
    public String toString() {
        return name().substring(0, name().length() - 1);
    }
}
