package com.zehjot.smartday;

import org.json.JSONArray;
import org.json.JSONException;

import yuku.ambilwarna.AmbilWarnaDialog;

import com.zehjot.smartday.data_access.DataSet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ColorPickerDialog extends DialogFragment {
	String[] appNames = {};
	int[] colors ={};

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		ScrollView view = new ScrollView(getActivity());
		view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT));
		//view.setFillViewport(true);
		
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		layout.setOrientation(LinearLayout.VERTICAL);
		view.addView(layout);
		
		TextView textView = null;
		
		JSONArray colorsOfApps;
		try {
			colorsOfApps = DataSet.getInstance(getActivity()).getColorsOfApps().getJSONArray("colors");
			appNames = new String[colorsOfApps.length()];
			colors = new int[colorsOfApps.length()];
			for(int i=0; i<colorsOfApps.length();i++){
				appNames[i] = colorsOfApps.getJSONObject(i).getString("app");
				colors[i] = colorsOfApps.getJSONObject(i).getInt("color");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		for(int i =0; i< appNames.length; i++){
			textView = new TextView(getActivity());
			textView.setText(appNames[i]);
			textView.setGravity(Gravity.CENTER);
			textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			textView.setTextSize(18);
			textView.setOnClickListener(new View.OnClickListener() {		
						@Override
						public void onClick(View v) {
							String appName = ((TextView)v).getText().toString();
							try{
								for(int i=0; i<DataSet.getInstance(getActivity()).getColorsOfApps().getJSONArray("colors").length();i++){
									if(DataSet.getInstance(getActivity()).getColorsOfApps().getJSONArray("colors").getJSONObject(i).getString("app").equals(appName)){
										AmbilWarnaDialog dialog = new AmbilWarnaDialog(getActivity(), DataSet.getInstance(getActivity()).getColorsOfApps().getJSONArray("colors").getJSONObject(i).getInt("color"), null);
										dialog.show();
									}
								}								
							}catch(JSONException e){
								
							}
						}
					}			
			);
			textView.setPadding(0, 5, 0, 5);
			layout.addView(textView);
		}
		builder.setTitle("test");
		builder.setView(view);
		return builder.create();
	}
	
}
