package com.capstone.merkado.Screens.Account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctionPackage.AccountDataFunctions;
import com.capstone.merkado.R;
import com.google.android.material.textfield.TextInputEditText;

public class SignIn extends AppCompatActivity {

    private Merkado merkado;
    private TextInputEditText email, password;
    private TextView emailWarning, passwordWarning;

    private final ActivityResultLauncher<Intent> doSignUp = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // if the result is okay, proceed to next screen
                    goToMainMenu();
                } else if (result.getResultCode() == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "Sign up cancelled.", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> doPasswordReset = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // if the result is okay, let user sign in with their new password.
                    Toast.makeText(getApplicationContext(), "Password reset successful. Please sign in with your new password.", Toast.LENGTH_SHORT).show();
                } else if (result.getResultCode() == RESULT_CANCELED) {
                    // continue to Sign In
                    Toast.makeText(getApplicationContext(), "Password reset cancelled", Toast.LENGTH_SHORT).show();
                }

            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acc_sign_in);

        // initialize this activity's screen.
        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        // get all elements
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        emailWarning = findViewById(R.id.email_warning);
        passwordWarning = findViewById(R.id.password_warning);
        CardView signIn = findViewById(R.id.sign_in);
        TextView forgotPassword = findViewById(R.id.forgot_password);
        TextView signUp = findViewById(R.id.sign_up);
        ImageView backButton = findViewById(R.id.back_button);

        // hide warnings
        emailWarning.setVisibility(View.GONE);
        passwordWarning.setVisibility(View.GONE);

        backButton.setOnClickListener(v -> new OnBackPressedDispatcher().onBackPressed());

        // set up Sign In listener
        signIn.setOnClickListener(v -> {
            Object emailObj = email.getText();
            Object passwordObj = password.getText();
            if (emailObj != null && passwordObj != null) {
                verifyCredentials(emailObj.toString(), passwordObj.toString());
            } else {
                if (emailObj == null) {
                    // if emailObj is null, then the user should input the email
                    emailWarning.setText(R.string.warning_input_email);
                    emailWarning.setVisibility(View.VISIBLE);
                } else {
                    // if the emailObj is not null, then the password was not input.
                    passwordWarning.setText(R.string.warning_input_password);
                    passwordWarning.setVisibility(View.VISIBLE);
                }
            }
        });

        // set up Forgot Password listener
        forgotPassword.setOnClickListener(v -> {
            // launch the activity result launcher, going to Reset Password screen.
            doPasswordReset.launch(new Intent(getApplicationContext(), ResetPassword.class));
        });

        // set up Sign Up listener
        signUp.setOnClickListener(v -> {
            // launch the activity result launcher, going to Sign Up screen.
            doSignUp.launch(new Intent(getApplicationContext(), SignUp.class));
        });
    }

    /**
     * Verify the credentials given for sign in.
     *
     * @param email    user's email.
     * @param password user's password.
     */
    private void verifyCredentials(String email, String password) {
        // hide warnings
        emailWarning.setVisibility(View.GONE);
        passwordWarning.setVisibility(View.GONE);
        // verify
        AccountDataFunctions.verifyAccount(email, password, (account, returnStatus) -> {
            if (account == null) return;
            switch (returnStatus) {
                case CANNOT_RETRIEVE_INFORMATION:
                    Toast.makeText(getApplicationContext(),
                            "Cannot retrieve information at the moment. Please try again later!",
                            Toast.LENGTH_SHORT).show();
                    break;
                case WRONG_EMAIL:
                    emailWarning.setText(R.string.warning_email_not_registered);
                    emailWarning.setVisibility(View.VISIBLE);
                    break;
                case WRONG_PASSWORD:
                    passwordWarning.setText(R.string.warning_incorrect_password);
                    passwordWarning.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    // save to SharedPref to keep signed in.
                    AccountDataFunctions.signInAccount(getApplicationContext(), account);
                    merkado.setAccount(account);
                    goToMainMenu();
                    break;
            }
        });
    }

    /**
     * Go to main menu.
     */
    private void goToMainMenu() {
        if (merkado.getAccount() == null) {
            Toast.makeText(getApplicationContext(), "Problem encountered. Try signing in later.", Toast.LENGTH_SHORT).show();
            return;
        }
        setResult(Activity.RESULT_OK);
        finish();
    }
}