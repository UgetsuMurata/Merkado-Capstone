package com.capstone.merkado.Objects.PlayerDataObjects;

public class Inventory {
    Integer resourceId;
    Integer quantity;

    public Inventory() {
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
