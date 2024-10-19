package com.capstone.merkado.Helpers;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.capstone.merkado.CustomViews.NotificationView;
import com.capstone.merkado.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    public static class InAppNotification {

        InAppNotification instance;
        List<MessageModePair> messagePile;
        Handler handler;

        NotificationView notificationView;

        public static String NEW_OBJECTIVE = "New Objective";
        public static String DONE_OBJECTIVE = "Objective Achieved!";

        public InAppNotification(NotificationView notificationView) {
            instance = this;
            this.notificationView = notificationView;
            handler = null;
            messagePile = new ArrayList<>();
        }

        public void sendMessage(String message, String mode) {
            messagePile.add(new MessageModePair(message, mode));
            Log.d("DEBUG_NOTIFICATION", String.format("ADDED MESSAGE %s", message));
            if (handler == null) sendInAppNotification(messagePile.get(0));
        }

        public void sendInAppNotification(MessageModePair messageModePair) {
            notificationView.setNotificationDetails(getImage(messageModePair.getMode()),
                    messageModePair.getMessage(),
                    messageModePair.getMode());
            messagePile.remove(messageModePair);
            Log.d("DEBUG_NOTIFICATION", String.format("REMOVED MESSAGE %s", messageModePair.getMessage()));
            notificationView.setVisibility(View.VISIBLE);
            animateMargin(notificationView, -200, 20);

            handler = new Handler();
            handler.postDelayed(() -> {
                animateMargin(notificationView, 20, -200);
                notificationView.setVisibility(View.GONE);
                handler = null;

                if (!messagePile.isEmpty()) {
                    sendInAppNotification(messagePile.get(0));
                }
            }, 5000);
        }

        private static int getImage(String mode) {
            if (NEW_OBJECTIVE.equals(mode)) {
                return R.drawable.icon_objectives_idle;
            }
            if (DONE_OBJECTIVE.equals(mode)) {
                return R.drawable.icon_objectives_idle;
            }

            return R.drawable.icon_nibble;
        }

        private static void animateMargin(final View view, int fromMargin, int toMargin) {
            final ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

            ValueAnimator marginAnimator = ValueAnimator.ofInt(fromMargin, toMargin);
            marginAnimator.setDuration(1000);

            marginAnimator.addUpdateListener(animation -> {
                layoutParams.leftMargin = (int) animation.getAnimatedValue();
                view.requestLayout();
            });

            marginAnimator.start();
        }

        public static class MessageModePair {
            String message;
            String mode;

            public MessageModePair(String message, String mode) {
                this.message = message;
                this.mode = mode;
            }

            public String getMessage() {
                return message;
            }

            public String getMode() {
                return mode;
            }
        }
    }
}
