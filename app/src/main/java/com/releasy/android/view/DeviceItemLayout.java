package com.releasy.android.view;

import com.releasy.android.R;
import com.releasy.android.activity.releasy.SelectActionActivity;
import com.releasy.android.activity.releasy.UserDefindEditActivity;
import com.releasy.android.bean.DeviceBean;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DeviceItemLayout extends LinearLayout{

	private Context context;
	private LayoutInflater mInflater;
	private View view;
	private TextView nameTxt;
	private TextView actionTxt;
	private ImageView powerImg;
	private DeviceBean bean;
	private int itemNum;
	
	public DeviceItemLayout(Context context) {
		super(context);
		init(context);
	}
	
	public DeviceItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public DeviceItemLayout(Context context, DeviceBean bean, int itemNum){
		super(context);
		this.context = context;
		this.bean = bean;
		this.itemNum = itemNum;

		init(context);
	}

	/**
	 * 初始化
	 * @param context
	 */
	private void init(Context context) {
		mInflater = LayoutInflater.from(context);
		view = mInflater.inflate(R.layout.layout_item_device, null);
		addView(view);
		
		initView();
	}
	
	/**
	 * 初始化视图
	 * @param context
	 */
	private void initView(){
		nameTxt = (TextView) view.findViewById(R.id.device_name);
		actionTxt = (TextView) view.findViewById(R.id.device_action);
		powerImg = (ImageView) view.findViewById(R.id.powerImg);
		
		nameTxt.setText(bean.getName());
		if(bean.getAction() != null){
			actionTxt.setText(bean.getAction().getActionName());
			changePowerImg();
		}
	}
	
	/**
	 * 设置点击事件
	 * @param context
	 */
	public void setOnClick(OnClickListener onClick){
		this.setOnClickListener(onClick);
	}
	
	/**
	 * 更改设备按摩动作信息
	 * @param context
	 */
	public void changeActionInfo(DeviceBean bean){
		this.bean = bean;
		actionTxt.setText(bean.getAction().getActionName());
		changePowerImg();
	}
	
	/**
	 * 更改力度图片
	 * @param context
	 */
	private void changePowerImg(){
		switch(bean.getAction().getStrength()){
		case 1:
			powerImg.setImageResource(R.drawable.ic_device_item_power_1);
			break;
		case 2:
			powerImg.setImageResource(R.drawable.ic_device_item_power_2);
			break;
		case 3:
			powerImg.setImageResource(R.drawable.ic_device_item_power_3);
			break;
		case 4:
			powerImg.setImageResource(R.drawable.ic_device_item_power_4);
			break;
		case 5:
			powerImg.setImageResource(R.drawable.ic_device_item_power_5);
			break;
		case 6:
			powerImg.setImageResource(R.drawable.ic_device_item_power_6);
			break;
		case 7:
			powerImg.setImageResource(R.drawable.ic_device_item_power_7);
			break;
		case 8:
			powerImg.setImageResource(R.drawable.ic_device_item_power_8);
			break;
		case 9:
			powerImg.setImageResource(R.drawable.ic_device_item_power_9);
			break;
		case 10:
			powerImg.setImageResource(R.drawable.ic_device_item_power_10);
			break;
		default:
			powerImg.setImageResource(R.drawable.ic_device_item_power_1);
			break;
		}
	}
	
}
