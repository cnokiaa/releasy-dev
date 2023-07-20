package com.releasy.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

public class TestButton extends Button{

	public TestButton(Context context) {
		super(context);
		
	}
	
	public TestButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	public boolean dispatchTouchEvent(MotionEvent event) {
	    // TODO Auto-generated method stub
	    switch (event.getAction()) {
	    case MotionEvent.ACTION_DOWN:
	      performClick();
	      break;

	    default:
	      break;
	    }
	    return true;
	 }
	

}
