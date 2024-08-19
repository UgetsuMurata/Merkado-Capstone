package com.capstone.merkado.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.ContextCompat;

import com.capstone.merkado.R;

public class ConstraintClicker extends ConstraintLayout {

    Guideline clickerStart, clickerEnd, clickerTop, clickerBottom;
    View clicker;
    OnClickListener onClickListener;

    public ConstraintClicker(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public ConstraintClicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ConstraintClicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.custom_constraint_clicker, this, true);
        clickerTop = findViewById(R.id.clicker_top);
        clickerBottom = findViewById(R.id.clicker_bottom);
        clickerStart = findViewById(R.id.clicker_start);
        clickerEnd = findViewById(R.id.clicker_end);
        clicker = findViewById(R.id.clicker);

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ConstraintClicker, 0, 0);

            try {
                if (!a.hasValue(R.styleable.ConstraintClicker_clickerStart)) {
                    throw new IllegalArgumentException("Attribute clickerStart is required");
                }
                if (!a.hasValue(R.styleable.ConstraintClicker_clickerEnd)) {
                    throw new IllegalArgumentException("Attribute clickerEnd is required");
                }
                if (!a.hasValue(R.styleable.ConstraintClicker_clickerTop)) {
                    throw new IllegalArgumentException("Attribute clickerTop is required");
                }
                if (!a.hasValue(R.styleable.ConstraintClicker_clickerBottom)) {
                    throw new IllegalArgumentException("Attribute clickerBottom is required");
                }

                float clickerStartValue = a.getFloat(R.styleable.ConstraintClicker_clickerStart, 0f);
                float clickerEndValue = a.getFloat(R.styleable.ConstraintClicker_clickerEnd, 0f);
                float clickerTopValue = a.getFloat(R.styleable.ConstraintClicker_clickerTop, 0f);
                float clickerBottomValue = a.getFloat(R.styleable.ConstraintClicker_clickerBottom, 0f);
                boolean showAreaValue = a.getBoolean(R.styleable.ConstraintClicker_showArea, false);

                // Check if the values are within the 0-1 range
                if (clickerStartValue < 0f || clickerStartValue > 1f) {
                    throw new IllegalArgumentException("clickerStart must be between 0 and 1");
                }
                if (clickerEndValue < 0f || clickerEndValue > 1f) {
                    throw new IllegalArgumentException("clickerEnd must be between 0 and 1");
                }
                if (clickerTopValue < 0f || clickerTopValue > 1f) {
                    throw new IllegalArgumentException("clickerTop must be between 0 and 1");
                }
                if (clickerBottomValue < 0f || clickerBottomValue > 1f) {
                    throw new IllegalArgumentException("clickerBottom must be between 0 and 1");
                }

                clickerStart.setGuidelinePercent(clickerStartValue);
                clickerEnd.setGuidelinePercent(clickerEndValue);
                clickerTop.setGuidelinePercent(clickerTopValue);
                clickerBottom.setGuidelinePercent(clickerBottomValue);
                if (showAreaValue)
                    clicker.setBackgroundColor(
                            ContextCompat.getColor(context, R.color.merkado_orange_transparent));
                clicker.setOnClickListener(v -> {
                    if (onClickListener != null) onClickListener.onClick(v);
                });
            } finally {
                a.recycle();
            }
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

}
