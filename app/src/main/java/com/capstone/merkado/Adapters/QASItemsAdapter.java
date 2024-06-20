package com.capstone.merkado.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.Objects.QASDataObjects.QASItems.QASDetail;
import com.capstone.merkado.R;

import java.util.List;

public class QASItemsAdapter extends RecyclerView.Adapter<QASItemsAdapter.QASItemsAdapterViewer> {
    Context context;
    List<QASDetail> qasDetailList;
    OnClickListener onClick;
    String qasGroup;

    public QASItemsAdapter(Context context, List<QASDetail> qasDetailList, String qasGroup) {
        this.context = context;
        this.qasDetailList = qasDetailList;
        this.qasGroup = qasGroup;
    }

    @NonNull
    @Override
    public QASItemsAdapterViewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gam_qas_quest_and_stories_list_item, parent, false);
        return new QASItemsAdapterViewer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QASItemsAdapterViewer holder, int position) {
        QASDetail qasDetail = qasDetailList.get(position);
        holder.bind(qasDetail, qasGroup, onClick);
    }

    @Override
    public int getItemCount() {
        return qasDetailList.size();
    }

    public static class QASItemsAdapterViewer extends RecyclerView.ViewHolder {
        View itemView;
        TextView qasDetailName, qasDetailShortDescription;

        public QASItemsAdapterViewer(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            qasDetailName = itemView.findViewById(R.id.qas_detail_name);
            qasDetailShortDescription = itemView.findViewById(R.id.qas_detail_short_description);
        }

        public void bind(QASDetail qasDetail, String qasGroup, OnClickListener onClick) {
            // set the data
            qasDetailName.setText(qasDetail.getQasName());
            qasDetailShortDescription.setText(qasDetail.getQasShortDescription());

            // set on click listener
            itemView.setOnClickListener(v -> onClick.onClick(qasDetail, qasGroup));
        }
    }

    public void setOnClickListener(OnClickListener onClick) {
        this.onClick = onClick;
    }

    public interface OnClickListener {
        void onClick(QASDetail qasDetail, String qasGroup);
    }
}
