package com.capstone.merkado.Screens.Game;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.DataManager.DataFunctions;
import com.capstone.merkado.DataManager.StaticData.StoryResourceCaller;
import com.capstone.merkado.Helpers.StringProcessor;
import com.capstone.merkado.Objects.StoryDataObjects.Chapter;
import com.capstone.merkado.Objects.StoryDataObjects.ImagePlacementData;
import com.capstone.merkado.Objects.StoryDataObjects.LineGroup;
import com.capstone.merkado.Objects.StoryDataObjects.PlayerStory;
import com.capstone.merkado.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class StoryMode extends AppCompatActivity {

    Merkado merkado;
    PlayerStory playerStory;

    /**
     * Dialogue
     */
    TextView dialogueTextView;
    /**
     * Tools
     */
    ImageView autoplayButton, skip, exit;
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
     * SCENE DETAILS
     */
    TextView sceneName, chapterName;

    /**
     * TRANSITION
     */
    ImageView blackScreen;

    Integer currentLineGroupIndex = 0;
    Integer nextLineGroupId;
    Integer currentQueueIndex;
    Boolean temporaryStopAutoPlay = false;
    Boolean autoPlay = false;
    Boolean skipDialogues = false;
    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gam_story_mode);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        sceneBackground = findViewById(R.id.scene_background);
        dialogueTextView = findViewById(R.id.dialogue);
        dialogueBox = findViewById(R.id.dialogue_box);
        autoplayButton = findViewById(R.id.autoplay);
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
        sceneName = findViewById(R.id.scene_name);
        chapterName = findViewById(R.id.chapter_name);

        // get the lineGroup from the intent
        playerStory = getIntent().getParcelableExtra("PLAYER_STORY");
        currentQueueIndex = getIntent().getIntExtra("CURRENT_QUEUE_INDEX", -1);
        nextLineGroupId = playerStory.getNextLineGroup() != null ? playerStory.getNextLineGroup().getId() : null;

        if (playerStory == null) {
            finish();
            Toast.makeText(getApplicationContext(), "Cannot retrieve story. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        initializeScreen(playerStory.getCurrentLineGroup());

        handler = new Handler();

        autoplayButton.setOnClickListener(v -> {
            autoPlay = !autoPlay;
            if (autoPlay) {
                autoplayButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gui_storymode_autoplay_active));
            } else {
                autoplayButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gui_storymode_autoplay_idle));
            }
            dialogueBox.performClick();
        });
        exit.setOnClickListener(v -> goToExit());
        skip.setOnClickListener(v -> {
            skipDialogues();
            if (skipDialogues) {
                skip.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gui_storymode_fast_forward_active));
            } else {
                skip.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gui_storymode_fast_forward_idle));
            }
        });
    }

    /**
     * Schedules an autoplay. This is triggered by each dialogue change. Checks the value of the autoplay: <br/>
     * If <b>True</b>, then it will successfully play after the seconds determined by how long the dialogue is and
     * the calculation of 183 wpm or 3 words per second. <br/>
     * If <b>False</b>, then it will not schedule an autoplay.
     * Note: If temporary stop for an autoplay is on (which is during choice selection), the autoplay will not fire
     * if the seconds indicated is done. But when a choice is clicked, the autoplay will still continue as clicking
     * a choice will trigger a dialogue change.
     *
     * @param dialogue raw dialogue.
     */
    private void scheduleAutoPlay(String dialogue) {
        if (autoPlay) {
            if (runnable != null)
                // clear any scheduled autoplay with this handler.
                handler.removeCallbacks(runnable);
            else
                // define the runnable if it was null
                runnable = () -> {
                    if (autoPlay && !temporaryStopAutoPlay) {
                        dialogueBox.performClick();
                    }
                };

            int wordCount = dialogue.split("\\s").length;
            int seconds = wordCount / 3 + (wordCount % 3 > 0 ? 1 : 0);

            handler.postDelayed(runnable, seconds * 1000L);
        }
    }

    private void pauseAutoplay() {
        temporaryStopAutoPlay = true;
    }

    private void skipDialogues() {
        autoPlay = false;
        skipDialogues = true;
        if (runnable != null)
            // clear any scheduled autoplay with this handler.
            handler.removeCallbacks(runnable);
        else
            // define the runnable if it was null
            runnable = () -> {
                if (skipDialogues) {
                    dialogueBox.performClick();
                    handler.postDelayed(runnable, 100L);
                }
            };
        handler.postDelayed(runnable, 100L);
    }

    private void stopSkipping() {
        skipDialogues = false;
    }

    /**
     * Removes any autoplay callbacks and then finishes after half-a-second.
     */
    private void goToExit() {
        exit.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gui_storymode_exit_active));

        new Handler().postDelayed(() -> {
            exit.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gui_storymode_exit_idle));
        }, 300);

        new Handler().postDelayed(() -> {
            if (runnable != null)
                // clear any scheduled autoplay with this handler.
                handler.removeCallbacks(runnable);
            runOnUiThread(this::finish);
        }, 500);


    }

    private void initializeScreen(LineGroup lineGroup) {
        clearCharacters();
        clearDialogueBox();

        if ("FTB".equals(lineGroup.getTransition())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    fadeToBlack();
                    runOnUiThread(() -> setUpScreen(lineGroup));
                }
            }).start();
        } else {
            setUpScreen(lineGroup);
        }
    }

    private void setUpScreen(LineGroup lineGroup) {
        currentLineGroupIndex = 0;
        // change chapter/scene details
        sceneName.setText(playerStory.getCurrentScene().getScene());
        chapterName.setText(playerStory.getChapter().getChapter());


        // display initial images
        if (lineGroup.getInitialImages() != null) {
            for (ImagePlacementData imagePlacementData : lineGroup.getInitialImages()) {
                showCharacter(imagePlacementData);
            }
        }
        // display initial background
        changeBackground(StoryResourceCaller.retrieveBackgroundResource(lineGroup.getBackground()));
        playBGM(lineGroup.getBgm());

        // after half-a-second, start the story.
        new Handler().postDelayed(() -> {
            // display first line
            LineGroup.DialogueLine dialogueLine = lineGroup.getDialogueLines().get(currentLineGroupIndex);
            showLine(StoryResourceCaller.retrieveDialogueBoxResource(dialogueLine.getCharacter()), dialogueLine.getDialogue());
            playSFX(dialogueLine.getSfx());
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
                if (currentLineGroupIndex >= lineGroup.getDialogueLines().size()) {
                    // reached the end.
                    currentLineEnded();
                    return;
                }
                // display first line
                LineGroup.DialogueLine dialogueLine1 = lineGroup.getDialogueLines().get(currentLineGroupIndex);
                showLine(StoryResourceCaller.retrieveDialogueBoxResource(dialogueLine1.getCharacter()), dialogueLine1.getDialogue());
                playSFX(dialogueLine1.getSfx());
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

    private void clearDialogueBox() {
        dialogueTextView.setText("");
        dialogueBox.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), StoryResourceCaller.retrieveDialogueBoxResource("")));
    }

    private void changeBackground(int scene) {
        sceneBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), scene));
    }

    private void showLine(int dialogueBoxResource, String dialogue) {
        dialogueBox.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), dialogueBoxResource));
        dialogueTextView.setText(Html.fromHtml(StringProcessor.dialogueProcessor(dialogue), HtmlCompat.FROM_HTML_MODE_LEGACY));
        scheduleAutoPlay(dialogue);
    }

    private void playSFX(String sfxFile) {
        if (sfxFile == null) return;
        int sfxResource = StoryResourceCaller.getSFX(sfxFile);
        if (sfxResource == -1) return;
        merkado.setSFX(getApplicationContext(), sfxResource);
    }

    private void playBGM(String bgmString) {
        if (bgmString == null) return;
        String[] bgmData = bgmString.split(" ");
        int bgmResource = StoryResourceCaller.getBGM(bgmData[0]);
        merkado.setBGM(getApplicationContext(), bgmResource, "loop".equals(bgmData[1]));
    }

    private void currentLineEnded() {
        if (nextLineGroupId == null || nextLineGroupId == -1) {
            currentSceneEnded();
            return;
        }
        DataFunctions.changeCurrentLineGroup(nextLineGroupId, merkado.getPlayerId(), currentQueueIndex);

        new Thread(() -> {
            LineGroup lineGroup = DataFunctions.getLineGroupFromId(playerStory.getChapter().getId(), playerStory.getCurrentScene().getId(), nextLineGroupId);
            if (lineGroup == null) {
                Toast.makeText(getApplicationContext(), "The story encountered a problem. Please try again later.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            DataFunctions.changeNextLineGroup(lineGroup.getDefaultNextLine(), merkado.getPlayerId(), currentQueueIndex);
            nextLineGroupId = lineGroup.getDefaultNextLine();
            runOnUiThread(() -> initializeScreen(lineGroup));
        }).start();
    }

    private void currentSceneEnded() {
        Chapter.Scene currentScene = playerStory.getNextScene();
        if (currentScene == null) {
            finish();
            return;
        }
        playerStory.setCurrentScene(currentScene);
        if (currentScene.getNextScene() != null) {
            for (Chapter.Scene scene : playerStory.getChapter().getScenes()) {
                if (Math.toIntExact(scene.getId()) == currentScene.getNextScene()) {
                    playerStory.setNextScene(scene);
                    break;
                }
            }
        } else playerStory.setNextScene(null);
        DataFunctions.changeCurrentScene(Math.toIntExact(currentScene.getId()), merkado.getPlayerId(), currentQueueIndex);
        DataFunctions.changeNextScene(currentScene.getNextScene() == null ? null : Math.toIntExact(currentScene.getNextScene()), merkado.getPlayerId(), currentQueueIndex);
        new Thread(() -> {
            DataFunctions.changeCurrentLineGroup(0, merkado.getPlayerId(), currentQueueIndex);
            LineGroup lineGroup = DataFunctions.getLineGroupFromId(playerStory.getChapter().getId(), currentScene.getId(), 0);
            if (lineGroup == null) {
                Toast.makeText(getApplicationContext(), "The story encountered a problem. Please try again later.", Toast.LENGTH_SHORT).show();
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
        pauseAutoplay();
        stopSkipping();
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
        temporaryStopAutoPlay = false;
    }

    private void fadeToBlack() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        runOnUiThread(() -> {
            blackScreen.setVisibility(View.VISIBLE);
            blackScreen.setAlpha(0f);
            blackScreen.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .withEndAction(() -> {
                        future.complete(null);
                        blackScreen.animate()
                                .alpha(0f)
                                .setDuration(500)
                                .start();
                    }).start();
        });
        try {
            future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopSkipping();
        pauseAutoplay();
    }
}