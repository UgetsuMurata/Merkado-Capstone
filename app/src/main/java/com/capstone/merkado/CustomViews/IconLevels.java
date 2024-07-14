package com.capstone.merkado.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.capstone.merkado.R;

import org.checkerframework.checker.index.qual.Positive;

public class IconLevels extends ConstraintLayout {

    ImageView icon;
    TextView currentValue, limitValue;
    ProgressBar levelProgress;
    Context context;
    Long current, max;

    public IconLevels(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public IconLevels(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public IconLevels(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.custom_icon_levels, this, true);
        icon = findViewById(R.id.icon);
        currentValue = findViewById(R.id.current_value);
        limitValue = findViewById(R.id.limit_value);
        levelProgress = findViewById(R.id.level_progress);
        this.context = context;

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.IconLevels, 0, 0);

            try {
                int iconRes = a.getResourceId(R.styleable.IconLevels_iconSrc, -1);
                int iconColor = a.getResourceId(R.styleable.IconLevels_iconColor, -1);
                int currentVal = a.getInt(R.styleable.IconLevels_currentValue, 0);
                int limitVal = a.getInt(R.styleable.IconLevels_limitValue, 100);
                boolean showLimitProgress = a.getBoolean(R.styleable.IconLevels_showLimitProgress, false);

                changeIcon(iconRes);
                changeIconColor(iconColor);
                changeCurrentValue(currentVal);
                changeLimitValue(limitVal);

                setProgressLevelMax(limitVal);
                setProgressLevelValue(currentVal);
                levelProgress.setMin(0);
                levelProgress.setVisibility(showLimitProgress ? VISIBLE : GONE);
            } finally {
                a.recycle();
            }
        }
    }

    /**
     * This initializes all the views in IconLevels.
     *
     * @param icon         Drawable resource of the status icon.
     * @param currentValue Player's current value for the status.
     * @param limitValue   Game's limit value for the status.
     */
    public void initializeViews(@DrawableRes int icon, String currentValue, String limitValue) {
        this.icon.setImageDrawable(ContextCompat.getDrawable(this.context, icon));
        this.currentValue.setText(currentValue);
        this.limitValue.setText(limitValue);
    }

    public void changeIcon(@DrawableRes int icon) {
        if (icon != -1)
            this.icon.setImageDrawable(ContextCompat.getDrawable(this.context, icon));
    }

    public void changeCurrentValue(@Positive int currentValue) {
        this.currentValue.setText(String.valueOf(currentValue));
        setProgressLevelValue(currentValue);
    }

    public void changeLimitValue(@Positive int limitValue) {
        this.limitValue.setText(String.valueOf(limitValue));
        setProgressLevelMax(limitValue);
    }

    public void changeIconColor(@ColorRes int color) {
        if (color != -1)
            this.icon.setImageTintList(ContextCompat.getColorStateList(this.context, color));
    }

    private void setProgressLevelMax(@Positive int max) {
        levelProgress.setMax(max);
    }

    private void setProgressLevelValue(@Positive int value) {
        levelProgress.setProgress(value);
    }
}
