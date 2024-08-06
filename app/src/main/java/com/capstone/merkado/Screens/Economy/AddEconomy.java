package com.capstone.merkado.Screens.Economy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctionPackage.PlayerDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.ServerDataFunctions;
import com.capstone.merkado.Helpers.StringVerifier;
import com.capstone.merkado.Helpers.WarningTextHelper;
import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.Objects.ServerDataObjects.BasicServerData;
import com.capstone.merkado.R;

public class AddEconomy extends AppCompatActivity {

    Merkado merkado;
    private EditText serverCodeEditText;
    private TextView serverCodeError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eco_add_economy);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        ImageView closeButton = findViewById(R.id.close_button);
        TextView createServer = findViewById(R.id.cserver);
        CardView joinEconomy = findViewById(R.id.join);
        CardView cancelEconomy = findViewById(R.id.cancel);
        serverCodeEditText = findViewById(R.id.server_code);
        serverCodeError = findViewById(R.id.server_code_error);

        closeButton.setOnClickListener(v -> new OnBackPressedDispatcher().onBackPressed());
        cancelEconomy.setOnClickListener(v -> new OnBackPressedDispatcher().onBackPressed());

        createServer.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), CreateEconomy.class));
            finish();
        });

        serverCodeEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                WarningTextHelper.hide(serverCodeError);
            } else {
                if (serverCodeEditText.getText() == null || serverCodeEditText.getText().toString().isEmpty())
                    return;
                if (StringVerifier.isValidServerCode(serverCodeEditText.getText().toString()))
                    WarningTextHelper.hide(serverCodeError);
                else
                    WarningTextHelper.showWarning(getApplicationContext(), serverCodeError, "Code must only contain a valid 6-digit number.");
            }
        });

        joinEconomy.setOnClickListener(v -> new Thread(this::joinEconomyFunction).start());
    }

    private Boolean economySavedAlready(String id) {
        for (BasicServerData basicServerData : merkado.getEconomyBasicList()) {
            if (id.equals(basicServerData.getId())) return true;
        }
        return false;
    }

    private void joinEconomyFunction() {
        if (serverCodeEditText.getText() == null || serverCodeEditText.getText().toString().trim().isEmpty()) {
            runOnUiThread(() -> WarningTextHelper.showWarning(getApplicationContext(), serverCodeError, "Please input the code."));
        } else if (!StringVerifier.isValidServerCode(serverCodeEditText.getText().toString().trim())) {
            runOnUiThread(() -> WarningTextHelper.showWarning(getApplicationContext(), serverCodeError, "Code must only contain a valid 6-digit number."));
        } else if (economySavedAlready(serverCodeEditText.getText().toString().trim())) {
            runOnUiThread(() -> WarningTextHelper.showWarning(getApplicationContext(), serverCodeError, "You already joined this economy!"));
        } else {
            String serverCodeStr = serverCodeEditText.getText().toString().trim();
            Boolean serverExists = ServerDataFunctions.checkServerExistence(serverCodeStr);
            if (Boolean.TRUE.equals(serverExists)) {
                Account account = merkado.getAccount();
                if (account == null) {
                    runOnUiThread(()->Toast.makeText(getApplicationContext(), "Error retrieving account information. Try again later.", Toast.LENGTH_SHORT).show());
                    finish();
                    return;
                }
                Boolean results = PlayerDataFunctions.addPlayerToServer(serverCodeStr, account);
                runOnUiThread(()-> {
                    Toast.makeText(getApplicationContext(), results ? "Server added successfully!" : "Failed to join server. Try again later.", Toast.LENGTH_SHORT).show();
                    setResult(results ? RESULT_OK : RESULT_CANCELED);
                    finish();
                });
            } else {
                runOnUiThread(()->Toast.makeText(getApplicationContext(), "Server does not exist.", Toast.LENGTH_SHORT).show());
                finish();
            }
        }
    }
}
