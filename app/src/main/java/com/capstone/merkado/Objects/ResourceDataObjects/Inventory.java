package com.capstone.merkado.Objects.ResourceDataObjects;

public class Inventory {
    Integer resourceId;
    ResourceData resourceData;
    Integer quantity;
    String type;

    public Inventory() {
    }

    public Inventory(Inventory inventory) {
        this.resourceId = inventory.getResourceId();
        this.quantity = inventory.getQuantity();
        this.resourceData = inventory.getResourceData();
        this.type = inventory.getType();
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public ResourceData getResourceData() {
        return resourceData;
    }

    public void setResourceData(ResourceData resourceData) {
        this.resourceData = resourceData;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
