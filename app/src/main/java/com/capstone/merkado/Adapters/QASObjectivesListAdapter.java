package com.capstone.merkado.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.merkado.Objects.ServerDataObjects.Objectives.Objective;
import com.capstone.merkado.R;

import java.util.List;

public class QASObjectivesListAdapter extends RecyclerView.Adapter<QASObjectivesListAdapter.QASItemsAdapterViewer> {
    Context context;
    List<Objective> qasObjectiveList;
    Integer currentObjective;
    Boolean done;

    public QASObjectivesListAdapter(@NonNull Context context,
                                    @NonNull List<Objective> qasObjectiveList,
                                    @Nullable Integer currentObjective,
                                    @NonNull Boolean done) {
        this.context = context;
        this.qasObjectiveList = qasObjectiveList;
        this.currentObjective = currentObjective;
        this.done = done;
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
        holder.bind(qasObjective, currentObjective, done);
    }

    @Override
    public int getItemCount() {
        return qasObjectiveList.size();
    }

    public static class QASItemsAdapterViewer extends RecyclerView.ViewHolder {
        TextView objective;

        public QASItemsAdapterViewer(@NonNull View itemView) {
            super(itemView);
            objective = itemView.findViewById(R.id.objective);
        }

        public void bind(@NonNull Objective qasObjective, @Nullable Integer currentObjective, @NonNull Boolean done) {
            // set the data
            objective.setCompoundDrawablesWithIntrinsicBounds(
                    currentObjective == null ?
                            (done ? R.drawable.icon_checkbox_checked : R.drawable.icon_checkbox) :
                            (currentObjective > qasObjective.getId() || done ?
                                    R.drawable.icon_checkbox_checked : R.drawable.icon_checkbox),
                    0, 0, 0);
            objective.setText(qasObjective.getObjective());
        }
    }
}
