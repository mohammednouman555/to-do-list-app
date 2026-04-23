package com.example.app2.database;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import com.example.app2.model.Task;
import java.util.*;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "tasks.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tasks(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, category TEXT, completed INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void insertTask(String name, String category) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("category", category);
        values.put("completed", 0);
        db.insert("tasks", null, values);
    }

    public List<Task> getTasks() {
        List<Task> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM tasks", null);

        while (c.moveToNext()) {
            list.add(new Task(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getInt(3)
            ));
        }
        c.close();
        return list;
    }

    public void updateTask(int id, String name, String category) {
        ContentValues v = new ContentValues();
        v.put("name", name);
        v.put("category", category);
        getWritableDatabase().update("tasks", v, "id=?", new String[]{String.valueOf(id)});
    }

    public void toggleComplete(int id, int status) {
        ContentValues v = new ContentValues();
        v.put("completed", status);
        getWritableDatabase().update("tasks", v, "id=?", new String[]{String.valueOf(id)});
    }

    public void deleteTask(int id) {
        getWritableDatabase().delete("tasks", "id=?", new String[]{String.valueOf(id)});
    }
}