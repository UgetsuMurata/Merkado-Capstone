package com.capstone.merkado.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.DataManager.StaticData.GameResourceCaller;
import com.capstone.merkado.Objects.FactoryDataObjects.PlayerFactory;
import com.capstone.merkado.Objects.StoresDataObjects.PlayerMarkets;
import com.capstone.merkado.R;

import java.util.List;

public class FactoriesGridAdapter extends RecyclerView.Adapter<FactoriesGridAdapter.ViewHolder> {

    List<PlayerFactory> playerFactories;
    Context context;
    OnClickListener onClickListener;

    // Constructor
    public FactoriesGridAdapter(Context context, List<PlayerFactory> playerFactories) {
        this.playerFactories = playerFactories;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gam_sto_stores_stores_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(context, playerFactories.get(position), onClickListener);
    }

    @Override
    public int getItemCount() {
        return playerFactories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView factoryIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            factoryIcon = itemView.findViewById(R.id.store_icon);
        }

        public void bind(Context context, PlayerFactory playerFactory, OnClickListener onClickListener) {
            int factoryIconResource = GameResourceCaller.getStoreIcons(playerFactory.getFactoryIcon());
            factoryIcon.setImageDrawable(ContextCompat.getDrawable(context, factoryIconResource));
            itemView.setOnClickListener(v -> onClickListener.onClick(playerFactory, playerFactory.getFactoryId()));
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(PlayerFactory playerFactory, Integer factoryId);
    }
}

