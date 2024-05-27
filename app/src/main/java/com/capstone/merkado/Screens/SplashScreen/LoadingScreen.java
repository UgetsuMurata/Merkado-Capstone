package com.capstone.merkado.Screens.SplashScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.R;

public class LoadingScreen extends AppCompatActivity {

    private Merkado merkado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        // initialize this activity's screen.
        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

    }
}