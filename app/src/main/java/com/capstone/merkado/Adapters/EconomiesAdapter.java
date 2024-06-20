package com.capstone.merkado.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.Objects.ServerDataObjects.EconomyBasic;
import com.capstone.merkado.R;

import java.util.List;
import java.util.Locale;

public class EconomiesAdapter extends RecyclerView.Adapter<EconomiesAdapter.EconomiesAdapterViewer> {
    Context context;
    List<EconomyBasic> economyBasicList;
    OnClickListener onClick;

    public EconomiesAdapter(Context context, List<EconomyBasic> economyBasicList) {
        this.context = context;
        this.economyBasicList = economyBasicList;
    }

    @NonNull
    @Override
    public EconomiesAdapterViewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mai_lobby, parent, false);
        return new EconomiesAdapterViewer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EconomiesAdapterViewer holder, int position) {
        EconomyBasic economyBasic = economyBasicList.get(position);
        holder.bind(context, economyBasic, position, onClick);
    }

    @Override
    public int getItemCount() {
        return economyBasicList.size();
    }

    public static class EconomiesAdapterViewer extends RecyclerView.ViewHolder {
        TextView serverName, playersOnline;
        ImageView serverImage;
        View itemView;

        public EconomiesAdapterViewer(@NonNull View itemView) {
            super(itemView);
            serverName = itemView.findViewById(R.id.server_name);
            playersOnline = itemView.findViewById(R.id.players_online);
            serverImage = itemView.findViewById(R.id.server_image);
            this.itemView = itemView;
        }

        public void bind(Context context, EconomyBasic economyBasic, int position, OnClickListener onClick) {
            // set the data
            serverName.setText(economyBasic.getTitle());
            playersOnline.setText(String.format(Locale.getDefault(), "%d player%s online", economyBasic.getPlayersOnline(), economyBasic.getPlayersOnline() <= 1 ? "" : "s"));
            if (economyBasic.getImage() == null) {
                serverImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_earth));
            }

            // set on click listener
            itemView.setOnClickListener(v -> onClick.onClick(economyBasic.getTitle(), position, economyBasic.getPlayerId()));
        }
    }

    public void setOnClickListener(OnClickListener onClick) {
        this.onClick = onClick;
    }

    public interface OnClickListener {
        void onClick(String title, Integer id, Integer playerId);
    }
}
