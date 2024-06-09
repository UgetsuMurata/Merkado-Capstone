package com.capstone.merkado.Screens.LoadingScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.R;
import com.capstone.merkado.Screens.MainMenu.MainMenu;

public class ServerLoadingScreen extends AppCompatActivity {

    private Merkado merkado;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_loading_screen);

        // initialize this activity's screen.
        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);
        handler = new Handler();

        // Set the activity to full-screen mode
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                startActivity(intent);
                finish();
            }
        }, 10000);

        // Load GIF into the View
        ImageView logoView = findViewById(R.id.gif_screen); // Change View to ImageView
        Glide.with(this).load(R.drawable.icon_loading_server_screen).into(logoView);

    }
}