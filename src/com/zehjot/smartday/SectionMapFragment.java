package com.zehjot.smartday;

import com.google.android.gms.maps.MapFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SectionMapFragment extends MapFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		Log.d("MapView", "CreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
		
	}
	
}
