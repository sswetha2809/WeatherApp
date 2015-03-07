package com.example.weatherapplication;

import java.util.Calendar;
import java.util.Date;

/**
 * author: swetha
 * The Class DateUtil.
 */
public class DateUtil
{
    
    /**
     * Adds the days.
     *
     * @param date the date
     * @param days the days
     * @return the date
     */
    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
}