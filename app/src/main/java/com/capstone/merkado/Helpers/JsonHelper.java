package com.capstone.merkado.Helpers;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.capstone.merkado.Objects.ResourceDataObjects.ResourceData;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JsonHelper {

    public static void getResourceList(Context context, OnParse<List<ResourceData>> onParse) {
        new Handler().post(() -> {
            String json = "";
            try {
                InputStream is = context.getAssets().open("resource.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                int ignore = is.read(buffer);
                is.close();
                json = new String(buffer, StandardCharsets.UTF_8);
            } catch (Exception e) {
                Log.e("getResourceList",
                        String.format("Problem with parsing resource data: %s", e));
                onParse.parsingComplete(null);
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<ResourceData>>() {
            }.getType();
            onParse.parsingComplete(gson.fromJson(json, listType));
        });
    }

    public interface OnParse<T> {
        void parsingComplete(T t);
    }
}
