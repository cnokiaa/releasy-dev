package com.releasy.android.activity.more;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.releasy.android.R;
import com.releasy.android.ReleasyApplication;
import com.releasy.android.activity.main.BaseActivity;
import com.releasy.android.constants.HttpConstants;
import com.releasy.android.http.HttpUtils;
import com.releasy.android.http.ResolveJsonUtils;
import com.releasy.android.service.UpdataAppService;
import com.releasy.android.utils.StringUtils;
import com.releasy.android.utils.Utils;
import com.releasy.android.view.TopNavLayout;


/**
 * 关于页面
 * @author Lighting.Z
 *
 */
public class AboutActivity extends BaseActivity{

	private TopNavLayout mTopNavLayout;           //导航菜单栏
	private TextView versionNameTxt;              //版本名字
	private Button versionBtn;                    //版本检测
	private ReleasyApplication app;               //Application
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        
        init();            //初始化
        
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	
	}
	
	public void onDestroy() {
        super.onDestroy();
        clearAsyncTask();
    }
	
	/**
	 * 初始化
	 */
	private void init(){
		app = (ReleasyApplication) this.getApplication();
		initProgressDialog(this.getString(R.string.retrieving_version));
		
		initViews();    //初始化视图
		setTopNav();    //初始化导航栏
		initEvents();   //初始化点击事件
	}
	
	/**
	 * 初始化导航栏
	 */
	private void setTopNav(){
		mTopNavLayout.setTitltTxt(R.string.about_title);
		mTopNavLayout.setLeftImgSrc(R.drawable.ic_nav_bar_back);
	}
	
	/**
	 * 初始化视图
	 */
	protected void initViews() {
		mTopNavLayout = (TopNavLayout) findViewById(R.id.topNavLayout);
		versionNameTxt = (TextView) findViewById(R.id.versionNameTxt);
		versionBtn = (Button) findViewById(R.id.versionBtn);
		setVersionNameTxt();
	}

	/**
	 * 初始化点击事件
	 */
	protected void initEvents() {
		//导航栏返回按钮逻辑
		mTopNavLayout.setLeftImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				AboutActivity.this.finish();
			}});
		
		
		//版本检测点击
		versionBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				progressDialog.show();
				putAsyncTask(new VersionAsyncTask());
			}});
	}
	
	/**
	 * 设置版本号
	 */
	private void setVersionNameTxt(){
		PackageInfo info;
		try {
			info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
			String versionName = info.versionName;  
			versionNameTxt.setText("V" + versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 配置信息线程处理
	 */
	private class VersionAsyncTask extends AsyncTask<Void, Void, Boolean>{
		Bundle appVersionResult;
		Bundle headBundle;
		int retStatus;
		String retMsg ;
		
		protected Boolean doInBackground(Void... param) {
			List<NameValuePair> appVParams = new ArrayList<NameValuePair>();
			appVersionResult = HttpUtils.doPost(appVParams,HttpConstants.CHECK_APP_VERSION
					, AboutActivity.this,mScreenWidth,mScreenHeight);
			
			if(appVersionResult.getInt("code") != HttpConstants.SUCCESS){
				retStatus = appVersionResult.getInt("code");
				retMsg = getResources().getString(R.string.network_anomaly);
				return null;
			}
			
			headBundle = ResolveJsonUtils.getJsonHead(appVersionResult.getString("content"));
			retStatus = headBundle.getInt("retStatus");
			retMsg = headBundle.getString("retMsg");
			showLogD("App版本信息   retStatus : " + retStatus + "    retMsg : " + retMsg);
			
			return null;
		}
		
		protected void onPostExecute(Boolean result) {  
			if(AboutActivity.this.isFinishing() || !progressDialog.isShowing())
				return;
			
			progressDialog.dismiss();
			if(retStatus == HttpConstants.SUCCESS){
				Bundle bundle = ResolveJsonUtils.getAppVersion(appVersionResult.getString("content"));
				String message = bundle.getString("message");
				String updateStrategy = bundle.getString("updateStrategy");
				String downloadUrl = bundle.getString("downloadUrl");
				String newVersion = bundle.getString("newVersion");

				showLogD("message : " + message + "    updateStrategy : " + updateStrategy 
						+ "    downloadUrl : " + downloadUrl + "    newVersion : " + newVersion);
				
				app.setAppUpdataInfo(message, downloadUrl, newVersion);
				checkAppVersion();
			}
			else{
				Toast.makeText(AboutActivity.this, retMsg, Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * 检测版本是否为最新
	 */
	private void checkAppVersion(){
		try {
			String newVersion = app.getAppNewVersion();
			PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
			String oldVersion = info.versionName;  
			
			showLogD("newVersion : " + newVersion + "    oldVersion : " + oldVersion);
			if(StringUtils.isBlank(newVersion) || StringUtils.isBlank(oldVersion))
				return;
			
			if(!newVersion.equals(oldVersion))
				showNewVersionDialog();
			else
				Toast.makeText(this, R.string.latest_version, Toast.LENGTH_LONG).show();
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}  
		
	}
	
	/**
	 * App版本更新Dialog
	 */
	private void showNewVersionDialog(){
		// 发现新版本，提示用户更新
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.updata_app_title)
				.setMessage(app.getAppUpdataMsg())
				.setPositiveButton(R.string.updata,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent updateIntent = new Intent(AboutActivity.this,UpdataAppService.class);
								startService(updateIntent);
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
		alert.create().show();
	}

}
