package com.capstone.merkado.Screens.Game;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctions;
import com.capstone.merkado.DataManager.StaticData.StoryResourceCaller;
import com.capstone.merkado.Helpers.StringProcessor;
import com.capstone.merkado.Objects.StoryDataObjects.ImagePlacementData;
import com.capstone.merkado.Objects.StoryDataObjects.LineGroup;
import com.capstone.merkado.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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

    /**
     * DISPLAYS
     */
    FrameLayout characterSlot1, characterSlot2, characterSlot3, characterSlot4, characterSlot5;

    /**
     * CHOICE
     */
    ConstraintLayout choiceGui;
    ImageView choice1, choice2, choice3, choice4;
    TextView choice1Text, choice2Text, choice3Text, choice4Text;

    /**
     * TRANSITION
     */
    ImageView blackScreen;

    Integer currentLineGroupIndex = 0;
    Integer nextLineGroupId;
    Integer currentQueueIndex;

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
        choiceGui = findViewById(R.id.choice_gui);
        choice1 = findViewById(R.id.choice_1);
        choice2 = findViewById(R.id.choice_2);
        choice3 = findViewById(R.id.choice_3);
        choice4 = findViewById(R.id.choice_4);
        choice1Text = findViewById(R.id.choice_1_text);
        choice2Text = findViewById(R.id.choice_2_text);
        choice3Text = findViewById(R.id.choice_3_text);
        choice4Text = findViewById(R.id.choice_4_text);
        characterSlot1 = findViewById(R.id.character_slot_1);
        characterSlot2 = findViewById(R.id.character_slot_2);
        characterSlot3 = findViewById(R.id.character_slot_3);
        characterSlot4 = findViewById(R.id.character_slot_4);
        characterSlot5 = findViewById(R.id.character_slot_5);
        blackScreen = findViewById(R.id.black_screen);

        // get the lineGroup from the intent
        LineGroup lineGroup = getIntent().getParcelableExtra("CURRENT_LINE_GROUP");
        nextLineGroupId = getIntent().getIntExtra("NEXT_LINE_GROUP", -1);
        currentQueueIndex = getIntent().getIntExtra("CURRENT_QUEUE_INDEX", -1);

        if (lineGroup == null) {
            finish();
            Toast.makeText(getApplicationContext(), "Cannot retrieve scripts. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        initializeScreen(lineGroup);
    }

    private void initializeScreen(LineGroup lineGroup) {
        currentLineGroupIndex = 0;
        clearCharacters();

        if ("FTB".equals(lineGroup.getTransition())) fadeToBlack();

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
                choiceGui.setVisibility(View.VISIBLE);
                setChoices(dialogueLine.getDialogueChoices());
            }
            if (dialogueLine.getImageChanges() != null) {
                for (ImagePlacementData imagePlacementData : dialogueLine.getImageChanges()) {
                    showCharacter(imagePlacementData);
                }
            }

            dialogueBox.setOnClickListener(v -> {
                currentLineGroupIndex++;
                if (currentLineGroupIndex.equals(lineGroup.getDialogueLines().size())) {
                    // reached the end.
                    currentLineEnded();
                    return;
                }
                // display first line
                LineGroup.DialogueLine dialogueLine1 = lineGroup.getDialogueLines().get(currentLineGroupIndex);
                showLine(StoryResourceCaller.retrieveDialogueBoxResource(dialogueLine1.getCharacter()), dialogueLine1.getDialogue());
                if (dialogueLine1.getDialogueChoices() != null) {
                    choiceGui.setVisibility(View.VISIBLE);
                    setChoices(dialogueLine1.getDialogueChoices());

                }
                if (dialogueLine1.getImageChanges() != null) {
                    for (ImagePlacementData imagePlacementData : dialogueLine1.getImageChanges()) {
                        showCharacter(imagePlacementData);
                    }
                }
            });
        }, 500);
        choiceGui.setVisibility(View.GONE);
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
                frameLayout = characterSlot1;
                break;
            case SLOT2:
                frameLayout = characterSlot2;
                break;
            case SLOT3:
                frameLayout = characterSlot3;
                break;
            case SLOT4:
                frameLayout = characterSlot4;
                break;
            case SLOT5:
                frameLayout = characterSlot5;
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
                resource = StoryResourceCaller.retrievePropResource(imagePlacementData.getImage());
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

    private void clearCharacters() {
        for (FrameLayout layout : Arrays.asList(characterSlot1, characterSlot2, characterSlot3, characterSlot4, characterSlot5)) {
            ImageView body = layout.findViewById(R.id.body);
            body.setImageDrawable(null);
            ImageView face = layout.findViewById(R.id.face);
            face.setImageDrawable(null);
            ImageView prop = layout.findViewById(R.id.props);
            prop.setImageDrawable(null);
        }
    }

    private void changeBackground(int scene) {
        sceneBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), scene));
    }

    private void showLine(int dialogueBoxResource, String dialogue) {
        dialogueBox.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), dialogueBoxResource));
        dialogueTextView.setText(StringProcessor.dialogueProcessor(dialogue));
    }

    private void currentLineEnded() {
        DataFunctions.changeCurrentLineGroup(nextLineGroupId, merkado.getPlayerId(), currentQueueIndex);

        new Thread(() -> {
            LineGroup lineGroup = DataFunctions.getLineGroupFromId(nextLineGroupId);
            if (lineGroup == null) {
                finish();
                return;
            }

            DataFunctions.changeNextLineGroup(lineGroup.getDefaultNextLine(), merkado.getPlayerId(), currentQueueIndex);
            nextLineGroupId = lineGroup.getDefaultNextLine();
            runOnUiThread(() -> initializeScreen(lineGroup));
        }).start();
    }

    private void setChoices(List<LineGroup.DialogueChoice> dialogueChoices) {
        Collections.shuffle(dialogueChoices);
        choice1Text.setText(dialogueChoices.get(0).getChoice());
        choice1.setOnClickListener(v -> {
            movingClick(choice1, 1);
            if (dialogueChoices.get(0).getNextLineGroup() != null) {
                nextLineGroupId = dialogueChoices.get(0).getNextLineGroup();
            }
        });
        choice2Text.setText(dialogueChoices.get(1).getChoice());
        choice2.setOnClickListener(v -> {
            movingClick(choice2, 2);
            if (dialogueChoices.get(1).getNextLineGroup() != null) {
                nextLineGroupId = dialogueChoices.get(1).getNextLineGroup();
            }
        });
        choice3Text.setText(dialogueChoices.get(2).getChoice());
        choice3.setOnClickListener(v -> {
            movingClick(choice3, 3);
            if (dialogueChoices.get(2).getNextLineGroup() != null) {
                nextLineGroupId = dialogueChoices.get(2).getNextLineGroup();
            }
        });
        choice4Text.setText(dialogueChoices.get(3).getChoice());
        choice4.setOnClickListener(v -> {
            movingClick(choice4, 4);
            if (dialogueChoices.get(3).getNextLineGroup() != null) {
                nextLineGroupId = dialogueChoices.get(3).getNextLineGroup();
            }
        });
    }

    private void movingClick(ImageView imageView, Integer id) {
        int hover = R.drawable.gui_choice_1_hover;
        int idle = R.drawable.gui_choice_1;
        switch (id) {
            case 2:
                hover = R.drawable.gui_choice_2_hover;
                idle = R.drawable.gui_choice_2;
                break;
            case 3:
                hover = R.drawable.gui_choice_3_hover;
                idle = R.drawable.gui_choice_3;
                break;
            case 4:
                hover = R.drawable.gui_choice_4_hover;
                idle = R.drawable.gui_choice_4;
                break;
        }
        imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), hover));

        int finalIdle = idle;
        new Handler().postDelayed(() -> {
            imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), finalIdle));
        }, 250);

        new Handler().postDelayed(() -> {
            choiceGui.setVisibility(View.GONE);
            continueDialogues();
        }, 400);
    }

    private void continueDialogues() {
        dialogueBox.performClick();
    }

    private void fadeToBlack() {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(5000);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setStartOffset(2000);
        fadeOut.setDuration(3000);

        Animation.AnimationListener listener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                blackScreen.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };

        fadeIn.setAnimationListener(listener);
        fadeOut.setAnimationListener(listener);

        blackScreen.setVisibility(View.VISIBLE);
        // Start the animations
        blackScreen.startAnimation(fadeIn);
        blackScreen.startAnimation(fadeOut);
    }
}