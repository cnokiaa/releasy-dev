package com.releasy.android.activity.music;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.releasy.android.R;
import com.releasy.android.activity.main.BaseActivity;

/**
 * 音乐列表页面
 * @author Lighting.Z
 *
 */
public class MusicRoomActivity extends BaseActivity{

	private ImageView backImg;
	private RadioGroup musicRG;
	private Fragment currentFragment;                 //记录当前Fragment
	private LocalMusicFragment localMusicFragment;
	private WebMusicFragment webMusicFragment;
	private int roomId;
	private boolean changeDel = false;                //是否有有删除操作
	private boolean changeDownload = false;           //是否有下载完成操作
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_room);
		
		getBundle();       //获取传入数据
		init();
	}
	
	/**
	 * 获取传入的数据
	 */
	private void getBundle(){
		Bundle bundle = this.getIntent().getExtras();
		roomId = bundle.getInt("roomId");            //获取放松馆Id
	}
	
	/**
	 *  初始化
	 */
	private void init(){
		localMusicFragment = new LocalMusicFragment();
		webMusicFragment = new WebMusicFragment();
		
		initViews();
		initEvents();
		
		initSquareContentFg();
	}
	
	/**
	 *  初始化控件
	 */
	protected void initViews() {
		backImg = (ImageView) findViewById(R.id.backImg);
		musicRG = (RadioGroup) findViewById(R.id.tabRG);
	}
	
	/**
	 * 获取放松馆Id
	 * @return
	 */
	public int getRoomId(){
		return roomId;
	}
	
	/**
	 * 获取是否有删除音乐操作
	 * @return
	 */
	public boolean getChangeDel(){
		return changeDel;
	}
	/**
	 * 设置是否有删除音乐操作
	 * @return
	 */
	public void setChangeDel(boolean changeDel){
		this.changeDel = changeDel;
	}
	
	/**
	 * 获取是否有音乐下载完成操作
	 * @return
	 */
	public boolean getChangeDownload(){
		return changeDownload;
	}
	/**
	 * 设置是否有音乐下载完成操作
	 * @return
	 */
	public void setChangeDownload(boolean changeDownload){
		this.changeDownload = changeDownload;
	}
	
	
	/**
	 * 设置执行过删除音乐操作
	 * @return
	 */
	public void setHaveDel(){
		setResult(Activity.RESULT_OK);  
	}
	/**
	 * 设置执行过下载音乐操作
	 * @return
	 */
	public void setHaveDownload(){
		setResult(Activity.RESULT_OK);  
	}
	
	/**
	 *  初始化点击事件
	 */
	protected void initEvents() {
		backImg.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				MusicRoomActivity.this.finish();
			}});
		
		musicRG.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(RadioGroup arg0, int checkId) {
				switch(checkId){
				case R.id.localRG:
					changeContentFg(localMusicFragment,currentFragment);    //切换内容页
					currentFragment = localMusicFragment;    //记录游标
					break;
				case R.id.webRG:
					changeContentFg(webMusicFragment,currentFragment);    //切换内容页
					currentFragment = webMusicFragment;    //记录游标
					break;
				default:
					break;
				}
			}
		});
	}
	
	/*
	 *  初始化内容视图
	 */
	private void initSquareContentFg(){
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content, localMusicFragment);
        ft.commit();
        
        currentFragment = localMusicFragment;     //记录游标
	}
	
	/*
	 *  内容显示切换方法
	 */
	private void changeContentFg(Fragment showFragment, Fragment hideFragment){
		if(showFragment == hideFragment)
			return;
		
		FragmentTransaction ft = MusicRoomActivity.this
		                         .getSupportFragmentManager().beginTransaction();
		hideFragment.onPause();
		if(showFragment.isAdded()){
			showFragment.onResume(); // 启动目标的onResume()
		}
		else{
          ft.add(R.id.content, showFragment);
        }
		ft.show(showFragment);
		ft.hide(hideFragment);
		ft.commit();
	}

}
