package com.releasy.android.activity.main;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.releasy.android.R;
import com.releasy.android.activity.device.ChoiceDeviceActivity;
import com.releasy.android.activity.device.SearchDeviceForM1Activity;
import com.releasy.android.constants.HttpConstants;
import com.releasy.android.http.HttpUtils;
import com.releasy.android.http.ResolveJsonUtils;
import com.releasy.android.utils.SharePreferenceUtils;
import com.releasy.android.utils.StringUtils;
import com.releasy.android.view.BaseScaleView.OnScrollListener;
import com.releasy.android.view.HorizontalScaleScrollView;
import com.releasy.android.view.TopNavLayout;
import com.releasy.android.view.VerticalScaleScrollView;
import com.releasy.android.view.wheelview.NumericWheelAdapter;
import com.releasy.android.view.wheelview.OnWheelScrollListener;
import com.releasy.android.view.wheelview.WheelView;

public class InformationPerfectActivity extends BaseActivity {

	private TopNavLayout mTopNavLayout;           //导航菜单栏
	private LinearLayout step1Layout;             //第一步页面       
	private Button step1NextBtn;                  //第一页下一步按钮
	private LinearLayout step2Layout;             //第二步页面       
	private Button step2LastBtn;                  //第二页上一步按钮
	private Button step2NextBtn;                  //第二页下一步按钮
	private LinearLayout step3Layout;             //第三步页面    
	private Button step3LastBtn;                  //第三页上一步按钮
	private Button step3NextBtn;                  //第三页下一步按钮
	private LinearLayout step4Layout;             //第四步页面    
	private Button step4LastBtn;                  //第四页上一步按钮
	private Button step4NextBtn;                  //第四页下一步按钮
	private LinearLayout step5Layout;             //第五步页面    
	private Button step5LastBtn;                  //第五页上一步按钮
	private Button completeBtn;                   //完成按钮
	
	private RadioGroup genderRgs;                 //性别单选按钮
	
	private VerticalScaleScrollView verticalScaleScrollView;  //身高刻度尺
	private TextView heightTxt;                   //身高Txt
	
	private HorizontalScaleScrollView horizontalScaleScrollView;  //体重刻度尺
	private TextView weightTxt;                   //体重Txt
	
	private EditText phoneOrEmailEdit;            //输入框
	private TextView step5Txt;                    //输入提示
	
	private String titlt;                         //TopNav 标签
	private String genderStr = "boy";
	private String birthdayStr = "1990-01-01";
	//private String age; 
	private int height = 0;
	private int weight = 0;
	private String phonenumStr = "";
	private String emailStr = "";
	private SharePreferenceUtils spInfo;  //SharePreference
	
	//生日相关
	private LayoutInflater inflater = null;
	private WheelView year;
	private WheelView month;
	private WheelView day;
	private int mYear=1990;
	private int mMonth=0;
	private int mDay=1;
	private LinearLayout dataLayout;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_information_perfect);
	        
	    init();            //初始化
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		titlt = this.getResources().getString(R.string.improve_personal_information);
		initProgressDialog(this.getString(R.string.submitting_data));
		
		spInfo = new SharePreferenceUtils(this); 
		//age = calculateDatePoor(birthdayStr);
		
		initViews();    //初始化视图
		setTopNav();    //初始化导航栏
		initEvents();   //初始化点击事件
	}
	
	/**
	 * 初始化导航栏
	 */
	private void setTopNav(){
		if(!getResources().getConfiguration().locale.getCountry().equals("CN")){
			mTopNavLayout.setRightTxt(R.string.skip);
			
			mTopNavLayout.setRightTxtOnClick(new OnClickListener(){
				public void onClick(View arg0) {
					showAlertDialog(InformationPerfectActivity.this.getString(R.string.prompt)
							,InformationPerfectActivity.this.getString(R.string.user_skip_prompt)
							,InformationPerfectActivity.this.getString(R.string.confirm)
							,new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface arg0, int arg1) {
									if(spInfo.getIsV_20FristOpen()){
										Intent intent = new Intent(InformationPerfectActivity.this,MainTabActivity.class);
										startActivity(intent);
										intent = new Intent(InformationPerfectActivity.this,ChoiceDeviceActivity.class);
										startActivity(intent);
									}
									
									spInfo.setIsV_20FristOpen(false);
						        	InformationPerfectActivity.this.finish();
								}}
							,InformationPerfectActivity.this.getString(R.string.cancel)
							,null
							);
				}});
		}
		
		mTopNavLayout.setTitltColor(this.getResources().getColor(R.color.color_txt_869398));
		mTopNavLayout.setTitltTxt(titlt/*+"1/5"*/);
	}
	
	protected void initViews() {
		mTopNavLayout = (TopNavLayout) findViewById(R.id.topNavLayout);
		
		step1Layout = (LinearLayout) findViewById(R.id.step1_layout);
		step1NextBtn = (Button) findViewById(R.id.step1_next_btn);
		
		genderRgs = (RadioGroup) findViewById(R.id.gender_rgs);
		
		step2Layout = (LinearLayout) findViewById(R.id.step2_layout);
		step2LastBtn = (Button) findViewById(R.id.step2_last_btn);
		step2NextBtn = (Button) findViewById(R.id.step2_next_btn);
		
		inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		dataLayout = (LinearLayout) findViewById(R.id.data_layout);
		dataLayout.addView(getDataPick());
		
		step3Layout = (LinearLayout) findViewById(R.id.step3_layout);
		step3LastBtn = (Button) findViewById(R.id.step3_last_btn);
		step3NextBtn = (Button) findViewById(R.id.step3_next_btn);
		
		verticalScaleScrollView = (VerticalScaleScrollView) findViewById(R.id.verticalScaleScrollView);
		heightTxt = (TextView) findViewById(R.id.height_txt);
		
		step4Layout = (LinearLayout) findViewById(R.id.step4_layout);
		step4LastBtn = (Button) findViewById(R.id.step4_last_btn);
		step4NextBtn = (Button) findViewById(R.id.step4_next_btn);
		
		horizontalScaleScrollView = (HorizontalScaleScrollView) findViewById(R.id.horizontalScaleScrollView);
		weightTxt = (TextView) findViewById(R.id.weight_txt);
		
		step5Layout = (LinearLayout) findViewById(R.id.step5_layout);
		step5LastBtn = (Button) findViewById(R.id.step5_last_btn);
		completeBtn = (Button) findViewById(R.id.complete_btn);
		
		step5Txt = (TextView) findViewById(R.id.step5_txt);
		phoneOrEmailEdit = (EditText) findViewById(R.id.phone_or_email_edit);
	}

	protected void initEvents() {
		step1NextBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				mTopNavLayout.setTitltTxt(titlt+"2/5");
				step1Layout.setVisibility(View.GONE);
				step2Layout.setVisibility(View.VISIBLE);
			}});
		
		step2LastBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				mTopNavLayout.setTitltTxt(titlt+"1/5");
				step1Layout.setVisibility(View.VISIBLE);
				step2Layout.setVisibility(View.GONE);
			}});
		
		step2NextBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				mTopNavLayout.setTitltTxt(titlt+"3/5");
				step2Layout.setVisibility(View.GONE);
				step3Layout.setVisibility(View.VISIBLE);
			}});
		
		step3LastBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				mTopNavLayout.setTitltTxt(titlt+"2/5");
				step2Layout.setVisibility(View.VISIBLE);
				step3Layout.setVisibility(View.GONE);
			}});
		
		step3NextBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				mTopNavLayout.setTitltTxt(titlt+"4/5");
				step3Layout.setVisibility(View.GONE);
				step4Layout.setVisibility(View.VISIBLE);
			}});
		
		step4LastBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				mTopNavLayout.setTitltTxt(titlt+"3/5");
				step3Layout.setVisibility(View.VISIBLE);
				step4Layout.setVisibility(View.GONE);
			}});
		
		step4NextBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				mTopNavLayout.setTitltTxt(titlt+"5/5");
				step4Layout.setVisibility(View.GONE);
				step5Layout.setVisibility(View.VISIBLE);
				
				if(getResources().getConfiguration().locale.getCountry().equals("CN")){
					step5Txt.setText(getResources().getString(R.string.please_enter_your_phone_number));
					phoneOrEmailEdit.setHint(R.string.please_enter_your_phone_number);
				}
				else{
					step5Txt.setText(getResources().getString(R.string.please_enter_your_email));
					phoneOrEmailEdit.setHint(R.string.please_enter_your_email);
				}
			}});
		
		step5LastBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				mTopNavLayout.setTitltTxt(titlt+"4/5");
				step4Layout.setVisibility(View.VISIBLE);
				step5Layout.setVisibility(View.GONE);
				
			}});
		
		completeBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				if(getResources().getConfiguration().locale.getCountry().equals("CN"))
					phonenumStr = phoneOrEmailEdit.getText().toString();
				else
					emailStr = phoneOrEmailEdit.getText().toString();

				Log.d("z17m"," genderStr : " + genderStr +"    birthdayStr : " + birthdayStr + "    heightStr : " + height
						+ "    weightStr : " + weight + "    phonenumStr : " + phonenumStr + "    emailStr : " + emailStr);
				
				if(!checkPhoneNumOrEmail())
					return;
				
				progressDialog.show();
				putAsyncTask(new RegisterAsyncTask());
			}});
		
		verticalScaleScrollView.setOnScrollListener(new OnScrollListener() {
            public void onScaleScroll(int scale) {
            	heightTxt.setText("" + scale);
            	height = scale;
             }});
		
		horizontalScaleScrollView.setOnScrollListener(new OnScrollListener() {
            public void onScaleScroll(int scale) {
            	weightTxt.setText("" + scale);
            	weight = scale;
             }});
		
		genderRgs.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
				if(R.id.boy_rg == checkedId)
					genderStr = "boy";
				else
					genderStr = "girl";
			}});
	}
	
	private View getDataPick() {
		Calendar c = Calendar.getInstance();
		int norYear = c.get(Calendar.YEAR);
		int curYear = mYear;
		int curMonth = mMonth+1;
		int curDate = mDay;
		
		final View view = inflater.inflate(R.layout.wheel_date_picker, null);
		
		year = (WheelView) view.findViewById(R.id.year);
		year.setAdapter(new NumericWheelAdapter(1950, norYear));
		year.setLabel(this.getString(R.string.year));
		year.setCyclic(false);
		year.addScrollingListener(scrollListener);
		
		month = (WheelView) view.findViewById(R.id.month);
		month.setAdapter(new NumericWheelAdapter(1, 12, "%02d"));
		month.setLabel(this.getString(R.string.month));
		month.setCyclic(false);
		month.addScrollingListener(scrollListener);
		
		day = (WheelView) view.findViewById(R.id.day);
		initDay(curYear,curMonth);
		day.setLabel(this.getString(R.string.day));
		day.setCyclic(false);
		day.addScrollingListener(scrollListener);

		year.setCurrentItem(curYear - 1950);
		month.setCurrentItem(curMonth - 1);
		day.setCurrentItem(curDate - 1);

		return view;
	}
	
	OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
		public void onScrollingStarted(WheelView wheel) {}

		public void onScrollingFinished(WheelView wheel) {
			int n_year = year.getCurrentItem() + 1950;//
			int n_month = month.getCurrentItem() + 1;//
			initDay(n_year,n_month);
			birthdayStr = new StringBuilder().append((year.getCurrentItem()+1950)).append("-").append((month.getCurrentItem() + 1) < 10 ? "0" + (month.getCurrentItem() + 1) : (month.getCurrentItem() + 1)).append("-").append(((day.getCurrentItem()+1) < 10) ? "0" + (day.getCurrentItem()+1) : (day.getCurrentItem()+1)).toString();
			//age = calculateDatePoor(birthdayStr);
			//Toast.makeText(InformationPerfectActivity.this, birthday, Toast.LENGTH_LONG).show();
		}
	};
	
	/**
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	private int getDay(int year, int month) {
		int day = 30;
		boolean flag = false;
		switch (year % 4) {
		case 0:
			flag = true;
			break;
		default:
			flag = false;
			break;
		}
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			day = 31;
			break;
		case 2:
			day = flag ? 29 : 28;
			break;
		default:
			day = 30;
			break;
		}
		return day;
	}

	/**
	 */
	private void initDay(int arg1, int arg2) {
		day.setAdapter(new NumericWheelAdapter(1, getDay(arg1, arg2), "%02d"));
	}
	
	/*public static final String calculateDatePoor(String birthday) {
		try {
			if (TextUtils.isEmpty(birthday))
				return "0";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date birthdayDate = sdf.parse(birthday);
			String currTimeStr = sdf.format(new Date());
			Date currDate = sdf.parse(currTimeStr);
			if (birthdayDate.getTime() > currDate.getTime()) {
				return "0";
			}
			long age = (currDate.getTime() - birthdayDate.getTime())
					/ (24 * 60 * 60 * 1000) + 1;
			String year = new DecimalFormat("0.00").format(age / 365f);
			if (TextUtils.isEmpty(year))
				return "0";
			return String.valueOf(new Double(year).intValue());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "0";
	}*/

	/**
	 * 检查电话号码
	 */
	private boolean checkPhoneNumOrEmail(){
		if(getResources().getConfiguration().locale.getCountry().equals("CN")){
			if(StringUtils.isBlank(phonenumStr)){
				Toast.makeText(this, R.string.please_enter_your_phone_number, Toast.LENGTH_LONG).show();
				return false;
			}
		}
		else{
			if(StringUtils.isBlank(emailStr)){
				Toast.makeText(this, R.string.please_enter_your_email, Toast.LENGTH_LONG).show();
				return false;
			}
		}
		

		return true;	
	}
	
/***************************************网络通信*****************************************/
	
	/**
	 * 请求参数封装
	 */
	private List<NameValuePair> getRegisterParams(){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("phone", phonenumStr));
		params.add(new BasicNameValuePair("email", emailStr));
		params.add(new BasicNameValuePair("birthday", birthdayStr));
		params.add(new BasicNameValuePair("height", height+""));
		params.add(new BasicNameValuePair("weight", weight+""));
		if(genderStr.equals("boy"))
			params.add(new BasicNameValuePair("sex", "1"));
		else
			params.add(new BasicNameValuePair("sex", "0"));
		return params;
	}
	
	/**
	 * 注册线程处理
	 */
	private class RegisterAsyncTask extends AsyncTask<Void, Void, Boolean>{
		private Bundle mResult;
		private Bundle headBundle;
		private int retStatus;
		private String retMsg ;
		
		protected Boolean doInBackground(Void... param) {
			List<NameValuePair> params = getRegisterParams();
			mResult = HttpUtils.doPost(params,HttpConstants.REGIST_USERINFO
					,InformationPerfectActivity.this,mScreenWidth,mScreenHeight);
			if(mResult.getInt("code") != HttpConstants.SUCCESS){
				retStatus = mResult.getInt("code");
				retMsg = getResources().getString(R.string.network_anomaly);
				return null;
			}
			
			headBundle = ResolveJsonUtils.getJsonHead(mResult.getString("content"));
			retStatus = headBundle.getInt("retStatus");
			retMsg = headBundle.getString("retMsg");
			showLogD("retStatus : " + retStatus + "    retMsg : " + retMsg);
			return null;
		}
		
		protected void onPostExecute(Boolean result) {  
			if(!InformationPerfectActivity.this.isFinishing() && progressDialog.isShowing())
				progressDialog.dismiss();
			else
				return;
			
			if(retStatus == HttpConstants.SUCCESS){
				int uid = ResolveJsonUtils.regist(mResult.getString("content"));
				Log.d("z17m","uid = " + uid);
				spInfo.setUId(uid);
			}

			if(getResources().getConfiguration().locale.getCountry().equals("CN"))
				spInfo.setPhoneNum(phonenumStr);
			else
				spInfo.setEmail(emailStr);
			
			spInfo.setUserSex(genderStr);
			spInfo.setUserBirthday(birthdayStr);
			//spInfo.setUserAge(age);
			spInfo.setUserHeight(height);
			spInfo.setUserWeight(weight);
			
			if(spInfo.getIsV_20FristOpen()){
				Intent intent = new Intent(InformationPerfectActivity.this,MainTabActivity.class);
				startActivity(intent);
				intent = new Intent(InformationPerfectActivity.this,ChoiceDeviceActivity.class);
				startActivity(intent);
			}
			
			spInfo.setIsV_20FristOpen(false);
        	InformationPerfectActivity.this.finish();
		}
	}
}
