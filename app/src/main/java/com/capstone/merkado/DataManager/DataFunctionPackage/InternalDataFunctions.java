package com.capstone.merkado.DataManager.DataFunctionPackage;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceData;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class InternalDataFunctions {

    public static ResourceData getResourceData(Integer resourceId) {
        Merkado merkado = Merkado.getInstance();
        return merkado.getResourceDataList().get(resourceId);
    }

    public static ResourceData getResourceData(Long resourceId) {
        Merkado merkado = Merkado.getInstance();
        return merkado.getResourceDataList().get(Math.toIntExact(resourceId));
    }

    public static List<ResourceData> getResourceDataList(ItemType itemType) {
        return Merkado.getInstance().getResourceDataList().stream()
                .filter(resource -> resource.getType().equals(itemType.name()))
                .collect(Collectors.toList());
    }

    public static List<ResourceData> getAllResources() {
        Merkado merkado = Merkado.getInstance();
        return merkado.getResourceDataList();
    }

    public enum ItemType {
        COLLECTIBLE, EDIBLE, RESOURCE
    }
}
