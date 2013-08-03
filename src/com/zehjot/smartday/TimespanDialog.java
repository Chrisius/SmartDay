package com.zehjot.smartday;



import java.util.Calendar;

import com.zehjot.smartday.data_access.DataSet;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TimespanDialog extends DialogFragment implements DatePickerFragment.OnDateChosenListener, View.OnClickListener {

	private LinearLayout linearLayout;
	private int startYear;
	private int startMonth;
	private int startDay;
	private int endYear;
	private int endMonth;
	private int endDay;
	private OnDateChosenListener listener;
	
	public interface OnDateChosenListener{
		void onDateChosen(int startYear,int startMonth,int startDay,int endYear,int endMonth,int endDay);
	}
	
	public void setListener(OnDateChosenListener listener){
		this.listener = listener;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle saved){
		long start = DataSet.getInstance(getActivity()).getSelectedDateStartAsTimestamp();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(start*1000);
		startYear = c.get(Calendar.YEAR);
		startMonth = c.get(Calendar.MONTH);
		startDay = c.get(Calendar.DAY_OF_MONTH);
		long end = DataSet.getInstance(getActivity()).getSelectedDateEndAsTimestamp();
		c.setTimeInMillis(end*1000);
		endYear = c.get(Calendar.YEAR);
		endMonth = c.get(Calendar.MONTH);
		endDay = c.get(Calendar.DAY_OF_MONTH);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		linearLayout = new LinearLayout(getActivity());
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		TextView tv1 = new TextView(getActivity());
		tv1.setText(" From: "+DataSet.getInstance(getActivity()).getSelectedDateStartAsString());
		tv1.setTextSize(22);
		tv1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		tv1.setId(101);
		tv1.setOnClickListener(this);
		TextView tv2 = new TextView(getActivity());
		tv2.setText("To: "+DataSet.getInstance(getActivity()).getSelectedDateEndAsString());
		tv2.setTextSize(22);
		tv2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		tv2.setId(102);
		tv2.setOnClickListener(this);
		linearLayout.addView(tv1,
				250,50);
		linearLayout.addView(tv2,
				250,50);
		builder.setTitle("select dates") //Dialogtitle
		.setView(linearLayout)
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				listener.onDateChosen(startYear, startMonth, startDay, endYear, endMonth, endDay);
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
	public void onDateChosen(int year, int month, int day, String whichDate) {
		if(whichDate.equals("start")){
			TextView view = (TextView) linearLayout.getChildAt(0);	
			view.setText("  From: "+day+". "+getActivity().getResources().getStringArray(R.array.months)[month]+" "+year);
			startDay = day;
			startMonth = month;
			startYear = year;
		}else{
			TextView view = (TextView) linearLayout.getChildAt(1);	
			view.setText("To: "+day+". "+getActivity().getResources().getStringArray(R.array.months)[month]+" "+year);
			endDay = day;
			endMonth = month;
			endYear = year;
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==101){
	    	DatePickerFragment dateEnd = new DatePickerFragment();
	    	dateEnd.setListener(this,"start");
	    	dateEnd.show(getFragmentManager(), getString(R.string.datepicker));			
		}else if(v.getId()==102){
	    	DatePickerFragment dateEnd = new DatePickerFragment();
	    	dateEnd.setListener(this,"end");
	    	dateEnd.show(getFragmentManager(), getString(R.string.datepicker));			
		}
	}
	
}
