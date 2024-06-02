package com.capstone.merkado.Helpers;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.capstone.merkado.R;

public class NotificationHelper {
    private static final String CHANNEL_ID = "merkado_channel_id";
    private static final String CHANNEL_NAME = "Merkado Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notifications for Merkado app";

    public static void sendNotification(Context context, String title, String message) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Notifications are blocked.", Toast.LENGTH_SHORT).show();
            return;
        }

        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_debug)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private static void createNotificationChannel(Context context) {
        // Create the NotificationChannel
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
        channel.setDescription(CHANNEL_DESCRIPTION);

        // Register the channel with the system
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }
}
