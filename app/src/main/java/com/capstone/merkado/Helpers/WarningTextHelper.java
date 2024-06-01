package com.capstone.merkado.Helpers;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class WarningTextHelper {
    /**
     * Shows warning using the TextView in parameter. This will use the <b>android.R.color.holo_red_dark</b>.
     *
     * @param context application context.
     * @param view    TextView object.
     * @param message message to show.
     */
    public static void showWarning(Context context, TextView view, String message) {
        view.setVisibility(View.VISIBLE);
        view.setText(message);
        view.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
    }

    /**
     * Shows information using the TextView in parameter. This will use the <b>android.R.color.black</b>.
     *
     * @param context application context.
     * @param view    TextView object.
     * @param message message to show.
     */
    public static void showInfo(Context context, TextView view, String message) {
        view.setVisibility(View.VISIBLE);
        view.setText(message);
        view.setTextColor(ContextCompat.getColor(context, android.R.color.black));
    }

    /**
     * Shows confirmation using the TextView in parameter. This will use the <b>android.R.color.holo_green_dark</b>.
     *
     * @param context application context.
     * @param view    TextView object.
     * @param message message to show.
     */
    public static void showConfirmation(Context context, TextView view, String message) {
        view.setVisibility(View.VISIBLE);
        view.setText(message);
        view.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
    }

    /**
     * This hides the TextView in parameter.
     * @param view TextView object.
     */
    public static void hide(TextView view) {
        view.setVisibility(View.GONE);
    }
}
