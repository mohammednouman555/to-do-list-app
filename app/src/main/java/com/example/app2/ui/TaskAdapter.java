package com.example.app2.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app2.R;
import com.example.app2.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    List<Task> list;
    OnActionListener listener;

    public interface OnActionListener {
        void onDelete(int position);
        void onToggle(int position, int status);
        void onEdit(Task task);
    }

    public TaskAdapter(List<Task> list, OnActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView taskText;
        TextView categoryText;
        TextView priorityText;
        TextView dueDateText;
        CheckBox checkBox;

        public ViewHolder(View v) {
            super(v);

            taskText = v.findViewById(R.id.taskText);
            categoryText = v.findViewById(R.id.categoryText);
            priorityText = v.findViewById(R.id.priorityText);
            dueDateText = v.findViewById(R.id.dueDateText);
            checkBox = v.findViewById(R.id.checkBox);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_task, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {

        Task t = list.get(position);

        h.taskText.setText(t.getName());
        h.categoryText.setText("Category: " + t.getCategory());

        h.dueDateText.setText("⏰ Due: " + t.getDueDate());

        // TEMPORARY until Task.java gets priority field
        h.priorityText.setText("Priority: " + t.getPriority());

        if ("High".equals(t.getPriority())) {
            h.priorityText.setTextColor(Color.RED);
        }
        else if ("Medium".equals(t.getPriority())) {
            h.priorityText.setTextColor(Color.YELLOW);
        }
        else {
            h.priorityText.setTextColor(Color.GREEN);
        }

        h.checkBox.setChecked(t.isCompleted() == 1);

        if (t.isCompleted() == 1) {
            h.taskText.setPaintFlags(
                    h.taskText.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG
            );
        } else {
            h.taskText.setPaintFlags(0);
        }

        h.checkBox.setOnClickListener(v -> {
            int pos = h.getAdapterPosition();

            if (pos != RecyclerView.NO_POSITION) {
                listener.onToggle(
                        pos,
                        h.checkBox.isChecked() ? 1 : 0
                );
            }
        });

        h.itemView.setOnClickListener(v -> {
            int pos = h.getAdapterPosition();

            if (pos != RecyclerView.NO_POSITION) {
                listener.onEdit(list.get(pos));
            }
        });

        h.itemView.setOnLongClickListener(v -> {
            int pos = h.getAdapterPosition();

            if (pos != RecyclerView.NO_POSITION) {
                listener.onDelete(pos);
            }

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}