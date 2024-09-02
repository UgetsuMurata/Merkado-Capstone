package com.capstone.merkado.Screens.LoadingScreen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.CustomViews.WoodenButton;
import com.capstone.merkado.DataManager.DataFunctionPackage.AccountDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.AppUpdateDataFunctions;
import com.capstone.merkado.Helpers.Updater;
import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.MainMenu.MainMenu;
import com.google.android.material.card.MaterialCardView;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    private Merkado merkado;
    private ProgressBar progressBar;
    private final int maxProcesses = 2;

    private MaterialCardView updateNotification;
    private WoodenButton updateConfirmation;
    private Activity activity;

    private String updateLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loa_splash_screen);

        // initialize this activity's screen.
        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        progressBar = findViewById(R.id.simpleProgressBar);
        progressBar.setMax(maxProcesses);

        updateNotification = findViewById(R.id.app_update_notification);
        updateConfirmation = findViewById(R.id.app_update_confirmation);
        activity = this;

        checkForUpdatesAndStartLoading();
    }

    private void startLoading() {
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
            }, 1000 - System.currentTimeMillis() - startingTime));
        }).start();
    }

    private void checkForUpdatesAndStartLoading() {
        AppUpdateDataFunctions.hasNewUpdate(this).thenAccept(hasNewUpdate -> {
            if (hasNewUpdate) {
                runOnUiThread(() -> updateNotification.setVisibility(View.VISIBLE));
                updateConfirmation.setOnClickListener(v ->
                    AppUpdateDataFunctions.getUpdateLink().thenAccept(updateLink -> {
                        Updater.allowDownload(activity);
                        this.updateLink = updateLink;
                    })
                );
            } else startLoading();
        }).exceptionally(throwable -> {
            startLoading();
            return null;
        });
    }

    /**
     * 1st process of loading screen. This retrieves the account logged in from the SharedPref.
     */
    private void process1() {
        // get the signed in from sharedpref.
        Account account = AccountDataFunctions.getSignedIn(getApplicationContext());
        if (account == null) return; // stop the function here if sign in is required.

        // update the account in the application class.
        merkado.setAccount(account);

        // update the account logged in from the SharedPref
        AccountDataFunctions.signInAccount(getApplicationContext(), account);
    }

    /**
     * 2nd process of the loading screen. This retrieves all internal data saved in json file.
     */
    private void process2() {
        // load internal data
        merkado.loadJSONResources(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!"".equals(updateLink))
            Updater.updateApp(getApplicationContext(), updateLink);
    }
}
