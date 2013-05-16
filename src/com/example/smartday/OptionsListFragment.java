package com.example.smartday;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class OptionsListFragment extends ListFragment {
	OnOptionSelectedListener mCallback;
	ArrayAdapter<String> optionsListAdapter;
	List<String> displayedOptions = new ArrayList<String>();
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		displayedOptions.addAll(Arrays.asList(getResources().getStringArray(R.array.options_general)));
		displayedOptions.addAll(Arrays.asList(getResources().getStringArray(R.array.options_map)));
		optionsListAdapter = new ArrayAdapter<String>(
				getActivity(),
				android.R.layout.simple_list_item_1,				//the lists layout
				displayedOptions);									//the list with data (source)
		setListAdapter(optionsListAdapter);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return inflater.inflate(R.layout.options_list, container, false);
	}
	
	public interface OnOptionSelectedListener{
		public void onOptionSelected(int pos);
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		mCallback = (OnOptionSelectedListener) activity;
	}
	
	@Override
	public void onListItemClick(ListView l,View v,int position, long id){
		mCallback.onOptionSelected(position);
	}
	public void updateOptions(int pos){
		switch (pos) {
		case 0:
			displayedOptions.retainAll(Arrays.asList(getResources().getStringArray(R.array.options_general)));
			displayedOptions.addAll(Arrays.asList(getResources().getStringArray(R.array.options_map)));
			optionsListAdapter.notifyDataSetChanged();
			break;
		case 1:
			displayedOptions.retainAll(Arrays.asList(getResources().getStringArray(R.array.options_general)));
			displayedOptions.addAll(Arrays.asList(getResources().getStringArray(R.array.options_chart)));
			optionsListAdapter.notifyDataSetChanged();
			break;
		case 2:
			displayedOptions.retainAll(Arrays.asList(getResources().getStringArray(R.array.options_general)));
			displayedOptions.addAll(Arrays.asList(getResources().getStringArray(R.array.options_timeline)));
			optionsListAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}
}
