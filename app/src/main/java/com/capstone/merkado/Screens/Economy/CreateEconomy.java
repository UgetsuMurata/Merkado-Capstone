package com.capstone.merkado.Screens.Economy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.R;

public class CreateEconomy extends AppCompatActivity {

    Merkado merkado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_economy);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);
    }
}

