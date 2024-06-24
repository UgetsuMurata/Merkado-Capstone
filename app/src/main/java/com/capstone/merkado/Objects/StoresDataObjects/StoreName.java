package com.capstone.merkado.Objects.StoresDataObjects;

public class StoreName {
    private int imageUrl;
    private String quantity;

    public StoreName(int imageUrl, String quantity) {
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    public Integer getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
