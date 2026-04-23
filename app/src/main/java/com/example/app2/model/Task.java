package com.example.app2.model;

public class Task {

    private int id;
    private String name;
    private String category;
    private int isCompleted;
    private String firebaseId;

    public Task(int id, String name, String category, int isCompleted) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.isCompleted = isCompleted;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int isCompleted() { return isCompleted; }

    public String getFirebaseId() { return firebaseId; }
    public void setFirebaseId(String id) { this.firebaseId = id; }
}