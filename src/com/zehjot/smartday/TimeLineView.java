package com.zehjot.smartday;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.Display;
import android.view.View;

public class TimeLineView extends View {
	private Paint mTextPaint = new Paint();
	private Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint mRectanglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	

	private float mTextSize = 16.f;
	
	public TimeLineView(Context context) {
		super(context);

		mTextPaint.setColor(getResources().getColor(android.R.color.black));
		mTextPaint.setTextSize(mTextSize);		
		mTextPaint.setTextAlign(Align.CENTER);
		mLinePaint.setColor(getResources().getColor(android.R.color.black));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {    
		int parentViewWidth = MeasureSpec.getSize(widthMeasureSpec);
		int parentViewHeight = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension((int)(parentViewWidth*25.f/24.f), parentViewHeight);
		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(getResources().getColor(android.R.color.holo_green_light));
		float xpad = (float) (getPaddingLeft()+getPaddingRight());
		float ypad = (float) (getPaddingTop()+getPaddingBottom());
		float height = this.getHeight();
		float width = this.getWidth();
		float offset = (width-width*24.f/25.f)*0.5f;
		width *= 24.f/25.f;
		
		canvas.drawText("TEST"+this.getHeight()+"width="+this.getWidth(), xpad+15, ypad+mTextSize, mTextPaint);
		
		for(int i=0;i<25;i++){
			canvas.drawLine(offset+xpad+i*width/24.f, ypad+height*0.81f, offset+xpad+i*width/24.f, ypad+height*0.77f, mLinePaint);
			canvas.drawText(""+i+":00", offset+xpad+i*width/24.f, ypad+height*0.81f+mTextSize+2, mTextPaint);
		}
		canvas.drawLine(offset+xpad, ypad+height*0.8f, offset+xpad+width, ypad+height*0.8f, mLinePaint);
		
		
		
		canvas.drawRect(xpad, ypad+30, xpad+50, ypad+50, mRectanglePaint);
		canvas.drawRect(xpad+1500, ypad+30, xpad+1550, ypad+50, mRectanglePaint);
	}
	
}
