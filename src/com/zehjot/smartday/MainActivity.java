package com.zehjot.smartday;

import com.zehjot.smartday.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;

public class MainActivity extends Activity 
		implements SectionListFragment.OnSectionSelectedListener, OptionsListFragment.OnOptionSelectedListener,
		DatePickerFragment.OnDateChosenListener{
	private FragmentManager fm = getFragmentManager();
	private OptionsListFragment optionsListFragment = new OptionsListFragment();
	private SectionMapFragment sectionMapFragment = new SectionMapFragment();
	private SectionChartFragment sectionChartFragment = new SectionChartFragment();
	private SectionTimelineFragment sectionTimelineFragment  = new SectionTimelineFragment();
	private Draw draw = new Draw();
	//private Option option = null;
	private DataSet dataSet = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if(savedInstanceState != null)
        	return;
        
        fm.beginTransaction().add(R.id.options_fragment_container, optionsListFragment).commit();
        fm.beginTransaction().add(R.id.section_fragment_container, sectionMapFragment).commit();
        //option = Option.getInstance(this);
        dataSet = DataSet.getInstance(this);
    }
    
    public void onSectionSelected(int pos){
    	dataSet.getApps();
    	switch (pos) {
		case 0:
			((OptionsListFragment) fm.findFragmentById(R.id.options_fragment_container)).updateOptions(pos); 	//Updates the displayed optionsList
			fm.beginTransaction().replace(R.id.section_fragment_container, sectionMapFragment).commit();		//Switches the fragments
			break;
		case 1:
			((OptionsListFragment) fm.findFragmentById(R.id.options_fragment_container)).updateOptions(pos);
			fm.beginTransaction().replace(R.id.section_fragment_container, sectionChartFragment).commit();
			break;
		case 2:
			((OptionsListFragment) fm.findFragmentById(R.id.options_fragment_container)).updateOptions(pos);
			fm.beginTransaction().replace(R.id.section_fragment_container, sectionTimelineFragment).commit();
			break;
		default:
			break;
		}
    }
    
    public void onOptionSelected(int pos){
    	DatePickerFragment date = new DatePickerFragment();
    	SelectAppsDialogFragment apps = new SelectAppsDialogFragment();
    	switch (pos) {
		case 0:
			date.show(fm, getString(R.string.datepicker));
			break;
		case 1:
			apps.show(fm, getString(R.string.datepicker));
			break;
		default:
			break;
		}
    }
    
    public void onDateChosen(int year, int month, int day){
    	dataSet.setSelectedDate(year, month, day);
    	((OptionsListFragment) fm.findFragmentById(R.id.options_fragment_container)).updateDate();
    }

    
}
