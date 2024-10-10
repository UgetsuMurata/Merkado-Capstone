package com.capstone.merkado.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.Objects.QASDataObjects.QASItems;
import com.capstone.merkado.Objects.QASDataObjects.QASItems.QASDetail;
import com.capstone.merkado.R;

import java.util.List;

public class QASAdapter extends RecyclerView.Adapter<QASAdapter.QASAdapterViewer> {
    Context context;
    List<QASItems> qasItemsList;
    OnClickListener onClick;

    public QASAdapter(Context context, List<QASItems> qasItemsList) {
        this.context = context;
        this.qasItemsList = qasItemsList;
    }

    @NonNull
    @Override
    public QASAdapterViewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gam_qas_quest_and_stories_list, parent, false);
        return new QASAdapterViewer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QASAdapterViewer holder, int position) {
        QASItems qasItems = qasItemsList.get(position);
        holder.bind(context, qasItems, onClick);
    }

    @Override
    public int getItemCount() {
        return qasItemsList.size();
    }

    public static class QASAdapterViewer extends RecyclerView.ViewHolder {
        TextView qasCategory;
        RecyclerView qasListIndividual;

        public QASAdapterViewer(@NonNull View itemView) {
            super(itemView);
            qasCategory = itemView.findViewById(R.id.qas_category);
            qasListIndividual = itemView.findViewById(R.id.qas_list_individual);
        }

        public void bind(Context context, QASItems qasItems, OnClickListener onClick) {
            boolean history = qasItems.getQasCategory().contains("[HISTORY]");
            if (history) {
                qasCategory.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.gray));
                qasCategory.setText(qasItems.getQasCategory().substring(9));
            } else {
                qasCategory.setText(qasItems.getQasCategory());
            }

            QASItemsAdapter qasItemsAdapter = new QASItemsAdapter(context, qasItems.getQasDetails(), qasItems.getQasGroup());
            qasListIndividual.setLayoutManager(new LinearLayoutManager(context));
            qasListIndividual.setAdapter(qasItemsAdapter);
            qasItemsAdapter.setOnClickListener((qasDetail, qasGroup) -> onClick.onClick(qasDetail, qasGroup, history));
        }
    }

    public void setOnClickListener(OnClickListener onClick) {
        this.onClick = onClick;
    }

    public interface OnClickListener {
        void onClick(QASDetail qasDetail, String qasGroup, Boolean history);
    }
}
