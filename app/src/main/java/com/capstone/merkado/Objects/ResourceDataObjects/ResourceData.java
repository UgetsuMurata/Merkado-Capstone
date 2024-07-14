package com.capstone.merkado.Objects.ResourceDataObjects;

import com.capstone.merkado.Objects.FactoryDataObjects.FactoryDefaults;

public class ResourceData {
    Integer resourceId;
    String name;
    String description;
    String type;
    Boolean sellable;
    FactoryDefaults factoryDefaults;

    public ResourceData() {
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getSellable() {
        return sellable;
    }

    public void setSellable(Boolean sellable) {
        this.sellable = sellable;
    }

    public FactoryDefaults getFactoryDefaults() {
        return factoryDefaults;
    }

    public void setFactoryDefaults(FactoryDefaults factoryDefaults) {
        this.factoryDefaults = factoryDefaults;
    }
}
