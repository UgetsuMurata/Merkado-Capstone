package com.capstone.merkado.Screens.Game;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.CustomViews.IconButton;
import com.capstone.merkado.CustomViews.IconToggle;
import com.capstone.merkado.CustomViews.IconToggle.ToggleStatus;
import com.capstone.merkado.DataManager.DataFunctionPackage.StoryDataFunctions;
import com.capstone.merkado.DataManager.StaticData.StoryResourceCaller;
import com.capstone.merkado.Helpers.RewardProcessor;
import com.capstone.merkado.Helpers.StoryTriggers;
import com.capstone.merkado.Helpers.StoryVariableHelper;
import com.capstone.merkado.Helpers.StringProcessor;
import com.capstone.merkado.Objects.StoryDataObjects.Chapter;
import com.capstone.merkado.Objects.StoryDataObjects.ImagePlacementData;
import com.capstone.merkado.Objects.StoryDataObjects.LineGroup;
import com.capstone.merkado.Objects.StoryDataObjects.LineGroup.QuizChoice;
import com.capstone.merkado.Objects.StoryDataObjects.PlayerStory;
import com.capstone.merkado.R;

import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class StoryMode extends AppCompatActivity {

    Merkado merkado;

    // DIALOGUE
    TextView dialogueTextView;

    // TOOLS
    IconToggle autoplayToggle, skipToggle;
    IconButton exitButton;
    ImageView dialogueBox;
    ConstraintLayout sceneBackground;

    // DISPLAYS
    FrameLayout characterSlot1, characterSlot2, characterSlot3, characterSlot4, characterSlot5;

    // CHOICE
    ConstraintLayout choiceGui;
    ImageView choice1, choice2, choice3, choice4;
    TextView choice1Text, choice2Text, choice3Text, choice4Text;

    // SCENE DETAILS
    TextView sceneName, chapterName;

    // CLICK VIEW
    View clickArea;

    // TRANSITION
    ImageView blackScreen;

    // VARIABLES
    PlayerStory playerStory;
    Integer currentQueueIndex;
    Integer currentLineGroupIndex = 0;
    Integer nextLineGroupId;
    Integer trigger;
    Boolean temporaryStopAutoPlay = false;
    Boolean isHistory = false;
    Handler handler;
    Runnable runnable;
    Integer quizScore = 0;
    RunnableState runnableState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gam_story_mode);

        if (getIntent().hasExtra("PROLOGUE")) {
            StoryTriggers.objectives(this, 1);
        }

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        sceneBackground = findViewById(R.id.scene_background);
        dialogueTextView = findViewById(R.id.dialogue);
        dialogueBox = findViewById(R.id.dialogue_box);
        autoplayToggle = findViewById(R.id.autoplay);
        skipToggle = findViewById(R.id.skip);
        exitButton = findViewById(R.id.exit);
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
        clickArea = findViewById(R.id.click_area);
        sceneName = findViewById(R.id.scene_name);
        chapterName = findViewById(R.id.chapter_name);

        handler = new Handler();
        runnableState = RunnableState.NULL;

        // get the lineGroup from the intent
        playerStory = getIntent().getParcelableExtra("PLAYER_STORY");
        currentQueueIndex = getIntent().getIntExtra("CURRENT_QUEUE_INDEX", -1);
        isHistory = getIntent().getBooleanExtra("HISTORY", false);
        nextLineGroupId = playerStory.getNextLineGroup() != null ?
                playerStory.getNextLineGroup().getId() : null;

        if (playerStory == null) {
            Toast.makeText(getApplicationContext(), "Cannot retrieve story. Please try again later.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        trigger = playerStory.getTrigger();

        initializeScreen(playerStory.getCurrentLineGroup());

        autoplayToggle.setOnClickListener(v -> clickArea.performClick());
        exitButton.setOnClickListener(v -> goToExit());
        skipToggle.setOnClickListener(v -> {
            if (skipToggle.isActive()) skipDialogues();
        });
    }

    /**
     * <b>TOOL FUNCTION</b>. Schedules an autoplay. This is triggered by each dialogue change. Checks the value of the autoplay: <br/>
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
        if (autoplayToggle.isActive()) {
            // if autoplay is on
            // define runnable
            if (!runnableState.equals(RunnableState.NULL))
                // clear any scheduled autoplay with this handler.
                handler.removeCallbacks(runnable);
            if (!runnableState.equals(RunnableState.AUTOPLAY)) {
                // define the runnable
                runnable = () -> {
                    if (autoplayToggle.isActive() && !temporaryStopAutoPlay) {
                        // check if autoplay is still ON and if the temporary stop is OFF
                        clickArea.performClick();
                    }
                };
                runnableState = RunnableState.AUTOPLAY;
            }
            // Calculate how many seconds before next click
            int wordCount = dialogue.split("\\s").length;
            int seconds = wordCount / 3 + (wordCount % 3 > 0 ? 1 : 0);

            // Schedule the runnable
            handler.postDelayed(runnable, seconds * 1000L);
        }
    }

    /**
     * <b>TOOL FUNCTION</b>. Pauses autoplay temporarily.
     */
    private void pauseAutoplay() {
        temporaryStopAutoPlay = true;
    }

    /**
     * <b>TOOL FUNCTION</b>. Skips dialogues every 100ms.
     */
    private void skipDialogues() {
        // define runnable
        if (!runnableState.equals(RunnableState.NULL))
            // clear any scheduled autoplay with this handler.
            handler.removeCallbacks(runnable);
        if (!runnableState.equals(RunnableState.SKIP)) {
            // define the runnable if it was null
            runnable = () -> {
                if (skipToggle.isActive()) {
                    clickArea.performClick();
                    handler.postDelayed(runnable, 100L);
                }
            };
            runnableState = RunnableState.SKIP;
        }
        // schedule runnable
        handler.postDelayed(runnable, 100L);
    }

    /**
     * <b>TOOL FUNCTION</b>. Removes any autoplay callbacks and then finishes after half-a-second.
     */
    private void goToExit() {
        new Handler().postDelayed(() -> {
            if (runnable != null) {
                // clear any scheduled autoplay with this handler.
                handler.removeCallbacks(runnable);
                runnableState = RunnableState.NULL;
            }
            runOnUiThread(this::finish);
        }, 500);
    }

    /**
     * <b>VISUAL FUNCTION</b>. Initializes the screen for the next line group; Clears the characters,
     * clears the dialogue box, updates the current line group in PlayerStory variable, runs the
     * transition, calls the function for setting up the screen.
     *
     * @param lineGroup LineGroup instance.
     */
    private void initializeScreen(LineGroup lineGroup) {
        // Clear the screen
        clearCharacters();
        clearDialogueBox();

        // update current line group
        playerStory.setCurrentLineGroup(lineGroup);

        // check for transition
        if ("FTB".equals(lineGroup.getTransition())) {
            new Thread(this::fadeToBlack).start();
        }

        // call the function for setting up screen
        setUpScreen(lineGroup);
    }

    /**
     * <b>VISUAL FUNCTION</b>. Sets up the screen for the dialogue group.
     *
     * @param lineGroup LineGroup instance.
     */
    private void setUpScreen(@NonNull LineGroup lineGroup) {
        // reset index
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
            displayLine(dialogueLine);

            if (dialogueLine.getVariable() != null)
                if ("GET".equals(dialogueLine.getVariable().getMethod())) {
                    try {
                        playerStory = StoryVariableHelper.processVariable(merkado.getPlayerId(), currentQueueIndex, dialogueLine.getVariable(), playerStory).get();
                    } catch (ExecutionException | InterruptedException e) {
                        Log.e("processVariable",
                                String.format(
                                        "StoryVariableHelper.processVariable() received an error: %s",
                                        e));
                    }
                } else {
                    StoryVariableHelper.processVariable(merkado.getPlayerId(), currentQueueIndex, dialogueLine.getVariable(), playerStory);
                }

            // set up onClickListener for the click area.
            clickArea.setOnClickListener(v -> {
                // increment the index
                currentLineGroupIndex++;

                // check if the index exceeds the size of the line group. return if so.
                if (currentLineGroupIndex >= lineGroup.getDialogueLines().size()) {
                    // reached the end.
                    currentLineEnded();
                    return;
                }

                // display line
                try {
                    LineGroup.DialogueLine dialogueLine1 = lineGroup.getDialogueLines().get(currentLineGroupIndex);
                    displayLine(dialogueLine1);
                } catch (ArrayIndexOutOfBoundsException ignore) {
                    currentLineEnded(); // reached the end.
                }
            });
        }, skipToggle.isActive() ? 10 : 500);

        // hide the choice GUI
        choiceGui.setVisibility(View.GONE);
    }

    /**
     * <b>VISUAL FUNCTION</b>. Checks the DialogueLine instance for cases (i.e. pure dialogue, playing
     * sfx, choices, quiz, or image changes.)
     *
     * @param dialogueLine DialogueLine instance.
     */
    private void displayLine(@NonNull LineGroup.DialogueLine dialogueLine) {
        showLine(StoryResourceCaller.retrieveDialogueBoxResource(dialogueLine.getCharacter()), dialogueLine.getDialogue());
        playSFX(dialogueLine.getSfx());
        if (dialogueLine.getDialogueChoices() != null) {
            choiceGui.setVisibility(View.VISIBLE);
            setChoices(dialogueLine.getDialogueChoices());
        }
        if (dialogueLine.getQuizChoices() != null) {
            choiceGui.setVisibility(View.VISIBLE);
            setQuizChoices(dialogueLine.getQuizChoices());
        }
        if (dialogueLine.getImageChanges() != null) {
            for (ImagePlacementData imagePlacementData : dialogueLine.getImageChanges()) {
                showCharacter(imagePlacementData);
            }
        }
    }

    /**
     * <b>VISUAL FUNCTION</b>. Shows (or hides) an image resource indicated in the ImagePlacementData.
     *
     * @param imagePlacementData ImagePlacementData instance from the Player instance.
     */
    private void showCharacter(@NonNull ImagePlacementData imagePlacementData) {
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

    /**
     * <b>VISUAL FUNCTION</b>. Removes all characters on screen.
     */
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

    /**
     * <b>VISUAL FUNCTION</b>. Removes text from the dialogue box and changes the image into empty.
     */
    private void clearDialogueBox() {
        dialogueTextView.setText("");
        dialogueBox.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), StoryResourceCaller.retrieveDialogueBoxResource("")));
    }

    /**
     * <b>VISUAL FUNCTION</b>. Change the background.
     *
     * @param scene {@code resId}.
     */
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
        merkado.setBGM(getApplicationContext(), bgmResource, bgmData.length == 2 && "loop".equals(bgmData[1]));
    }

    private void currentLineEnded() {
        if (!isHistory)
            RewardProcessor.processRewards(
                    this,
                    merkado.getPlayerId(),
                    playerStory.getCurrentScene().getRewards());
        if (playerStory.getCurrentLineGroup().getIsQuiz() != null && playerStory.getCurrentLineGroup().getIsQuiz()) {
            // change nextLineGroupId based on the quizScore.
            nextLineGroupId = processQuizNextLineGroup(quizScore, playerStory.getCurrentLineGroup().getNextLineCode());

            // reset the quizScore
            quizScore = 0;
        } else if (nextLineGroupId == null || nextLineGroupId == -1) {
            StoryDataFunctions.addStoryHistoryToPlayer(merkado.getPlayerId(), playerStory.getChapter().getId(), playerStory.getCurrentScene().getId());
            currentSceneEnded();
            return;
        }
        if (!isHistory)
            StoryDataFunctions.changeCurrentLineGroup(nextLineGroupId, merkado.getPlayerId(), currentQueueIndex);

        new Thread(() -> {
            LineGroup lineGroup = StoryDataFunctions.getLineGroupFromId(playerStory.getChapter().getId(), playerStory.getCurrentScene().getId(), nextLineGroupId);
            if (lineGroup == null) {
                Toast.makeText(getApplicationContext(), "The story encountered a problem. Please try again later.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            if (!isHistory)
                StoryDataFunctions.changeNextLineGroup(lineGroup.getDefaultNextLine(), merkado.getPlayerId(), currentQueueIndex);
            nextLineGroupId = lineGroup.getDefaultNextLine();
            runOnUiThread(() -> initializeScreen(lineGroup));
        }).start();
    }

    private void currentSceneEnded() {
        if (isHistory) {
            finish();
            return;
        }
        Chapter.Scene currentScene = playerStory.getNextScene();
        if (currentScene == null) {
            finish();
            StoryDataFunctions.removeStoryQueueId(merkado.getPlayerId(), currentQueueIndex);
            if (trigger != null) StoryTriggers.trigger(merkado.getPlayerId(), trigger);
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
        StoryDataFunctions.changeCurrentScene(Math.toIntExact(currentScene.getId()), merkado.getPlayerId(), currentQueueIndex);
        StoryDataFunctions.changeNextScene(currentScene.getNextScene() == null ? null : Math.toIntExact(currentScene.getNextScene()), merkado.getPlayerId(), currentQueueIndex);
        new Thread(() -> {
            StoryDataFunctions.changeCurrentLineGroup(0, merkado.getPlayerId(), currentQueueIndex);
            LineGroup lineGroup = StoryDataFunctions.getLineGroupFromId(playerStory.getChapter().getId(), currentScene.getId(), 0);
            if (lineGroup == null) {
                Toast.makeText(getApplicationContext(), "The story encountered a problem. Please try again later.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            StoryDataFunctions.changeNextLineGroup(lineGroup.getDefaultNextLine(), merkado.getPlayerId(), currentQueueIndex);
            nextLineGroupId = lineGroup.getDefaultNextLine();
            runOnUiThread(() -> initializeScreen(lineGroup));
        }).start();
    }

    private void setChoices(List<LineGroup.DialogueChoice> dialogueChoices) {
        Collections.shuffle(dialogueChoices);
        pauseAutoplay();
        disableClickArea();
        choice1Text.setText(dialogueChoices.get(0).getChoice());
        choice1.setOnClickListener(v -> {
            movingClick(choice1, 1);
            continueFromChoices();
            if (dialogueChoices.get(0).getNextLineGroup() != null) {
                nextLineGroupId = dialogueChoices.get(0).getNextLineGroup();
            }
        });
        choice2Text.setText(dialogueChoices.get(1).getChoice());
        choice2.setOnClickListener(v -> {
            movingClick(choice2, 2);
            continueFromChoices();
            if (dialogueChoices.get(1).getNextLineGroup() != null) {
                nextLineGroupId = dialogueChoices.get(1).getNextLineGroup();
            }
        });
        choice3Text.setText(dialogueChoices.get(2).getChoice());
        choice3.setOnClickListener(v -> {
            movingClick(choice3, 3);
            continueFromChoices();
            if (dialogueChoices.get(2).getNextLineGroup() != null) {
                nextLineGroupId = dialogueChoices.get(2).getNextLineGroup();
            }
        });
        choice4Text.setText(dialogueChoices.get(3).getChoice());
        choice4.setOnClickListener(v -> {
            movingClick(choice4, 4);
            continueFromChoices();
            if (dialogueChoices.get(3).getNextLineGroup() != null) {
                nextLineGroupId = dialogueChoices.get(3).getNextLineGroup();
            }
        });
    }

    private void continueFromChoices() {
        new Handler().postDelayed(() -> {
            enableClickArea();
            choiceGui.setVisibility(View.GONE);
            continueDialogues();
        }, 400);
    }

    private void setQuizChoices(List<QuizChoice> quizChoices) {
        Collections.shuffle(quizChoices);
        pauseAutoplay();
        disableClickArea();
        choice1Text.setText(quizChoices.get(0).getChoice());
        choice1.setOnClickListener(v -> {
            movingClick(choice1, 1);
            quizScore += quizChoices.get(0).getPoints();
            continueFromQuiz(quizChoices.get(0).getDialogueLine());
        });
        choice2Text.setText(quizChoices.get(1).getChoice());
        choice2.setOnClickListener(v -> {
            movingClick(choice2, 2);
            quizScore += quizChoices.get(1).getPoints();
            continueFromQuiz(quizChoices.get(1).getDialogueLine());
        });
        choice3Text.setText(quizChoices.get(2).getChoice());
        choice3.setOnClickListener(v -> {
            movingClick(choice3, 3);
            quizScore += quizChoices.get(2).getPoints();
            continueFromQuiz(quizChoices.get(2).getDialogueLine());
        });
        choice4Text.setText(quizChoices.get(3).getChoice());
        choice4.setOnClickListener(v -> {
            movingClick(choice4, 4);
            quizScore += quizChoices.get(3).getPoints();
            continueFromQuiz(quizChoices.get(3).getDialogueLine());
        });
    }

    private void continueFromQuiz(LineGroup.DialogueLine dialogueLine) {
        enableClickArea();
        displayLine(dialogueLine);
        choiceGui.setVisibility(View.GONE);
        temporaryStopAutoPlay = false;
    }

    private void disableClickArea() {
        clickArea.setVisibility(View.GONE);
        skipToggle.disable();
        exitButton.disable();
        autoplayToggle.disable();
    }

    private void enableClickArea() {
        clickArea.setVisibility(View.VISIBLE);
        skipToggle.enable();
        exitButton.enable();
        autoplayToggle.enable();

        // check for skipping dialogues
        if (skipToggle.isActive()) skipDialogues();
    }

    private Integer processQuizNextLineGroup(Integer currentScore, @NonNull String nextLineCode) {
        Integer nextLine = nextLineGroupId; // this will be the default if ever none matched the condition string.
        for (String condition : nextLineCode.split(";")) {
            // condition = "score==2:4";
            if (condition.startsWith("score==")) {
                Integer[] processedCondition = conditionProcessor(condition, "score==");
                if (processedCondition[0] == -1) continue;
                if (Objects.equals(currentScore, processedCondition[0])) {
                    nextLine = processedCondition[1];
                    break;
                }
            } else if (condition.startsWith("score>=")) {
                Integer[] processedCondition = conditionProcessor(condition, "score>=");
                if (processedCondition[0] >= -1) continue;
                if (Objects.equals(currentScore, processedCondition[0])) {
                    nextLine = processedCondition[1];
                    break;
                }
            } else if (condition.startsWith("score>")) {
                Integer[] processedCondition = conditionProcessor(condition, "score>");
                if (processedCondition[0] > -1) continue;
                if (Objects.equals(currentScore, processedCondition[0])) {
                    nextLine = processedCondition[1];
                    break;
                }
            } else if (condition.startsWith("score<=")) {
                Integer[] processedCondition = conditionProcessor(condition, "score<=");
                if (processedCondition[0] <= -1) continue;
                if (Objects.equals(currentScore, processedCondition[0])) {
                    nextLine = processedCondition[1];
                    break;
                }
            } else if (condition.startsWith("score<")) {
                Integer[] processedCondition = conditionProcessor(condition, "score<");
                if (processedCondition[0] < -1) continue;
                if (Objects.equals(currentScore, processedCondition[0])) {
                    nextLine = processedCondition[1];
                    break;
                }
            } else if (condition.startsWith("score!=")) {
                Integer[] processedCondition = conditionProcessor(condition, "score!=");
                if (processedCondition[0] != -1) continue;
                if (Objects.equals(currentScore, processedCondition[0])) {
                    nextLine = processedCondition[1];
                    break;
                }
            }
        }
        return nextLine;
    }

    @NonNull
    @Contract("_, _ -> new")
    private Integer[] conditionProcessor(@NonNull String condition, String startsWith) {
        String[] numbers = condition.substring(condition.indexOf(startsWith) + startsWith.length()).split(":");
        int conditionNumber;
        int nextLineNumber;
        try {
            // make sure that the numbers from the condition is parsable.
            conditionNumber = Integer.parseInt(numbers[0]);
            nextLineNumber = Integer.parseInt(numbers[1]);
        } catch (NumberFormatException e) {
            Log.e("conditionProcessor", "Error parsing numbers from condition: " + condition, e);
            return new Integer[]{-1, -1};
        }
        return new Integer[]{conditionNumber, nextLineNumber};
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
        new Handler().postDelayed(() ->
                        imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), finalIdle))
                , 250);
    }

    private void continueDialogues() {
        clickArea.performClick();
        temporaryStopAutoPlay = false;
    }

    private void fadeToBlack() {
        runOnUiThread(() -> {
            blackScreen.setVisibility(View.VISIBLE);
            blackScreen.setAlpha(0f);
            blackScreen.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .withEndAction(() ->
                            blackScreen.animate()
                                    .alpha(0f)
                                    .setDuration(500)
                                    .start()).start();
        });
    }

    public enum RunnableState {
        SKIP, AUTOPLAY, NULL
    }

    @Override
    protected void onPause() {
        super.onPause();
        skipToggle.setStatus(ToggleStatus.IDLE);
        pauseAutoplay();
    }
}