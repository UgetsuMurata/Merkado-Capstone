package com.capstone.merkado.Screens.Game.Story;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.merkado.Helpers.JsonHelper;
import com.capstone.merkado.Objects.StoryDataObjects.Quiz;
import com.capstone.merkado.Objects.StoryDataObjects.Test;
import com.capstone.merkado.R;

import java.util.List;

public class PrePostTest extends AppCompatActivity {

    private ImageView questionImage;
    private TextView questionText;
    private ImageView choice1Image, choice2Image, choice3Image, choice4Image;
    private TextView choice1Text, choice2Text, choice3Text, choice4Text;

    Integer score, currentItem, maxItem;

    private Test test;  // Assume Test is a model class for your quiz data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_post_test);

        // Initialize views
        questionImage = findViewById(R.id.question_image);
        questionText = findViewById(R.id.question_text);
        choice1Image = findViewById(R.id.choice_1_image);
        choice2Image = findViewById(R.id.choice_2_image);
        choice3Image = findViewById(R.id.choice_3_image);
        choice4Image = findViewById(R.id.choice_4_image);
        choice1Text = findViewById(R.id.choice_1_text);
        choice2Text = findViewById(R.id.choice_2_text);
        choice3Text = findViewById(R.id.choice_3_text);
        choice4Text = findViewById(R.id.choice_4_text);

//        // Load quiz data
//        loadQuizData();
    }

//    private void loadQuizData() {
//        // Assuming you have a JsonHelper class to parse the JSON
//        JsonHelper.getDiagnosticTool(getApplicationContext(), this::setUp);
//    }
//
//    private void setUp(Test test) {
//        this.score = 0;
//        this.currentItem = 0;
//        this.maxItem = test.getItems().size();
//        this.test = test;
//    }
//
//    private void displayQuestion(Test.Item item) {
//        // Load the question image from the drawable if it exists
//        if (item.getQuestionImage() != null) {
//            int imageResId = getResources().getIdentifier(item.getQuestionImage(), "drawable", getPackageName());
//            if (imageResId != 0) {
//                questionImage.setVisibility(View.VISIBLE);
//                questionImage.setImageResource(imageResId);
//            } else {
//                questionImage.setVisibility(View.GONE);
//            }
//        } else {
//            questionImage.setVisibility(View.GONE);
//        }
//
//        // Set question text
//        questionText.setText(item.getQuestion());
//
//        // Hide all choice views initially
//        choice1Image.setVisibility(View.GONE);
//        choice1Text.setVisibility(View.GONE);
//        choice2Image.setVisibility(View.GONE);
//        choice2Text.setVisibility(View.GONE);
//        choice3Image.setVisibility(View.GONE);
//        choice3Text.setVisibility(View.GONE);
//        choice4Image.setVisibility(View.GONE);
//        choice4Text.setVisibility(View.GONE);
//
//        // Display each choice
//        List<Test.Item.Choice> choices = item.getChoices();
//        for (int i = 0; i < choices.size(); i++) {
//            Test.Item.Choice choice = choices.get(i);
//            int choiceImageResId = getResources().getIdentifier(choice.getImage(), "drawable", getPackageName());
//
//            switch (i) {
//                case 0:
//                    choice1Image.setVisibility(View.VISIBLE);
//                    choice1Text.setVisibility(View.VISIBLE);
//                    choice1Text.setText(choice.getChoice());
//                    if (choiceImageResId != 0) {
//                        choice1Image.setImageResource(choiceImageResId);
//                    }
//                    choice1Text.setOnClickListener(v -> handleChoiceSelection(0, item));
//                    break;
//                case 1:
//                    choice2Image.setVisibility(View.VISIBLE);
//                    choice2Text.setVisibility(View.VISIBLE);
//                    choice2Text.setText(choice.getChoice());
//                    if (choiceImageResId != 0) {
//                        choice2Image.setImageResource(choiceImageResId);
//                    }
//                    choice2Text.setOnClickListener(v -> handleChoiceSelection(1, item));
//                    break;
//                case 2:
//                    choice3Image.setVisibility(View.VISIBLE);
//                    choice3Text.setVisibility(View.VISIBLE);
//                    choice3Text.setText(choice.getChoice());
//                    if (choiceImageResId != 0) {
//                        choice3Image.setImageResource(choiceImageResId);
//                    }
//                    choice3Text.setOnClickListener(v -> handleChoiceSelection(2, item));
//                    break;
//                case 3:
//                    choice4Image.setVisibility(View.VISIBLE);
//                    choice4Text.setVisibility(View.VISIBLE);
//                    choice4Text.setText(choice.getChoice());
//                    if (choiceImageResId != 0) {
//                        choice4Image.setImageResource(choiceImageResId);
//                    }
//                    choice4Text.setOnClickListener(v -> handleChoiceSelection(3, item));
//                    break;
//            }
//        }
//    }
//
//    private void handleChoiceSelection(int selectedChoiceIndex, Test.Item item) {
//        // Check if the selected choice is correct
//        if (item.getCorrectChoices().contains(selectedChoiceIndex)) {
//            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Wrong answer. Try again!", Toast.LENGTH_SHORT).show();
//        }
//
//        // Load the next question or perform any other action
//        // For example: displayQuestion(test.getItems().get(nextQuestionIndex));
//    }
}