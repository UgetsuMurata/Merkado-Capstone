package com.capstone.merkado.Screens.Economy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.R;

public class SettingsEconomy extends AppCompatActivity {

    Merkado merkado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eco_settings_economy);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

    }
}