package com.releasy.android.activity.more;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.releasy.android.R;
import com.releasy.android.activity.main.BaseActivity;
import com.releasy.android.constants.HttpConstants;
import com.releasy.android.http.HttpUtils;
import com.releasy.android.http.ResolveJsonUtils;
import com.releasy.android.utils.SharePreferenceUtils;
import com.releasy.android.view.TopNavLayout;
import com.releasy.android.view.wheelview.NumericWheelAdapter;
import com.releasy.android.view.wheelview.OnWheelScrollListener;
import com.releasy.android.view.wheelview.WheelView;

public class AgeActivity extends BaseActivity {

	private SharePreferenceUtils spInfo;                    //SharePreference
	private TopNavLayout mTopNavLayout;                     //导航菜单栏
	
	//生日相关
	private String birthdayStr = "1990-01-01";
	private LayoutInflater inflater = null;
	private WheelView year;
	private WheelView month;
	private WheelView day;
	private int mYear=1990;
	private int mMonth=0;
	private int mDay=1;
	private LinearLayout dataLayout;
	//private String age; 
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_age);
	        
	    init();            //初始化
	}
	
	
	private void init(){
		spInfo = new SharePreferenceUtils(this); 
		initProgressDialog(this.getResources().getString(R.string.submitting_data));
		
		initViews();    //初始化视图
		setTopNav();    //初始化导航栏
		initEvents();   //初始化点击事件
	}
	
	/**
	 * 初始化导航栏
	 */
	private void setTopNav(){
		mTopNavLayout.setTitltTxt(R.string.age);
		mTopNavLayout.setLeftImgSrc(R.drawable.ic_nav_bar_back);
		mTopNavLayout.setRightImgSrc(R.drawable.ic_finish);          //设置导航栏右边按钮
	}
	
	protected void initViews() {
		mTopNavLayout = (TopNavLayout) findViewById(R.id.topNavLayout);
		
		inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		dataLayout = (LinearLayout) findViewById(R.id.data_layout);
		dataLayout.addView(getDataPick());
	}

	protected void initEvents() {
		mTopNavLayout.setLeftImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				AgeActivity.this.finish();
			}});
		
		mTopNavLayout.setRightImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				progressDialog.show();
				putAsyncTask(new UpdateAsyncTask());
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
			//Toast.makeText(AgeActivity.this, birthdayStr, Toast.LENGTH_LONG).show();
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
	
/***************************************网络通信*****************************************/
	
	/**
	 * 请求参数封装
	 */
	private List<NameValuePair> updateParams(){
		int uid = spInfo.getUId();
		
		String phonenumStr = spInfo.getPhoneNum();
		String emailStr = spInfo.getEmail();
		String genderStr = spInfo.getUserSex();
		String height = spInfo.getUserHeight() + "";
		String weight = spInfo.getUserWeight() + "";
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uid", uid + ""));
		params.add(new BasicNameValuePair("phone", phonenumStr));
		params.add(new BasicNameValuePair("email", emailStr));
		params.add(new BasicNameValuePair("birthday", birthdayStr));
		params.add(new BasicNameValuePair("height", height));
		params.add(new BasicNameValuePair("weight", weight));
		if(genderStr.equals("boy"))
			params.add(new BasicNameValuePair("sex", "1"));
		else
			params.add(new BasicNameValuePair("sex", "0"));
		return params;
	}
	
	/**
	 * 更新线程处理
	 */
	private class UpdateAsyncTask extends AsyncTask<Void, Void, Boolean>{
		private Bundle mResult;
		private Bundle headBundle;
		private int retStatus;
		private String retMsg ;
		
		protected Boolean doInBackground(Void... param) {
			List<NameValuePair> params = updateParams();
			mResult = HttpUtils.doPost(params,HttpConstants.UPDATE_USERINFO
					,AgeActivity.this,mScreenWidth,mScreenHeight);
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
			if(!AgeActivity.this.isFinishing() && progressDialog.isShowing())
				progressDialog.dismiss();
			else
				return;
			
			if(retStatus == HttpConstants.SUCCESS){
				spInfo.setUserBirthday(birthdayStr);
				//spInfo.setUserAge(age);
				Toast.makeText(AgeActivity.this, R.string.update_success, Toast.LENGTH_LONG).show();
				AgeActivity.this.finish();
			}
			else{
				Toast.makeText(AgeActivity.this, retMsg, Toast.LENGTH_LONG).show();
			}
		}
	}
}
