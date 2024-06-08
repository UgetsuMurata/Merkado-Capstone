package com.capstone.merkado.Screens.Account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctions;
import com.capstone.merkado.R;

public class SignOut extends AppCompatActivity {

    private Merkado merkado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acc_sign_out);

        // initialize this activity's screen.
        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        // find views
        CardView signOut = findViewById(R.id.sign_out);
        CardView cancel = findViewById(R.id.cancel_sign_out);

        signOut.setOnClickListener(v -> {
            // Sign out the account.
            DataFunctions.signOut(getApplicationContext(), merkado.getAccount());

            // set account to null.
            merkado.setAccount(null);

            // show toast to confirm.
            Toast.makeText(getApplicationContext(), "Signed out successfully!", Toast.LENGTH_SHORT).show();

            // go back to main menu.
            setResult(Activity.RESULT_OK);
            finish();
        });

        cancel.setOnClickListener(v -> {
            // go back to main menu.
            setResult(Activity.RESULT_CANCELED);
            finish();
        });
    }
}