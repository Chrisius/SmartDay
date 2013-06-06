package com.zehjot.smartday.helper;

import java.util.Calendar;

public class Utilities {
	public static long getTimestamp(int year, int month, int day, int hour, int minutes, int seconds){
		Calendar c = Calendar.getInstance();
		c.set(2013, 6, 4);
		return c.getTimeInMillis()+seconds*1000+minutes*60000+hour*360000;
	}
	
}
