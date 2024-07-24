package com.capstone.merkado.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.Objects.ServerDataObjects.BasicServerData;
import com.capstone.merkado.R;

import java.util.List;
import java.util.Locale;

@SuppressLint("NotifyDataSetChanged")
public class EconomiesAdapter extends RecyclerView.Adapter<EconomiesAdapter.EconomiesAdapterViewer> {
    Context context;
    List<BasicServerData> basicServerDataList;
    OnClickListener onClick;

    public EconomiesAdapter(Context context, List<BasicServerData> basicServerDataList) {
        this.context = context;
        this.basicServerDataList = basicServerDataList;
    }

    @NonNull
    @Override
    public EconomiesAdapterViewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mai_lobby, parent, false);
        return new EconomiesAdapterViewer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EconomiesAdapterViewer holder, int position) {
        BasicServerData basicServerData = basicServerDataList.get(position);
        holder.bind(context, basicServerData, onClick);
    }

    @Override
    public int getItemCount() {
        return basicServerDataList.size();
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

        public void bind(Context context, BasicServerData basicServerData, OnClickListener onClick) {
            // set the data
            serverName.setText(basicServerData.getName());
            playersOnline.setText(
                    String.format(Locale.getDefault(), "%d player%s online",
                            basicServerData.getOnlinePlayersCount(),
                            basicServerData.getOnlinePlayersCount() <= 1 ? "" : "s"));
            if (basicServerData.getImage() == null) {
                serverImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_earth));
            }

            // set on click listener
            itemView.setOnClickListener(v -> {
                if (onClick != null) onClick.onClick(basicServerData);
            });
        }
    }

    public void updateList(List<BasicServerData> basicServerDataList) {
        this.basicServerDataList = basicServerDataList;
        notifyDataSetChanged();
    }

    public void setOnClickListener(OnClickListener onClick) {
        this.onClick = onClick;
    }

    public interface OnClickListener {
        void onClick(BasicServerData serverData);
    }
}
