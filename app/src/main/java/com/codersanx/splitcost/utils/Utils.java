package com.codersanx.splitcost.utils;

import android.content.Context;

public class Utils {
    public static String currentDb(Context c) {
        Databases db = new Databases(c, "Settings");
        return db.get("currentDb");
    }
}
