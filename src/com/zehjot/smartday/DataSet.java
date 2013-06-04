package com.zehjot.smartday;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class DataSet {
	private static final String USER_NAME = "username";
	private static final String USER_PASS = "password";
	private static final String NAME  = "Christian";
	private static final String PASSWORD = "DiCBP_2909";
	private static final String AID = "5";
	private static final String APP_SECRET = "9lycn2n42mgave0pgatx5s6po6zg4b0rfm39exbs6fdll0iuvm";
	private static final String DOMAIN = "http://context.thues.com/";
	private static final String VERSION = "2/";
	private static DataSet instance = null;
	private static Activity activity = null;
	private static SharedPreferences sharedPreferences = null;
	private static SharedPreferences.Editor editor = null;
	private static ArrayList<Integer> selectedApps=null;
    private static final String DEBUG_TAG = "HttpExample";
    private static ArrayList<String> apps;
	private List<Pair> data = new ArrayList<Pair>();
	
	private String test;
	
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
		}
		String user = sharedPreferences.getString(USER_NAME, activity.getString(R.string.error));
		String pass = sharedPreferences.getString(USER_PASS, activity.getString(R.string.error));
		if(user.equals(activity.getString(R.string.error))||pass.equals(activity.getString(R.string.error))){
			editor.putString(USER_NAME, NAME);
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
	private ArrayList<String> getApps(int year, int month, int day_of_month){
		ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if(networkInfo == null || !networkInfo.isConnected()){
			//TODO ErrorMessage
		}
		else{apps = new ArrayList<String>();
			new DownloadTask().execute("TEST");
			String[] strApps = activity.getResources().getStringArray(R.array.months);
			for(int i = 0; i<strApps.length;i++){
				apps.add(strApps[i]);
			}
		}
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
	
	private static String getNonce() { 
		SecureRandom sr = null;	
		try {
			sr = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		String nonce = new BigInteger(256, sr).toString(26);
		return nonce;
	}
	
	private static long getTimestamp(){
		Calendar c = Calendar.getInstance();
		c.set(2013, 6, 4);
		return c.getTimeInMillis();
	}
	
	private String getUserURL(String name, String password){
		String url = DOMAIN+VERSION+"newuser";
		
		if(!url.endsWith("?"))
	        url += "?";
		//create JSONObject from back to front
	    JSONObject jObj = new JSONObject();
	    try {
		    jObj.put("pass", password);
			jObj.put("name", name);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	    //JSON Object as String with prefix
	    String dataAsURL=jObj.toString();
	    url += "data="+dataAsURL;
	    return url;
	}
	
	private String getURL(String queryType,String data){
		String url = DOMAIN+VERSION+queryType;
		if(!url.endsWith("?"))
	        url += "?";

	    List<NameValuePair> params = new LinkedList<NameValuePair>();
	    String nonce = getNonce();
	    
	    params.add(new BasicNameValuePair("data", data));
	    params.add(new BasicNameValuePair("nonce", nonce));
	    params.add(new BasicNameValuePair("aid", AID));
	    params.add(new BasicNameValuePair("user", USER_NAME));
	    String dataAsURL=null;
		try {
			dataAsURL = URLEncoder.encode(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	    params.add(new BasicNameValuePair("h", sha1(dataAsURL+"."+AID+"."+nonce+"."+APP_SECRET+"."+sha1(USER_PASS))));
	    
	    String paramString = URLEncodedUtils.format(params, "utf-8");

	    url += paramString;
	    return url;
	}
	
	private class DownloadTask extends AsyncTask<String, Void, String>{
		String json = null;
		@Override
		protected String doInBackground(String... url) {
			
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
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			//show query and result
			if(result == null)
				test = "FAIL";
			else
				test = getURL("foo", "bar")+"||"+getUserURL(NAME, PASSWORD)+"||"+result;
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setMessage(test);
			AlertDialog dialog = builder.create();
			dialog.show();			
		}
		
		private String downloadData(String urlString) throws IOException, JSONException{
			InputStream is = null;
			try{
				//creating URL object and open connection
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
				
				return json;//new JSONObject(json);
			}finally{
		        if (is != null) {
		        	//close inputstream
		            is.close();
		        } 
			}
			
			
		}
		
	}
}