package com.codersanx.splitcost.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

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
        editor.putString(key, encrypt(get(key) + data)).apply();
    }

    public String get(String key) {
        return decrypt(sharedPreferences.getString(key, null));
    }

    public void delete(String key) {
        editor.remove(key);
    }

    private static String invertByte(String data) {
        byte[] bytes = data.getBytes();
        byte[] invertedBytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            invertedBytes[i] = (byte) ~bytes[i];
        }
        return new String(invertedBytes);
    }

    private static String encrypt(String data) {
        return invertByte(data);
    }

    private static String decrypt(String data) {
        return invertByte(data);
    }
}
