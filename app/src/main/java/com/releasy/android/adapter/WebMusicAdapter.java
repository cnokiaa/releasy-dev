package com.releasy.android.adapter;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.util.EntityUtils;

import com.loopj.android.image.SmartImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.releasy.android.R;
import com.releasy.android.bean.MusicBean;
import com.releasy.android.constants.Constants;
import com.releasy.android.download.DownloadTask;
import com.releasy.android.utils.StringUtils;
import com.releasy.android.view.ProgressWheel;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WebMusicAdapter extends BaseAdapter{

	private Context mContext;
	private List<MusicBean> musicList;
	private int roomId;
	
	public WebMusicAdapter(Context context, List<MusicBean> musicList, int roomId){
		mContext = context;
		this.musicList = musicList;
		this.roomId = roomId;
	}
	
	public int getCount() {
		return musicList.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_web_music, null);
			holder = new ViewHolder();
			holder.picImg = (SmartImageView) convertView.findViewById(R.id.picImg);
			holder.nameTxt = (TextView) convertView.findViewById(R.id.nameTxt);
			holder.artistTxt = (TextView) convertView.findViewById(R.id.artistTxt);
			holder.downloadImg = (ImageView) convertView.findViewById(R.id.downloadImg);
			holder.progressLayout = (RelativeLayout) convertView.findViewById(R.id.progressLayout);
			holder.progressBar = (ProgressWheel) convertView.findViewById(R.id.progressBar);
			holder.progressTxt = (TextView) convertView.findViewById(R.id.progressTxt);
			holder.downloadTxt = (TextView) convertView.findViewById(R.id.downloadTxt);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		MusicBean music = musicList.get(position);
		holder.nameTxt.setText(music.getName());
		holder.artistTxt.setText(music.getArtist());
		
		String artPath =  music.getArtPath();
		holder.picImg.setTag(artPath);
		
		if(!StringUtils.isBlank(artPath)){
			holder.picImg.setBackgroundResource(R.drawable.ic_acquiesce_img);
			// 通过 tag 来防止图片错位
			/*if (holder.picImg.getTag() != null && holder.picImg.getTag().equals(artPath)) {
				holder.picImg.setImageUrl(pathToUTF8(artPath, music.getName()));
			}*/
			
			holder.picImg.setImageUrl(pathToUTF8(artPath));
		}
		else{
			holder.picImg.setBackground(null);
			holder.picImg.setImageResource(R.drawable.ic_acquiesce_img);
		}
		
		if(music.getDownloadStatus() == 0){
			holder.downloadImg.setVisibility(View.VISIBLE);
			holder.progressLayout.setVisibility(View.GONE);
			holder.downloadTxt.setVisibility(View.GONE);
			holder.downloadImg.setOnClickListener(new DownloadListener(position));
		}
		else if(music.getDownloadStatus() == 1){
			holder.downloadImg.setVisibility(View.GONE);
			holder.progressLayout.setVisibility(View.VISIBLE);
			holder.downloadTxt.setVisibility(View.GONE);
			holder.progressTxt.setText(music.getProgress() + "%");
			int progress = (int) ((float)music.getProgress() * (float)360 / (float)100);
			holder.progressBar.setProgress(progress);
		}
		else{
			holder.downloadImg.setVisibility(View.GONE);
			holder.progressLayout.setVisibility(View.GONE);
			holder.downloadTxt.setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}

	/*
	 * 控件基类
	 */
	private class ViewHolder {
		SmartImageView picImg;
		TextView nameTxt;
		TextView artistTxt;
		ImageView downloadImg;
		RelativeLayout progressLayout;
		ProgressWheel progressBar;
		TextView progressTxt;
		TextView downloadTxt;
	}
	
	private String pathToUTF8(String artPath){
		String utf8Str = null;
		String musicName;
		if(StringUtils.isBlank(artPath))
			return utf8Str;
		
		int start = artPath.lastIndexOf("/") + 1;
		musicName = artPath.substring(start, artPath.length());
		try {
			String musicNameUTF8 = URLEncoder.encode(musicName, "utf-8");
			musicNameUTF8 = musicNameUTF8.replace("+","%20");
			if(musicName.length() == 1)
				utf8Str = artPath.replace(musicName, musicNameUTF8);
			else
				utf8Str = artPath.replaceAll(musicName, musicNameUTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return utf8Str;
	}
	
	/*
	 * 异步下载方法
	 */
	private class DownloadListener implements OnClickListener{
		private int position ;
		private DownloadListener(int pos){
			position = pos;}
		public void onClick(View arg0) {
			//Log.d("z17m","DownloadListener : " + position);
			String path = musicList.get(position).getFileUrl();
			String filename = path.substring(path.lastIndexOf('/') + 1);

			try {
				// URL编码（这里是为了将中文进行URL编码）
				filename = URLEncoder.encode(filename, "UTF-8");
				filename = filename.replace("+","%20");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			path = path.substring(0, path.lastIndexOf("/") + 1) + filename;
			Log.d("z17m","path : " + path);
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				// File savDir =
				// Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
				// 保存路径
				File savDir = new File(Constants.MUSIC);
				download(path, savDir, musicList.get(position), musicList.get(position).getFileUrl());
				musicList.get(position).setProgress(0);
				musicList.get(position).setDownloadStatus(1);
				WebMusicAdapter.this.notifyDataSetChanged();
			} else {
				Toast.makeText(mContext,R.string.sdcard_error, Toast.LENGTH_LONG).show();
			}
			
		}
	}
	
	private void download(String path, File savDir, MusicBean music, String downloadFileName) {
		DownloadTask task = new DownloadTask(mContext, path, savDir, music, roomId, downloadFileName);
		new Thread(task).start();
		//Log.d("z17m","download musicId : " + musicId);
	}

}
