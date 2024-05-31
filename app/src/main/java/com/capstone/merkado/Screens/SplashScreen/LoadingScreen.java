package com.capstone.merkado.Screens.SplashScreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctions;
import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.Account.SignIn;

public class LoadingScreen extends AppCompatActivity {

    private Merkado merkado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        // initialize this activity's screen.
        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        // execute the processes
        process1();

        // go to next activity: SignIn
        if (merkado.getAccount() == null) startActivity(new Intent(this, SignIn.class));
        // proceed to the game's main menu.
        finish();
    }

    /**
     * 1st process of loading screen. This retrieves the account logged in from the SharedPref.
     */
    private void process1() {
        // get the signed in from sharedpref.
        Account account = DataFunctions.getSignedIn(getApplicationContext());
        if (account == null) return; // stop the function here if sign in is required.

        // update the account in the application class.
        merkado.setAccount(account);

        // update the account logged in from the SharedPref
        DataFunctions.signInAccount(getApplicationContext(), account);
    }
}