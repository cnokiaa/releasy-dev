package com.releasy.android.activity.more;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.releasy.android.R;
import com.releasy.android.activity.main.BaseActivity;
import com.releasy.android.constants.HttpConstants;
import com.releasy.android.http.HttpUtils;
import com.releasy.android.http.ResolveJsonUtils;
import com.releasy.android.utils.SharePreferenceUtils;
import com.releasy.android.utils.StringUtils;
import com.releasy.android.view.TopNavLayout;

public class PhonenumOrEmailAcrivity extends BaseActivity{

	private SharePreferenceUtils spInfo;                    //SharePreference
	private TopNavLayout mTopNavLayout;                     //导航菜单栏
	private TextView phonenumOrEmailTxt;                    //提示Txt
	private EditText phoneOrEmailEdit;                      //输入框
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_phonenum_or_email);
	        
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
		if(getResources().getConfiguration().locale.getCountry().equals("CN"))
			mTopNavLayout.setTitltTxt(R.string.phone_number);
		else
			mTopNavLayout.setTitltTxt(R.string.email);
		mTopNavLayout.setLeftImgSrc(R.drawable.ic_nav_bar_back);
		mTopNavLayout.setRightImgSrc(R.drawable.ic_finish);          //设置导航栏右边按钮
	}
	
	/**
	 * 初始化控件
	 */
	protected void initViews() {
		mTopNavLayout = (TopNavLayout) findViewById(R.id.topNavLayout);
		phonenumOrEmailTxt = (TextView) findViewById(R.id.phonenum_or_email_txt);
		phoneOrEmailEdit = (EditText) findViewById(R.id.phone_or_email_edit);
	}

	/**
	 * 初始化数据
	 */
	private void initData(){
		String phonenumOrEmailStr;
		if(getResources().getConfiguration().locale.getCountry().equals("CN")){
			phonenumOrEmailTxt.setText(R.string.please_enter_your_phone_number);
			phoneOrEmailEdit.setHint(R.string.please_enter_your_phone_number);
			phonenumOrEmailStr = spInfo.getPhoneNum();
		}
		else{
			phonenumOrEmailTxt.setText(R.string.please_enter_your_email);
			phoneOrEmailEdit.setHint(R.string.please_enter_your_email);
			phonenumOrEmailStr = spInfo.getEmail();
		}
		
		if(!StringUtils.isBlank(phonenumOrEmailStr))
			phoneOrEmailEdit.setText(phonenumOrEmailStr);
	}
	
	/**
	 * 初始化监听函数
	 */
	protected void initEvents() {
		mTopNavLayout.setLeftImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				PhonenumOrEmailAcrivity.this.finish();
			}});
		
		mTopNavLayout.setRightImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				if(!checkPhoneNumOrEmail())
					return;
				
				collapseSoftInputMethod(phoneOrEmailEdit); //收起键盘
				progressDialog.show();
				putAsyncTask(new UpdateAsyncTask());

			}});
	}
	
	/**
	 * 检查输入信息
	 */
	private boolean checkPhoneNumOrEmail(){
		if(getResources().getConfiguration().locale.getCountry().equals("CN")){
			if(StringUtils.isBlank(phoneOrEmailEdit.getText().toString())){
				Toast.makeText(this, R.string.please_enter_your_phone_number, Toast.LENGTH_LONG).show();
				return false;
			}
		}
		else{
			if(StringUtils.isBlank(phoneOrEmailEdit.getText().toString())){
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
	private List<NameValuePair> updateParams(){
		String phonenumStr = "";
		String emailStr = "";
		int uid = spInfo.getUId();
		if(getResources().getConfiguration().locale.getCountry().equals("CN"))
			phonenumStr = phoneOrEmailEdit.getText().toString();
		else
			emailStr = phoneOrEmailEdit.getText().toString();
		
		String birthdayStr = spInfo.getUserBirthday();
		String height = spInfo.getUserHeight() + "";
		String weight = spInfo.getUserWeight() + "";
		String genderStr = spInfo.getUserSex();
		
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
					,PhonenumOrEmailAcrivity.this,mScreenWidth,mScreenHeight);
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
			if(!PhonenumOrEmailAcrivity.this.isFinishing() && progressDialog.isShowing())
				progressDialog.dismiss();
			else
				return;
			
			if(retStatus == HttpConstants.SUCCESS){
				String phone = phoneOrEmailEdit.getText().toString();
				spInfo.setPhoneNum(phone);
				Toast.makeText(PhonenumOrEmailAcrivity.this, R.string.update_success, Toast.LENGTH_LONG).show();
				PhonenumOrEmailAcrivity.this.finish();
			}
			else{
				Toast.makeText(PhonenumOrEmailAcrivity.this, retMsg, Toast.LENGTH_LONG).show();
			}
		}
	}

}
