package com.zehjot.smartday;

import org.json.JSONObject;

import com.google.android.gms.maps.MapFragment;
import com.zehjot.smartday.TabListener.OnUpdateListener;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SectionMapFragment extends MapFragment implements OnUpdateListener{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		Log.d("MapView", "CreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
		
	}

	public void onUpdate() {
		// TODO Auto-generated method stub
		
	}
	 @Override
	public void putExtra(JSONObject jObj) {
		// TODO Auto-generated method stub
		
	}
}
