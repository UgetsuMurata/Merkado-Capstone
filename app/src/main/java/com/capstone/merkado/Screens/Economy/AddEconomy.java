package com.capstone.merkado.Screens.Economy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.Objects.Account;
import com.capstone.merkado.R;
import com.capstone.merkado.DataManager.DataFunctions;
import com.capstone.merkado.Screens.Account.SignUp;
import com.capstone.merkado.Screens.LoadingScreen.SplashScreen;
import com.capstone.merkado.Screens.MainMenu.MainMenu;

public class AddEconomy extends AppCompatActivity {

    Merkado merkado;
    private EditText serverCodeEditText;
    private CardView join_economy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eco_add_economy);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        ImageView closeButton = findViewById(R.id.close_button);
        TextView create_server = findViewById(R.id.cserver);
        join_economy = findViewById(R.id.join);
        serverCodeEditText = findViewById(R.id.server_code);

        closeButton.setOnClickListener(v -> onBackPressed());

        create_server.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), CreateEconomy.class));
            finish();
        });

        join_economy.setOnClickListener(v -> {
            String serverCode = serverCodeEditText.getText().toString().trim();
            if (serverCode.isEmpty()) {
                Toast.makeText(AddEconomy.this, "Please input the code", Toast.LENGTH_SHORT).show();
            } else {
                join_economy.setEnabled(false); // Disable the CardView to prevent multiple clicks

                DataFunctions.checkServerExistence(AddEconomy.this, serverCode, new DataFunctions.ServerExistenceCallback() {
                    @Override
                    public void onServerExists() {
                        DataFunctions.getCurrentAccount(AddEconomy.this, new DataFunctions.AccountReturn() {
                            @Override
                            public void accountReturn(Account account) {
                                if (account != null) {
                                    DataFunctions.addPlayerToServer(AddEconomy.this, serverCode, account);
                                    runOnUiThread(() -> {
                                        Toast.makeText(AddEconomy.this, "Server successfully added", Toast.LENGTH_SHORT).show();
                                        join_economy.setEnabled(true); // Re-enable the CardView
                                    });
                                } else {
                                    runOnUiThread(() -> {
                                        Toast.makeText(AddEconomy.this, "Error retrieving account information", Toast.LENGTH_SHORT).show();
                                        join_economy.setEnabled(true); // Re-enable the CardView
                                    });
                                }
                            }
                        });
                    }

                    @Override
                    public void onServerDoesNotExist() {
                        runOnUiThread(() -> {
                            Toast.makeText(AddEconomy.this, "Server does not exist", Toast.LENGTH_SHORT).show();
                            join_economy.setEnabled(true); // Re-enable the CardView
                        });
                    }
                });
            }
        });
    }
}
