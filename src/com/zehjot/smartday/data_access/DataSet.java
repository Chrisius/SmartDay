package com.zehjot.smartday.data_access;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zehjot.smartday.MainActivity;
import com.zehjot.smartday.R;
import com.zehjot.smartday.data_access.DownloadTask.onDataDownloadedListener;
import com.zehjot.smartday.data_access.LoadFileTask.onDataLoadedListener;
import com.zehjot.smartday.data_access.UserData.OnUserDataAvailableListener;
import com.zehjot.smartday.helper.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class DataSet extends Activity implements OnUserDataAvailableListener, onDataDownloadedListener, onDataLoadedListener{
	private static DataSet instance = null;
	private static UserData userData = null;
	private static Activity activity = null;
	private static SharedPreferences sharedPreferences = null;
	private static SharedPreferences.Editor editor = null;
	private static JSONObject user = null;
	private static long queryStart;
	private static JSONObject selectedApps = null;
	private static JSONObject ignoreApps = null;
	
	
	protected DataSet(){
		//For Singleton
	}
	
	public static DataSet getInstance(Context context){
		if(instance == null){
			init(context);
		}
		return instance;
	}
	public static void updateActivity(Activity act){
		if(act == null)
			return;
		activity = act;
		userData.updateActivity(act);
		updateDate();
	}
	
	public static JSONObject getUser() {
		return user;
	}
	
	private static void init(Context context){
		instance = new DataSet();
		activity = (Activity) context;
		sharedPreferences = activity.getPreferences(MainActivity.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		initDate();
		/**
		 * For debug reason delete user file
		 */
		/*
		File file = activity.getFileStreamPath(activity.getString(R.string.user_file));
		if(file.exists()){
			file.delete();
		}*/
		createUserData();
		selectedApps = new JSONObject();
		File file = new File(activity.getString(R.string.file_ignored_apps));
		if(file.exists()){
			try {
				ignoreApps = new JSONObject(Utilities.readFile(activity.getString(R.string.file_ignored_apps), activity));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else
			ignoreApps = new JSONObject();
	}

	private static void initDate(){
		final Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);
		editor.putString(activity.getString(R.string.key_date_default), day+". "+activity.getResources().getStringArray(R.array.months)[month]+" "+year);
		editor.putLong(activity.getString(R.string.key_date_default_timestamp), Utilities.getTimestamp(year, month, day, 0, 0, 0)).commit();
		//editor.putString(activity.getString(R.string.key_date_selected_apps),"not Initialized"); //Sets String for selectDate to "not Initialized"
		editor.commit();
		instance.setSelectedDate(year, month, day);
	}
	
	private static void updateDate(){ 
		final Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);
		editor.putString(activity.getString(R.string.key_date_default), day+". "+activity.getResources().getStringArray(R.array.months)[month]+" "+year);
		editor.putLong(activity.getString(R.string.key_date_default_timestamp), Utilities.getTimestamp(year, month, day, 0, 0, 0)).commit();		
	}
	
	private static void createUserData(){
		if(userData==null)
			userData = new UserData();
		userData.getUserLoginData(activity);
	}
	
	public void createNewUser(){
		userData.getUserLoginData(activity, true);
	}
	
	public interface onDataAvailableListener{
		public void onDataAvailable(JSONObject jObj, String request);
	}
	
	public void getApps(onDataAvailableListener listener){
		int[] date = getSelectedDateAsArray();
		getAppsAtDate(date[0],date[1],date[2],listener);
	}
	
	public void getAppsAtDate(int year, int month, int day, onDataAvailableListener listener){
		long start=Utilities.getTimestamp(year, month, day, 0, 0, 0);
		long end=start+(23*60+59)*60+59;
		
		JSONObject data = new JSONObject();
		try {
			data.put("model", "SPECIFIC");
			data.put("start", start);
			data.put("end", end);/*
			data.put("source", "MOBILE");
			data.put("type", "APPSTART");
			data.put("key", "app");*/
		} catch (JSONException e) {
			e.printStackTrace();
		}
		getData(listener, "values", data);
	}
	
	public JSONObject getSelectedApps(){
		return selectedApps;
	}
	
	public JSONObject getIgnoreApps() {
		return ignoreApps;
	}
	
	public void setIgnoreApps(JSONObject ignoreApps) {
		DataSet.ignoreApps = ignoreApps;
		Utilities.writeFile(activity.getString(R.string.file_ignored_apps), ignoreApps.toString(), activity);
	}
	public boolean[] getSelectedApps(ArrayList<String> apps){
		boolean[] boolSelectedApps = new boolean[apps.size()];
		for(int i=0 ; i<apps.size();i++){
			boolSelectedApps[i] = selectedApps.optBoolean(apps.get(i));
		}
	
		
		/*
		for(int i=0 ; i<apps.size();i++){
			String app = apps.get(i);
			for(int j= 0; j<selectedApps.length();j++){
				if(!selectedApps.optJSONObject(j).optString(app).equals("")){
					boolSelectedApps[i]=selectedApps.optJSONObject(j).optBoolean("checked");
					break;
				}
			}
		}*/
		return boolSelectedApps;
	}

	public void setSelectedApps(JSONObject selectedApps){
		DataSet.selectedApps = selectedApps;
	}

	public String getSelectedDate(){
		String date = getSharedString(R.string.key_date);
		String default_date = sharedPreferences.getString(activity.getString(R.string.key_date_default), activity.getString(R.string.key_date_default));
		if(date.equals(default_date))
			return activity.getString(R.string.today);
		return date;
	}

	public long getTodayAsTimestamp(){
		return sharedPreferences.getLong(activity.getString(R.string.key_date_default_timestamp), -1);
	}

	public void setSelectedDate(int year,  int month, int day){
		editor.putString(activity.getString(R.string.key_date), day+". "+activity.getResources().getStringArray(R.array.months)[month]+" "+year);
		editor.putInt(activity.getString(R.string.key_date_day), day);
		editor.putInt(activity.getString(R.string.key_date_month), month);
		editor.putInt(activity.getString(R.string.key_date_year), year);
		editor.commit();
	}
	
	public int[] getSelectedDateAsArray(){
		int year = getSharedInt(R.string.key_date_year);
		int month = getSharedInt(R.string.key_date_month);
		int day = getSharedInt(R.string.key_date_day);
		
		return new int[] {year, month, day};
	}
	
	public long getSelectedDateAsTimestamp(){
		int year = getSharedInt(R.string.key_date_year);
		int month = getSharedInt(R.string.key_date_month);
		int day = getSharedInt(R.string.key_date_day);		
		return Utilities.getTimestamp(year, month, day, 0, 0, 0);
	}
	public long getNextDayAsTimestamp(){
		int year = getSharedInt(R.string.key_date_year);
		int month = getSharedInt(R.string.key_date_month);
		int day = getSharedInt(R.string.key_date_day);		
		return Utilities.getTimestamp(year, month, day, 0, 0, 0)+24*60*60;
	}
	@Override
	public void onUserDataAvailable(JSONObject jObj) {
		user = jObj;
		((onDataAvailableListener)activity).onDataAvailable(null, null);
	}
	public void onDataDownloaded(int serverResponse, JSONObject jObj, String request, onDataAvailableListener requester, String fileName, String fileNameBasic){
		/**
		 * Gets called after server has send data
		 * if result jObj is null it will ask for new Login data
		 * else calls the requester and if a filename is given the data will be stored
		 */
		long queryEnd = Utilities.getSystemTime();
		if(jObj == null){
			downloadTaskErrorHandler(jObj, serverResponse);
		}else{
			JSONObject result = constructBasicJSONObj(jObj);
			if(getSelectedDateAsTimestamp()<getTodayAsTimestamp() && fileNameBasic != null){
				new StoreFileTask(activity).execute(result.toString(),fileNameBasic);
			}
			if(request == "events"){	
				result = filterIgnoredApps(result);
				if(requester!=null)
					requester.onDataAvailable(result, request);
			}else if(request == "values"){
				//result = constructAppNameJSONObj(result);
				if(getSelectedDateAsTimestamp()<getTodayAsTimestamp() && fileName != null){
					new StoreFileTask(activity).execute(result.toString(),fileName);
				}			
				result = filterIgnoredApps(result);				
				if(requester!=null)
					requester.onDataAvailable(result, request);	
			}else if(request == "allApps"&&requester!=null){
				requester.onDataAvailable(jObj, request);				
			}/*
			if(requester!=null){
				requester.onDataAvailable(jObj, request);
				//store data if selected date is not today and a filename was given, else new data might be available
				if(getSelectedDateAsTimestamp()<getTodayAsTimestamp() && fileName != null){
					new StoreFileTask(activity).execute(jObj.toString(),fileName);
				}
			}*/
			String time = "Download time: "+(queryEnd-queryStart)+"ms";
			Utilities.showDialog(time, activity);
		}
	}
	public void onDataLoaded(JSONObject jObj, String request, onDataAvailableListener requester, String fileName){
		/**
		 * gets called after internal data is loaded
		 * deletes file if jObj==null
		 * else calls the requester
		 */
		long queryEnd = Utilities.getSystemTime();
		if(jObj == null && fileName!= null){
			File file = activity.getFileStreamPath(fileName);
			if(file.exists())
				file.delete();
		}else{
			jObj = filterIgnoredApps(jObj);
			requester.onDataAvailable(jObj, request);
		}
		String time = "Internal Storage time: "+(queryEnd-queryStart)+"ms";
		Utilities.showDialog(time, activity);
	}
	
	public void getContext(onDataAvailableListener requester){
		getContext(getSelectedDateAsTimestamp(), getNextDayAsTimestamp(), requester);
	}
	
	public void getContext(long start, long end, onDataAvailableListener requester){
		JSONObject data = new JSONObject();
		try{
		data.put("model", "SPECIFIC");
		data.put("start", start);
		data.put("end", end);
		//data.put("type", "POSITION");
		}catch(JSONException e){
			
		}
		getData(requester, "events", data);
		/*
		String url = Utilities.getURL("events", data.toString(), user, activity);
		queryStart = Utilities.getSystemTime();
		new DownloadTask(null,activity).execute(url);
		*/
	}
	
	private String getSharedString(int id){
		return sharedPreferences.getString(activity.getString(id), activity.getString(R.string.error_no_string));
	}
	private int getSharedInt(int id){
		return sharedPreferences.getInt(activity.getString(id), -1);
	}
	public void getAllApps(onDataAvailableListener requester){
		JSONObject data = new JSONObject();
		try{
			data.put("type", "APPSTART");
			data.put("key", "app");
			data.put("start", Utilities.getTimestamp(2013, 6, 12, 0, 0, 0));
			data.put("end", Utilities.getTimestamp(2013, 6, 13, 0, 0, 0));
		}catch(JSONException e){
			
		}
		String url = Utilities.getURL("value",data.toString(),user, activity);
		new DownloadTask(requester,activity).execute(url,"allApps",null,null);
	}
	private void getEventTypes(){
		String url = Utilities.getURL("types",null,user,activity);
		new DownloadTask().execute(url);
	}
	private void getEventCategories(){
		String url = Utilities.getURL("categories", null, user, activity);
		new DownloadTask().execute(url);
	}
	private JSONObject constructBasicJSONObj(JSONObject jObj){
		JSONArray jArrayInput=null;
		JSONArray lastKnownPos = null;
		JSONArray jArrayOutput= new JSONArray();
		JSONObject result=new JSONObject();
		try{
			jArrayInput = jObj.getJSONArray("events");					
			for(int i=0; i<jArrayInput.length();i++){
				JSONObject jObjInput = jArrayInput.getJSONObject(i);
				if(jObjInput.getString("type").equals("APPSTART")){
					boolean found = false;
					
					String appName = "NoAppNameFound";
					JSONArray entities = jObjInput.getJSONArray("entities");
					for(int x = 0; x<entities.length(); x++){
						if(entities.getJSONObject(x).getString("key").equals("app")){
							appName = entities.getJSONObject(x).getString("value");
							break;
						}
					}
					
					String appSession = jObjInput.getString("session");
					long time = jObjInput.getLong("timestamp");
					for(int j = 0; j<jArrayOutput.length();j++){
						JSONObject jObjOutput = jArrayOutput.getJSONObject(j);
						if(jObjOutput.getString("app").equals(appName)){
							JSONArray usages = jObjOutput.getJSONArray("usage");
							for(int k = 0; k<usages.length();k++){
								JSONObject usage = usages.getJSONObject(k);
								if(usage.getString("session").equals(appSession)){
									usage.put("end", time);
									found = true;
									break;
								}
							}
							if(!found){
								JSONObject usage = new JSONObject();
								usage.put("end", Utilities.getSystemTime());
								usage.put("start", time);
								usage.put("session",appSession);
								usages.put(usage);
								found = true;
								break;
							}
						}
					}
					if(!found){
						JSONObject app = new JSONObject();
							JSONArray usages = new JSONArray();
								JSONObject usage = new JSONObject();
								usage.put("start", time);
								usage.put("session",appSession);
							usages.put(usage);
						app.put("usage",usages);
						app.put("app", appName);
						jArrayOutput.put(app);
					}
				}else if(jObjInput.getString("type").equals("POSITION")){
					long time = jObjInput.getLong("timestamp");
					JSONArray location = jObjInput.getJSONArray("entities");
					lastKnownPos = location;
					for(int j = 0; j<jArrayOutput.length();j++){
						JSONArray appUsages = jArrayOutput.getJSONObject(j).getJSONArray("usage");
						for(int k = 0; k<appUsages.length();k++){
							JSONObject usage = appUsages.getJSONObject(k);
							if(usage.getLong("start")<=time && !usage.has("location")){
								usage.put("location",location);
							}
						}
					}
				}
			}
			for(int i = 0; i<jArrayOutput.length(); i++){
				for(int j = 0; j < jArrayOutput.getJSONObject(i).getJSONArray("usage").length(); j++){
					if(!jArrayOutput.getJSONObject(i).getJSONArray("usage").getJSONObject(j).has("location")){
						jArrayOutput.getJSONObject(i).getJSONArray("usage").getJSONObject(j).put("location", lastKnownPos);
					}
				}
			}
			result.put("result", jArrayOutput);
		}catch(JSONException e){
			
		}
		return result;
	}
	
	private JSONObject constructAppNameJSONObj(JSONObject jObj){
		JSONObject result= new JSONObject();
		JSONArray output= new JSONArray();
		try{
		for(int i = 0; i< jObj.getJSONArray("result").length(); i++){
			JSONObject app = jObj.getJSONArray("result").getJSONObject(i);
			output.put(new JSONObject().put("app",app.getString("app")));			
		}
		result.put("result", output);
		}catch(JSONException e){
			return result;			
		}
		return result;
	}
	
	private void getData(onDataAvailableListener requester, String request, JSONObject jObj){
		/**
		 * Checks if requested data is offline available and loads it.
		 * If it's not available the server is requested.
		 */
		queryStart = Utilities.getSystemTime();
		boolean fileExists = false;
		boolean fileBasicExists = false;
		String fileName = Utilities.getFileName(request, user, jObj,activity);
		String fileNameBasic = Utilities.getFileName("events", user, jObj,activity);
		if(fileName != null)
			fileExists = activity.getFileStreamPath(fileName).exists();
		if(fileNameBasic != null)
			fileBasicExists = activity.getFileStreamPath(fileNameBasic).exists();
		if(fileExists){
			new LoadFileTask(requester, activity).execute(fileName, request);
		}else if(fileBasicExists){
			new LoadFileTask(requester, activity).execute(fileNameBasic, request);	
		}else{
			String url = Utilities.getURL("events", jObj.toString(), user, activity);
			new DownloadTask(requester,activity).execute(url,request,fileName,fileNameBasic);
		}
	}
	public JSONObject filterIgnoredApps(JSONObject jObj){
		JSONObject result = new JSONObject();
		JSONArray output = new JSONArray();
		try{
		for(int i=0; i<jObj.getJSONArray("result").length();i++){
			JSONObject app = jObj.getJSONArray("result").getJSONObject(i);
			if(!ignoreApps.optBoolean(app.getString("app")))
				output.put(app);
		}
		return result.put("result",output);
		}catch(JSONException e){
			return result;
		}
	}
	protected void downloadTaskErrorHandler(JSONObject jObj, int serverResponse){
		ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if(networkInfo == null || !networkInfo.isConnected()){
			Utilities.showDialog("No dataservice available",activity);
			return;
		}
		if(jObj == null){
			String errorMessage;
			switch (serverResponse) {
			case -1:
				if(user==null)
					errorMessage = activity.getString(R.string.error_no_user_jObj);
				else
					errorMessage = activity.getString(R.string.error);
				break;
			case -2:
				//task was canceled by user
				return;
			case 403:
				errorMessage = activity.getString(R.string.error_authentication_fail)+serverResponse;
				break;
			case 404:
				errorMessage = activity.getString(R.string.error)+serverResponse;
				break;
			default:
				errorMessage = activity.getString(R.string.error);
				break;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setMessage(errorMessage)
				.setCancelable(false)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			           @Override
			           public void onClick(DialogInterface dialog, int id) {
			        	  //Create or Recreate user data
			        	  createUserData();
			           }
			       });
			AlertDialog dialog = builder.create();
			if(((MainActivity) activity).isRunning())
				dialog.show();			
		}else{
			try {
				Utilities.showDialog("Error occurred: "+jObj.getString("reason"),activity);
			} catch (JSONException e) {
				Utilities.showDialog("Error occured while trying to read server error, no reason stated.",activity);
			}
		}
	}
}