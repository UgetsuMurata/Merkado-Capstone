package com.capstone.merkado.Screens.Game.StoreView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.capstone.merkado.Adapters.StoreViewConsumerAdapter;
import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.Objects.StoreViewObjects.StoreViewConsumerObject;
import com.capstone.merkado.R;

import java.util.ArrayList;
import java.util.List;

public class StoreViewConsumer extends AppCompatActivity {

    Merkado merkado;
    private RecyclerView recyclerView;
    private StoreViewConsumerAdapter adapter;
    private List<StoreViewConsumerObject> storeViewConsumerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_view_consumer);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        recyclerView = findViewById(R.id.item_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        storeViewConsumerList = new ArrayList<>();
        storeViewConsumerList.add(new StoreViewConsumerObject(R.drawable.icon_inventory, "This is a long title that you cannot imagine", "P200.00", 10000000));
        storeViewConsumerList.add(new StoreViewConsumerObject(R.drawable.icon_inventory, "Task 2", "P200.00", 200));

        adapter = new StoreViewConsumerAdapter(this, storeViewConsumerList);
        recyclerView.setAdapter(adapter);
    }
}