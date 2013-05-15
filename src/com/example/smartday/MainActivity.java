package com.example.smartday;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;

public class MainActivity extends Activity implements SectionListFragment.OnSectionSelectedListener{
	int position = 0;
	FragmentManager fm = getFragmentManager();
	OptionsMapFragment optionsMapFragment = new OptionsMapFragment();
	OptionsChartFragment optionsChartFragment = new OptionsChartFragment();
	OptionsTimelineFragment optionsTimelineFragment = new OptionsTimelineFragment();
	SectionMapFragment sectionMapFragment = new SectionMapFragment();
	SectionChartFragment sectionChartFragment = new SectionChartFragment();
	SectionTimelineFragment sectionTimelineFragment  = new SectionTimelineFragment();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if(savedInstanceState != null)
        	return;
     
        fm.beginTransaction().add(R.id.options_fragment_container, optionsMapFragment).commit();
        fm.beginTransaction().add(R.id.section_fragment_container, sectionMapFragment).commit();
    }
    
    public void onSectionSelected(int pos){
    	if(position  == pos)
    		return;
    	position = pos;
    	switch (pos) {
		case 0:
			fm.beginTransaction().replace(R.id.options_fragment_container, optionsMapFragment).commit();
			fm.beginTransaction().replace(R.id.section_fragment_container, sectionMapFragment).commit();
			break;
		case 1:
			fm.beginTransaction().replace(R.id.options_fragment_container, optionsChartFragment).commit();
			fm.beginTransaction().replace(R.id.section_fragment_container, sectionChartFragment).commit();
			break;
		case 2:
			fm.beginTransaction().replace(R.id.options_fragment_container, optionsTimelineFragment).commit();
			fm.beginTransaction().replace(R.id.section_fragment_container, sectionTimelineFragment).commit();
			break;
		default:
			break;
		}
    }
    
}
