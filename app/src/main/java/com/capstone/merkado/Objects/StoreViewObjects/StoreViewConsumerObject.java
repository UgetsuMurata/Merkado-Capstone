package com.capstone.merkado.Objects.StoreViewObjects;

public class StoreViewConsumerObject {
    private int imageResource;
    private String itemName;
    private String itemPrice;
    private int quantity;

    public StoreViewConsumerObject(int imageResource, String itemName, String itemPrice, int quantity) {
        this.imageResource = imageResource;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.quantity = quantity;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public int getQuantity() {
        return quantity;
    }
}

