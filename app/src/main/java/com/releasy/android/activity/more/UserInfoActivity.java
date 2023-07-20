package com.releasy.android.activity.more;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.releasy.android.R;
import com.releasy.android.activity.main.BaseActivity;
import com.releasy.android.constants.Constants;
import com.releasy.android.utils.SharePreferenceUtils;
import com.releasy.android.view.TopNavLayout;

public class UserInfoActivity extends BaseActivity{

	private SharePreferenceUtils spInfo;                    //SharePreference
	private TopNavLayout mTopNavLayout;                     //导航菜单栏
	private LinearLayout phonenumOrEmailLayout;
	private LinearLayout sexLayout;
	private LinearLayout ageLayout;
	private LinearLayout heightLayout;
	private LinearLayout weightLayout;
	private TextView phoneOrEmailTitleTxt;
	private TextView phoneOrEmailTxt;
	private TextView sexTxt;
	private TextView ageTxt;
	private TextView heightTxt;
	private TextView WeightTxt;
	private LinearLayout bodyFatTitleLayout;
	private LinearLayout bodyFatDataLayout;
	
	private String genderStr = "";
	private String birthdayStr = "";
	private String age = ""; 
	private int height = 0;
	private int weight = 0;
	private String phonenumStr = "";
	private String emailStr = "";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_user_info);
	        
	    init();            //初始化
	}
	
	protected void onResume() {
        super.onResume();
        
        initData();  //加载数据
	}
	
	private void init(){
		
		spInfo = new SharePreferenceUtils(this); 
		
		initViews();    //初始化视图
		initData();     //初始数据
		setTopNav();    //初始化导航栏
		initEvents();   //初始化点击事件
	}
	
	/**
	 * 初始化导航栏
	 */
	private void setTopNav(){
		mTopNavLayout.setTitltTxt(R.string.personal_information);
		mTopNavLayout.setLeftImgSrc(R.drawable.ic_nav_bar_back);
	}
	
	/**
	 * 初始化控件
	 */
	protected void initViews() {
		mTopNavLayout = (TopNavLayout) findViewById(R.id.topNavLayout);
		
		phonenumOrEmailLayout = (LinearLayout) findViewById(R.id.phonenum_or_email_layout);
		sexLayout = (LinearLayout) findViewById(R.id.sex_layout);
		ageLayout = (LinearLayout) findViewById(R.id.age_layout);
		heightLayout = (LinearLayout) findViewById(R.id.height_layout);
		weightLayout = (LinearLayout) findViewById(R.id.weight_layout);
		
		phoneOrEmailTitleTxt = (TextView) findViewById(R.id.phone_or_email_title_txt);
		phoneOrEmailTxt = (TextView) findViewById(R.id.phone_or_email_txt);
		sexTxt = (TextView) findViewById(R.id.sex_txt);
		ageTxt = (TextView) findViewById(R.id.age_txt);
		heightTxt = (TextView) findViewById(R.id.height_txt);
		WeightTxt = (TextView) findViewById(R.id.weight_txt);
		
		bodyFatTitleLayout = (LinearLayout) findViewById(R.id.body_fat_title_layout);
		bodyFatDataLayout = (LinearLayout) findViewById(R.id.body_fat_data_layout);
		
		/*if(!spInfo.getSelectDevice().equals(Constants.SELECT_DEVICE_M2)){
			bodyFatTitleLayout.setVisibility(View.GONE);
			bodyFatDataLayout.setVisibility(View.GONE);
		}*/
	}
	
	/**
	 * 加载数据
	 */
	private void initData(){
		if(getResources().getConfiguration().locale.getCountry().equals("CN")){
			phoneOrEmailTitleTxt.setText(R.string.phone_number);
			phonenumStr = spInfo.getPhoneNum();
			phoneOrEmailTxt.setText(phonenumStr);
		}
		else{
			phoneOrEmailTitleTxt.setText(R.string.email);
			emailStr = spInfo.getEmail();
			phoneOrEmailTxt.setText(emailStr);
		}
		
		genderStr = spInfo.getUserSex();
		birthdayStr = spInfo.getUserBirthday();
		age = spInfo.getUserAge();
		height = spInfo.getUserHeight();
		weight = spInfo.getUserWeight();
		
		if(genderStr.equals("boy"))
			sexTxt.setText(R.string.man);
		else
			sexTxt.setText(R.string.woman);
		
		Log.d("z17m","birthdayStr : " + birthdayStr);
		ageTxt.setText(birthdayStr);
		heightTxt.setText(height + "");
		WeightTxt.setText(weight + "");
	}

	/**
	 * 设置监听函数
	 */
	protected void initEvents() {
		mTopNavLayout.setLeftImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				UserInfoActivity.this.finish();
			}});
		
		phonenumOrEmailLayout.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				Intent intent = new Intent(UserInfoActivity.this,PhonenumOrEmailAcrivity.class);
		        startActivity(intent);
			}});
		
		sexLayout.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				Intent intent = new Intent(UserInfoActivity.this,SexActivity.class);
		        startActivity(intent);
			}});
		
		ageLayout.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				Intent intent = new Intent(UserInfoActivity.this,AgeActivity.class);
		        startActivity(intent);
			}});
		
		heightLayout.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				Intent intent = new Intent(UserInfoActivity.this,HeightActivity.class);
		        startActivity(intent);
			}});
		
		weightLayout.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				Intent intent = new Intent(UserInfoActivity.this,WeightActivity.class);
		        startActivity(intent);
			}});
	}

}
