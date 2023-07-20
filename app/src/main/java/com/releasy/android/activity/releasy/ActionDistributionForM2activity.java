package com.releasy.android.activity.releasy;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.releasy.android.R;
import com.releasy.android.ReleasyApplication;
import com.releasy.android.activity.main.RelesyBaseActivity;
import com.releasy.android.activity.music.ISOMusicActivity;
import com.releasy.android.bean.ActionBean;
import com.releasy.android.bean.DeviceBean;
import com.releasy.android.constants.Constants;
import com.releasy.android.db.DeviceDBUtils;
import com.releasy.android.db.ReleasyDatabaseHelper;
import com.releasy.android.service.BleWorkService;
import com.releasy.android.service.MusicService;
import com.releasy.android.utils.CountdownTimerUtils;
import com.releasy.android.utils.SendOrderOutTimeThread;
import com.releasy.android.utils.SharePreferenceUtils;
import com.releasy.android.view.BottomActionView;

public class ActionDistributionForM2activity extends RelesyBaseActivity{

	private final int TO_MUISC_ROOM = 101;        //标识
	private ImageView navLeftImg;                 //导航菜单栏返回键
	private ImageView navRightImg;                //导航菜单栏音乐键
	private TextView navTxt;                      //导航菜单栏名称
	private TextView timeTxt;                     //时间控件
	private ImageView switchImg;                  //开关按钮
	private TextView m2mActionTxt;                //M2主机动作控件、
	private TextView m2mStrengthTxt;              //M2主机力度Txt
	private ImageView m2mSubtractBtn;             //M2主机力度减按钮
	private ImageView m2mAddBtn;                  //M2主机力度加按钮
	private TextView m2sActionTxt;                //M2副机动作控件
	private TextView m2sStrengthTxt;              //M2副机力度Txt
	private ImageView m2sSubtractBtn;             //M2副机力度减按钮
	private ImageView m2sAddBtn;                  //M2副机力度加按钮
	private BottomActionView m2mBottomActionView;
	private BottomActionView m2sBottomActionView;
	
	//private PickerView powerPv;                   //力度调整控件

	private String roomName;                      //放松馆名字
	private int roomId;                           //放松馆Id
	private int roomType;                         //放松馆Type
	private ActionBean action;                    //按摩动作信息
	private List<DeviceBean> deviceList;          //设备列表
	private ReleasyDatabaseHelper db;             //数据库
	private int duration = 30;                    //按摩持续时间  (min)
	
	private ReleasyApplication app;               //Application
	private SharePreferenceUtils spInfo;          //SharePreference
	private BleWorkService bleService;            //蓝牙service
	private MusicService musicService;            //音乐service
	private OutTimeHandler outTimeHandler;        //超时Handler
	private SendOrderOutTimeThread outTimeThread; //超时线程
	private String cursorAddress = "";
	private int  m2mPowerCurrent = 1;             //力度游标
	private int  m2sPowerCurrent = 1;             //力度游标
	private long remainingTime;                   //剩余时间
	private int startStepTote = 10;                //按摩动作初始数据条数
	
	private int M2M_POSITION;
	private int M2S_POSITION;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_distribution_for_m2);
        
        getBundle();       //获取传入数据
        init();            //初始化
        contrastWorking(); //对比正在工作的按摩状态设置UI
        closeGatt();
	}
	
	protected void onResume() {
		super.onResume();
		showLogD("onResume");
		restoreUI();
		registerReceiver(broadcastReceiver, bleGattIntentFilter()); //绑定广播
	}
	
	protected void onPause() {
		try{
			unregisterReceiver(broadcastReceiver); //解绑广播
		}catch(Exception e){}
		
		super.onPause();
		
	}
	
	protected void onDestroy() {
		super.onDestroy();
	}
	
	/**
	 * 回调函数
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		showLogD("onActivityResult");
		if(requestCode == TO_MUISC_ROOM && resultCode == Activity.RESULT_OK){
			if(app.getIsWorking() && roomId == app.getRoomId()){
        		musicService.refreshMusicList(); //刷新后台音乐Service的音乐列表
        	}
			
			musicService.refreshMusicList(roomId);
		}
	}
	
	/**
	 * 获取传入的数据
	 */
	private void getBundle(){
		Bundle bundle = this.getIntent().getExtras();
		roomName = bundle.getString("roomName");     //获取放松馆名字
		roomId = bundle.getInt("roomId");            //获取放松馆Id
		roomType = bundle.getInt("roomType");        //获取放松馆Type
		
		Log.d("z17m","roomName : " + roomName + "    roomId : " + roomId + "    roomType : " + roomType);
	}
	
	/**
	 * 对比正在工作的按摩状态设置UI
	 */
	private void contrastWorking(){
		if(!app.getIsWorking() || app.getRoomId() != roomId)
			return;
		
		int time = (int) (app.getActionTotalTime()/1000);
		
        switchImg.setImageResource(R.drawable.ic_stop); //替换开关按钮图片
        
        ActionBean m2mAction = app.getM2MAction();
        ActionBean m2sAction = app.getM2SAction();
        
		deviceList.get(M2M_POSITION).setAction(m2mAction);
		deviceList.get(M2S_POSITION).setAction(m2sAction);
		
		timeTxt.setText(time/60 + ":" + time%60);                     //时间控件
		
		m2mActionTxt.setText(m2mAction.getActionName());
		m2mStrengthTxt.setText(m2mAction.getStrength()+"");
		
		m2sActionTxt.setText(m2sAction.getActionName());
		m2sStrengthTxt.setText(m2sAction.getStrength()+"");
        
        switchParam = OPEN;
        presentParam = OPEN;
	}
	
	private void closeGatt(){
		if(!app.getIsWorking()){
			bleService.closeAll();
		}
	}
	
	/**
	 * 还原为起始UI
	 */
	private void restoreUI(){
		if(app.getIsWorking())
			return ;
		
		current = 0;
		switchImg.setImageResource(R.drawable.ic_start);
		//anim.stop();  //停止动画
		switchParam = CLOSE;
		presentParam = CLOSE;
		
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		db = DeviceDBUtils.openData(this);  //获取DB
		app = (ReleasyApplication) this.getApplication(); //获取Application
		spInfo = new SharePreferenceUtils(this); //获取SharePreference 存储
		bleService = app.getBleService();        //获取蓝牙Service
		musicService = app.getMusicService();    //获取音乐Service
		
		outTimeHandler = new OutTimeHandler(); //超时Handler
		outTimeThread = new SendOrderOutTimeThread(this, outTimeHandler); //超时Thread
		
		getDbData();   //获取数据库数据
		initProgressDialog(this.getString(R.string.are_connected_devices));
		initViews();   //初始化视图
		setTopNav();   //初始化导航栏
		initEvents();  //初始化点击事件

	}
	
	/**
	 * 初始化导航栏
	 */
	private void setTopNav(){
		navTxt.setText(roomName);   //设置导航栏名称
	}
	
	/**
	 * 初始化视图
	 */
	protected void initViews() {
		navLeftImg = (ImageView) findViewById(R.id.tobNavLeftImg);
		navRightImg = (ImageView) findViewById(R.id.tobNavRightImg);
		navTxt = (TextView) findViewById(R.id.titleTxt);
		
		timeTxt = (TextView) findViewById(R.id.timeTxt);
		switchImg = (ImageView) findViewById(R.id.switchImg);
		
		m2mActionTxt = (TextView) findViewById(R.id.m2_m_action_txt);
		m2mStrengthTxt = (TextView) findViewById(R.id.m2_m_strength_txt);            
		m2mSubtractBtn = (ImageView) findViewById(R.id.m2_m_subtract_btn);
		m2mAddBtn = (ImageView) findViewById(R.id.m2_m_add_btn);
		m2sActionTxt = (TextView) findViewById(R.id.m2_s_action_txt);
		m2sStrengthTxt = (TextView) findViewById(R.id.m2_s_strength_txt);
		m2sSubtractBtn = (ImageView) findViewById(R.id.m2_s_subtract_btn);
		m2sAddBtn = (ImageView) findViewById(R.id.m2_s_add_btn);
		
		
		//根据存储的静音状态   设置图标及静音
		if(spInfo.getIsMusicPlay()){
			app.openMusic();  //打开声音
		}
		else{
			app.muteMusic();  //静音
		}
		
		//anim = (AnimationDrawable) musicAnimImg.getBackground(); //获取动画
	}

	/**
	 * 初始化点击事件
	 */
	protected void initEvents() {
		//导航栏返回按钮逻辑
		navLeftImg.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				ActionDistributionForM2activity.this.finish();
			}});
		
		//导航栏返回按钮逻辑
		navRightImg.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				//TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		        //String simCountryIso = telManager.getSimCountryIso();// 返回SIM卡运营商的国家代码 READ_PHONE_STATE
		        //showLogD("simCountryIso : " + simCountryIso);
				/*if(spInfo.getMusicCursor()){
					Toast.makeText(ActionDistributionForM2activity.this, R.string.music_permission_msg, Toast.LENGTH_LONG).show();
				}*/
				if (Build.VERSION.SDK_INT >= 23){
					if(spInfo.getIOSMusic()){
						Toast.makeText(ActionDistributionForM2activity.this, R.string.music_permission_msg, Toast.LENGTH_LONG).show();
					}
				}
				
		        Intent intent = null;
		        intent = new Intent(ActionDistributionForM2activity.this,ISOMusicActivity.class);
	        	intent.putExtra("roomId", roomId);
				startActivity(intent);

			}});

		
		/**
		 * 开关按钮点击事件
		 */
		switchImg.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				if(deviceList.size() == 0){
					Toast.makeText(ActionDistributionForM2activity.this, R.string.currently_no_device, Toast.LENGTH_LONG).show();
					return;
				}
				
				if(deviceList.get(M2M_POSITION).getAction() == null){
					Toast.makeText(ActionDistributionForM2activity.this, R.string.pls_selsct_action
							, Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(deviceList.get(M2S_POSITION).getAction() == null){
					Toast.makeText(ActionDistributionForM2activity.this, R.string.pls_selsct_action
							, Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(checkDeviceVerify(deviceList)){
					return;
				}
				
				//当前按钮显示为开始
				if(switchParam == CLOSE){
					current = 0;
					presentParam = CLOSE;
					switchParam = CONFIGURE;
					progressDialog.show();
					setDuration();
					toWriteOrderConnectDevice();
				}
				//当前按钮显示为停止
				else{
					current = 0;
					remainingTime = 0;
					presentParam = OPEN;
					switchParam = CLOSE;
					progressDialog.show();
					toStopOrderConnectDevice();
				}
			}});
		
		m2mSubtractBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				Log.d("z17m","m2mSubtractBtn");
				if(deviceList.get(M2M_POSITION).getAction() == null){
					Toast.makeText(ActionDistributionForM2activity.this, R.string.pls_selsct_action
							, Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(m2mPowerCurrent > 1){
					m2mPowerCurrent = m2mPowerCurrent - 1;
					m2mStrengthTxt.setText(m2mPowerCurrent+"");
					deviceList.get(M2M_POSITION).getAction().setStrength(m2mPowerCurrent);
					toChangePower(M2M_POSITION);
				}
			}});
		
		m2mAddBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				if(deviceList.get(M2M_POSITION).getAction() == null){
					Toast.makeText(ActionDistributionForM2activity.this, R.string.pls_selsct_action
							, Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(m2mPowerCurrent < deviceList.get(M2M_POSITION).getAction().getPowerLV().length){
					m2mPowerCurrent = m2mPowerCurrent + 1;
					m2mStrengthTxt.setText(m2mPowerCurrent+"");
					deviceList.get(M2M_POSITION).getAction().setStrength(m2mPowerCurrent);
					toChangePower(M2M_POSITION);
				}
			}});
		
		m2sSubtractBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				if(deviceList.get(M2S_POSITION).getAction() == null){
					Toast.makeText(ActionDistributionForM2activity.this, R.string.pls_selsct_action
							, Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(m2sPowerCurrent > 1){
					m2sPowerCurrent = m2sPowerCurrent - 1;
					m2sStrengthTxt.setText(m2sPowerCurrent+"");
					deviceList.get(M2S_POSITION).getAction().setStrength(m2sPowerCurrent);
					toChangePower(M2S_POSITION);
				}
			}});
		
		m2sAddBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				if(deviceList.get(M2S_POSITION).getAction() == null){
					Toast.makeText(ActionDistributionForM2activity.this, R.string.pls_selsct_action
							, Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(m2sPowerCurrent < deviceList.get(M2S_POSITION).getAction().getPowerLV().length){
					m2sPowerCurrent = m2sPowerCurrent + 1;
					m2sStrengthTxt.setText(m2sPowerCurrent+"");
					deviceList.get(M2S_POSITION).getAction().setStrength(m2sPowerCurrent);
					toChangePower(M2S_POSITION);
				}
			}});
		
		m2mActionTxt.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				m2mBottomActionView = new BottomActionView(ActionDistributionForM2activity.this, m2mOnItemClickListener);
				m2mBottomActionView.show();
			}});
		
		m2sActionTxt.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				m2sBottomActionView = new BottomActionView(ActionDistributionForM2activity.this, m2sOnItemClickListener);
				m2sBottomActionView.show();
			}});
		
	}

	private OnItemClickListener m2mOnItemClickListener = new OnItemClickListener(){
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
			ActionBean bean = m2mBottomActionView.getCheckedRadio(position);
			m2mBottomActionView.dismiss();
			Log.d("z17m","M2M_POSITION : " + M2M_POSITION);
			
			deviceList.get(M2M_POSITION).setAction(bean);
			m2mActionTxt.setText(bean.getActionName());
			m2mStrengthTxt.setText(bean.getStrength() + "");
			m2mPowerCurrent = bean.getStrength();
			
			
			if(switchParam == OPEN){
				//
				isChangePower = true;
				current = 0;			
				presentParam = OPEN;
				switchParam = CONFIGURE;
				progressDialog.show();
				updataDuration();
				toWriteOrderConnectDevice();
			}
		}};
	
	private OnItemClickListener m2sOnItemClickListener = new OnItemClickListener(){
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
			ActionBean bean = m2sBottomActionView.getCheckedRadio(position);
			m2sBottomActionView.dismiss();
			Log.d("z17m","M2S_POSITION : " + M2S_POSITION);
				
			deviceList.get(M2S_POSITION).setAction(bean);
			m2sActionTxt.setText(bean.getActionName());
			m2sStrengthTxt.setText(bean.getStrength() + "");
			m2sPowerCurrent = bean.getStrength();
			
			if(switchParam == OPEN){
				//
				isChangePower = true;
				current = 0;			
				presentParam = OPEN;
				switchParam = CONFIGURE;
				progressDialog.show();
				updataDuration();
				toWriteOrderConnectDevice();
			}
		}};
	
	/**
	 * 对设备改变力度值
	 */
	private void toChangePower(int position){
		if(switchParam == OPEN){
			isChangePower = true;
			current = position;
			switchParam = UPDATA_POWER;
			progressDialog.show();
			updataDuration();
			toUpdataPowerConnectDevice();
		}
	}
	
	/**
	 * 获取数据库数据
	 */
	private void getDbData(){
		musicService.getMusicList(roomId);
		deviceList = DeviceDBUtils.searchData(db);
		
		for(int i = 0; i < deviceList.size(); i++){
			if(deviceList.get(i).getDeviceVersion().equals(Constants.DEVICE_VERSION_M2_A))
				M2M_POSITION = i;
			else if(deviceList.get(i).getDeviceVersion().equals(Constants.DEVICE_VERSION_M2_B))
				M2S_POSITION = i;
		}
		
	}
	
	/**
	 * 设置按摩持续时间
	 */
	private void setDuration(){
		for(int i = 0 ; i < deviceList.size(); i++){
			if(deviceList.get(i) == null)
				Log.d("z17m","deviceList.get(i) == null");
			
			if(deviceList.get(i).getAction() == null)
				Log.d("z17m","deviceList.get(i).getAction() == null");
				
			deviceList.get(i).getAction().setStopTime(duration*60);
		}
		
		remainingTime = duration*60;
	}
	
	/**
	 * 更新按摩持续时间
	 */
	private void updataDuration(){
		for(int i = 0 ; i < deviceList.size(); i++){
			deviceList.get(i).getAction().setStopTime((int)remainingTime/1000);
		}
		
	}
	
	/*************************************************************************************/
	
	/**
	 * 操作设备写入动作指令
	 */
	private void toWriteOrderConnectDevice(){
		if(current >= deviceList.size()){
			current = 0;
			
			if(switchParam == CONFIGURE)
				switchParam = OPEN;
			else{
				if(!isChangePower){
					app.CountdownTimerUtilsStart(this,duration*60*1000,1000,roomId,roomName,roomType,action);  //开启倒计时线程
					app.initDeviceLoad(deviceList);
					app.setM2MAction(deviceList.get(M2M_POSITION).getAction());
					app.setM2SAction(deviceList.get(M2S_POSITION).getAction());
					musicService.startPlayerList(roomId);  //开启音乐
					switchImg.setImageResource(R.drawable.ic_stop); //替换开关按钮图片
					//anim.start();  //开启动画
				}
				app.setAction(action);
				isChangePower = false;
				cursorAddress = "";
				progressDialog.dismiss(); //关闭Dialog
				presentParam = OPEN;
				return ;
			}
		}
		showLogD("current : " + current + "      address : " + deviceList.get(current).getAddress());
		
		cursorAddress = deviceList.get(current).getAddress();
		if(!bleService.connect(deviceList.get(current).getAddress())){
			showLogD("connect failure");
			current = current + 1;
			toWriteOrderConnectDevice();
		}
		else{
			isConfigure = true;
			outTimeHandler.postDelayed(outTimeThread, 10000);
		}
		
	}
	
	/**
	 * 操作设备更新力度
	 */
	private void toUpdataPowerConnectDevice(){
		startStep = 6;
		
		showLogD("current : " + current + "      address : " + deviceList.get(current).getAddress());
		
		cursorAddress = deviceList.get(current).getAddress();
		if(!bleService.connect(deviceList.get(current).getAddress())){
			showLogD("connect failure");
		}
		else{
			isConfigure = true;
			outTimeHandler.postDelayed(outTimeThread, 10000);
		}
		
	}
	
	/**
	 * 操作设备更新力度开始
	 */
	private void toUpdataOpenConnectDevice(){
		showLogD("current : " + current + "      address : " + deviceList.get(current).getAddress());
		
		cursorAddress = deviceList.get(current).getAddress();
		if(!bleService.connect(deviceList.get(current).getAddress())){
			showLogD("connect failure");
		}
		else{
			isConfigure = true;
			outTimeHandler.postDelayed(outTimeThread, 10000);
		}
		
	}
	
	
	/**
	 * 操作设备执行停止
	 */
	private void toStopOrderConnectDevice(){
		if(current >= deviceList.size()){
			current = 0;
			cursorAddress = "";
			timeTxt.setText(R.string.initial_time);
			//app.CountdownTimerUtilsStop();  //停止倒计时线程
			app.CountdownTimerUtilsStopForM2ActionDistribution();
			musicService.stopPlayer(); //停止音乐
			switchImg.setImageResource(R.drawable.ic_start); //替换开关按钮图片
			//anim.stop();  //停止动画
			progressDialog.dismiss();  //关闭Dialog
			presentParam = CLOSE;
			return ;
		}
		showLogD("current : " + current + "      address : " + deviceList.get(deviceList.size()-1-current).getAddress());
		
		cursorAddress = deviceList.get(deviceList.size()-1-current).getAddress();
		if(!bleService.connect(deviceList.get(deviceList.size()-1-current).getAddress())){
			showLogD("connect failure");
			current = current + 1;
			toStopOrderConnectDevice();
		}
		else{
			isConfigure = true;
			outTimeHandler.postDelayed(outTimeThread, 10000);
		}
	}
	
	/**
	 * 广播注册
	 */
	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			//蓝牙连接成功广播
			if (BleWorkService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				// Show all the supported services and characteristics on the
				// user interface.
				outTimeHandler.removeCallbacks(outTimeThread);
				failCount = 0;
				isConfigure = false;
				if(!cursorAddress.equals(intent.getExtras().getString("data")))
					return;
				
				displayGattServices(bleService.getSupportedGattServices());
			} 
			
			//蓝牙连接失败
			/*if(BleWorkService.ACTION_GATT_CONNECTED_133.equals(action)){
				String strat = getString(R.string.connect_failure_start);
				String end = getString(R.string.connect_failure_end);
				Toast.makeText(SingleActionActivity.this, strat + deviceList.get(current).getAddress()
						+ end, Toast.LENGTH_LONG).show();
				startStep = 0;
				sendStep = 0;
				current = current + 1;
				if(switchParam == CLOSE) toStopOrderConnectDevice();
				else if(switchParam == CONFIGURE) toWriteOrderConnectDevice();
				else if(switchParam == OPEN) toWriteOrderConnectDevice();
				else if(switchParam == UPDATA_POWER) toUpdataPowerConnectDevice();
			}*/
			
			if(BleWorkService.ACTION_GATT_DISCONNECTED.equals(action)){
				if(progressDialog.isShowing())
					progressDialog.dismiss();
				Bundle bundle = intent.getExtras();
				String deviceStr = ActionDistributionForM2activity.this.getString(R.string.device) + " ";
				String connectionIsbroken = " " + ActionDistributionForM2activity.this.getString(R.string.connection_is_broken);
				String address = bundle.getString("data");
				Toast.makeText(ActionDistributionForM2activity.this, deviceStr + address + connectionIsbroken, Toast.LENGTH_LONG).show();
				
				for(int i = 0; i < deviceList.size(); i++){
					if(deviceList.get(i).getAddress().equals(address))
						deviceList.get(i).setConnectStatus(false);
				}
				
				for(int i = 0; i < deviceList.size(); i++){
					if(deviceList.get(i).getConnectStatus()){
						return;
					}
				}
				
				switchParam = CLOSE;
				current = 0;
				cursorAddress = "";
				timeTxt.setText(duration + ":00");
				musicService.stopPlayer(); //停止音乐
				switchImg.setImageResource(R.drawable.ic_start); //替换开关按钮图片
				
				showAlertDialog(ActionDistributionForM2activity.this.getString(R.string.prompt)
						,ActionDistributionForM2activity.this.getString(R.string.no_device_attached_to_the_body)
						,ActionDistributionForM2activity.this.getString(R.string.confirm));
			}
			
			//特征值写入成功广播
			if(BleWorkService.ACTION_CHARACTERISTIC_WRITE_SUCCESS.equals(action)){
				outTimeHandler.removeCallbacks(outTimeThread);     
				failCount = 0;
				
				switch(switchParam){
				case CONFIGURE:
					if(startStep >= startStepTote && isSendOver){
						startStep = 0;
						sendStep = 0;
						current = current + 1;
						toWriteOrderConnectDevice();
					}
					else{
						if(isSendOver){
							startStep = startStep + 1;
							sendStep = 0;
							showLogD("send over : " + startStep );
						}
						else{
							sendStep = sendStep + 1;
							showLogD("send not over : " + sendStep );
						}
						configureCharacteristicWrite(deviceList.get(current),bleService,outTimeHandler, outTimeThread);						
					}
					break;
				case OPEN:
					app.addNewLog(roomType,deviceList.get(current).getAction(),1);
					deviceList.get(current).setConnectStatus(true);
					current = current + 1;
					toWriteOrderConnectDevice();
					break;
				case CLOSE:
					app.addNewLog(roomType,deviceList.get(current).getAction(),3);
					deviceList.get(current).setConnectStatus(true);
					current = current + 1;
					toStopOrderConnectDevice(); 
					break;
				case UPDATA_POWER:
					if(startStep >= 7 && isSendOver){
						app.addNewLog(roomType,deviceList.get(current).getAction(),2);
						startStep = 0;
						sendStep = 0;
						switchParam = UPDATA_OPEN;
						toUpdataOpenConnectDevice();
					}
					else{
						if(isSendOver){
							startStep = startStep + 1;
							sendStep = 0;
							showLogD("send over : " + startStep );
						}
						else{
							sendStep = sendStep + 1;
							showLogD("send not over : " + sendStep );
						}
						configureCharacteristicWrite(deviceList.get(current),bleService,outTimeHandler, outTimeThread);						
					}
					break;
				case UPDATA_OPEN:
					deviceList.get(current).setConnectStatus(true);
					isChangePower = false;
					current = 0;
					cursorAddress = "";
					progressDialog.dismiss();
					app.setM2MAction(deviceList.get(M2M_POSITION).getAction());
					app.setM2SAction(deviceList.get(M2S_POSITION).getAction());
					switchParam = OPEN;
					break;
				}
			}
			
			//倒计时更新广播
			if(CountdownTimerUtils.COUNTDOWN_TIMER_TICK.equals(action)){
				if(app.getIsWorking() && app.getRoomId() != roomId)
					return;
				
				Bundle bundle = intent.getExtras();
				long millisUntilFinished = bundle.getLong("millisUntilFinished");
				remainingTime = millisUntilFinished;
				//circularSeekbar.setProgress((int)(millisUntilFinished/1000));
				//circularSeekbar.invalidate();
				
				int minInt = (int) ((millisUntilFinished/1000)/60);
				int secondInt = (int) ((millisUntilFinished/1000)%60);
				String min = "" + minInt;
				String second = "" + secondInt;
				if(minInt < 10) min = "0" + minInt;
				if(secondInt < 10) second = "0" + secondInt;
				
				timeTxt.setText(min + ":" + second);
			}
			
			//倒计时结束广播
			if(CountdownTimerUtils.COUNTDOWN_TIMER_FINISH.equals(action)){
				showLogD("CountdownTimerUtils.COUNTDOWN_TIMER_FINISH");
				musicService.stopPlayer();
				//showShareDialog(duration);
				//setCircularSeekbar(15,15,true);     //设置时间控件
				//circularSeekbar.invalidate();       //刷新时间控件
				timeTxt.setText(R.string.initial_time);
				current = 0;
				remainingTime = 0;
				switchImg.setImageResource(R.drawable.ic_start);
				//anim.stop();  //停止动画
				switchParam = CLOSE;
				
				for(int i = 0; i < deviceList.size(); i++){
					app.addNewLog(roomType,deviceList.get(i).getAction(),4);
				}
			}
			
			//音乐名称更新
			if(MusicService.UPDATA_MUSIC_UI.equals(action)){
				if(!app.getIsWorking() || app.getRoomId() != roomId)
					return;
				
				Bundle bundle = intent.getExtras();
				String musicName = bundle.getString("musicName");
				showLogD("musicName : " + musicName);
				//musicNameTxt.setText(musicName);
			}
			
			//分享
			if (Constants.SHARE.equals(action)) {
				openShare();
			} 
			
			if(BleWorkService.DEVICE_IS_NOT_LOAD.equals(action)){
				String toastStr1 = ActionDistributionForM2activity.this.getString(R.string.device);
				String toastStr2 = ActionDistributionForM2activity.this.getString(R.string.not_affixed_to_the_body);
				Toast.makeText(ActionDistributionForM2activity.this, toastStr1 
						+ " " + intent.getExtras().getString("deviceAddress") 
						+ " " + toastStr2, Toast.LENGTH_SHORT).show();
			}
			
			if(BleWorkService.DEVICE_ALL_IS_NOT_LOAD.equals(action)){
				switchParam = CLOSE;
				current = 0;
				cursorAddress = "";
				timeTxt.setText(duration + ":00");
				musicService.stopPlayer(); //停止音乐
				switchImg.setImageResource(R.drawable.ic_start); //替换开关按钮图片
				
				showAlertDialog(ActionDistributionForM2activity.this.getString(R.string.prompt)
						,ActionDistributionForM2activity.this.getString(R.string.no_device_attached_to_the_body)
						,ActionDistributionForM2activity.this.getString(R.string.confirm));
			}
	}};
	
	/**
	 * 广播Action
	 */
	private static IntentFilter bleGattIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BleWorkService.ACTION_GATT_CONNECTED);    //蓝牙连接
		intentFilter.addAction(BleWorkService.ACTION_GATT_DISCONNECTED); //蓝牙断开
		intentFilter.addAction(BleWorkService.ACTION_GATT_SERVICES_DISCOVERED);  //蓝牙连接成功
		intentFilter.addAction(BleWorkService.ACTION_CHARACTERISTIC_WRITE_SUCCESS);  //蓝牙UUID数据写入成功
		//intentFilter.addAction(BleWorkService.ACTION_GATT_CONNECTED_133);  //蓝牙连接133错误
		intentFilter.addAction(CountdownTimerUtils.COUNTDOWN_TIMER_FINISH); //倒计时结束
		intentFilter.addAction(CountdownTimerUtils.COUNTDOWN_TIMER_TICK);   //倒计时更新
		intentFilter.addAction(MusicService.UPDATA_MUSIC_UI);  //音乐更新
		intentFilter.addAction(Constants.SHARE);  //分享
		intentFilter.addAction(BleWorkService.DEVICE_IS_NOT_LOAD);  //负载
		intentFilter.addAction(BleWorkService.DEVICE_ALL_IS_NOT_LOAD);  //负载
		return intentFilter;
	}
	
	// Demonstrates how to iterate through the supported GATT
	// Services/Characteristics.
	// In this sample, we populate the data structure that is bound to the
	// ExpandableListView
	// on the UI.
	private void displayGattServices(List<BluetoothGattService> gattServices) {
		showLogD("displayGattServices");
		
		if (gattServices == null){
			bleService.close();
			if(switchParam == OPEN || switchParam == CONFIGURE)
				toWriteOrderConnectDevice();
			else
				toStopOrderConnectDevice();
			return;
		}
		
		characteristicList = new ArrayList<BluetoothGattCharacteristic>();

		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {
				
			List<BluetoothGattCharacteristic> gattCharacteristics = gattService
					.getCharacteristics();
				
			// Loops through available Characteristics.
			for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				characteristicList.add(gattCharacteristic);
			}	
		}
		
		//特征值列表获取失败
		if(characteristicList.size() == 0){
			bleService.close();
			if(switchParam == OPEN || switchParam == CONFIGURE)
				toWriteOrderConnectDevice();
			else
				toStopOrderConnectDevice();
			return;
		}
		
		//指令处理
		if(switchParam == CONFIGURE)
			configureCharacteristicWrite(deviceList.get(current),bleService,outTimeHandler, outTimeThread); //写入按摩特征值
		else if(switchParam == OPEN)
			startCharacteristicWrite(deviceList.get(current),bleService,outTimeHandler, outTimeThread);     //写入开启特征值
		else if(switchParam == CLOSE)
			stopCharacteristicWrite(deviceList.get(current),bleService,outTimeHandler, outTimeThread);      //写入停止特征值
		else if(switchParam == UPDATA_POWER)
			configureCharacteristicWrite(deviceList.get(current),bleService,outTimeHandler, outTimeThread); //写入按摩特征值
		else if(switchParam == UPDATA_OPEN)
			startCharacteristicWrite(deviceList.get(current),bleService,outTimeHandler, outTimeThread);     //写入开启特征值
	}
	
	/**
	 * 超时Handler
	 */
	private class OutTimeHandler extends Handler{
		public void handleMessage(Message msg) {
			if(ActionDistributionForM2activity.this.isFinishing())
				return;
			if(progressDialog.isShowing())
				progressDialog.dismiss(); //关闭Dialog
			
			String device;
			String connectFailure;
			device = ActionDistributionForM2activity.this.getString(R.string.device);
			connectFailure = ActionDistributionForM2activity.this.getString(R.string.connect_failure);
			
			Toast.makeText(ActionDistributionForM2activity.this, device + connectFailure
					, Toast.LENGTH_LONG).show();
			
			startStep = 0;
			sendStep = 0;
			current = 0;
			
			switchParam = presentParam;
			Log.d("z17m","switchParam : " + switchParam + "    presentParam : " + presentParam);
			
			/*
			if(isConfigure){
				isConfigure = false;
				String strat = getString(R.string.connect_failure_start);
				String end = getString(R.string.connect_failure_end);
				Toast.makeText(ActionDistributionForM2activity.this, strat + deviceList.get(current).getAddress()
						+ end, Toast.LENGTH_LONG).show();
				startStep = 0;
				sendStep = 0;
				current = current + 1;
				if(switchParam == CLOSE) toStopOrderConnectDevice();
				else if(switchParam == CONFIGURE) toWriteOrderConnectDevice();
				else if(switchParam == OPEN) toWriteOrderConnectDevice();
				else if(switchParam == UPDATA_POWER) toUpdataPowerConnectDevice();
				return;
			}
			
			showLogD("This is outTimeHandler........");
			failCount = failCount + 1;
			String device;
			
			switch(switchParam){
			case CONFIGURE:
				if(failCount >= 1){
					failCount = 0;
					startStep = 0;
					sendStep = 0;
					current = current + 1;
					toWriteOrderConnectDevice();
					return;
				}
				
				if(startStep >= 9) toWriteOrderConnectDevice();
				else configureCharacteristicWrite(deviceList.get(current),bleService,outTimeHandler, outTimeThread);
				break;
				
			case OPEN:
				device = ActionDistributionForM2activity.this.getString(R.string.device);
				connectFailure = ActionDistributionForM2activity.this.getString(R.string.connect_failure);
				
				Toast.makeText(ActionDistributionForM2activity.this, device + " " 
						+ deviceList.get(current).getAddress() + " " + connectFailure
						, Toast.LENGTH_LONG).show();
				current = current + 1;
				toWriteOrderConnectDevice(); 
				break;
				
			case CLOSE:
				if(failCount >= 1){
					device = ActionDistributionForM2activity.this.getString(R.string.device);
					connectFailure = ActionDistributionForM2activity.this.getString(R.string.connect_failure);
					
					Toast.makeText(ActionDistributionForM2activity.this, device + " " 
							+ deviceList.get(current).getAddress() + " " + connectFailure
							, Toast.LENGTH_LONG).show();
					failCount = 0;
					current = current + 1;
					toStopOrderConnectDevice(); 
					return;
				}
				toStopOrderConnectDevice(); 
				break;
			default:
				break;
			}*/
		}
	}
}
