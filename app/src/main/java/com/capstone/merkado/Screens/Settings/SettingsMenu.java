package com.capstone.merkado.Screens.Settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.R;

public class SettingsMenu extends AppCompatActivity {

    private int activityResult = Activity.RESULT_CANCELED;
    private final ActivityResultLauncher<Intent> doChangeUsername = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                activityResult = activityResult != RESULT_OK ? result.getResultCode() : RESULT_OK;
                if (result.getResultCode() == RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Username saved succcessfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Username saved cancelled.", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> doChangePassword = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                activityResult = activityResult != RESULT_OK ? result.getResultCode() : RESULT_OK;
                if (result.getResultCode() == RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Password changed succcessfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Password change cancelled.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_settings_menu);

        // initialize screen using application instance.
        Merkado merkado = Merkado.getInstance();
        merkado.initializeScreen(this);
        ImageView backButton = findViewById(R.id.back_button);

        // find the cardviews
        CardView changeUsername = findViewById(R.id.change_username);
        CardView changePassword = findViewById(R.id.change_password);
        CardView about = findViewById(R.id.about);
        CardView termsAndConditions = findViewById(R.id.terms_and_conditions);

        // navigate to those
        changeUsername.setOnClickListener(v -> doChangeUsername.launch(new Intent(getApplicationContext(), ChangeUsername.class)));
        changePassword.setOnClickListener(v -> doChangePassword.launch(new Intent(getApplicationContext(), ChangePassword.class)));
        about.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), About.class)));
        termsAndConditions.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), TermsAndConditions.class)));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                setResult(activityResult);
                finish();
            }
        });
    }

}