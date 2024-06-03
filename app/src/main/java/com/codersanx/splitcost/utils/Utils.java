package com.codersanx.splitcost.utils;

import static com.codersanx.splitcost.utils.Constants.ALL_DATABASES;
import static com.codersanx.splitcost.utils.Constants.CATEGORY;
import static com.codersanx.splitcost.utils.Constants.CURRENT_DB;
import static com.codersanx.splitcost.utils.Constants.EXPENSES;
import static com.codersanx.splitcost.utils.Constants.FALSE;
import static com.codersanx.splitcost.utils.Constants.INCOMES;
import static com.codersanx.splitcost.utils.Constants.MAIN_SETTINGS;
import static com.codersanx.splitcost.utils.Constants.TRUE;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.codersanx.splitcost.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Utils {
    // 0 = False, 1 = True
    public static void initApp(Context c) {
        Databases db = new Databases(c, MAIN_SETTINGS);


        if (db.get("isInitComplete") != null)
            return;

        Databases allDb = new Databases(c, ALL_DATABASES);
        allDb.set("LocalDatabase", TRUE);
        db.set(CURRENT_DB, "LocalDatabase");

        Databases expenseCategory = new Databases(c, currentDb(c) + CATEGORY + EXPENSES);
        expenseCategory.set("Products", TRUE);

        Databases incomesCategory = new Databases(c, currentDb(c) + CATEGORY + INCOMES);
        incomesCategory.set("Salary", TRUE);

        db.set("language", "en");
        db.set("theme", "white");
        db.set("isOnline", FALSE);
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
}
