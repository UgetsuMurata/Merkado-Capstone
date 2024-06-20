package com.capstone.merkado.CustomViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.capstone.merkado.R;

import java.util.Locale;

public class PlayerLevelView extends ConstraintLayout {

    private ProgressBar playerLevelDisplay;
    private TextView playerLevel;
    private TextView playerExperience;

    public PlayerLevelView(Context context) {
        super(context);
        init(context);
    }

    public PlayerLevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PlayerLevelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.custom_player_level_view, this, true);
        playerLevelDisplay = findViewById(R.id.custom_m_player_level_display);
        playerLevel = findViewById(R.id.custom_m_player_level);
        playerExperience = findViewById(R.id.custom_m_player_exp);
    }

    public void setExperience(int previousMaxExperience, int currentExperience, int maxExperience) {
        playerLevelDisplay.setMax(maxExperience - previousMaxExperience);
        playerLevelDisplay.setProgress(currentExperience - previousMaxExperience);
        playerExperience.setText(String.format(Locale.getDefault(), "%d/%d", currentExperience, maxExperience));
    }

    public void setPlayerLevel(int level) {
        playerLevel.setText(level);
    }
}
