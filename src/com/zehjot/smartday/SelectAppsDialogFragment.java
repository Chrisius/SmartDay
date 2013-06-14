package com.zehjot.smartday;

import org.json.JSONException;
import org.json.JSONObject;

import com.zehjot.smartday.R;
import com.zehjot.smartday.data_access.DataSet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class SelectAppsDialogFragment extends DialogFragment{
	public static final int SELECT_APPS=0;
	public static final int IGNORE_APPS=1;
	private DataSet dataSet = null;
	private String[]strings = {"No Data Available"};
	private int mode=0;
	
	public void setStrings(String[] strings) {
		this.strings = strings;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	@Override
	public void onCreate(Bundle saved){
		super.onCreate(saved);
		if(saved!=null)
			strings= saved.getStringArray("strings");
	}
	@Override
	public Dialog onCreateDialog(Bundle saved){
		dataSet = DataSet.getInstance(getActivity());
		final JSONObject selectedApps;
		if(mode == SELECT_APPS){
			selectedApps = dataSet.getSelectedApps();
		}else{
			selectedApps = dataSet.getIgnoreApps();
		}
		boolean[] boolSelectedApps = new boolean[strings.length];
		for(int i=0 ; i<strings.length;i++){
			boolSelectedApps[i] = selectedApps.optBoolean(strings[i]);
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setTitle(R.string.options_app_select) //Dialogtitle
			   .setMultiChoiceItems(strings, boolSelectedApps,
				new DialogInterface.OnMultiChoiceClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				try{
					if(isChecked){
						dataSet.getSelectedApps().put(strings[which], "true");
					} else {
						dataSet.getSelectedApps().put(strings[which], "false");
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
				
			}
		})
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(mode == SELECT_APPS)
					dataSet.setSelectedApps(selectedApps);
				else
					dataSet.setIgnoreApps(selectedApps);
			}
		})
		.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		return builder.create();
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putStringArray("strings", strings);
		super.onSaveInstanceState(outState);
	}
}
