package com.example.app2.ui;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.*;

import com.example.app2.R;
import com.example.app2.database.FirebaseHelper;
import com.example.app2.model.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    EditText input, searchBox;
    Spinner category;
    RecyclerView rv;

    FirebaseHelper firebase;
    TaskAdapter adapter;

    List<Task> list;
    List<Task> fullList;

    String[] categories = {"Work", "Personal", "Study"};

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);

        input = findViewById(R.id.inputText);
        searchBox = findViewById(R.id.searchBox);
        category = findViewById(R.id.categorySpinner);
        rv = findViewById(R.id.recyclerView);

        category.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, categories));

        firebase = new FirebaseHelper();

        list = new ArrayList<>();
        fullList = new ArrayList<>();

        adapter = new TaskAdapter(list, new TaskAdapter.OnActionListener() {
            public void onDelete(int position) {
                firebase.deleteTask(list.get(position).getFirebaseId());
            }

            public void onToggle(int position, int status) {
                firebase.toggleTask(
                        list.get(position).getFirebaseId(),
                        status == 1
                );
            }

            public void onEdit(Task t) {
                showEditDialog(t);
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // 🔓 Logout
        findViewById(R.id.logoutBtn).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // ➕ FAB
        findViewById(R.id.fabAdd).setOnClickListener(v -> {
            String text = input.getText().toString();

            if (text.isEmpty()) {
                Toast.makeText(this, "Enter task", Toast.LENGTH_SHORT).show();
                return;
            }

            showDateTimePicker(text);
        });

        // 🔥 REAL-TIME SYNC
        firebase.listenTasks(tasks -> {

            list.clear();
            fullList.clear();

            for (Map<String, Object> t : tasks) {

                String name = t.get("name").toString();
                String cat = t.get("category").toString();
                int completed = (boolean) t.get("completed") ? 1 : 0;
                String id = t.get("id").toString();
                String due = t.get("dueDate") != null ? t.get("dueDate").toString() : "";

                Task task = new Task(0, name, cat, completed, due);
                task.setFirebaseId(id);

                list.add(task);
                fullList.add(task);
            }

            adapter.notifyDataSetChanged();
        });

        // 🔍 SEARCH
        searchBox.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}

            public void onTextChanged(CharSequence s, int a, int b, int c) {
                filterTasks(s.toString());
            }

            public void afterTextChanged(Editable s) {}
        });
    }

    void filterTasks(String text) {
        list.clear();

        for (Task t : fullList) {
            if (t.getName().toLowerCase().contains(text.toLowerCase())) {
                list.add(t);
            }
        }

        adapter.notifyDataSetChanged();
    }

    void showEditDialog(Task t) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_task, null);

        EditText name = view.findViewById(R.id.editTaskName);
        Spinner cat = view.findViewById(R.id.editCategory);

        name.setText(t.getName());

        cat.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, categories));

        new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("Update", (d, w) -> {

                    firebase.updateTask(
                            t.getFirebaseId(),
                            name.getText().toString(),
                            cat.getSelectedItem().toString(),
                            t.getDueDate()
                    );

                    Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ⏰ DATE + TIME PICKER
    void showDateTimePicker(String taskName) {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, day) -> {

                    TimePickerDialog timePicker = new TimePickerDialog(this,
                            (timeView, hour, minute) -> {

                                String due = day + "/" + (month+1) + "/" + year +
                                        " " + hour + ":" + minute;

                                firebase.addTask(taskName,
                                        category.getSelectedItem().toString(),
                                        due);

                                sendNotification(taskName);

                            }, calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE), true);

                    timePicker.show();

                }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }

    void sendNotification(String task) {
        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String channelId = "task_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(channelId, "Tasks",
                            NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, channelId)
                        .setContentTitle("Task Added")
                        .setContentText(task)
                        .setSmallIcon(android.R.drawable.ic_dialog_info);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}