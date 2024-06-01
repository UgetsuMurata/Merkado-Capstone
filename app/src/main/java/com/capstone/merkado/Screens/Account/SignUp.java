package com.capstone.merkado.Screens.Account;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctions;
import com.capstone.merkado.Helpers.StringVerifier;
import com.capstone.merkado.Helpers.WarningTextHelper;
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
    private TextView emailWarning, verificationCodeWarning, passwordWarning, confirmPasswordWarning, signIn;
    private CardView verificationCodeButton, next, signUp;
    private ImageView goBack;

    /**
     * ACTIVITY VARIABLES
     */
    private VerificationCode savedCode;
    private Integer tries = 0;
    private Long pauseTimerDisplay = 0L;
    private CountDownTimer countDownTimer;
    private static final long START_TIME_IN_MILLIS = 120000; // 2 minutes
    private long timeLeftInMillis = START_TIME_IN_MILLIS;
    private String emailInput;
    private String passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        // initialize this activity's screen.
        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        // find views for page 1
        page1 = findViewById(R.id.sign_up_1);
        email = findViewById(R.id.email);
        verificationCode = findViewById(R.id.verification_code);
        emailWarning = findViewById(R.id.email_warning);
        verificationCodeWarning = findViewById(R.id.verification_code_warning);
        verificationCodeButton = findViewById(R.id.verification_code_button);
        next = findViewById(R.id.next);

        // find views for page 2
        page2 = findViewById(R.id.sign_up_2);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        passwordWarning = findViewById(R.id.password_warning);
        confirmPasswordWarning = findViewById(R.id.confirm_password_warning);
        signIn = findViewById(R.id.sign_in);
        signUp = findViewById(R.id.sign_up);
        goBack = findViewById(R.id.go_back);

        // if email is on focus, close warning.
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    emailWarning.setVisibility(View.GONE);
                }
            }
        });

        // generate the code and send it to user.
        verificationCodeButton.setOnClickListener(v -> {
            if (sendCode()) {
                emailWarning.setVisibility(View.GONE);

                // Show information about the countdown.
                WarningTextHelper.showInfo(getApplicationContext(), verificationCodeWarning, "Wait for the code. 2:00.");
                startCountdownTimer();
            } else {
                if (email.getText() == null || email.getText().toString().isEmpty())
                    // Check email value as it should not be empty.
                    WarningTextHelper.showWarning(getApplicationContext(), emailWarning, "Please input a valid Google E-Mail.");
                else if (!StringVerifier.isValidGmail(email.getText().toString()))
                    // Check email value if it is a valid GMail.
                    WarningTextHelper.showWarning(getApplicationContext(), emailWarning, "Google E-Mail is invalid. Please input a valid Google E-Mail.");
            }
        });

        // Check the code and try to go to next page.
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText() == null || email.getText().toString().isEmpty()) {
                    // if there is no email input.
                    WarningTextHelper.showWarning(getApplicationContext(), emailWarning, "Please input a valid Google E-Mail.");
                    return;
                }
                if (!timerStarted() || savedCode == null) {
                    WarningTextHelper.showInfo(getApplicationContext(), verificationCodeWarning, "Please generate a code.");
                    return;
                }
                if (verificationCode.getText() == null || verificationCode.getText().toString().isEmpty()) {
                    // if there is no verification code input.
                    WarningTextHelper.showWarning(getApplicationContext(), verificationCodeWarning, "Please input the verification code.");
                    pauseTimer();
                    return;
                }
                if (!email.getText().toString().equals(savedCode.getEmail())) {
                    // if the email input does not match the saved email-code.
                    WarningTextHelper.showWarning(getApplicationContext(), verificationCodeWarning, "Email changed. Please generate a new code.");
                    stopTimer();
                    savedCode = null;
                    return;
                }
                if (!verificationCode.getText().toString().equals(savedCode.getCode())) {
                    // if the verification code does not match the input
                    if (tries < 3) {
                        WarningTextHelper.showWarning(getApplicationContext(), verificationCodeWarning, String.format("Incorrect code. Try again.\nTries: %s Left", 3 - tries));
                        pauseTimer();
                        tries += 1;
                    } else {
                        WarningTextHelper.showWarning(getApplicationContext(), verificationCodeWarning, "No more tries. Please generate another code.");
                        stopTimer();
                        tries = 0;
                        savedCode = null;
                    }
                    return;
                }
                page1.setVisibility(View.GONE);
                page2.setVisibility(View.VISIBLE);
                emailInput = savedCode.getEmail();
            }
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

        // Generate Code and save it as VerificationCode object.
        Random random = new Random();
        Integer codeInt = random.nextInt(10000);
        String code = String.format(Locale.getDefault(), "%04d", codeInt);
        savedCode = new VerificationCode(email.getText().toString(), code);

        // Send the code through API.
        DataFunctions.sendCodeThroughEmail(savedCode);
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
        countDownTimer.cancel();
        countDownTimer = null;
    }

    private Boolean timerStarted() {
        return countDownTimer != null;
    }
}