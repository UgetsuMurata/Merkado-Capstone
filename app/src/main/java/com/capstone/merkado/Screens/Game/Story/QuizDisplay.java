package com.capstone.merkado.Screens.Game.Story;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.capstone.merkado.DataManager.StaticData.StoryResourceCaller;
import com.capstone.merkado.Helpers.JsonHelper;
import com.capstone.merkado.Objects.StoryDataObjects.Quiz;
import com.capstone.merkado.R;
import com.google.android.material.card.MaterialCardView;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizDisplay extends AppCompatActivity {

    ProgressBar progressTracker;
    TextView itemNumber, question;
    ChoiceModifier choice1, choice2, choice3, choice4;
    ConstraintLayout root;

    Integer score, currentItem, maxItem;
    Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gam_sto_quiz);

        root = findViewById(R.id.quiz_root);
        progressTracker = findViewById(R.id.progress);
        itemNumber = findViewById(R.id.item_number);
        question = findViewById(R.id.question);

        choice1 = new ChoiceModifier(this, findViewById(R.id.choice_1), findViewById(R.id.choice_1_bg));
        choice2 = new ChoiceModifier(this, findViewById(R.id.choice_2), findViewById(R.id.choice_2_bg));
        choice3 = new ChoiceModifier(this, findViewById(R.id.choice_3), findViewById(R.id.choice_3_bg));
        choice4 = new ChoiceModifier(this, findViewById(R.id.choice_4), findViewById(R.id.choice_4_bg));

        int id = getIntent().getIntExtra("QUIZ_ID", -1);
        if (id == -1) {
            finish();
            Toast.makeText(getApplicationContext(),
                    "Error occurred when retrieving quiz.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String bg = getIntent().getStringExtra("BACKGROUND");
        if (bg != null && bg.isEmpty()) {
            int bgRes = StoryResourceCaller.retrieveBackgroundResource(bg);
            root.setBackground(ContextCompat.getDrawable(getApplicationContext(), bgRes));
        }

        getQuestions(id);
    }

    private void getQuestions(Integer id) {
        JsonHelper.getQuizList(getApplicationContext(), quizzes -> setUp(quizzes.get(id)));
    }

    private void setUp(Quiz quiz) {
        this.score = 0;
        this.currentItem = 0;
        this.maxItem = quiz.getItems().size();
        this.quiz = quiz;

        progressTracker.setMax(maxItem);
        progressTracker.setProgress(this.currentItem);

        // setup choice clicks
        choice1.setOnClickListener(status -> {
            if (status == ChoiceModifier.Status.CORRECT) score++;
            nextItem();
        });
        choice2.setOnClickListener(status -> {
            if (status == ChoiceModifier.Status.CORRECT) score++;
            nextItem();
        });
        choice3.setOnClickListener(status -> {
            if (status == ChoiceModifier.Status.CORRECT) score++;
            nextItem();
        });
        choice4.setOnClickListener(status -> {
            if (status == ChoiceModifier.Status.CORRECT) score++;
            nextItem();
        });

        // start quiz
        nextItem();
    }

    private void nextItem() {
        if (this.currentItem >= this.maxItem) {
            setResult(Activity.RESULT_OK, new Intent()
                    .putExtra("SCORE", score)
                    .putExtra("QUIZ_ID", quiz.getId()));
            finish();
            return;
        }
        this.currentItem++;
        progressTracker.setProgress(this.currentItem);
        itemNumber.setText(String.format(Locale.getDefault(), "%d.", this.currentItem));
        displayItem(this.quiz.getItems().get(this.currentItem - 1));
    }

    private void displayItem(Quiz.Item item) {
        question.setText(item.getQuestion());
        List<Quiz.Item.Choice> choiceList = item.getChoices();
        Collections.shuffle(choiceList);
        choice1.setChoice(choiceList.get(0), item.getCorrectChoices().contains(choiceList.get(0).getId()));
        choice2.setChoice(choiceList.get(1), item.getCorrectChoices().contains(choiceList.get(0).getId()));
        choice3.setChoice(choiceList.get(2), item.getCorrectChoices().contains(choiceList.get(0).getId()));
        choice4.setChoice(choiceList.get(3), item.getCorrectChoices().contains(choiceList.get(0).getId()));
    }

    public static class ChoiceModifier {
        Handler handler;
        StatusReturn statusReturn;
        Boolean correct;

        Activity activity;
        TextView choiceString;
        MaterialCardView choiceBG;
        Quiz.Item.Choice choice;

        public ChoiceModifier(Activity activity, TextView choiceString, MaterialCardView choiceBG) {
            this.activity = activity;
            this.choiceString = choiceString;
            this.choiceBG = choiceBG;
            this.correct = true;

            this.handler = new Handler();

            this.choiceBG.setOnClickListener(v -> {
                if (correct) correct();
                else wrong();
            });
        }

        public void setChoice(Quiz.Item.Choice choice, Boolean correct) {
            this.choice = choice;
            this.correct = correct;
            this.choiceString.setText(choice.getChoice());
        }

        private void correct() {
            choiceBG.setBackgroundTintList(ContextCompat.getColorStateList(activity.getApplicationContext(),
                    android.R.color.holo_green_light));
            handler.postDelayed(() -> {
                        activity.runOnUiThread(() ->
                                choiceBG.setBackgroundTintList(ContextCompat.getColorStateList(activity.getApplicationContext(),
                                        android.R.color.white)));
                        statusReturn.statusReturn(Status.CORRECT);
                    }, 500);
        }

        private void wrong() {
            choiceBG.setBackgroundTintList(ContextCompat.getColorStateList(activity.getApplicationContext(),
                    android.R.color.holo_red_light));
            handler.postDelayed(() -> {
                        activity.runOnUiThread(() ->
                                choiceBG.setBackgroundTintList(ContextCompat.getColorStateList(activity.getApplicationContext(),
                                        android.R.color.white)));
                        statusReturn.statusReturn(Status.WRONG);
                    }, 500);
        }

        public void setOnClickListener(StatusReturn statusReturn) {
            this.statusReturn = statusReturn;
        }

        public interface StatusReturn {
            void statusReturn(Status status);
        }

        public enum Status {
            CORRECT, WRONG
        }
    }
}