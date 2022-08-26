package com.example.vchtcollector.tests;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TestDateTime {
    public static void main(String[] args) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        System.out.println("Current Date " + dateFormat.format(date));

        // Convert Date to Calendar
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        // Perform addition/subtraction
        c.add(Calendar.YEAR, 2);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DATE, -10);
        c.add(Calendar.HOUR, -4);
        c.add(Calendar.MINUTE, 30);
        c.add(Calendar.SECOND, 50);

        // Convert calendar back to Date
        Date currentDatePlusOne = c.getTime();

        System.out.println("Updated Date " + dateFormat.format(currentDatePlusOne));
    }
}
