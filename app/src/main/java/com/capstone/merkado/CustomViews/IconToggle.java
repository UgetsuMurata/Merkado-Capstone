package com.capstone.merkado.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.capstone.merkado.R;

public class IconToggle extends ConstraintLayout {

    ImageView image;
    int idleRes, activeRes, disabledRes;
    ToggleStatus currentStatus;
    ToggleStatus previousStatus;
    Context context;
    View.OnClickListener onClickListener;

    public IconToggle(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public IconToggle(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public IconToggle(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.custom_icon_toggle, this, true);
        image = findViewById(R.id.image);
        this.context = context;

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.IconToggle, 0, 0);

            try {
                idleRes = a.getResourceId(R.styleable.IconToggle_drawableIdle, -1);
                activeRes = a.getResourceId(R.styleable.IconToggle_drawableActive, -1);
                disabledRes = a.getResourceId(R.styleable.IconToggle_drawableDisabled, -1);
                int status = a.getInt(R.styleable.IconToggle_status, 0);

                currentStatus = status == 0 ? ToggleStatus.IDLE : status == 1 ? ToggleStatus.ACTIVE : ToggleStatus.DISABLED;
                changeResource(context, currentStatus);

                image.setOnClickListener(v -> {
                    if (currentStatus == ToggleStatus.DISABLED) return;
                    setStatus(currentStatus == ToggleStatus.ACTIVE ? ToggleStatus.IDLE : ToggleStatus.ACTIVE);
                    if (onClickListener != null) onClickListener.onClick(v);
                });
            } finally {
                a.recycle();
            }
        }
    }

    public Boolean setStatus(ToggleStatus toggleStatus) {
        return changeResource(context, toggleStatus);
    }

    public ToggleStatus getStatus() {
        return currentStatus;
    }

    public Boolean isActive() {
        return currentStatus == ToggleStatus.ACTIVE;
    }

    public Boolean isDisabled() {
        return currentStatus == ToggleStatus.DISABLED;
    }

    public Boolean isIdle() {
        return currentStatus == ToggleStatus.IDLE;
    }

    public void disable() {
        this.previousStatus = currentStatus;
        changeResource(context, ToggleStatus.DISABLED);
    }

    public void enable() {
        this.currentStatus = previousStatus;
        changeResource(context, currentStatus == null ? ToggleStatus.IDLE : currentStatus);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private Boolean changeResource(Context context, ToggleStatus toggleStatus) {
        switch (toggleStatus) {
            case IDLE:
                if (idleRes == -1) {
                    Log.e("IconToggle", "Missing drawable for idle status");
                    return false;
                }
                image.setImageDrawable(ContextCompat.getDrawable(context, idleRes));
                break;
            case ACTIVE:
                if (activeRes == -1) {
                    Log.e("IconToggle", "Missing drawable for active status");
                    return false;
                }
                image.setImageDrawable(ContextCompat.getDrawable(context, activeRes));
                break;
            case DISABLED:
                if (disabledRes == -1) {
                    Log.e("IconToggle", "Missing drawable for disabled status");
                    return false;
                }
                image.setImageDrawable(ContextCompat.getDrawable(context, disabledRes));
                break;
        }
        this.currentStatus = toggleStatus;
        return true;
    }

    public enum ToggleStatus {
        IDLE, ACTIVE, DISABLED
    }
}
