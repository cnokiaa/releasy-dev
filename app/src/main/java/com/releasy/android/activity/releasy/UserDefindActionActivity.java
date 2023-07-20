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
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.releasy.android.R;
import com.releasy.android.ReleasyApplication;
import com.releasy.android.activity.main.RelesyBaseActivity;
import com.releasy.android.bean.ActionBean;
import com.releasy.android.bean.DeviceBean;
import com.releasy.android.constants.Constants;
import com.releasy.android.db.ActionDBUtils;
import com.releasy.android.db.DeviceDBUtils;
import com.releasy.android.db.ReleasyDatabaseHelper;
import com.releasy.android.service.BleWorkService;
import com.releasy.android.service.MusicService;
import com.releasy.android.utils.CountdownTimerUtils;
import com.releasy.android.utils.SendOrderOutTimeThread;
import com.releasy.android.utils.SharePreferenceUtils;
import com.releasy.android.view.CircularSeekBar;
import com.releasy.android.view.DeviceItemLayout;
import com.releasy.android.view.TopNavLayout;
import com.releasy.android.view.CircularSeekBar.OnSeekChangeListener;

public class UserDefindActionActivity extends RelesyBaseActivity{
	
	private TopNavLayout mTopNavLayout;           //导航菜单栏
	private CircularSeekBar circularSeekbar;      //时间控件
	private ImageView switchImg;                  //开关按钮
	private ImageView musicAnimImg;               //音乐动画图标
	private AnimationDrawable anim;               //音乐图标动画
	private TextView musicNameTxt;                //音乐名称Txt
	private ImageView musicSwitchBtn;             //音乐开关按钮
	private LinearLayout deviceLayout;            //设备加载Layout
	
	private String roomName;                      //放松馆名字
	private int roomId;                           //放松馆Id
	private int roomType;                         //放松馆Type
	private ActionBean action;                    //按摩动作信息
	private List<DeviceBean> deviceList;          //设备列表
	private List<DeviceItemLayout> deviceItemList;//设备信息显示Layout List
	private ReleasyDatabaseHelper db;             //数据库
	private int duration = 15;                    //按摩持续时间  (min)
	
	private ReleasyApplication app;               //Application
	private SharePreferenceUtils spInfo;          //SharePreference
	private BleWorkService bleService;            //蓝牙service
	private MusicService musicService;            //音乐service
	private final int USER_DEFIND_EDIT = 100;     //跳转编辑页面标识
	private OutTimeHandler outTimeHandler;        //超时Handler
	private SendOrderOutTimeThread outTimeThread; //超时线程
	private String cursorAddress = "";
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_defind_action);
        
        getBundle();       //获取传入数据
        init();            //初始化
        contrastWorking(); //对比正在工作的按摩状态设置UI
	}
	
	protected void onResume() {
		super.onResume();
		showLogD("onResume");
		restoreUI();
		registerReceiver(broadcastReceiver, bleGattIntentFilter()); //绑定广播
		//musicService.getMusicName(roomId);
		musicNameTxt.setText(musicService.getMusicName(roomId));
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
	 * 回调
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
		showLogD("ReleasyMainFragment onActivityResult");
		if(requestCode == USER_DEFIND_EDIT && resultCode == Activity.RESULT_OK){
			roomName = data.getExtras().getString("roomName");
			mTopNavLayout.setTitltTxt(roomName);
			getDbData();
			setDeviceLyaoutInfo();
			setResult(Activity.RESULT_OK); 
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
	}
	
	/**
	 * 对比正在工作的按摩状态设置UI
	 */
	private void contrastWorking(){
		if(!app.getIsWorking() || app.getRoomId() != roomId)
			return;
		
		switchParam = OPEN;
		int time = (int) (app.getActionTotalTime()/1000);
		setCircularSeekbar(15*60,time,false);  //设置时间控件时间
        circularSeekbar.invalidate(); //更新时间控件UI

        switchImg.setImageResource(R.drawable.ic_stop); //替换开关按钮图片
        anim.start();  //开启动画
	}
	
	/**
	 * 还原为起始UI
	 */
	private void restoreUI(){
		if(app.getIsWorking())
			return ;
		
		setCircularSeekbar(15,15,true);     //设置时间控件
		circularSeekbar.invalidate();       //刷新时间控件
		current = 0;
		switchImg.setImageResource(R.drawable.ic_start);
		anim.stop();  //停止动画
		switchParam = CLOSE;
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		db = DeviceDBUtils.openData(this);  //获取DB
		deviceItemList = new ArrayList<DeviceItemLayout>();
		app = (ReleasyApplication) this.getApplication();
		spInfo = new SharePreferenceUtils(this); 
		bleService = app.getBleService();
		musicService = app.getMusicService();
		
		outTimeHandler = new OutTimeHandler(); //超时Handler
		outTimeThread = new SendOrderOutTimeThread(this, outTimeHandler); //超时Thread
		
		getDbData();   //获取数据库数据
		initProgressDialog(this.getString(R.string.are_connected_devices));
		initViews();   //初始化视图
		setTopNav();   //初始化导航栏
		setDeviceLyaoutInfo(); //设置设备列表信息
		setCircularSeekbar(15,15,true);   //设置时间控件
		initEvents();  //初始化点击事件
	}
	
	/**
	 * 初始化导航栏
	 */
	private void setTopNav(){
		mTopNavLayout.setTitltTxt(roomName);
		mTopNavLayout.setLeftImgSrc(R.drawable.ic_nav_bar_back);
		mTopNavLayout.setRightImgSrc(R.drawable.ic_nav_bar_edit);
	}
	
	/**
	 * 初始化视图
	 */
	protected void initViews() {
		mTopNavLayout = (TopNavLayout) findViewById(R.id.topNavLayout);
		circularSeekbar = (CircularSeekBar) findViewById(R.id.circularSeekBar);
		switchImg = (ImageView) findViewById(R.id.switchImg);
		musicAnimImg = (ImageView) findViewById(R.id.musicAnimImg);
		musicNameTxt = (TextView) findViewById(R.id.musicNameTxt);
		musicSwitchBtn = (ImageView) findViewById(R.id.musicSwitchBtn);
		deviceLayout = (LinearLayout) findViewById(R.id.deviceLayout);
		
		//根据存储的静音状态   设置图标及静音
		if(spInfo.getIsMusicPlay()){
			app.openMusic();  //打开声音
			musicSwitchBtn.setImageResource(R.drawable.ic_volume_float);
		}
		else{
			app.muteMusic();  //静音
			musicSwitchBtn.setImageResource(R.drawable.ic_volume_float_mute);
		}
		
		anim = (AnimationDrawable) musicAnimImg.getBackground();
	}
	
	/**
	 * 设置设备列表信息
	 */
	private void setDeviceLyaoutInfo(){
		deviceLayout.removeAllViews();
		deviceItemList.clear();
		for(int i = 0; i < deviceList.size(); i++){
			DeviceBean bean = deviceList.get(i);
			bean.setPower(1);
			DeviceItemLayout layout = new DeviceItemLayout(this, bean, i);
			deviceLayout.addView(layout);
			deviceItemList.add(layout);
		}
	}
	
	/**
	 * 设置时间控件
	 */
	private void setCircularSeekbar(int maxProgress, int progress, boolean openTouch){
		circularSeekbar.setOpenTouch(openTouch);
		if(openTouch)
			circularSeekbar.showSeekBar();
		else
			circularSeekbar.hideSeekBar();
		
		circularSeekbar.setMaxProgress(maxProgress);  //设置最大值
        circularSeekbar.setProgress(progress);     //设置当前值
	}

	/**
	 * 初始化点击事件
	 */
	protected void initEvents() {
		//导航栏返回按钮逻辑
		mTopNavLayout.setLeftImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				UserDefindActionActivity.this.finish();
			}});
		
		//导航栏编辑按钮逻辑
		mTopNavLayout.setRightImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				if(app.getIsWorking() && app.getRoomId() == roomId){
					Toast.makeText(UserDefindActionActivity.this, R.string.pls_stop_action_re_edit, Toast.LENGTH_LONG).show();
					return;
				}
				
				Intent intent = new Intent(UserDefindActionActivity.this,UserDefindEditActivity.class);
				intent.putExtra("type", UserDefindEditActivity.TO_EDIT);
				intent.putExtra("roomId", roomId);
				startActivityForResult(intent, USER_DEFIND_EDIT);
			}});
		
		/**
		 * 时间控件 时间改变监听时间
		 */
		circularSeekbar.setSeekBarChangeListener(new OnSeekChangeListener() {
            public void onProgressChange(CircularSeekBar view, int newProgress) {
            	if(circularSeekbar.getOpenTouch()){
            		circularSeekbar.changeDrawable(newProgress);  //改变小圈图标
            		duration = newProgress;  //设置时间
            		showLogD("duration : " + duration);
            	}
            	else{
            		circularSeekbar.changeDoingDrawable(newProgress);  //改变小圈图标
            	}
            }
        });
		
		/**
		 * 开关按钮点击事件
		 */
		switchImg.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				if(deviceList.size() == 0){
					Toast.makeText(UserDefindActionActivity.this, R.string.currently_no_device, Toast.LENGTH_LONG).show();
					return;
				}
				
				if(checkDeviceVerify(deviceList)){
					return;
				}
				
				//当前按钮显示为开始
				if(switchParam == CLOSE){
					current = 0;
					switchParam = CONFIGURE;
					progressDialog.show();
					setDuration();
					toOrderConnectDevice();
				}
				//当前按钮显示为停止
				else{
					current = 0;
					switchParam = CLOSE;
					progressDialog.show();
					toROrderConnectDevice();
				}
			}});
		
		/**
		 * 音乐开关按钮
		 */
		musicSwitchBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				boolean isPlay = spInfo.getIsMusicPlay();
				if(isPlay){
					spInfo.setIsMusicPlay(!isPlay);  //设置音乐状态
					musicSwitchBtn.setImageResource(R.drawable.ic_volume_float_mute);
					app.muteMusic();  //静音
					//musicService.stopPlayer();
					//anim.stop();
				}
				else{
					spInfo.setIsMusicPlay(!isPlay);  //设置音乐状态
					musicSwitchBtn.setImageResource(R.drawable.ic_volume_float);
					app.openMusic();  //打开声音
					/*if(switchParam == OPEN){
						musicService.startPlayer();
						anim.start();
					}*/
				}
			}});
	}

	/**
	 * 获取数据库数据
	 */
	private void getDbData(){
		musicService.getMusicList(roomId);
		deviceList = DeviceDBUtils.searchData(db);
		
		List<ActionBean> actionList = ActionDBUtils.searchRoomActionData(db, roomId, this);
		if(actionList.size() == 0) return;
		for(int i = 0; i < deviceList.size(); i++){
			if(i >= actionList.size()) return;
			deviceList.get(i).setAction(actionList.get(i));
		}
	}
	
	/**
	 * 设置按摩持续时间
	 */
	private void setDuration(){
		for(int i = 0 ; i < deviceList.size(); i++){
			if(deviceList.get(i).getAction() != null)
				deviceList.get(i).getAction().setStopTime(duration*60);
		}
	}
	
    /*************************************************************************************/
	
	/**
	 * 正序操作设备
	 */
	private void toOrderConnectDevice(){
		if(current >= deviceList.size()){
			current = 0;
			
			if(switchParam == CONFIGURE)
				switchParam = OPEN;
			else{
				setCircularSeekbar(15*60,duration*60,false);  //设置时间控件时间
		        circularSeekbar.invalidate(); //更新时间控件UI
		        app.CountdownTimerUtilsStart(this,duration*60*1000,1000,roomId,roomName,roomType,action);  //开启倒计时线程
		        musicService.startPlayerList(roomId);  //开启音乐
		        switchImg.setImageResource(R.drawable.ic_stop); //替换开关按钮图片
		        if(spInfo.getIsMusicPlay()) anim.start();  //开启动画
		        cursorAddress = "";
				progressDialog.dismiss(); //关闭Dialog
				return ;
			}
		}
		showLogD("current : " + current + "      address : " + deviceList.get(current).getAddress());
		
		if(deviceList.get(current).getAction() == null){
			current = current + 1;
			toOrderConnectDevice();
			return;
		}
		
		cursorAddress = deviceList.get(current).getAddress();
		if(!bleService.connect(deviceList.get(current).getAddress())){
			showLogD("connect failure");
			current = current + 1;
			toOrderConnectDevice();
		}
		else{
			isConfigure = true;
			outTimeHandler.postDelayed(outTimeThread, 10000);
		}
		
	}
	
	/**
	 * 倒序操作设备
	 */
	private void toROrderConnectDevice(){
		if(current >= deviceList.size()){
			current = 0;
			cursorAddress = "";
			setCircularSeekbar(15,15,true);  //设置时间控件时间
	        circularSeekbar.invalidate();  //更新时间控件UI
			app.CountdownTimerUtilsStop();  //停止倒计时线程
			musicService.stopPlayer(); //停止音乐
			switchImg.setImageResource(R.drawable.ic_start); //替换开关按钮图片
			anim.stop();  //停止动画
			progressDialog.dismiss();  //关闭Dialog
			return ;
		}
		showLogD("current : " + current + "      address : " + deviceList.get(deviceList.size()-1-current).getAddress());
		
		if(deviceList.get(deviceList.size()-1-current).getAction() == null){
			current = current + 1;
			toROrderConnectDevice();
			return;
		}
		
		cursorAddress = deviceList.get(deviceList.size()-1-current).getAddress();
		if(!bleService.connect(deviceList.get(deviceList.size()-1-current).getAddress())){
			showLogD("connect failure");
			current = current + 1;
			toROrderConnectDevice();
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
				Toast.makeText(UserDefindActionActivity.this, strat + deviceList.get(current).getAddress()
						+ end, Toast.LENGTH_LONG).show();
				startStep = 0;
				sendStep = 0;
				current = current + 1;
				if(switchParam == CLOSE) toROrderConnectDevice();
				else if(switchParam == CONFIGURE) toOrderConnectDevice();
				else if(switchParam == OPEN) toOrderConnectDevice();
			}*/
			
			if(BleWorkService.ACTION_GATT_DISCONNECTED.equals(action)){
				if(progressDialog.isShowing())
					progressDialog.dismiss();
				Bundle bundle = intent.getExtras();
				String deviceStr = UserDefindActionActivity.this.getString(R.string.device) + " ";
				String connectionIsbroken = " " + UserDefindActionActivity.this.getString(R.string.connection_is_broken);
				Toast.makeText(UserDefindActionActivity.this, deviceStr + bundle.getString("data") + connectionIsbroken, Toast.LENGTH_LONG).show();
			}
			
			//特征值写入成功广播
			if(BleWorkService.ACTION_CHARACTERISTIC_WRITE_SUCCESS.equals(action)){
				outTimeHandler.removeCallbacks(outTimeThread);     
				failCount = 0;
				
				switch(switchParam){
				case CONFIGURE:
					if(startStep >= 7 && isSendOver){
						startStep = 0;
						sendStep = 0;
						current = current + 1;
						toOrderConnectDevice();
					}
					else{
						if(isSendOver){
							startStep = startStep + 1;
							sendStep = 0;
						}
						else{
							sendStep = sendStep + 1;
						}
						configureCharacteristicWrite(deviceList.get(current),bleService,outTimeHandler, outTimeThread);						
					}
					break;
				case OPEN:
					app.addNewLog(roomType,deviceList.get(current).getAction(),1);
					current = current + 1;
					toOrderConnectDevice();
					break;
				case CLOSE:
					app.addNewLog(roomType,deviceList.get(current).getAction(),3);
					current = current + 1;
					toROrderConnectDevice(); 
					break;
				}
			}
			
			//倒计时更新广播
			if(CountdownTimerUtils.COUNTDOWN_TIMER_TICK.equals(action)){
				if(app.getIsWorking() && app.getRoomId() != roomId)
					return;
				
				Bundle bundle = intent.getExtras();
				long millisUntilFinished = bundle.getLong("millisUntilFinished");
				circularSeekbar.setProgress((int)(millisUntilFinished/1000));
				circularSeekbar.invalidate();
			}
			
			//倒计时结束广播
			if(CountdownTimerUtils.COUNTDOWN_TIMER_FINISH.equals(action)){
				showLogD("CountdownTimerUtils.COUNTDOWN_TIMER_FINISH");
				musicService.stopPlayer();
				//showShareDialog(duration);
				setCircularSeekbar(15,15,true);     //设置时间控件
				circularSeekbar.invalidate();       //刷新时间控件
				current = 0;
				switchImg.setImageResource(R.drawable.ic_start);
				anim.stop();  //停止动画
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
				musicNameTxt.setText(musicName);
			}
			
			//分享
			if (Constants.SHARE.equals(action)) {
				openShare();
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
				toOrderConnectDevice();
			else
				toROrderConnectDevice();
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
				toOrderConnectDevice();
			else
				toROrderConnectDevice();
			return;
		}
		
		//指令处理
		if(switchParam == CONFIGURE)
			configureCharacteristicWrite(deviceList.get(current),bleService,outTimeHandler, outTimeThread); //写入按摩特征值
		else if(switchParam == OPEN)
			startCharacteristicWrite(deviceList.get(current),bleService,outTimeHandler, outTimeThread);     //写入开启特征值
		else if(switchParam == CLOSE){
			stopCharacteristicWrite(deviceList.get(current),bleService,outTimeHandler, outTimeThread);      //写入停止特征值
		}
	}
	
	/**
	 * 超时Handler
	 */
	private class OutTimeHandler extends Handler{
		public void handleMessage(Message msg) {
			if(UserDefindActionActivity.this.isFinishing())
				return;
			
			if(isConfigure){
				isConfigure = false;
				String strat = getString(R.string.connect_failure_start);
				String end = getString(R.string.connect_failure_end);
				Toast.makeText(UserDefindActionActivity.this, strat + deviceList.get(current).getAddress()
						+ end, Toast.LENGTH_LONG).show();
				startStep = 0;
				sendStep = 0;
				current = current + 1;
				if(switchParam == CLOSE) toROrderConnectDevice();
				else if(switchParam == CONFIGURE) toOrderConnectDevice();
				else if(switchParam == OPEN) toOrderConnectDevice();
				return;
			}
			
			showLogD("This is outTimeHandler........");
			failCount = failCount + 1;
			String device;
			String connectFailure;
			
			switch(switchParam){
			case CONFIGURE:
				if(failCount >= 1){
					failCount = 0;
					startStep = 0;
					sendStep = 0;
					current = current + 1;
					toOrderConnectDevice();
					return;
				}
				
				if(startStep >= 7) toOrderConnectDevice();
				else configureCharacteristicWrite(deviceList.get(current),bleService,outTimeHandler, outTimeThread);
				break;
				
			case OPEN:
				device = UserDefindActionActivity.this.getString(R.string.device);
				connectFailure = UserDefindActionActivity.this.getString(R.string.connect_failure);
				
				Toast.makeText(UserDefindActionActivity.this, device + " " 
						+ deviceList.get(current).getAddress() + " " + connectFailure
						, 3000).show();
				current = current + 1;
				toOrderConnectDevice(); 
				
				break;
				
			case CLOSE:
				if(failCount >= 1){
					device = UserDefindActionActivity.this.getString(R.string.device);
					connectFailure = UserDefindActionActivity.this.getString(R.string.connect_failure);
					
					Toast.makeText(UserDefindActionActivity.this, device + " " 
							+ deviceList.get(current).getAddress() + " " + connectFailure
							, Toast.LENGTH_LONG).show();
					failCount = 0;
					current = current + 1;
					toROrderConnectDevice(); 
					return;
				}
				toROrderConnectDevice(); 
				break;
			default:
				break;
			}
		}
	}

}
