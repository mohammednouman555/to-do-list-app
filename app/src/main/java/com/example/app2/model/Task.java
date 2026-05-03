package com.example.app2.model;

public class Task {

    private int id;
    private String name;
    private String category;
    private int isCompleted;
    private String firebaseId;
    private String dueDate; // NEW

    public Task(int id, String name, String category, int isCompleted, String dueDate) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.isCompleted = isCompleted;
        this.dueDate = dueDate;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int isCompleted() { return isCompleted; }

    public String getFirebaseId() { return firebaseId; }
    public void setFirebaseId(String id) { this.firebaseId = id; }

    public String getDueDate() { return dueDate; }
}