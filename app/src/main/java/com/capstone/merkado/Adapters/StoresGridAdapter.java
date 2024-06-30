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
import com.capstone.merkado.Objects.StoresDataObjects.PlayerMarkets;
import com.capstone.merkado.R;

import java.util.List;

public class StoresGridAdapter extends RecyclerView.Adapter<StoresGridAdapter.ViewHolder> {

    List<PlayerMarkets> playerMarkets;
    Context context;
    OnClickListener onClickListener;

    // Constructor
    public StoresGridAdapter(Context context, List<PlayerMarkets> playerMarkets) {
        this.playerMarkets = playerMarkets;
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
        holder.bind(context, playerMarkets.get(position), position, onClickListener);
    }

    @Override
    public int getItemCount() {
        return playerMarkets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView storeIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storeIcon = itemView.findViewById(R.id.store_icon);
        }

        public void bind(Context context, PlayerMarkets playerMarkets, Integer marketId, OnClickListener onClickListener) {
            int storeIconResource = GameResourceCaller.getStoreIcons(playerMarkets.getStoreIcon());
            storeIcon.setImageDrawable(ContextCompat.getDrawable(context, storeIconResource));
            itemView.setOnClickListener(v -> onClickListener.onClick(playerMarkets, marketId));
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(PlayerMarkets playerMarkets, Integer marketId);
    }
}

