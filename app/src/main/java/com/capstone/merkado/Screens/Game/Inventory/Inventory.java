package com.capstone.merkado.Screens.Game.Inventory;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.R;

public class Inventory extends AppCompatActivity {

    Merkado merkado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gam_inv_inventory);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);
    }
}