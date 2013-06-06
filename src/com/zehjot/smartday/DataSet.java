package com.zehjot.smartday;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.zehjot.smartday.helper.Security;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class DataSet {
	private static DataSet instance = null;
	private static Activity activity = null;
	private static SharedPreferences sharedPreferences = null;
	private static SharedPreferences.Editor editor = null;
	private static ArrayList<Integer> selectedApps=null;
    private static final String DEBUG_TAG = "HttpExample";
    private static ArrayList<String> apps;
	private static JSONObject user = null;
	
	protected DataSet(){
		//For Singleton
	}
	
	public static DataSet getInstance(Context context){
		if(instance == null){
			init(context);
		}
		return instance;
	}

	public class Pair{
		public final String app;
		public final double duration;
		
		public Pair(String a, double d){
			this.app = a;
			this.duration = d;
		}
		
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
	public JSONObject getUserData(){
		FileInputStream fis;
		StringBuilder sb = null;
		String data = null;
		try{
			fis = activity.openFileInput(activity.getString(R.string.user_file));
			InputStreamReader inputStreamReader = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			sb = new StringBuilder();
			while ((data = bufferedReader.readLine()) != null) {
				sb.append(data);
			}
			fis.close();
		}catch(IOException e){
			showDialog("No Userdata found");
			return null;
		}
		data=sb.toString();
		String decryptedData = Security.decrypt(data);
		JSONObject result = null;
		try {
			result = new JSONObject(decryptedData);
		} catch (JSONException e) {
			showDialog("JSON Object failed");
			e.printStackTrace();
		}
		return result;
	}

	public void testUserData(){
		String url = getURL("testcredentials",null);
		new DownloadTask().execute(url);
	}

	private static void setUserData(String name, String pass){			
		JSONObject jObj = new JSONObject();		
		try {
			jObj.put(activity.getString(R.string.user_appSec), "9lycn2n42mgave0pgatx5s6po6zg4b0rfm39exbs6fdll0iuvm");
			jObj.put(activity.getString(R.string.user_appID),"5");
			jObj.put(activity.getString(R.string.user_url_prefix), "http://context.thues.com/2/");
			jObj.put(activity.getString(R.string.user_pass), pass);
			jObj.put(activity.getString(R.string.user_name), name);
		} catch (JSONException e) {
			instance.showDialog("JSONError:USER CREATE");				
		}
		String encryptedData = Security.encrypt(jObj.toString());
		FileOutputStream fos;
		try{
			fos = activity.openFileOutput(activity.getString(R.string.user_file), Context.MODE_PRIVATE);
			fos.write(encryptedData.getBytes());
			fos.close();
		}catch(IOException e){
			instance.showDialog("Error while storing Userdata");
		}
	}

	private static void init(Context context){
		activity = (Activity) context;
		instance = new DataSet();
		sharedPreferences = activity.getPreferences(MainActivity.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		initDate();
		File file = activity.getFileStreamPath(activity.getString(R.string.user_file));
		if(!file.exists()){
			//TODO create Dialog asking user for name and Password
			setUserData("Christian","DiCBP_2909");
		}
		user = instance.getUserData();	
		instance.testUserData();
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
		apps = new ArrayList<String>();
		//new DownloadTask().execute("TEST")
		/*
		try {
			new DownloadTask().execute(
					getURLregisterUserToServer(
							user.getString(activity.getString(R.string.user_name)),
							user.getString(activity.getString(R.string.user_pass))
							)
						);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		//testUserData();
		getEventTypes();
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
	private void showDialog(String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(message);
		AlertDialog dialog = builder.create();
		dialog.show();		
	}
	
	private void getEventTypes(){
		String url = getURL("types",null);
		new DownloadTask().execute(url);
	}
	private String getURLregisterUserToServer(String name, String password){
		//create JSONObject
	    try {	
		    JSONObject jObj = new JSONObject();
			jObj.put("name", name);
		    jObj.put("pass", password);
			return user.getString(activity.getString(R.string.user_url_prefix))
		    		+"newuser?data="
		    		+jObj.toString();
		} catch (JSONException e) {
			instance.showDialog("JSONError registerUserToServer");
			e.printStackTrace();
			return null;
		}
	}
	
	private String getURL(String queryType,String data){
		try {
		    List<NameValuePair> params = new LinkedList<NameValuePair>();
		    String nonce = Security.getNonce();
    	    if(data != null){
    	    	params.add(new BasicNameValuePair("data", data));
    	    }else{
    	    	data="";
    	    }
	    	params.add(new BasicNameValuePair("nonce", nonce));		    
	    	params.add(new BasicNameValuePair("aid", user.getString(activity.getString(R.string.user_appID))));
			params.add(new BasicNameValuePair("user", user.getString(activity.getString(R.string.user_name))));
			//String dataAsURL = URLEncoder.encode(data, "UTF-8");
			params.add(new BasicNameValuePair("h", 
				Security.sha1(	
					data+
					user.getString(activity.getString(R.string.user_appID))+
					user.getString(activity.getString(R.string.user_name))+
					nonce+
					user.getString(activity.getString(R.string.user_appSec))+
					Security.sha1(user.getString(activity.getString(R.string.user_pass)))
				)
			));
			return user.getString(activity.getString(R.string.user_url_prefix))
					+queryType
					+"?"
					+URLEncodedUtils.format(params, "utf-8");
				
		}catch (JSONException e) {
			instance.showDialog("JSONError in getURL");
			e.printStackTrace();
			return null;
		}
	}
	private void downloadTaskErrorHandler(JSONObject jObj, int serverResponse){
		ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if(networkInfo == null || !networkInfo.isConnected()){
			showDialog("No dataservice available");
			return;
		}
		if(jObj == null){
			showDialog("Invalid name or password. Server response:"+ serverResponse);
			//TODO Add dialog to request new user data
			setUserData("Christian", "DiCBP_2909");
			user = getUserData();
			testUserData();
			return;
		}
		try {
			showDialog("Error occurred: "+jObj.getString("reason"));
		} catch (JSONException e) {
			showDialog("Error occured while trying to read server error, no reason stated.");
		}
	}
	private class DownloadTask extends AsyncTask<String, Void, JSONObject>{
		String urlRequest = null;
		int serverResponse = -1;
		@Override
		protected JSONObject doInBackground(String... url) {
			ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if(networkInfo == null || !networkInfo.isConnected()){
				return null;
			}
			urlRequest = url[0];
			try{
				return downloadData(url[0]);
			} catch (IOException e){	
				return null;
			} catch (JSONException e) {	
				return null;
			}
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			//show query and result
			try {
				if(result == null || result.get("result").toString().equals("0"))
					downloadTaskErrorHandler(result, serverResponse);
				else
					showDialog("Sucsess Query: "+urlRequest+"\n"+"Result: "+result.toString());
			} catch (JSONException e) {
				downloadTaskErrorHandler(result, serverResponse);
			}
			
		}
		
		private JSONObject downloadData(String urlString) throws IOException, JSONException{
			InputStream is = null;
			String json = null;
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
				serverResponse = conn.getResponseCode();
				Log.d(DEBUG_TAG, "Response: "+serverResponse);
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
		        	//close inputstream
		            is.close();
		        } 
			}
			
			
		}
		
	}
	/*public class AuthDialogFragment extends DialogFragment {
		
	}*/
}