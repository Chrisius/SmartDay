package com.example.smartday;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;

public class MainActivity extends Activity implements SectionListFragment.OnSectionSelectedListener, OptionsListFragment.OnOptionSelectedListener{
	int position = 0;
	FragmentManager fm = getFragmentManager();
	OptionsListFragment optionsListFragment = new OptionsListFragment();
	SectionMapFragment sectionMapFragment = new SectionMapFragment();
	SectionChartFragment sectionChartFragment = new SectionChartFragment();
	SectionTimelineFragment sectionTimelineFragment  = new SectionTimelineFragment();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if(savedInstanceState != null)
        	return;
     
        fm.beginTransaction().add(R.id.options_fragment_container, optionsListFragment).commit();
        fm.beginTransaction().add(R.id.section_fragment_container, sectionMapFragment).commit();
    }
    
    public void onSectionSelected(int pos){    	
    	if(position  == pos)
    		return;
    	position = pos;
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
    	
    }
    
}
