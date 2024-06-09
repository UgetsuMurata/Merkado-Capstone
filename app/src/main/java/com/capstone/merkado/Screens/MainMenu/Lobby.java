package com.capstone.merkado.Screens.MainMenu;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.capstone.merkado.Adapters.EconomiesAdapter;
import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.Objects.EconomyBasic;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.Economy.AddEconomy;

import java.util.List;
import java.util.Locale;

public class Lobby extends AppCompatActivity {

    Merkado merkado;
    RecyclerView serverList;
    CardView addEconomy;

    EconomiesAdapter economiesAdapter;
    List<EconomyBasic> economyBasicList;
    private final ActivityResultLauncher<Intent> doAddEconomy = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent i = new Intent(getApplicationContext(), MainMenu.class);
                    overridePendingTransition(0, 0);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                    finish();
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

        addEconomy.setOnClickListener(v -> doAddEconomy.launch(new Intent(getApplicationContext(), AddEconomy.class)));

        economyBasicList = merkado.getEconomyBasicList();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        economiesAdapter = new EconomiesAdapter(this, economyBasicList);
        serverList.setLayoutManager(llm);
        serverList.setAdapter(economiesAdapter);

        economiesAdapter.setOnClickListener(new EconomiesAdapter.OnClickListener() {
            @Override
            public void onClick(String title, Integer id) {
                Toast.makeText(getApplicationContext(), String.format(Locale.getDefault(), "Entering %s . id:%d", title, id), Toast.LENGTH_SHORT).show();
            }
        });
    }
}