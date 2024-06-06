package com.capstone.merkado.Screens.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.R;

public class SettingsMenu extends AppCompatActivity {

    private Merkado merkado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_settings_menu);

        // initialize screen using application instance.
        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);
    }
}