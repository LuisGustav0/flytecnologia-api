package com.flytecnologia.core.base.plusService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public interface FlyTimeSpentService {
    default void showTheTimeSpent(long start, String message) {
        long end = System.currentTimeMillis();

        Date date = new Date(end - start);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateFormatted = formatter.format(date);

        System.out.println(message + dateFormatted);
    }
}
