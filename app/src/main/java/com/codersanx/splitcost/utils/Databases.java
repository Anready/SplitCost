package com.codersanx.splitcost.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Databases {
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public Databases(Context context, String database) {
        this.context = context;

        sharedPreferences = this.context.getSharedPreferences(database, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void set(String key, String data) {
        editor.putString(key, data).apply();
    }

    public String get(String key) {
        return sharedPreferences.getString(key, null);
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

    // Encrypt method
    private static String encrypt(String data) {
        if (data == null)
            return null;

        byte[] byteArray = data.getBytes();

        // Создаем новый массив для результатов
        byte[] resultArray = new byte[byteArray.length];

        // Добавляем к каждому байту +5
        for (int i = 0; i < byteArray.length; i++) {
            resultArray[i] = (byte) (byteArray[i] + 5);
        }

        // Преобразуем модифицированные байты обратно в строку
        return new String(resultArray);
    }

    // Decrypt method
    private static String decrypt(String data) {
        if (data == null)
            return null;

        byte[] byteArray = data.getBytes();

        // Создаем новый массив для результатов
        byte[] resultArray = new byte[byteArray.length];

        // Добавляем к каждому байту +5
        for (int i = 0; i < byteArray.length; i++) {
            resultArray[i] = (byte) (byteArray[i] - 5);
        }

        // Преобразуем модифицированные байты обратно в строку
        return new String(resultArray);
    }
}