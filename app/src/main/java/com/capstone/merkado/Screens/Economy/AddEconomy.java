package com.capstone.merkado.Screens.Economy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.CustomViews.WoodenButton;
import com.capstone.merkado.DataManager.DataFunctionPackage.PlayerDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.ServerDataFunctions;
import com.capstone.merkado.Helpers.StringVerifier;
import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.Objects.ServerDataObjects.BasicServerData;
import com.capstone.merkado.R;
import com.google.android.material.textfield.TextInputLayout;

public class AddEconomy extends AppCompatActivity {

    Merkado merkado;
    EditText serverIdEditText, serverKeyEditText;
    TextInputLayout serverId, serverKey;
    WoodenButton joinEconomy, cancelEconomy;
    ImageView closeButton;

    Boolean validId = false;
    Boolean validKey = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eco_add_economy);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        TextView createServer = findViewById(R.id.create_server);
        closeButton = findViewById(R.id.close_button);
        joinEconomy = findViewById(R.id.join);
        cancelEconomy = findViewById(R.id.cancel);
        serverId = findViewById(R.id.server_code);
        serverIdEditText = findViewById(R.id.server_code_edittext);
        serverKey = findViewById(R.id.server_key);
        serverKeyEditText = findViewById(R.id.server_key_edittext);


        closeButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        cancelEconomy.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        createServer.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), CreateEconomy.class));
            finish();
        });

        serverId.setErrorEnabled(true);
        serverId.setError(null);
        serverKey.setErrorEnabled(true);
        serverKey.setError(null);

        serverIdEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                serverId.setError(null);
                validId = false;
            } else {
                if (serverIdEditText.getText() == null || serverIdEditText.getText().toString().isEmpty()) {
                    serverId.setError(null);
                    validId = false;
                    return;
                }
                if (StringVerifier.isValidServerId(serverIdEditText.getText().toString())) {
                    serverId.setError(null);
                    validId = true;
                } else {
                    serverId.setError("Invalid server id.");
                    validId = false;
                }
            }
            checkJoinButton();
        });

        serverKeyEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                serverKey.setError(null);
                validKey = false;
            } else {
                if (serverKeyEditText.getText() == null || serverKeyEditText.getText().toString().isEmpty()) {
                    validKey = false;
                    return;
                }
                if (StringVerifier.isValidServerKey(serverKeyEditText.getText().toString())) {
                    serverKey.setError(null);
                    validKey = true;
                } else {
                    serverKey.setError("Invalid server key.");
                    validKey = false;
                }
            }
            checkJoinButton();
        });

        joinEconomy.setOnClickListener(v -> new Handler().post(this::joinEconomyFunction));
    }

    private Boolean economySavedAlready(String id) {
        for (BasicServerData basicServerData : merkado.getEconomyBasicList()) {
            if (id.equals(basicServerData.getId())) return true;
        }
        return false;
    }

    private void checkJoinButton() {
        if (validId && validKey) joinEconomy.enable();
        else joinEconomy.disable();
    }

    private void joinEconomyFunction() {
        if (economySavedAlready(serverIdEditText.getText().toString().trim())) {
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "You already joined this server.", Toast.LENGTH_SHORT).show());
            finish();
        } else {
            String serverIdStr = serverIdEditText.getText().toString().trim();
            String serverKeyStr = serverKeyEditText.getText().toString().trim();
            ServerDataFunctions.checkServerExistence(serverIdStr)
                    .thenAccept(serverExists -> {
                        if (Boolean.TRUE.equals(serverExists)) {
                            Account account = merkado.getAccount();
                            if (account == null) {
                                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Error retrieving account information. Try again later.", Toast.LENGTH_SHORT).show());
                                finish();
                                return;
                            }
                            PlayerDataFunctions.addPlayerToServer(serverIdStr, serverKeyStr, account).thenAccept(result -> {
                                switch (result) {
                                    case 0:
                                        runOnUiThread(() -> {
                                            Toast.makeText(getApplicationContext(), "Server added successfully!", Toast.LENGTH_SHORT).show();
                                            setResult(RESULT_OK);
                                            finish();
                                        });
                                    case -1:
                                        runOnUiThread(() -> {
                                            Toast.makeText(getApplicationContext(), "Error has occurred. Please try again later!", Toast.LENGTH_SHORT).show();
                                            setResult(RESULT_CANCELED);
                                            finish();
                                        });
                                    case -2:
                                        serverKeyEditText.setText("");
                                        serverKey.setError("Incorrect key.");
                                        validKey = false;
                                }
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Server does not exist.", Toast.LENGTH_SHORT).show());
                            finish();
                        }
                    });
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
