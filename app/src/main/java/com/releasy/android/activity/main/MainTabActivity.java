package com.releasy.android.activity.main;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.emokit.sdk.util.SDKAppInit;
import com.releasy.android.MainActivity;
import com.releasy.android.R;
import com.releasy.android.ReleasyApplication;
import com.releasy.android.activity.device.DeviceMainFragment;
import com.releasy.android.activity.more.MoreMainFragment;
import com.releasy.android.activity.music.MusicMainFragment;
import com.releasy.android.activity.releasy.ReleasyMainFragment;
import com.releasy.android.bean.DeviceBean;
import com.releasy.android.constants.Constants;
import com.releasy.android.constants.HttpConstants;
import com.releasy.android.db.DeviceDBUtils;
import com.releasy.android.db.ReleasyDatabaseHelper;
import com.releasy.android.http.HttpUtils;
import com.releasy.android.http.ResolveJsonUtils;
import com.releasy.android.service.BleWorkService;
import com.releasy.android.service.UpdataAppService;
import com.releasy.android.utils.SharePreferenceUtils;
import com.releasy.android.utils.StringUtils;
import com.releasy.android.utils.Utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * 主页Tab 页面
 * @author Lighting.Z
 *
 */
public class MainTabActivity extends BaseActivity{
	
	/**
     * Called when the activity is first created.
     */
    private RadioGroup rgs;                       //底端Tab
    private RadioButton moreRb;                   //更多Tab按钮
    public List<Fragment> fragments = new ArrayList<Fragment>();  //内容页
    private ReleasyApplication app;               //Application
    private SharePreferenceUtils spInfo;          //SharePreference
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintab);
        //SDKAppInit.createInstance(this);
        
        init(); //初始化
        putAsyncTask(new CheckDeviceIdAsyncTask());
        
        
        
    }
    
    protected void onResume() {
		super.onResume();
		registerReceiver(mHomeKeyEventReceiver, new IntentFilter(  
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));  
	}
    
    /**
     * 初始化Fragment内容页
     */
    private void initFragment(){
    	fragments.add(new ReleasyMainFragment());
        //fragments.add(new MusicMainFragment());
        fragments.add(new DeviceMainFragment());
        fragments.add(new MoreMainFragment());

        rgs = (RadioGroup) findViewById(R.id.tabs_rg);
        moreRb = (RadioButton) findViewById(R.id.tab_rb_more);
        setMoreRbImg();  //设置图标
        
        FragmentTabAdapter tabAdapter = new FragmentTabAdapter(this, fragments, R.id.tab_content, rgs);
        tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener(){
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
                //showLogD("Extra---- " + index + " checked!!! ");
            }
        });
    }
    
    protected void onDestroy() {
    	app.unbindService();
		super.onDestroy();
	}

	protected void initViews() {}
	protected void initEvents() {}

	/**
	 * 初始化
	 */
	private void init(){
		app = (ReleasyApplication) MainTabActivity.this.getApplication();
        spInfo = new SharePreferenceUtils(this); 

		initFragment(); //初始化Fragment内容页
		
		checkAppVersion();   //检测版本
		
	   
	}
	
	/**
	 * 重设更多Tab图标
	 */
	public void setMoreRbImg(){
		Drawable img = null;
		if(spInfo.getHasFeedbackNotify())
			img = getResources().getDrawable(R.drawable.ic_tab_more_new_msg);
		else
			img = getResources().getDrawable(R.drawable.ic_tab_more);
		img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
		moreRb.setCompoundDrawables(null, img, null, null); //设置图标
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
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}  
		
	}
	
	/**
	 * App版本更新Dialog
	 */
	private void showNewVersionDialog(){
		// 发现新版本，提示用户更新
		Builder alert = new Builder(this);
		alert.setTitle(R.string.updata_app_title)
				.setMessage(app.getAppUpdataMsg())
				.setPositiveButton(R.string.updata,
						new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent updateIntent = new Intent(MainTabActivity.this,UpdataAppService.class);
								startService(updateIntent);
							}
						})
				.setNegativeButton(R.string.cancel,
						new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
		alert.create().show();
	}
	
	
	private boolean isExit;   //标示变量  用于表示是否退出程序  true为退出 false为不退出
	/**
	 * 重新返回按键点击事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if (keyCode == KeyEvent.KEYCODE_BACK) {  
            if(app.getIsWorking()){
            	showExitDialog();
            }
            else
            	exit(); 
            return false;  
        } 
        else   
            return super.onKeyDown(keyCode, event);  
    }  

	/**
	 * 在运行状态删除最后一个设备时弹出dialog
	 */
	private void showExitDialog(){
		// 发现新版本，提示用户更新
		Builder alert = new Builder(MainTabActivity.this);
		alert.setTitle(R.string.exit)
				.setMessage(R.string.exit_msg)
				.setPositiveButton(R.string.confirm,
						new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								spInfo.setUserRecord(app.getUserRecord());
								app.cancelBackstageNotificition();
								BleWorkService mBluetoothLeService = app.getBleService();   
								mBluetoothLeService.closeAll();
								unregisterReceiver(mHomeKeyEventReceiver); //解绑广播
								spInfo.setLogout(spInfo.getLogout() + app.getLogOut());
								System.exit(0);
							}
						})
				.setNegativeButton(R.string.cancel,
						new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
		alert.create().show();
	}
	
	/**
	 * 退出应用方法
	 */
	public void exit(){  
		if (!isExit) {  
			isExit = true;  
			Toast.makeText(this, R.string.again_to_exit, Toast.LENGTH_SHORT).show();  
			mHandler.sendEmptyMessageDelayed(0, 2000);  
	    } 
		else {
			spInfo.setUserRecord(app.getUserRecord());
			app.cancelBackstageNotificition();
			BleWorkService mBluetoothLeService = app.getBleService();   
			mBluetoothLeService.closeAll();
			unregisterReceiver(mHomeKeyEventReceiver); //解绑广播
			spInfo.setLogout(spInfo.getLogout() + app.getLogOut());
			System.exit(0);
		}  
	}  
	
	/**
	 * 超过两秒将标示变量 isExit设置为false
	 */
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {  
			super.handleMessage(msg);  
			isExit = false;  
		}  
	};  

	
	/** 
     * 监听是否点击了home键将客户端推到后台 
     */  
    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {  
        String SYSTEM_REASON = "reason";  
        String SYSTEM_HOME_KEY = "homekey";  
        String SYSTEM_HOME_KEY_LONG = "recentapps";  
            
        public void onReceive(Context context, Intent intent) {  
            String action = intent.getAction();  
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {  
                String reason = intent.getStringExtra(SYSTEM_REASON);  
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {  
                     //表示按了home键,程序到了后台  
                	
                	if(!Utils.isBackground(MainTabActivity.this) && app.getIsWorking())
                		app.initBackstageNotificition();
                    
                }else if(TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)){  
                    //表示长按home键,显示最近使用的程序列表  
                }  
            }   
        }  
    }; 
    
    /**
	 * 请求参数封装
	 */
	private List<NameValuePair> getParams(String deviceAddress){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uid", spInfo.getUId() +""));
		params.add(new BasicNameValuePair("deviceAddress", deviceAddress));
		//showLogD("uid : " + spInfo.getUId() + "    deviceAddress : " + deviceAddress);
		return params;
	}
    
    /**
	 * 设备检测线程处理
	 */
	private class CheckDeviceIdAsyncTask extends AsyncTask<Void, Void, Boolean>{
		private Bundle mResult;
		private Bundle headBundle;
		private int retStatus;
		private String retMsg ;
		
		protected Boolean doInBackground(Void... param) {
			ReleasyDatabaseHelper db = DeviceDBUtils.openData(MainTabActivity.this);
			List<DeviceBean> deviceList = DeviceDBUtils.searchData(db);
			
			showLogD("CheckDeviceIdAsyncTask start!");
			
			for(int i = 0; i < deviceList.size(); i++){
				showLogD("device : " + deviceList.get(i).getUuid() + "    Status : " + deviceList.get(i).getVerifyStatus());
				if(deviceList.get(i).getVerifyStatus() != 0 || StringUtils.isBlank(deviceList.get(i).getUuid()))
					continue;
				
				List<NameValuePair> params = getParams(deviceList.get(i).getUuid());
				Bundle mResult = HttpUtils.doPost(params,HttpConstants.CHECK_DEVICE_ID
						,MainTabActivity.this,mScreenWidth,mScreenHeight);
				
				showLogD("code : " + mResult.getInt("code"));
				if(mResult.getInt("code") != HttpConstants.SUCCESS){
					retStatus = mResult.getInt("code");
					retMsg = getResources().getString(R.string.network_anomaly);
					return null;
				}
				
				headBundle = ResolveJsonUtils.getJsonHead(mResult.getString("content"));
				retStatus = headBundle.getInt("retStatus");
				retMsg = headBundle.getString("retMsg");
				showLogD("address : " + deviceList.get(i).getAddress() + "    retStatus : " + retStatus 
						+ "    retMsg : " + retMsg);
				
				if(retStatus == HttpConstants.SUCCESS){
					int isValid = ResolveJsonUtils.checkDeviceIdResult(mResult.getString("content"));
					showLogD("isValid : " + isValid);
					if(isValid == 1)
						DeviceDBUtils.UpdataDeviceVerify(db,deviceList.get(i).getAddress(),1);
					if(isValid == 0)
						DeviceDBUtils.UpdataDeviceVerify(db,deviceList.get(i).getAddress(),2);
				}
			}
			return null;
		}
		
		protected void onPostExecute(Boolean result) {  
				
		}
	}
}
