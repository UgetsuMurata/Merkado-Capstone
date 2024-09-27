package com.capstone.merkado.CustomViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.capstone.merkado.R;

public class ChoiceSwitch extends ConstraintLayout {

    OnChooseListener onChooseListener;
    Boolean choice1Chosen = true;

    public ChoiceSwitch(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ChoiceSwitch(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChoiceSwitch(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.custom_choice_switch, this, true);
        TextView choice1 = findViewById(R.id.choice_1);
        TextView choice2 = findViewById(R.id.choice_2);
        View highlightAnimator = findViewById(R.id.animate_switching);
        choice1Chosen = true;

        setOnClickListener(v -> {
            choice1Chosen = !choice1Chosen;
            if (onChooseListener != null) onChooseListener.onClick(choice1Chosen);
            if (choice1Chosen) {
                choice1.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                choice2.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                for (int i = 50 ; i >= 0; i--) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) highlightAnimator.getLayoutParams();
                    params.weight = i;
                    highlightAnimator.setLayoutParams(params);
                }
            } else {
                choice1.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                choice2.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                for (int i = 0 ; i <= 50; i++) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) highlightAnimator.getLayoutParams();
                    params.weight = i;
                    highlightAnimator.setLayoutParams(params);
                }
            }
        });
    }

    public void setOnChooseListener(OnChooseListener onChooseListener) {
        this.onChooseListener = onChooseListener;
    }

    public interface OnChooseListener {
        void onClick(Boolean choice1Chosen);
    }

}
