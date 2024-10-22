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

import com.capstone.merkado.Objects.TaskDataObjects.PlayerTask;
import com.capstone.merkado.Objects.TaskDataObjects.TaskData;
import com.capstone.merkado.R;

import java.util.List;

public class QASTasksListAdapter extends RecyclerView.Adapter<QASTasksListAdapter.QASItemsAdapterViewer> {
    Context context;
    List<PlayerTask> playerTaskList;
    List<TaskData> taskDataList;
    OnCLickListener onCLickListener;

    public QASTasksListAdapter(@NonNull Context context,
                               @NonNull List<PlayerTask> playerTaskList,
                               @NonNull List<TaskData> taskDataList) {
        this.context = context;
        this.playerTaskList = playerTaskList;
        this.taskDataList = taskDataList;
    }

    @NonNull
    @Override
    public QASItemsAdapterViewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_qas_task_list, parent, false);
        return new QASItemsAdapterViewer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QASItemsAdapterViewer holder, int position) {
        PlayerTask playerTask = playerTaskList.get(position);
        holder.bind(context, playerTask, taskDataList, onCLickListener);
    }

    @Override
    public int getItemCount() {
        return playerTaskList.size();
    }

    public static class QASItemsAdapterViewer extends RecyclerView.ViewHolder {
        View itemView;
        ImageView icon;
        TextView taskTitle, taskShortDescription;

        public QASItemsAdapterViewer(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            icon = itemView.findViewById(R.id.icon);
            taskTitle = itemView.findViewById(R.id.task_title);
            taskShortDescription = itemView.findViewById(R.id.task_short_description);
        }

        public void bind(@NonNull Context context, @NonNull PlayerTask playerTask, @NonNull List<TaskData> taskDataList, OnCLickListener onCLickListener) {
            // set the data
            icon.setImageDrawable(ContextCompat.getDrawable(context,
                    playerTask.getDone() ?
                            R.drawable.icon_checkbox_checked :
                            R.drawable.icon_checkbox));
            TaskData task = taskDataList.stream()
                    .filter(taskData -> taskData.getTaskID().equals(playerTask.getTaskId()))
                    .findFirst()
                    .orElse(null);
            if (task==null) return;
            taskTitle.setText(task.getTitle());
            taskShortDescription.setText(task.getShortDescription());
            this.itemView.setOnClickListener(v -> {
                if (onCLickListener != null)
                    onCLickListener.onCLick(task, playerTask);
            });
        }
    }

    public void setOnCLickListener(OnCLickListener onCLickListener) {
        this.onCLickListener = onCLickListener;
    }

    public interface OnCLickListener {
        void onCLick(TaskData taskData, PlayerTask playerTask);
    }
}
