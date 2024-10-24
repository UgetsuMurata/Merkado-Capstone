package com.capstone.merkado.Screens.Game.Story;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.CustomViews.IconButton;
import com.capstone.merkado.CustomViews.IconToggle;
import com.capstone.merkado.CustomViews.IconToggle.ToggleStatus;
import com.capstone.merkado.DataManager.DataFunctionPackage.PlayerDataFunctions;
import com.capstone.merkado.DataManager.DataFunctionPackage.StoryDataFunctions;
import com.capstone.merkado.DataManager.StaticData.StoryResourceCaller;
import com.capstone.merkado.Helpers.NotificationHelper.InAppNotification;
import com.capstone.merkado.Helpers.PlayerActions;
import com.capstone.merkado.Helpers.RewardProcessor;
import com.capstone.merkado.Helpers.StoryVariableHelper;
import com.capstone.merkado.Helpers.StringProcessor;
import com.capstone.merkado.Helpers.TriggerProcessor;
import com.capstone.merkado.Objects.PlayerDataObjects.PlayerFBExtractor1.PlayerObjectives;
import com.capstone.merkado.Objects.ServerDataObjects.Objectives;
import com.capstone.merkado.Objects.StoryDataObjects.Chapter;
import com.capstone.merkado.Objects.StoryDataObjects.Chapter.GameRewards;
import com.capstone.merkado.Objects.StoryDataObjects.ImagePlacementData;
import com.capstone.merkado.Objects.StoryDataObjects.LineGroup;
import com.capstone.merkado.Objects.StoryDataObjects.LineGroup.QuizChoice;
import com.capstone.merkado.Objects.StoryDataObjects.PlayerStory;
import com.capstone.merkado.R;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class StoryMode extends AppCompatActivity {

    Merkado merkado;

    // DIALOGUE
    TextView dialogueTextView;

    HandlerThread backgroundThread;

    // TOOLS
    IconToggle autoplayToggle, skipToggle;
    IconButton exitButton;
    ImageView dialogueBox;
    ConstraintLayout sceneBackground;

    // DISPLAYS
    CharacterImage characterSlot1, characterSlot2, characterSlot3, characterSlot4, characterSlot5;

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
    Integer currentDialogueIndex = 0;
    Integer nextLineGroupId;
    Integer trigger;
    Boolean temporaryStopAutoPlay = false;
    Boolean isHistory = false;
    Handler backgroundHandler;
    Handler autoClickHandler;
    Runnable setupScreenRunnable;

    Runnable runnable;
    Integer quizScore = 0;
    RunnableState runnableState;

    Long chapterIndex;
    Long sceneIndex;
    Integer lineGroupIndex;

    InAppNotification inAppNotification;

    Boolean waitForNextChapter_start = false;
    Boolean waitForNextChapter_end = false;
    Boolean waitForNextScene_start = false;
    Boolean waitForNextScene_end = false;
    Boolean waitForNextLineGroup_start = false;
    Boolean waitForNextLineGroup_end = false;

    ActivityResultLauncher<Intent> quizLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null) {
                        Integer score = result.getData().getIntExtra("SCORE", -1);
                        Integer expReward = result.getData().getIntExtra("EXP_REWARD", -1);
                        Integer quizId = result.getData().getIntExtra("QUIZ_ID", -1);
                        processQuizReward(expReward);
                        StoryDataFunctions.setStudentScore(merkado.getPlayerId(), quizId, score);
                        currentLineEnded();
                    }
                }
            });

    ActivityResultLauncher<Intent> objectiveDisplayLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    checkObjective();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gam_story_mode);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        backgroundThread = new HandlerThread("MAIN_BACKGROUND_THREAD");
        backgroundThread.start();

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
        characterSlot1 = new CharacterImage(getApplicationContext(), findViewById(R.id.character_slot_1));
        characterSlot2 = new CharacterImage(getApplicationContext(), findViewById(R.id.character_slot_2));
        characterSlot3 = new CharacterImage(getApplicationContext(), findViewById(R.id.character_slot_3));
        characterSlot4 = new CharacterImage(getApplicationContext(), findViewById(R.id.character_slot_4));
        characterSlot5 = new CharacterImage(getApplicationContext(), findViewById(R.id.character_slot_5));
        blackScreen = findViewById(R.id.black_screen);
        clickArea = findViewById(R.id.click_area);
        sceneName = findViewById(R.id.scene_name);
        chapterName = findViewById(R.id.chapter_name);

        inAppNotification = new InAppNotification(findViewById(R.id.notification_view));

        // trigger objective display
        if (getIntent().hasExtra("PROLOGUE")) {
            TriggerProcessor.objectives(this, 1, objectiveDisplayLauncher);
        }

        backgroundHandler = new Handler(backgroundThread.getLooper());
        autoClickHandler = new Handler();
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

        chapterIndex = playerStory.getChapter().getId();
        sceneIndex = playerStory.getCurrentScene().getId();
        lineGroupIndex = playerStory.getCurrentLineGroup().getId();
        waitForNextChapter_start = merkado.getPlayerData().getObjectiveDone() != null && merkado.getPlayerData().getObjectiveDone();
        waitForNextChapter_end = waitForNextChapter_start;

        if (!getIntent().hasExtra("PROLOGUE")) {
            checkObjective();
        }

        trigger = playerStory.getTrigger();

        playBGM(playerStory.getCurrentScene().getBgm());
        backgroundHandler.post(() -> initializeScreen(playerStory.getCurrentLineGroup()));

        autoplayToggle.setOnClickListener(v -> clickArea.performClick());
        exitButton.setOnClickListener(v -> goToExit());
        skipToggle.setOnClickListener(v -> {
            if (skipToggle.isActive()) skipDialogues();
        });

        merkado.getPlayerActionTask().setOnTaskSuccess(gameRewards ->
                RewardProcessor.processRewards(this,
                        merkado.getPlayer().getServer(),
                        merkado.getPlayerId(),
                        gameRewards));
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
                autoClickHandler.removeCallbacks(runnable);
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
            autoClickHandler.postDelayed(runnable, seconds * 1000L);
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
            autoClickHandler.removeCallbacks(runnable);
        if (!runnableState.equals(RunnableState.SKIP)) {
            // define the runnable if it was null
            runnable = () -> {
                if (skipToggle.isActive()) {
                    clickArea.performClick();
                    autoClickHandler.postDelayed(runnable, 100L);
                }
            };
            runnableState = RunnableState.SKIP;
        }
        // schedule runnable
        autoClickHandler.postDelayed(runnable, 100L);
    }

    /**
     * <b>TOOL FUNCTION</b>. Removes any autoplay callbacks and then finishes after half-a-second.
     */
    private void goToExit() {
        new Handler().postDelayed(() -> {
            if (runnable != null) {
                // clear any scheduled autoplay with this handler.
                backgroundHandler.removeCallbacks(runnable);
                runnableState = RunnableState.NULL;
            }
            backgroundThread.quit();
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
        backgroundHandler.removeCallbacks(setupScreenRunnable);
        backgroundHandler.removeCallbacksAndMessages(null);
        // Clear the screen
        clearCharacters();
        clearDialogueBox();

        // update current line group
        playerStory.setCurrentLineGroup(lineGroup);

        // check for transition
        if ("FTB".equals(lineGroup.getTransition())) {
            backgroundHandler.post(this::fadeToBlack);
        }

        // call the function for setting up screen
        // after half-a-second, start the story.
        setupScreenRunnable = () -> setUpScreen(lineGroup);
        backgroundHandler.postDelayed(setupScreenRunnable,
                skipToggle.isActive() ? 10 : 200);
    }

    /**
     * <b>VISUAL FUNCTION</b>. Sets up the screen for the dialogue group.
     *
     * @param lineGroup LineGroup instance.
     */
    private void setUpScreen(@NonNull LineGroup lineGroup) {
        // reset index
        currentDialogueIndex = 0;

        // change chapter/scene details
        runOnUiThread(() -> {
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
        });

        // display first line
        try {
            LineGroup.DialogueLine dialogueLine = lineGroup.getDialogueLines().get(currentDialogueIndex);
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
            runOnUiThread(() ->
                    clickArea.setOnClickListener(v -> {
                        clickArea.setEnabled(false);
                        new Handler(Looper.getMainLooper()).postDelayed(() ->
                                clickArea.setEnabled(true), RunnableState.SKIP.equals(runnableState) ? 75L : 205L);

                        if (sceneIndex == 15 && lineGroupIndex == 0) {
                            Log.d("PREPARE", "QUIZ PREPARE");
                        }

                        // increment the index
                        currentDialogueIndex++;
                        if (!waitForNextLineGroup_start || !waitForNextLineGroup_end)
                            checkObjective();

                        // check if the index exceeds the size of the line group. return if so.
                        if (currentDialogueIndex >= lineGroup.getDialogueLines().size()) {
                            // reached the end.
                            if (lineGroup.getGradedQuiz() != null) {
                                openQuizDisplay(lineGroup.getGradedQuiz(), lineGroup.getBackground());
                            } else currentLineEnded();
                            return;
                        }

                        // display line
                        try {
                            LineGroup.DialogueLine dialogueLine1 = lineGroup.getDialogueLines().get(currentDialogueIndex);
                            displayLine(dialogueLine1);
                        } catch (ArrayIndexOutOfBoundsException ignore) {
                            // reached the end.
                            if (lineGroup.getGradedQuiz() != null) {
                                openQuizDisplay(lineGroup.getGradedQuiz(), lineGroup.getBackground());
                            } else currentLineEnded();
                        }
                    }));
        } catch (IndexOutOfBoundsException e) {
            Log.e("Dialogue Line Index Error", String.format("%d;%d;%d;%d;%s", chapterIndex,
                    sceneIndex, lineGroupIndex, currentDialogueIndex, e.getMessage()));
        }

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
            runOnUiThread(() -> {
                choiceGui.setVisibility(View.VISIBLE);
                setChoices(dialogueLine.getDialogueChoices());
            });
        }
        if (dialogueLine.getQuizChoices() != null) {
            runOnUiThread(() -> {
                choiceGui.setVisibility(View.VISIBLE);
                setQuizChoices(dialogueLine.getQuizChoices());
            });
        }
        if (dialogueLine.getImageChanges() != null) {
            for (ImagePlacementData imagePlacementData : dialogueLine.getImageChanges()) {
                runOnUiThread(() -> showCharacter(imagePlacementData));
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

        StringProcessor.Placement.Value slotValue = labelValueHashMap.get(StringProcessor.Placement.Label.SLOT);
        StringProcessor.Placement.Value layerValue = labelValueHashMap.get(StringProcessor.Placement.Label.LAYER);

        if (slotValue == null || layerValue == null) return;
        CharacterImage characterImage;
        switch (slotValue) {
            case SLOT1:
                characterImage = characterSlot1;
                break;
            case SLOT2:
                characterImage = characterSlot2;
                break;
            case SLOT3:
                characterImage = characterSlot3;
                break;
            case SLOT4:
                characterImage = characterSlot4;
                break;
            case SLOT5:
                characterImage = characterSlot5;
                break;
            default:
                characterImage = null;
                break;
        }

        if (characterImage == null) return;

        Integer resource;
        switch (layerValue) {
            case BODY:
                if (!imagePlacementData.getToShow()) resource = null;
                else
                    resource = StoryResourceCaller.retrieveCharacterBodyResource(imagePlacementData.getImage());
                characterImage.setBody(resource);
                break;
            case FACE:
                if (!imagePlacementData.getToShow()) resource = null;
                else
                    resource = StoryResourceCaller.retrieveCharacterFaceResource(imagePlacementData.getImage());
                characterImage.setFace(resource);
                break;
            case PROP:
                if (!imagePlacementData.getToShow()) resource = null;
                else
                    resource = StoryResourceCaller.retrievePropResource(imagePlacementData.getImage());
                characterImage.setProps(resource);
                break;
            default:
                break;
        }
    }

    /**
     * <b>VISUAL FUNCTION</b>. Removes all characters on screen.
     */
    private void clearCharacters() {
        runOnUiThread(() -> {
            characterSlot1.clear();
            characterSlot2.clear();
            characterSlot3.clear();
            characterSlot4.clear();
            characterSlot5.clear();
        });
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
        backgroundHandler.post(() ->
                merkado.getPlayerActionTask().taskActivity(PlayerActions.Task.PlayerActivity.READING,
                        1,
                        PlayerActions.Task.generateRequirementCodeFromStory("LINE_GROUP")));
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

        backgroundHandler.post(() -> {
            LineGroup lineGroup = StoryDataFunctions.getLineGroupFromId(playerStory.getChapter().getId(), playerStory.getCurrentScene().getId(), nextLineGroupId);
            if (lineGroup == null) {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(),
                                "The story encountered a problem. Please try again later.",
                                Toast.LENGTH_SHORT).show());
                finish();
                return;
            }
            lineGroupIndex = lineGroup.getId();
            currentDialogueIndex = 0;
            if (!waitForNextScene_start || !waitForNextScene_end) {
                if (!waitForNextLineGroup_start) waitForNextLineGroup_start = false;
                if (!waitForNextLineGroup_end) waitForNextLineGroup_end = false;
                checkObjective();
            }
            if (!isHistory)
                StoryDataFunctions.changeNextLineGroup(lineGroup.getDefaultNextLine(), merkado.getPlayerId(), currentQueueIndex);
            nextLineGroupId = lineGroup.getDefaultNextLine();
            backgroundHandler.post(() -> initializeScreen(lineGroup));
        });
    }

    private void currentSceneEnded() {
        backgroundHandler.post(() ->
                merkado.getPlayerActionTask().taskActivity(PlayerActions.Task.PlayerActivity.READING,
                        1,
                        PlayerActions.Task.generateRequirementCodeFromStory("SCENE")));
        if (isHistory) {
            finish();
            return;
        }
        if (playerStory.getCurrentScene().getRewards() != null && !playerStory.getCurrentScene().getRewards().isEmpty())
            RewardProcessor.processRewards(
                    this,
                    merkado.getPlayer().getServer(),
                    merkado.getPlayerId(),
                    playerStory.getCurrentScene().getRewards());
        Chapter.Scene currentScene = playerStory.getNextScene();

        if (currentScene == null) {
            backgroundHandler.post(() ->
                    merkado.getPlayerActionTask().taskActivity(PlayerActions.Task.PlayerActivity.READING,
                            1,
                            PlayerActions.Task.generateRequirementCodeFromStory("CHAPTER")));
            checkObjective();
            finish();
            StoryDataFunctions.removeStoryQueueId(merkado.getPlayerId(), currentQueueIndex);
            if (trigger != null) TriggerProcessor.storyTrigger(merkado.getPlayerId(), trigger);
            return;
        }
        sceneIndex = currentScene.getId();
        lineGroupIndex = 0;
        currentDialogueIndex = 0;
        playBGM(currentScene.getBgm());
        if (!waitForNextChapter_start || !waitForNextChapter_end) {
            if (!waitForNextChapter_start) waitForNextScene_start = false;
            if (!waitForNextChapter_end) waitForNextScene_end = false;
            checkObjective();
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
        backgroundHandler.post(() -> {
            StoryDataFunctions.changeCurrentLineGroup(0, merkado.getPlayerId(), currentQueueIndex);
            LineGroup lineGroup = StoryDataFunctions.getLineGroupFromId(playerStory.getChapter().getId(), currentScene.getId(), 0);
            if (lineGroup == null) {
                Toast.makeText(getApplicationContext(), "The story encountered a problem. Please try again later.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            lineGroupIndex = lineGroup.getId();
            currentDialogueIndex = 0;

            StoryDataFunctions.changeNextLineGroup(lineGroup.getDefaultNextLine(), merkado.getPlayerId(), currentQueueIndex);
            nextLineGroupId = lineGroup.getDefaultNextLine();
            initializeScreen(lineGroup);
        });
    }

    private void setChoices(List<LineGroup.DialogueChoice> dialogueChoices) {
        Collections.shuffle(dialogueChoices);
        pauseAutoplay();
        disableClickArea();
        if (!dialogueChoices.isEmpty()) {
            choice1Text.setText(dialogueChoices.get(0).getChoice());
            choice1.setOnClickListener(v -> {
                movingClick(choice1, 1);
                continueFromChoices();
                if (dialogueChoices.get(0).getNextLineGroup() != null) {
                    nextLineGroupId = dialogueChoices.get(0).getNextLineGroup();
                }
            });
        } else {
            choice1Text.setVisibility(View.GONE);
            choice1.setVisibility(View.GONE);
        }
        if (dialogueChoices.size() > 1) {
            choice2Text.setText(dialogueChoices.get(1).getChoice());
            choice2.setOnClickListener(v -> {
                movingClick(choice2, 2);
                continueFromChoices();
                if (dialogueChoices.get(1).getNextLineGroup() != null) {
                    nextLineGroupId = dialogueChoices.get(1).getNextLineGroup();
                }
            });
        } else {
            choice2Text.setVisibility(View.GONE);
            choice2.setVisibility(View.GONE);
        }
        if (dialogueChoices.size() > 2) {
            choice3Text.setText(dialogueChoices.get(2).getChoice());
            choice3.setOnClickListener(v -> {
                movingClick(choice3, 3);
                continueFromChoices();
                if (dialogueChoices.get(2).getNextLineGroup() != null) {
                    nextLineGroupId = dialogueChoices.get(2).getNextLineGroup();
                }
            });
        } else {
            choice3Text.setVisibility(View.GONE);
            choice3.setVisibility(View.GONE);
        }
        if (dialogueChoices.size() > 3) {
            choice4Text.setText(dialogueChoices.get(3).getChoice());
            choice4.setOnClickListener(v -> {
                movingClick(choice4, 4);
                continueFromChoices();
                if (dialogueChoices.get(3).getNextLineGroup() != null) {
                    nextLineGroupId = dialogueChoices.get(3).getNextLineGroup();
                }
            });
        } else {
            choice4Text.setVisibility(View.GONE);
            choice4.setVisibility(View.GONE);
        }
    }

    private void continueFromChoices() {
        backgroundHandler.postDelayed(() ->
                runOnUiThread(() -> {
                    enableClickArea();
                    choiceGui.setVisibility(View.GONE);
                    continueDialogues();
                }), 400);
    }

    private void setQuizChoices(List<QuizChoice> quizChoices) {
        Collections.shuffle(quizChoices);
        pauseAutoplay();
        disableClickArea();
        choice1Text.setText(quizChoices.get(0).getChoice());
        choice1.setOnClickListener(v -> {
            movingClick(choice1, 1);
            quizScore += quizChoices.get(0).getPoints();
            runOnUiThread(() -> continueFromQuiz(quizChoices.get(0).getDialogueLine()));
        });
        choice2Text.setText(quizChoices.get(1).getChoice());
        choice2.setOnClickListener(v -> {
            movingClick(choice2, 2);
            quizScore += quizChoices.get(1).getPoints();
            runOnUiThread(() -> continueFromQuiz(quizChoices.get(1).getDialogueLine()));
        });
        choice3Text.setText(quizChoices.get(2).getChoice());
        choice3.setOnClickListener(v -> {
            movingClick(choice3, 3);
            quizScore += quizChoices.get(2).getPoints();
            runOnUiThread(() -> continueFromQuiz(quizChoices.get(2).getDialogueLine()));
        });
        choice4Text.setText(quizChoices.get(3).getChoice());
        choice4.setOnClickListener(v -> {
            movingClick(choice4, 4);
            quizScore += quizChoices.get(3).getPoints();
            runOnUiThread(() -> continueFromQuiz(quizChoices.get(3).getDialogueLine()));
        });
    }

    private void continueFromQuiz(@NonNull LineGroup.DialogueLine dialogueLine) {
        enableClickArea();
        backgroundHandler.post(() -> displayLine(dialogueLine));
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

    private void openQuizDisplay(Integer quizId, String background) {
        Intent intent = new Intent(this, QuizDisplay.class);
        intent.putExtra("QUIZ_ID", quizId);
        intent.putExtra("BACKGROUND", background);
        quizLauncher.launch(intent);
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
        backgroundHandler.postDelayed(() ->
                        runOnUiThread(() ->
                                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), finalIdle)))
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

    private void processQuizReward(Integer gameRewards) {
        RewardProcessor.processRewards(this,
                merkado.getPlayer().getServer(),
                merkado.getPlayerId(),
                Collections.singletonList(new GameRewards(1L, Long.valueOf(gameRewards))));
    }

    private void checkObjective() {
        if (!waitForNextChapter_start && !waitForNextScene_start && !waitForNextLineGroup_start
                && merkado.getPlayerData().getCurrentObjective() != null) {
            String startTrigger = merkado.getPlayerData().getCurrentObjective().getStartTrigger();
            List<Integer> startTriggers = splitTriggers(startTrigger);
            int startResult = processTriggers(startTriggers,
                    merkado.getPlayerData().getCurrentObjective(),
                    InAppNotification.NEW_OBJECTIVE);
            switch (startResult) {
                case -1:
                    waitForNextChapter_start = true;
                    break;
                case -2:
                    waitForNextScene_start = true;
                    break;
                case -3:
                    waitForNextLineGroup_start = true;
                    break;
            }
        }
        if (!waitForNextChapter_end && !waitForNextScene_end && !waitForNextLineGroup_end &&
                merkado.getPlayerData().getCurrentObjective() != null) {
            String endTrigger = merkado.getPlayerData().getCurrentObjective().getEndTrigger();
            List<Integer> endTriggers = splitTriggers(endTrigger);
            int endResult = processTriggers(endTriggers,
                    merkado.getPlayerData().getCurrentObjective(),
                    InAppNotification.DONE_OBJECTIVE);
            Log.d("DEBUG_NOTIFICATION", String.format(Locale.getDefault(), "[%d,%d,%d,%d] WITH END RESULT [%d]",
                    chapterIndex, sceneIndex, lineGroupIndex, currentDialogueIndex, endResult));
            switch (endResult) {
                case -1:
                    waitForNextChapter_end = true;
                    break;
                case -2:
                    waitForNextScene_end = true;
                    break;
                case -3:
                    waitForNextLineGroup_end = true;
                    break;
            }
        }
    }

    private int processTriggers(List<Integer> trigger, Objectives.Objective objective, String mode) {
        // Convert indexes
        int chapterIndex = Math.toIntExact(this.chapterIndex);
        int sceneIndex = Math.toIntExact(this.sceneIndex);

        // Compare chapter index
        if (trigger.get(0) < chapterIndex) return 0;
        if (trigger.get(0) > chapterIndex) return -1;

        // Compare scene index
        if (trigger.get(1) < sceneIndex) return 0;
        if (trigger.get(1) > sceneIndex) return -2;  // next scene

        // Compare line group index
        if (trigger.get(2) < lineGroupIndex) return 0;
        if (trigger.get(2) > lineGroupIndex) return -3;  // next line group

        // Compare dialogue index
        if (!trigger.get(3).equals(currentDialogueIndex)) return 0;

        // If all checks pass, send notification
        processSendNotification(objective.getObjective(), mode);
        return 0;  // success
    }

    private void processSendNotification(String objective, String mode) {
        inAppNotification.sendMessage(objective, mode);
        if (InAppNotification.DONE_OBJECTIVE.equals(mode) && merkado.getPlayerData().getCurrentObjective() != null) {
            if (merkado.getPlayerData().getObjectives().getObjectives().size() >
                    merkado.getPlayerData().getCurrentObjective().getId() + 1) {
                PlayerDataFunctions.setCurrentObjective(merkado.getPlayerId(), new PlayerObjectives(
                        merkado.getPlayerData().getObjectives().getId(),
                        merkado.getPlayerData().getCurrentObjective().getId() + 1,
                        false
                ));
                new Handler().postDelayed(this::checkObjective, 500);
            } else {
                PlayerDataFunctions.setCurrentObjective(merkado.getPlayerId(), new PlayerObjectives(
                        merkado.getPlayerData().getObjectives().getId(),
                        merkado.getPlayerData().getCurrentObjective().getId(),
                        true
                ));
                waitForNextChapter_start = true;
                waitForNextChapter_end = true;
                checkObjective();
            }
        }
    }

    private List<Integer> splitTriggers(String trigger) {
        String[] stringNumbers = trigger.split(",");
        List<Integer> numbers = new ArrayList<>();
        for (String num : stringNumbers) {
            numbers.add(Integer.parseInt(num));
        }
        return numbers;
    }

    private static class CharacterImage {
        Context context;
        ImageView face, body, props;

        public CharacterImage(Context context, FrameLayout layout) {
            face = layout.findViewById(R.id.face);
            body = layout.findViewById(R.id.body);
            props = layout.findViewById(R.id.props);
            this.context = context;
        }

        public void setFace(@Nullable Integer face) {
            if (face == null) clear();
            else
                this.face.setImageDrawable(face != -1 ? ContextCompat.getDrawable(context, face) : null);
        }

        public void setBody(@Nullable Integer body) {
            if (body == null) clear();
            else
                this.body.setImageDrawable(body != -1 ? ContextCompat.getDrawable(context, body) : null);
        }

        public void setProps(@Nullable Integer props) {
            if (props == null) clear();
            else
                this.props.setImageDrawable(props != -1 ? ContextCompat.getDrawable(context, props) : null);
        }

        public void clear() {
            this.face.setImageDrawable(null);
            this.body.setImageDrawable(null);
            this.props.setImageDrawable(null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        skipToggle.setStatus(ToggleStatus.IDLE);
        pauseAutoplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        backgroundHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}