package com.zehjot.smartday;

import java.util.Arrays;
import java.util.Comparator;

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
	private float textSize;
	private JSONObject colors;
	private long totalDuration;
	private JSONObject[] orderedApps;

	public TimeLineDetailView(Context context) {
		super(context);
		textSize = 18;
		
		mTextPaint = new Paint();
		mTextPaint.setTextSize(textSize);
		mTextPaint.setColor(getResources().getColor(android.R.color.white));
	}
	@Override
	protected void onDraw(Canvas canvas) {
		float xpad = (float) (getPaddingLeft()+getPaddingRight());
		float ypad = (float) (getPaddingTop()+getPaddingBottom());
		for(int i=0;i<orderedApps.length;i++){
			canvas.drawText(orderedApps[i].optString("app", "Error"), xpad+20, ypad+(i+1)*textSize+(textSize/4.f)*i, mTextPaint);			
		}
	}
	
	public void setData(JSONObject jObj){
		if(jObj == null)
			return;
		this.jObj = jObj;
		/**
		 * Get App Bar Length
		 */
		try{
			JSONArray apps = jObj.getJSONArray("result");
			totalDuration=0;
			orderedApps = new JSONObject[apps.length()];
			for(int i=0;i<apps.length();i++){
				totalDuration += apps.getJSONObject(i).optLong("duration", 0);
				orderedApps[i] = (new JSONObject()
					.put("app", apps.getJSONObject(i).getString("app"))
					.put("duration", apps.getJSONObject(i).optLong("duration", 0))
				);
			}
			
			Arrays.sort(orderedApps, new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject lhs, JSONObject rhs) {
					int i= ((Long)rhs.optLong("duration", 0)).compareTo(lhs.optLong("duration",0));
					return i;
				}
			});
			
			for(int i=0;i<orderedApps.length;i++){
				orderedApps[i].put("barLength",orderedApps[i].getLong("duration")/totalDuration);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
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
