package com.capstone.merkado.Screens.Game.Sectors;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.CustomViews.WoodenButton;
import com.capstone.merkado.DataManager.DataFunctionPackage.FactoryDataFunctions;
import com.capstone.merkado.Objects.FactoryDataObjects.FactoryTypes;
import com.capstone.merkado.R;

public class ChooseSector extends AppCompatActivity {

    Merkado merkado;

    ConstraintLayout foodFactoryView, manufacturingFactoryView;
    TextView foodFactoryHeader, manufacturingFactoryHeader;
    WoodenButton foodFactoryConfirm, manufacturingFactoryConfirm;
    View foodFactoryDarken, manufacturingFactoryDarken;
    FactoryViews foodFactory, manufacturingFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gam_sec_choose_sector);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        foodFactoryView = findViewById(R.id.food_factory_view);
        foodFactoryHeader = findViewById(R.id.food_factory_header);
        foodFactoryConfirm = findViewById(R.id.food_factory_confirm);
        foodFactoryDarken = findViewById(R.id.food_factory_darken);
        manufacturingFactoryView = findViewById(R.id.manufacturing_factory_view);
        manufacturingFactoryHeader = findViewById(R.id.manufacturing_factory_header);
        manufacturingFactoryConfirm = findViewById(R.id.manufacturing_factory_confirm);
        manufacturingFactoryDarken = findViewById(R.id.manufacturing_factory_darken);

        foodFactory = new FactoryViews(
                foodFactoryView,
                foodFactoryHeader,
                foodFactoryConfirm,
                foodFactoryDarken);

        manufacturingFactory = new FactoryViews(
                manufacturingFactoryView,
                manufacturingFactoryHeader,
                manufacturingFactoryConfirm,
                manufacturingFactoryDarken);

        foodFactory.setEvents(new FactoryViews.Events() {
            @Override
            public void onConfirm() {
                FactoryDataFunctions.setFactoryType(
                        merkado.getPlayer().getServer(),
                        merkado.getPlayerId(),
                        merkado.getPlayerData().getPlayerFactory().getFactoryMarketId(),
                        merkado.getAccount().getUsername(),
                        FactoryTypes.FOOD
                );
                startActivity(new Intent(getApplicationContext(), Factory.class));
                finish();
            }

            @Override
            public void onNormal() {
                manufacturingFactory.normal();
            }

            @Override
            public void onChosen() {
                manufacturingFactory.notChosen();
            }
        });

        manufacturingFactory.setEvents(new FactoryViews.Events() {
            @Override
            public void onConfirm() {
                FactoryDataFunctions.setFactoryType(
                        merkado.getPlayer().getServer(),
                        merkado.getPlayerId(),
                        merkado.getPlayerData().getPlayerFactory().getFactoryMarketId(),
                        merkado.getAccount().getUsername(),
                        FactoryTypes.MANUFACTURING
                );
                startActivity(new Intent(getApplicationContext(), Factory.class));
                finish();
            }

            @Override
            public void onNormal() {
                foodFactory.normal();
            }

            @Override
            public void onChosen() {
                foodFactory.notChosen();
            }
        });
    }

    private static class FactoryViews {
        ConstraintLayout view;
        TextView header;
        WoodenButton confirm;
        View darken;
        Events events;
        States state;

        public FactoryViews(ConstraintLayout view, TextView header, WoodenButton confirm, View darken) {
            this.view = view;
            this.header = header;
            this.confirm = confirm;
            this.darken = darken;
            this.state = States.NORMAL;

            this.confirm.setOnClickListener(v -> {
                if (events != null) events.onConfirm();
            });
            this.view.setOnClickListener(v -> clicked());
        }

        public void setEvents(Events events) {
            this.events = events;
        }

        private void chosen() {
            changeWeights(this.view, 8);
            this.header.setVisibility(View.VISIBLE);
            this.confirm.setVisibility(View.VISIBLE);
            this.darken.setVisibility(View.GONE);
        }

        public void normal() {
            changeWeights(this.view, 5);
            this.header.setVisibility(View.VISIBLE);
            this.confirm.setVisibility(View.GONE);
            this.darken.setVisibility(View.GONE);
        }

        public void notChosen() {
            changeWeights(this.view, 2);
            this.header.setVisibility(View.GONE);
            this.confirm.setVisibility(View.GONE);
            this.darken.setVisibility(View.VISIBLE);
            this.state = States.NOT_CHOSEN;
        }

        private void clicked() {
            if (this.state == States.CHOSEN) {
                this.state = States.NORMAL;
                normal();
                if (events != null) events.onNormal();
            } else {
                this.state = States.CHOSEN;
                chosen();
                if (events != null) events.onChosen();
            }
        }

        private void changeWeights(View view, Integer weight) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            params.weight = weight;
            view.setLayoutParams(params);
        }

        public interface Events {
            void onConfirm();

            void onNormal();

            void onChosen();
        }

        public enum States {
            CHOSEN, NOT_CHOSEN, NORMAL
        }
    }
}