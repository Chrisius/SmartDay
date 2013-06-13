package com.zehjot.smartday;

import com.zehjot.smartday.R;
import com.zehjot.smartday.data_access.DataSet;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;

public class MainActivity extends Activity 
		implements SectionListFragment.OnSectionSelectedListener, OptionsListFragment.OnOptionSelectedListener,
		DatePickerFragment.OnDateChosenListener{
	private FragmentManager fm;
	private OptionsListFragment optionsListFragment;
	private SectionMapFragment sectionMapFragment;
	private SectionChartFragment sectionChartFragment;
	private SectionTimelineFragment sectionTimelineFragment;
	private DataSet dataSet;
	private Bundle args;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm = getFragmentManager();
        dataSet = DataSet.getInstance(this);
        args = new Bundle();
        if(savedInstanceState != null){
        	args.putInt(getString(R.string.start_view), savedInstanceState.getInt(getString(R.string.start_view)));
        	optionsListFragment = (OptionsListFragment) fm.findFragmentByTag("optionsList");
        	sectionMapFragment = (SectionMapFragment) fm.findFragmentByTag("sectionMap");
        	sectionChartFragment = (SectionChartFragment) fm.findFragmentByTag("sectionChart");
        	sectionTimelineFragment = (SectionTimelineFragment) fm.findFragmentByTag("sectionTimeline");
        	onSectionSelected(args.getInt(getString(R.string.start_view)));
        	//showView(args.getInt(getString(R.string.start_view)));
//        	fm.beginTransaction().hide(sectionChartFragment).commit();
//        	fm.beginTransaction().hide(sectionMapFragment).commit();
//        	fm.beginTransaction().hide(sectionTimelineFragment).commit();
        	return;
        }
    	optionsListFragment = new OptionsListFragment();
    	sectionMapFragment = new SectionMapFragment();
    	sectionChartFragment = new SectionChartFragment();
    	sectionTimelineFragment  = new SectionTimelineFragment();
        setStartView(1);
    }
    @Override
    public void onResume(){
    	super.onResume();
    	DataSet.updateActivity(this);
    	//dataSet.init();
    	//dataSet.initUser();
    }
    
    @Override
    public void onStop(){
    	super.onStop();
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(getString(R.string.start_view),args.getInt(getString(R.string.start_view)));
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    }
    
    public void onSectionSelected(int pos){
    	args.putInt(getString(R.string.start_view), pos);
    	switch (pos) {
		case 0:
			optionsListFragment.updateOptions(pos); 	//Updates the displayed optionsList
//			fm.beginTransaction().hide(sectionChartFragment).commit();
//			fm.beginTransaction().hide(sectionTimelineFragment).commit();
//			fm.beginTransaction().show(sectionMapFragment).commit();
			//fm.beginTransaction().replace(R.id.section_fragment_container, sectionMapFragment).commit();		//Switches the fragments
			break;
		case 1:
			optionsListFragment.updateOptions(pos);
//			fm.beginTransaction().hide(sectionMapFragment).commit();
//			fm.beginTransaction().hide(sectionTimelineFragment).commit();
//			fm.beginTransaction().show(sectionChartFragment).commit();
			//fm.beginTransaction().replace(R.id.section_fragment_container, sectionChartFragment).commit();
			break;
		case 2:
			optionsListFragment.updateOptions(pos);
//			fm.beginTransaction().hide(sectionChartFragment).commit();
//			fm.beginTransaction().hide(sectionMapFragment).commit();
//			fm.beginTransaction().show(sectionTimelineFragment).commit();
			//fm.beginTransaction().replace(R.id.section_fragment_container, sectionTimelineFragment).commit();
			break;
		default:
			break;
		}
    	showView(pos);
    }
    
    public void onOptionSelected(int pos){
    	DatePickerFragment date = new DatePickerFragment();
    	
    	switch (pos) {
		case 0:
			date.show(fm, getString(R.string.datepicker));
			break;
		case 1:
//			SelectAppsDialogFragment apps = new SelectAppsDialogFragment();
//			String[] foo = {"asd","123","sfhpio"};
//			apps.setStrings(foo);
//			apps.show(fm, getString(R.string.datepicker));
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
    	
    	
    	fm.beginTransaction().add(R.id.section_fragment_container, sectionMapFragment,"sectionMap").commit();
    	//fm.beginTransaction().hide(sectionMapFragment).commit();
		fm.beginTransaction().add(R.id.section_fragment_container, sectionTimelineFragment,"sectionTimeline").commit();
    	//fm.beginTransaction().hide(sectionTimelineFragment).commit();
		fm.beginTransaction().add(R.id.section_fragment_container, sectionChartFragment,"sectionChart").commit();
    	//fm.beginTransaction().hide(sectionChartFragment).commit();
    	
    	//Create Section "Header" and fill container
    	SectionListFragment sectionHeader = new SectionListFragment();
        fm.beginTransaction().add(R.id.section_header_container, sectionHeader).commit();
        fm.beginTransaction().add(R.id.options_fragment_container, optionsListFragment,"optionsList").commit();
        
        //setup bundle with int for start position
    	args.putInt(getString(R.string.start_view), pos);
    	
    	//attach bundle to options and sectionHeader to container
    	optionsListFragment.setArguments(args);
    	sectionHeader.setArguments(args);
    	
        //set main view
    	showView(pos);/*
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
	    	args.putInt(getString(R.string.start_view), 0);
	        fm.beginTransaction().add(R.id.section_fragment_container, sectionMapFragment).commit();	
			break;
		}*/
    }
    private void showView(int pos){
    	switch (pos) {
		case 0:
			fm.beginTransaction().hide(sectionChartFragment).commit();
			fm.beginTransaction().hide(sectionTimelineFragment).commit();
			fm.beginTransaction().show(sectionMapFragment).commit();		
			break;
		case 1:
			fm.beginTransaction().hide(sectionMapFragment).commit();
			fm.beginTransaction().hide(sectionTimelineFragment).commit();
			fm.beginTransaction().show(sectionChartFragment).commit();
	        break;
		case 2:
			fm.beginTransaction().hide(sectionChartFragment).commit();
			fm.beginTransaction().hide(sectionMapFragment).commit();
			fm.beginTransaction().show(sectionTimelineFragment).commit();
	        break;
		default:
			fm.beginTransaction().hide(sectionChartFragment).commit();
			fm.beginTransaction().hide(sectionTimelineFragment).commit();
			fm.beginTransaction().show(sectionMapFragment).commit();	
			break;
		}
    }
}
