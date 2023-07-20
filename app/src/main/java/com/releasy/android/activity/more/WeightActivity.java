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
import com.releasy.android.view.HorizontalScaleScrollView;
import com.releasy.android.view.TopNavLayout;
import com.releasy.android.view.BaseScaleView.OnScrollListener;

public class WeightActivity extends BaseActivity {

	private SharePreferenceUtils spInfo;                          //SharePreference
	private TopNavLayout mTopNavLayout;                           //导航菜单栏
	private HorizontalScaleScrollView horizontalScaleScrollView;  //体重刻度尺
	private TextView weightTxt;                                   //体重Txt
	private int weight;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_weight);
	        
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
		mTopNavLayout.setTitltTxt(R.string.weight);
		mTopNavLayout.setLeftImgSrc(R.drawable.ic_nav_bar_back);
		mTopNavLayout.setRightImgSrc(R.drawable.ic_finish);          //设置导航栏右边按钮
	}
	
	protected void initViews() {
		mTopNavLayout = (TopNavLayout) findViewById(R.id.topNavLayout);
		
		horizontalScaleScrollView = (HorizontalScaleScrollView) findViewById(R.id.horizontalScaleScrollView);
		weightTxt = (TextView) findViewById(R.id.weight_txt);
	}

	protected void initEvents() {
		mTopNavLayout.setLeftImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				WeightActivity.this.finish();
			}});
		
		mTopNavLayout.setRightImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				progressDialog.show();
				putAsyncTask(new UpdateAsyncTask());
			}});
		
		horizontalScaleScrollView.setOnScrollListener(new OnScrollListener() {
            public void onScaleScroll(int scale) {
            	weightTxt.setText("" + scale);
            	weight = scale;
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
		String height = spInfo.getUserHeight() + "";
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uid", uid + ""));
		params.add(new BasicNameValuePair("phone", phonenumStr));
		params.add(new BasicNameValuePair("email", emailStr));
		params.add(new BasicNameValuePair("birthday", birthdayStr));
		params.add(new BasicNameValuePair("height", height));
		params.add(new BasicNameValuePair("weight", weight + ""));
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
					,WeightActivity.this,mScreenWidth,mScreenHeight);
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
			if(!WeightActivity.this.isFinishing() && progressDialog.isShowing())
				progressDialog.dismiss();
			else
				return;
			
			if(retStatus == HttpConstants.SUCCESS){
				spInfo.setUserWeight(weight);
				Toast.makeText(WeightActivity.this, R.string.update_success, Toast.LENGTH_LONG).show();
				WeightActivity.this.finish();
			}
			else{
				Toast.makeText(WeightActivity.this, retMsg, Toast.LENGTH_LONG).show();
			}
		}
	}
}
