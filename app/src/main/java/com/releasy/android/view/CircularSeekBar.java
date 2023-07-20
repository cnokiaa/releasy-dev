package com.releasy.android.view;


import com.releasy.android.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;

/**
 * The Class CircularSeekBar.
 */
public class CircularSeekBar extends View {

	/** The context */
	private Context mContext;

	/** The listener to listen for changes */
	private OnSeekChangeListener mListener;

	/** The color of the progress ring */
	private Paint circleColor;

	/** the color of the inside circle. Acts as background color */
	private Paint innerColor;

	/** The progress circle ring background */
	private Paint circleRing;

	/** the color of sideline circle */
	private Paint lineColor;
	private Paint mPaint ;
	
	//刻度线
	private Paint linePaint;
	//刻度
	private Paint txtPaint;
	private float degreeStringSize;
	private Rect textBound;
	
	/** The angle of progress */
	private int angle = 0;

	/** The start angle (12 O'clock */
	private int startAngle = 270;

	/** The width of the progress ring */
	private float barWidth = 25;

	/** The width of the view */
	private int width;

	/** The height of the view */
	private int height;

	/** The maximum progress amount */
	private int maxProgress = 100;

	/** The current progress */
	private int progress;

	/** The progress percent */
	private int progressPercent;

	/** The radius of the inner circle */
	private float innerRadius;

	/** The radius of the outer circle */
	private float outerRadius;

	/** The circle's center X coordinate */
	private float cx;

	/** The circle's center Y coordinate */
	private float cy;

	/** The left bound for the circle RectF */
	private float left;

	/** The right bound for the circle RectF */
	private float right;

	/** The top bound for the circle RectF */
	private float top;

	/** The bottom bound for the circle RectF */
	private float bottom;

	/** The X coordinate for the top left corner of the marking drawable */
	private float dx;

	/** The Y coordinate for the top left corner of the marking drawable */
	private float dy;

	/** The X coordinate for 12 O'Clock */
	private float startPointX;

	/** The Y coordinate for 12 O'Clock */
	private float startPointY;

	/**
	 * The X coordinate for the current position of the marker, pre adjustment
	 * to center
	 */
	private float markPointX;

	/**
	 * The Y coordinate for the current position of the marker, pre adjustment
	 * to center
	 */
	private float markPointY;

	/**
	 * The adjustment factor. This adds an adjustment of the specified size to
	 * both sides of the progress bar, allowing touch events to be processed
	 * more user friendly (yes, I know that's not a word)
	 */
	private float adjustmentFactor = 100;

	/** The progress mark when the view isn't being progress modified */
	private Bitmap progressMark;

	/** The progress mark when the view is being progress modified. */
	private Bitmap progressMarkPressed;

	/** The flag to see if view is pressed */
	private boolean IS_PRESSED = false;

	/**
	 * The flag to see if the setProgress() method was called from our own
	 * View's setAngle() method, or externally by a user.
	 */
	private boolean CALLED_FROM_ANGLE = false;

	private boolean SHOW_SEEKBAR = true;
	
	private boolean OPEN_TOUCH = true;
	
	
	private int[] mColors = new int[] {//渐变色数组  
			 0xffb0e916,0xffdccf06,0xfff7a100
			,0xfffc6f00,0xffff5100,0xffff4e00
        }; 
	/** The rectangle containing our circles and arcs. */
	private RectF rect = new RectF();

	{
		mListener = new OnSeekChangeListener() {
			public void onProgressChange(CircularSeekBar view, int newProgress) {

			}
		};
		
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  
        mPaint.setStyle(Paint.Style.FILL);  
        mPaint.setStrokeWidth(30); 
        
        
		circleColor = new Paint();
		innerColor = new Paint();
		circleRing = new Paint();
		lineColor = new Paint();

		circleColor.setColor(Color.parseColor("#ff04d8d9")); // Set default
		// progress
		// color to holo
		// blue.
		innerColor.setColor(Color.parseColor("#ffffffff")); // Set default background color to
		// black
		circleRing.setColor(Color.parseColor("#ffe8e6e7"));// Set default background color to Gray
		
		lineColor.setColor(Color.parseColor("#ffbdb6b6"));// Set default background color to Gray
		
		circleColor.setAntiAlias(true);
		innerColor.setAntiAlias(true);
		circleRing.setAntiAlias(true);
		lineColor.setAntiAlias(true);
		
		circleColor.setStrokeWidth(30);
		innerColor.setStrokeWidth(30);
		circleRing.setStrokeWidth(30);
		lineColor.setStrokeWidth(30);

		circleColor.setStyle(Paint.Style.FILL);
		
		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setColor(Color.parseColor("#ffbdb6b6"));
		linePaint.setStrokeWidth(2);
		
		txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		txtPaint.setStyle(Paint.Style.FILL);
		txtPaint.setColor(Color.parseColor("#ffbdb6b6"));
		
		
		textBound = new Rect();
	}

	/**
	 * Instantiates a new circular seek bar.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 * @param defStyle
	 *            the def style
	 */
	public CircularSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initDrawable();
		degreeStringSize = context.getResources().getDimension(R.dimen.circular_seek_bar_view_string_size);
		barWidth = context.getResources().getDimension(R.dimen.circular_seek_bar_view_bar_width);
		
	}

	/**
	 * Instantiates a new circular seek bar.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 */
	public CircularSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initDrawable();
		degreeStringSize = context.getResources().getDimension(R.dimen.circular_seek_bar_view_string_size);
		barWidth = context.getResources().getDimension(R.dimen.circular_seek_bar_view_bar_width);
	}

	/**
	 * Instantiates a new circular seek bar.
	 * 
	 * @param context
	 *            the context
	 */
	public CircularSeekBar(Context context) {
		super(context);
		mContext = context;
		initDrawable();
	}

	/**
	 * Inits the drawable.
	 */
	public void initDrawable() {
		progressMark = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_circular_seek_bar_time_15);
		progressMarkPressed = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.ic_circular_seek_bar_time_15);
	}
	
	/**
	 * Inits the drawable.
	 */
	public void changeDrawable(int progress) {
		int drawable = R.drawable.ic_circular_seek_bar_time_15;
		switch(progress){
		case 0:
			drawable = R.drawable.ic_circular_seek_bar_time_1;
			break;
		case 1:
			drawable = R.drawable.ic_circular_seek_bar_time_1;
			break;
		case 2:
			drawable = R.drawable.ic_circular_seek_bar_time_2;
			break;
		case 3:
			drawable = R.drawable.ic_circular_seek_bar_time_3;
			break;
		case 4:
			drawable = R.drawable.ic_circular_seek_bar_time_4;
			break;
		case 5:
			drawable = R.drawable.ic_circular_seek_bar_time_5;
			break;
		case 6:
			drawable = R.drawable.ic_circular_seek_bar_time_6;
			break;
		case 7:
			drawable = R.drawable.ic_circular_seek_bar_time_7;
			break;
		case 8:
			drawable = R.drawable.ic_circular_seek_bar_time_8;
			break;
		case 9:
			drawable = R.drawable.ic_circular_seek_bar_time_9;
			break;
		case 10:
			drawable = R.drawable.ic_circular_seek_bar_time_10;
			break;
		case 11:
			drawable = R.drawable.ic_circular_seek_bar_time_11;
			break;
		case 12:
			drawable = R.drawable.ic_circular_seek_bar_time_12;
			break;
		case 13:
			drawable = R.drawable.ic_circular_seek_bar_time_13;
			break;
		case 14:
			drawable = R.drawable.ic_circular_seek_bar_time_14;
			break;
		case 15:
			drawable = R.drawable.ic_circular_seek_bar_time_15;
			break;
		default:
			drawable = R.drawable.ic_circular_seek_bar_time_15;
			break;
		}
		
		progressMark = BitmapFactory.decodeResource(mContext.getResources(), drawable);
		progressMarkPressed = BitmapFactory.decodeResource(mContext.getResources(),
				drawable);
	}
	
	/**
	 * Inits the drawable.
	 */
	public void changeDoingDrawable(int progress) {
		int drawable = R.drawable.ic_circular_seek_bar_time_15;
		if(progress >= 0 && progress <= 60)
			drawable = R.drawable.ic_circular_seek_bar_time_1;
		else if(progress > 60 && progress <= 120)
			drawable = R.drawable.ic_circular_seek_bar_time_2;
		else if(progress > 120 && progress <= 180)
			drawable = R.drawable.ic_circular_seek_bar_time_3;
		else if(progress > 180 && progress <= 240)
			drawable = R.drawable.ic_circular_seek_bar_time_4;
		else if(progress > 240 && progress <= 300)
			drawable = R.drawable.ic_circular_seek_bar_time_5;
		else if(progress > 300 && progress <= 360)
			drawable = R.drawable.ic_circular_seek_bar_time_6;
		else if(progress > 360 && progress <= 420)
			drawable = R.drawable.ic_circular_seek_bar_time_7;
		else if(progress > 420 && progress <= 480)
			drawable = R.drawable.ic_circular_seek_bar_time_8;
		else if(progress > 480 && progress <= 540)
			drawable = R.drawable.ic_circular_seek_bar_time_9;
		else if(progress > 540 && progress <= 600)
			drawable = R.drawable.ic_circular_seek_bar_time_10;
		else if(progress > 600 && progress <= 660)
			drawable = R.drawable.ic_circular_seek_bar_time_11;
		else if(progress > 660 && progress <= 720)
			drawable = R.drawable.ic_circular_seek_bar_time_12;
		else if(progress > 720 && progress <= 780)
			drawable = R.drawable.ic_circular_seek_bar_time_13;
		else if(progress > 780 && progress <= 840)
			drawable = R.drawable.ic_circular_seek_bar_time_14;
		else if(progress > 840 && progress <= 900)
			drawable = R.drawable.ic_circular_seek_bar_time_15;
		
		progressMark = BitmapFactory.decodeResource(mContext.getResources(), drawable);
		progressMarkPressed = BitmapFactory.decodeResource(mContext.getResources(),
				drawable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		width = getWidth(); // Get View Width
		height = getHeight();// Get View Height

		int size = (width > height) ? height : width; // Choose the smaller
		// between width and
		// height to make a
		// square

		cx = width / 2; // Center X for circle
		cy = height / 2; // Center Y for circle
		outerRadius = size / 2 - progressMark.getHeight()/2; // Radius of the outer circle
		
		innerRadius = outerRadius - barWidth; // Radius of the inner circle

		left = cx - outerRadius; // Calculate left bound of our rect
		right = cx + outerRadius;// Calculate right bound of our rect
		top = cy - outerRadius;// Calculate top bound of our rect
		bottom = cy + outerRadius;// Calculate bottom bound of our rect

		startPointX = cx; // 12 O'clock X coordinate
		startPointY = cy - outerRadius;// 12 O'clock Y coordinate
		markPointX = startPointX;// Initial locatino of the marker X coordinate
		markPointY = startPointY;// Initial locatino of the marker Y coordinate

		rect.set(left, top, right, bottom); // assign size to rect
		
		SweepGradient shader = new SweepGradient(cx, cy, mColors,null);
		Matrix matrix = new Matrix();  
		matrix.setRotate(270, cx, cy);//这个方法是以哪个点为中心进行旋转多少度
		shader.setLocalMatrix(matrix);
        mPaint.setShader(shader);
        
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		
		canvas.drawCircle(cx, cy, outerRadius+1, lineColor);
		canvas.drawCircle(cx, cy, outerRadius, circleRing);
		canvas.drawArc(rect, startAngle, angle, true, /*circleColor*/mPaint);
		canvas.drawCircle(cx, cy, innerRadius+1, lineColor);
		canvas.drawCircle(cx, cy, innerRadius, innerColor);
		
		for (int i = 0; i < 15; i ++) {
			double realAngle = Math.toRadians(i*360/15 + 180) ;
			float startX = (float) (cx + innerRadius * Math.sin(realAngle));
			float startY = (float) (cy + innerRadius * Math.cos(realAngle));
			float stopX = (float) (cx + (innerRadius - barWidth) * Math.sin(realAngle));
			float stopY = (float) (cy + (innerRadius - barWidth) * Math.cos(realAngle));
			canvas.drawLine(startX, startY, stopX, stopY, linePaint);        //绘制直线  
			
			txtPaint.setTextSize(degreeStringSize);
			txtPaint.getTextBounds(00 + "", 0, (00 + "").length(), textBound);
			float txtStartX = (float) (cx + (innerRadius - barWidth - textBound.height()) * Math.sin(realAngle));
			float txtStartY = (float) (cy + (innerRadius - barWidth - textBound.height()) * Math.cos(realAngle));
			
			if(i % 3 == 0)
				canvas.drawText((15-i) + "", txtStartX - textBound.width() / 2, txtStartY + textBound.height() / 2, txtPaint);
		}
		
		/*if(SHOW_SEEKBAR){
			dx = getXFromAngle();
			dy = getYFromAngle();
			drawMarkerAtProgress(canvas);
		}*/

		if(OPEN_TOUCH){
			dx = getXFromAngle();
			dy = getYFromAngle();
			drawMarkerAtProgress(canvas);
		}
		else{
			double realAngle = Math.toRadians(angle) ;
			float r = outerRadius - barWidth/2;
			float w = progressMark.getWidth();
			float drawX = (float) (cx + r * Math.sin(realAngle) - w/2);
			float drawY = (float) (cy - r * Math.cos(realAngle) - w/2);
			/*
			Log.d("z17m","r * Math.sin(realAngle) : " +r * Math.sin(realAngle));
			Log.d("z17m","r * Math.cos(realAngle) : " +r * Math.cos(realAngle));
			Log.d("z17m", "r : " + r + "    cx : " + cx + "    cy : " + cy + "    drawX : " + drawX + "    drawY : " + drawY);*/
			canvas.drawBitmap(progressMark, drawX, drawY, null);
		}
		
		
		
		
		super.onDraw(canvas);
	}

	/**
	 * Draw marker at the current progress point onto the given canvas.
	 * 
	 * @param canvas
	 *            the canvas
	 */
	public void drawMarkerAtProgress(Canvas canvas) {
		if (IS_PRESSED) {
			canvas.drawBitmap(progressMarkPressed, dx, dy, null);
		} else {
			canvas.drawBitmap(progressMark, dx, dy, null);
		}
	}

	/**
	 * Gets the X coordinate of the arc's end arm's point of intersection with
	 * the circle
	 * 
	 * @return the X coordinate
	 */
	public float getXFromAngle() {
		int size1 = progressMark.getWidth();
		int size2 = progressMarkPressed.getWidth();
		int adjust = (size1 > size2) ? size1 : size2;
		float x = markPointX - (adjust / 2);
		if(cx > x)
			x = x + barWidth/2;
		else
			x = x - barWidth/2;
		return x;
	}

	/**
	 * Gets the Y coordinate of the arc's end arm's point of intersection with
	 * the circle
	 * 
	 * @return the Y coordinate
	 */
	public float getYFromAngle() {
		int size1 = progressMark.getHeight();
		int size2 = progressMarkPressed.getHeight();
		int adjust = (size1 > size2) ? size1 : size2;
		float y = markPointY - (adjust / 2);
		if(cy > y)
			y = y + barWidth/2;
		else
			y = y - barWidth/2;
		return y;
	}

	/**
	 * Get the angle.
	 * 
	 * @return the angle
	 */
	public int getAngle() {
		return angle;
	}

	/**
	 * Set the angle.
	 * 
	 * @param angle
	 *            the new angle
	 */
	public void setAngle(int angle) {
		this.angle = angle;
		float donePercent = (((float) this.angle) / 360) * 100;
		float progress = (donePercent / 100) * getMaxProgress();
		setProgressPercent(Math.round(donePercent));
		CALLED_FROM_ANGLE = true;
		setProgress(Math.round(progress));
	}

	/**
	 * Sets the seek bar change listener.
	 * 
	 * @param listener
	 *            the new seek bar change listener
	 */
	public void setSeekBarChangeListener(OnSeekChangeListener listener) {
		mListener = listener;
	}

	/**
	 * Gets the seek bar change listener.
	 * 
	 * @return the seek bar change listener
	 */
	public OnSeekChangeListener getSeekBarChangeListener() {
		return mListener;
	}

	/**
	 * Gets the bar width.
	 * 
	 * @return the bar width
	 */
	public float getBarWidth() {
		return barWidth;
	}

	/**
	 * Sets the bar width.
	 * 
	 * @param barWidth
	 *            the new bar width
	 */
	public void setBarWidth(int barWidth) {
		this.barWidth = barWidth;
	}

	/**
	 * The listener interface for receiving onSeekChange events. The class that
	 * is interested in processing a onSeekChange event implements this
	 * interface, and the object created with that class is registered with a
	 * component using the component's
	 * <code>setSeekBarChangeListener(OnSeekChangeListener)<code> method. When
	 * the onSeekChange event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see OnSeekChangeEvent
	 */
	public interface OnSeekChangeListener {

		/**
		 * On progress change.
		 * 
		 * @param view
		 *            the view
		 * @param newProgress
		 *            the new progress
		 */
		public void onProgressChange(CircularSeekBar view, int newProgress);
	}

	/**
	 * Gets the max progress.
	 * 
	 * @return the max progress
	 */
	public int getMaxProgress() {
		return maxProgress;
	}

	/**
	 * Sets the max progress.
	 * 
	 * @param maxProgress
	 *            the new max progress
	 */
	public void setMaxProgress(int maxProgress) {
		this.maxProgress = maxProgress;
	}

	/**
	 * Gets the progress.
	 * 
	 * @return the progress
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * Sets the progress.
	 * 
	 * @param progress
	 *            the new progress
	 */
	public void setProgress(int progress) {
		if (this.progress != progress) {
			this.progress = progress;
			if (!CALLED_FROM_ANGLE) {
				int newPercent = (this.progress * 100) / this.maxProgress;
				int newAngle = (newPercent * 360) / 100 ;
				this.setAngle(newAngle);
				this.setProgressPercent(newPercent);
			}
			mListener.onProgressChange(this, this.getProgress());
			CALLED_FROM_ANGLE = false;
		}
	}

	/**
	 * Gets the progress percent.
	 * 
	 * @return the progress percent
	 */
	public int getProgressPercent() {
		return progressPercent;
	}

	/**
	 * Sets the progress percent.
	 * 
	 * @param progressPercent
	 *            the new progress percent
	 */
	public void setProgressPercent(int progressPercent) {
		this.progressPercent = progressPercent;
	}

	/**
	 * Sets the ring background color.
	 * 
	 * @param color
	 *            the new ring background color
	 */
	public void setRingBackgroundColor(int color) {
		circleRing.setColor(color);
	}

	/**
	 * Sets the back ground color.
	 * 
	 * @param color
	 *            the new back ground color
	 */
	public void setBackGroundColor(int color) {
		innerColor.setColor(color);
	}

	/**
	 * Sets the progress color.
	 * 
	 * @param color
	 *            the new progress color
	 */
	public void setProgressColor(int color) {
		circleColor.setColor(color);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!OPEN_TOUCH)
			return true;
		
		float x = event.getX();
		float y = event.getY();
		boolean up = false;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			moved(x, y, up);
			break;
		case MotionEvent.ACTION_MOVE:
			if(getProgress() == getMaxProgress() && x >= cx){
				setAngle(360);
				
				//moved(cx, cy, up);
				break;
			}
			
			moved(x, y, up);
			break;
		case MotionEvent.ACTION_UP:
			up = true;
			if(getProgress() == getMaxProgress()){
				setAngle(360);
			}
			
			moved(x, y, up);
			break;
		}
		return true;
	}

	/**
	 * Moved.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param up
	 *            the up
	 */
	private void moved(float x, float y, boolean up) {
		float distance = (float) Math.sqrt(Math.pow((x - cx), 2) + Math.pow((y - cy), 2));
		if (distance < outerRadius + adjustmentFactor && distance > innerRadius - adjustmentFactor && !up) {
			IS_PRESSED = true;

			markPointX = (float) (cx + outerRadius * Math.cos(Math.atan2(x - cx, cy - y) - (Math.PI /2)));
			markPointY = (float) (cy + outerRadius * Math.sin(Math.atan2(x - cx, cy - y) - (Math.PI /2)));

			float degrees = (float) ((float) ((Math.toDegrees(Math.atan2(x - cx, cy - y)) + 360.0)) % 360.0);
			// and to make it count 0-360
			if (degrees < 0) {
				degrees += 2 * Math.PI;
			}

			setAngle(Math.round(degrees));
			invalidate();

		} else {
			IS_PRESSED = false;
			invalidate();
		}

	}

	/**
	 * Gets the adjustment factor.
	 * 
	 * @return the adjustment factor
	 */
	public float getAdjustmentFactor() {
		return adjustmentFactor;
	}

	/**
	 * Sets the adjustment factor.
	 * 
	 * @param adjustmentFactor
	 *            the new adjustment factor
	 */
	public void setAdjustmentFactor(float adjustmentFactor) {
		this.adjustmentFactor = adjustmentFactor;
	}

	/**
	 * To display seekbar
	 */
	public void showSeekBar() {
		SHOW_SEEKBAR = true;
	}

	/**
	 * To hide seekbar
	 */
	public void hideSeekBar() {
		SHOW_SEEKBAR = false;
	}
	
	/**
	 * 设置是否打开触摸滑动
	 */
	public void setOpenTouch(boolean OPEN_TOUCH) {
		this.OPEN_TOUCH = OPEN_TOUCH;
	}

	/**
	 * 获取触摸滑动状态
	 */
	public boolean getOpenTouch() {
		return OPEN_TOUCH;
	}
	
}
