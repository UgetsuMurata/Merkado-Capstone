package com.capstone.merkado.Screens.Economy;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.Adapters.ImageSelectionAdapter;
import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.CustomViews.WoodenButton;
import com.capstone.merkado.DataManager.DataFunctionPackage.ServerDataFunctions;
import com.capstone.merkado.DataManager.StaticData.GameResourceCaller;
import com.capstone.merkado.Helpers.Generator;
import com.capstone.merkado.Helpers.StringHash;
import com.capstone.merkado.Objects.ServerDataObjects.NewServer;
import com.capstone.merkado.R;
import com.google.android.material.card.MaterialCardView;

import java.util.Arrays;

public class CreateEconomy extends AppCompatActivity {

    Merkado merkado;

    LinearLayout createServerPage1, createServerPage2, changeImage;
    MaterialCardView changeImageWindow;
    EditText serverName;
    TextView playerLimitValue, cancelServer, cancelImageChange, exitSuccess, readTAC, serverId, serverKey;
    SeekBar playerLimitSeekbar;
    WoodenButton createServer, nextSettings, confirmImageChange;
    ImageView serverImage, serverIdCopy, serverKeyCopy;
    RecyclerView imageSelection;

    Integer playerLimitFinal = 20;
    ImageSelectionAdapter imageSelectionAdapter;
    Integer imageChosen = 1;
    Integer imageTempChosen = 1;
    String generatedId, generatedKey;
    ClipboardManager clipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eco_create_economy);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        createServerPage1 = findViewById(R.id.create_server_page1);
        createServerPage2 = findViewById(R.id.create_server_page2);
        changeImageWindow = findViewById(R.id.change_image_window);
        changeImage = findViewById(R.id.change_image);
        serverImage = findViewById(R.id.server_image);
        serverName = findViewById(R.id.server_name_edittext);
        playerLimitValue = findViewById(R.id.player_limit_value);
        playerLimitSeekbar = findViewById(R.id.player_limit_seekbar);
        cancelImageChange = findViewById(R.id.cancel_image_change);
        cancelServer = findViewById(R.id.cancel_button);
        exitSuccess = findViewById(R.id.exit);
        readTAC = findViewById(R.id.read_terms_and_conditions);
        serverId = findViewById(R.id.server_id);
        serverKey = findViewById(R.id.server_key);
        createServer = findViewById(R.id.create_server_button);
        nextSettings = findViewById(R.id.server_next);
        confirmImageChange = findViewById(R.id.confirm_image_change);
        serverIdCopy = findViewById(R.id.server_id_copy);
        serverKeyCopy = findViewById(R.id.server_key_copy);
        imageSelection = findViewById(R.id.image_selection);

        createServerPage1.setVisibility(View.VISIBLE);
        createServerPage2.setVisibility(View.GONE);
        changeImageWindow.setVisibility(View.GONE);

        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        setUpPage1();
    }

    private void setUpPage1() {
        serverImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), GameResourceCaller.getServerImage(1)));
        changeImage.setOnClickListener(v -> {
            changeImageWindow.setVisibility(View.VISIBLE);
            setUpChangeImageWindow();
        });

        playerLimitSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                playerLimitFinal = Arrays.asList(20, 30, 40, 50).get(progress - 1);
                playerLimitValue.setText(String.valueOf(playerLimitFinal));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        cancelServer.setOnClickListener(v -> finish());
        createServer.setOnClickListener(v -> {
            generatedId = Generator.generateID();
            generatedKey = Generator.generateKey();
            createServerPage1.setVisibility(View.GONE);
            createServerPage2.setVisibility(View.VISIBLE);
            saveToDatabase();
        });
    }

    private void setUpChangeImageWindow() {
        imageTempChosen = imageChosen;
        imageSelection.setLayoutManager(new GridLayoutManager(this, 4));
        imageSelectionAdapter = new ImageSelectionAdapter(getApplicationContext(),
                Arrays.asList(
                        new ImageIdPair(GameResourceCaller.getServerImage(1), 1),
                        new ImageIdPair(GameResourceCaller.getServerImage(2), 2),
                        new ImageIdPair(GameResourceCaller.getServerImage(3), 3),
                        new ImageIdPair(GameResourceCaller.getServerImage(4), 4)),
                imageChosen);
        imageSelection.setAdapter(imageSelectionAdapter);
        imageSelectionAdapter.setOnClickListener(src -> imageTempChosen = src.getId());
        confirmImageChange.setOnClickListener(v -> {
            imageChosen = imageTempChosen;
            serverImage.setImageDrawable(
                    ContextCompat.getDrawable(getApplicationContext(),
                            GameResourceCaller.getServerImage(imageChosen)));
            changeImageWindow.setVisibility(View.GONE);
        });
        cancelImageChange.setOnClickListener(v -> changeImageWindow.setVisibility(View.GONE));
    }

    private void setUpPage2() {
        serverId.setText(generatedId);
        serverKey.setText(generatedKey);
        serverIdCopy.setOnClickListener(v -> copyText("Merkado Id", generatedId));
        serverKeyCopy.setOnClickListener(v -> copyText("*".repeat(generatedKey.length()), generatedKey));
        exitSuccess.setOnClickListener(v -> finish());
        nextSettings.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SettingsEconomy.class));
            finish();
        });
    }

    private void copyText(String label, String text) {
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }

    private void saveToDatabase() {
        ServerDataFunctions.createNewServer(generatedId, generateServerObject()).thenAccept(returnCode -> {
            switch (returnCode) {
                case -1:
                    generatedId = Generator.generateID();
                    generatedKey = Generator.generateKey();
                    saveToDatabase();
                case 0:
                    setUpPage2();
            }
        });
    }

    private NewServer generateServerObject() {
        NewServer newServer = new NewServer();
        NewServer.Settings settings = new NewServer.Settings();

        newServer.setServerOwner(Merkado.getInstance().getAccount().getEmail());
        newServer.setName(serverName.getText().toString());
        newServer.setKey(StringHash.hashPassword(generatedKey));
        newServer.setServerImage(imageChosen);

        settings.setPlayerLimit(playerLimitFinal);
        settings.setSensitivityFactor(0.1f);

        newServer.setSettings(settings);

        return newServer;
    }

    public static class ImageIdPair {
        Integer image;
        Integer id;

        public ImageIdPair(Integer image, Integer id) {
            this.image = image;
            this.id = id;
        }

        public Integer getImage() {
            return image;
        }

        public Integer getId() {
            return id;
        }
    }
}