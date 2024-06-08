package com.capstone.merkado.Screens.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.R;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_about);

        // get the instance of Merkado Application Java Class and initialize the screen.
        Merkado merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        // Change the about contents.
        TextView aboutContent = findViewById(R.id.about_content);
        aboutContent.setText(merkado.getStaticContents().getAbout());
    }
}