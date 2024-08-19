package com.capstone.merkado.DataManager.DataFunctionPackage;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceData;

import java.util.List;

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

    public static List<ResourceData> getAllResources() {
        Merkado merkado = Merkado.getInstance();
        return merkado.getResourceDataList();
    }

}
