package com.capstone.merkado.Screens.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.capstone.merkado.R;
import com.google.android.material.textfield.TextInputEditText;

public class ChangePassword extends AppCompatActivity {

    TextInputEditText oldPassword, newPassword, confirmPassword;
    TextView oldPasswordWarning, newPasswordWarning, confirmPasswordWarning;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_change_password);
    }
}