package com.example.administrator.envsystem.utils;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/6/11.
 */

public class TimeUtils {

    public static String getYear(){
        Calendar calendar= Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        return year+"";
    }

    public static String getMonth(){
        Calendar calendar= Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        if (month < 10) {
            return "0" + (month + 1);
        } else {
            return month + "";
        }
    }

    public static String getDay(){
        Calendar calendar= Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day < 10) {
            return "0" + day;
        } else {
            return day + "";
        }
    }
}
