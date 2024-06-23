package com.codersanx.splitcost.utils;

import static com.codersanx.splitcost.utils.Constants.ALL_DATABASES;
import static com.codersanx.splitcost.utils.Constants.CATEGORY;
import static com.codersanx.splitcost.utils.Constants.CURRENT_DB;
import static com.codersanx.splitcost.utils.Constants.EXPENSES;
import static com.codersanx.splitcost.utils.Constants.FALSE;
import static com.codersanx.splitcost.utils.Constants.INCOMES;
import static com.codersanx.splitcost.utils.Constants.MAIN_SETTINGS;
import static com.codersanx.splitcost.utils.Constants.PREFIX;
import static com.codersanx.splitcost.utils.Constants.TRUE;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Utils {
    // 0 = False, 1 = True
    public static void initApp(Context c) {
        Databases db = new Databases(c, MAIN_SETTINGS);
        Databases settings = new Databases(c, currentDb(c) + MAIN_SETTINGS);

        if (db.get("isInitComplete") != null){
            applyTheme(settings);
            return;
        }

        Databases allDb = new Databases(c, ALL_DATABASES);
        allDb.set("LocalDatabase", TRUE);
        db.set(CURRENT_DB, "LocalDatabase");

        Databases expenseCategory = new Databases(c, currentDb(c) + CATEGORY + EXPENSES);
        expenseCategory.set("Products", TRUE);

        Databases incomesCategory = new Databases(c, currentDb(c) + CATEGORY + INCOMES);
        incomesCategory.set("Salary", TRUE);

        settings.set(PREFIX, "$");

        settings.set("language", "en");
        settings.set("theme", "white");
        settings.set("isOnline", FALSE);
        db.set("isInitComplete", FALSE);
    }

    public static String currentDb(Context c) {
        Databases db = new Databases(c, MAIN_SETTINGS);
        return db.get(CURRENT_DB);
    }

    public static List<String> getAllDb(Context c) {
        Databases db = new Databases(c, ALL_DATABASES);

        List<String> items = new ArrayList<>();

        for (Map.Entry<String, String> database : db.readAll().entrySet()) {
            items.add(database.getKey());
        }

        Collections.sort(items);

        return items;
    }

    public static boolean renameFile(File oldFile, String newFileName) {
        File directory = oldFile.getParentFile();
        File newFile = new File(directory, newFileName);

        return oldFile.renameTo(newFile);
    }

    public static void applyTheme(Databases settings){
        if (settings.get("theme") != null && settings.get("theme").equals("dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
