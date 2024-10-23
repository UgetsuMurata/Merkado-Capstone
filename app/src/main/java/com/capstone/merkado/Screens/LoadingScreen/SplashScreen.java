package com.capstone.merkado.Screens.LoadingScreen;

import static com.capstone.merkado.CustomViews.WoodenButton.WoodenButtonMode.MEDIUM;
import static com.capstone.merkado.CustomViews.WoodenButton.WoodenButtonMode.SHORT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.CustomViews.WoodenButton;
import com.capstone.merkado.DataManager.DataFunctionPackage.AccountDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.UtilityDataFunctions;
import com.capstone.merkado.Helpers.Updater;
import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.MainMenu.MainMenu;
import com.google.android.material.card.MaterialCardView;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    private Merkado merkado;
    private ProgressBar progressBar;

    private MaterialCardView updateNotification;
    private TextView appUpdateMessage;
    private WoodenButton updateConfirmation;
    private Activity activity;

    private String updateLink = "";
    private Boolean updating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loa_splash_screen);

        // initialize this activity's screen.
        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        progressBar = findViewById(R.id.simpleProgressBar);
        progressBar.setMax(3);

        updateNotification = findViewById(R.id.app_update_notification);
        updateConfirmation = findViewById(R.id.app_update_confirmation);
        appUpdateMessage = findViewById(R.id.app_update_message);
        activity = this;

        updateNotification.setVisibility(View.GONE);

        checkForUpdatesAndStartLoading();
    }

    private void startLoading() {
        AtomicInteger processNumber = new AtomicInteger(0);

        long startingTime = System.currentTimeMillis();

        // Sequentially execute each process and update the progress bar after each completes
        getServerTimeOffset()
                .thenCompose(result -> {
                    updateProgressBar(processNumber.incrementAndGet());  // Increment progress after process 1 completes
                    return process1();
                })
                .thenCompose(result -> {
                    updateProgressBar(processNumber.incrementAndGet());  // Increment progress after process 2 completes
                    return process2();
                })
                .thenRun(() -> {
                    updateProgressBar(processNumber.incrementAndGet());  // Final increment after all processes are done
                    runOnUiThread(() -> {
                        // Delay the transition to MainMenu for 1 second (subtracting the time already taken by the processes)
                        new android.os.Handler().postDelayed(() -> {
                            startActivity(new Intent(SplashScreen.this, MainMenu.class));
                            finish();
                        }, Math.max(0, 1000 - (System.currentTimeMillis() - startingTime)));
                    });
                });
    }

    private void checkForUpdatesAndStartLoading() {
        UtilityDataFunctions.hasNewUpdate(this).thenAccept(hasNewUpdate -> {
            if (hasNewUpdate) {
                runOnUiThread(() -> {
                    updateNotification.setVisibility(View.VISIBLE);
                    appUpdateMessage.setText(ContextCompat.getString(getApplicationContext(),
                            R.string.app_update_contents));
                    updateConfirmation.setText("Update");
                    updateConfirmation.setMode(SHORT);
                });
                updateConfirmation.setOnClickListener(v ->
                        UtilityDataFunctions.getUpdateLink(getApplicationContext())
                                .thenAccept(updateLink -> {
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

    private CompletableFuture<Long> getServerTimeOffset() {
        return UtilityDataFunctions.getServerTimeOffset().thenCompose(serverTimeOffset -> {
            merkado.setServerTimeOffset(serverTimeOffset);
            return CompletableFuture.completedFuture(serverTimeOffset);
        });
    }

    /**
     * 1st process of loading screen. This retrieves the account logged in from the SharedPref.
     */
    private CompletableFuture<Void> process1() {
        // get the signed in from sharedpref.
        Account account = AccountDataFunctions.getSignedIn(getApplicationContext());
        if (account == null) return CompletableFuture.completedFuture(null); // stop the function here if sign in is required.

        // update the account in the application class.
        merkado.setAccount(account);

        // update the account logged in from the SharedPref
        AccountDataFunctions.signInAccount(getApplicationContext(), account);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 2nd process of the loading screen. This retrieves all internal data saved in json file.
     */
    private CompletableFuture<Void> process2() {
        // load internal data
        merkado.loadJSONResources(getApplicationContext());
        return CompletableFuture.completedFuture(null);
    }

    private void updateProgressBar(int progress) {
        runOnUiThread(() -> progressBar.setProgress(progress));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!updateLink.isEmpty() && !updating) {
            Updater.updateApp(getApplicationContext(), updateLink);
            appUpdateMessage.setText(ContextCompat.getString(getApplicationContext(),
                    R.string.app_update_contents_2));
            updateConfirmation.setText("Close App");
            updateConfirmation.setMode(MEDIUM);
            updating = true;
        }
    }
}
