package com.releasy.android.utils;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

public class ScalePageTransformer implements ViewPager.PageTransformer  {

	public static final float MAX_SCALE = 1.0f;
    public static final float MIN_SCALE = 0.8f;

    public void transformPage(View page, float position) {

    	//Log.d("z17m","  transformPage ");
    	
        if (position < -1) {
            position = -1;
        } else if (position > 1) {
            position = 1;
        }

        float tempScale = position < 0 ? 1 + position : 1 - position;

        float slope = (MAX_SCALE - MIN_SCALE) / 1;
        //一个公式
        float scaleValue = MIN_SCALE + tempScale * slope;
        
        //Log.d("z17m","scaleValue = " + scaleValue);
        
        page.setScaleX(scaleValue);
        page.setScaleY(scaleValue);

        /*if (Build.VERSION.SDK_INT < 20) {
            page.getParent().requestLayout();
        }*/
    }
}
