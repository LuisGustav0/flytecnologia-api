package com.flytecnologia.core.base.service.plus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FlyTimeSpentService {
    public static String getMessageTimeSpent(long start, String message) {
        final long end = System.currentTimeMillis();

        final Date date = new Date(end - start);
        final DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String dateFormatted = formatter.format(date);

        return message + dateFormatted;
    }
}
