package com.capstone.merkado.DataManager;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPref {

    private final static String PREFS_NAME = "MERKADO";
    public final static String KEEP_SIGNED_IN = "ACCOUNT";

    public static void write(Context context, String code, String object ) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(code, object);
        editor.apply();
    }

    public static void write(Context context, String code, Boolean object ) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(code, object);
        editor.apply();
    }

    public static void write(Context context, String code, Integer object ) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(code, object);
        editor.apply();
    }

    public static String readString(Context context, String code, String default_value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(code, default_value);
    }

    public static Boolean readBool(Context context, String code, Boolean default_value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(code, default_value);
    }

    public static Integer readInt(Context context, String code, Integer default_value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(code, default_value);
    }

    public static void delete(Context context, String code) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.remove(code).apply();
    }
}
