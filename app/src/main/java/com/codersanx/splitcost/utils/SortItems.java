package com.codersanx.splitcost.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class SortItems implements Comparator<String> {
    @Override
    public int compare(String s1, String s2) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        try {
            Date date1 = sdf.parse(s1);
            Date date2 = sdf.parse(s2);

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(date1);
            cal2.setTime(date2);

            int yearCompare = Integer.compare(cal2.get(Calendar.YEAR), cal1.get(Calendar.YEAR));
            if (yearCompare != 0) {
                return yearCompare;
            }
            int monthCompare = Integer.compare(cal2.get(Calendar.MONTH), cal1.get(Calendar.MONTH));
            if (monthCompare != 0) {
                return monthCompare;
            }
            int dayCompare = Integer.compare(cal2.get(Calendar.DAY_OF_MONTH), cal1.get(Calendar.DAY_OF_MONTH));
            if (dayCompare != 0) {
                return dayCompare;
            }
            int hourCompare = Integer.compare(cal2.get(Calendar.HOUR_OF_DAY), cal1.get(Calendar.HOUR_OF_DAY));
            if (hourCompare != 0) {
                return hourCompare;
            }
            int minuteCompare = Integer.compare(cal2.get(Calendar.MINUTE), cal1.get(Calendar.MINUTE));
            if (minuteCompare != 0) {
                return minuteCompare;
            }
            return Integer.compare(cal2.get(Calendar.SECOND), cal1.get(Calendar.SECOND));
        } catch (ParseException ignored) {}
        return 0;
    }
}
