package com.releasy.android.download;

import java.io.File;

import com.releasy.android.ReleasyApplication;
import com.releasy.android.bean.MusicBean;
import com.releasy.android.db.MusicDBUtils;
import com.releasy.android.db.ReleasyDatabaseHelper;
import com.releasy.android.utils.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DownloadTask implements Runnable{
	
	public final static String UPDATA_WEB_MUSIC_ADAPTER_UI = "com.releasy.android.UPDATA_WEB_MUSIC_ADAPTER_UI";
	private Context mContext;
	private int musicId;
	private String path;
	private File saveDir;
	private FileDownloader loader;
	private String downloadFileName;
	private String fileSavePath;
	private MusicBean music;
	private int roomId;
	
	public DownloadTask(Context mContext, String path, File saveDir, MusicBean music, int roomId, String downloadFileName) {
		this.mContext = mContext;
		this.path = path;
		this.saveDir = saveDir;
		this.music = music;
		this.downloadFileName = downloadFileName;
		this.roomId = roomId;
		musicId = music.getMusicId();
	}

	/**
	 * 退出下载
	 */
	public void exit() {
		if (loader != null)
			loader.exit();
	}
	
	DownloadProgressListener downloadProgressListener = new DownloadProgressListener() {
		public void onDownloadSize(int size) {
			float num = (float)size*(float)100/(float)loader.getFileSize();
			int result = (int) (num);
			//Log.d("z17m","size : " + size + "     loader.getFileSize() : " + loader.getFileSize());
			Log.d("z17m",musicId + " ---> onDownloadSize : " + result + "%");
			if(StringUtils.isBlank(fileSavePath))
				fileSavePath = loader.getSaveFilePath();
			
			if(result == 100){
				ReleasyDatabaseHelper db = MusicDBUtils.openData(mContext);
				MusicDBUtils.insertData(db, roomId, musicId, music.getName()
						, music.getArtPath(), fileSavePath, music.getArtist());
				
				ReleasyApplication app = (ReleasyApplication) mContext.getApplicationContext();
				if(app.getIsWorking() && roomId == app.getRoomId()){
					app.getMusicService().refreshMusicList(); //刷新后台音乐Service的音乐列表
	        	}
			}
			
			Intent intent = new Intent(UPDATA_WEB_MUSIC_ADAPTER_UI);
			intent.putExtra("musicId", musicId);
			intent.putExtra("result", result);
			intent.putExtra("fileSavePath", fileSavePath);
			mContext.sendBroadcast(intent);
		}
	};

	public void run() {
		try {
			Log.d("z17m","DownloadTask run");
			Log.d("z17m","DownloadTask path : " + path + "    saveDir : " + saveDir);
			// 实例化一个文件下载器
			loader = new FileDownloader(mContext, path,
					saveDir, 1, downloadFileName);
			// 设置进度条最大值
			loader.download(downloadProgressListener);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}

}
