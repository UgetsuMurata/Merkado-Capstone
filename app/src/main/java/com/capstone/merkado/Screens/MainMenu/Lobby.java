package com.capstone.merkado.Screens.MainMenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.Adapters.EconomiesAdapter;
import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctionPackage.DataFunctions;
import com.capstone.merkado.Objects.ServerDataObjects.EconomyBasic;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.Economy.AddEconomy;
import com.capstone.merkado.Screens.LoadingScreen.ServerLoadingScreen;

import java.util.List;

public class Lobby extends AppCompatActivity {

    Merkado merkado;
    RecyclerView serverList;
    CardView addEconomy;
    TextView noEconomy;

    EconomiesAdapter economiesAdapter;
    List<EconomyBasic> economyBasicList;
    private final ActivityResultLauncher<Intent> doAddEconomy = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    retrieveAllEconomyBasics();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mai_lobby);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        serverList = findViewById(R.id.server_list);
        addEconomy = findViewById(R.id.add_economy);
        noEconomy = findViewById(R.id.empty_economy);
        ImageView backButton = findViewById(R.id.back_button);
        addEconomy.setOnClickListener(v -> doAddEconomy.launch(new Intent(getApplicationContext(), AddEconomy.class)));

        economyBasicList = merkado.getEconomyBasicList();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (economyBasicList == null || economyBasicList.size() == 0) {
            noEconomy.setVisibility(View.VISIBLE);
            serverList.setVisibility(View.GONE);

            if (merkado.getAccount() == null) {
                noEconomy.setText("Sign in to join economy");
                addEconomy.setVisibility(View.GONE);
            } else {
                noEconomy.setText("The economy's empty here");
                addEconomy.setVisibility(View.VISIBLE);
            }
        } else {
            serverList.setVisibility(View.VISIBLE);
            noEconomy.setVisibility(View.GONE);

            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            economiesAdapter = new EconomiesAdapter(this, economyBasicList);
            serverList.setLayoutManager(llm);
            serverList.setAdapter(economiesAdapter);

            economiesAdapter.setOnClickListener((title, id, playerId) -> {
                Intent intent = new Intent(getApplicationContext(), ServerLoadingScreen.class);
                intent.putExtra("TITLE", title);
                intent.putExtra("ID", id);
                intent.putExtra("PLAYER_ID", playerId);
                startActivity(intent);
            });
        }
    }

    private void isAccountAlreadyAPlayer() {
        for (EconomyBasic economyBasic : merkado.getEconomyBasicList()) {

        }
    }

    /**
     * Takes the basic server information based on the account.
     * If there is no account, then this will be skipped.
     */
    private void retrieveAllEconomyBasics() {
        if (merkado.getAccount() == null) return;
        DataFunctions.getEconomyBasic(merkado.getAccount(), economyBasicList -> {
            merkado.setEconomyBasicList(economyBasicList);

            //restart this screen.
            Intent i = new Intent(getApplicationContext(), Lobby.class);
            overridePendingTransition(0, 0);
            startActivity(i);
            overridePendingTransition(0, 0);
            finish();
        });
    }
}