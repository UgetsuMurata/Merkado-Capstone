package com.capstone.merkado.Objects.ServerDataObjects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Objectives implements Parcelable {
    Integer id;
    String title, subtitle;
    List<String> objectives;

    public Objectives() {
    }

    protected Objectives(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        title = in.readString();
        subtitle = in.readString();
        objectives = in.createStringArrayList();
    }

    public static final Creator<Objectives> CREATOR = new Creator<Objectives>() {
        @Override
        public Objectives createFromParcel(Parcel in) {
            return new Objectives(in);
        }

        @Override
        public Objectives[] newArray(int size) {
            return new Objectives[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<String> getObjectives() {
        return objectives;
    }

    public void setObjectives(List<String> objectives) {
        this.objectives = objectives;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeStringList(objectives);
    }
}
