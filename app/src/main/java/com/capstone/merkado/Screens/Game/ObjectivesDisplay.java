package com.capstone.merkado.Screens.Game;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.capstone.merkado.Objects.ServerDataObjects.Objectives;
import com.capstone.merkado.R;

import java.util.List;

public class ObjectivesDisplay extends AppCompatActivity implements View.OnClickListener {

    ConstraintLayout root;
    TextView milestoneTitle, milestoneSubtitle, milestoneObjectives;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gam_objectives_display);

        root = findViewById(R.id.objectives_display_root);
        milestoneTitle = findViewById(R.id.milestone_title);
        milestoneSubtitle = findViewById(R.id.milestone_subtitle);
        milestoneObjectives = findViewById(R.id.milestone_objectives);

        Objectives objectives = getIntent().getParcelableExtra("OBJECTIVE");
        if (objectives == null) {
            root.performClick();
            return;
        }
        setUp(objectives);
        root.setOnClickListener(this);
    }

    private void setUp(Objectives objectives) {
        milestoneTitle.setText(objectives.getTitle());
        milestoneSubtitle.setText(objectives.getSubtitle());
        milestoneObjectives.setText(listObjectives(objectives.getObjectives()));
    }

    private String listObjectives(List<String> objectivesList) {
        StringBuilder finalString = new StringBuilder();

        for (String objective : objectivesList) {
            finalString.append("▢ ");
            finalString.append(objective);
            finalString.append("\n");
        }

        return finalString.toString();
    }

    @Override
    public void onClick(View v) {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}