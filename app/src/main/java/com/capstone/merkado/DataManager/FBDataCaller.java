package com.capstone.merkado.DataManager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;

public class FBDataCaller {
    public interface ReturnHandler {
        void returnObject(@NonNull Object object);
    }

    public interface ReturnHandlerWithStatus {
        void returnObject(Object object, Boolean status);
    }

    public interface ReturnHandlerWithKey {
        void returnObject(String key, Object object);
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return nc != null && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }
}
