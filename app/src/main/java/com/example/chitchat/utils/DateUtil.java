package com.example.chitchat.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    private static DateFormat fullDateFormat;

    static {
        fullDateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
    }
    public static String getLastMessageTimestamp(Date utcDate) {
        if (utcDate != null) {
            Calendar todayCalendar = Calendar.getInstance();
            Calendar localCalendar = Calendar.getInstance();
            localCalendar.setTime(utcDate);

            if (getDateStringFromDate(todayCalendar.getTime())
                    .equals(getDateStringFromDate(localCalendar.getTime()))) {

                return getTimeStringFromDate(utcDate);

            } else if ((todayCalendar.get(Calendar.DATE) - localCalendar.get(Calendar.DATE)) == 1) {
                return "Yesterday";
            } else {
                return getDateStringFromDate(utcDate);
            }
        } else {
            return null;
        }
    }
    public static String toFullDate(Date date) {
        return fullDateFormat.format(date);
    }
    public static String getTimeStringFromDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        return dateFormat.format(date);
    }

    public static String getDateStringFromDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return dateFormat.format(date);
    }
}
