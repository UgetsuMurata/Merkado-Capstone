package com.capstone.merkado.Screens.Game;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.Objects.StoryDataObjects.LineGroup;
import com.capstone.merkado.Objects.StoryDataObjects.PlayerStory;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.MainMenu.MainMenu;

public class MainMap extends AppCompatActivity {

    Merkado merkado;

    private final ActivityResultLauncher<Intent> goToStoryMode = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent i = new Intent(getApplicationContext(), MainMap.class);
                overridePendingTransition(0, 0);
                startActivity(i);
                overridePendingTransition(0, 0);
                finish();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gam_main_map);

        // check for an intent that says there's prologue
        if (getIntent().hasExtra("PROLOGUE")) {
            Intent intent = new Intent(getApplicationContext(), StoryMode.class);
            intent.putExtras(getIntent());
            goToStoryMode.launch(intent);
        }

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);
    }

}