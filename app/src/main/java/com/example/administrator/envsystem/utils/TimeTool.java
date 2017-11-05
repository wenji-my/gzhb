package com.example.administrator.envsystem.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeTool {
	public static String getTiem() {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		String time = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
		return time;
	}

	public static String getTiemriqi() {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		String time = year + "-" + month + "-" + day;
		return time;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getfiveTiem() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String time = sDateFormat.format(new java.util.Date());
		return time;
	}
}
