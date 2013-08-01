package com.zehjot.smartday;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.zehjot.smartday.TabListener.OnUpdateListener;
import com.zehjot.smartday.data_access.DataSet;
import com.zehjot.smartday.data_access.DataSet.onDataAvailableListener;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SectionMapFragment extends MapFragment implements OnUpdateListener,onDataAvailableListener{
	private GoogleMap mMap;
	private List<Marker> markerList = new ArrayList<Marker>();
	private JSONObject marker;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		Log.d("MapView", "CreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
		
	}
	
	public void onUpdate() {
		DataSet.getInstance(getActivity()).getApps(this);		
	}
	 @Override
	public void putExtra(JSONObject jObj) {
		double lat = jObj.optDouble("lat",0);
		double lng = jObj.optDouble("lng",0);
		if(getMap()!=null){
			if(mMap==null)
				mMap = this.getMap();
			zoomTo(lat, lng,15);
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		DataSet.getInstance(getActivity()).getApps(this);	
	}
	
	public void zoomTo(double lat, double lng, float zoom){
		 mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));
	}
	public void zoomTo(double lat, double lng){
		 mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 12));
	}
	
	@Override
	public void onDataAvailable(JSONObject jObj, String requestedFunction) {
		if(getMap()!=null){
			if(mMap==null)
				mMap = this.getMap();
			try{
				for(int i=0; i<markerList.size();i++){
					markerList.get(i).remove();
				}
				constructMarker(jObj);
				markerList = new ArrayList<Marker>();
				JSONArray positions = marker.optJSONArray("positions");
				for(int i=0;i<positions.length();i++){
					JSONObject position = positions.getJSONObject(i);
					double lat = position.optDouble("lat",-1);
					double lng = position.optDouble("lng", -1);
					MarkerOptions markerOptions = new MarkerOptions()
						.position(new LatLng(lat, lng))
						.title("Apps:");
					JSONArray apps = position.optJSONArray("apps");
					for(int j=0; j<apps.length();j++){
						JSONObject app = apps.optJSONObject(j);
						String appName = app.optString("app");
						markerOptions.title(markerOptions.getTitle()+"\n"+appName);
					}
					Marker mMarker = mMap.addMarker(markerOptions);
					markerList.add(mMarker);
				}/*
				Marker mMarker= mMap.addMarker(new MarkerOptions()
				.position(new LatLng(lat, lng))
				.title(appName));
				markerList.add(mMarker);
				
				
				JSONArray apps = jObj.getJSONArray("result");
				for(int i=0;i<apps.length();i++){
					JSONObject app = apps.getJSONObject(i);
					String appName = app.getString("app");
					JSONArray usages = app.getJSONArray("usage");
					for(int j=0;j<usages.length();j++){
						JSONObject usage = usages.getJSONObject(j);
						JSONArray location = usage.getJSONArray("location");
						double lat;
						double lng;
						if(location.getJSONObject(0).getString("key").equals("lng")){
							lng = location.getJSONObject(0).getDouble("value");
							lat = location.getJSONObject(1).getDouble("value");
						}else{
							lat = location.getJSONObject(0).getDouble("value");
							lng = location.getJSONObject(1).getDouble("value");						
						}
						boolean found = false;
						for(int k=0; k<markerList.size();k++){
							Marker tmpMarker = markerList.get(k);
							double markerLat = (int)(tmpMarker.getPosition().latitude*1000000+1);
							markerLat/=1000000.f;
							double markerLng = (int)(tmpMarker.getPosition().longitude*1000000+1);
							markerLng /= 1000000.f;
							if(markerLat==lat&&markerLng==lng){
								String oldTitle = tmpMarker.getTitle();
								tmpMarker.setTitle(oldTitle+appName);
								found = true;
								break;
							}
						}
						if(!found){
							Marker mMarker= mMap.addMarker(new MarkerOptions()
							.position(new LatLng(lat, lng))
							.title(appName));
							markerList.add(mMarker);
						}
					}
				}*/
			}catch (JSONException e){
				e.printStackTrace();
			}
		}
	}
	/**
	 * {
	 *  positions:
	 *  [
	 *    {
	 *  	"lat":double
	 *  	"lng":double
	 *  	"apps":
	 *  	[
	 *  	  {
	 *  		"app":String
	 *  		"usage":
	 *  		[
	 *  		  {
	 *  			"start":long
	 *  			"end":long
	 *  		  }
	 *  		]
	 *  	  }
	 *  	  ...
	 *  	]
	 *    }
	 *    ...
	 *  ]
	 * }
	 */
	private JSONObject constructMarker(JSONObject jObj){
		double testlat=50.47;
		double testlng=6.5;
		if(jObj == null)
			return null;
		try {
			JSONArray apps = jObj.getJSONArray("result");
			marker = new JSONObject();
			JSONArray positions = new JSONArray();
			marker.put("positions",positions);
			for(int i=0; i<apps.length();i++){
				JSONObject app = apps.getJSONObject(i);
				String appName = app.getString("app");
				JSONArray usages = app.getJSONArray("usage");
				for(int j=0;j<usages.length();j++){
					JSONObject usage = usages.getJSONObject(j);
					JSONArray location = usage.getJSONArray("location");
					long start=usage.optLong("start", -1);
					long end=usage.optLong("end", -1);
					double lat;
					double lng;
					if(start!=-1&&end!=-1){
						if(location.getJSONObject(0).getString("key").equals("lat")){
							lat = location.getJSONObject(0).getDouble("value");
							lng = location.getJSONObject(1).getDouble("value");	
						}else{
							lng = location.getJSONObject(0).getDouble("value");
							lat = location.getJSONObject(1).getDouble("value");						
						}
						testlat=lat;
						testlng=lng;
						boolean found = false;
						for(int k=0;k<positions.length();k++){
							JSONObject position = positions.getJSONObject(k);
							if(position.getDouble("lat")==lat&&position.getDouble("lng")==lng){
								JSONArray markerApps = position.getJSONArray("apps");
								for(int l=0;l<markerApps.length();l++){
									JSONObject markerApp = markerApps.getJSONObject(l);
									if(markerApp.getString("app").equals(appName)){
										markerApp.getJSONArray("usage").put(new JSONObject()
										.put("start", usage.getLong("start"))
										.put("end",usage.getLong("end")));
									found =true;
									break;
									}
								}
								if(!found){
									markerApps.put(new JSONObject()
										.put("app", appName)
										.put("usage", new JSONArray()
											.put(new JSONObject()
												.put("start", usage.getLong("start"))
												.put("end",usage.getLong("end"))
											)
										)
									);
									found = true;
								}
								break;
							}
						}
						if(!found){
							positions.put(new JSONObject()
								.put("lat", lat)
								.put("lng", lng)
								.put("apps", new JSONArray()
									.put(new JSONObject()
										.put("app", appName)
										.put("usage", new JSONArray()
											.put(new JSONObject()
												.put("start", usage.getLong("start"))
												.put("end",usage.getLong("end"))
											)
										)
									)
								)
							);
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		zoomTo(testlat, testlng);
		return marker;
	}
}
