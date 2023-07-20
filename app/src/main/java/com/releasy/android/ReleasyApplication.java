package com.releasy.android;

import java.io.File;
import java.util.List;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.releasy.android.activity.releasy.ActionDistributionForM2activity;
import com.releasy.android.activity.releasy.MultipleActionActivity;
import com.releasy.android.activity.releasy.SingleActionActivity;
import com.releasy.android.activity.releasy.UserDefindActionActivity;
import com.releasy.android.bean.ActionBean;
import com.releasy.android.bean.DeviceBean;
import com.releasy.android.bean.UserRecordBean;
import com.releasy.android.constants.Constants;
import com.releasy.android.service.BleWorkService;
import com.releasy.android.service.MusicService;
import com.releasy.android.utils.CountdownTimerUtils;
import com.releasy.android.utils.Utils;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.IBinder;
import android.widget.RemoteViews;

/**
 * 程序的Application
 * 做一些必要的初始化  以及记录全局信息 
 * @author Lighting.Z
 *
 */
public class ReleasyApplication extends Application{

	private UserRecordBean userRecord;
	
	private static ReleasyApplication instance;  
    public static ReleasyApplication getInstance() {  
        return instance;  
    }  
	
	public void onCreate() {
		super.onCreate();
		//createFolder();
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by  
		//  ImageLoaderConfiguration.createDefault(this);  
		// method.  
		ImageLoaderConfiguration configuration =
            new ImageLoaderConfiguration.Builder(getApplicationContext()).threadPriority(Thread.NORM_PRIORITY - 1)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs()
                .threadPoolSize(10)
                .build();
        ImageLoader.getInstance().init(configuration);
        
        //Log.d("z17m","ReleasyApplication onCreate");
        
        Intent gattServiceIntent = new Intent(this, BleWorkService.class);
		bindService(gattServiceIntent, mBelServiceConnection,BIND_AUTO_CREATE);
        
		Intent musicServiceIntent = new Intent(this, MusicService.class);
		bindService(musicServiceIntent, mMusicServiceConnection,BIND_AUTO_CREATE);
		
		userRecord = new UserRecordBean(Utils.getTime3());
	}
	
	// Code to manage Service lifecycle.
	//service bind 的参数
	private final ServiceConnection mBelServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBluetoothLeService = ((BleWorkService.LocalBinder) service)
					.getService();
			if (!mBluetoothLeService.initialize()) {
				
			}
	        setBleService(mBluetoothLeService);
	        //Log.d("z17m"," CheirapsisActivity onServiceConnected connect ");
		}

		public void onServiceDisconnected(ComponentName componentName) {
			//Log.d("z17m","BelServiceConnection onServiceDisconnected ");
			mBluetoothLeService = null;
		}
	};
	
	// Code to manage Service lifecycle.
	//service bind 的参数
	private final ServiceConnection mMusicServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mMusicService = ((MusicService.LocalBinder) service)
					.getService();
			mMusicService.init();
			
	        setMusicService(mMusicService);
	        //Log.d("z17m"," MusicService onServiceConnected connect ");
		}

		public void onServiceDisconnected(ComponentName componentName) {
			//Log.d("z17m","MusicServiceConnection onServiceDisconnected ");
			mMusicService = null;
		}
	};
	
	
	/**
	 * 创建文件
	 */
	private void createFolder(){
		if(!Utils.hasSdcard()){
			return;
		}
		File dir = new File(Constants.ROOT_FILE);  
        dir.mkdir();
        File dir2 = new File(Constants.MUSIC);  
        dir2.mkdir();
        File dir3 = new File(Constants.UPDATA);  
        dir3.mkdir();
	}
	
	
	/**
	 * 检测APP版本更新
	 */
	private String appUpdataMsg;
	private String appDownloadUrl;
	private String appNewVersion;
	//设置APP版本信息
	public void setAppUpdataInfo(String appUpdataMsg, String appDownloadUrl, String appNewVersion){
		this.appUpdataMsg = appUpdataMsg;
		this.appDownloadUrl = appDownloadUrl;
		this.appNewVersion = appNewVersion;
	}
	//获取更新信息
	public String getAppUpdataMsg(){
		return appUpdataMsg;
	}
	//获取更新地址
	public String getAppDownloadUrl(){
		return appDownloadUrl;
	}
	//获取更新版本号
	public String getAppNewVersion(){
		return appNewVersion;
	}
	
	
	//打开静音设置
	public void muteMusic(){
		mMusicService.muteMusic();
	}
	//关闭静音设置
	public void openMusic(){
		mMusicService.openMusic();
	}
	
	/**
	 * 设备版本信息，用于适配和统计。
	 * 返回格式为 系统名称_系统版本_分辨率。
	 * 如：android_4.0.4_480*800
	 */
	public String device_v = null;  //版本信息
	//获取版本信息
	public String getDeviceV(){
		return device_v;
	}
	//设置版本信息
	public void setDeviceV(String device_v){
		this.device_v = device_v;
	}
	
	
	/**
	 * 获取手机唯一编号
	 * 返回值为String 记录手机唯一编号
	 * 如：012682006095510
	 */
	public String device_id = null;  //设备ID号
	//获取设备ID
	public String getDeviceId(){
		return device_id;
	}
	//设置设备ID
	public void setDeviceId(String device_id){
		this.device_id = device_id;
	}
	
	
	/**
	 * 解除绑定
	 */
	public void unbindService(){
		//Log.d("z17m","app unbindService");
		cleanWorkingInfo();
		
		mBluetoothLeService.closeAll();
		unbindService(mBelServiceConnection);
		
		mMusicService.stopPlayer();
		unbindService(mMusicServiceConnection);
	}
	
	
	/**
	 * 全局蓝牙Sevicier
	 */
	public BleWorkService mBluetoothLeService;       //蓝牙Service
	//记录按摩Service
	public void setBleService(BleWorkService mBluetoothLeService){
		this.mBluetoothLeService = mBluetoothLeService;
	}
	//获取按摩Service
	public BleWorkService getBleService(){
		return mBluetoothLeService;
	}
	
	
	/**
	 * 全局音乐service
	 */
	public MusicService mMusicService;                //音乐Service
	//记录音乐Service
	public void setMusicService(MusicService mMusicService){
		this.mMusicService = mMusicService;
	}
	//获取音乐Service
	public MusicService getMusicService(){
		return mMusicService;
	}
	
	
	/**
	 * 倒计时工具
	 */
	public CountdownTimerUtils countdownTimerUtils;      //倒计时工具
	private long totalTime;
	//倒计时开始
	public void CountdownTimerUtilsStart(Context context, long millisInFuture, long countDownInterval
			,int roomId, String roomName, int roomType, ActionBean action){
		
		if(countdownTimerUtils != null){
			if(isWorking){
				if(action != null)
					userRecord.updata(action.getActionId(), (int)(countdownTimerUtils.getRuntime()/1000));
			}
				
			countdownTimerUtils.cancel();
		}
		
		setWorkingInfo(roomId, roomName, roomType, action);
		
		countdownTimerUtils = new CountdownTimerUtils(context, millisInFuture, countDownInterval);
		countdownTimerUtils.start();
		totalTime = millisInFuture;
	}
	//倒计时关闭
	public void CountdownTimerUtilsStop(){
		if(isWorking){
			userRecord.updata(action.getActionId(), (int)(countdownTimerUtils.getRuntime()/1000));
		}
		
		cleanWorkingInfo();
		
		if(countdownTimerUtils != null){
			countdownTimerUtils.cancel();
			countdownTimerUtils = null;
		}
	}
	public void CountdownTimerUtilsStopForM2ActionDistribution(){
		if(isWorking){
			//userRecord.updata(action.getActionId(), (int)(countdownTimerUtils.getRuntime()/1000));
		}
		
		cleanWorkingInfo();
		
		if(countdownTimerUtils != null){
			countdownTimerUtils.cancel();
			countdownTimerUtils = null;
		}
	}
	
	
	/**
	 * 设备是否在运行状态
	 * true为在运行状态 
	 * false为不在运行状态
	 */
	private boolean isWorking = false;
	private int roomId;
	private String roomName;
	private int roomType;
	private ActionBean action;
	private int lastRoomId = -100;
	private String lastRoomName;
	private int lastRoomType;
	private String logout = "";
	
	private ActionBean m2mAction;
	private ActionBean m2sAction;
	//记录运行信息
	public void setWorkingInfo(int roomId, String roomName, int roomType, ActionBean action){
		isWorking = true;
		this.roomId = roomId;
		this.roomName = roomName;
		this.roomType = roomType;
		this.action = action;
		
		lastRoomId = roomId;
		lastRoomName = roomName;
		lastRoomType = roomType;
		
		//Log.d("z17m","roomId : " + roomId + "    roomName : " + roomName + "    roomType : " +roomType);
		//if(action != null)
			//Log.d("z17m", "actionId : " + action.getActionId() + "    actionName : " + action.getActionName());
		
	}
	//获取运行信息
	public void cleanWorkingInfo(){
		isWorking = false;
		roomId = 0;
		roomName = null;
		roomType = 0;
		action = null;
		m2mAction = null;
		m2sAction = null;
		
		if(mMusicService != null)
			mMusicService.stopPlayer();
		
		//Log.d("z17m", "cleanWorkingInfo"); 
	}
	
	//返回使用记录
	public UserRecordBean getUserRecord(){
		return userRecord;
	}
	
	private List<DeviceBean> deviceLoadList ;
	//开始按摩 初始负载判断记录
	public void initDeviceLoad(List<DeviceBean> loadList){
		this.deviceLoadList = loadList;
		for(int i = 0; i < deviceLoadList.size(); i++){
			deviceLoadList.get(i).setIsLoad(true);
		}
	}
	//设置离体没有负载记录
	public void setDeviceIsNotLoad(String deviceAddress){
		for(int i = 0; i < deviceLoadList.size(); i++){
			if(deviceLoadList.get(i).getAddress().equals(deviceAddress)){
				deviceLoadList.get(i).setIsLoad(false);
			}
		}
		
		Intent intent = new Intent(BleWorkService.DEVICE_IS_NOT_LOAD);
		intent.putExtra("deviceAddress", deviceAddress);
		sendBroadcast(intent);
		
		checkDeviceLoad();
	}
	//检查全部负载状况
	public void checkDeviceLoad(){
		if(deviceLoadList == null)
			return;
		
		for(int i = 0; i < deviceLoadList.size(); i++){
			if(deviceLoadList.get(i).getIsLoad())
				return;
		}
		
		Intent intent = new Intent(BleWorkService.DEVICE_ALL_IS_NOT_LOAD);
		sendBroadcast(intent);
		CountdownTimerUtilsStop();
		
	}
	
	
	//获取运行状态
	public boolean getIsWorking(){
		return isWorking;
	}
	//获取roomId
	public int getRoomId(){
		return roomId;
	}
	//获取roomName
	public String getRoomName(){
		return roomName;
	}
	//获取roomType
	public int getRoomType(){
		return roomType;
	}
	//获取ActionBean
	public void setAction(ActionBean action){
		this.action = action;
	}
	//获取ActionBean
	public ActionBean getAction(){
		return action;
	}
	//获取按摩总时间
	public long getActionTotalTime(){
		return totalTime;
	}
	//获取上一个按摩的roomId
	public int getLastRoomId(){
		return lastRoomId;
	}
	public void setLastRoomId(int lastRoomId){
		this.lastRoomId = lastRoomId;
	}
	//获取上一个按摩的roomName
	public String getLastRoomName(){
		return lastRoomName;
	}
	//获取上一个按摩的roomType
	public int getLastRoomType(){
		return lastRoomType;
	}
	//设置2代主机动作
	public void setM2MAction(ActionBean m2mAction){
		this.m2mAction = m2mAction;
	}
	//获取2代主机动作
	public ActionBean getM2MAction(){
		return m2mAction;
	}
	
	//设置2代副机动作
	public void setM2SAction(ActionBean m2sAction){
		this.m2sAction = m2sAction;
	}
	//获取2代副机动作
	public ActionBean getM2SAction(){
		return m2sAction;
	}
	
	//添加新的操作记录
	public void addNewLog(int roomType, ActionBean action, int operate){
		if(action == null)
			return;
		
		logout = logout + operate + "," + roomType + "," + action.getActionId() + "," 
		         + action.getStrength() + "," + Utils.getTime2() + ";";
		//Log.d("z17m",logout);
	}
	//获取操作记录
	public String getLogOut(){
		return logout;
	}
	
	/**
	 * 切入后台 设置Notify
	 */
//	private NotificationManager notifyManage;
//	private Notification notify;
//	private RemoteViews contentView;
	private boolean hasBackstageNotify = false;
	//获取是否有后台Botify
	public boolean getHasBackstageNotify(){
		return hasBackstageNotify;
	}
	//初始化后台Notify
	public void initBackstageNotificition(){
//		notifyManage = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//		notify = new Notification();
//		notify.flags = Notification.FLAG_AUTO_CANCEL;
//
//		contentView = new RemoteViews(getPackageName(), R.layout.layout_backstage_notification);
//		contentView.setTextViewText(R.id.actionTxt, this.getString(R.string.doing) + roomName);
//
//		// 加载类，如果直接通过类名，会在点击时重新加载页面，无法恢复最后页面状态。
//		/*Intent contentIntent;
//		contentIntent = new Intent(getApplicationContext(),SingleActionActivity.class);
//		contentIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//		notify.contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, contentIntent,0); */
//
//
//		notify.flags|=Notification.FLAG_AUTO_CANCEL;
//        Intent intent = null ;
//        switch(roomType){
//		case Constants.SINGLE_TYPE:
//			intent = new Intent(this,SingleActionActivity.class);
//			//Log.d("z17m","roomName : " + roomName + "    roomId : " + roomId + "    roomType : " + roomType);
//			intent.putExtra("roomName", roomName);
//			intent.putExtra("roomId", roomId);
//			intent.putExtra("roomType", roomType);
//			break;
//		case Constants.MULTIPLE_TYPE:
//			intent = new Intent(this,MultipleActionActivity.class);
//			intent.putExtra("roomName", roomName);
//			intent.putExtra("roomId", roomId);
//			intent.putExtra("roomType", roomType);
//			break;
//		case Constants.USER_DEFINED_TYPE:
//			intent = new Intent(this,UserDefindActionActivity.class);
//			intent.putExtra("roomName", roomName);
//			intent.putExtra("roomId", roomId);
//			intent.putExtra("roomType", roomType);
//			break;
//		case Constants.ACTION_DISTRIBUTION_FOR_M2_TYPE:
//			intent = new Intent(this,ActionDistributionForM2activity.class);
//			intent.putExtra("roomName", roomName);
//			intent.putExtra("roomId", roomId);
//			intent.putExtra("roomType", roomType);
//			break;
//		}
//        //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT/*|Intent.FLAG_ACTIVITY_SINGLE_TOP*/);
//        notify.contentIntent = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_ONE_SHOT);
//
//		//notify.flags |= Notification.FLAG_ONGOING_EVENT;
//        notify.contentView = contentView;
//        notify.icon = R.drawable.icon_notify;
        hasBackstageNotify = true;
//        notifyManage.notify(13000, notify);
	}
	//更新状态
	public void updataBackstageNotificition(){
//		if(notifyManage == null || notify == null || contentView == null)
//			return;
//
//		contentView.setTextViewText(R.id.actionTxt, this.getString(R.string.end_massage));
//		notify.contentView = contentView;
		hasBackstageNotify = false;
//        notifyManage.notify(13000, notify);
	}
	//清除notify
	public void cancelBackstageNotificition(){
//		if(notifyManage == null)
//			return;
//
//		notifyManage.cancel(13000);
	}
}
