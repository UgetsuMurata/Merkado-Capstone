package com.capstone.merkado.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.Objects.StoresDataObjects.StoreName;
import com.capstone.merkado.R;

import java.util.List;

public class StoreNameAdapter extends RecyclerView.Adapter<StoreNameAdapter.ViewHolder> {

    private List<StoreName> storeNames;
    private Context context;

    public StoreNameAdapter(List<StoreName> storeNames, Context context) {
        this.storeNames = storeNames;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game_sto_stores, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StoreName storeName = storeNames.get(position);
        holder.resourceQuantity.setText(storeName.getQuantity());
        holder.resourceImage.setImageResource(storeName.getImageUrl());
    }

    @Override
    public int getItemCount() {
        return storeNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView resourceImage;
        public TextView resourceQuantity;

        public ViewHolder(View itemView) {
            super(itemView);
            resourceImage = itemView.findViewById(R.id.resource_image);
            resourceQuantity = itemView.findViewById(R.id.resource_quantity);
        }
    }
}

