package com.capstone.merkado.DataManager.DataFunctionPackage;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.capstone.merkado.DataManager.FirebaseData;
import com.google.firebase.database.DataSnapshot;

import java.util.concurrent.CompletableFuture;

public class UtilityDataFunctions {
    public static CompletableFuture<Boolean> hasNewUpdate(Activity activity) {
        String currentVersionName = getCurrentVersionName(activity);
        if (currentVersionName == null)
            return CompletableFuture.completedFuture(false); // assume that there is no new update.
        return getVersionNameFromDB().thenCompose(versionNameFromDB ->
                CompletableFuture.completedFuture(!currentVersionName.equals(versionNameFromDB)));
    }

    public static CompletableFuture<String> getUpdateLink() {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();

        firebaseData.retrieveData("updates/url", future::complete);

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null) return CompletableFuture.completedFuture(null);
            String versionName = dataSnapshot.getValue(String.class);
            if (versionName == null) return CompletableFuture.completedFuture(null);
            return CompletableFuture.completedFuture(versionName);
        });
    }

    private static String getCurrentVersionName(Activity activity) {
        try {
            PackageManager packageManager = activity.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(activity.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("getCurrentVersionName", "PackageManager: NameNotFound\n" + e);
            return null;
        }
    }

    private static CompletableFuture<String> getVersionNameFromDB() {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();

        firebaseData.retrieveData("updates/version", future::complete);

        return future.thenCompose(dataSnapshot -> {
            if (dataSnapshot == null) return CompletableFuture.completedFuture(null);
            String versionName = dataSnapshot.getValue(String.class);
            if (versionName == null) return CompletableFuture.completedFuture(null);
            return CompletableFuture.completedFuture(versionName);
        });
    }

    public static CompletableFuture<Long> getServerTimeOffset() {
        FirebaseData firebaseData = new FirebaseData();
        CompletableFuture<Long> future = new CompletableFuture<>();

        firebaseData.getServerTimeOffset(future::complete);
        return future;
    }

    public static void reportError(Context context, String error, String errorLocation, String timestamp) {
        FirebaseData firebaseData = new FirebaseData(context);
        firebaseData.setValue(
                String.format("unexpectedErrors/%s/", timestamp),
                String.format("[ %s ] at [ %s ]", error, errorLocation));
    }
}
