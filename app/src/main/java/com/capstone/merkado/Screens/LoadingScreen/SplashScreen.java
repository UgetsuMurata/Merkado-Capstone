package com.capstone.merkado.Screens.LoadingScreen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctions;
import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.MainMenu.MainMenu;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {

    private Merkado merkado;
    private ProgressBar progressBar;
    private Timer timer;
    private int i = 0;
    private boolean isProcess1Completed = false;
    private boolean isProcess2Completed = false;
    private boolean isProcess3Completed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spl_loading_screen);

        // initialize this activity's screen.
        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        progressBar = findViewById(R.id.simpleProgressBar);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (i < 100) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(i);
                        }
                    });
                    i++;

                    // Call process1() in a background thread
                    if (i == 25 && !isProcess1Completed) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                process1();
                                isProcess1Completed = true;
                            }
                        }).start();
                    }

                    // Call process2() in a background thread
                    if (i == 50 && !isProcess2Completed) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                process2();
                                isProcess2Completed = true;
                            }
                        }).start();
                    }

                    // Call process3() in a background thread
                    if (i == 75 && !isProcess3Completed) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                process3();
                                isProcess3Completed = true;
                            }
                        }).start();
                    }
                } else {
                    timer.cancel();

                    // Ensure process1() has completed before starting the next activity
                    if (isProcess1Completed && merkado.getStaticContents().getAbout() != null && isProcess3Completed) {
                        // go to next activity: Main Menu
                        startActivity(new Intent(SplashScreen.this, MainMenu.class));
                        finish();
                    }
                }
            }
        }, 0, 50);
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
        DataFunctions.getAbout(getApplicationContext(), string -> merkado.getStaticContents().setAbout(string));
        DataFunctions.getTermsAndConditions(getApplicationContext(), string -> merkado.getStaticContents().setTermsAndConditions(string));
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
