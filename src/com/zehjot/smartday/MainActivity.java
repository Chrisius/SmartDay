package com.zehjot.smartday;

import java.io.File;

import org.json.JSONObject;

import com.zehjot.smartday.R;
import com.zehjot.smartday.data_access.DataSet;
import com.zehjot.smartday.helper.Security;
import com.zehjot.smartday.helper.Utilities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentManager;

public class MainActivity extends Activity 
		implements OptionsListFragment.OnOptionSelectedListener, DataSet.onDataAvailableListener,
		DatePickerFragment.OnDateChosenListener{
	private FragmentManager fm;
	private OptionsListFragment optionsListFragment;
	private DataSet dataSet;
	private boolean isRunning = true;
	
    public boolean isRunning(){
		return isRunning;
	}
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm = getFragmentManager();
        dataSet = DataSet.getInstance(this);          

        if(savedInstanceState != null){
        	optionsListFragment = (OptionsListFragment) fm.findFragmentByTag("optionsList");
        }else{
	    	optionsListFragment = new OptionsListFragment();
	    	fm.beginTransaction().add(R.id.options_fragment_container, optionsListFragment,"optionsList").commit();
        }
        /**
         * Set Up Tabs
         */
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        Tab tab = actionBar.newTab();
        tab.setText("MapView NoR.");
        tab.setTabListener(new TabListener<SectionMapFragment>(this, "mapView", SectionMapFragment.class, optionsListFragment));
        actionBar.addTab(tab);
        
        tab = actionBar.newTab();
        tab.setText("ChartView NoR.");
        tab.setTabListener(new TabListener<SectionChartFragment>(this, "chartView", SectionChartFragment.class, optionsListFragment));
        actionBar.addTab(tab);
        
        tab = actionBar.newTab();
        tab.setText("Timeline NoR.");
        tab.setTabListener(new TabListener<SectionTimelineFragment>(this, "timeline", SectionTimelineFragment.class, optionsListFragment));
        actionBar.addTab(tab);
        
        if(savedInstanceState != null){
        	actionBar.setSelectedNavigationItem(0);
        	actionBar.setSelectedNavigationItem(1);
        	actionBar.setSelectedNavigationItem(2);
        	actionBar.setSelectedNavigationItem(savedInstanceState.getInt(getString(R.string.start_view)));
        }
    }
    @Override
    public void onResume(){
    	super.onResume();
    	isRunning = true;
    	DataSet.updateActivity(this);
    }
    
    @Override
    public void onStop(){
    	isRunning = false;
    	super.onStop();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);    	
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	super.onOptionsItemSelected(item);
    	switch (item.getItemId()) {
		case R.id.action_blacklist:
			dataSet.getAllApps(this);
			break;
		case R.id.action_color_apps:
			break;
		case R.id.action_delete_files:
			String[] list = getFilesDir().list();
			for(int i=0; i<list.length;i++){
			File file = new File(getFilesDir(),list[i]);
				if(file.equals(new File(getFilesDir(),Security.sha1(getString(R.string.user_file)))))
					Utilities.showDialog(item.toString()+item.getItemId(), this);
				else
					file.delete();
			
			}
			break;
		case R.id.action_new_user:
			File file = new File(getFilesDir(),Security.sha1(getString(R.string.user_file)));
			file.delete();
			dataSet.createNewUser();
			break;
		default:
			break;
		}
    	//Utilities.showDialog(item.toString()+item.getItemId(), this);
    	return true;
    }
    
    
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(getString(R.string.start_view), getActionBar().getSelectedNavigationIndex());
    }
    @Override
    public void onDestroy(){
    	super.onDestroy();
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
    
	@Override
	public void onDataAvailable(JSONObject jObj, String request) {
		if(request=="allApps"){
			SelectAppsDialogFragment ignoreAppsDialog = new SelectAppsDialogFragment();
			ignoreAppsDialog.setStrings(Utilities.jObjValuesToArrayList(jObj).toArray(new String[0]));
			ignoreAppsDialog.setMode(SelectAppsDialogFragment.IGNORE_APPS);
			ignoreAppsDialog.show(fm, "nada");
		}
	}

}
