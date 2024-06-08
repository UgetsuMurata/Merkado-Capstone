package com.capstone.merkado.Screens.Account;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctions;
import com.capstone.merkado.Helpers.Generator;
import com.capstone.merkado.Helpers.NotificationHelper;
import com.capstone.merkado.Helpers.StringVerifier;
import com.capstone.merkado.Helpers.WarningTextHelper;
import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.Objects.VerificationCode;
import com.capstone.merkado.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;
import java.util.Random;

public class SignUp extends AppCompatActivity {

    private Merkado merkado;
    /**
     * VIEWS
     */
    private LinearLayout page1, page2;
    private TextInputEditText email, verificationCode, password, confirmPassword;
    private TextView emailWarning, verificationCodeWarning, passwordWarning, confirmPasswordWarning;

    /**
     * ACTIVITY VARIABLES
     */
    private VerificationCode savedCode;
    private Integer tries = 0;
    private Long pauseTimerDisplay = 0L;
    private CountDownTimer countDownTimer;
    private Boolean emailExists = false;
    private static final long START_TIME_IN_MILLIS = 120000; // 2 minutes
    private long timeLeftInMillis = START_TIME_IN_MILLIS;
    private String emailInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acc_sign_up);

        // initialize this activity's screen.
        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        // find views for page 1
        page1 = findViewById(R.id.sign_up_1);
        email = findViewById(R.id.email);
        verificationCode = findViewById(R.id.verification_code);
        emailWarning = findViewById(R.id.email_warning);
        verificationCodeWarning = findViewById(R.id.verification_code_warning);
        CardView verificationCodeButton = findViewById(R.id.verification_code_button);
        CardView next = findViewById(R.id.next);

        // find views for page 2
        page2 = findViewById(R.id.sign_up_2);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        passwordWarning = findViewById(R.id.password_warning);
        confirmPasswordWarning = findViewById(R.id.confirm_password_warning);
        TextView signIn = findViewById(R.id.sign_in_page_1);
        TextView signIn2 = findViewById(R.id.sign_in_page_2);
        CardView signUp = findViewById(R.id.sign_up);
        ImageView goBack = findViewById(R.id.go_back);

        // Hide page 2, show page 1
        page1.setVisibility(View.VISIBLE);
        page2.setVisibility(View.GONE);

        // hide warnings
        WarningTextHelper.hide(emailWarning);
        WarningTextHelper.hide(verificationCodeWarning);
        WarningTextHelper.hide(passwordWarning);
        WarningTextHelper.hide(confirmPasswordWarning);

        /*
         * PAGE 1 FUNCTIONALITIES: EMAIL VERIFICATION
         */

        // if email is on focus, close warning.
        email.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                emailWarning.setVisibility(View.GONE);
            } else {
                String emailText = email.getText() != null ? email.getText().toString() : "";
                if (StringVerifier.isValidGmail(emailText)) {
                    DataFunctions.emailExists(emailText, bool -> {
                        if (bool) {
                            runOnUiThread(() -> WarningTextHelper.showWarning(getApplicationContext(), emailWarning, "Email already exists."));
                            emailExists = true;
                        } else {
                            emailExists = false;
                        }
                    });
                }
            }
        });

        // generate the code and send it to user.
        verificationCodeButton.setOnClickListener(v -> {
            if (sendCode()) {
                emailWarning.setVisibility(View.GONE);

                // Show information about the countdown.
                WarningTextHelper.showInfo(getApplicationContext(), verificationCodeWarning, "Wait for the code. 2:00.");
                stopTimer();
                startCountdownTimer();
            } else {
                if (email.getText() == null || email.getText().toString().isEmpty())
                    // Check email value as it should not be empty.
                    WarningTextHelper.showWarning(getApplicationContext(), emailWarning, "Please input a valid Google E-Mail.");
                else if (!StringVerifier.isValidGmail(email.getText().toString()))
                    // Check email value if it is a valid GMail.
                    WarningTextHelper.showWarning(getApplicationContext(), emailWarning, "Google E-Mail is invalid. Please input a valid Google E-Mail.");
                else if (emailExists)
                    // Email exists.
                    WarningTextHelper.showWarning(getApplicationContext(), emailWarning, "Email already exists.");
            }
        });

        // Check the code and try to go to next page.
        next.setOnClickListener(v -> {
            if (email.getText() == null || email.getText().toString().isEmpty()) {
                // if there is no email input.
                stopTimer();
                WarningTextHelper.hide(verificationCodeWarning);
                WarningTextHelper.showWarning(getApplicationContext(), emailWarning, "Please input a valid Google E-Mail.");
            } else if (!timerStarted() || savedCode == null) {
                stopTimer();
                WarningTextHelper.showInfo(getApplicationContext(), verificationCodeWarning, "Please generate a code.");
            } else if (verificationCode.getText() == null || verificationCode.getText().toString().isEmpty()) {
                // if there is no verification code input.
                WarningTextHelper.showWarning(getApplicationContext(), verificationCodeWarning, "Please input the verification code.");
                pauseTimer();
            } else if (!email.getText().toString().equals(savedCode.getEmail())) {
                // if the email input does not match the saved email-code.
                stopTimer();
                WarningTextHelper.showWarning(getApplicationContext(), verificationCodeWarning, "Email changed. Please generate a new code.");
                savedCode = null;
            } else if (!verificationCode.getText().toString().equals(savedCode.getCode())) {
                // if the verification code does not match the input
                if (tries < 3) {
                    WarningTextHelper.showWarning(getApplicationContext(), verificationCodeWarning, String.format("Incorrect code. Try again.\nTries: %s Left", 3 - tries));
                    pauseTimer();
                    tries += 1;
                } else {
                    stopTimer();
                    WarningTextHelper.showWarning(getApplicationContext(), verificationCodeWarning, "No more tries. Please generate another code.");
                    tries = 0;
                    savedCode = null;
                }
            } else {
                page1.setVisibility(View.GONE);
                page2.setVisibility(View.VISIBLE);
                emailInput = savedCode.getEmail();
                WarningTextHelper.hide(emailWarning);
                WarningTextHelper.hide(verificationCodeWarning);
                stopTimer();
            }
        });


        /*
         * PAGE 2 FUNCTIONALITIES: PASSWORD AND FINISH SIGN UP
         */

        // check if password input is valid.
        password.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                WarningTextHelper.hide(passwordWarning);
            } else {
                String passwordText = password.getText() != null ? password.getText().toString().trim() : "";
                if (passwordText.isEmpty()) return;
                if (!StringVerifier.isValidPassword(passwordText)) {
                    WarningTextHelper.showWarning(getApplicationContext(), passwordWarning, "Password must be at least 8 characters long and contain a mix of letters, numbers, and special characters.");
                } else {
                    WarningTextHelper.hide(passwordWarning);
                }
            }
        });

        // check if "password" and "confirm password" matches.
        confirmPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                WarningTextHelper.hide(confirmPasswordWarning);
            } else {
                String passwordText = password.getText() != null ? password.getText().toString().trim() : "";
                String confirmPasswordText = confirmPassword.getText() != null ? confirmPassword.getText().toString().trim() : "";
                if (confirmPasswordText.isEmpty()) return;
                if (!passwordText.equals(confirmPasswordText)) {
                    WarningTextHelper.showWarning(getApplicationContext(), confirmPasswordWarning, "Passwords do not match.");
                } else {
                    WarningTextHelper.hide(passwordWarning);
                }
            }
        });

        signUp.setOnClickListener(v -> {
            String passwordText = password.getText() != null ? password.getText().toString().trim() : "";
            String confirmPasswordText = confirmPassword.getText() != null ? confirmPassword.getText().toString().trim() : "";
            if (passwordText.isEmpty())
                WarningTextHelper.showWarning(getApplicationContext(), passwordWarning, "Input password.");
            else if (confirmPasswordText.isEmpty())
                WarningTextHelper.showWarning(getApplicationContext(), confirmPasswordWarning, "Confirm your password.");
            else if (!passwordText.equals(confirmPasswordText))
                WarningTextHelper.showWarning(getApplicationContext(), confirmPasswordWarning, "Passwords do not match.");
            else {
                passwordInput = passwordText;

                // Sign up the account and Sign it in.
                Account account = DataFunctions.signUpAccount(emailInput, passwordInput);
                DataFunctions.signInAccount(getApplicationContext(), account);
                merkado.setAccount(account);
                Toast.makeText(getApplicationContext(), "Sign up successful!", Toast.LENGTH_SHORT).show();
                // go back to sign in and return RESULT_OK.
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        // Go back to page 1.
        goBack.setOnClickListener(v -> {
            password.setText("");
            confirmPassword.setText("");
            WarningTextHelper.hide(passwordWarning);
            WarningTextHelper.hide(confirmPasswordWarning);

            page1.setVisibility(View.VISIBLE);
            page2.setVisibility(View.GONE);
        });

        // Finish this activity and go back to sign in.
        signIn.setOnClickListener(v -> {
            // go back to sign in and return RESULT_CANCELED.
            setResult(Activity.RESULT_CANCELED);
            finish();
        });
        signIn2.setOnClickListener(v -> {
            // go back to sign in and return RESULT_CANCELED.
            setResult(Activity.RESULT_CANCELED);
            finish();
        });
    }

    /**
     * Generates and sends a 4-digit code to the provided email.
     *
     * @return Returns <b>false</b> if the email is invalid (empty, null, or invalid GMail account), <b>true</b> if the process is successful.
     */
    private Boolean sendCode() {
        // Check email value as it should not be empty.
        if (!(email.getText() != null && !email.getText().toString().isEmpty())) return false;

        // Check email value if it is a valid GMail.
        if (!StringVerifier.isValidGmail(email.getText().toString())) return false;

        // Check email value if it exists.
        if (emailExists) return false;

        // Generate Code.
        savedCode = Generator.code(email.getText().toString());

        // Send the code through API.
        DataFunctions.sendCodeThroughEmail(savedCode);
        NotificationHelper.sendNotification(getApplicationContext(),
                String.format("Verification code sent to %s", savedCode.getEmail()),
                String.format("Your Verification Code: %s.", savedCode.getCode()));
        return true;
    }

    /**
     * Stars the CountDownTimer.
     */
    private void startCountdownTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                WarningTextHelper.showWarning(getApplicationContext(), verificationCodeWarning, "Code expired. Please generate another code.");
                savedCode = null;
                timeLeftInMillis = START_TIME_IN_MILLIS;
            }
        }.start();
    }

    /**
     * Updates the TextView with the remaining time.
     */
    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        if (pauseTimerDisplay == 0 || System.currentTimeMillis() - pauseTimerDisplay > 2000)
            WarningTextHelper.showInfo(getApplicationContext(), verificationCodeWarning, String.format("Wait for the code. %s.", timeLeftFormatted));
    }

    /**
     * Defines the pauseTimerDisplay with the current millis.
     */
    private void pauseTimer() {
        pauseTimerDisplay = System.currentTimeMillis();
    }

    /**
     * Stops the CountDownTimer.
     */
    private void stopTimer() {
        if (countDownTimer == null) return;
        timeLeftInMillis = START_TIME_IN_MILLIS;
        countDownTimer.cancel();
        countDownTimer = null;
    }

    private Boolean timerStarted() {
        return countDownTimer != null;
    }
}