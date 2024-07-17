package com.capstone.merkado.Screens.LoadingScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctionPackage.DataFunctions;
import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.MainMenu.MainMenu;

import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

public class SplashScreen extends AppCompatActivity {

    private Merkado merkado;
    private ProgressBar progressBar;
    private Timer timer;
    private final int maxProcesses = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loa_splash_screen);

        // initialize this activity's screen.
        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        progressBar = findViewById(R.id.simpleProgressBar);
        progressBar.setMax(maxProcesses);

        new Thread(() -> {
            long startingTime = System.currentTimeMillis();
            AtomicInteger process_number = new AtomicInteger(0);
            for (int i = 0; i < maxProcesses; i++) {
                process_number.getAndIncrement();
                switch (process_number.get()) {
                    case 1:
                        process1();
                        break;
                    case 2:
                        process2();
                        break;
                    case 3:
                        process3();
                        break;
                    default:
                        break;
                }
                progressBar.setProgress(process_number.get());
            }
            runOnUiThread(() -> new Handler().postDelayed(() -> {
                if (process_number.get() == maxProcesses) {
                    startActivity(new Intent(SplashScreen.this, MainMenu.class));
                    finish();
                }
            }, 1000 - System.currentTimeMillis()-startingTime));
        }).start();
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

    /**
     * 2nd process of the loading screen. This retrieves the static texts from Firebase.
     */
    private void process2() {
        // get the data from Firebase.
        DataFunctions.getAbout(string -> merkado.getStaticContents().setAbout(string));
        DataFunctions.getTermsAndConditions(string -> merkado.getStaticContents().setTermsAndConditions(string));
    }

    /**
     * 3rd process of the loading screen. Taking the basic server information based on the account.
     * If there is no account, then this will be skipped.
     */
    private void process3() {
        if (merkado.getAccount() == null) return;
        DataFunctions.getEconomyBasic(merkado.getAccount(), economyBasicList -> merkado.setEconomyBasicList(economyBasicList));
    }
}
