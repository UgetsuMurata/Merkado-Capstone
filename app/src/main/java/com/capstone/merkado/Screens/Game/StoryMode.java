package com.capstone.merkado.Screens.Game;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.Objects.StoryDataObjects.CharacterImg;
import com.capstone.merkado.Objects.StoryDataObjects.CharacterImg.Character;
import com.capstone.merkado.R;

public class StoryMode extends AppCompatActivity {

    Merkado merkado;

    /**
     * Dialogue
     */
    TextView characterNameTextView, dialogueTextView;
    /**
     * Tools
     */
    CardView dialogueBox, autoplay, skip, exit;
    /**
     * CHARACTERS
     */
    ImageView character1, character2, character3, character4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gam_story_mode);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        characterNameTextView = findViewById(R.id.character_name);
        dialogueTextView = findViewById(R.id.dialogue);
        dialogueBox = findViewById(R.id.dialogue_box);
        autoplay = findViewById(R.id.autoplay);
        skip = findViewById(R.id.skip);
        exit = findViewById(R.id.exit);

    }

    /**
     * Proceeds to next dialogue.
     * @param characterName name of the character.
     * @param dialogue dialogue of that character.
     */
    private void nextDialogue(String characterName, String dialogue, CharacterImg characterImg, Character character) {
        characterNameTextView.setText(characterName);
        dialogueTextView.setText(dialogue);
    }
}