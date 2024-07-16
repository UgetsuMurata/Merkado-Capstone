package com.capstone.merkado.Objects.FactoryDataObjects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.capstone.merkado.Objects.StoresDataObjects.PlayerMarkets.OnSale;

import java.util.List;

public class PlayerFactory implements Parcelable {
    Integer factoryOwner;
    Integer factoryId;
    String factoryName;
    String factoryType;
    Long opened;
    String factoryIcon;
    List<OnSale> onSale;

    public PlayerFactory() {
    }

    protected PlayerFactory(Parcel in) {
        if (in.readByte() == 0) {
            factoryOwner = null;
        } else {
            factoryOwner = in.readInt();
        }
        if (in.readByte() == 0) {
            factoryId = null;
        } else {
            factoryId = in.readInt();
        }
        factoryName = in.readString();
        factoryType = in.readString();
        if (in.readByte() == 0) {
            opened = null;
        } else {
            opened = in.readLong();
        }
        factoryIcon = in.readString();
        onSale = in.createTypedArrayList(OnSale.CREATOR);
    }

    public static final Creator<PlayerFactory> CREATOR = new Creator<PlayerFactory>() {
        @Override
        public PlayerFactory createFromParcel(Parcel in) {
            return new PlayerFactory(in);
        }

        @Override
        public PlayerFactory[] newArray(int size) {
            return new PlayerFactory[size];
        }
    };

    public Integer getFactoryOwner() {
        return factoryOwner;
    }

    public void setFactoryOwner(Integer factoryOwner) {
        this.factoryOwner = factoryOwner;
    }

    public Integer getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Integer factoryId) {
        this.factoryId = factoryId;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getFactoryType() {
        return factoryType;
    }

    public void setFactoryType(String factoryType) {
        this.factoryType = factoryType;
    }

    public Long getOpened() {
        return opened;
    }

    public void setOpened(Long opened) {
        this.opened = opened;
    }

    public String getFactoryIcon() {
        return factoryIcon;
    }

    public void setFactoryIcon(String factoryIcon) {
        this.factoryIcon = factoryIcon;
    }

    public List<OnSale> getOnSale() {
        return onSale;
    }

    public void setOnSale(List<OnSale> onSale) {
        this.onSale = onSale;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (factoryOwner == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(factoryOwner);
        }
        if (factoryId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(factoryId);
        }
        dest.writeString(factoryName);
        dest.writeString(factoryType);
        if (opened == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(opened);
        }
        dest.writeString(factoryIcon);
        dest.writeTypedList(onSale);
    }
}
