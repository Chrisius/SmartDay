package com.zehjot.smartday;

import java.util.ArrayList;
import java.util.List;

public class DataSet {
	private static DataSet instance = null;
	private static Option options = null;
	
	private List<Pair> data = new ArrayList<Pair>();
	
	protected DataSet(){
		
	}
	
	public static DataSet getInstance(){
		if(instance == null){
			instance = new DataSet();
			options = new Option();
			instance.initTestData();
		}
		return instance;
	}
	
	public List<Pair> getApps(){
		int[] date = options.getSelectedDateAsArray();
		return getAppsAtDate(date[0],date[1],date[2]);
	}
	
	public List<Pair> getAppsAtDate(int year, int month, int day){
		return null;
	}
	
	private void initTestData(){
		data.add(new Pair("Facebook",0.4));
		data.add(new Pair("Whatsapp",0.2));
		data.add(new Pair("Facebook",1.0));
		data.add(new Pair("Mail",3.0));
		data.add(new Pair("Reader",0.4));
		data.add(new Pair("Facebook",0.84));
		data.add(new Pair("Google",1.2));
		data.add(new Pair("PhoneCall",1.4));
		data.add(new Pair("Google",0.7));
		data.add(new Pair("Facebook",1.2));
	}
	
	public class Pair{
		public final String app;
		public final double duration;
		
		public Pair(String a, double d){
			this.app = a;
			this.duration = d;
		}
		
	}
}






















