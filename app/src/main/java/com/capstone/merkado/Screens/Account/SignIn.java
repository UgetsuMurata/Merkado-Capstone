package com.capstone.merkado.Screens.Account;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.R;

public class SignIn extends AppCompatActivity {

    private Merkado merkado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        // initialize this activity's screen.
        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);
    }
}