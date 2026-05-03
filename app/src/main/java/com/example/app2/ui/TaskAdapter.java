package com.example.app2.ui;

import android.graphics.Paint;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app2.R;
import com.example.app2.model.Task;

import java.util.*;

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
        TextView taskText, categoryText, dueDateText;
        CheckBox checkBox;

        public ViewHolder(View v) {
            super(v);
            taskText = v.findViewById(R.id.taskText);
            categoryText = v.findViewById(R.id.categoryText);
            dueDateText = v.findViewById(R.id.dueDateText);
            checkBox = v.findViewById(R.id.checkBox);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int i) {
        Task t = list.get(i);

        h.taskText.setText(t.getName());
        h.categoryText.setText(t.getCategory());
        h.dueDateText.setText("Due: " + t.getDueDate());

        h.checkBox.setChecked(t.isCompleted() == 1);

        if (t.isCompleted() == 1) {
            h.taskText.setPaintFlags(h.taskText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            h.taskText.setPaintFlags(0);
        }

        h.checkBox.setOnClickListener(v -> {
            int pos = h.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onToggle(pos, h.checkBox.isChecked() ? 1 : 0);
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int viewType) {
        return new ViewHolder(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_task, p, false));
    }
}