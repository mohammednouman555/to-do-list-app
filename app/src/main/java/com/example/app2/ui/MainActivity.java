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
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.*;
import android.os.Environment;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import com.example.app2.R;
import com.example.app2.database.FirebaseHelper;
import com.example.app2.model.Task;
import com.google.firebase.auth.FirebaseAuth;

import android.app.AlarmManager;
import android.app.PendingIntent;

import android.net.Uri;
import android.content.ActivityNotFoundException;

import java.io.OutputStream;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    private static final int CREATE_PDF_REQUEST = 1001;
    EditText input, searchBox;
    TextView statsText;
    ProgressBar progressBar;
    Spinner category;
    Spinner prioritySpinner;
    RecyclerView rv;

    FirebaseHelper firebase;
    TaskAdapter adapter;

    List<Task> list;
    List<Task> fullList;

    String[] categories = {"Work", "Personal", "Study"};
    String[] priorities = {"High", "Medium", "Low"};

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);

        input = findViewById(R.id.inputText);
        statsText = findViewById(R.id.statsText);
        progressBar = findViewById(R.id.progressBar);
        searchBox = findViewById(R.id.searchBox);
        category = findViewById(R.id.categorySpinner);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        rv = findViewById(R.id.recyclerView);
        Button exportPdfBtn = findViewById(R.id.exportPdfBtn);

        category.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, categories));
        prioritySpinner.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        priorities
                )
        );

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

        // 📄 Export PDF
        exportPdfBtn.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/pdf");
            intent.putExtra(
                    Intent.EXTRA_TITLE,
                    "TaskManagerReport.pdf"
            );

            startActivityForResult(
                    intent,
                    CREATE_PDF_REQUEST
            );
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
                String priority = t.get("priority") != null
                        ? t.get("priority").toString()
                        : "Medium";
                int completed = (boolean) t.get("completed") ? 1 : 0;
                String id = t.get("id").toString();
                String due = t.get("dueDate") != null
                        ? t.get("dueDate").toString()
                        : "";

                long dueTimestamp = 0;

                if (t.get("dueTimestamp") != null) {
                    dueTimestamp =
                            ((Number) t.get("dueTimestamp")).longValue();
                }

                Task task = new Task(
                        0,
                        name,
                        cat,
                        priority,
                        completed,
                        due,
                        dueTimestamp
                );
                task.setFirebaseId(id);

                list.add(task);
                fullList.add(task);
            }
            int completedCount = 0;

            for (Task task : list) {
                if (task.isCompleted() == 1) {
                    completedCount++;
                }
            }

            int pendingCount = list.size() - completedCount;

            statsText.setText(
                    "Total: " + list.size() +
                            " | Completed: " + completedCount +
                            " | Pending: " + pendingCount
            );

            int progress = 0;

            if (list.size() > 0) {
                progress = (completedCount * 100) / list.size();
            }

            progressBar.setProgress(progress);
            Collections.sort(list, (task1, task2) -> {

                int pa = getPriorityValue(task1.getPriority());
                int pb = getPriorityValue(task2.getPriority());

                if (pa != pb) {
                    return pb - pa;
                }

                return Long.compare(
                        task1.getDueTimestamp(),
                        task2.getDueTimestamp()
                );
            });

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

    int getPriorityValue(String priority) {

        switch (priority) {

            case "High":
                return 3;

            case "Medium":
                return 2;

            case "Low":
                return 1;

            default:
                return 0;
        }
    }

    void filterTasks(String text) {

        list.clear();

        for (Task t : fullList) {

            if (t.getName()
                    .toLowerCase()
                    .contains(text.toLowerCase())) {

                list.add(t);
            }
        }

        Collections.sort(list, (task1, task2) -> {

            int pa = getPriorityValue(task1.getPriority());
            int pb = getPriorityValue(task2.getPriority());

            if (pa != pb) {
                return pb - pa;
            }

            return Long.compare(
                    task1.getDueTimestamp(),
                    task2.getDueTimestamp()
            );
        });

        adapter.notifyDataSetChanged();
    }

    void showEditDialog(Task t) {

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_edit_task, null);

        EditText name = view.findViewById(R.id.editTaskName);
        Spinner cat = view.findViewById(R.id.editCategory);
        Spinner priority = view.findViewById(R.id.editPriority);

        name.setText(t.getName());

        cat.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories
        ));

        priority.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                priorities
        ));

        new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("Update", (d, w) -> {

                    firebase.updateTask(
                            t.getFirebaseId(),
                            name.getText().toString(),
                            cat.getSelectedItem().toString(),
                            priority.getSelectedItem().toString(),
                            t.getDueDate(),
                            t.getDueTimestamp()
                    );

                    Toast.makeText(
                            this,
                            "Updated",
                            Toast.LENGTH_SHORT
                    ).show();
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

                                Calendar selected = Calendar.getInstance();

                                selected.set(Calendar.YEAR, year);
                                selected.set(Calendar.MONTH, month);
                                selected.set(Calendar.DAY_OF_MONTH, day);
                                selected.set(Calendar.HOUR_OF_DAY, hour);
                                selected.set(Calendar.MINUTE, minute);
                                selected.set(Calendar.SECOND, 0);
                                selected.set(Calendar.MILLISECOND, 0);

                                firebase.addTask(
                                        taskName,
                                        category.getSelectedItem().toString(),
                                        prioritySpinner.getSelectedItem().toString(),
                                        due,
                                        selected.getTimeInMillis()
                                );

                                scheduleReminder(taskName, selected);
                                scheduleAdvanceReminder(
                                        taskName,
                                        selected
                                );
                                input.setText("");
                            }, calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE), true);

                    timePicker.show();

                }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }


    void scheduleReminder(String task, Calendar calendar) {

        try {

            Intent intent = new Intent(this, ReminderReceiver.class);
            intent.putExtra("task", task);
            intent.putExtra(
                    "title",
                    "⏰ Task Reminder"
            );

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    (int) System.currentTimeMillis(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager =
                    (AlarmManager) getSystemService(ALARM_SERVICE);

            if (alarmManager != null) {

                alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );

                Toast.makeText(
                        this,
                        "Reminder Scheduled",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Reminder Error",
                    Toast.LENGTH_LONG
            ).show();

            e.printStackTrace();
        }
    }


    void scheduleAdvanceReminder(
            String task,
            Calendar calendar
    ) {

        Calendar advance = (Calendar) calendar.clone();

        advance.add(Calendar.MINUTE, -10);

        Intent intent = new Intent(
                this,
                ReminderReceiver.class
        );

        intent.putExtra("task", task);
        intent.putExtra(
                "title",
                "⏰ Task Due In 10 Minutes"
        );

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        this,
                        (int) (System.currentTimeMillis() + 1),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        AlarmManager alarmManager =
                (AlarmManager)
                        getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {

            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    advance.getTimeInMillis(),
                    pendingIntent
            );
        }
    }
    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (
                requestCode == CREATE_PDF_REQUEST
                        && resultCode == RESULT_OK
                        && data != null
        ) {

            Uri uri = data.getData();

            if (uri != null) {
                savePdfToUri(uri);
            }
        }
    }

    void savePdfToUri(Uri uri) {

        try {

            OutputStream outputStream =
                    getContentResolver()
                            .openOutputStream(uri);

            if (outputStream == null) {
                return;
            }

            Document document = new Document();

            PdfWriter.getInstance(
                    document,
                    outputStream
            );

            document.open();

            document.add(
                    new Paragraph(
                            "TASK MANAGER REPORT\n\n"
                    )
            );

            document.add(
                    new Paragraph(
                            "Total Tasks: "
                                    + fullList.size()
                                    + "\n\n"
                    )
            );

            for (Task task : fullList) {

                document.add(
                        new Paragraph(
                                "Task: " + task.getName()
                                        + "\nCategory: " + task.getCategory()
                                        + "\nPriority: " + task.getPriority()
                                        + "\nDue: " + task.getDueDate()
                                        + "\nStatus: "
                                        + (task.isCompleted() == 1
                                        ? "Completed"
                                        : "Pending")
                                        + "\n\n"
                        )
                );
            }

            document.close();
            outputStream.close();

            Toast.makeText(
                    this,
                    "PDF Saved Successfully",
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "PDF Export Failed",
                    Toast.LENGTH_LONG
            ).show();

            e.printStackTrace();
        }
    }
}