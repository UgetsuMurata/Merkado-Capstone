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
import static com.capstone.merkado.CustomViews.WoodenButton.WoodenButtonStyle.PRIMARY;
import static com.capstone.merkado.CustomViews.WoodenButton.WoodenButtonStyle.SECONDARY;

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

@SuppressWarnings("unused")
public class WoodenButton extends ConstraintLayout {

    ImageView background;
    TextView text;
    ConstraintLayout itemView;
    WoodenButtonMode mode;
    WoodenButtonState state;
    WoodenButtonStyle style;
    WoodenButtonStateChangeSpeed changeSpeed;
    Context context;

    HashMap<WoodenButtonStyle, HashMap<WoodenButtonMode, HashMap<WoodenButtonState, Integer>>> styleMapping;
    View.OnClickListener onClickListener;
    Boolean showToast = false;
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

        styleMapping = populateDrawableMapping();

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WoodenButton, 0, 0);
            try {
                int mode = a.getInt(R.styleable.WoodenButton_buttonMode, 1);
                String text = a.getString(R.styleable.WoodenButton_buttonText);
                int state = a.getInt(R.styleable.WoodenButton_buttonState, 0);
                int speed = a.getInt(R.styleable.WoodenButton_changeSpeed, 1);
                int style = a.getInt(R.styleable.WoodenButton_primaryOrSecondary, 0);

                this.mode = mode == 0 ? SHORT : mode == 1 ? MEDIUM : LONG;
                this.state = state == 0 ? IDLE : state == 1 ? ACTIVE : DISABLED;
                this.style = style == 0 ? PRIMARY : SECONDARY;
                updateState();

                this.text.setText(text);
                this.changeSpeed = speed == 0 ? SLOW : speed == 1 ? NORMAL : FAST;
            } catch (Exception ignore) {
                a.recycle();
            }
        }

        itemView.setOnClickListener(v -> {
            if (onClickListener == null) return;
            if (state != DISABLED) {
                onClickListener.onClick(v);
                onClickDrawableChanges();
            } else if (showToast) Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
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
        Integer backgroundRes = getDrawable(this.style, this.mode, this.state);
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
    private Integer getDrawable(@NonNull WoodenButtonStyle style, @NonNull WoodenButtonMode mode, @NonNull WoodenButtonState state) {
        HashMap<WoodenButtonMode, HashMap<WoodenButtonState, Integer>> modeMapping = styleMapping.get(style);
        if (modeMapping == null) return null;
        HashMap<WoodenButtonState, Integer> stateMapping = modeMapping.get(mode);
        if (stateMapping == null) return null;
        return stateMapping.get(state);
    }

    private Long getSpeed(@NonNull WoodenButtonStateChangeSpeed changeSpeed) {
        return changeSpeed == SLOW ? 500L : changeSpeed == NORMAL ? 100L : 50L;
    }

    private HashMap<WoodenButtonStyle, HashMap<WoodenButtonMode, HashMap<WoodenButtonState, Integer>>> populateDrawableMapping() {
        HashMap<WoodenButtonStyle, HashMap<WoodenButtonMode, HashMap<WoodenButtonState, Integer>>> styleMapping = new HashMap<>();

        // Populate PRIMARY style mappings
        HashMap<WoodenButtonMode, HashMap<WoodenButtonState, Integer>> primaryMapping = new HashMap<>();

        primaryMapping.put(SHORT, createStateMapping(
                R.drawable.gui_general_button_primary_short_idle,
                R.drawable.gui_general_button_primary_short_active,
                R.drawable.gui_general_button_primary_short_disabled
        ));

        primaryMapping.put(MEDIUM, createStateMapping(
                R.drawable.gui_general_button_primary_medium_idle,
                R.drawable.gui_general_button_primary_medium_active,
                R.drawable.gui_general_button_primary_medium_disabled
        ));

        primaryMapping.put(LONG, createStateMapping(
                R.drawable.gui_general_button_primary_long_idle,
                R.drawable.gui_general_button_primary_long_active,
                R.drawable.gui_general_button_primary_long_disabled
        ));

        styleMapping.put(PRIMARY, primaryMapping);

        // Populate SECONDARY style mappings
        HashMap<WoodenButtonMode, HashMap<WoodenButtonState, Integer>> secondaryMapping = new HashMap<>();

        secondaryMapping.put(SHORT, createStateMapping(
                R.drawable.gui_general_button_secondary_long_idle,
                R.drawable.gui_general_button_secondary_long_idle,
                R.drawable.gui_general_button_secondary_long_idle
        ));

        secondaryMapping.put(MEDIUM, createStateMapping(
                R.drawable.gui_general_button_secondary_long_idle,
                R.drawable.gui_general_button_secondary_long_idle,
                R.drawable.gui_general_button_secondary_long_idle
        ));

        secondaryMapping.put(LONG, createStateMapping(
                R.drawable.gui_general_button_secondary_long_idle,
                R.drawable.gui_general_button_secondary_long_idle,
                R.drawable.gui_general_button_secondary_long_idle
        ));

        styleMapping.put(SECONDARY, secondaryMapping);

        return styleMapping;
    }

    // Helper method to create the state mappings
    private HashMap<WoodenButtonState, Integer> createStateMapping(int idleDrawable, int activeDrawable, int disabledDrawable) {
        HashMap<WoodenButtonState, Integer> stateMapping = new HashMap<>();
        stateMapping.put(IDLE, idleDrawable);
        stateMapping.put(ACTIVE, activeDrawable);
        stateMapping.put(DISABLED, disabledDrawable);
        return stateMapping;
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

    public enum WoodenButtonStyle {
        PRIMARY, SECONDARY
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
