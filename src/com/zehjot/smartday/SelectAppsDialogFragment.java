package com.zehjot.smartday;

import java.util.ArrayList;

import com.zehjot.smartday.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class SelectAppsDialogFragment extends DialogFragment {
	private DataSet dataSet = null;
	@Override
	public Dialog onCreateDialog(Bundle saved){
		dataSet = DataSet.getInstance(getActivity());
		final ArrayList<Integer> selectedApps = new ArrayList<Integer>();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setTitle(R.string.options_app_select) //Dialogtitle
			   .setMultiChoiceItems(R.array.months, dataSet.getSelectedApps(), 
				new DialogInterface.OnMultiChoiceClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				if(isChecked){
					selectedApps.add(which);
				} else if (selectedApps.contains(which)){
					selectedApps.remove(Integer.valueOf(which));
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
