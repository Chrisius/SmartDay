package com.zehjot.smartday;

import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zehjot.smartday.R;
import com.zehjot.smartday.TabListener.OnUpdateListener;
import com.zehjot.smartday.data_access.DataSet;
import com.zehjot.smartday.data_access.DataSet.onDataAvailableListener;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SectionChartFragment extends Fragment implements onDataAvailableListener, OnUpdateListener{
	private GraphicalView chartView1=null;
	private DefaultRenderer renderer1=null;
	private CategorySeries categories1=null;
	private static double minTimeinPercent = 0.05;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return inflater.inflate(R.layout.section_chart_fragment, container, false);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		DataSet.getInstance(getActivity()).getApps(this);	
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	private void draw(String[] apps, double[] time, int[] colors){
		draw3();
		draw1(apps, time, colors);
		draw2();	
		//draw4();
		draw5();
	}
	
	private void draw1(String[] apps, double[] time, int[] colors){
		double totaltime = 0;
		DataSet dataset = DataSet.getInstance(getActivity());
		JSONObject selectedApps = dataset.getSelectedApps();
		for(int i=0; i < apps.length; i++){
			if(selectedApps.optBoolean(apps[i], true)){
				totaltime += time[i];
			}
		}
		totaltime = Math.round(totaltime*100.f);
		totaltime /=100;
		if(chartView1==null){
			double otherTime = 0;
			categories1 = new CategorySeries("Number1");
			renderer1 = new DefaultRenderer();
			for(int i=0; i < apps.length; i++){
				if(selectedApps.optBoolean(apps[i], true)){
					if(time[i]/totaltime>minTimeinPercent){
					SimpleSeriesRenderer r = new SimpleSeriesRenderer();
					categories1.add(apps[i], time[i]);
					r.setColor(colors[i]);
					renderer1.addSeriesRenderer(r);
					}else{
						otherTime += time[i];
					}
				}
			}
			if(otherTime > 0){
				SimpleSeriesRenderer r = new SimpleSeriesRenderer();
				otherTime = Math.round(otherTime*100.f);
				otherTime /=100;
				categories1.add("other", otherTime);
				r.setColor(DataSet.getInstance(getActivity()).getColorsOfApps().optInt("other"));
				renderer1.addSeriesRenderer(r);				
			}
			renderer1.setFitLegend(true);			
			renderer1.setDisplayValues(true);
			renderer1.setPanEnabled(false);
			renderer1.setClickEnabled(true);
			renderer1.setInScroll(true);
			renderer1.setChartTitle(""+totaltime);
			chartView1 = ChartFactory.getPieChartView(getActivity(), categories1, renderer1);	
			
			chartView1.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
			          SeriesSelection seriesSelection = chartView1.getCurrentSeriesAndPoint();
			          if (seriesSelection == null) {
			        	  
			          } else {
			        	  SimpleSeriesRenderer[] renederers = renderer1.getSeriesRenderers();
			        	  for(SimpleSeriesRenderer renderer : renederers){
			        		  renderer.setHighlighted(false);
			        	  }
			        	  //renderer1.getSeriesRendererAt(seriesSelection.getPointIndex()).get
			        	  //addDetails();
			        	  renderer1.getSeriesRendererAt(seriesSelection.getPointIndex()).setHighlighted(true);
			        	  chartView1.repaint();
			          }
					
				}
			});
			
			
			LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.chart_1);
			layout.addView(chartView1);
		}else{
			double otherTime = 0;
			categories1.clear();
			renderer1.removeAllRenderers();	
			for(int i=0; i < apps.length; i++){
				if(selectedApps.optBoolean(apps[i], true)){
					if(time[i]/totaltime>minTimeinPercent){
					SimpleSeriesRenderer r = new SimpleSeriesRenderer();
					categories1.add(apps[i], time[i]);
					r.setColor(colors[i]);
					renderer1.addSeriesRenderer(r);
					}else{
						otherTime += time[i];
					}
				}
			}
			if(otherTime > 0){
				SimpleSeriesRenderer r = new SimpleSeriesRenderer();
				otherTime = Math.round(otherTime*100.f);
				otherTime /=100;
				categories1.add("other", otherTime);
				r.setColor(DataSet.getInstance(getActivity()).getColorsOfApps().optInt("other"));
				renderer1.addSeriesRenderer(r);				
			}
			renderer1.setChartTitle(""+totaltime);
			chartView1.repaint();
		}
	}
	

	
	private void draw2(){
		CategorySeries categories = new CategorySeries("Number2");
		categories.add("Social", 12.0);
		categories.add("Productiv", 6.0);
		categories.add("Undefined", 8);
		
		int[] colors = {Color.BLUE, Color.GREEN, Color.RED};
		
		DefaultRenderer renderer = new DefaultRenderer();
		for(int color : colors){
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		
		renderer.setPanEnabled(false);
		renderer.setInScroll(true);
		
		GraphicalView chartView = ChartFactory.getPieChartView(getActivity(), categories, renderer);
					
		LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.chart_2);
		layout.addView(chartView);
	}
	private void draw3(){
		CategorySeries categories = new CategorySeries("Number3");
		categories.add("Social", 12.0);
		categories.add("Productiv", 6.0);
		categories.add("Undefined", 8);
		
		int[] colors = {Color.LTGRAY, Color.BLUE, Color.YELLOW};
		
		DefaultRenderer renderer = new DefaultRenderer();
		for(int color : colors){
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		
		renderer.setPanEnabled(false);
		renderer.setInScroll(true);
		
		GraphicalView chartView = ChartFactory.getPieChartView(getActivity(), categories, renderer);
					
		LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.chart_3);
		layout.addView(chartView);
	}
	private void draw4(){
		CategorySeries categories = new CategorySeries("Number12");
		categories.add("Social", 12.0);
		categories.add("Productiv", 6.0);
		categories.add("Undefined", 8);
		
		int[] colors = {Color.CYAN, Color.MAGENTA, Color.BLACK};
		
		DefaultRenderer renderer = new DefaultRenderer();
		for(int color : colors){
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		
		renderer.setPanEnabled(false);
		renderer.setInScroll(true);
		
		GraphicalView chartView = ChartFactory.getPieChartView(getActivity(), categories, renderer);
					
		LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.chart_4);
		layout.addView(chartView);
	}
	private void draw5(){
		CategorySeries categories = new CategorySeries("Number13");
		categories.add("Social", 12.0);
		categories.add("Productiv", 6.0);
		categories.add("Undefined", 8);
		
		int[] colors = {Color.CYAN, Color.MAGENTA, Color.BLACK};
		
		DefaultRenderer renderer = new DefaultRenderer();
		for(int color : colors){
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		
		renderer.setPanEnabled(false);
		renderer.setInScroll(true);
		
		GraphicalView chartView = ChartFactory.getPieChartView(getActivity(), categories, renderer);
					
		LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.chart_5);
		layout.addView(chartView);
	}
	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDataAvailable(JSONObject jObj, String request) {
		JSONArray jArray = null;
		String[] apps = {"No Data available"};
		double[] time = {1.0};
		try {
			jArray = jObj.getJSONArray("result");
			apps = new String[jArray.length()];
			time = new double[jArray.length()];
			for(int i=0; i<jArray.length(); i++){
				JSONObject app = jArray.getJSONObject(i);
				apps[i] = app.getString("app");
				time[i] = 0;
				JSONArray usages = app.getJSONArray("usage");
				for(int j=0; j<usages.length();j++){
					JSONObject usage = usages.getJSONObject(j);
					long start = usage.optLong("start", -1);
					long end = usage.optLong("end", -1);
					if(start!=-1 && end!=-1)
						time[i] += end-start;
				}
				time[i] /=60.f;
				time[i] = Math.round(time[i]*100.f);
				time[i] /=100;
			}	
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		JSONObject colorsOfApps = DataSet.getInstance(getActivity()).getColorsOfApps();
		Random rnd = new Random();
		int[] colors = new int[apps.length];
		try{
			for(int i=0; i<apps.length;i++){
				if(colorsOfApps.has(apps[i]))
					colors[i] = colorsOfApps.getInt(apps[i]);
				else{
					colors[i] = rnd.nextInt();
					colorsOfApps.put(apps[i], colors[i]);
				}
			}
			if(!colorsOfApps.has("other"))
				colorsOfApps.put("other", rnd.nextInt());	
			
			DataSet.getInstance(getActivity()).setColorsOfApps(colorsOfApps);
		}catch(JSONException e){
			
		}
		
		draw(apps,time, colors);
//		MyChart chart = new MyChart();
//		chart.draw(jObj, R.id.chart_1);
	}

	public void onUpdate() {
		DataSet.getInstance(getActivity()).getApps(this);		
	}
	
	private void addDetails(){
		LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.chart_4);
	    //LinearLayout layout = (LinearLayout) findViewById(R.id.info);


	    TextView valueTV = new TextView(getActivity());
	    valueTV.setText("hallo hallo");
	    valueTV.setLayoutParams(new LayoutParams(
	            LayoutParams.WRAP_CONTENT,
	            LayoutParams.WRAP_CONTENT));

	    linearLayout.addView(valueTV);
	}
	
	/*
	private class MyChart{
		private CategorySeries categories; 
		private DefaultRenderer renderer;
		private String[] apps;
		private JSONObject data;
		private double[] time;
		private int[] colors;
		private GraphicalView chartView;
		
		private void processData(JSONObject jObj){
			data = jObj;
			JSONArray jArray = null;
			String[] apps = {"No Data available"};
			time = new double[]{1.0};
			try {
				jArray = jObj.getJSONArray("result");
				apps = new String[jArray.length()];
				time = new double[jArray.length()];
				for(int i=0; i<jArray.length(); i++){
					JSONObject app = jArray.getJSONObject(i);
					apps[i] = app.getString("app");
					time[i] = 0;
					JSONArray usages = app.getJSONArray("usage");
					for(int j=0; j<usages.length();j++){
						JSONObject usage = usages.getJSONObject(j);
						long start = usage.optLong("start", -1);
						long end = usage.optLong("end", -1);
						if(start!=-1 && end!=-1)
							time[i] += end-start;
					}
					time[i] /=60.f;
					time[i] = Math.round(time[i]*100.f);
					time[i] /=100;
				}	
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			JSONObject colorsOfApps = DataSet.getInstance(getActivity()).getColorsOfApps();
			Random rnd = new Random();
			int[] colors = new int[apps.length];
			try{
				for(int i=0; i<apps.length;i++){
					if(colorsOfApps.has(apps[i]))
						colors[i] = colorsOfApps.getInt(apps[i]);
					else{
						colors[i] = rnd.nextInt();
						colorsOfApps.put(apps[i], colors[i]);
					}
				}
				if(!colorsOfApps.has("other"))
					colorsOfApps.put("other", rnd.nextInt());	
				
				DataSet.getInstance(getActivity()).setColorsOfApps(colorsOfApps);
			}catch(JSONException e){
				
			}
			this.apps = apps;
		}
		
		public void draw(JSONObject jObj, int drawContainer){
			
			double totaltime = 0;
			DataSet dataset = DataSet.getInstance(getActivity());
			JSONObject selectedApps = dataset.getSelectedApps();
			for(int i=0; i < apps.length; i++){
				if(selectedApps.optBoolean(apps[i], true)){
					totaltime += time[i];
				}
			}
			totaltime = Math.round(totaltime*100.f);
			totaltime /=100;
			if(chartView1==null){
				double otherTime = 0;
				categories = new CategorySeries("Number1");
				renderer = new DefaultRenderer();
				for(int i=0; i < apps.length; i++){
					if(selectedApps.optBoolean(apps[i], true)){
						if(time[i]/totaltime>minTimeinPercent){
						SimpleSeriesRenderer r = new SimpleSeriesRenderer();
						categories.add(apps[i], time[i]);
						r.setColor(colors[i]);
						renderer.addSeriesRenderer(i,r);
						}else{
							otherTime += time[i];
						}
					}
				}
				if(otherTime > 0){
					SimpleSeriesRenderer r = new SimpleSeriesRenderer();
					otherTime = Math.round(otherTime*100.f);
					otherTime /=100;
					categories.add("other", otherTime);
					r.setColor(DataSet.getInstance(getActivity()).getColorsOfApps().optInt("other"));
					renderer.addSeriesRenderer(r);				
				}
				renderer.setFitLegend(true);			
				renderer.setDisplayValues(true);
				renderer.setPanEnabled(false);
				renderer.setClickEnabled(true);
				renderer.setInScroll(true);
				renderer.setChartTitle(""+totaltime);
				chartView1 = ChartFactory.getPieChartView(getActivity(), categories, renderer);	
				
				chartView1.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
				          SeriesSelection seriesSelection = chartView1.getCurrentSeriesAndPoint();
				          if (seriesSelection == null) {
				        	  
				          } else {
				        	  SimpleSeriesRenderer[] renederers = renderer.getSeriesRenderers();
				        	  for(SimpleSeriesRenderer renderer : renederers){
				        		  renderer.setHighlighted(false);
				        	  }
				        	  //renderer1.getSeriesRendererAt(seriesSelection.getPointIndex()).get
				        	  //addDetails();
				        	  renderer.getSeriesRendererAt(seriesSelection.getPointIndex()).setHighlighted(true);
				        	  chartView1.repaint();
				          }
						
					}
				});
				
				
				LinearLayout layout = (LinearLayout) getActivity().findViewById(drawContainer);
				layout.addView(chartView1);
			}else{
				double otherTime = 0;
				categories.clear();
				renderer.removeAllRenderers();	
				for(int i=0; i < apps.length; i++){
					if(selectedApps.optBoolean(apps[i], true)){
						if(time[i]/totaltime>minTimeinPercent){
						SimpleSeriesRenderer r = new SimpleSeriesRenderer();
						categories.add(apps[i], time[i]);
						r.setColor(colors[i]);
						renderer.addSeriesRenderer(r);
						}else{
							otherTime += time[i];
						}
					}
				}
				if(otherTime > 0){
					SimpleSeriesRenderer r = new SimpleSeriesRenderer();
					otherTime = Math.round(otherTime*100.f);
					otherTime /=100;
					categories.add("other", otherTime);
					r.setColor(DataSet.getInstance(getActivity()).getColorsOfApps().optInt("other"));
					renderer.addSeriesRenderer(r);				
				}
				renderer.setChartTitle(""+totaltime);
				chartView1.repaint();
			}
		}
	}*/
}
