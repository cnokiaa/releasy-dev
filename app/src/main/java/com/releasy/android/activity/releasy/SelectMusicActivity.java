package com.releasy.android.activity.releasy;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.releasy.android.R;
import com.releasy.android.activity.main.BaseActivity;
import com.releasy.android.adapter.SelectMusicAdapter;
import com.releasy.android.bean.MusicBean;
import com.releasy.android.utils.StringUtils;
import com.releasy.android.view.TopNavLayout;

/**
 * 自定义按摩动作中 选择音乐页面
 * @author Lighting.Z
 *
 */
public class SelectMusicActivity extends BaseActivity{

	private TopNavLayout mTopNavLayout;           //导航菜单栏
	private List<MusicBean> musicList;
	private ListView listView;
	private SelectMusicAdapter adapter;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_select_music);
	        
	    init();    //初始化
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		musicList = new ArrayList<MusicBean>();
		
		initViews();    //初始化控件
		initEvents();   //初始化点击事件
		setTopNav();    //初始化导航栏
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
		mTopNavLayout.setRightTxt(R.string.confirm);
		
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
				SelectMusicActivity.this.finish();
			}});
		
		//导航栏确定按钮逻辑
		mTopNavLayout.setRightTxtOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				MusicBean bean = null;
				for(int i = 0; i <  musicList.size(); i++){
					if(musicList.get(i).getIsChoose())
						bean = musicList.get(i);
				}
				
				if(bean == null){
					Toast.makeText(SelectMusicActivity.this, R.string.no_select_music_toast, Toast.LENGTH_LONG).show();
				}
				else{
					Intent data=new Intent();  
		            data.putExtra("musicPath", bean.getFilePath()); 
		            data.putExtra("musicName", bean.getName());  
		            data.putExtra("musicPic", bean.getArtPath());  
		            data.putExtra("musicArtist", bean.getArtist());  
		            setResult(Activity.RESULT_OK, data);  
		            finish();  
				}
			}});
		
		listView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				for(int i = 0; i < musicList.size(); i++){
					musicList.get(i).setIsChoose(false);
				}
				musicList.get(position).setIsChoose(true);
				adapter.notifyDataSetChanged();
			}});
	}
	
	/**
	 * 获取SD卡音频文件列表
	 */
	private void getMusicFile(){
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
        		musicList.add(bean);
    		}
    		while (cursorMedia.moveToNext());
    	}
    	
    	cursorMedia.close();
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
			adapter = new SelectMusicAdapter(SelectMusicActivity.this,musicList);
			listView.setAdapter(adapter);
			progressDialog.dismiss();
		}
	}
	
}
