package com.capstone.merkado.Screens.MainMenu;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.capstone.merkado.DataManager.DataFunctionPackage.PlayerDataFunctions.PlayerListListener;
import com.capstone.merkado.Objects.ServerDataObjects.BasicServerData;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.Economy.AddEconomy;
import com.capstone.merkado.Screens.LoadingScreen.ServerLoadingScreen;

import java.util.List;

public class Lobby extends AppCompatActivity {

    Merkado merkado;
    RecyclerView serverList;
    CardView addEconomy;
    TextView noEconomy;
    ImageView backButton,helpGuideIcon;

    EconomiesAdapter economiesAdapter;
    List<BasicServerData> basicServerDataList;
    PlayerListListener serverBasicDataListener;
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
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());

        helpGuideIcon = findViewById(R.id.help_guide);


        helpGuideIcon.setOnClickListener(v -> showHelpGuideDialog());

        if (merkado.getAccount() == null) loggedOut();
        else loggedIn();
    }

    private void showHelpGuideDialog() {
        // Create a new dialog instance
        Dialog helpDialog = new Dialog(this);
        helpDialog.setContentView(R.layout.dialog_help_guide);

        // Find views within the dialog layout
        TextView helpText = helpDialog.findViewById(R.id.help_text);
        Button closeButton = helpDialog.findViewById(R.id.close_button);

        // Set any necessary text (optional)
        helpText.setText("This is some useful information to guide users.");

        // Set the close button listener
        closeButton.setOnClickListener(v -> helpDialog.dismiss());

        // Show the dialog
        helpDialog.show();
    }

    private void loggedOut() {
        noEconomy.setText(R.string.lobby_sign_in_requirement);
        addEconomy.setVisibility(View.GONE);
    }

    private void loggedIn() {
        addEconomy.setOnClickListener(v -> doAddEconomy.launch(new Intent(getApplicationContext(), AddEconomy.class)));
        basicServerDataList = merkado.getEconomyBasicList();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        economiesAdapter = new EconomiesAdapter(this, basicServerDataList);
        serverList.setLayoutManager(llm);
        serverList.setAdapter(economiesAdapter);
        economiesAdapter.setOnClickListener(basicServerData -> {
            Intent intent = new Intent(getApplicationContext(), ServerLoadingScreen.class);
            intent.putExtra("BASIC_SERVER_DATA", basicServerData);
            startActivity(intent);
        });
        setUpListener();
    }

    private void setUpListener() {
        if (serverBasicDataListener != null) serverBasicDataListener.stop();
        serverBasicDataListener = new PlayerListListener(merkado.getAccount().getEmail());
        serverBasicDataListener.start(this::updateLobby);
    }

    private void updateLobby(List<BasicServerData> basicServerDataList) {
        this.basicServerDataList = basicServerDataList;
        if (basicServerDataList == null || basicServerDataList.isEmpty()) {
            noEconomy.setVisibility(View.VISIBLE);
            serverList.setVisibility(View.GONE);
            noEconomy.setText(R.string.lobby_empty);
            addEconomy.setVisibility(View.VISIBLE);
        } else {
            serverList.setVisibility(View.VISIBLE);
            noEconomy.setVisibility(View.GONE);
            economiesAdapter.updateList(basicServerDataList);
        }
    }

    /**
     * Takes the basic server information based on the account.
     * If there is no account, then this will be skipped.
     */
    private void retrieveAllEconomyBasics() {
        if (merkado.getAccount() == null) return;
        setUpListener();
    }

    @Override
    protected void onDestroy() {
        if (serverBasicDataListener != null) serverBasicDataListener.stop();
        super.onDestroy();
    }
}