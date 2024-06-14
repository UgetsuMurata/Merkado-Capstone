package com.capstone.merkado.Objects.StoryDataObjects;

import android.os.Parcel;
import android.os.Parcelable;

public class ImagePlacementData implements Parcelable {
    private String image;
    private String placement;
    private Boolean toShow;

    // No-argument constructor required for Firebase
    public ImagePlacementData() {
    }

    // Getters and setters
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeString(placement);
        dest.writeByte((byte) (toShow == null ? 0 : toShow ? 1 : 2));
    }

    protected ImagePlacementData(Parcel in) {
        image = in.readString();
        placement = in.readString();
        byte tmpToShow = in.readByte();
        toShow = tmpToShow == 0 ? null : tmpToShow == 1;
    }

    public static final Creator<ImagePlacementData> CREATOR = new Creator<ImagePlacementData>() {
        @Override
        public ImagePlacementData createFromParcel(Parcel in) {
            return new ImagePlacementData(in);
        }

        @Override
        public ImagePlacementData[] newArray(int size) {
            return new ImagePlacementData[size];
        }
    };
}
