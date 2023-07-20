package com.releasy.android.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.releasy.android.R;
import com.releasy.android.ReleasyApplication;
import com.releasy.android.activity.main.MainTabActivity;
import com.releasy.android.adapter.WebMusicAdapter;
import com.releasy.android.constants.Constants;
import com.releasy.android.download.DownloadProgressListener;
import com.releasy.android.download.DownloadTask;
import com.releasy.android.download.FileDownloader;
import com.releasy.android.utils.StringUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class UpdataAppService extends Service{

//	private NotificationManager notifyManage;
//	private Notification notify;
//	private RemoteViews contentView;
	private ReleasyApplication app;               //Application

	public IBinder onBind(Intent arg0) {
		Log.d("z17m", "UpdataAppService onBind !");
		return null;
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("z17m", "UpdataAppService onStartCommand !");
		app = (ReleasyApplication) this.getApplication();
		initNotificition();
		toDownload();
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void initNotificition(){
//		notifyManage = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//		notify = new Notification();
//		notify.flags = Notification.FLAG_AUTO_CANCEL;
//
//		contentView = new RemoteViews(getPackageName(), R.layout.layout_updata_app_notification);
//		contentView.setProgressBar(R.id.notifyProgressBar, 100, 0, false);
//		contentView.setTextViewText(R.id.notifyProgressTxt, "已下载: " + 0 + "%");
//
//        notify.contentView = contentView;
//        notify.icon = R.drawable.icon_notify;
//        notifyManage.notify(10000, notify);
		
	}
	
	private void toDownload(){
		String path = app.getAppDownloadUrl();
		if(StringUtils.isBlank(path)){
			return;
		}
		
		String filename = path.substring(path.lastIndexOf('/') + 1);
		try {
			// URL编码（这里是为了将中文进行URL编码）
			filename = URLEncoder.encode(filename, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		path = path.substring(0, path.lastIndexOf("/") + 1) + filename;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// File savDir =
			// Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
			// 保存路径
			File savDir = new File(Constants.UPDATA);
			download(path, savDir, app.getAppDownloadUrl());
		} else {
			Toast.makeText(this,R.string.sdcard_error, Toast.LENGTH_LONG).show();
		}
	}
	
	private void download(String path, File savDir, String downloadFileName) {
		DownloadUpdataAppTask task = new DownloadUpdataAppTask(path, savDir, downloadFileName);
		new Thread(task).start();
	}
	
	public class DownloadUpdataAppTask implements Runnable{
		
		private String path;
		private File saveDir;
		private FileDownloader loader;
		private String downloadFileName;
		
		public DownloadUpdataAppTask(String path, File saveDir, String downloadFileName) {
			this.path = path;
			this.saveDir = saveDir;
			this.downloadFileName = downloadFileName;
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
//				contentView.setProgressBar(R.id.notifyProgressBar, 100, result, false);
//				contentView.setTextViewText(R.id.notifyProgressTxt, "已下载: " + result + "%");
//				notify.contentView = contentView;
//		        notifyManage.notify(10000, notify);

		        if(result == 100){
//		        	notifyManage.cancel(10000);
		        	 Intent intent = new Intent(); 
		             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		             intent.setAction(Intent.ACTION_VIEW);
		             intent.setDataAndType(Uri.fromFile(new File(loader.getSaveFilePath())), 
		                             "application/vnd.android.package-archive"); 
		             startActivity(intent);  
		        }
			}
		};

		public void run() {
			try {
				Log.d("z17m","DownloadTask run");
				Log.d("z17m","DownloadTask path : " + path + "    saveDir : " + saveDir);
				// 实例化一个文件下载器
				loader = new FileDownloader(UpdataAppService.this, path,
						saveDir, 1, downloadFileName);
				// 设置进度条最大值
				loader.download(downloadProgressListener);
				
			} catch (Exception e) {
				e.printStackTrace();
				
			}
		}

	}
}
