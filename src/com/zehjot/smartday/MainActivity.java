package com.zehjot.smartday;

import com.google.android.gms.maps.MapFragment;
import com.zehjot.smartday.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;

public class MainActivity extends Activity 
		implements SectionListFragment.OnSectionSelectedListener, OptionsListFragment.OnOptionSelectedListener,
		DatePickerFragment.OnDateChosenListener{
	private FragmentManager fm = getFragmentManager();
	private OptionsListFragment optionsListFragment = new OptionsListFragment();
	private MapFragment sectionMapFragment = new MapFragment();
	private SectionChartFragment sectionChartFragment = new SectionChartFragment();
	private SectionTimelineFragment sectionTimelineFragment  = new SectionTimelineFragment();
	private DataSet dataSet = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState != null)
        	return;
        setStartView(1);
        dataSet = DataSet.getInstance(this);
    }
    
    public void onSectionSelected(int pos){
    	dataSet.getApps();
    	switch (pos) {
		case 0:
			optionsListFragment.updateOptions(pos); 	//Updates the displayed optionsList
			fm.beginTransaction().hide(sectionChartFragment).commit();
			fm.beginTransaction().hide(sectionTimelineFragment).commit();
			fm.beginTransaction().show(sectionMapFragment).commit();
			//fm.beginTransaction().replace(R.id.section_fragment_container, sectionMapFragment).commit();		//Switches the fragments
			break;
		case 1:
			optionsListFragment.updateOptions(pos);
			fm.beginTransaction().hide(sectionMapFragment).commit();
			fm.beginTransaction().hide(sectionTimelineFragment).commit();
			fm.beginTransaction().show(sectionChartFragment).commit();
			//fm.beginTransaction().replace(R.id.section_fragment_container, sectionChartFragment).commit();
			break;
		case 2:
			optionsListFragment.updateOptions(pos);
			fm.beginTransaction().hide(sectionChartFragment).commit();
			fm.beginTransaction().hide(sectionMapFragment).commit();
			fm.beginTransaction().show(sectionTimelineFragment).commit();
			//fm.beginTransaction().replace(R.id.section_fragment_container, sectionTimelineFragment).commit();
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
    private void setStartView(int pos){
    	
    	
    	fm.beginTransaction().add(R.id.section_fragment_container, sectionMapFragment).commit();
    	fm.beginTransaction().hide(sectionMapFragment).commit();
		fm.beginTransaction().add(R.id.section_fragment_container, sectionTimelineFragment).commit();
    	fm.beginTransaction().hide(sectionTimelineFragment).commit();
		fm.beginTransaction().add(R.id.section_fragment_container, sectionChartFragment).commit();
    	fm.beginTransaction().hide(sectionChartFragment).commit();
    	
    	
    	
    	
    	
    	
    	//Create Section "Header" and fill container
    	SectionListFragment sectionHeader = new SectionListFragment();
        fm.beginTransaction().add(R.id.section_header_container, sectionHeader).commit();
        fm.beginTransaction().add(R.id.options_fragment_container, optionsListFragment).commit();
        
        //setup bundle with int for start position
    	Bundle args = new Bundle();
    	args.putInt(getString(R.string.start_position), pos);
    	
    	//attach bundle to options and sectionHeader to container
    	optionsListFragment.setArguments(args);
    	sectionHeader.setArguments(args);
    	
        //set main view
    	switch (pos) {
		case 0:
	        fm.beginTransaction().show(sectionMapFragment).commit();			
			break;
		case 1:
	    	fm.beginTransaction().show(sectionChartFragment).commit();
	        break;
		case 2:
	        fm.beginTransaction().show(sectionTimelineFragment).commit();
	        break;
		default:
	    	args.putInt(getString(R.string.start_position), 0);
	        fm.beginTransaction().add(R.id.section_fragment_container, sectionMapFragment).commit();	
			break;
		}
    }
    
}
