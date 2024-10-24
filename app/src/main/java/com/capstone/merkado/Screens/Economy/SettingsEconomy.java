package com.capstone.merkado.Screens.Economy;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.CustomViews.WoodenButton;
import com.capstone.merkado.DataManager.DataFunctionPackage.ServerDataFunctions;
import com.capstone.merkado.R;

import java.util.Locale;

public class SettingsEconomy extends AppCompatActivity {

    Merkado merkado;
    TextView sensitivityFactorValue, botRemovalValue, readTAC, serverId, serverKey;
    SeekBar sensitivityFactorSeekbar, botRemovalSeekbar;
    CheckBox testCheck;
    LinearLayout serverIdButton, serverKeyButton;
    WoodenButton cancelButton, saveChangesButton;
    Integer sensitivityFactor, botRemoval;
    String serverIdStr, serverKeyStr;
    ClipboardManager clipboard;
    ImageView closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eco_settings_economy);

        serverIdStr = getIntent().getStringExtra("SERVER_ID");
        serverKeyStr = getIntent().getStringExtra("SERVER_KEY");

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        if (serverIdStr == null || serverKeyStr == null) {
            Pair<String, String> idKeyPair = merkado.getServerIdKeyPair();
            serverIdStr = idKeyPair.first;
            serverKeyStr = idKeyPair.second;
        }

        sensitivityFactorValue = findViewById(R.id.sensitivity_factor_value);
        sensitivityFactorSeekbar = findViewById(R.id.sensitivity_factor_seekbar);
        testCheck = findViewById(R.id.test_check);
        botRemovalValue = findViewById(R.id.bots_removal_value);
        botRemovalSeekbar = findViewById(R.id.bot_removal_seekbar);
        readTAC = findViewById(R.id.read_terms_and_conditions);
        serverId = findViewById(R.id.server_id);
        serverIdButton = findViewById(R.id.server_id_button);
        serverKey = findViewById(R.id.server_key);
        serverKeyButton = findViewById(R.id.server_key_button);
        cancelButton = findViewById(R.id.cancel_button);
        saveChangesButton = findViewById(R.id.save_changes_button);
        closeButton = findViewById(R.id.close_button);

        sensitivityFactor = 10;
        sensitivityFactorSeekbar.setProgress(sensitivityFactor);
        sensitivityFactorValue.setText(String.format(Locale.getDefault(), "%.2f", (float) sensitivityFactor / 100));

        botRemoval = 10;
        botRemovalSeekbar.setProgress(botRemoval);
        botRemovalValue.setText(String.format(Locale.getDefault(), "%d", botRemoval));

        serverId.setText(serverIdStr);
        serverKey.setText(serverKeyStr);

        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        setUpViewListeners();

    }

    private void setUpViewListeners() {
        sensitivityFactorSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String displayedValue = String.format(Locale.getDefault(), "%.2f", (float) progress / 100);
                sensitivityFactorValue.setText(displayedValue);
                sensitivityFactor = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        testCheck.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Cannot be unchecked during user testing.", Toast.LENGTH_SHORT).show();
            testCheck.setChecked(true);
        });

        botRemovalSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                botRemovalValue.setText(String.valueOf(progress*10));
                botRemoval = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        readTAC.setOnClickListener(v -> {
            // TODO: GO TO TERMS AND CONDITIONS.
        });

        serverIdButton.setOnClickListener(v -> copyText("Server Id", serverIdStr));

        serverKeyButton.setOnClickListener(v -> copyText("Server Key", serverKeyStr));

        cancelButton.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Changes not saved.", Toast.LENGTH_SHORT).show();
            finish();
        });

        saveChangesButton.setOnClickListener(v -> {
            ServerDataFunctions.getSettings(this.serverIdStr).thenAccept(settings -> {
                settings.setSensitivityFactor((float) sensitivityFactor / 100);
                settings.setRequiredPercentage((float) botRemoval / 10);
                settings.setTestCheck(testCheck.isChecked());
                ServerDataFunctions.setSettings(this.serverIdStr, merkado.getAccount().getEmail(), settings);
                Toast.makeText(getApplicationContext(), "Changes saved!", Toast.LENGTH_SHORT).show();
            });
            finish();
        });
    }

    private void copyText(String label, String text) {
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }
}