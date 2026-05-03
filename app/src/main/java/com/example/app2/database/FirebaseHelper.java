package com.example.app2.database;

import com.google.firebase.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import java.util.*;

public class FirebaseHelper {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void addTask(String name, String category, String dueDate) {
        Map<String, Object> task = new HashMap<>();
        task.put("name", name);
        task.put("category", category);
        task.put("completed", false);
        task.put("dueDate", dueDate);

        db.collection("users").document(getUid())
                .collection("tasks").add(task);
    }

    public void listenTasks(OnTasksChanged listener) {
        db.collection("users").document(getUid())
                .collection("tasks")
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
        db.collection("users").document(getUid())
                .collection("tasks").document(id).delete();
    }

    public void toggleTask(String id, boolean status) {
        db.collection("users").document(getUid())
                .collection("tasks").document(id)
                .update("completed", status);
    }

    public void updateTask(String id, String name, String category, String dueDate) {
        db.collection("users").document(getUid())
                .collection("tasks").document(id)
                .update("name", name, "category", category, "dueDate", dueDate);
    }

    public interface OnTasksChanged {
        void onChanged(List<Map<String, Object>> tasks);
    }
}