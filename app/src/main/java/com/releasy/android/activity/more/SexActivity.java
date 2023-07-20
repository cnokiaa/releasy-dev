package com.releasy.android.activity.more;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.releasy.android.R;
import com.releasy.android.activity.main.BaseActivity;
import com.releasy.android.constants.HttpConstants;
import com.releasy.android.http.HttpUtils;
import com.releasy.android.http.ResolveJsonUtils;
import com.releasy.android.utils.SharePreferenceUtils;
import com.releasy.android.view.TopNavLayout;

public class SexActivity extends BaseActivity{

	private SharePreferenceUtils spInfo;                    //SharePreference
	private TopNavLayout mTopNavLayout;                     //导航菜单栏
	private RadioGroup genderRgs;                           //性别单选按钮
	private RadioButton boyRbtn;
	private RadioButton girlRbtn;
	private String genderStr = "boy";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_sex);
	        
	    init();            //初始化
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		spInfo = new SharePreferenceUtils(this); 
		initProgressDialog(this.getResources().getString(R.string.submitting_data));
		
		initViews();    //初始化视图
		initData();     //初始化数据
		setTopNav();    //初始化导航栏
		initEvents();   //初始化点击事件
	}
	
	/**
	 * 初始化导航栏
	 */
	private void setTopNav(){
		mTopNavLayout.setTitltTxt(R.string.sex);
		mTopNavLayout.setLeftImgSrc(R.drawable.ic_nav_bar_back);
		mTopNavLayout.setRightImgSrc(R.drawable.ic_finish);          //设置导航栏右边按钮
	}
	
	/**
	 * 初始化控件
	 */
	protected void initViews() {
		mTopNavLayout = (TopNavLayout) findViewById(R.id.topNavLayout);
		genderRgs = (RadioGroup) findViewById(R.id.gender_rgs);
		boyRbtn = (RadioButton) findViewById(R.id.boy_rg);
		girlRbtn = (RadioButton) findViewById(R.id.girl_rg);
	}

	/**
	 * 初始化数据
	 */
	private void initData(){
		genderStr = spInfo.getUserSex();
		
		if(genderStr.equals("boy"))
			boyRbtn.setChecked(true);
		else
			girlRbtn.setChecked(true);
	}
	
	/**
	 * 初始化监听函数
	 */
	protected void initEvents() {
		mTopNavLayout.setLeftImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				SexActivity.this.finish();
			}});
		
		mTopNavLayout.setRightImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				progressDialog.show();
				putAsyncTask(new UpdateAsyncTask());
			}});
		
		genderRgs.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
				if(R.id.boy_rg == checkedId)
					genderStr = "boy";
				else
					genderStr = "girl";
				
				spInfo.setUserSex(genderStr);
			}});
	}

	/***************************************网络通信*****************************************/
	
	/**
	 * 请求参数封装
	 */
	private List<NameValuePair> updateParams(){
		int uid = spInfo.getUId();
		
		String phonenumStr = spInfo.getPhoneNum();
		String emailStr = spInfo.getEmail();
		String birthdayStr = spInfo.getUserBirthday();
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
					,SexActivity.this,mScreenWidth,mScreenHeight);
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
			if(!SexActivity.this.isFinishing() && progressDialog.isShowing())
				progressDialog.dismiss();
			else
				return;
			
			if(retStatus == HttpConstants.SUCCESS){
				spInfo.setUserSex(genderStr);
				Toast.makeText(SexActivity.this, R.string.update_success, Toast.LENGTH_LONG).show();
				SexActivity.this.finish();
			}
			else{
				Toast.makeText(SexActivity.this, retMsg, Toast.LENGTH_LONG).show();
			}
		}
	}
}
