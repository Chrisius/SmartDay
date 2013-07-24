package com.zehjot.smartday;

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zehjot.smartday.data_access.DataSet;
import com.zehjot.smartday.helper.Utilities;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Scroller;
/**
 * Dont put inside scrollview or else the timeline cant be scrolled and zooming works like crap
 *
 */
public class TimeLineView extends View {
	private JSONObject jObj= null;
	private Paint mTextPaint = new Paint();
	private Paint mSubTextPaint = new Paint();
	private Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint mRectanglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint mDebugTextPaint = new Paint();
	private int debugDrawCounter = 0;
	private Random rnd = new Random();
	private float mTextSize = 16.f;
	private float scaleFactor = 1.f;
	private ScaleGestureDetector zoomDetector;
	private GestureDetector scrollDetector;
	private float scrollX;
	private int zoomEnd=0;
	
	private Scroller mScroller;
	private ValueAnimator mScrollAnimator;
	
	private float width=0;
	private float offset=0;
	private float height=0;
	
	
	public TimeLineView(Context context) {
		super(context);
		
		int textColor = getResources().getColor(android.R.color.white);
		
		mTextPaint.setColor(textColor);
		mTextPaint.setTextSize(mTextSize);		
		mTextPaint.setTextAlign(Align.CENTER);

		mSubTextPaint.setColor(textColor);
		mSubTextPaint.setTextSize(mTextSize*.9f);		
		mSubTextPaint.setTextAlign(Align.CENTER);
		
		mDebugTextPaint.setColor(textColor);
		mDebugTextPaint.setTextSize(mTextSize);		
		
		mLinePaint.setColor(textColor);
		
		zoomDetector=new ScaleGestureDetector(getContext(), new ZoomListener());
		scrollDetector= new GestureDetector(getContext(), new PanListener());
		
		
        mScroller = new Scroller(getContext(), null, true);
        
        // The scroller doesn't have any built-in animation functions--it just supplies
        // values when we ask it to. So we have to have a way to call it every frame
        // until the fling ends. This code (ab)uses a ValueAnimator object to generate
        // a callback on every animation frame. We don't use the animated value at all.
        mScrollAnimator = ValueAnimator.ofFloat(0, 1);
        mScrollAnimator.addUpdateListener(new AnimatorTick());
        
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
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
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		//canvas.scale(scaleFactor, scaleFactor);
		canvas.translate(scrollX, 0);
		//canvas.drawColor(getResources().getColor(android.R.color.holo_green_light));
		float xpad = (float) (getPaddingLeft()+getPaddingRight());
		float ypad = (float) (getPaddingTop()+getPaddingBottom());
		height = this.getHeight();
		offset = height*0.2f;
		width = this.getWidth();
		width *= scaleFactor;///(width-width*24.f/25.f)*0.5f;
		width -= 2.f*offset;//*= 24.f/25.f;
		debugDrawCounter +=1;
		canvas.drawText("height="+this.getHeight()+" width="+this.getWidth()+" calls="+debugDrawCounter+" scale="+scaleFactor+" scrollX="+scrollX+" zoomEnd="+zoomEnd, xpad-scrollX, ypad+mTextSize, mDebugTextPaint);
		/**
		 * Line with hourdisplay
		 */
		for(int i=0;i<25;i++){
			canvas.drawLine(offset+xpad+i*width/24.f, ypad+height*0.81f, offset+xpad+i*width/24.f, ypad+height*0.77f, mLinePaint);
			if(i%2==0 || getWidth()*scaleFactor>=1400 || i==24)
				canvas.drawText(""+i+":00", offset+xpad+i*width/24.f, ypad+height*0.81f+mTextSize+2, mTextPaint);
			if(getWidth()*scaleFactor>=2878 && i!=24){
				canvas.drawText(""+i+":30", offset+xpad+(i+0.5f)*width/24.f, ypad+height*0.81f+mTextSize+2, mSubTextPaint);
				canvas.drawLine(offset+xpad+(i+0.5f)*width/24.f, ypad+height*0.81f, offset+xpad+(i+0.5f)*width/24.f, ypad+height*0.79f, mLinePaint);
			}
			if(getWidth()*scaleFactor>=5040 && i!=24){
				canvas.drawLine(offset+xpad+(i+0.25f)*width/24.f, ypad+height*0.81f, offset+xpad+(i+0.25f)*width/24.f, ypad+height*0.79f, mLinePaint);
				canvas.drawLine(offset+xpad+(i+0.75f)*width/24.f, ypad+height*0.81f, offset+xpad+(i+0.75f)*width/24.f, ypad+height*0.79f, mLinePaint);
				if(getWidth()*scaleFactor>=6500){
					canvas.drawText(""+i+":15", offset+xpad+(i+0.25f)*width/24.f, ypad+height*0.81f+mTextSize+2, mSubTextPaint);
					canvas.drawText(""+i+":45", offset+xpad+(i+0.75f)*width/24.f, ypad+height*0.81f+mTextSize+2, mSubTextPaint);
				}
			}
			if(getWidth()*scaleFactor>=8000 && i!=24){			
				for(int j=1; j<=14;j++){
					canvas.drawLine(offset+xpad+(i+0.0f+j/60.f)*width/24.f, ypad+height*0.81f, offset+xpad+(i+0.0f+j/60.f)*width/24.f, ypad+height*0.80f, mLinePaint);
					canvas.drawLine(offset+xpad+(i+0.25f+j/60.f)*width/24.f, ypad+height*0.81f, offset+xpad+(i+0.25f+j/60.f)*width/24.f, ypad+height*0.80f, mLinePaint);
					canvas.drawLine(offset+xpad+(i+0.5f+j/60.f)*width/24.f, ypad+height*0.81f, offset+xpad+(i+0.5f+j/60.f)*width/24.f, ypad+height*0.80f, mLinePaint);
					canvas.drawLine(offset+xpad+(i+0.75f+j/60.f)*width/24.f, ypad+height*0.81f, offset+xpad+(i+0.75f+j/60.f)*width/24.f, ypad+height*0.80f, mLinePaint);					
				}
			}
		}
		canvas.drawLine(offset+xpad, ypad+height*0.8f, offset+xpad+width, ypad+height*0.8f, mLinePaint);
		/**
		 * App Bars
		 */
		if(jObj!=null){
			JSONArray jArray;
			try {
				jArray = jObj.getJSONArray("result");		
				for(int i=0; i<jArray.length();i++){
					JSONObject app = jArray.getJSONObject(i);
					JSONArray usages = app.getJSONArray("usage");
					for(int j=0;j<usages.length();j++){
						JSONObject usage = usages.getJSONObject(j);
						int startInSec = Utilities.getTimeOfDay(usage.getLong("start"));
						int endInSec = Utilities.getTimeOfDay(usage.getLong("end"));
						/**
						 * width/24.f = pixels for one hour = h
						 * h/60.f = pixels for one minute = m
						 * m/60.f = pixels for one second = s
						 * 
						 */
						float pxForSecond = ((width/24.f)/60.f)/60.f;
						mRectanglePaint.setColor(rnd.nextInt()); //TODO load color from dataset... rewrite DataSet.getColor
						canvas.drawRect(xpad+offset+pxForSecond*startInSec,ypad+height*0.3f,xpad+offset+pxForSecond*endInSec,ypad+height*0.7f,mRectanglePaint);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setData(JSONObject jObj){
		if(jObj == null)
			return;
		this.jObj=jObj;		
	   invalidate();
	   requestLayout();
	}
	
	/**
	 * Important calculations for zooming:
	 * 
	 * notes:
	 * 	'time' is time on timeline, for example 13:30 is 13.5
	 *	'width' = getWidth()*scalefactor-2*offset
	 *	'offset' creates space between viewborder and start of line
	 *	'scrollX' translation of canvas. should not be >0 or else the canvas is translated to the right
	 * pxOnCanvas = offset+time*width/24
	 * 
	 * when tapping on screen:
	 * 	pxOnScreen = offset+time*width/24+scrollX
	 * 	time = ((pxOnScreen-offset-scrollX)/width)*24
	 * zooming in to a tapped time requires recalculation of scrollX:
	 * 	scrollX = pxOnScreen-time*((getWidth()*scaleFactor-2*offset)/24.f)-offset -- calculate new 'width' calculating with old 'width' leads to ugly shaking
	 * while scrolling:
	 * 	maxScroll = getWidth()*(1.f-scaleFactor)
	 */
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		zoomDetector.onTouchEvent(event);
		scrollDetector.onTouchEvent(event);
		return true;
	}
	
	private class ZoomListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
		float time;
		float pointOnScreen;
		boolean zoomStart=true;
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			if(zoomStart){
				time = ((detector.getFocusX()-offset-scrollX)/width)*24.f;
				pointOnScreen = detector.getFocusX();
				zoomStart = false;
				zoomEnd+=1;
			}
			return true;
		}
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			scaleFactor *= detector.getScaleFactor();
			if(scaleFactor<1.f){
				scrollX =0;
				scaleFactor=1.f;
				invalidate();
				return true;
			}
			scrollX = pointOnScreen-time*((getWidth()*scaleFactor-offset*2.f)/24.f)-offset;
			invalidate();
			return true;
		}
		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			zoomStart = true;

			
			super.onScaleEnd(detector);
		}
	}
	private class PanListener extends GestureDetector.SimpleOnGestureListener{
		@Override
		public boolean onDown(MotionEvent e) {

			if(!mScroller.isFinished()){
				mScroller.forceFinished(true);
			}
			return true;
		}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mScroller.fling(
                    (int)e1.getX(),
                    0,
                    (int)(velocityX/2.f),
                    0,
                    Integer.MIN_VALUE,
                    Integer.MAX_VALUE,
                    0,
                    0);

            // Start the animator and tell it to animate for the expected duration of the fling.
            
            mScrollAnimator.setDuration(mScroller.getDuration());
            mScrollAnimator.start();
            
            return true;
		};
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			scrollX-=distanceX;
			if(scrollX>0)
				scrollX=0.f;
			else if(scrollX<getWidth()*(1.f-scaleFactor))
				scrollX=getWidth()*(1.f-scaleFactor);
			invalidate();
			return true;
		}
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if(scaleFactor!=1.f){
				scaleFactor =1.f;
				scrollX = 0;
			}
			else{
				scaleFactor =5.f;
				float time = ((e.getX()-offset-scrollX)/width)*24.f;
				scrollX = e.getX()-time*((getWidth()*scaleFactor-2.f*offset)/24.f)-offset;
			}
			invalidate();
			return true;
		}
		
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return true;
		}
	}
	private class AnimatorTick implements ValueAnimator.AnimatorUpdateListener{
		float tmp=0;
		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			if(!mScroller.isFinished()){
	            mScroller.computeScrollOffset();     
	        	scrollX += mScroller.getCurrX()-mScroller.getStartX()-tmp;
	            tmp = mScroller.getCurrX()-mScroller.getStartX();
	            if(scrollX<getWidth()*(1.f-scaleFactor)){
	            	scrollX = getWidth()*(1.f-scaleFactor);
	    			tmp = 0;
	            	mScrollAnimator.cancel();
	            }else if(scrollX>0){
	            	scrollX = 0;
	    			tmp = 0;
	            	mScrollAnimator.cancel();
	            }
			}else{
				tmp = 0;
	        	mScrollAnimator.cancel();
			}
	        invalidate();
	    }
	}
}
