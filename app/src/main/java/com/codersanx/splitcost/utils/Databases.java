package com.codersanx.splitcost.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class Databases {
    Context context;
    String databaseName;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public Databases(Context context, String database) {
        this.context = context;
        this.databaseName = database;

        sharedPreferences = this.context.getSharedPreferences(database, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void set(String key, String data) {
        editor.putString(key, data).apply();
    }

    public String get(String key) {
        return sharedPreferences.getString(key, null);
    }

    public String getCurrentDbName() {
        return databaseName;
    }

    public Map<String, String> readAll() {
        Map<String, ?> allEntries = sharedPreferences.getAll();
        Map<String, String> result = new HashMap<>();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            result.put(key, value.toString());
        }

        return result;
    }

    public void delete(String key) {
        editor.remove(key).apply();
    }
}