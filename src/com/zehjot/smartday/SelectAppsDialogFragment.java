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
	private DataSet dataSet = null;
	private String[]strings = {"No Data Available"};
	
	public void setStrings(String[] array_of_strings){
		strings = array_of_strings;
	}
	@Override
	public Dialog onCreateDialog(Bundle saved){
		dataSet = DataSet.getInstance(getActivity());
		final JSONObject selectedApps = dataSet.getSelectedApps();
		
		boolean[] boolSelectedApps = new boolean[strings.length];
		for(int i=0 ; i<strings.length;i++){
			boolSelectedApps[i] = selectedApps.optBoolean(strings[i]);
		}
		//strings = getArguments().getStringArray(getString(R.string.bundle_app_string_array));
		//final ArrayList<Integer> selectedApps = new ArrayList<Integer>();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setTitle(R.string.options_app_select) //Dialogtitle
			   .setMultiChoiceItems(strings, boolSelectedApps,
				new DialogInterface.OnMultiChoiceClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				try{
					if(isChecked){
						selectedApps.put(strings[which], "true");
					} else {
						selectedApps.put(strings[which], "false");
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
				
			}
		})
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dataSet.setSelectedApps(selectedApps);
			}
		})
		.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		return builder.create();
	}
}
