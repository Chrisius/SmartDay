package com.zehjot.smartday;

import java.util.Arrays;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zehjot.smartday.data_access.DataSet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;

public class TimeLineDetailView extends View {
	private JSONObject jObj;
	private Paint mTextPaint;
	private float textSize;
	private JSONObject colors;
	private float totalDuration;
	private float longestDuration;
	private JSONObject[] orderedApps;
	private int pxLongestWord = 0;
	private int maxBarLength;

	private Paint mRectanglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private String selectedApp;
	
	private GestureDetector tapDetector;
	
	public TimeLineDetailView(Context context) {
		super(context);
		textSize = 18;
		
		
		mTextPaint = new Paint();
		mTextPaint.setTextSize(textSize);
		mTextPaint.setColor(getResources().getColor(android.R.color.white));
		
		tapDetector = new GestureDetector(getContext(), new TapListener());
		
		selectedApp ="";
	}
/*
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//The Specified Mode (Exactly=hard coded pixels, at_most=match_parent, unspecified=wrap_content)
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		int width;
		int height;
		
		switch (widthMode) {
		case MeasureSpec.EXACTLY:
			width = widthSize;
			break;
		case MeasureSpec.AT_MOST:
			width = widthSize;
			break;
		case MeasureSpec.UNSPECIFIED:
			width = 0;
			break;
		default:
			width = 0;
			break;
		}
		
		switch (heightMode) {
		case MeasureSpec.EXACTLY:
			height = heightSize;
			break;
		case MeasureSpec.AT_MOST:
			height = Math.min(heightSize,300);
			break;
		case MeasureSpec.UNSPECIFIED:
			height = 300;
			break;
		default:
			height = 300;
			break;
		}	
		setMeasuredDimension(width, height);
	}
	*/
	@Override
	protected void onDraw(Canvas canvas) {
		float xpad = (float) (getPaddingLeft()+getPaddingRight());
		float ypad = (float) (getPaddingTop()+getPaddingBottom());
		String appName;
		float relativeBarLength;
		for(int i=0;i<orderedApps.length;i++){
			if(orderedApps[i].optString("app").equals(selectedApp)){
				mRectanglePaint.setColor(getResources().getColor(android.R.color.holo_blue_light));
				canvas.drawRect(xpad+15, ypad+(i)*textSize+(textSize/4.f)*i, xpad+20+pxLongestWord, ypad+(i+1)*textSize+(textSize/4.f)*(i+1), mRectanglePaint);
			}
			canvas.drawText(orderedApps[i].optString("app", "Error"), xpad+20, ypad+(i+1)*textSize+(textSize/4.f)*i, mTextPaint);
			
			appName = orderedApps[i].optString("app", "Error");
			relativeBarLength = orderedApps[i].optLong("duration")/longestDuration;
			mRectanglePaint.setColor(colors.optInt(appName));
			canvas.drawRect(
					xpad+pxLongestWord+20,
					ypad+(i)*textSize+(textSize/4.f)*i+textSize*0.1f,
					xpad+pxLongestWord+20+relativeBarLength*maxBarLength,
					ypad+(i+1)*textSize+(textSize/4.f)*i+textSize*0.1f,
					mRectanglePaint);
			float percent = ((float)orderedApps[i].optLong("duration")/totalDuration)*100.f;
			percent = Math.round(percent*100.f);
			percent /= 100.f;
			canvas.drawText(percent+"%", xpad+20+10+pxLongestWord+relativeBarLength*maxBarLength, ypad+(i+1)*textSize+(textSize/4.f)*i, mTextPaint);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		tapDetector.onTouchEvent(event);
		return true;
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
			longestDuration=0;
			orderedApps = new JSONObject[apps.length()];
			for(int i=0;i<apps.length();i++){
				totalDuration += apps.getJSONObject(i).optLong("duration", 0);
				orderedApps[i] = (new JSONObject()
					.put("app", apps.getJSONObject(i).getString("app"))
					.put("duration", apps.getJSONObject(i).optLong("duration", 0))
				);
				
				
				Rect bounds = new Rect();
				String text = apps.getJSONObject(i).getString("app");
				mTextPaint.getTextBounds(text, 0, text.length(), bounds);
				if(bounds.width()>pxLongestWord){
					pxLongestWord = bounds.width();
				}
				
				if(apps.getJSONObject(i).optLong("duration", 0)>longestDuration){
					longestDuration = apps.getJSONObject(i).optLong("duration", 0);
				}
			}
			
			Arrays.sort(orderedApps, new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject lhs, JSONObject rhs) {
					int i= ((Long)rhs.optLong("duration", 0)).compareTo(lhs.optLong("duration",0));
					return i;
				}
			});
			
			for(int i=0;i<orderedApps.length;i++){
				float length = orderedApps[i].getLong("duration")/longestDuration;
				orderedApps[i].put("barLength",length);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Rect bounds = new Rect();
		String text = "99.99%";
		mTextPaint.getTextBounds(text, 0, text.length(), bounds);
		int percentageSize = bounds.width();
		int offset = 10 ;
		
		maxBarLength = (int)(getWidth()/2.f)-pxLongestWord-percentageSize-offset;
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
		getParent().requestLayout();
		if(orderedApps!=null){
			int height = (int) ((orderedApps.length)*textSize+(textSize/4.f)*orderedApps.length);
			this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height));
			//requestLayout();
		}
		invalidate();
	}
	
	private class TapListener extends GestureDetector.SimpleOnGestureListener{
		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return true;
		}
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			setId(1);
			LinearLayout linearLayout = (LinearLayout)getParent();
			((TimeLineView)linearLayout.getChildAt(0)).selectApp("");
			linearLayout.removeView(linearLayout.findViewById(1));
			return true;
		}
		
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return true;
		}
	}
	public void selectApp(String app){
		selectedApp = app;
		invalidate();
	}
	
}
