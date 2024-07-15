package com.capstone.merkado.CustomViews;

import static com.capstone.merkado.CustomViews.WoodenButton.WoodenButtonMode.LONG;
import static com.capstone.merkado.CustomViews.WoodenButton.WoodenButtonMode.MEDIUM;
import static com.capstone.merkado.CustomViews.WoodenButton.WoodenButtonMode.SHORT;
import static com.capstone.merkado.CustomViews.WoodenButton.WoodenButtonState.ACTIVE;
import static com.capstone.merkado.CustomViews.WoodenButton.WoodenButtonState.DISABLED;
import static com.capstone.merkado.CustomViews.WoodenButton.WoodenButtonState.IDLE;
import static com.capstone.merkado.CustomViews.WoodenButton.WoodenButtonStateChangeSpeed.FAST;
import static com.capstone.merkado.CustomViews.WoodenButton.WoodenButtonStateChangeSpeed.NORMAL;
import static com.capstone.merkado.CustomViews.WoodenButton.WoodenButtonStateChangeSpeed.SLOW;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.capstone.merkado.R;

import java.util.HashMap;

public class WoodenButton extends ConstraintLayout {

    ImageView background;
    TextView text;
    ConstraintLayout itemView;
    WoodenButtonMode mode;
    WoodenButtonState state;
    WoodenButtonStateChangeSpeed changeSpeed;
    Context context;

    HashMap<WoodenButtonMode, HashMap<WoodenButtonState, Integer>> drawableMapping;
    View.OnClickListener onClickListener;
    Boolean showToast = true;
    String toastMessage = "This button is disabled.";

    public WoodenButton(@NonNull Context context) {
        this(context, null, 0, 0);
    }

    public WoodenButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public WoodenButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public WoodenButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.custom_general_medium_button, this, true);
        this.context = context;

        text = findViewById(R.id.text);
        background = findViewById(R.id.background);
        itemView = findViewById(R.id.item_view);

        drawableMapping = populateDrawableMapping();

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WoodenButton, 0, 0);
            try {
                int mode = a.getInt(R.styleable.WoodenButton_buttonMode, 1);
                String text = a.getString(R.styleable.WoodenButton_buttonText);
                int state = a.getInt(R.styleable.WoodenButton_buttonState, 0);
                int speed = a.getInt(R.styleable.WoodenButton_changeSpeed, 1);

                this.mode = mode == 0 ? SHORT : mode == 1 ? MEDIUM : LONG;
                this.state = state == 0 ? IDLE : state == 1 ? ACTIVE : DISABLED;
                updateState();

                this.text.setText(text);
                this.changeSpeed = speed == 0 ? SLOW : speed == 1 ? NORMAL : FAST;
            } catch (Exception ignore) {
                a.recycle();
            }
        }

        itemView.setOnClickListener(v -> {
            if (onClickListener != null && state != DISABLED) onClickListener.onClick(v);
            else if (onClickListener != null) Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
            onClickDrawableChanges();
        });
    }

    public void setText(@NonNull String text) {
        this.text.setText(text);
    }

    public void setDrawableStateChangeSpeed(WoodenButtonStateChangeSpeed speed) {
        this.changeSpeed = speed;
    }

    /**
     * This sets the message shown when button is clicked during disabled.
     *
     * @param message Message to be sent. If {@code Null}, no toast message will be sent.
     */
    public void setDisabledClickToast(@Nullable String message) {
        if (message != null) {
            toastMessage = message;
        } else showToast = false;
    }

    public void setMode(WoodenButtonMode mode) {
        this.mode = mode;
        updateState();
    }

    public void disable() {
        this.state = DISABLED;
        updateState();
    }

    public void enable() {
        this.state = IDLE;
        updateState();
    }

    private void updateState() {
        Integer backgroundRes = getDrawable(this.mode, this.state);
        if (backgroundRes != null)
            background.setImageDrawable(ContextCompat.getDrawable(context, backgroundRes));
    }

    private void onClickDrawableChanges() {
        this.state = ACTIVE;
        updateState();
        new Handler().postDelayed(() -> {
            this.state = IDLE;
            updateState();
        }, getSpeed(this.changeSpeed));
    }

    @Nullable
    private Integer getDrawable(@NonNull WoodenButtonMode mode, @NonNull WoodenButtonState state) {
        HashMap<WoodenButtonState, Integer> stateMapping = drawableMapping.get(mode);
        if (stateMapping == null) return null;
        return stateMapping.get(state);
    }

    private Long getSpeed(@NonNull WoodenButtonStateChangeSpeed changeSpeed) {
        return changeSpeed == SLOW ? 500L : changeSpeed == NORMAL ? 100L : 50L;
    }

    private HashMap<WoodenButtonMode, HashMap<WoodenButtonState, Integer>> populateDrawableMapping() {
        HashMap<WoodenButtonMode, HashMap<WoodenButtonState, Integer>> drawableMapping = new HashMap<>();
        HashMap<WoodenButtonState, Integer> stateMapping = new HashMap<>();
        stateMapping.put(IDLE, R.drawable.gui_general_short_idle);
        stateMapping.put(ACTIVE, R.drawable.gui_general_short_active);
        stateMapping.put(DISABLED, R.drawable.gui_general_short_disabled);
        drawableMapping.put(SHORT, stateMapping);
        stateMapping.clear();
        stateMapping.put(IDLE, R.drawable.gui_general_medium_idle);
        stateMapping.put(ACTIVE, R.drawable.gui_general_medium_active);
        stateMapping.put(DISABLED, R.drawable.gui_general_medium_disabled);
        drawableMapping.put(MEDIUM, stateMapping);
        stateMapping.clear();
        stateMapping.put(IDLE, R.drawable.gui_general_long_idle);
        stateMapping.put(ACTIVE, R.drawable.gui_general_long_active);
        stateMapping.put(DISABLED, R.drawable.gui_general_long_disabled);
        drawableMapping.put(LONG, stateMapping);
        return drawableMapping;
    }

    public enum WoodenButtonMode {
        SHORT, MEDIUM, LONG
    }

    public enum WoodenButtonState {
        IDLE, ACTIVE, DISABLED
    }

    public enum WoodenButtonStateChangeSpeed {
        SLOW, NORMAL, FAST
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
