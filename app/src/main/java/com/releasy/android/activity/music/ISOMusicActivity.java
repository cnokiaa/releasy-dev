package com.releasy.android.activity.music;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.releasy.android.R;
import com.releasy.android.ReleasyApplication;
import com.releasy.android.activity.main.BaseActivity;
import com.releasy.android.adapter.SelectMusicAdapter;
import com.releasy.android.bean.MusicBean;
import com.releasy.android.db.DeviceDBUtils;
import com.releasy.android.db.MusicDBUtils;
import com.releasy.android.db.ReleasyDatabaseHelper;
import com.releasy.android.service.MusicService;
import com.releasy.android.utils.SharePreferenceUtils;
import com.releasy.android.view.TopNavLayout;

public class ISOMusicActivity extends BaseActivity{
	
	private TopNavLayout mTopNavLayout;           //导航菜单栏
	private List<MusicBean> musicList;
	private ListView listView;
	private SelectMusicAdapter adapter;
	private ReleasyDatabaseHelper db;             //数据库
	private int roomId = 0;
	private ReleasyApplication app;               //Application
	private MusicService musicService;            //音乐service
	private SharePreferenceUtils spInfo;          //SharePreference
	private String musicPath = "";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_select_music);
	        
	    getBundle();
	    init();    //初始化
	}
	
	private void getBundle(){
		Bundle bundle = this.getIntent().getExtras();
		roomId = bundle.getInt("roomId");     //馆ID
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		musicList = new ArrayList<MusicBean>();
		app = (ReleasyApplication) this.getApplication(); //获取Application
		db = DeviceDBUtils.openData(this);  //获取DB
		spInfo = new SharePreferenceUtils(this); //获取SharePreference 存储
		musicService = app.getMusicService();        //获取音乐Service
		List<MusicBean> musicList = musicService.getRoomMusicList(roomId);
		if(musicList != null && musicList.size() > 0){
			musicPath = musicList.get(0).getFilePath();
	    }
		
		
		initViews();    //初始化控件
		initEvents();   //初始化点击事件
		setTopNav();    //初始化导航栏
		initProgressDialog(this.getResources().getString(R.string.loading));
		
		/*if (Build.VERSION.SDK_INT >= 23){
			if(spInfo.getMusicCursor()){
				AlertDialog.Builder builder = new Builder(this);
		    	builder.setMessage(R.string.music_permission_title);
		    	builder.setTitle(R.string.music_permission_msg);
		    	builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
		    		public void onClick(DialogInterface dialog, int which) {
		    			dialog.dismiss();
		    			progressDialog.show();
		    			
		    		}});
		    	
		    	builder.create().show();
			}
		}

		spInfo.setMusicCursor(false);*/

		initProgressDialog(this.getResources().getString(R.string.loading));
		progressDialog.show();
		putAsyncTask(new GetFileListAsyncTask());
	} 
	
	/**
	 * 初始化导航栏
	 */
	private void setTopNav(){
		mTopNavLayout.setTitltTxt(R.string.select_music);
		mTopNavLayout.setLeftImgSrc(R.drawable.ic_nav_bar_back);
		mTopNavLayout.setRightImgSrc(R.drawable.ic_finish);
		
		if(spInfo.getIsMusicPlay()){
			app.openMusic();  //打开声音
			mTopNavLayout.setRightSecondImgSrc(R.drawable.ic_volume_float);
		}
		else{
			app.muteMusic();  //静音
			mTopNavLayout.setRightSecondImgSrc(R.drawable.ic_volume_float_mute);
		}
		
	}
	
	/**
	 * 初始化控件
	 */
	protected void initViews() {
		mTopNavLayout = (TopNavLayout) findViewById(R.id.topNavLayout);
		listView = (ListView) findViewById(R.id.listView);
	}

	/**
	 * 初始化点击事件
	 */
	protected void initEvents() {
		//导航栏返回按钮逻辑
		mTopNavLayout.setLeftImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				spInfo.setIOSMusic(false);
				ISOMusicActivity.this.finish();
			}});
		
		//导航栏确定按钮逻辑
		mTopNavLayout.setRightImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				MusicBean bean = null;
				for(int i = 0; i <  musicList.size(); i++){
					if(musicList.get(i).getIsChoose())
						bean = musicList.get(i);
				}
				
				if(bean == null){
					//Toast.makeText(ISOMusicActivity.this, R.string.no_select_music_toast, Toast.LENGTH_LONG).show();
					if(MusicDBUtils.isISOMusicExist(db,roomId)){
		            	MusicDBUtils.deleteRoomMuisc(db, roomId);
						musicService.refreshMusicList(roomId);
					}
					musicService.stopPlayer();
					finish();  
				}
				else{
					//Intent data=new Intent();  
		            //data.putExtra("musicPath", bean.getFilePath()); 
		            //data.putExtra("musicName", bean.getName());  
		            //data.putExtra("musicPic", bean.getArtPath());  
		            //data.putExtra("musicArtist", bean.getArtist());  
		            //setResult(Activity.RESULT_OK, data);  
		            
		            if(MusicDBUtils.isISOMusicExist(db,roomId)){
		            	MusicDBUtils.updataISOMusic(db, roomId, 0, bean.getName(), bean.getArtPath(), bean.getFilePath(), bean.getArtist());
		            }
		            else
		            	MusicDBUtils.insertData(db, roomId, 0, bean.getName(), bean.getArtPath(), bean.getFilePath(), bean.getArtist());
		            
		            musicService.refreshMusicList(roomId);
		            if(app.getIsWorking() && roomId == app.getRoomId()){
						//musicService.startPlayer(musicList.get(position).getMusicId());  //更改播放音乐
		            	//musicService.refreshMusicList();
		            	musicService.startPlayerList(roomId);
		            }
		            //setResult(Activity.RESULT_OK);  
		            finish();  
				}
			}});
		
		//导航栏静音按钮逻辑
		mTopNavLayout.setRightSecondImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				if(spInfo.getIsMusicPlay()){
					spInfo.setIsMusicPlay(false);
					app.muteMusic();  //静音
					mTopNavLayout.setRightSecondImgSrc(R.drawable.ic_volume_float_mute);
				}
				else{
					spInfo.setIsMusicPlay(true);
					app.openMusic();  //静音
					mTopNavLayout.setRightSecondImgSrc(R.drawable.ic_volume_float);
				}
			}});
		
		listView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				for(int i = 0; i < musicList.size(); i++){
					if(position == i)
						musicList.get(position).setIsChoose(!musicList.get(position).getIsChoose());
					else
						musicList.get(i).setIsChoose(false);
				}
				adapter.notifyDataSetChanged();
				spInfo.setIOSMusic(false);
			}});
	}
	
	/**
	 * 获取SD卡音频文件列表
	 */
	private void getMusicFile(){
		try{
			ContentResolver cr = this.getContentResolver();
			String[] projectionMedia = { Media._ID, Media.DATA, Media.TITLE, Media.ARTIST, Media.ALBUM_ID};
	    	Cursor cursorMedia = cr.query(Media.EXTERNAL_CONTENT_URI, projectionMedia,
	    			null, null, Media.TITLE + " asc");
	    	if(cursorMedia.moveToFirst()){
	    		do{
	    			int audio_id = cursorMedia.getInt(cursorMedia.getColumnIndex(Media._ID));
	    			String path = cursorMedia.getString(cursorMedia.getColumnIndex(Media.DATA));
	        		String title = cursorMedia.getString(cursorMedia.getColumnIndex(Media.TITLE));
	        		String artist = cursorMedia.getString(cursorMedia.getColumnIndex(Media.ARTIST));
	        		int album_id = cursorMedia.getInt(cursorMedia.getColumnIndex(Media.ALBUM_ID));
	        		String audio_path = getAlbumArt(album_id);
				
	        		
	        		showLogD("title :"  + title + "    path : " + path
	        				+ "    artist : " + artist + "    album_id : " + album_id + "    audio_path : " + audio_path) ;
	        		
	        		MusicBean bean = new MusicBean(title,artist,audio_path,path);
	        		if(musicPath.equals(path))
	        			bean.setIsChoose(true);
	        		musicList.add(bean);
	    		}
	    		while (cursorMedia.moveToNext());
	    	}
	    	
	    	cursorMedia.close();
		}
		catch(Exception e) {
			ISOMusicActivity.this.finish();
		}
		
		
	}
	
	/**
	 * 获取专辑封面图片
	 * @param album_id
	 * @return
	 */
	private String getAlbumArt(int album_id){
        if (album_id < 0) {
        	
            return null;
        }
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[] {"album_id"};
        projection = new String[] {"album_art"};
        Cursor cur = this.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)), projection, null, null, null);

        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        cur = null;
            
        return album_art;
    } 

	/**
	 * 获取SD卡音频文件列表线程
	 */
	private class GetFileListAsyncTask extends AsyncTask<Void, Void, Boolean>{
		
		protected Boolean doInBackground(Void... param) {
			getMusicFile();
			
			return null;
		}
		
		protected void onPostExecute(Boolean result) {
			adapter = new SelectMusicAdapter(ISOMusicActivity.this,musicList);
			listView.setAdapter(adapter);
			progressDialog.dismiss();
		}
	}

}
