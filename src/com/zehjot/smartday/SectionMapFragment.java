package com.zehjot.smartday;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.zehjot.smartday.TabListener.OnUpdateListener;
import com.zehjot.smartday.data_access.DataSet;
import com.zehjot.smartday.data_access.DataSet.onDataAvailableListener;
import com.zehjot.smartday.helper.Utilities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SectionMapFragment extends MapFragment implements OnUpdateListener,onDataAvailableListener, InfoWindowAdapter{
	private GoogleMap mMap;
	private List<Marker> markerList = new ArrayList<Marker>();
	private JSONObject marker;
	private Polyline polyline;
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
		//DataSet.getInstance(getActivity()).getPositions(this);
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
			mMap.setInfoWindowAdapter(this);
			try{
				for(int i=0; i<markerList.size();i++){
					markerList.get(i).remove();
				}
				if(polyline!=null){
					polyline.remove();
				}

				PolylineOptions polylineOptions = new PolylineOptions();
//				PolygonOptions polyOptions = new PolygonOptions();
				JSONArray locations = jObj.getJSONArray("locations");
//				LatLng latlng[]=new LatLng[locations.length()];
				for(int i=0;i<locations.length();i++){
					polylineOptions.add(new LatLng(locations.getJSONObject(i).getDouble("lat"),locations.getJSONObject(i).getDouble("lng")));
//					latlng[i]= new LatLng(locations.getJSONObject(i).getDouble("lat"),locations.getJSONObject(i).getDouble("lng"));
				}
				polyline = mMap.addPolyline(polylineOptions);
				int color = getResources().getColor(android.R.color.holo_blue_light);
//				color = (color<<8);
//				color = (color>>8);
//				color = color&0x7fffffff;
				polyline.setColor(color);
				polyline.setWidth(4);
				constructMarker(jObj);
				markerList = new ArrayList<Marker>();
				JSONArray positions = marker.optJSONArray("positions");
				for(int i=0;i<positions.length();i++){
					JSONObject position = positions.getJSONObject(i);
					double lat = position.optDouble("lat",-1);
					double lng = position.optDouble("lng", -1);
					MarkerOptions markerOptions = new MarkerOptions()
						.position(new LatLng(lat, lng))
						.title("Apps used from "+Utilities.getTimeFromTimeStamp(position.getLong("start"))+" to "+Utilities.getTimeFromTimeStamp(position.getLong("end")));
					JSONArray apps = position.optJSONArray("apps");
					for(int j=0; j<apps.length();j++){
						JSONObject app = apps.optJSONObject(j);
						String appName = app.optString("app");
						String oldApps = markerOptions.getSnippet();
						if(oldApps==null)
							oldApps = "";
						else 
							appName = ", "+appName;
						markerOptions.snippet(oldApps+appName);
						markerOptions.icon(null);
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
		double testlat=50.7783846;
		double testlng=6.060978;
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
					JSONArray location = usage.optJSONArray("location");
					if(location != null){						
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
								long locStart=-1;
								long locEnd=-1;
								JSONArray locations = jObj.getJSONArray("locations");
								for(int k=0;k<locations.length();k++ ){
									JSONObject tmpLocation = locations.getJSONObject(k);
									if(tmpLocation.getDouble("lat")==lat&&tmpLocation.getDouble("lng")==lng){
										locStart = tmpLocation.getLong("timestamp");
										if(k<locations.length()-1)
											locEnd = locations.getJSONObject(k+1).getLong("timestamp");
									break;
									}
								}
								
								
								positions.put(new JSONObject()
									.put("lat", lat)
									.put("lng", lng)
									.put("start", locStart)
									.put("end", locEnd)
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
			}
			for(int i=0; i<positions.length();i++){
				JSONObject position = positions.getJSONObject(i);
				long markerEnd = position.getLong("end");
				if(markerEnd==-1){
					JSONArray tmpApps = position.getJSONArray("apps");
					for(int j = 0; j<tmpApps.length();j++){
						JSONObject tmpApp = tmpApps.getJSONObject(j);
						JSONArray tmpUsages = tmpApp.getJSONArray("usage");
						for(int k=0;k<tmpUsages.length();k++){
							JSONObject tmpUsage = tmpUsages.getJSONObject(k);
							long tmpEnd = tmpUsage.optLong("end", -1);
							if(tmpEnd!=-1 && markerEnd<tmpEnd){
								markerEnd = tmpEnd;
							}
						}
					}
					position.put("end", markerEnd);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		zoomTo(testlat, testlng);
		return marker;
	}


	@Override
	public View getInfoWindow(Marker arg0) {
		TextView tv = new TextView(getActivity());
		tv.setText("test");
		return null;
	}

	@Override
	public View getInfoContents(Marker arg0) {
		String title = arg0.getTitle();
		String apps[]= arg0.getSnippet().split(", ");
		LinearLayout ll = new LinearLayout(getActivity());
		ll.setOrientation(LinearLayout.VERTICAL);
		TextView tv = new TextView(getActivity());
		tv.setText(title);
		ll.addView(tv);
		for(int i=0; i<apps.length;i++){
			tv= new TextView(getActivity());
			tv.setText(apps[i]);
			ll.addView(tv);			
		}
		return ll;
	}



}
