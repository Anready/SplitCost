package com.codersanx.splitcost.utils;

import android.content.Context;

public class Utils {
    // 0 = False, 1 = True
    public static void initApp(Context c) {
        Databases db = new Databases(c, "MainSettings");

        if (db.get("isInitComplete") != null)
            return;

        db.set("currentDb", "LocalDatabase");
        db.set("language", "en");
        db.set("theme", "white");
        db.set("isOnline", "0");
        db.set("isInitComplete", "1");
    }

    public static String currentDb(Context c) {
        Databases db = new Databases(c, "MainSettings");
        return db.get("currentDb");
    }

    public static String getSettings(Context c, String setting) {
        Databases db = new Databases(c, "MainSettings");
        return db.get(setting);
    }
}
