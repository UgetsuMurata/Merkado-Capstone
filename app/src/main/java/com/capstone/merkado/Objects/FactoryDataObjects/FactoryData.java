package com.capstone.merkado.Objects.FactoryDataObjects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

@SuppressWarnings("unused")
public class FactoryData implements Parcelable {
    String factoryType;
    Integer factoryMarketId;
    FactoryDetails details;

    public FactoryData() {
    }

    public String getFactoryType() {
        return factoryType;
    }

    public void setFactoryType(String factoryType) {
        this.factoryType = factoryType;
    }

    public Integer getFactoryMarketId() {
        return factoryMarketId;
    }

    public void setFactoryMarketId(Integer factoryMarketId) {
        this.factoryMarketId = factoryMarketId;
    }

    public FactoryDetails getDetails() {
        return details;
    }

    public void setDetails(FactoryDetails details) {
        this.details = details;
    }

    protected FactoryData(Parcel in) {
        factoryType = in.readString();
        if (in.readByte() == 0) {
            factoryMarketId = null;
        } else {
            factoryMarketId = in.readInt();
        }
        details = in.readParcelable(FactoryDetails.class.getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(factoryType);
        if (factoryMarketId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(factoryMarketId);
        }
        dest.writeParcelable(details, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FactoryData> CREATOR = new Creator<FactoryData>() {
        @Override
        public FactoryData createFromParcel(Parcel in) {
            return new FactoryData(in);
        }

        @Override
        public FactoryData[] newArray(int size) {
            return new FactoryData[size];
        }
    };

    public static class FactoryDetails implements Parcelable {
        Long energy;
        Long energyMax;
        Long energyRecharge;
        Long productPerTap;
        Long lastUsedEnergy;
        Integer onProduction;
        Long foodProficiency;
        Long manufacturingProficiency;

        public FactoryDetails() {
        }

        public Integer getOnProduction() {
            return onProduction;
        }

        public void setOnProduction(Integer onProduction) {
            this.onProduction = onProduction;
        }

        public Long getFoodProficiency() {
            return foodProficiency;
        }

        public void setFoodProficiency(Long foodProficiency) {
            this.foodProficiency = foodProficiency;
        }

        public Long getManufacturingProficiency() {
            return manufacturingProficiency;
        }

        public void setManufacturingProficiency(Long manufacturingProficiency) {
            this.manufacturingProficiency = manufacturingProficiency;
        }

        public Long getEnergy() {
            return energy;
        }

        public void setEnergy(Long energy) {
            this.energy = energy;
        }

        public Long getEnergyMax() {
            return energyMax;
        }

        public void setEnergyMax(Long energyMax) {
            this.energyMax = energyMax;
        }

        public Long getLastUsedEnergy() {
            return lastUsedEnergy;
        }

        public void setLastUsedEnergy(Long lastUsedEnergy) {
            this.lastUsedEnergy = lastUsedEnergy;
        }

        public Long getEnergyRecharge() {
            return energyRecharge;
        }

        public void setEnergyRecharge(Long energyRecharge) {
            this.energyRecharge = energyRecharge;
        }

        public Long getProductPerTap() {
            return productPerTap;
        }

        public void setProductPerTap(Long productPerTap) {
            this.productPerTap = productPerTap;
        }

        public FactoryDetails(Parcel in) {
            if (in.readByte() == 1) onProduction = in.readInt();
            else onProduction = null;
            if (in.readByte() == 1) foodProficiency = in.readLong();
            else foodProficiency = null;
            if (in.readByte() == 1) manufacturingProficiency = in.readLong();
            else manufacturingProficiency = null;
            if (in.readByte() == 1) energy = in.readLong();
            else energy = null;
            if (in.readByte() == 1) energyMax = in.readLong();
            else energyMax = null;
            if (in.readByte() == 1) energyRecharge = in.readLong();
            else energyRecharge = null;
            if (in.readByte() == 1) productPerTap = in.readLong();
            else productPerTap = null;
            if (in.readByte() == 1) lastUsedEnergy = in.readLong();
            else lastUsedEnergy = null;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            if (onProduction == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(onProduction);
            }
            if (foodProficiency == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeLong(foodProficiency);
            }
            if (manufacturingProficiency == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeLong(manufacturingProficiency);
            }
            if (energy == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeLong(energy);
            }
            if (energyMax == null) dest.writeByte((byte) 0);
            else {
                dest.writeByte((byte) 1);
                dest.writeLong(energyMax);
            }
            if (energyRecharge == null) dest.writeByte((byte) 0);
            else {
                dest.writeByte((byte) 1);
                dest.writeLong(energyRecharge);
            }
            if (productPerTap == null) dest.writeByte((byte) 0);
            else {
                dest.writeByte((byte) 1);
                dest.writeLong(productPerTap);
            }
            if (lastUsedEnergy == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeLong(lastUsedEnergy);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<FactoryDetails> CREATOR = new Creator<FactoryDetails>() {
            @Override
            public FactoryDetails[] newArray(int size) {
                return new FactoryDetails[size];
            }

            @Override
            public FactoryDetails createFromParcel(Parcel in) {
                return new FactoryDetails(in);
            }
        };
    }
}
