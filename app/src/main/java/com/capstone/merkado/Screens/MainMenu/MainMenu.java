package com.capstone.merkado.Screens.MainMenu;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.Account.SignIn;
import com.capstone.merkado.Screens.Account.SignOut;
import com.capstone.merkado.Screens.Settings.SettingsMenu;

public class MainMenu extends AppCompatActivity {

    private Merkado merkado;

    /**
     * VIEWS
     */
    private ImageView account, settings;
    private CardView play;

    /**
     * VARIABLES
     */
    private Account currentUser;
    private final ActivityResultLauncher<Intent> doSignIn = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent i = new Intent(getApplicationContext(), MainMenu.class);
                        overridePendingTransition(0, 0);
                        startActivity(i);
                        overridePendingTransition(0, 0);
                        finish();
                    }
                }
            });

    private final ActivityResultLauncher<Intent> doSignOut = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent i = new Intent(getApplicationContext(), MainMenu.class);
                        overridePendingTransition(0, 0);
                        startActivity(i);
                        overridePendingTransition(0, 0);
                        finish();
                    }
                }
            });


    private final ActivityResultLauncher<Intent> doSettings = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent i = new Intent(getApplicationContext(), MainMenu.class);
                        overridePendingTransition(0, 0);
                        startActivity(i);
                        overridePendingTransition(0, 0);
                        finish();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mai_main_menu);

        // initialize this activity's screen.
        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        // Set the activity to full-screen mode
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // find views
        account = findViewById(R.id.account_button);
        settings = findViewById(R.id.settings_button);
        play = findViewById(R.id.play);

        // get current account
        currentUser = merkado.getAccount();

        // change icon depending on account status (signed in or not), and set their click listeners.
        if (currentUser != null) {
            account.setOnClickListener(v -> doSignOut.launch(new Intent(getApplicationContext(), SignOut.class)));
            account.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_sign_out));
        } else {
            account.setOnClickListener(v -> doSignIn.launch(new Intent(getApplicationContext(), SignIn.class)));
            account.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_sign_in));
        }

        // navigate to settings
        settings.setOnClickListener(v -> doSettings.launch(new Intent(getApplicationContext(), SettingsMenu.class)));
    }
}