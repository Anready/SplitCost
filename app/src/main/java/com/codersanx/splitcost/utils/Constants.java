package com.codersanx.splitcost.utils;

import android.content.Context;

import com.codersanx.splitcost.R;

public class Constants {
    public static final String MAIN_SETTINGS = "MainSettings";
    public static final String EXPENSES = "Expenses";
    public static final String INCOMES = "Incomes";
    public static final String CATEGORY = "Category";
    public static final String TRUE = "1";
    public static final String FALSE = "0";
    public static final String LAST_CATEGORY = "LastCategory";
    public static final String ALL_DATABASES = "AllDatabases";
    public static final String CURRENT_DB = "currentDb";
    public static final String PREFIX = "prefix";
    public static final String PASSWORD = "password";
    public static String PASS_FROM_ZIP(Context c) {
        return c.getResources().getString(R.string.PASS_FOR_ZIP);
    }

    public static final String[] ALL_PREFIX = {
            "Australian Dollar, A$",
            "Bitcoin, ₿",
            "Brazilian Real, R$",
            "Canadian Dollar, C$",
            "Danish Krone, kr",
            "Dollar USA, $",
            "Euro, €",
            "Franc, CHF",
            "Hong Kong Dollar, HK$",
            "Hryvnia, ₴",
            "Indonesian Rupiah, Rp",
            "Israeli Shekel, ₪",
            "Malaysian Ringgit, RM",
            "Mexican Peso, MX$",
            "New Zealand Dollar, NZ$",
            "Norwegian Krone, kr",
            "Pound Sterling, £",
            "Ruble, ₽",
            "Rupee, ₹",
            "Saudi Riyal, SAR",
            "Singapore Dollar, S$",
            "South African Rand, R",
            "Swedish Krona, kr",
            "Swiss Franc, CHF",
            "Thai Baht, ฿",
            "Turkish Lira, ₺",
            "UAE Dirham, AED",
            "Won, ₩",
            "Yen, ¥",
            "Yuan, ¥"
    };

}
