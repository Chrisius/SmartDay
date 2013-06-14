package com.zehjot.smartday;

import com.zehjot.smartday.R;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SectionTimelineFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		Log.d("Timeline", "CreateView");
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.section_timeline_fragment, container, false);
	}
}
