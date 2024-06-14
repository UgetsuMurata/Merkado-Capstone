package com.capstone.merkado.Screens.Account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctions;
import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.MainMenu.MainMenu;
import com.google.android.material.textfield.TextInputEditText;

public class SignIn extends AppCompatActivity {

    private Merkado merkado;
    private TextInputEditText email, password;
    private TextView emailWarning, passwordWarning;

    private final ActivityResultLauncher<Intent> doSignUp = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        // if the result is okay, proceed to next screen
                        goToMainMenu();
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        Toast.makeText(getApplicationContext(), "Sign up cancelled.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private final ActivityResultLauncher<Intent> doPasswordReset = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        // if the result is okay, let user sign in with their new password.
                        Toast.makeText(getApplicationContext(), "Password reset successful. Please sign in with your new password.", Toast.LENGTH_SHORT).show();
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        // continue to Sign In
                        Toast.makeText(getApplicationContext(), "Password reset cancelled", Toast.LENGTH_SHORT).show();
                    }

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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // set up Sign In listener
        signIn.setOnClickListener(v -> {
            Object emailObj = email.getText();
            Object passwordObj = password.getText();
            if (emailObj != null && passwordObj != null) {
                verifyCredentials(emailObj.toString(), passwordObj.toString());
            } else {
                if (emailObj == null) {
                    // if emailObj is null, then the user should input the email
                    emailWarning.setText("Input email!");
                    emailWarning.setVisibility(View.VISIBLE);
                } else {
                    // if the emailObj is not null, then the password was not input.
                    passwordWarning.setText("Input password!");
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
        DataFunctions.verifyAccount(getApplicationContext(), email, password, new DataFunctions.AccountReturn() {
            @Override
            public void accountReturn(Account account) {
                String username = account.getUsername();
                if (!username.matches("^\\[ERROR:[a-zA-Z_]+\\]$")) {
                    // save to SharedPref to keep signed in.
                    DataFunctions.signInAccount(getApplicationContext(), account);
                    merkado.setAccount(account);
                    goToMainMenu();
                } else {
                    // check if email exists
                    if (username.equals("[ERROR:WRONG_EMAIL]")) {
                        emailWarning.setText("Email wasn't registered. Please sign up.");
                        emailWarning.setVisibility(View.VISIBLE);
                    } else if (username.equals("[ERROR:WRONG_PASSWORD]")) {
                        passwordWarning.setText("Incorrect password!");
                        passwordWarning.setVisibility(View.VISIBLE);
                    }
                }
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
        DataFunctions.getEconomyBasic(merkado.getAccount(), economyBasicList -> merkado.setEconomyBasicList(economyBasicList));
        setResult(Activity.RESULT_OK);
        finish();
    }
}