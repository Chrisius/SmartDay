package com.example.smartday;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

public class SectionList extends ListFragment {
	private String[] sections;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		sections = getResources().getStringArray(R.array.sections);
		ListAdapter sectionListAdapter = new ArrayAdapter<String>(
				getActivity(),
				android.R.layout.simple_list_item_activated_1,		//the lists layout
				sections);											//the array with data (source)
		setListAdapter(sectionListAdapter);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return inflater.inflate(R.layout.section_list, container, false);
	}
	
}
