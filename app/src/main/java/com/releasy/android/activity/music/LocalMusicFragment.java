package com.releasy.android.activity.music;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.releasy.android.R;
import com.releasy.android.ReleasyApplication;
import com.releasy.android.activity.device.DeviceMainFragment;
import com.releasy.android.activity.main.BaseFragment;
import com.releasy.android.adapter.LocalMusicAdapter;
import com.releasy.android.bean.MusicBean;
import com.releasy.android.constants.RoomConstants;
import com.releasy.android.db.MusicDBUtils;
import com.releasy.android.db.ReleasyDatabaseHelper;
import com.releasy.android.service.MusicService;
import com.releasy.android.utils.Utils;

/**
 * 本地音乐页面
 * @author Lighting.Z
 *
 */
public class LocalMusicFragment extends BaseFragment{

	private View view;                            //主视图
	private LinearLayout nullMusicLayout;         //没有音乐时的视图
	private SwipeMenuListView listView;           //音乐列表
	private LocalMusicAdapter adapter;            //列表Adapter
	private int roomId;                           //放松馆Id
	private ReleasyDatabaseHelper db;             //数据库
	private List<MusicBean> musicList;            //音乐数据列表
	private ReleasyApplication app;               //Application
	private MusicRoomActivity activity;           //绑定的Activity
	private MusicService musicService;            //音乐service
	private MediaPlayer mPlayer;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLogD("LocalMusicFragment onCreate");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	showLogD("LocalMusicFragment onCreateView");
        
        if (view == null || view.getParent() != null) {
    		//判定视图是否存在   不重复加载
    		view = inflater.inflate(R.layout.fragment_local_music, container, false);
		}
    	init(); //初始化
    	
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showLogD("LocalMusicFragment onActivityCreated");
    }

    public void onResume() {
        super.onResume();
        showLogD("LocalMusicFragment onResume");
        getActivity().registerReceiver(broadcastReceiver, intentFilter()); //绑定广播
        getActivity().registerReceiver(phoneBroadcastReceiver, PhoneIntentFilter()); //绑定广播
        
        //如果有刷新
        if(activity.getChangeDownload()){
        	getDbData();  //重新获取数据
        	activity.setChangeDownload(false);  //设置刷新标识为false
        }
        
        //刷新播放图标
        if(current >= 0){
        	changePlayerAnimImg(current);
        }
    }

    public void onPause() {
        super.onPause();
        showLogD("LocalMusicFragment onPause");
    }

    public void onDestroyView() {
        super.onDestroyView();
        showLogD("LocalMusicFragment onDestroyView");
    }

    public void onDestroy() {
        super.onDestroy();
        showLogD("LocalMusicFragment onDestroy");
        try{
        	getActivity().unregisterReceiver(broadcastReceiver); //解绑广播
        	getActivity().unregisterReceiver(phoneBroadcastReceiver); //解绑广播
		}catch(Exception e){}
        
        stopPlayer();  //停止本页音乐播放
        musicService.recoveryPlayer();  //如果后台音乐有再播放则恢复后台音乐播放
    }
	
    /**
     * 初始化
     */
    private void init(){
    	app = (ReleasyApplication) getActivity().getApplication(); //获取Application
    	musicService = app.getMusicService();        //获取音乐Service
    	activity = (MusicRoomActivity)getActivity(); //获取Fragment绑定的Activity
    	roomId = activity.getRoomId();               //获取RoomID
    	db = MusicDBUtils.openData(getActivity());   //获取DB
    	musicList = new ArrayList<MusicBean>();      //音乐列表
    	mPlayer = new MediaPlayer();                 //播放器
    	
    	initViews();   //初始化视图
    	initEvents();  //初始化点击事件
    	getDbData();   //获取数据库音乐数据
    }
    
    /**
     * 初始化视图
     */
	protected void initViews() {
		listView = (SwipeMenuListView) view.findViewById(R.id.listView);  //音乐列表
		nullMusicLayout = (LinearLayout) view.findViewById(R.id.nullMusicLayout);  //没有音乐时 展示音乐列表为空提示Layout
		initSwipeMenuListView();  //初始化列表Mune
	}

	/**
     * 初始化点击事件
     */
	protected void initEvents() {
		//侧滑ListView 侧滑按钮点击事件
		listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
				case 0:
					MusicBean bean = musicList.get(position);  //获取点击的音乐列表项
					
					//当删除的为特定音乐时  不给删除
					/*if(bean.getMusicId() < 0){
						Toast.makeText(LocalMusicFragment.this.getActivity(), "放松馆特定歌曲,不能删除!"
								, Toast.LENGTH_LONG).show();
						return;
					}*/
					
					musicList.remove(position);     //从列表中移除
					adapter.notifyDataSetChanged(); //刷新列表
					activity.setChangeDel(true);    //设置数据刷新标识
					activity.setHaveDel();          //执行过删除操作 
					
					//当前音乐页面属于正在按摩的放松馆的音乐页面
					if(app.getIsWorking() && roomId == app.getRoomId()){
						musicService.changeMusicListDelete(bean.getMusicId()); //刷新后台音乐列表
					}
					else{
						changeMusicListDelete(position);  //刷新本页音乐列表
					}
					
					MusicDBUtils.deleteData(db, bean.getMusicId());  //从数据库中删除数据
					Utils.deleteFile(bean.getFilePath());  //删除SD卡文件
					
					if(musicList == null || musicList.size() == 0){
						nullMusicLayout.setVisibility(View.VISIBLE);
						listView.setVisibility(View.GONE);
					}
					break;
				
				}
				
				return false;
			}
		});
		
		//ListView Item点击事件
		listView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				//当前音乐页面属于正在按摩的放松馆的音乐页面
				if(app.getIsWorking() && roomId == app.getRoomId())
					musicService.startPlayer(musicList.get(position).getMusicId());  //更改播放音乐
				else if(app.getIsWorking()){
					if(current == position)
						return;
					
					musicService.pausePlayer(); //暂停后台音乐
					current = position;  //更新本地音乐游标
					prepareAndPlay();    //开始播放本地音乐
				}
				else{
					if(current == position)
						return;
					
					current = position;  //更新本地音乐游标
					prepareAndPlay();    //开始播放本地音乐
				}
				
				changePlayerAnimImg(position);
			}});
		
		
		//播放结束
		mPlayer.setOnCompletionListener(new OnCompletionListener(){
			public void onCompletion(MediaPlayer arg0) {
				prepareAndPlay();  //播放首歌曲
			}});
	}
	
	/**
	 * 改变播放状态图标
	 * @param position
	 */
	private void changePlayerAnimImg(int position){
		if(position >= musicList.size())
			return;
		
		for(int i = 0; i < musicList.size(); i ++){
			musicList.get(i).setIsChoose(false);
		}
		musicList.get(position).setIsChoose(true);
		adapter.notifyDataSetChanged();
	}
	
	/**
     * 初始化listview 侧滑控件
     */
	private void initSwipeMenuListView(){
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			public void create(SwipeMenu menu) {
				int txtSize = (int) LocalMusicFragment.this.getResources().getDimension(R.dimen.swipe_menu_txt_size);
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						LocalMusicFragment.this.getActivity().getApplicationContext());
				
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,  // set item background
						0x5a, 0x59)));
				deleteItem.setWidth(Utils.dp2px(90,LocalMusicFragment.this.getActivity())); // set item width
				//deleteItem.setIcon(R.drawable.ic_delete); // set a icon
				deleteItem.setTitle(R.string.delete);
				int whiteColor = LocalMusicFragment.this.getResources().getColor(R.color.white);
				deleteItem.setTitleColor(whiteColor);
				deleteItem.setTitleSize(txtSize);
				
				menu.addMenuItem(deleteItem); // add to menu
			}
		};
		listView.setMenuCreator(creator);
	}
	
	/**
	 * 获取数据库音乐数据
	 */
	private void getDbData(){
		musicList.clear();  //清除数据
		musicList = MusicDBUtils.searchRoomMusicData(db, roomId);  //从数据库中加载数据
		
		//MusicBean bean = RoomConstants.getRoomMusic(roomId);  //获取特定音乐
		//if(bean != null) musicList.add(bean);
		
		//当前音乐页面属于正在按摩的放松馆的音乐页面
		if(app.getIsWorking() && roomId == app.getRoomId()){
			if(!(musicService.getMusicCurrent() >= musicList.size())){
				showLogD("current :" + musicService.getMusicCurrent() + "    size : " + musicList.size());
				musicList.get(musicService.getMusicCurrent()).setIsChoose(true);
			}
		}
		
		if(musicList == null || musicList.size() == 0){
			nullMusicLayout.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		}
		else{
			nullMusicLayout.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			adapter = new LocalMusicAdapter(this.getActivity(),musicList);
			listView.setAdapter(adapter);
		}
	}
	
	/**
	 *  获取歌曲并播放
	 */
	private int current = -1;    //游标
	//播放方法
	private void prepareAndPlay(){
		try {
			if(musicList.get(current).getIsAssets()){
				AssetManager am = getActivity().getAssets();
				AssetFileDescriptor afd = am.openFd(musicList.get(current).getFilePath());
				mPlayer.reset();
				mPlayer.setDataSource(afd.getFileDescriptor()
						, afd.getStartOffset()
						, afd.getLength());
			}
			else{
				mPlayer.reset();
				File file = new File(musicList.get(current).getFilePath());
				FileInputStream fis = new FileInputStream(file);
				mPlayer.setDataSource(fis.getFD()); 
			}
			mPlayer.prepare();
			mPlayer.start();
		}
		catch (IOException e) {
			Log.d("z17m","music prepareAndPlay catch");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 停止播放
	 */
	private void stopPlayer(){
		Log.d("z17m","stopPlayer");
		if(mPlayer.isPlaying())
			mPlayer.stop();
	}
	
	/**
	 * 当删除音乐时 更新列表
	 */
	private void changeMusicListDelete(int position){
		if(!mPlayer.isPlaying())
			return;
		
		//更新游标
		if(current > position){
			current = current - 1;
			if(current < 0) current = 0;
		}
				
		else if(current == position){
			current = 0;
			stopPlayer();
		}		
	}
	
	/**
	 * 广播Action
	 */
	private static IntentFilter intentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MusicService.UPDATA_MUSIC_UI);  //音乐更新
		return intentFilter;
	}
	
	/**
	 * 广播注册
	 */
	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (MusicService.UPDATA_MUSIC_UI.equals(action)) {
				Bundle bundle = intent.getExtras();
				int musicCurrent = bundle.getInt("musicCurrent");

				//当前音乐页面属于正在按摩的放松馆的音乐页面
				if(app.getIsWorking() && roomId == app.getRoomId()){
					changePlayerAnimImg(musicCurrent);
				}
			}
		}
	};
	
	private boolean isPause = false;      //是否暂停标识
	private boolean incomingFlag = false;   
	
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
		if(isPause && mPlayer != null){
			isPause = false;
			mPlayer.start();
		}
	}
	
	private final BroadcastReceiver phoneBroadcastReceiver = new BroadcastReceiver() {  
    	public void onReceive(Context context, Intent intent) {  
    		//如果是拨打电话  
    		if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){                          
    			//incomingFlag = false;    
    			Log.d("z17m","ACTION_NEW_OUTGOING_CALL");
    		}else{                          
    			//如果是来电  
    			/*TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);                          
    			switch (tm.getCallState()) {  
    			case TelephonyManager.CALL_STATE_RINGING:  
    				//来电话
    				//incomingFlag = true;//标识当前是来电  
    				pausePlayer();
    				Log.d("z17m","CALL_STATE_RINGING");
                    break;  
                case TelephonyManager.CALL_STATE_OFFHOOK:    
                	//通话
                	pausePlayer();
                	Log.d("z17m","CALL_STATE_OFFHOOK");
                    break;  
                case TelephonyManager.CALL_STATE_IDLE:   
                	//挂电话
                	recoveryPlayer();
                	Log.d("z17m","CALL_STATE_IDLE");
                    break;  
                }   */
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
