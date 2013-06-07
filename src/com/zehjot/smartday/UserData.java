package com.zehjot.smartday;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.zehjot.smartday.helper.Security;
import com.zehjot.smartday.helper.Utilities;

public class UserData {
	private static Activity activity = null;
	private static JSONObject user = null;
	private static OnCallBackListener mCallBack;
	/**
	 * Functions to handle user data (Name, Password, Email)
	 * ask  - opens an alertDialog in which data can be written
	 * set  - stores if wanted login data in external file 
	 * get  - gets name, pw, email
	 * test - verifies login data with server
	 */

	public interface OnCallBackListener{
		public void onCallback(JSONObject jObj);
	}
	public void getUserLoginData(Context context){
		if(activity == null)
			activity = (Activity) context;		
		if(user !=null)
			mCallBack.onCallback(user);		
		mCallBack = (OnCallBackListener) DataSet.getInstance(activity);
		File file = activity.getFileStreamPath(activity.getString(R.string.user_file));
		if(!file.exists()){
			askForUserLogInData();
		}
		else{
			loadUserLogInData();
		}
//		testUserLogInData();
//		if(user_ok){
//			user_ok=false;
//		}
//		Utilities.showDialog("Invalid User", activity);
//		return getUserLoginData(context);
	}
	
	private void askForUserLogInData(){
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		LayoutInflater inflater = activity.getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_auth,null);
		builder.setView(view)
			.setTitle("Authentication")
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	           @Override
	           public void onClick(DialogInterface dialog, int id) {
	        	   EditText editText = (EditText) view.findViewById(R.id.auth_user_name);
	        	   String user_name = editText.getText().toString();
	        	   editText = (EditText) view.findViewById(R.id.auth_user_pass);
	        	   String user_pass = editText.getText().toString();
	        	   editText = (EditText) view.findViewById(R.id.auth_user_email);
	        	   String user_email = editText.getText().toString();
	        	   CheckBox saveData = (CheckBox) view.findViewById(R.id.auth_save_user_data);
	        	   Boolean save = saveData.isChecked();
	        	   createUserLogInData(user_name, user_pass, user_email, save);
	           }
	       })
	       
	       .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
	           @Override
	           public void onClick(DialogInterface dialog, int id) {
	               
	           }
	       });
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void createUserLogInData(String name, String pass, String email, Boolean saveData){			
		JSONObject jObj = new JSONObject();
		try {
			jObj.put(activity.getString(R.string.user_pass), Security.sha1(pass));
			jObj.put(activity.getString(R.string.user_name), name);
			if(email != null)
				jObj.put(activity.getString(R.string.user_email), name);
				
		} catch (JSONException e) {
			Utilities.showDialog("JSONError:USER CREATE",activity);				
		}
		if(saveData){
			Utilities.writeFile(activity.getString(R.string.user_file),jObj.toString(),activity);
			loadUserLogInData();
		}else{
			File file = activity.getFileStreamPath(activity.getString(R.string.user_file));
			if(file.exists()){
				file.delete();
			}
			user = jObj;
			testUserLogInData();
		}
		
	}

	private void testUserLogInData(){
		String url = Utilities.getURL("testcredentials",null,user,activity);
		new DownloadTask().execute(url);
	}

	private void loadUserLogInData(){
		String data = Utilities.readFile(activity.getString(R.string.user_file),activity);
		JSONObject jObj = null;
		try {
			jObj = new JSONObject(data);
		} catch (JSONException e) {
			Utilities.showDialog("JSON Object failed",activity);
			e.printStackTrace();
		}
		user = jObj;
		testUserLogInData();
	}
	private class DownloadTask extends AsyncTask<String, Void, Boolean>{
		int serverResponse = -1;
		@Override
		protected Boolean doInBackground(String... url) {/*
			try {
				this.get();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}*/
			ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if(networkInfo == null || !networkInfo.isConnected()){
				return null;
			}
			try{
				return downloadData(url[0]);
			} catch (IOException e){	
				return null;
			} 
		}
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);				
			if(result==null || result== false){
				Utilities.showDialog("NOT", activity);
				askForUserLogInData();
				return;
			}
			else{
				Utilities.showDialog("OK", activity);
				mCallBack.onCallback(user);
			}
		}
		
		private Boolean downloadData(String urlString) throws IOException{
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
				Log.d("Verify User", "Response: "+serverResponse);
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
				}	catch (Exception e) {
		            Log.e("Buffer Error", "Error converting result " + e.toString());
		        }
				try{
					JSONObject jObj = new JSONObject(json);
					if(jObj.get("result").toString().equals("1"))
						return true;
					else
						return false;
				} catch (JSONException e) {
				}
			}finally{
		        if (is != null) {
		        	//close inputstream
		            is.close();
		        } 
			}
			
		return false;	
		}
		
	}
}
