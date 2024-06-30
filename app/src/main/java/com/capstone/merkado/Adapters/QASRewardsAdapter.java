package com.capstone.merkado.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.Objects.QASDataObjects.QASItems.QASDetail.QASReward;
import com.capstone.merkado.R;

import java.util.List;
import java.util.Locale;

public class QASRewardsAdapter extends RecyclerView.Adapter<QASRewardsAdapter.QASItemsAdapterViewer> {
    Context context;
    List<QASReward> qasRewards;

    public QASRewardsAdapter(Context context, List<QASReward> qasRewards) {
        this.context = context;
        this.qasRewards = qasRewards;
    }

    @NonNull
    @Override
    public QASItemsAdapterViewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gam_qas_quest_and_stories_rewards, parent, false);
        return new QASItemsAdapterViewer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QASItemsAdapterViewer holder, int position) {
        QASReward qasReward = qasRewards.get(position);
        holder.bind(context, qasReward);
    }

    @Override
    public int getItemCount() {
        return qasRewards.size();
    }

    public static class QASItemsAdapterViewer extends RecyclerView.ViewHolder {
        View itemView;
        TextView resourceQTY;
        ImageView resourceImage;

        public QASItemsAdapterViewer(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            resourceQTY = itemView.findViewById(R.id.resource_price);
            resourceImage = itemView.findViewById(R.id.resource_image);
        }

        public void bind(Context context, QASReward qasReward) {
            // set the data
            resourceQTY.setText(String.format(Locale.getDefault(), "%d", qasReward.getResourceQuantity()));
            resourceImage.setImageDrawable(ContextCompat.getDrawable(context, qasReward.getResourceImage()));
        }
    }
}
