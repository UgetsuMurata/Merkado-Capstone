package com.capstone.merkado.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.capstone.merkado.R;

public class IconButton extends ConstraintLayout {

    ImageView image;
    int idleRes, activeRes, disabledRes;
    Boolean enableStatus;
    Context context;
    OnClickListener onClickListener;

    public IconButton(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public IconButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public IconButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.custom_icon_toggle, this, true);
        image = findViewById(R.id.image);
        this.context = context;

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.IconButton, 0, 0);

            try {
                idleRes = a.getResourceId(R.styleable.IconButton_buttonIdle, -1);
                activeRes = a.getResourceId(R.styleable.IconButton_buttonActive, -1);
                disabledRes = a.getResourceId(R.styleable.IconButton_buttonDisabled, -1);
                boolean status = a.getBoolean(R.styleable.IconButton_buttonEnable, true);

                enableStatus = status;
                changeResource(context, status);

                image.setOnClickListener(v -> {
                    if (!enableStatus) return;

                    image.setImageDrawable(ContextCompat.getDrawable(context, activeRes));
                    new Handler().postDelayed(() ->
                            image.setImageDrawable(ContextCompat.getDrawable(context, idleRes)),
                            100);

                    if (onClickListener != null) onClickListener.onClick(v);
                });
            } finally {
                a.recycle();
            }
        }
    }

    public boolean isEnabled() {
        return enableStatus;
    }

    public Boolean disable() {
        return changeResource(context, false);
    }

    public Boolean enable() {
        return changeResource(context, true);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private Boolean changeResource(Context context, Boolean enable) {
        if (enable) {
            if (idleRes == -1) {
                Log.e("IconToggle", "Missing drawable for idle status");
                return false;
            }
            image.setImageDrawable(ContextCompat.getDrawable(context, idleRes));
        } else {
            if (disabledRes == -1) {
                Log.e("IconToggle", "Missing drawable for disabled status");
                return false;
            }
            image.setImageDrawable(ContextCompat.getDrawable(context, disabledRes));
        }
        this.enableStatus = enable;
        return true;
    }
}
