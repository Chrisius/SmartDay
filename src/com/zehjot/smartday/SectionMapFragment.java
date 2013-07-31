package com.zehjot.smartday;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onResume() {
		super.onResume();
		DataSet.getInstance(getActivity()).getApps(this);	
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
				markerList = new ArrayList<Marker>();
				JSONArray apps = jObj.getJSONArray("result");
				for(int i=0;i<apps.length();i++){
					JSONObject app = apps.getJSONObject(i);
					String appName = app.getString("app");
					JSONArray usages = app.getJSONArray("usage");
					for(int j=0;j<usages.length();j++){
						JSONObject usage = usages.getJSONObject(i);
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
				}
			}catch (JSONException e){
				e.printStackTrace();
			}
		}
	}
}
