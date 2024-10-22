package com.capstone.merkado.Objects.ServerDataObjects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class BasicServerData implements Parcelable {
    private String id;
    private String image;
    private String name;
    private String serverOwner;
    private List<Integer> onlinePlayers;
    private Integer playerId = -1;

    public BasicServerData() {
        onlinePlayers = new ArrayList<>();
    }

    protected BasicServerData(Parcel in) {
        id = in.readString();
        image = in.readString();
        name = in.readString();
        serverOwner = in.readString();
        if (in.readByte() == 0) {
            playerId = null;
        } else {
            playerId = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(image);
        dest.writeString(name);
        dest.writeString(serverOwner);
        if (playerId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(playerId);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BasicServerData> CREATOR = new Creator<BasicServerData>() {
        @Override
        public BasicServerData createFromParcel(Parcel in) {
            return new BasicServerData(in);
        }

        @Override
        public BasicServerData[] newArray(int size) {
            return new BasicServerData[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getOnlinePlayers() {
        return onlinePlayers;
    }

    public void setOnlinePlayers(List<Integer> onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public Integer getOnlinePlayersCount() {
        return this.onlinePlayers.size();
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public String getServerOwner() {
        return serverOwner;
    }

    public void setServerOwner(String serverOwner) {
        this.serverOwner = serverOwner;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) return false;
        if (obj instanceof BasicServerData) {
            BasicServerData basicServerData = (BasicServerData) obj;
            return Objects.equals(basicServerData.getId(), getId());
        } else return false;
    }
}
