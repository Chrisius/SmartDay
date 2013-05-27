package com.zehjot.smartday;

import com.zehjot.smartday.R;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SectionListFragment extends ListFragment {
	OnSectionSelectedListener mCallback;
	boolean firstStart = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		ListAdapter sectionListAdapter = new ArrayAdapter<String>(
				getActivity(),
				android.R.layout.simple_list_item_activated_1,		//the lists layout
				getResources().getStringArray(R.array.sections));	//the array with data (source)
		setListAdapter(sectionListAdapter);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.section_list, container, false);
	}
	
	public interface OnSectionSelectedListener{
		public void onSectionSelected(int pos);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		if(firstStart){
			firstStart = !firstStart;
			getListView().setItemChecked(0,true);
		}
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		mCallback = (OnSectionSelectedListener) activity;
	}
	
	@Override
	public void onListItemClick(ListView l,View v,int position, long id){
		mCallback.onSectionSelected(position);
		l.setItemChecked(position, true);
	}
	
}
