package com.zehjot.smartday;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zehjot.smartday.data_access.DataSet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class TimeLineDetailView extends View {
	private JSONObject jObj;
	private Paint mTextPaint;
	private JSONObject colors;

	public TimeLineDetailView(Context context) {
		super(context);
		mTextPaint = new Paint();
		mTextPaint.setTextSize(18);
		mTextPaint.setColor(getResources().getColor(android.R.color.white));
	}
	@Override
	protected void onDraw(Canvas canvas) {
	}
	
	public void setData(JSONObject jObj){
		if(jObj == null)
			return;
		this.jObj = jObj;
		/**
		 * Get App Bar Length
		 */
		
		/**
		 * GetColors for Bars
		 */
		if(colors==null)
			colors=new JSONObject();
		JSONObject rawObj =	DataSet.getInstance(getContext()).getColorsOfApps(jObj);
		try {
			JSONArray jArray = rawObj.getJSONArray("colors");
			String appName;
			int appColor;
			JSONObject color;
			for(int i = 0; i<jArray.length();i++){
				color = jArray.getJSONObject(i);
				appName = color.getString("app");
				appColor = color.getInt("color");
				colors.put(appName, appColor);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}	
	}
}
