package com.example.smartday;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.SharedPreferences;

public class Option{
	static Activity activity;
	static SharedPreferences sharedPreferences;
	static SharedPreferences.Editor editor;
	
	public Option(){
		
	}
	
	public Option(Activity act){
		activity = act;
		sharedPreferences = activity.getPreferences(MainActivity.MODE_PRIVATE);
		editor = sharedPreferences.edit();
	}
	
	public void setDate(int year,  int month, int day){
		editor.putString(activity.getString(R.string.date), day+". "+activity.getResources().getStringArray(R.array.months)[month]+" "+year);
		editor.commit();
	}
	
	public String getDate(){
		String date = sharedPreferences.getString(activity.getString(R.string.date), activity.getString(R.string.date_error));
		String default_date = sharedPreferences.getString(activity.getString(R.string.date_default), activity.getString(R.string.date_default));
		if(date.equals(default_date))
			return activity.getString(R.string.today);
		return date;
	}
	
	public void setDefault(){
		final Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);
		editor.putString(activity.getString(R.string.date_default), day+". "+activity.getResources().getStringArray(R.array.months)[month]+" "+year);
		editor.commit();
		this.setDate(year, month, day);
	}
	
	public void setApps(ArrayList<Integer> list){
		editor.putString(activity.getString(R.string.apps), list.toString());
		editor.commit();	
	}
	
	public boolean[] getApps(){
		char[] strApps = sharedPreferences.getString(activity.getString(R.string.apps), activity.getString(android.R.string.no)).replaceAll(", ", "").toCharArray();
		int numbers = strApps.length;
		int maxNumber = strApps[numbers-1];
		boolean[] contains = new boolean[maxNumber];
		for(int i=0; i<maxNumber;i++){
			if(i==strApps[i])
				contains[i]=true;
			else
				contains[i]=false;
		}
		return contains;
		
	}
}

