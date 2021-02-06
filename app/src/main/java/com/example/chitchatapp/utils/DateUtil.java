package com.example.chitchatapp.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static DateFormat fullDateFormat;

    static {
        fullDateFormat = new SimpleDateFormat("hh:mm a");
    }

    public static String toTimeDate(Date date) {
        return fullDateFormat.format(date);
    }
}
