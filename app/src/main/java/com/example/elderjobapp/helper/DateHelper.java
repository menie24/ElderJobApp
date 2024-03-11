package com.example.elderjobapp.helper;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {
    public static Date getDate(int day, int mon, int year){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, mon-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }
    public static int getAgeFromDOB(Date dob){
        Calendar dobCalendar = Calendar.getInstance();
        dobCalendar.setTime(dob);
        Calendar todayCalendar = Calendar.getInstance();
        return todayCalendar.get(Calendar.YEAR)- dobCalendar.get(Calendar.YEAR);
    }
    public static String convertDateToStringFormat(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int mon = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);
        int yearBuddhist = year+543;
        String str = String.format("%02d/%02d/%04d",day, mon, yearBuddhist);
        return str;
    }
}
