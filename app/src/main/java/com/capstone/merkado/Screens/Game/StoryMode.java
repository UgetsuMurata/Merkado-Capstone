package com.capstone.merkado.Screens.Game;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.StaticData.StoryResourceCaller;
import com.capstone.merkado.Helpers.StringProcessor;
import com.capstone.merkado.Objects.StoryDataObjects.ImagePlacementData;
import com.capstone.merkado.Objects.StoryDataObjects.LineGroup;
import com.capstone.merkado.R;

import java.util.HashMap;

public class StoryMode extends AppCompatActivity {

    Merkado merkado;

    /**
     * Dialogue
     */
    TextView dialogueTextView;
    /**
     * Tools
     */
    CardView autoplay, skip, exit;
    ImageView dialogueBox;
    ConstraintLayout sceneBackground;

    Integer currentLineGroupIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gam_story_mode);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        sceneBackground = findViewById(R.id.scene_background);
        dialogueTextView = findViewById(R.id.dialogue);
        dialogueBox = findViewById(R.id.dialogue_box);
        autoplay = findViewById(R.id.autoplay);
        skip = findViewById(R.id.skip);
        exit = findViewById(R.id.exit);

        // get the lineGroup from the intent
        LineGroup lineGroup = getIntent().getParcelableExtra("CURRENT_LINE_GROUP");

        if (lineGroup == null) {
            finish();
            Toast.makeText(getApplicationContext(), "Cannot retrieve scripts. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        // display initial images
        for (ImagePlacementData imagePlacementData : lineGroup.getInitialImages()) {
            showCharacter(imagePlacementData);
        }

        // display initial background
        changeBackground(StoryResourceCaller.retrieveBackgroundResource(lineGroup.getBackground()));

        // after half-a-second, start the story.
        new Handler().postDelayed(() -> {
            // display first line
            LineGroup.DialogueLine dialogueLine = lineGroup.getDialogueLines().get(currentLineGroupIndex);
            showLine(StoryResourceCaller.retrieveDialogueBoxResource(dialogueLine.getCharacter()), dialogueLine.getDialogue());
            if (dialogueLine.getDialogueChoices() != null) {
                //TODO: Show the dialogue choice gui.
            }
            if (dialogueLine.getImageChanges() != null) {
                for (ImagePlacementData imagePlacementData : dialogueLine.getImageChanges()) {
                    showCharacter(imagePlacementData);
                }
            }

            dialogueBox.setOnClickListener(v -> {
                currentLineGroupIndex++;
                // display first line
                LineGroup.DialogueLine dialogueLine1 = lineGroup.getDialogueLines().get(currentLineGroupIndex);
                showLine(StoryResourceCaller.retrieveDialogueBoxResource(dialogueLine1.getCharacter()), dialogueLine1.getDialogue());
                if (dialogueLine1.getDialogueChoices() != null) {
                    //TODO: Show the dialogue choice gui.
                }
                if (dialogueLine1.getImageChanges() != null) {
                    for (ImagePlacementData imagePlacementData : dialogueLine1.getImageChanges()) {
                        showCharacter(imagePlacementData);
                    }
                }
            });
        }, 500);
    }

    /**
     * Shows (or hides) an image resource indicated in the ImagePlacementData.
     *
     * @param imagePlacementData ImagePlacementData instance from the Player instance.
     */
    private void showCharacter(ImagePlacementData imagePlacementData) {
        String placement = imagePlacementData.getPlacement();
        HashMap<StringProcessor.Placement.Label, StringProcessor.Placement.Value> labelValueHashMap = StringProcessor.extractPlacement(placement);

        FrameLayout frameLayout;

        StringProcessor.Placement.Value slotValue = labelValueHashMap.get(StringProcessor.Placement.Label.SLOT);
        StringProcessor.Placement.Value layerValue = labelValueHashMap.get(StringProcessor.Placement.Label.LAYER);
        if (slotValue == null || layerValue == null) return;

        switch (slotValue) {
            case SLOT1:
                frameLayout = findViewById(R.id.character_slot_1);
                break;
            case SLOT2:
                frameLayout = findViewById(R.id.character_slot_2);
                break;
            case SLOT3:
                frameLayout = findViewById(R.id.character_slot_3);
                break;
            case SLOT4:
                frameLayout = findViewById(R.id.character_slot_4);
                break;
            case SLOT5:
                frameLayout = findViewById(R.id.character_slot_5);
                break;
            case SLOT6:
                frameLayout = findViewById(R.id.character_slot_6);
                break;
            default:
                frameLayout = null;
                break;
        }

        if (frameLayout == null) return;

        ImageView layer;
        int resource = -1;

        switch (layerValue) {
            case BODY:
                layer = frameLayout.findViewById(R.id.body);
                resource = StoryResourceCaller.retrieveCharacterBodyResource(imagePlacementData.getImage());
                break;
            case FACE:
                layer = frameLayout.findViewById(R.id.face);
                resource = StoryResourceCaller.retrieveCharacterFaceResource(imagePlacementData.getImage());
                break;
            case PROP:
                layer = frameLayout.findViewById(R.id.props);
                break;
            default:
                layer = null;
                break;
        }

        if (!imagePlacementData.getToShow()) {
            if (layer == null) return;
            layer.setImageDrawable(null);
            return;
        }

        if (layer == null || resource == -1) return;

        layer.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), resource));
    }

    private void changeBackground(int scene) {
        sceneBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), scene));
    }

    private void showLine(int dialogueBoxResource, String dialogue) {
        dialogueBox.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), dialogueBoxResource));
        dialogueTextView.setText(dialogue);
    }
}