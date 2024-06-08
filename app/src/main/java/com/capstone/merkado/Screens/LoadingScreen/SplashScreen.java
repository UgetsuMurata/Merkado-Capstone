package com.capstone.merkado.Screens.LoadingScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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
    private int i=0;
    private boolean isProcess1Completed = false;

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
                if (i<100){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(i);
                        }
                    });
                    i++;

                    // Call process1() in a background thread
                    if (i == 50 && !isProcess1Completed) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                process1();
                                isProcess1Completed = true;
                            }
                        }).start();
                    }

                } else {
                    timer.cancel();

                    // Ensure process1() has completed before starting the next activity
                    if (isProcess1Completed) {
                        // go to next activity: Main Menu
                        startActivity(new Intent(SplashScreen.this, MainMenu.class));
                        finish();
                    }
                }
            }
        },0, 50);
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
