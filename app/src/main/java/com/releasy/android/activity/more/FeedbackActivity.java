package com.releasy.android.activity.more;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.releasy.android.MainActivity;
import com.releasy.android.R;
import com.releasy.android.activity.main.BaseActivity;
import com.releasy.android.adapter.FeedbackAdapter;
import com.releasy.android.bean.FeedbackBean;
import com.releasy.android.constants.HttpConstants;
import com.releasy.android.db.FeedbackDBUtils;
import com.releasy.android.db.ReleasyDatabaseHelper;
import com.releasy.android.http.HttpUtils;
import com.releasy.android.http.ResolveJsonUtils;
import com.releasy.android.utils.SharePreferenceUtils;
import com.releasy.android.utils.Utils;
import com.releasy.android.view.TopNavLayout;

/**
 * 意见反馈页面
 * @author Lighting.Z
 *
 */
public class FeedbackActivity extends BaseActivity{

	private TopNavLayout mTopNavLayout;           //导航菜单栏
	//编辑框
	private EditText feedbackEdit;                //意见编辑框
	private TextView feedbackBtn;                 //意见发送按钮
	//聊天list
	private ListView feedbackList;                //意见列表
	private FeedbackAdapter feedbackAdapter;      //意见Adapter
	private List<FeedbackBean> dataArrays;        //意见信息列表
	private SharePreferenceUtils spInfo;          //SharePreference
	private ReleasyDatabaseHelper db ;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_feedback);
	        
	    init();    //初始化
	}
	
	public void onDestroy() {
        super.onDestroy();
        clearAsyncTask();
    }
	
	/**
	 * 初始化
	 */
	private void init(){
		db = FeedbackDBUtils.openData(this);
		dataArrays = new ArrayList<FeedbackBean>();
		spInfo = new SharePreferenceUtils(this); 
		
		initProgressDialog(this.getResources().getString(R.string.loading));
		initViews();     //初始化视图
		initEvents();    //初始化点击
		setTopNav();     //初始化导航栏
		checkHasFeedbackNotify(); //检测是否有新消息
		
	} 
	 
	/**
	 * 初始化导航栏
	 */
	private void setTopNav(){
		mTopNavLayout.setTitltTxt(R.string.feedback);
		mTopNavLayout.setLeftImgSrc(R.drawable.ic_nav_bar_back);
		
	}
	
	/**
	 * 初始化视图
	 */
	protected void initViews() {
		mTopNavLayout = (TopNavLayout) findViewById(R.id.topNavLayout);
		feedbackEdit = (EditText) findViewById(R.id.feedbackEdit);
		feedbackBtn = (TextView) findViewById(R.id.feedbackBtn);
		feedbackList = (ListView) findViewById(R.id.feedbackList);
	}

	/**
	 * 初始化点击事件
	 */
	protected void initEvents() {
		//导航栏返回按钮逻辑
		mTopNavLayout.setLeftImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				FeedbackActivity.this.finish();
			}});
		
		//发送按钮逻辑
		feedbackBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				if(!checkFeedback())
					return ;
				
				String date = Utils.getTime();
				String msg = feedbackEdit.getText().toString();
				putAsyncTask(new SeedSuggestionAsyncTask(msg,date));
				
				FeedbackBean bean = new FeedbackBean(date,msg, 1);
				dataArrays.add(0, bean);
				feedbackAdapter = new FeedbackAdapter(FeedbackActivity.this, dataArrays);
				feedbackList.setAdapter(feedbackAdapter);
				feedbackList.setSelection(feedbackList.getCount() - 1);
				
				feedbackEdit.setText("");
				collapseSoftInputMethod(feedbackEdit);//收起键盘
			}});
	}
	
	/**
	 * 检测
	 * @return
	 */
	private boolean checkFeedback(){
		String feedback = feedbackEdit.getText().toString();
		if(com.releasy.android.utils.StringUtils.isBlank(feedback)){
			return false;
		}
		
		return true;
	}
	
	/**
	 * 检测
	 */
	private void checkHasFeedbackNotify(){
		if(spInfo.getHasFeedbackNotify()){
			progressDialog.show();
			putAsyncTask(new GetSessionAsyncTask());
		}
		else{
			initLoadData();
		}
	}
	
	/**
	 * 加载本地数据库数据
	 */
	public void initLoadData(){
		int uid = spInfo.getUId();
		dataArrays = FeedbackDBUtils.searchRoomMusicData(db,uid);
		
		feedbackAdapter = new FeedbackAdapter(this, dataArrays);
		feedbackList.setAdapter(feedbackAdapter);
		feedbackList.setSelection(feedbackList.getCount() - 1);
	}
	
	/**
	 * 保存数据到数据库中
	 */
	private void saveNewDataToDB(){
		FeedbackDBUtils.deleteData(db);
		
		int uid = spInfo.getUId();
		for(int i = dataArrays.size() - 1; i >= 0; i--){
			FeedbackDBUtils.insertData(db, uid, dataArrays.get(i).getDate(), dataArrays.get(i).getMsg()
					, dataArrays.get(i).getMsgType());
		}
		
		spInfo.setHasFeedbackNotify(false);
		FeedbackDBUtils.searchRoomMusicData(db,uid);
	}
	
	/***************************************网络通信*****************************************/
	/**
	 * 请求参数封装
	 */
	private List<NameValuePair> getSessionListParams(){
		int uid = spInfo.getUId();
		showLogD("uid : " + uid);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("offset", "0"));
		params.add(new BasicNameValuePair("size", "99"));
		params.add(new BasicNameValuePair("uid", uid + ""));
		return params;
	}
	
	/**
	 * 会话列表线程处理
	 */
	private class GetSessionAsyncTask extends AsyncTask<Void, Void, Boolean>{
		private Bundle mResult;
		private Bundle headBundle;
		private int retStatus;
		private String retMsg ;
		
		protected Boolean doInBackground(Void... param) {
			List<NameValuePair> params = getSessionListParams();
			mResult = HttpUtils.doPost(params,HttpConstants.GET_SESSION_LIST
					,FeedbackActivity.this,mScreenWidth,mScreenHeight);
			if(mResult.getInt("code") != HttpConstants.SUCCESS){
				retStatus = mResult.getInt("code");
				retMsg = getResources().getString(R.string.network_anomaly);
				return null;
			}
			
			headBundle = ResolveJsonUtils.getJsonHead(mResult.getString("content"));
			retStatus = headBundle.getInt("retStatus");
			retMsg = headBundle.getString("retMsg");
			showLogD("retStatus : " + retStatus + "    retMsg : " + retMsg);
			
			if(retStatus == HttpConstants.SUCCESS){
				Bundle bundle = ResolveJsonUtils.getSessionList(mResult.getString("content"));
				int total = bundle.getInt("total");
				dataArrays = (List<FeedbackBean>) bundle.getSerializable("dataList");

				if(dataArrays != null){
					saveNewDataToDB();
				}
			}
			
			return null;
		}
		
		protected void onPostExecute(Boolean result) {  
			if(!FeedbackActivity.this.isFinishing() && progressDialog.isShowing())
				progressDialog.dismiss();
			else
				return;
			
			if(retStatus == HttpConstants.SUCCESS){
				feedbackAdapter = new FeedbackAdapter(FeedbackActivity.this, dataArrays);
				feedbackList.setAdapter(feedbackAdapter);
				feedbackList.setSelection(feedbackList.getCount() - 1);
			}
			else{
				Toast.makeText(FeedbackActivity.this, retMsg, Toast.LENGTH_LONG).show();
			}
		}
	}
	
	
	/**
	 * 请求参数封装
	 */
	private List<NameValuePair> seedSuggestionParams(String feedback){
		int uid = spInfo.getUId();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uid", uid + ""));
		params.add(new BasicNameValuePair("content", feedback));
		return params;
	}
	
	/**
	 * 反馈发送线程处理
	 */
	private class SeedSuggestionAsyncTask extends AsyncTask<Void, Void, Boolean>{
		private Bundle mResult;
		private Bundle headBundle;
		private int retStatus;
		private String retMsg ;
		private String msg;
		private String date;
		
		public SeedSuggestionAsyncTask(String msg, String date){
			this.date = date;
			this.msg = msg;
		}
		
		protected Boolean doInBackground(Void... param) {
			List<NameValuePair> params = seedSuggestionParams(msg);
			mResult = HttpUtils.doPost(params,HttpConstants.SEED_SUGGESTION
					,FeedbackActivity.this,mScreenWidth,mScreenHeight);
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
			if(FeedbackActivity.this.isFinishing())
				return;
			
			if(retStatus == HttpConstants.SUCCESS){
				int uid = spInfo.getUId();
				FeedbackDBUtils.insertData(db, uid, date, msg, 1);
				
				FeedbackDBUtils.searchRoomMusicData(db, uid);
			}
		}
	}
	
}
