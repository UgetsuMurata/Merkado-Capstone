package com.capstone.merkado.Helpers;

import android.content.Context;
import android.util.Log;

import com.capstone.merkado.Application.Merkado.StaticContents;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceData;
import com.capstone.merkado.Objects.StoryDataObjects.Chapter;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JsonHelper {

    public static void getResourceList(Context context, OnParse<List<ResourceData>> onParse) {
        new Thread(() -> {
            String json = "";
            try {
                json = parseJSON(context, "resource.json");
            } catch (IOException e) {
                Log.e("getResourceList",
                        String.format("Problem with parsing resource data: %s", e));
                onParse.parsingComplete(null);
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<ResourceData>>() {
            }.getType();
            onParse.parsingComplete(gson.fromJson(json, listType));
        }).start();
    }

    public static void getStoryList(Context context, OnParse<List<Chapter>> onParse) {
        new Thread(() -> {
            String json = "";
            try {
                json = parseJSON(context, "story.json");
            } catch (IOException e) {
                Log.e("getStoryList",
                        String.format("Problem with parsing resource data: %s", e));
                onParse.parsingComplete(null);
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Chapter>>() {
            }.getType();
            onParse.parsingComplete(gson.fromJson(json, listType));
        }).start();
    }

    public static void getAppData(Context context, OnParse<StaticContents> onParse) {
        new Thread(() -> {
            String json = "";
            try {
                json = parseJSON(context, "texts.json");
            } catch (IOException e) {
                Log.e("getAppData",
                        String.format("Problem with parsing resource data: %s", e));
                onParse.parsingComplete(null);
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<StaticContents>() {
            }.getType();
            onParse.parsingComplete(gson.fromJson(json, listType));
        }).start();
    }

    private static String parseJSON(Context context, String path) throws IOException {
        InputStream is = context.getAssets().open(path);
        int size = is.available();
        byte[] buffer = new byte[size];
        int ignore = is.read(buffer);
        is.close();
        return new String(buffer, StandardCharsets.UTF_8);
    }

    public interface OnParse<T> {
        void parsingComplete(T t);
    }
}