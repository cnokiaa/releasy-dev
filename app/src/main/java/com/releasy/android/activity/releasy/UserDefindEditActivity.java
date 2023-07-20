package com.releasy.android.activity.releasy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.releasy.android.R;
import com.releasy.android.activity.main.BaseActivity;
import com.releasy.android.bean.ActionBean;
import com.releasy.android.bean.DeviceBean;
import com.releasy.android.bean.MusicBean;
import com.releasy.android.bean.RoomBean;
import com.releasy.android.constants.RoomConstants;
import com.releasy.android.db.ActionDBUtils;
import com.releasy.android.db.DeviceDBUtils;
import com.releasy.android.db.MusicDBUtils;
import com.releasy.android.db.ReleasyDatabaseHelper;
import com.releasy.android.db.RoomDBUtils;
import com.releasy.android.utils.BitmapUtils;
import com.releasy.android.utils.StringUtils;
import com.releasy.android.view.DeviceItemLayout;
import com.releasy.android.view.TopNavLayout;

/**
 * 编辑自定义放松馆页面
 * @author Lighting.Z
 *
 */
public class UserDefindEditActivity extends BaseActivity{

	public static final int TO_NEW = 0;
	public static final int TO_EDIT = 1;
	public static final int SELECT_PIC = 101;     //跳转图片页面参数 
	public static final int SELECT_MUSIC = 102;   //跳转音乐页面参数 
	public static final int SELECT_ACTION = 103;  //跳转图片页面参数 
	private TopNavLayout mTopNavLayout;           //导航菜单栏
	private ImageView bgImg;                      //背景Img
	private ImageView titlePicImg;                //封面Img
	private EditText roomNameEdit;                //馆名Edit
	private LinearLayout deviceLayout;            //设备加载Layout
	private ReleasyDatabaseHelper db;             //数据库
	private List<DeviceBean> deviceList;
	private List<DeviceItemLayout> deviceItemList;
	private LinearLayout musicLayout;             //音乐Layout
	private TextView musicNameTxt;                //音乐名称Txt
	
	private String picPath;                       //封面地址
	private String musicPath;                     //音乐地址
	private String musicName;                     //音乐名称
	private String musicPic;                      //音乐封面
	private String musicArtist;                   //音乐作者
	private int type;
	private int roomId;
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions displayImageOptions;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	
	/*
	 * 异步加载方法
	 */
	private class AnimateFirstDisplayListener extends SimpleImageLoadingListener{
        List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
        {
            ImageView imageView = (ImageView)view;
            boolean firstDisplay = !displayedImages.contains(imageUri);
            if (firstDisplay){
                FadeInBitmapDisplayer.animate(imageView, 500);
                displayedImages.add(imageUri);
            }
        }
    }
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_defind_edit);
        
        getBundle();         //获取传入数据
        init();              //初始化
	}
	
	private void getBundle(){
		Bundle bundle = this.getIntent().getExtras();
		type = bundle.getInt("type");     //设备Item
		showLogD("type : " + type);
		if(type == TO_EDIT)
			roomId = bundle.getInt("roomId");     //馆ID
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		db = DeviceDBUtils.openData(this);  //获取DB
		deviceItemList = new ArrayList<DeviceItemLayout>();
		
		initViews();    //初始化视图
		setTopNav();    //初始化导航栏
		initEvents();   //初始化点击事件
		getDbData();    //获取数据
		
	}
	
	/**
	 * 初始化视图
	 */
	protected void initViews() {
		mTopNavLayout = (TopNavLayout) findViewById(R.id.topNavLayout);
		bgImg = (ImageView) findViewById(R.id.bgImg);
		titlePicImg = (ImageView) findViewById(R.id.titlePicImg);
		roomNameEdit = (EditText) findViewById(R.id.roomNameEdit);
		deviceLayout = (LinearLayout) findViewById(R.id.deviceLayout);
		musicLayout = (LinearLayout) findViewById(R.id.musicLayout);
		musicNameTxt = (TextView) findViewById(R.id.musicNameTxt);
	}
	
	/**
	 * 初始化导航栏
	 */
	private void setTopNav(){
		mTopNavLayout.setTitltTxt(R.string.add_new_shop);
		mTopNavLayout.setLeftImgSrc(R.drawable.ic_nav_bar_back);
		mTopNavLayout.setRightTxt(R.string.save);
	}
	
	/**
	 * 初始化点击事件
	 */
	protected void initEvents() {
		//音乐控件点击跳转
		musicLayout.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				Intent intent = new Intent(UserDefindEditActivity.this,SelectMusicActivity.class);
				startActivityForResult(intent, SELECT_MUSIC);
			}});
		
		//图片控件点击跳转
		titlePicImg.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				Intent intent = new Intent(UserDefindEditActivity.this,SelectPicActivity.class);
				startActivityForResult(intent, SELECT_PIC);
			}});
		
		//左侧导航栏按钮点击返回
		mTopNavLayout.setLeftImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				UserDefindEditActivity.this.finish();
			}});
		
		//右侧导航栏按钮点击保存
		mTopNavLayout.setRightTxtOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				toSave();
			}});
	}
	
	/**
	 * 获取数据库数据
	 */
	private void getDbData(){
		deviceList = DeviceDBUtils.searchData(db);
		
		if(type == TO_EDIT){
			RoomBean room =RoomDBUtils.searchRoomIdData(db, roomId);
			picPath = room.getRoomPic();
			setTitleImg(room.getRoomPic());
			roomNameEdit.setText(room.getRoomName());
			
			List<MusicBean> musicList = MusicDBUtils.searchRoomMusicData(db, roomId);
			if(musicList != null && musicList.size() > 0){
				musicPath = musicList.get(0).getFilePath();
				musicName = musicList.get(0).getName();
				musicPic = musicList.get(0).getArtPath();
				musicArtist = musicList.get(0).getArtist();
				setMusicInfo(musicName,musicPath);
			}
			
			List<ActionBean> actionList = ActionDBUtils.searchRoomActionData(db, roomId, this);
			for(int i = 0; i < deviceList.size(); i++){
				if(i >= actionList.size()) break;
				deviceList.get(i).setAction(actionList.get(i));
			}
		}
		
		for(int i = 0; i < deviceList.size(); i++){
			DeviceBean bean = deviceList.get(i);
			bean.setPower(1);
			DeviceItemLayout layout = new DeviceItemLayout(this, bean, i);
			layout.setOnClick(new DeviceItemOnClick(i));
			deviceLayout.addView(layout);
			deviceItemList.add(layout);
		}
	}
	
	/**
	 * 设备点击事件
	 */
	private class DeviceItemOnClick implements OnClickListener{
		int item ;  //设备项
		public DeviceItemOnClick(int item){
			this.item = item;
		}
		public void onClick(View arg0) {
			int power = 1;          //力度
			int actionId = 0;       //动作 id
			int actionType = 0;     //动作 Type
			boolean haveAction = false;  //是否有动作存储
			DeviceBean deivce = deviceList.get(item);
			
			//判断是否有动作存储
			if(deivce.getAction() != null){
				haveAction = true;
				actionId = deivce.getAction().getActionId();
				actionType = deivce.getAction().getActionType();
				power = deivce.getAction().getStrength(); //获得力度
			}
			
			Intent intent = new Intent(UserDefindEditActivity.this,SelectActionActivity.class);
			intent.putExtra("deviceItem", item);
			intent.putExtra("haveAction", haveAction);
			intent.putExtra("actionId", actionId);
			intent.putExtra("actionType", actionType);
			intent.putExtra("power", power);
			
			startActivityForResult(intent, SELECT_ACTION);
		}
	}
	
	
	/**
	 * 回调函数
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		//选择图片回调
		case SELECT_PIC:
			if (resultCode == Activity.RESULT_OK){  
				picPath = data.getExtras().getString("picPath");  
				setTitleImg(picPath);
        	}
			break;
	    //选择音乐回调
		case SELECT_MUSIC:
			if (resultCode == Activity.RESULT_OK){  
				musicPath = data.getExtras().getString("musicPath");  
				musicName = data.getExtras().getString("musicName");  
				musicPic = data.getExtras().getString("musicPic");  
				musicArtist = data.getExtras().getString("musicArtist");  
				setMusicInfo(musicName,musicPath);
        	}
			break;
		//选择动作回调
		case SELECT_ACTION:
			if (resultCode == Activity.RESULT_OK){  
				int item = data.getExtras().getInt("deviceItem");  
				int dbId = data.getExtras().getInt("dbId");  
				int power = data.getExtras().getInt("power");  
				setDeviceInfo(item,dbId,power);
        	}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 设置封面图片
	 */
	private void setTitleImg(String path){
		showLogD("path : " + path);

		imageLoader.displayImage("file://" + path, titlePicImg
				, displayImageOptions, animateFirstListener);
		
		imageLoader.displayImage("file://" + path, bgImg
				, displayImageOptions, animateFirstListener);
		
	}
	
	/**
	 * 设置音乐信息
	 */
	private void setMusicInfo(String musicName, String musicPath){
		showLogD("musicName : " + musicName + "    musicPath : " + musicPath);
		musicNameTxt.setText(musicName);
	}
	
	/**
	 * 设置动作信息
	 */
	private void setDeviceInfo(int item, int dbId, int strength){
		ActionBean action =ActionDBUtils.searchDBIdActionData(db, dbId, this);
		DeviceBean device = deviceList.get(item);
		device.setAction(action);
		device.getAction().setStrength(strength);
		deviceItemList.get(item).changeActionInfo(device);
	}
	
	/**
	 * 检测输入信息是否完整
	 * @return
	 */
	private boolean toCheckInfo(){
		String name = roomNameEdit.getText().toString();
		//馆名为空 提示用户
		if(StringUtils.isBlank(name)){
			Toast.makeText(this, R.string.input_shop_name, Toast.LENGTH_LONG).show();
			return false;
		}
		
		//设备动作为空 提示用户
		for(int i = 0; i < deviceList.size(); i++){
			if(deviceList.get(i).getAction() == null){
				Toast.makeText(this, R.string.pls_selsct_action, Toast.LENGTH_LONG).show();
				return false;
			}
		}
		
		//馆名为空 提示用户
		if(StringUtils.isBlank(musicPath)){
			Toast.makeText(this, R.string.no_select_music_toast, Toast.LENGTH_LONG).show();
			return false;
		}
		
		return true;
	}
	
	/**
	 * 保存自定义放松馆数据到数据库
	 */
	private void toSave(){
		if(!toCheckInfo())
			return;
		
		if(type == TO_NEW)
			saveNewRoom();
		else if(type == TO_EDIT)
			saveUpdataRoom();
		else
			this.finish();
		
		//RoomDBUtils.searchAllData(db);
		//ActionDBUtils.searchAllData(db);
		//MusicDBUtils.searchAllData(db);
		
		String name = roomNameEdit.getText().toString();
		Intent data = new Intent();  
		data.putExtra("roomName", name); 
		setResult(Activity.RESULT_OK,data);  
		this.finish();
	}
	
	/**
	 * 保存新的放松馆
	 */
	private void saveNewRoom(){
		int id = RoomDBUtils.searchMaxRoomIdData(db) + 1;
		String name = roomNameEdit.getText().toString();
		RoomDBUtils.insertData(db,id,name,RoomConstants.ROOM_USER_DEFIND,picPath,0);
		
		//int musicId = MusicDBUtils.searchMaxMusicIdData(db) + 1;
		MusicDBUtils.insertData(db, id, 0, musicName, musicPic, musicPath, musicArtist);
		
		for(int i = 0; i < deviceList.size(); i++){
			ActionBean action = deviceList.get(i).getAction();
			ActionDBUtils.insertData(db, action.getActionId(), id, action.getActionName(), 2, action.getActionPicUrl()
					, action.getBytesCheck(), action.getHighTime(), action.getLowTime(), action.getInnerHighAndLow()
					, action.getPeriod(), action.getInterval(), action.getRateMin(), action.getRateMax(), action.getPowerLV()
					, action.getMaxWorkTime(),action.getStrength());
		}
	}
	
	/**
	 * 更新原有的放松馆
	 */
	private void saveUpdataRoom(){
		String name = roomNameEdit.getText().toString();
		RoomDBUtils.updataRoom(db,roomId,name,picPath);
		
		MusicDBUtils.deleteRoomMuisc(db,roomId);
		//int musicId = MusicDBUtils.searchMaxMusicIdData(db) + 1;
		MusicDBUtils.insertData(db, roomId, 0, musicName, musicPic, musicPath, musicArtist);
		
		ActionDBUtils.deleteRoomActionData(db,roomId);
		for(int i = 0; i < deviceList.size(); i++){
			ActionBean action = deviceList.get(i).getAction();
			ActionDBUtils.insertData(db, action.getActionId(), roomId, action.getActionName(), 2, action.getActionPicUrl()
					, action.getBytesCheck(), action.getHighTime(), action.getLowTime(), action.getInnerHighAndLow()
					, action.getPeriod(), action.getInterval(), action.getRateMin(), action.getRateMax(), action.getPowerLV()
					, action.getMaxWorkTime(), action.getStrength());
		}
	}
}
