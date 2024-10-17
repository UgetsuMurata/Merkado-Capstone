package com.capstone.merkado.CustomViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.capstone.merkado.R;

public class NotificationView extends LinearLayout {
    Context context;
    ImageView notificationIcon;
    TextView notificationName;
    TextView notificationSubtitle;

    public NotificationView(Context context) {
        this(context, null);
    }

    public NotificationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NotificationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_overlay_notifications, this, true);
        notificationIcon = findViewById(R.id.notification_icon);
        notificationName = findViewById(R.id.notification_name);
        notificationSubtitle = findViewById(R.id.notification_subtitle);
        this.context = context;
    }

    public void setNotificationDetails(int icon, @NonNull String name, @NonNull String subtitle) {
        notificationIcon.setImageDrawable(ContextCompat.getDrawable(context, icon));
        notificationName.setText(name);
        notificationSubtitle.setText(subtitle);
    }
}
