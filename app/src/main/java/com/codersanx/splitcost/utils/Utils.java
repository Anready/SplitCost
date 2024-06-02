package com.codersanx.splitcost.utils;

import static com.codersanx.splitcost.utils.Constants.CATEGORY;
import static com.codersanx.splitcost.utils.Constants.EXPENSES;
import static com.codersanx.splitcost.utils.Constants.FALSE;
import static com.codersanx.splitcost.utils.Constants.INCOMES;
import static com.codersanx.splitcost.utils.Constants.MAIN_SETTINGS;
import static com.codersanx.splitcost.utils.Constants.TRUE;

import android.content.Context;

public class Utils {
    // 0 = False, 1 = True
    public static void initApp(Context c) {
        Databases db = new Databases(c, MAIN_SETTINGS);

        if (db.get("isInitComplete") != null)
            return;

        db.set("currentDb", "LocalDatabase");

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
        Databases db = new Databases(c, "MainSettings");
        return db.get("currentDb");
    }

//    public static String getSettings(Context c, String setting) {
//        Databases db = new Databases(c, "MainSettings");
//        return db.get(setting);
//    }
}
