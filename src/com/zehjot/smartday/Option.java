package com.zehjot.smartday;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import com.zehjot.smartday.R;

import android.app.Activity;
import android.content.SharedPreferences;

public class Option{
	//private static Option option = null;
	static Activity activity = null;
	static SharedPreferences sharedPreferences = null;
	static SharedPreferences.Editor editor = null;
	static ArrayList<Integer> selectedApps=null;
	
	public Option(){
		
	}
	/*
	public static Option getInstance(){
		return option;
	}*/
	public Option(Activity act){
		//option = this;
		activity = act;
		sharedPreferences = activity.getPreferences(MainActivity.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		
	}/*
	public static void initOptions(Activity act){
		if(option == null){
			option = new Option();
			activity = act;
			sharedPreferences = activity.getPreferences(MainActivity.MODE_PRIVATE);
			editor = sharedPreferences.edit();	
		}
		return;
	}*/
	
	public void setDate(int year,  int month, int day){
		editor.putString(activity.getString(R.string.key_date), day+". "+activity.getResources().getStringArray(R.array.months)[month]+" "+year);
		editor.putInt(activity.getString(R.string.key_date_day), day);
		editor.putInt(activity.getString(R.string.key_date_month), month);
		editor.putInt(activity.getString(R.string.key_date_year), year);
		editor.commit();
	}
	
	public String getSelectedDate(){
		String date = getSharedString(R.string.key_date);
		String default_date = sharedPreferences.getString(activity.getString(R.string.key_date_default), activity.getString(R.string.key_date_default));
		if(date.equals(default_date))
			return activity.getString(R.string.today);
		return date;
	}
	
	public int[] getSelectedDateAsArray(){
		int year = getSharedInt(R.string.key_date_year);
		int month = getSharedInt(R.string.key_date_month);
		int day = getSharedInt(R.string.key_date_day);
		
		return new int[] {year, month, day};
	}
	
	public void init(){
		final Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);
		editor.putString(activity.getString(R.string.key_date_default), day+". "+activity.getResources().getStringArray(R.array.months)[month]+" "+year);
		editor.putString(activity.getString(R.string.key_date_selected_apps),"not Initialized");
		editor.commit();
		this.setDate(year, month, day);
	}
	
	public void storeSelectedApps(ArrayList<Integer> list){
		selectedApps = list;
		editor.putString(activity.getString(R.string.key_apps), list.toString());
		editor.putString(activity.getString(R.string.key_date_selected_apps), getSelectedDate());
		editor.commit();	
	}
	
	public boolean[] getSelectedApps(){
		ArrayList<String> appsAtDate = getApps(getSharedInt(R.string.key_date_year),getSharedInt(R.string.key_date_month),getSharedInt(R.string.key_date_day));
		int size = appsAtDate.size();
		boolean[] boolSelectedApps = new boolean[size];
		Arrays.fill(boolSelectedApps, Boolean.FALSE);

		if(getSharedString(R.string.key_date_selected_apps).equals(getSelectedDate())){
			for(int i = 0 ; i<selectedApps.size() ; i++){ 				//looks up every stored selected App
				boolSelectedApps[selectedApps.get(i)]=true;				//sets every arrayfield to true if app was selected
			}
		}
		return boolSelectedApps;
		
	}
	
	private ArrayList<String> getApps(int year, int month, int day_of_month){
		//TODO query server Data
		ArrayList<String> apps = new ArrayList<String>();
		
		String[] strApps = activity.getResources().getStringArray(R.array.months);
		for(int i = 0; i<strApps.length;i++){
			apps.add(strApps[i]);
		}
		
		return apps;
	}
	
	private String getSharedString(int id){
		return sharedPreferences.getString(activity.getString(id), activity.getString(R.string.error));
	}
	private int getSharedInt(int id){
		return sharedPreferences.getInt(activity.getString(id), -1);
	}
}

