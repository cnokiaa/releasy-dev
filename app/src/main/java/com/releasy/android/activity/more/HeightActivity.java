package com.releasy.android.activity.more;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.releasy.android.R;
import com.releasy.android.activity.main.BaseActivity;
import com.releasy.android.constants.HttpConstants;
import com.releasy.android.http.HttpUtils;
import com.releasy.android.http.ResolveJsonUtils;
import com.releasy.android.utils.SharePreferenceUtils;
import com.releasy.android.view.TopNavLayout;
import com.releasy.android.view.VerticalScaleScrollView;
import com.releasy.android.view.BaseScaleView.OnScrollListener;

public class HeightActivity extends BaseActivity{

	private SharePreferenceUtils spInfo;                    //SharePreference
	private TopNavLayout mTopNavLayout;                     //导航菜单栏
	private VerticalScaleScrollView verticalScaleScrollView;  //身高刻度尺
	private TextView heightTxt;                   //身高Txt
	private int height;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_height);
	        
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
		mTopNavLayout.setTitltTxt(R.string.height);
		mTopNavLayout.setLeftImgSrc(R.drawable.ic_nav_bar_back);
		mTopNavLayout.setRightImgSrc(R.drawable.ic_finish);          //设置导航栏右边按钮
	}
	
	protected void initViews() {
		mTopNavLayout = (TopNavLayout) findViewById(R.id.topNavLayout);
		
		verticalScaleScrollView = (VerticalScaleScrollView) findViewById(R.id.verticalScaleScrollView);
		heightTxt = (TextView) findViewById(R.id.height_txt);
				
	}

	protected void initEvents() {
		mTopNavLayout.setLeftImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				HeightActivity.this.finish();
			}});
		
		mTopNavLayout.setRightImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				progressDialog.show();
				putAsyncTask(new UpdateAsyncTask());
			}});
		
		verticalScaleScrollView.setOnScrollListener(new OnScrollListener() {
            public void onScaleScroll(int scale) {
            	heightTxt.setText("" + scale);
            	height = scale;
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
		String genderStr = spInfo.getUserSex();
		String birthdayStr =  spInfo.getUserBirthday();
		String weight = spInfo.getUserWeight() + "";
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uid", uid + ""));
		params.add(new BasicNameValuePair("phone", phonenumStr));
		params.add(new BasicNameValuePair("email", emailStr));
		params.add(new BasicNameValuePair("birthday", birthdayStr));
		params.add(new BasicNameValuePair("height", height+""));
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
					,HeightActivity.this,mScreenWidth,mScreenHeight);
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
			if(!HeightActivity.this.isFinishing() && progressDialog.isShowing())
				progressDialog.dismiss();
			else
				return;
			
			if(retStatus == HttpConstants.SUCCESS){
				spInfo.setUserHeight(height);
				Toast.makeText(HeightActivity.this, R.string.update_success, Toast.LENGTH_LONG).show();
				HeightActivity.this.finish();
			}
			else{
				Toast.makeText(HeightActivity.this, retMsg, Toast.LENGTH_LONG).show();
			}
		}
	}
}
