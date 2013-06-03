package com.zehjot.smartday;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class DataSet {
	private static final String USER_NAME = "username";
	private static final String USER_PASS = "password";
	private static final String PASSWORD = "DiCBP_2909";
	private static DataSet instance = null;
	private static Activity activity = null;
	private static SharedPreferences sharedPreferences = null;
	private static SharedPreferences.Editor editor = null;
	private static ArrayList<Integer> selectedApps=null;
    private static final String DEBUG_TAG = "HttpExample";
	
	private List<Pair> data = new ArrayList<Pair>();
	
	protected DataSet(){
		//For Singleton
	}
	
	public class Pair{
		public final String app;
		public final double duration;
		
		public Pair(String a, double d){
			this.app = a;
			this.duration = d;
		}
		
	}

	public static DataSet getInstance(Context context){
		if(instance == null){
			activity = (Activity) context;
			instance = new DataSet();
			sharedPreferences = activity.getPreferences(MainActivity.MODE_PRIVATE);
			editor = sharedPreferences.edit();
			DataSet.initDate();
			instance.initTestData();
		}
		String user = sharedPreferences.getString(USER_NAME, activity.getString(R.string.error));
		String pass = sharedPreferences.getString(USER_PASS, activity.getString(R.string.error));
		if(user.equals(activity.getString(R.string.error))||pass.equals(activity.getString(R.string.error))){
			editor.putString(USER_NAME, "Christian");
			editor.putString(USER_PASS, instance.sha1(PASSWORD));
			editor.commit();
		}
		return instance;
	}
	
	public List<Pair> getApps(){
		int[] date = getSelectedDateAsArray();
		return getAppsAtDate(date[0],date[1],date[2]);
	}
	
	public List<Pair> getAppsAtDate(int year, int month, int day){
		return null;
	}
	
	public void setSelectedDate(int year,  int month, int day){
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
	
	public void setSelectedApps(ArrayList<Integer> list){
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
	
	private static void initDate(){
		final Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);
		editor.putString(activity.getString(R.string.key_date_default), day+". "+activity.getResources().getStringArray(R.array.months)[month]+" "+year);
		editor.putString(activity.getString(R.string.key_date_selected_apps),"not Initialized"); //Sets String for selectDate to "not Initialized" 
		editor.commit();
		instance.setSelectedDate(year, month, day);
	}

	private void initTestData(){
		data.add(new Pair("Facebook",0.4));
		data.add(new Pair("Whatsapp",0.2));
		data.add(new Pair("Facebook",1.0));
		data.add(new Pair("Mail",3.0));
		data.add(new Pair("Reader",0.4));
		data.add(new Pair("Facebook",0.84));
		data.add(new Pair("Google",1.2));
		data.add(new Pair("PhoneCall",1.4));
		data.add(new Pair("Google",0.7));
		data.add(new Pair("Facebook",1.2));
	}

	private ArrayList<String> getApps(int year, int month, int day_of_month){
		ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if(networkInfo == null || !networkInfo.isConnected()){
			//TODO ErrorMessage
		}
		else{
			new DownloadTask().execute("TESTURL");
		}
		
		ArrayList<String> apps = new ArrayList<String>();
		/*
		String[] strApps = activity.getResources().getStringArray(R.array.months);
		for(int i = 0; i<strApps.length;i++){
			apps.add(strApps[i]);
		}
		*/
		return apps;
	}
	
	private String getSharedString(int id){
		return sharedPreferences.getString(activity.getString(id), activity.getString(R.string.error));
	}
	private int getSharedInt(int id){
		return sharedPreferences.getInt(activity.getString(id), -1);
	}
	
	private String sha1(String s){
        MessageDigest digest = null;
        try {
                digest = MessageDigest.getInstance("SHA-1");
	        } catch (Exception e) {
                e.printStackTrace();
        }
		digest.reset();
		byte[] data = digest.digest(s.getBytes());
		return String.format("%0" + (data.length*2) + "X", new BigInteger(1, data));		
	}
	
	private class DownloadTask extends AsyncTask<String, Void, JSONObject>{
		String json = "";

		@Override
		protected JSONObject doInBackground(String... url) {
			
			try{
				return downloadData(url[0]);
			} catch (IOException e){		
				e.printStackTrace();
				return null;
			} catch (JSONException e) {				
				e.printStackTrace();
				return null;
			}
		}
		
		private JSONObject downloadData(String urlString) throws IOException, JSONException{
			InputStream is = null;
			try{
				URL url = new URL(urlString);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(10000 /*ms*/);
				conn.setConnectTimeout(15000/*ms*/);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				
				//Start query
				conn.connect();
				int response = conn.getResponseCode();
				Log.d(DEBUG_TAG, "Response: "+response);
				is = conn.getInputStream();
				//InpuStream to JSONObeject
				try{
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
					StringBuilder sb = new StringBuilder();
		            String line = null;
		            while ((line = reader.readLine()) != null) {
		                sb.append(line + "\n");
		            }
		            json = sb.toString();
				}catch (Exception e) {
		            Log.e("Buffer Error", "Error converting result " + e.toString());
		        }
				return new JSONObject(json);
			}finally{
		        if (is != null) {
		            is.close();
		        } 
			}
			
			
		}
		
	}
}