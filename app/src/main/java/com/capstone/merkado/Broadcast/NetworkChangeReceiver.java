package com.capstone.merkado.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Broadcast receiver class for detecting changes in the network.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private NetworkChangeListener listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Check the connectivity service.
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        // Check if connected.
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // If listener instance was created, return the value.
        if (listener != null) {
            listener.onNetworkChange(isConnected);
        }
    }

    /**
     * Change listener for network change.
     */
    public interface NetworkChangeListener {
        /**
         * Returns callback for changes in the network.
         * @param isConnected boolean value of connectivity status.
         */
        void onNetworkChange(boolean isConnected);
    }
}
