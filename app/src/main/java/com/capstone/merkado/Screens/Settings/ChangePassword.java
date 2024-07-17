package com.capstone.merkado.Screens.Settings;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctionPackage.AccountDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.DataFunctions;
import com.capstone.merkado.Helpers.StringVerifier;
import com.capstone.merkado.Helpers.WarningTextHelper;
import com.capstone.merkado.R;
import com.google.android.material.textfield.TextInputEditText;

public class ChangePassword extends AppCompatActivity {

    Merkado merkado;
    TextInputEditText oldPassword, newPassword, confirmPassword;
    TextView oldPasswordWarning, newPasswordWarning, confirmPasswordWarning;
    CardView cancel, save;
    String passwordString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_change_password);

        // initialize screen.
        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        // find views
        oldPassword = findViewById(R.id.old_password);
        oldPasswordWarning = findViewById(R.id.old_password_warning);
        newPassword = findViewById(R.id.new_password);
        newPasswordWarning = findViewById(R.id.new_password_warning);
        confirmPassword = findViewById(R.id.confirm_password);
        confirmPasswordWarning = findViewById(R.id.confirm_password_warning);
        cancel = findViewById(R.id.cancel);
        save = findViewById(R.id.save);
        ImageView backButton = findViewById(R.id.back_button);

        // hide warnings
        WarningTextHelper.hide(oldPasswordWarning);
        WarningTextHelper.hide(newPasswordWarning);
        WarningTextHelper.hide(confirmPasswordWarning);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        oldPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                WarningTextHelper.hide(oldPasswordWarning);
            }
        });
        newPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                WarningTextHelper.hide(newPasswordWarning);
            } else {
                if (newPassword.getText() == null || newPassword.getText().toString().isEmpty()) {
                    passwordString = "";
                } else if (StringVerifier.isValidPassword(newPassword.getText().toString())) {
                    WarningTextHelper.hide(newPasswordWarning);
                    passwordString = newPassword.getText().toString().trim();
                } else {
                    WarningTextHelper.showWarning(getApplicationContext(), newPasswordWarning, "Password must be at least 8 characters long and contain a mix of letters, numbers, and special characters.");
                    passwordString = "";
                }
            }
        });
        confirmPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                WarningTextHelper.hide(confirmPasswordWarning);
            } else {
                if (confirmPassword.getText() == null || confirmPassword.getText().toString().isEmpty())
                    return;
                if (passwordString == null || passwordString.isEmpty()) return;
                if (passwordString.equals(confirmPassword.getText().toString().trim())) {
                    WarningTextHelper.hide(confirmPasswordWarning);
                } else {
                    WarningTextHelper.showWarning(getApplicationContext(), confirmPasswordWarning, "Passwords do not match.");
                }
            }
        });
        save.setOnClickListener(v -> {
            oldPassword.clearFocus();
            newPassword.clearFocus();
            confirmPassword.clearFocus();
            updatePasswordButton();
        });
        cancel.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    /**
     * Verify the contents before updating password.
     */
    private void updatePasswordButton() {
        if (oldPassword.getText() == null || oldPassword.getText().toString().isEmpty()) {
            WarningTextHelper.showWarning(getApplicationContext(), oldPasswordWarning, "Input old password.");
            return;
        }
        AccountDataFunctions.comparePasswords(merkado.getAccount().getEmail(), oldPassword.getText().toString(), bool -> {
            if (bool) {
                if (newPassword.getText() == null || newPassword.getText().toString().isEmpty()) {
                    // if the password doesn't have something in it.
                    WarningTextHelper.showWarning(getApplicationContext(), newPasswordWarning, "Input new password.");
                } else if (confirmPassword.getText() == null || confirmPassword.getText().toString().trim().isEmpty()) {
                    // if the user hasn't confirmed their password.
                    WarningTextHelper.showWarning(getApplicationContext(), confirmPasswordWarning, "Confirm new password.");
                } else if (!newPassword.getText().toString().trim().equals(confirmPassword.getText().toString().trim())) {
                    // if the password and confirmPassword isn't equal
                    WarningTextHelper.showWarning(getApplicationContext(), confirmPasswordWarning, "Passwords do not match.");
                } else {
                    // hide the warnings.
                    WarningTextHelper.hide(confirmPasswordWarning);
                    WarningTextHelper.hide(newPasswordWarning);

                    // reset the password in database.
                    AccountDataFunctions.resetPassword(merkado.getAccount().getEmail(), newPassword.getText().toString());

                    // return to Sign In with RESULT_OK.
                    setResult(RESULT_OK);
                    finish();
                }
            } else {
                WarningTextHelper.showWarning(getApplicationContext(), oldPasswordWarning, "Incorrect password.");
            }
        });
    }
}