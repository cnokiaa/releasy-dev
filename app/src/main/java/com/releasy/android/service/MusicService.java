package com.releasy.android.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.releasy.android.R;
import com.releasy.android.ReleasyApplication;
import com.releasy.android.bean.MusicBean;
import com.releasy.android.constants.RoomConstants;
import com.releasy.android.db.MusicDBUtils;
import com.releasy.android.db.ReleasyDatabaseHelper;
import com.releasy.android.utils.SharePreferenceUtils;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;

/**
 * 音乐service
 * @author Lighting.Z
 *
 */
public class MusicService extends Service{

	private final IBinder mBinder = new LocalBinder();
	private AssetManager am;        
	private MediaPlayer mPlayer;
	private int current = 0;              //游标
	private int muiscId = -100;           //音乐Id
	private List<MusicBean> roomMusicList;//房间音乐列表
	private List<MusicBean> playerList;   //播放音乐列表
	private SharePreferenceUtils spInfo;  //SharePreference
	private ReleasyDatabaseHelper db;     //数据库
	private boolean isPause = false;      //是否暂停标识
	private int roomId = -1;              //房间标识
	private ReleasyApplication app;       //Application
	
	public final static String UPDATA_MUSIC_UI = "com.releasy.android.UPDATA_MUSIC_UI";
	
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	public class LocalBinder extends Binder {
		public MusicService getService() {
			return MusicService.this;
		}
	}
	
	public void onDestroy(){
		try{
			unregisterReceiver(broadcastReceiver); //解绑广播
		}catch(Exception e){}
		
        super.onDestroy();
        
    }
	
	/**
	 * 初始化
	 */
	public void init(){	
		db = MusicDBUtils.openData(this);  //获取DB
		spInfo = new SharePreferenceUtils(this); 
		roomMusicList = new ArrayList<MusicBean>();
		playerList = new ArrayList<MusicBean>();
		registerReceiver(broadcastReceiver, PhoneIntentFilter()); //绑定广播
		
		am = getAssets();
		mPlayer = new MediaPlayer();
		
		mPlayer.setOnCompletionListener(new OnCompletionListener(){
			public void onCompletion(MediaPlayer arg0) {
				if(playerList == null || playerList.size() == 0)
					return;
				
				current = current + 1;
				if(current >= playerList.size()){
					current = 0;
				}
				
				//获取音乐名称 发送广播更新界面
				Intent intent = new Intent(UPDATA_MUSIC_UI);
				intent.putExtra("musicName", playerList.get(current).getName());
				intent.putExtra("musicId", playerList.get(current).getMusicId());
				intent.putExtra("musicCurrent", current);
				intent.putExtra("size", playerList.size());
				sendBroadcast(intent);
				
				//getMusicName(roomId);    //获取音乐名称 发送广播更新界面
				prepareAndPlay();  //播放首歌曲
			}});
	} 
	
	/**
	 *  获取歌曲并播放
	 */
	public void prepareAndPlay(){
		try {
			muiscId = playerList.get(current).getMusicId();
			
			if(playerList.get(current).getMusicId() < 0){
				AssetFileDescriptor afd = am.openFd(playerList.get(current).getFilePath());
				mPlayer.reset();
				mPlayer.setDataSource(afd.getFileDescriptor()
						, afd.getStartOffset()
						, afd.getLength());
			}
			else{
				mPlayer.reset();
				File file = new File(playerList.get(current).getFilePath());
				FileInputStream fis = new FileInputStream(file);
				mPlayer.setDataSource(fis.getFD()); 
			}
			mPlayer.prepare();
			mPlayer.start();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 开始播放
	 */
	public void startPlayerList(int room_Id){	
		stopPlayer();
		
		playerList.clear();
		for(int i = 0; i < roomMusicList.size(); i++){
			playerList.add(roomMusicList.get(i));
		}
		
		//playerList = roomMusicList;
		if(roomId != room_Id){
			roomId = room_Id;
			current = 0;
			muiscId = -100;
		}
		
		if(playerList == null || playerList.size() == 0){
			//Toast.makeText(MusicService.this, "当前没有可播放的歌曲", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(current >= playerList.size())
			current = 0;
		
		prepareAndPlay();
	}
	
	/**
	 * 开始播放特定Id音乐
	 */
	public void startPlayer(int music_Id){
		if(playerList == null || playerList.size() == 0){
			//Toast.makeText(MusicService.this, "当前没有可播放的歌曲", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(music_Id == muiscId)
			return;
		
		for(int i = 0; i < playerList.size(); i ++){
			if(music_Id == playerList.get(i).getMusicId())
				current = i;
		}
		
		prepareAndPlay();
	}
	
	public void muteMusic(){
		mPlayer.setVolume(0,0);
	}
	
	public void openMusic(){
		mPlayer.setVolume(1,1);
	}
	
	/**
	 * 暂停音乐
	 */
	public void pausePlayer(){
		if(mPlayer != null){
			isPause = true;
			mPlayer.pause();  
		}
	}       
	
	/**
	 * 恢复播放
	 */
	public void recoveryPlayer(){
		app = (ReleasyApplication) this.getApplication(); //获取Application
		if(!app.getIsWorking())
			return;
		
		if(isPause && mPlayer != null){
			isPause = false;
			mPlayer.start();
		}
	}
	
	/**
	 * 停止播放
	 */
	public void stopPlayer(){
		if(mPlayer.isPlaying())
			mPlayer.stop();
	}
	
	/**
	 * 获取播放列表
	 * @param roomId
	 */
	public void getMusicList(int room_Id){
		roomMusicList.clear();
		roomMusicList = MusicDBUtils.searchRoomMusicData(db, room_Id);
	}
	
	/**
	 * 获取播放列表
	 * @param roomId
	 */
	public List<MusicBean> getRoomMusicList(int room_Id){
		return MusicDBUtils.searchRoomMusicData(db, room_Id);
	}
	
	
	/**
	 * 获取音乐名称
	 * @param roomId
	 */
	public String getMusicName(int room_Id){
		//Intent intent = new Intent(UPDATA_MUSIC_UI);
		String musicName = this.getString(R.string.click_to_select_music);
		if(roomMusicList.size() == 0){
			//intent.putExtra("musicName", "当前没有可播放的歌曲!");
			musicName = this.getString(R.string.click_to_select_music);
		}
		else{
			if(room_Id == roomId){
				if(current >= playerList.size())
					current = 0;
				musicName = playerList.get(current).getName();
				//intent.putExtra("musicName", roomMusicList.get(current).getName());
			}
			else{
				musicName = roomMusicList.get(0).getName();
				//intent.putExtra("musicName", roomMusicList.get(0).getName());
			}
		}
		//sendBroadcast(intent);
		
		return musicName;
	}
	
	/**
	 * 获取音乐Id
	 * @param muiscId
	 */
	public int getMusicId(){
		return muiscId;
	}
	
	/**
	 * 获取游标
	 * @param muiscId
	 */
	public int getMusicCurrent(){
		return current;
	}
	
	/**
	 * 当删除音乐时 更新列表
	 */
	public void changeMusicListDelete(int music_Id){
		for(int i = 0; i < playerList.size(); i++){
			if(music_Id == playerList.get(i).getMusicId()){
				if(current > i){
					current = current - 1;
					if(current < 0) current = 0;
					playerList.remove(i);
					break;
				}
				
				else if(current == i){
					if(current >= playerList.size()) current = 0;
					playerList.remove(i);
					if(playerList == null || playerList.size() == 0){
						stopPlayer();
						return;
					}
					
					prepareAndPlay();
					break;
				}
			}	
		}		
	}
	
	/**
	 * 刷新列表
	 */
	public void refreshMusicList(){
		List<MusicBean> list = MusicDBUtils.searchRoomMusicData(db, roomId);
		if(list == null || list.size() == 0){
			return;
		}
		
		for(int i = 0; i < list.size(); i++){
			if(muiscId == list.get(i).getMusicId()){
				current = i;
				break;
			}	
		}
		
		if(playerList == null || playerList.size() == 0){
			playerList = list;
			roomMusicList = list;
			current = 0;
			muiscId = -100;
			
			Intent intent = new Intent(UPDATA_MUSIC_UI);
			intent.putExtra("musicName", playerList.get(current).getName());
			intent.putExtra("musicId", playerList.get(current).getMusicId());
			intent.putExtra("musicCurrent", current);
			intent.putExtra("size", playerList.size());
			sendBroadcast(intent);
			
			prepareAndPlay();
		}
		else{
			playerList = list;
			roomMusicList = list;
		}
	}
	
	/**
	 * 刷新列表
	 */
	public void refreshMusicList(int room_Id){
		List<MusicBean> list = MusicDBUtils.searchRoomMusicData(db, room_Id);
		
		roomMusicList = list;
	}
	
	
	private boolean incomingFlag = false;   
	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {  
    	public void onReceive(Context context, Intent intent) {  
    		//如果是拨打电话  
    		if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){                          
    			//incomingFlag = false;    
    		}else{                          
    			//如果是来电 
    			app = (ReleasyApplication) MusicService.this.getApplication(); //获取Application
    			TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);                          
    			switch (tm.getCallState()) {  
    			case TelephonyManager.CALL_STATE_RINGING:  
    				//来电话
    				if(mPlayer != null && app.getIsWorking()){
    					//incomingFlag = true;
    					mPlayer.pause();  
    				}
                    break;  
                case TelephonyManager.CALL_STATE_OFFHOOK:    
                	//通话
                	if(mPlayer != null && app.getIsWorking()){
    					mPlayer.pause();  
    					//incomingFlag = true;
                	}
                    break;  
                case TelephonyManager.CALL_STATE_IDLE:   
                	//挂电话
            		if(!app.getIsWorking()){
            			return;
            		}
            		
            		if(!isPause && mPlayer != null && app.getIsWorking()){
            			//isPause = false;
            			//incomingFlag = false;
            			mPlayer.start();
            		}
                    break;  
                }   
            }  
        }  
    }; 
    
    /**
	 * 广播Action
	 */
	private static IntentFilter PhoneIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.PHONE_STATE"); 
		intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		return intentFilter;
	}
	
}
