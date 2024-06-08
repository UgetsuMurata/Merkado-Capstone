package com.capstone.merkado.Screens.Settings;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctions;
import com.capstone.merkado.Helpers.StringVerifier;
import com.capstone.merkado.Helpers.StringVerifier.UsernameCode;
import com.capstone.merkado.Helpers.WarningTextHelper;
import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.R;
import com.google.android.material.textfield.TextInputEditText;

public class ChangeUsername extends AppCompatActivity {

    Merkado merkado;
    TextInputEditText username;
    TextView usernameWarning;
    CardView cancel, save;
    Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_change_username);

        // initialize Merkado and set screen.
        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        // get account details
        account = merkado.getAccount();
        if (account == null) {
            Toast.makeText(this, "Cannot retrieve your account right now. Please try again later.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // find views
        username = findViewById(R.id.username);
        usernameWarning = findViewById(R.id.username_warning);
        cancel = findViewById(R.id.cancel);
        save = findViewById(R.id.save);

        username.setText(account.getUsername());
        username.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                WarningTextHelper.hide(usernameWarning);
            } else {
                if (username.getText() == null || username.getText().toString().isEmpty()) return;
                UsernameCode usernameCode = StringVerifier.validateUsername(username.getText().toString());
                // check if username is valid.
                if (usernameCode.equals(UsernameCode.VALID)) {
                    WarningTextHelper.hide(usernameWarning);
                } else {
                    warningUsername(usernameCode);
                }
            }
        });
        cancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        save.setOnClickListener(v -> {
            if (username.getText() == null || username.getText().toString().isEmpty()) {
                WarningTextHelper.showWarning(getApplicationContext(), usernameWarning, "Input your desired username.");
            } else {
                Toast.makeText(getApplicationContext(), "Saving username.", Toast.LENGTH_SHORT).show();

                // save username in Firebase and Shared Preferences.
                saveUsername(username.getText().toString(), account.getEmail());

                // save new username in Merkado application java class.
                account.setUsername(username.getText().toString());
                merkado.setAccount(account);
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    /**
     * Method caller for saving username.
     *
     * @param username raw username.
     * @param email    raw email.
     */
    private void saveUsername(String username, String email) {
        DataFunctions.changeUsername(this, username, email);
    }

    private void warningUsername(UsernameCode code) {
        switch (code) {
            case INVALID_CHARACTERS:
                WarningTextHelper.showWarning(getApplicationContext(), usernameWarning, "Username can only contain letters, numbers, underscores, and periods.");
                break;
            case HAS_PROFANITY:
                WarningTextHelper.showWarning(getApplicationContext(), usernameWarning, "Username cannot contain offensive language.");
                break;
            case INVALID_LENGTH:
                WarningTextHelper.showWarning(getApplicationContext(), usernameWarning, "Username must be between 3 and 15 characters long.");
                break;
            default:
                break;
        }
    }
}