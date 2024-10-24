package com.capstone.merkado.Screens.MainMenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.Account.SignIn;
import com.capstone.merkado.Screens.Account.SignOut;
import com.capstone.merkado.Screens.Settings.SettingsMenu;

public class MainMenu extends AppCompatActivity {

    private final ActivityResultLauncher<Intent> doSignIn = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent i = new Intent(getApplicationContext(), MainMenu.class);
                    overridePendingTransition(0, 0);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                    finish();
                }
            });

    private final ActivityResultLauncher<Intent> doSignOut = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent i = new Intent(getApplicationContext(), MainMenu.class);
                    overridePendingTransition(0, 0);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                    finish();
                }
            });

    private final ActivityResultLauncher<Intent> doSettings = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent i = new Intent(getApplicationContext(), MainMenu.class);
                    overridePendingTransition(0, 0);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mai_main_menu);

        // initialize this activity's screen.
        Merkado merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        // find views
        ImageView account = findViewById(R.id.account_button);
        ImageView settings = findViewById(R.id.settings_button);
        ImageView play = findViewById(R.id.play);
        TextView accountText = findViewById(R.id.text_sign_out);

        // get current account
        Account currentUser = merkado.getAccount();

        // welcome the user if signed in
        if (currentUser != null) {
            Toast.makeText(this, String.format("Welcome, %s", currentUser.getUsername()), Toast.LENGTH_SHORT).show();
        }

        // change icon and text depending on account status (signed in or not), and set their click listeners.
        if (currentUser != null) {
            account.setOnClickListener(v -> doSignOut.launch(new Intent(getApplicationContext(), SignOut.class)));
            account.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_sign_out));
            accountText.setText("SIGN OUT");  // Set text to "SIGN OUT" when the user is signed in
            settings.setVisibility(View.VISIBLE);
        } else {
            account.setOnClickListener(v -> doSignIn.launch(new Intent(getApplicationContext(), SignIn.class)));
            account.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_sign_in));
            accountText.setText("SIGN IN");  // Set text to "SIGN IN" when the user is not signed in
            settings.setVisibility(View.GONE);
        }

        // navigate to settings
        settings.setOnClickListener(v -> doSettings.launch(new Intent(getApplicationContext(), SettingsMenu.class)));

        play.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Lobby.class)));

        merkado.setPlayer(null, null);
    }


}