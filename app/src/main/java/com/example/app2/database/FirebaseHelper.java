package com.example.app2.database;

import com.google.firebase.firestore.*;
import java.util.*;


public class FirebaseHelper {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void addTask(String name, String category) {
        Map<String, Object> task = new HashMap<>();
        task.put("name", name);
        task.put("category", category);
        task.put("completed", false);

        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(uid).collection("tasks").add(task);
    }

    public void listenTasks(OnTasksChanged listener) {
        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(uid).collection("tasks")
                .addSnapshotListener((value, error) -> {

                    if (error != null || value == null) return;

                    List<Map<String, Object>> list = new ArrayList<>();

                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Map<String, Object> map = doc.getData();
                        if (map != null) {
                            map.put("id", doc.getId());
                            list.add(map);
                        }
                    }

                    listener.onChanged(list);
                });
    }

    public void deleteTask(String id) {
        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(uid).collection("tasks").document(id).delete();
    }

    public void toggleTask(String id, boolean status) {
        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(uid).collection("tasks").document(id)
                .update("completed", status);
    }

    public void updateTask(String id, String name, String category) {
        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(uid).collection("tasks").document(id)
                .update("name", name, "category", category);
    }

    public interface OnTasksChanged {
        void onChanged(List<Map<String, Object>> tasks);
    }
}