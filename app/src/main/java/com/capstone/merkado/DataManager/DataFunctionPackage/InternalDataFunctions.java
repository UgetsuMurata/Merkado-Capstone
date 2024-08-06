package com.capstone.merkado.DataManager.DataFunctionPackage;

import android.content.Context;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceData;

@SuppressWarnings("unused")
public class InternalDataFunctions {

    public static ResourceData getResourceData(Context context, Integer resourceId) {
        Merkado merkado = Merkado.getInstance();
        return merkado.getResourceDataList().get(resourceId);
    }

}
