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
    private static final byte XOR_KEY = 0x5A; // XOR key

    public Databases(Context context, String database) {
        this.context = context;

        sharedPreferences = this.context.getSharedPreferences(database, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void set(String key, String data) {
        editor.putString(key, encrypt(data)).apply();
    }

    public String get(String key) {
        return decrypt(sharedPreferences.getString(key, null));
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
        editor.remove(key);
    }

    // XOR method
    private static byte[] xorEncryptDecrypt(byte[] data, byte key) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ key);
        }
        return result;
    }

    // Convert string to byte array
    private static byte[] stringToBytes(String input) {
        return input.getBytes();
    }

    // Convert byte array to string
    private static String bytesToString(byte[] input) {
        return new String(input);
    }

    // XOR invert method
    private static String xorByte(String data) {
        if (data == null) {
            return null;
        }

        byte[] bytes = stringToBytes(data);
        byte[] xorBytes = xorEncryptDecrypt(bytes, XOR_KEY);
        return bytesToString(xorBytes);
    }

    // Encrypt method
    private static String encrypt(String data) {
        return xorByte(data);
    }

    // Decrypt method
    private static String decrypt(String data) {
        return xorByte(data);
    }
}