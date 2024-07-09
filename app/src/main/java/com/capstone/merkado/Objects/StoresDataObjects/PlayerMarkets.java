package com.capstone.merkado.Objects.StoresDataObjects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerMarkets implements Parcelable {
    Integer marketOwner;
    Integer marketId;
    String storeName;
    Long opened;
    String storeIcon;
    List<OnSale> onSale;

    public PlayerMarkets() {
    }

    public Integer getMarketOwner() {
        return marketOwner;
    }

    public void setMarketOwner(Integer marketOwner) {
        this.marketOwner = marketOwner;
    }

    public Integer getMarketId() {
        return marketId;
    }

    public void setMarketId(Integer marketId) {
        this.marketId = marketId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Long getOpened() {
        return opened;
    }

    public void setOpened(Long opened) {
        this.opened = opened;
    }

    public List<OnSale> getOnSale() {
        return onSale;
    }

    public void setOnSale(List<OnSale> onSale) {
        this.onSale = onSale;
    }

    public String getStoreIcon() {
        return storeIcon;
    }

    public void setStoreIcon(String storeIcon) {
        this.storeIcon = storeIcon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected PlayerMarkets(Parcel in) {
        if (in.readByte() == 0) {
            marketOwner = null;
        } else {
            marketOwner = in.readInt();
        }
        if (in.readByte() == 0) {
            marketId = null;
        } else {
            marketId = in.readInt();
        }
        storeName = in.readString();
        if (in.readByte() == 0) {
            opened = null;
        } else {
            opened = in.readLong();
        }
        onSale = new ArrayList<>();
        in.readList(onSale, OnSale.class.getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (marketOwner == null) dest.writeByte((byte) 0);
        else {
            dest.writeByte((byte) 1);
            dest.writeInt(marketOwner);
        }
        if (marketId == null) dest.writeByte((byte) 0);
        else {
            dest.writeByte((byte) 1);
            dest.writeInt(marketId);
        }
        dest.writeString(storeName);
        if (opened == null) dest.writeByte((byte) 0);
        else {
            dest.writeByte((byte) 1);
            dest.writeLong(opened);
        }
        dest.writeList(onSale);
    }

    public static final Creator<PlayerMarkets> CREATOR = new Creator<PlayerMarkets>() {
        @Override
        public PlayerMarkets createFromParcel(Parcel in) {
            return new PlayerMarkets(in);
        }

        @Override
        public PlayerMarkets[] newArray(int size) {
            return new PlayerMarkets[size];
        }
    };

    public static class OnSale implements Parcelable {
        Integer onSaleId;
        String itemName;
        Integer resourceId;
        String type;
        Float price;
        Integer quantity;
        Integer inventoryResourceReference;

        public OnSale() {
        }

        public OnSale(OnSale onSale) {
            onSaleId = onSale.getOnSaleId();
            itemName = onSale.getItemName();
            resourceId = onSale.getResourceId();
            type = onSale.getType();
            price = onSale.getPrice();
            quantity = onSale.getQuantity();
            inventoryResourceReference = onSale.getInventoryResourceReference();
        }

        public Integer getOnSaleId() {
            return onSaleId;
        }

        public void setOnSaleId(Integer onSaleId) {
            this.onSaleId = onSaleId;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public Integer getResourceId() {
            return resourceId;
        }

        public void setResourceId(Integer resourceId) {
            this.resourceId = resourceId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Float getPrice() {
            return price;
        }

        public void setPrice(Float price) {
            this.price = price;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Integer getInventoryResourceReference() {
            return inventoryResourceReference;
        }

        public void setInventoryResourceReference(Integer inventoryResourceReference) {
            this.inventoryResourceReference = inventoryResourceReference;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        protected OnSale(Parcel in) {
            if (in.readByte() == 0) {
                onSaleId = null;
            } else {
                onSaleId = in.readInt();
            }
            itemName = in.readString();
            if (in.readByte() == 0) {
                resourceId = null;
            } else {
                resourceId = in.readInt();
            }
            type = in.readString();
            if (in.readByte() == 0) {
                price = null;
            } else {
                price = in.readFloat();
            }
            if (in.readByte() == 0) {
                quantity = null;
            } else {
                quantity = in.readInt();
            }
            if (in.readByte() == 0) {
                inventoryResourceReference = null;
            } else {
                inventoryResourceReference = in.readInt();
            }
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            if (onSaleId == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(onSaleId);
            }
            dest.writeString(itemName);
            if (resourceId == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(resourceId);
            }
            dest.writeString(type);
            if (price == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeFloat(price);
            }
            if (quantity == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(quantity);
            }
            if (inventoryResourceReference == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(inventoryResourceReference);
            }
        }

        public static final Creator<OnSale> CREATOR = new Creator<OnSale>() {
            @Override
            public OnSale createFromParcel(Parcel in) {
                return new OnSale(in);
            }

            @Override
            public OnSale[] newArray(int size) {
                return new OnSale[size];
            }
        };

        @Override
        public boolean equals(@Nullable Object obj) {
            if (!(obj instanceof OnSale)) return false;
            OnSale onSale1 = (OnSale) obj;
            return getResourceId().equals(onSale1.getResourceId()) &&
                    getPrice().equals(onSale1.getPrice()) &&
                    Objects.equals(getInventoryResourceReference(), onSale1.getInventoryResourceReference());
        }
    }
}
