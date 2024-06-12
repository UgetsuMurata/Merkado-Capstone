package com.capstone.merkado.Screens.Economy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.Account.SignUp;
import com.capstone.merkado.Screens.LoadingScreen.SplashScreen;
import com.capstone.merkado.Screens.MainMenu.MainMenu;

public class AddEconomy extends AppCompatActivity {

    Merkado merkado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eco_add_economy);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        ImageView closeButton = findViewById(R.id.close_button);
        TextView create_server = findViewById(R.id.cserver);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Close the activity
            }
        });

        // set up Sign Up listener
        create_server.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), CreateEconomy.class));
            finish();
        });

    }
}