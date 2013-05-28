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
	private OnSectionSelectedListener mCallback;
	private boolean firstStart = true;
	private ListAdapter sectionListAdapter = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		sectionListAdapter = new ArrayAdapter<String>(
				getActivity(),
				android.R.layout.simple_list_item_activated_1,		//the lists layout
				getResources().getStringArray(R.array.sections));	//the array with data (source)
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.section_list, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//getListView().addHeaderView(getActivity().getLayoutInflater().inflate(R.layout.header_view,null),null,false); //Inflates the Header View und attaches it to the List
		setListAdapter(sectionListAdapter);
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
