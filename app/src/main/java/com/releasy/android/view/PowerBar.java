package com.releasy.android.view;

import com.releasy.android.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class PowerBar extends RelativeLayout{

	private LayoutInflater mInflater;
	private View view;
	private int power = 1;
	private ImageView addPowerImg;
	private ImageView subtractPowerImg;
	
	private ImageView power1Img;
	private ImageView power2Img;
	private ImageView power3Img;
	private ImageView power4Img;
	private ImageView power5Img;
	private ImageView power6Img;
	private ImageView power7Img;
	private ImageView power8Img;
	private ImageView power9Img;
	private ImageView power10Img;
	
	public PowerBar(Context context) {
		super(context);
		init(context);
	}
	
	public PowerBar(Context context, AttributeSet attrs) {
		super(context,attrs);
		init(context);
	}
	
	private void init(Context context) {
		mInflater = LayoutInflater.from(context);
		view = mInflater.inflate(R.layout.layout_power_bar, null);
		addView(view);
		initViews();
	}
	
	private void initViews(){
		addPowerImg = (ImageView)findViewByPowerBarId(R.id.addImg);
		subtractPowerImg = (ImageView)findViewByPowerBarId(R.id.subtractImg);
		
		power1Img = (ImageView)findViewByPowerBarId(R.id.power1Img);
		power2Img = (ImageView)findViewByPowerBarId(R.id.power2Img);
		power3Img = (ImageView)findViewByPowerBarId(R.id.power3Img);
		power4Img = (ImageView)findViewByPowerBarId(R.id.power4Img);
		power5Img = (ImageView)findViewByPowerBarId(R.id.power5Img);
		power6Img = (ImageView)findViewByPowerBarId(R.id.power6Img);
		power7Img = (ImageView)findViewByPowerBarId(R.id.power7Img);
		power8Img = (ImageView)findViewByPowerBarId(R.id.power8Img);
		power9Img = (ImageView)findViewByPowerBarId(R.id.power9Img);
		power10Img = (ImageView)findViewByPowerBarId(R.id.power10Img);
	}
	
	//findViewById
	private View findViewByPowerBarId(int id) {
		return view.findViewById(id);
	}
	
	public void setAddPowerOnClick(OnClickListener onClick){
		addPowerImg.setOnClickListener(onClick);
	}
	
	public void setSubtractOnClick(OnClickListener onClick){
		subtractPowerImg.setOnClickListener(onClick);
	}

	public void addPower(){
		if(power >= 10)
			return;
		
		power = power+1;
		changePowerImg();
	}
	
	public void subtractPower(){
		if(power <= 1)
			return;
		
		power = power-1;
		changePowerImg();
	}
	
	public int getPower(){
		return power;
	}
	
	public void setPower(int power){
		this.power = power;
		changePowerImg();
	}
	
	private void changePowerImg(){
		//Log.d("z17m","changePowerImg");
		power1Img.setImageResource(R.drawable.power_1_normal);
		power2Img.setImageResource(R.drawable.power_2_normal);
		power3Img.setImageResource(R.drawable.power_3_normal);
		power4Img.setImageResource(R.drawable.power_4_normal);
		power5Img.setImageResource(R.drawable.power_5_normal);
		power6Img.setImageResource(R.drawable.power_6_normal);
		power7Img.setImageResource(R.drawable.power_7_normal);
		power8Img.setImageResource(R.drawable.power_8_normal);
		power9Img.setImageResource(R.drawable.power_9_normal);
		power10Img.setImageResource(R.drawable.power_10_normal);
		
		//Log.d("z17m","power = " + power) ;
		
		switch(power){
		case 1:
			power1Img.setImageResource(R.drawable.power_1_pressed);
			break;
		case 2:
			power2Img.setImageResource(R.drawable.power_2_pressed);
			break;
		case 3:
			power3Img.setImageResource(R.drawable.power_3_pressed);
			break;
		case 4:
			power4Img.setImageResource(R.drawable.power_4_pressed);
			break;
		case 5:
			power5Img.setImageResource(R.drawable.power_5_pressed);
			break;
		case 6:
			power6Img.setImageResource(R.drawable.power_6_pressed);
			break;
		case 7:
			power7Img.setImageResource(R.drawable.power_7_pressed);
			break;
		case 8:
			power8Img.setImageResource(R.drawable.power_8_pressed);
			break;
		case 9:
			power9Img.setImageResource(R.drawable.power_9_pressed);
			break;
		case 10:
			power10Img.setImageResource(R.drawable.power_10_pressed);
			break;
		}
	}

}
