package com.example.smartday;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;

public class MainActivity extends Activity 
		implements SectionListFragment.OnSectionSelectedListener, OptionsListFragment.OnOptionSelectedListener,
		DatePickerFragment.OnDateChosenListener{
	FragmentManager fm = getFragmentManager();
	OptionsListFragment optionsListFragment = new OptionsListFragment();
	SectionMapFragment sectionMapFragment = new SectionMapFragment();
	SectionChartFragment sectionChartFragment = new SectionChartFragment();
	SectionTimelineFragment sectionTimelineFragment  = new SectionTimelineFragment();
	Option option;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if(savedInstanceState != null)
        	return;
        fm.beginTransaction().add(R.id.options_fragment_container, optionsListFragment).commit();
        fm.beginTransaction().add(R.id.section_fragment_container, sectionMapFragment).commit();
        option = new Option(this);
        option.init();
    }
    
    public void onSectionSelected(int pos){    	
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
    	option.setDate(year, month, day);
    	((OptionsListFragment) fm.findFragmentById(R.id.options_fragment_container)).updateDate();
    }
    /*
    public class Option{
    	Activity activity;
    	SharedPreferences sharedPreferences;
    	SharedPreferences.Editor editor;
    	
    	public Option(Activity act){
    		activity = act;
    		sharedPreferences = activity.getPreferences(MainActivity.MODE_PRIVATE);
    		editor = sharedPreferences.edit();
    	}
    	
    	public void setDate(int year,  int month, int day){
    		editor.putString(getString(R.string.date), day+". "+getResources().getStringArray(R.array.months)[month]+" "+year);
    		editor.commit();
    	}
    	
    	public void setDefault(){
    		editor.putString(getString(R.string.date), Calendar.getInstance().toString());
    	}
    }*/
    
}
