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
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.releasy.android.R;
import com.releasy.android.ReleasyApplication;
import com.releasy.android.activity.main.RelesyBaseActivity;
import com.releasy.android.activity.music.ISOMusicActivity;
import com.releasy.android.adapter.RecyclingPagerAdapter;
import com.releasy.android.bean.ActionBean;
import com.releasy.android.bean.DeviceBean;
import com.releasy.android.constants.ActionConstants;
import com.releasy.android.constants.Constants;
import com.releasy.android.constants.RoomConstants;
import com.releasy.android.db.ActionDBUtils;
import com.releasy.android.db.ActionForM2DBUtils;
import com.releasy.android.db.DeviceDBUtils;
import com.releasy.android.db.ReleasyDatabaseHelper;
import com.releasy.android.service.BleWorkService;
import com.releasy.android.service.MusicService;
import com.releasy.android.utils.CountdownTimerUtils;
import com.releasy.android.utils.ScalePageTransformer;
import com.releasy.android.utils.SendOrderOutTimeThread;
import com.releasy.android.utils.SharePreferenceUtils;
import com.releasy.android.view.PickerView;
import com.releasy.android.view.PickerView.onSelectListener;


/**
 * 多个动作的放松馆页面
 * @author Lighting.Z
 *
 */
public class MultipleActionActivity extends RelesyBaseActivity{

	private final int TO_MUISC_ROOM = 101;        //标识
	private ImageView actionBg;                   //背景大图
	private ImageView navLeftImg;                 //导航菜单栏返回键
	private ImageView navRightImg;                //导航菜单栏音乐键
	private TextView navTxt;                      //导航菜单栏名称
	
	private ViewPager mViewPager;
    private TubatuAdapter mPagerAdapter;
	private TextView timeTxt;                     //时间控件
	private ImageView switchImg;                  //开关按钮
	private PickerView powerPv;                   //力度调整控件
	private ImageView subtractPowerBtn;           //力度减按钮
	private ImageView addPowerBtn;                //力度加按钮
	private RadioGroup rgs;                       //底端动作Tab
	
	private String roomName;                      //放松馆名字
	private int roomId;                           //放松馆Id
	private int roomType;                         //放松馆Type
	private String reason = "";                   //表情识别专用
	private ActionBean action;                    //按摩动作信息
	private List<DeviceBean> deviceList;          //设备列表
	private List<ActionBean> actionList;          //动作列表
	private ReleasyDatabaseHelper db;             //数据库
	private int duration = 30;                    //按摩持续时间  (min)
	private int maxPowerLV = 15;
	
	private ReleasyApplication app;               //Application
	private SharePreferenceUtils spInfo;          //SharePreference
	private BleWorkService bleService;            //蓝牙service
	private MusicService musicService;            //音乐service
	private OutTimeHandler outTimeHandler;        //超时Handler
	private SendOrderOutTimeThread outTimeThread; //超时线程
	private String cursorAddress = "";
	private int  powerCurrent = 1;                //力度游标
	private long remainingTime;                   //剩余时间
	private int startStepTote = 7;                //按摩动作初始数据条数
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_action);
        
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
		//musicService.getMusicName(roomId);  //获取音乐名称
		//musicNameTxt.setText(musicService.getMusicName(roomId));
	}
	
	protected void onPause() {
		try{
			unregisterReceiver(broadcastReceiver); //解绑广播
		}catch(Exception e){}
		
		super.onPause();
	}
	
	protected void onDestroy() {
		/*try{
			unregisterReceiver(broadcastReceiver); //解绑广播
		}catch(Exception e){}*/
		
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
		
		if(roomId == RoomConstants.ACTION_COUNTENANCE_TYPE){
			reason = bundle.getString("reason");     //获取放松馆名字
			if(reason == null)
				reason = "";
		}
	}
	
	/**
	 * 对比正在工作的按摩状态设置UI
	 */
	private void contrastWorking(){
		if(!app.getIsWorking() || app.getRoomId() != roomId)
			return;
		
		int time = (int) (app.getActionTotalTime()/1000);
		//setCircularSeekbar(15*60,time,false);  //设置时间控件时间
        //circularSeekbar.invalidate(); //更新时间控件UI

        switchImg.setImageResource(R.drawable.ic_stop); //替换开关按钮图片
        //anim.start();  //开启动画
        
        powerPv.setCannotSlide();
		addPowerBtn.setVisibility(View.VISIBLE);
		subtractPowerBtn.setVisibility(View.VISIBLE);
        
        action = app.getAction();
        maxPowerLV = action.getPowerLV().length;
        
        for(int i = 0; i < actionList.size(); i++){
        	if(actionList.get(i).getActionId() == action.getActionId()){
        		actionList.get(i).setStrength(action.getStrength());
        	}
        }
        
        for(int i = 0; i < rgs.getChildCount(); i++){
			if(rgs.getChildAt(i).getId() == action.getActionId()){
				rgs.check(actionList.get(i).getActionId());
			}
		}
        
        for(int i = 0; i < deviceList.size(); i++){
        	deviceList.get(i).setAction(action);
        }

        //powerBar.setPower(action.getStrength());
        powerCurrent = action.getStrength();
        int power = powerCurrent;
        List<String> data = new ArrayList<String>();
        for(int i = 0; i < maxPowerLV; i++){
        	if(power <= maxPowerLV )
        		data.add("" + power);
        	else
        		data.add("" + (power-maxPowerLV));
        	power = power + 1;
        }
        powerPv.setData(data);
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
		
		//setCircularSeekbar(15,15,true);     //设置时间控件
		//circularSeekbar.invalidate();       //刷新时间控件
		timeTxt.setText(R.string.initial_time);
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
		app = (ReleasyApplication) this.getApplication();  //获取Application
		spInfo = new SharePreferenceUtils(this);  //获取SharePreference存储
		bleService = app.getBleService();         //获取蓝牙Service
		musicService = app.getMusicService();     //获取音乐Service
		
		outTimeHandler = new OutTimeHandler(); //超时Handler
		outTimeThread = new SendOrderOutTimeThread(this, outTimeHandler); //超时Thread
		
		getDbData();   //获取数据库数据
		initProgressDialog(this.getString(R.string.are_connected_devices));
		initViews();   //初始化视图
		setTopNav();   //初始化导航栏
		//setCircularSeekbar(15,15,true);   //设置时间控件
		initViewPager();
		initPowerPv();
		initEvents();  //初始化点击事件
		
		if(roomId == RoomConstants.ACTION_COUNTENANCE_TYPE){
			//TODO
			initExpressionView();
		}
	}
	
	/**
	 * 初始化导航栏
	 */
	private void setTopNav(){
		navTxt.setText(roomName);   //设置导航栏名称
		actionBg.setImageResource(RoomConstants.getRoomTopNavPic(roomId));
	}
	
	/**
	 * 初始化视图
	 */
	protected void initViews() {
		navLeftImg = (ImageView) findViewById(R.id.tobNavLeftImg);
		navRightImg = (ImageView) findViewById(R.id.tobNavRightImg);
		navTxt = (TextView) findViewById(R.id.titleTxt);
		actionBg = (ImageView) findViewById(R.id.action_bg_img);
		
		timeTxt = (TextView) findViewById(R.id.timeTxt);
		switchImg = (ImageView) findViewById(R.id.switchImg);
		powerPv = (PickerView) findViewById(R.id.power_pv);
		subtractPowerBtn = (ImageView) findViewById(R.id.subtract_btn);
		addPowerBtn = (ImageView) findViewById(R.id.add_btn);
		
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
        
		rgs = (RadioGroup) findViewById(R.id.tabs_rg);
		
		//根据存储的声音状态  设置图标 以及声音
		if(spInfo.getIsMusicPlay()){
			app.openMusic();  //打开声音
			//musicSwitchBtn.setImageResource(R.drawable.ic_volume_float);
		}
		else{
			app.muteMusic();  //静音
			//musicSwitchBtn.setImageResource(R.drawable.ic_volume_float_mute);
		}
		
		//anim = (AnimationDrawable) musicAnimImg.getBackground();
		
		initRadioGroup(); //加载动作底部菜单
		
		timeTxt.setText(duration + ":00");
	}
	
	/**
	 * 初始化力度控件数据
	 */
	private void initPowerPv(){
		List<String> data = new ArrayList<String>();
		for (int i = 1; i <= maxPowerLV; i++){
			data.add("" + i);//添加da
		}
		
		powerPv.setMaxValue(maxPowerLV);
		powerPv.setData(data);
	}
	
	private void initViewPager(){
		mViewPager.setPageTransformer(true, new ScalePageTransformer());
		mPagerAdapter = new TubatuAdapter(this);
        mViewPager.setAdapter(mPagerAdapter);
        
        mViewPager.setPageMargin(this.getResources().getDimensionPixelSize(R.dimen.page_margin));
        
        mViewPager.setOnPageChangeListener(new ActionOnPageChangeListener());
        
        initViewPagerData();
	}
	
	private void initViewPagerData() {
        List<Integer> list = new ArrayList<Integer>();
        for(int i = 0; i < actionList.size(); i++){
        	list.add(ActionConstants.getActionBg(actionList.get(i).getActionId()));
        }

        //设置OffscreenPageLimit
        mViewPager.setOffscreenPageLimit(list.size());
        mPagerAdapter.addAll(list);
    }
	
	/**
	 * 加载动作底部菜单
	 */
	private void initRadioGroup(){
		for(int i = 0; i < actionList.size(); i++){
			RadioButton rdbtn = (RadioButton) LayoutInflater.from(this).inflate(R.layout.layout_action_radio, null); 
			rdbtn.setId(actionList.get(i).getActionId());
			/*rdbtn.setCompoundDrawables(null, ActionConstants.getActionDrawable(this,actionList.get(i).getActionId())
					, null, null); //设置左图标*/
			rdbtn.setText(actionList.get(i).getActionName());
			rgs.addView(rdbtn);
			if(i == 0) rgs.check(actionList.get(i).getActionId());
		}
	}

	/**
	 * 初始化点击事件
	 */
	protected void initEvents() {
		//导航栏返回按钮逻辑
		navLeftImg.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				MultipleActionActivity.this.finish();
			}});
		
		//导航栏返回按钮逻辑
		navRightImg.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				//TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		        //String simCountryIso = telManager.getSimCountryIso();// 返回SIM卡运营商的国家代码 READ_PHONE_STATE
		        //showLogD("simCountryIso : " + simCountryIso);
				/*if(spInfo.getMusicCursor()){
					Toast.makeText(MultipleActionActivity.this, R.string.music_permission_msg, Toast.LENGTH_LONG).show();
				}*/
				if (Build.VERSION.SDK_INT >= 23){
					if(spInfo.getIOSMusic()){
						Toast.makeText(MultipleActionActivity.this, R.string.music_permission_msg, Toast.LENGTH_LONG).show();
					}
				}
				
		        Intent intent = null;
		        intent = new Intent(MultipleActionActivity.this,ISOMusicActivity.class);
	        	intent.putExtra("roomId", roomId);
				startActivity(intent);
		        
		        /*if(simCountryIso.equals("cn")){
		        	intent = new Intent(SingleActionActivity.this,MusicRoomActivity.class);
		        	intent.putExtra("roomId", roomId);
					startActivityForResult(intent, TO_MUISC_ROOM);
		        }
		        else{
		        	intent = new Intent(SingleActionActivity.this,ISOMusicActivity.class);
		        	intent.putExtra("roomId", roomId);
					startActivity(intent);
		        }*/
			}});
		
		/**
		 * 开关按钮点击事件
		 */
		switchImg.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				if(deviceList.size() == 0){
					Toast.makeText(MultipleActionActivity.this, R.string.currently_no_device, Toast.LENGTH_LONG).show();
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
		
		/**
		 * 力度控件增加力度的点击事件
		 */
		powerPv.setOnSelectListener(new onSelectListener(){
			public void onSelect(String text){
				int strength = Integer.parseInt(text);
				if(powerCurrent == strength)
					return;
				
				powerCurrent = strength;
				for(int i = 0; i < deviceList.size(); i++){
					deviceList.get(i).getAction().setStrength(strength);
					action = deviceList.get(i).getAction();
				}
				toChangePower();  //对设备改变力度值
			}
		});
		
		/**
		 * 力度加按钮点击事件
		 */
		addPowerBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				int currentValue = Integer.parseInt(powerPv.getCurrentValue());
				if(currentValue == maxPowerLV)
					return;
				
				powerPv.addValue();
				
				powerCurrent = Integer.parseInt(powerPv.getCurrentValue());
				for(int i = 0; i < deviceList.size(); i++){
					deviceList.get(i).getAction().setStrength(powerCurrent);
					action = deviceList.get(i).getAction();
				}
				toChangePower();  //对设备改变力度值
			}});
		
		/**
		 * 力度减按钮点击事件
		 */
		subtractPowerBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				int currentValue = Integer.parseInt(powerPv.getCurrentValue());
				if(currentValue == 1)
					return;
				
				powerPv.subtractValue();
				
				powerCurrent = Integer.parseInt(powerPv.getCurrentValue());
				for(int i = 0; i < deviceList.size(); i++){
					deviceList.get(i).getAction().setStrength(powerCurrent);
					action = deviceList.get(i).getAction();
				}
				toChangePower();  //对设备改变力度值
			}});
		
		/**
		 * 底端按摩动作菜单选项改变监听
		 */
		rgs.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
				Log.d("z17m","rgs.setOnCheckedChangeListener");
				
				for(int i = 0; i < rgs.getChildCount(); i++){
					if(rgs.getChildAt(i).getId() == checkedId){
						action = actionList.get(i);
						//powerBar.setPower(action.getStrength());
						maxPowerLV = action.getPowerLV().length;
						setPower(action.getStrength());
						
						mViewPager.setCurrentItem(i);
						for(int k = 0; k < deviceList.size(); k++){
							deviceList.get(k).setAction(action);
						}
						
						duration = action.getMaxWorkTime()/60;
						timeTxt.setText(duration + ":00");
					}
				}
				
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
			}});
	}
	
	private void initExpressionView(){
		if(reason.equals("K")){
			action = actionList.get(0);
		}
		else if(reason.equals("W")){
			action = actionList.get(1);
		}
		else if(reason.equals("Y")){
			action = actionList.get(2);
		}
		else if(reason.equals("M")){
			action = actionList.get(3);
		}
		else if(reason.equals("D")){
			action = actionList.get(4);
		}
		else if(reason.equals("C")){
			action = actionList.get(5);
		}
		else{
			action = actionList.get(0);
		}
        
        for(int i = 0; i < rgs.getChildCount(); i++){
			if(rgs.getChildAt(i).getId() == action.getActionId()){
				rgs.check(actionList.get(i).getActionId());
			}
		}
        
        for(int i = 0; i < deviceList.size(); i++){
        	deviceList.get(i).setAction(action);
        }
	}
	
	private void setPower(int actionPower){
        int power = actionPower;
        List<String> data = new ArrayList<String>();
        for(int i = 0; i < maxPowerLV; i++){
        	if(power <= maxPowerLV )
        		data.add("" + power);
        	else
        		data.add("" + (power-maxPowerLV));
        	power = power + 1;
        }
        powerPv.setData(data);
	}
	
	/**
	 * 对设备改变力度值
	 */
	private void toChangePower(){
		if(switchParam == OPEN){
			isChangePower = true;
			current = 0;
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
		
		if(spInfo.getSelectDevice().equals(Constants.SELECT_DEVICE_M2)){
			actionList = ActionForM2DBUtils.searchRoomActionData(db, roomId, this);
			startStepTote = 10;
		}
		else{
			actionList = ActionDBUtils.searchRoomActionData(db, roomId, this);
			startStepTote = 7;
		}
		
		if(actionList.size() == 0) return;
		action = actionList.get(0);
		if(action == null) return;
		for(int i = 0; i < deviceList.size(); i++){
			deviceList.get(i).setAction(action);
		}
		duration = action.getMaxWorkTime()/60;
		maxPowerLV = action.getPowerLV().length;
	}
	
	/**
	 * 设置按摩持续时间
	 */
	private void setDuration(){
		for(int i = 0 ; i < deviceList.size(); i++){
			deviceList.get(i).getAction().setStopTime(duration*60);
		}
		
		remainingTime = duration*60;
	}
	
	/**
	 * 更新按摩持续时间
	 */
	private void updataDuration(){
		for(int i = 0 ; i < deviceList.size(); i++){
			//Log.d("z17m","remainingTime : " + remainingTime + "    action name : " + deviceList.get(i).getAction().getActionName());
			deviceList.get(i).getAction().setStopTime((int)remainingTime/1000);
		}
		
	}
	
	public static class TubatuAdapter extends RecyclingPagerAdapter {

        private final List<Integer> mList;
        private final Context mContext;

        public TubatuAdapter(Context context) {
            mList = new ArrayList<Integer>();
            mContext = context;
        }

        public void addAll(List<Integer> list) {
            mList.addAll(list);
            notifyDataSetChanged();
        }

        public View getView(int position, View convertView, ViewGroup container) {
            ImageView imageView = null;
            if (convertView == null) {
                imageView = new ImageView(mContext);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setTag(position);
            imageView.setImageResource(mList.get(position));
            return imageView;
        }

        public int getCount() {
            return mList.size();
        }
    }
	
	public class ActionOnPageChangeListener implements OnPageChangeListener{  
  	  
        public void onPageScrollStateChanged(int arg0) { }  
  
        public void onPageScrolled(int arg0, float arg1, int arg2) {}  
  
        public void onPageSelected(int arg0) {  
            ((RadioButton)rgs.getChildAt(arg0)).setChecked(true);
        	/*for(int i = 0; i < rgs.getChildCount(); i++){
            	if(i == arg0){
            		action = actionList.get(i);
            		((RadioButton)rgs.getChildAt(i)).setChecked(true);
            		setPower(action.getStrength());
            		for(int k = 0; k < deviceList.size(); k++){
						deviceList.get(k).setAction(action);
					}
            	}
            	else
            		((RadioButton)rgs.getChildAt(i)).setChecked(false);
            }
        	
			if(switchParam == OPEN){
				//
				
				isChangePower = true;
				current = 0;
				switchParam = CONFIGURE;
				progressDialog.show();
				updataDuration();
				toWriteOrderConnectDevice();
				
			}*/
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
					//setCircularSeekbar(15*60,duration*60,false);  //设置时间控件时间
					//circularSeekbar.invalidate(); //更新时间控件UI
					app.CountdownTimerUtilsStart(this,duration*60*1000,1000,roomId,roomName,roomType,action);  //开启倒计时线程
					app.initDeviceLoad(deviceList);
					musicService.startPlayerList(roomId);  //开启音乐
					switchImg.setImageResource(R.drawable.ic_stop); //替换开关按钮图片
					//anim.start();  //开启动画
					//TODO
					powerPv.setCannotSlide();
					addPowerBtn.setVisibility(View.VISIBLE);
					subtractPowerBtn.setVisibility(View.VISIBLE);
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
		if(current >= deviceList.size()){
			current = 0;
			startStep = 0;
			cursorAddress = "";
			switchParam = OPEN;
		}
		showLogD("current : " + current + "      address : " + deviceList.get(current).getAddress());
		
		cursorAddress = deviceList.get(current).getAddress();
		if(!bleService.connect(deviceList.get(current).getAddress())){
			showLogD("connect failure");
			current = current + 1;
			toUpdataPowerConnectDevice();
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
			//setCircularSeekbar(15,15,true);  //设置时间控件时间
	        //circularSeekbar.invalidate();  //更新时间控件UI
			timeTxt.setText(R.string.initial_time);
			app.CountdownTimerUtilsStop();  //停止倒计时线程
			musicService.stopPlayer(); //停止音乐
			switchImg.setImageResource(R.drawable.ic_start); //替换开关按钮图片
			//anim.stop();  //停止动画
			progressDialog.dismiss();  //关闭Dialog
			//TODO
			powerPv.setCanSlide();
			addPowerBtn.setVisibility(View.GONE);
			subtractPowerBtn.setVisibility(View.GONE);
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
				Toast.makeText(MultipleActionActivity.this, strat + deviceList.get(current).getAddress()
						+ end, 3000).show();
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
				String deviceStr = MultipleActionActivity.this.getString(R.string.device) + " ";
				String connectionIsbroken = " " + MultipleActionActivity.this.getString(R.string.connection_is_broken);
				String address = bundle.getString("data");
				Toast.makeText(MultipleActionActivity.this, deviceStr + address + connectionIsbroken, Toast.LENGTH_LONG).show();
				
				//11111
				/*showAlertDialog(MultipleActionActivity.this.getString(R.string.prompt)
						,deviceStr + address + connectionIsbroken
						,MultipleActionActivity.this.getString(R.string.confirm));*/
				
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
				powerPv.setCanSlide();
				addPowerBtn.setVisibility(View.GONE);
				subtractPowerBtn.setVisibility(View.GONE);
				
				showAlertDialog(MultipleActionActivity.this.getString(R.string.prompt)
						,MultipleActionActivity.this.getString(R.string.no_device_connect)
						,MultipleActionActivity.this.getString(R.string.confirm));
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
						}
						else{
							sendStep = sendStep + 1;
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
						current = current + 1;
						toUpdataPowerConnectDevice();
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
				//showActionFeedbackDialog();
				//setCircularSeekbar(15,15,true);     //设置时间控件
				//circularSeekbar.invalidate();       //刷新时间控件
				timeTxt.setText(R.string.initial_time);
				current = 0;
				remainingTime = 0;
				switchImg.setImageResource(R.drawable.ic_start);
				//anim.stop();  //停止动画
				switchParam = CLOSE;
				//TODO
				powerPv.setCanSlide();
				addPowerBtn.setVisibility(View.GONE);
				subtractPowerBtn.setVisibility(View.GONE);
				
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
				String toastStr1 = MultipleActionActivity.this.getString(R.string.device);
				String toastStr2 = MultipleActionActivity.this.getString(R.string.not_affixed_to_the_body);
				String toastStr = toastStr1 + " " + intent.getExtras().getString("deviceAddress") + " " + toastStr2;
				
				Toast.makeText(MultipleActionActivity.this, toastStr, Toast.LENGTH_SHORT).show();
				
				//11111
				/*showAlertDialog(MultipleActionActivity.this.getString(R.string.prompt)
						,toastStr
						,MultipleActionActivity.this.getString(R.string.confirm));*/
				
			}
			
			if(BleWorkService.DEVICE_ALL_IS_NOT_LOAD.equals(action)){
				switchParam = CLOSE;
				current = 0;
				cursorAddress = "";
				timeTxt.setText(duration + ":00");
				musicService.stopPlayer(); //停止音乐
				switchImg.setImageResource(R.drawable.ic_start); //替换开关按钮图片
				powerPv.setCanSlide();
				addPowerBtn.setVisibility(View.GONE);
				subtractPowerBtn.setVisibility(View.GONE);
				
				showAlertDialog(MultipleActionActivity.this.getString(R.string.prompt)
						,MultipleActionActivity.this.getString(R.string.no_device_attached_to_the_body)
						,MultipleActionActivity.this.getString(R.string.confirm));
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
	}
	
	/**
	 * 超时Handler
	 */
	/**
	 * 超时Handler
	 */
	private class OutTimeHandler extends Handler{
		public void handleMessage(Message msg) {
			if(MultipleActionActivity.this.isFinishing())
				return;
			
			if(spInfo.getSelectDevice().equals(Constants.SELECT_DEVICE_M2)){
				if(progressDialog.isShowing())
					progressDialog.dismiss(); //关闭Dialog
				
				String device;
				String connectFailure;
				device = MultipleActionActivity.this.getString(R.string.device);
				connectFailure = MultipleActionActivity.this.getString(R.string.connect_failure);
				
				Toast.makeText(MultipleActionActivity.this, device + connectFailure
						, Toast.LENGTH_LONG).show();
				
				startStep = 0;
				sendStep = 0;
				current = 0;
				
				switchParam = presentParam;
				Log.d("z17m","switchParam : " + switchParam + "    presentParam : " + presentParam);
			}
			else{
				if(isConfigure){
					isConfigure = false;
					String strat = getString(R.string.connect_failure_start);
					String end = getString(R.string.connect_failure_end);
					Toast.makeText(MultipleActionActivity.this, strat + deviceList.get(current).getAddress()
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
				String connectFailure;
				
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
					
					if(startStep >= startStepTote) toWriteOrderConnectDevice();
					else configureCharacteristicWrite(deviceList.get(current),bleService,outTimeHandler, outTimeThread);
					break;
					
				case OPEN:
					device = MultipleActionActivity.this.getString(R.string.device);
					connectFailure = MultipleActionActivity.this.getString(R.string.connect_failure);
					
					Toast.makeText(MultipleActionActivity.this, device + " " 
							+ deviceList.get(current).getAddress() + " " + connectFailure
							, Toast.LENGTH_LONG).show();
					current = current + 1;
					toWriteOrderConnectDevice(); 
					break;
					
				case CLOSE:
					if(failCount >= 1){
						device = MultipleActionActivity.this.getString(R.string.device);
						connectFailure = MultipleActionActivity.this.getString(R.string.connect_failure);
						
						Toast.makeText(MultipleActionActivity.this, device + " " 
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
				}
			}
		}
	}
		
	

}
