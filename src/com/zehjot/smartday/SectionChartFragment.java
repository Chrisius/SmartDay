package com.zehjot.smartday;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import com.zehjot.smartday.R;

import android.app.Fragment;
//import android.content.Context;
//import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class SectionChartFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return inflater.inflate(R.layout.section_chart_fragment, container, false);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		draw();
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	private void draw(){
		draw3();
		draw1();
		draw2();	
		draw4();
		draw5();
	}
	private void draw1(){
		CategorySeries categories = new CategorySeries("Number1");
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
					
		LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.chart_1);
		layout.addView(chartView);
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
}
