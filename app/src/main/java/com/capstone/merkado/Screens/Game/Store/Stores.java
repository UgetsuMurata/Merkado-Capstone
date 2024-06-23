package com.capstone.merkado.Screens.Game.Store;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.capstone.merkado.Adapters.StoreNameAdapter;
import com.capstone.merkado.Adapters.StoresItemsAdapter;
import com.capstone.merkado.Application.Merkado;
import com.capstone.merkado.Objects.StoresDataObjects.StoreItems;
import com.capstone.merkado.Objects.StoresDataObjects.StoreName;
import com.capstone.merkado.R;

import java.util.ArrayList;
import java.util.List;

public class Stores extends AppCompatActivity {

    Merkado merkado;
    private RecyclerView recyclerView, recyclerview1;
    private TextView emptyView;
    private StoresItemsAdapter adapter;
    private StoreNameAdapter storeNameAdapter;
    private List<StoreItems> itemList;
    private List<StoreName> storeNameList;
    private TextView storename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gam_sto_stores);

        merkado = Merkado.getInstance();
        merkado.initializeScreen(this);

        recyclerView = findViewById(R.id.storelist);
        recyclerview1 = findViewById(R.id.store_name_list);
        emptyView = findViewById(R.id.store_list_empty);
        storename = findViewById(R.id.store_name);

        // Set up the RecyclerView with a GridLayoutManager
        int numberOfColumns = 5; // Maintain 5 columns
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerview1.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        //Mockup data only.
        // Initialize your data
        itemList = new ArrayList<>();
        // Add items to the itemList.
        itemList.add(new StoreItems(R.drawable.icon_store));
        itemList.add(new StoreItems(R.drawable.icon_store));
        itemList.add(new StoreItems(R.drawable.icon_store));
        itemList.add(new StoreItems(R.drawable.icon_store));
        itemList.add(new StoreItems(R.drawable.icon_store));
        itemList.add(new StoreItems(R.drawable.icon_store));
        itemList.add(new StoreItems(R.drawable.icon_store));
        itemList.add(new StoreItems(R.drawable.icon_store));
        itemList.add(new StoreItems(R.drawable.icon_store));
        itemList.add(new StoreItems(R.drawable.icon_store));
        itemList.add(new StoreItems(R.drawable.icon_store));
        itemList.add(new StoreItems(R.drawable.icon_store));
        itemList.add(new StoreItems(R.drawable.icon_store));
        itemList.add(new StoreItems(R.drawable.icon_store));
        itemList.add(new StoreItems(R.drawable.icon_store));
        itemList.add(new StoreItems(R.drawable.icon_store));
        itemList.add(new StoreItems(R.drawable.icon_store));
        itemList.add(new StoreItems(R.drawable.icon_store));
        itemList.add(new StoreItems(R.drawable.icon_store));
        itemList.add(new StoreItems(R.drawable.icon_store));
        itemList.add(new StoreItems(R.drawable.icon_store));
        itemList.add(new StoreItems(R.drawable.icon_store));

        storeNameList = new ArrayList<>();
        storeNameList.add(new StoreName(R.drawable.icon_store, "P2000"));
        storeNameList.add(new StoreName(R.drawable.icon_store, "P2000"));
        storeNameList.add(new StoreName(R.drawable.icon_store, "P2000"));
        storeNameList.add(new StoreName(R.drawable.icon_store, "P2000"));
        storeNameList.add(new StoreName(R.drawable.icon_store, "P2000"));
        storeNameList.add(new StoreName(R.drawable.icon_store, "P2000"));
        storeNameList.add(new StoreName(R.drawable.icon_store, "P2000"));
        storeNameList.add(new StoreName(R.drawable.icon_store, "P2000"));
        storeNameList.add(new StoreName(R.drawable.icon_store, "P2000"));
        storeNameList.add(new StoreName(R.drawable.icon_store, "P2000"));
        storeNameList.add(new StoreName(R.drawable.icon_store, "P2000"));
        storeNameList.add(new StoreName(R.drawable.icon_store, "P2000"));
        storeNameList.add(new StoreName(R.drawable.icon_store, "P2000"));
        storeNameList.add(new StoreName(R.drawable.icon_store, "P2000"));
        storeNameList.add(new StoreName(R.drawable.icon_store, "P2000"));
        storeNameList.add(new StoreName(R.drawable.icon_store, "P2000"));
        storeNameList.add(new StoreName(R.drawable.icon_store, "P2000"));
        storeNameList.add(new StoreName(R.drawable.icon_store, "P2000"));
        storeNameList.add(new StoreName(R.drawable.icon_store, "P2000"));
        storeNameList.add(new StoreName(R.drawable.icon_store, "P2000"));

        // Add more items as needed

        adapter = new StoresItemsAdapter(itemList);
        storeNameAdapter = new StoreNameAdapter(storeNameList, this);
        recyclerView.setAdapter(adapter);
        recyclerview1.setAdapter(storeNameAdapter);

        changeStoreName("New Store Name");

        checkIfEmpty();
    }

    private void checkIfEmpty() {
        if (adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void changeStoreName(String newName) {
        storename.setText(newName);
    }

}