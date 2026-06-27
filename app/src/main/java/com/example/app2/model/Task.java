package com.example.app2.model;

public class Task {

    private int id;
    private String name;
    private String category;
    private String priority;
    private int isCompleted;
    private String firebaseId;
    private String dueDate;
    private long dueTimestamp;

    public Task(
            int id,
            String name,
            String category,
            String priority,
            int isCompleted,
            String dueDate,
            long dueTimestamp
    ) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.priority = priority;
        this.isCompleted = isCompleted;
        this.dueDate = dueDate;
        this.dueTimestamp = dueTimestamp;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public String getCategory() { return category; }

    public String getPriority() { return priority; }

    public int isCompleted() { return isCompleted; }

    public String getFirebaseId() { return firebaseId; }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getDueDate() {
        return dueDate;
    }

    public long getDueTimestamp() {
        return dueTimestamp;
    }
}