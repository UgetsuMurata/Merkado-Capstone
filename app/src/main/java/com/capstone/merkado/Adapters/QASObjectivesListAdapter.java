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

import com.capstone.merkado.Objects.ServerDataObjects.Objectives.Objective;
import com.capstone.merkado.R;

import java.util.List;

public class QASObjectivesListAdapter extends RecyclerView.Adapter<QASObjectivesListAdapter.QASItemsAdapterViewer> {
    Context context;
    List<Objective> qasObjectiveList;
    Integer currentObjective;

    public QASObjectivesListAdapter(@NonNull Context context,
                                    @NonNull List<Objective> qasObjectiveList,
                                    @NonNull Integer currentObjective) {
        this.context = context;
        this.qasObjectiveList = qasObjectiveList;
        this.currentObjective = currentObjective;
    }

    @NonNull
    @Override
    public QASItemsAdapterViewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_qas_objective_list, parent, false);
        return new QASItemsAdapterViewer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QASItemsAdapterViewer holder, int position) {
        Objective qasObjective = qasObjectiveList.get(position);
        holder.bind(context, qasObjective, currentObjective);
    }

    @Override
    public int getItemCount() {
        return qasObjectiveList.size();
    }

    public static class QASItemsAdapterViewer extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView objective;

        public QASItemsAdapterViewer(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            objective = itemView.findViewById(R.id.objective);
        }

        public void bind(@NonNull Context context, @NonNull Objective qasObjective, @NonNull Integer currentObjective) {
            // set the data
            icon.setImageDrawable(ContextCompat.getDrawable(context,
                    currentObjective > qasObjective.getId() ?
                            R.drawable.icon_checkbox_checked :
                            R.drawable.icon_checkbox));
            objective.setText(qasObjective.getObjective());
        }
    }
}
