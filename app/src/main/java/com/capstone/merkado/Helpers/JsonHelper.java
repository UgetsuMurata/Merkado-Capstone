package com.capstone.merkado.Helpers;

import android.content.Context;
import android.util.Log;

import com.capstone.merkado.Application.Merkado.StaticContents;
import com.capstone.merkado.Objects.ResourceDataObjects.ResourceData;
import com.capstone.merkado.Objects.ServerDataObjects.Objectives;
import com.capstone.merkado.Objects.StoresDataObjects.MarketPrice;
import com.capstone.merkado.Objects.StoryDataObjects.Chapter;
import com.capstone.merkado.Objects.StoryDataObjects.Quiz;
import com.capstone.merkado.Objects.TaskDataObjects.TaskData;
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

    public static void getMarketPriceList(Context context, OnParse<List<MarketPrice>> onParse) {
        new Thread(() -> {
            String json = "";
            try {
                json = parseJSON(context, "market_prices.json");
            } catch (IOException e) {
                Log.e("getMarketPriceList",
                        String.format("Problem with parsing resource data: %s", e));
                onParse.parsingComplete(null);
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<MarketPrice>>() {
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

    public static void getTaskList(Context context, OnParse<List<TaskData>> onParse) {
        new Thread(() -> {
            String json = "";
            try {
                json = parseJSON(context, "tasks.json");
            } catch (IOException e) {
                Log.e("getStoryList",
                        String.format("Problem with parsing resource data: %s", e));
                onParse.parsingComplete(null);
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<TaskData>>() {
            }.getType();
            onParse.parsingComplete(gson.fromJson(json, listType));
        }).start();
    }

    public static void getObjectivesList(Context context, OnParse<List<Objectives>> onParse) {
        new Thread(() -> {
            String json = "";
            try {
                json = parseJSON(context, "objectives.json");
            } catch (IOException e) {
                Log.e("getObjectivesList",
                        String.format("Problem with parsing resource data: %s", e));
                onParse.parsingComplete(null);
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Objectives>>() {
            }.getType();
            onParse.parsingComplete(gson.fromJson(json, listType));
        }).start();
    }

    public static void getQuizList(Context context, OnParse<List<Quiz>> onParse) {
        new Thread(() -> {
            String json = "";
            try {
                json = parseJSON(context, "quiz.json");
            } catch (IOException e) {
                Log.e("getQuizList",
                        String.format("Problem with parsing resource data: %s", e));
                onParse.parsingComplete(null);
            }

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Quiz>>() {
            }.getType();
            onParse.parsingComplete(gson.fromJson(json, listType));
        }).start();
    }

    public static void getDiagnosticTool(Context context, OnParse<Quiz> onParse) {
        new Thread(() -> {
            String json = "";
            try {
                json = parseJSON(context, "diagnostic_tool.json");
            } catch (IOException e) {
                Log.e("getDiagnosticTool",
                        String.format("Problem with parsing resource data: %s", e));
                onParse.parsingComplete(null);
            }

            Gson gson = new Gson();
            Type type = new TypeToken<Quiz>() {
            }.getType();
            onParse.parsingComplete(gson.fromJson(json, type));
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
