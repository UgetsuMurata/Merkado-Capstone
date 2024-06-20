package com.capstone.merkado.CustomViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.capstone.merkado.R;

import java.util.Locale;

public class PlayerBalanceView extends ConstraintLayout {
    private TextView playerBalance;

    public PlayerBalanceView(Context context) {
        super(context);
        init(context);
    }

    public PlayerBalanceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PlayerBalanceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.custom_player_balance_view, this, true);
        playerBalance = findViewById(R.id.custom_m_balance);
    }

    public void setBalance(float balance) {
        playerBalance.setText(String.format(Locale.getDefault(), "%.2f", balance));
    }
}
