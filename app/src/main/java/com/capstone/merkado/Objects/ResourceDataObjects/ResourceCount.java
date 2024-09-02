package com.capstone.merkado.Objects.ResourceDataObjects;

@SuppressWarnings("unused")
public class ResourceCount {
    Integer resourceId;
    Long quantity;

    public ResourceCount() {
    }

    public ResourceCount(Integer resourceId, Long quantity) {
        this.resourceId = resourceId;
        this.quantity = quantity;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
