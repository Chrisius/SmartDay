package com.zehjot.smartday.data_access;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.zehjot.smartday.data_access.DataSet.onDataAvailableListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadTask extends AsyncTask<String, Void, JSONObject>{
	private int serverResponse = -1;
	private ProgressDialog progress;
	private String request;
	private onDataAvailableListener requester;
	private static Activity activity;
	private String fileName = null;
	private static onDataDownloadedListener listener = (onDataDownloadedListener) DataSet.getInstance(activity);
	
	protected interface onDataDownloadedListener{
		public void onDataDownloaded(int serverResponse, JSONObject jObj, String request, onDataAvailableListener requester, String fileName);
	}
	
	protected DownloadTask(onDataAvailableListener requester, Activity activity){
		this.requester = requester;
		DownloadTask.activity = activity;
	}
	protected DownloadTask(){
	}
	
	@Override
	protected void onPreExecute(){
		super.onPreExecute();
		progress = new ProgressDialog(activity);
		progress.setCancelable(false);
		progress.setMessage("Downloading data from server...");
		progress.show();
	}
	
	@Override
	protected JSONObject doInBackground(String... url) {
		ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if(networkInfo == null || !networkInfo.isConnected()){
			return null;
		}
		if(url.length>1){
			request = url[1];
			fileName = url[2];
		}
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
		progress.cancel();
		listener.onDataDownloaded(serverResponse, result, request, requester, fileName);		
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
			Log.d("HTTPDebug", "Response: "+serverResponse);
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
