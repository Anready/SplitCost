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

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;

import com.codersanx.splitcost.R;

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
            applyTheme(new Databases(c, MAIN_SETTINGS));
            return;
        }

        Databases allDb = new Databases(c, ALL_DATABASES);
        allDb.set("LocalDatabase", TRUE);
        db.set(CURRENT_DB, "LocalDatabase");

        Databases expenseCategory = new Databases(c, currentDb(c) + CATEGORY + EXPENSES);
        expenseCategory.set(c.getResources().getString(R.string.products), TRUE);

        Databases incomesCategory = new Databases(c, currentDb(c) + CATEGORY + INCOMES);
        incomesCategory.set(c.getResources().getString(R.string.salary), TRUE);

        settings.set(PREFIX, "$");

        settings.set("language", "en");
        settings.set("theme", "white");
        settings.set("isOnline", FALSE);
        db.set("isInitComplete", TRUE);
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

    public static String getPrefix(Context c) {
        String prefix = new Databases(c, currentDb(c) + MAIN_SETTINGS).get(PREFIX);
        if (prefix == null) prefix = "$";
        return prefix;
    }

    public static boolean isInternetAvailable(Application application) {
        ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }

    public static boolean isDatabaseOnline(Context c) {
        Databases settings = new Databases(c, currentDb(c) + MAIN_SETTINGS);
        return settings.get("isOnline") != null && settings.get("isOnline").equals(TRUE);
    }

    public static void synchronizeDb(Context c) {
        synchronizeDb(c,null);
    }

    public static void synchronizeDb(Context c, AlertDialog a) {
        System.out.println("SYNCING");
        if (a != null) a.cancel();
    }
}
