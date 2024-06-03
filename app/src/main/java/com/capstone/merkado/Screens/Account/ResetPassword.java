package com.capstone.merkado.Screens.Account;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctions;
import com.capstone.merkado.Helpers.NotificationHelper;
import com.capstone.merkado.Helpers.StringVerifier;
import com.capstone.merkado.Helpers.WarningTextHelper;
import com.capstone.merkado.Objects.VerificationCode;
import com.capstone.merkado.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;
import java.util.Random;

public class ResetPassword extends AppCompatActivity {

    private Merkado merkado;

    /**
     * VIEWS
     */
    private LinearLayout findEmail, enterCode, changePassword;
    private CardView cancel, goToPage1, goToPage2, goBackToPage2, goToPage3;
    private TextInputEditText email, code;
    private TextView emailWarning, codeInstructions, codeWarning, resendCode;

    /**
     * VARIABLES
     */
    private String emailString;
    private VerificationCode savedCode;
    private Integer tries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acc_reset_password);

        // initialize this activity's screen.
        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        // find views
        findEmail = findViewById(R.id.find_email);
        enterCode = findViewById(R.id.enter_code);
        changePassword = findViewById(R.id.change_password);
        cancel = findViewById(R.id.cancel);
        goToPage1 = findViewById(R.id.go_to_page1);
        goToPage2 = findViewById(R.id.go_to_page2);
        goBackToPage2 = findViewById(R.id.go_back_to_page2);
        goToPage3 = findViewById(R.id.go_to_page3);
        email = findViewById(R.id.email);
        emailWarning = findViewById(R.id.email_warning);
        codeInstructions = findViewById(R.id.code_instructions);
        code = findViewById(R.id.code);
        codeWarning = findViewById(R.id.code_warning);
        resendCode = findViewById(R.id.resend_code);

        // Page Navigation
        goToPage(1);
        cancel.setOnClickListener(v -> { // from page 1, cancel
            setResult(RESULT_CANCELED);
            finish();
        });
        goToPage1.setOnClickListener(v -> goToPage1Function()); // from page 2, back
        goToPage2.setOnClickListener(v -> goToPage2Function()); // from page 1, next
        goBackToPage2.setOnClickListener(v -> goBackToPage2Function()); // from page 3, back
        goToPage3.setOnClickListener(v -> goToPage3Function()); // from page 2, next

        /*
         * PAGE 1: FIND EMAIL
         */

        email.setOnFocusChangeListener((v, hasFocus) -> {
            // if changes focus from focused to not, check if email is valid
            if (!hasFocus) {
                if (email.getText() == null || email.getText().toString().isEmpty()) return;
                if (StringVerifier.isValidGmail(email.getText().toString())) {
                    WarningTextHelper.hide(emailWarning);
                } else {
                    WarningTextHelper.showWarning(getApplicationContext(), emailWarning, "Invalid email.");
                }
            }
        });

        /*
         * PAGE 2: GET CODE.
         */
        code.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                WarningTextHelper.hide(codeWarning);
            }
        });
        resendCode.setOnClickListener(v -> sendCode());
    }

    /**
     * Switches the visible layout depending on the page number.
     *
     * @param number page number.
     */
    private void goToPage(int number) {
        findEmail.setVisibility(number == 1 ? View.VISIBLE : View.GONE);
        enterCode.setVisibility(number == 2 ? View.VISIBLE : View.GONE);
        changePassword.setVisibility(number == 3 ? View.VISIBLE : View.GONE);
    }

    /**
     * Prepares Page 1 before going back from Page 2.
     */
    private void goToPage1Function() {
        email.setText(emailString);
        WarningTextHelper.hide(emailWarning);
        savedCode = null;
        goToPage(1);
    }

    /**
     * Checks if all conditions are followed before going to Page 2. After checking, and if all conditions are followed, then the email will be saved and a code will be generated.
     */
    private void goToPage2Function() {
        if (email.getText() == null || email.getText().toString().isEmpty()) {
            // if email input is null or empty
            WarningTextHelper.showWarning(getApplicationContext(), emailWarning, "Input your email.");
        } else if (!StringVerifier.isValidGmail(email.getText().toString())) {
            // if email is not a valid GMail
            WarningTextHelper.showWarning(getApplicationContext(), emailWarning, "Invalid email.");
        } else {
            // check if email exists in database
            DataFunctions.emailExists(email.getText().toString(), new DataFunctions.BooleanReturn() {
                @Override
                public void booleanReturn(Boolean bool) {
                    if (bool) {
                        // if the email exists, save the email and go to page 2, send code.
                        emailString = email.getText().toString();
                        goToPage(2);
                        sendCode();
                    } else {
                        // if the email does not exist, show warning.
                        WarningTextHelper.showWarning(getApplicationContext(), emailWarning, "Email is not registered. Please recheck your input.");
                    }
                }
            });
        }
    }

    /**
     * Prepares Page 2 before going back from Page 3.
     */
    private void goBackToPage2Function() {
        code.setText("");
        WarningTextHelper.showInfo(getApplicationContext(), codeWarning, "Ask to resend code for the email.");
        savedCode = null;
        goToPage(2);
    }

    /**
     * Checks if all conditions are followed before going to Page 3.
     */
    private void goToPage3Function() {
        if (code.getText() != null || code.getText().toString().isEmpty()) {
            // if code input is null or empty
            WarningTextHelper.showWarning(getApplicationContext(), codeWarning, "Please input the code.");
        } else if (savedCode == null) {
            // if there is no saved code
            WarningTextHelper.showInfo(getApplicationContext(), codeWarning, "Please ask to resend code.");
        } else if (tries >= 2) {
            // if tries is equal (or somehow greater than) 2, this is the 3rd try
            tries = 0;
            WarningTextHelper.showWarning(getApplicationContext(), codeWarning, "Incorrect code. Please ask to resend code.");
            savedCode = null;
        } else if (!code.getText().toString().equals(savedCode.getCode())) {
            // if tries is less than 2, not yet the 3rd try
            tries++;
            WarningTextHelper.showWarning(getApplicationContext(), codeWarning,
                    String.format(Locale.getDefault(), "Incorrect code. %d tries left.", 3 - tries));
        } else {
            goToPage(3);
        }
    }

    /**
     * Algorithm to generate, save, and send a 4-digit code.
     */
    private void sendCode() {
        // Generate Code and save it as VerificationCode object.
        Random random = new Random();
        Integer codeInt = random.nextInt(10000);
        String code = String.format(Locale.getDefault(), "%04d", codeInt);
        savedCode = new VerificationCode(emailString, code);

        // Send the code through API.
        DataFunctions.sendCodeThroughEmail(savedCode);
        NotificationHelper.sendNotification(getApplicationContext(),
                String.format("Verification code sent to %s", savedCode.getEmail()),
                String.format("Your Verification Code: %s.", savedCode.getCode()));
    }
}