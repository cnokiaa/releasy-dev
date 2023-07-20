package com.releasy.android.adapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.loopj.android.image.SmartImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.releasy.android.R;
import com.releasy.android.bean.MusicBean;
import com.releasy.android.utils.StringUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LocalMusicAdapter extends BaseAdapter{

	private Context mContext;
	private List<MusicBean> musicList;
	
	public LocalMusicAdapter(Context context, List<MusicBean> musicList){
		mContext = context;
		this.musicList = musicList;
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_local_music, null);
			holder = new ViewHolder();
			holder.picImg = (SmartImageView) convertView.findViewById(R.id.picImg);
			holder.nameTxt = (TextView) convertView.findViewById(R.id.nameTxt);
			holder.artistTxt = (TextView) convertView.findViewById(R.id.artistTxt);
			holder.playerImg = (ImageView) convertView.findViewById(R.id.playerImg);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		MusicBean music = musicList.get(position);
		holder.nameTxt.setText(music.getName());
		holder.artistTxt.setText(music.getArtist());
		
		String artPath =  music.getArtPath();
		if(!StringUtils.isBlank(artPath)){
			holder.picImg.setImageUrl(pathToUTF8(artPath));
		}
		else{
			holder.picImg.setImageResource(R.drawable.ic_acquiesce_img);
		}
		
		if(music.getIsChoose())
			holder.playerImg.setVisibility(View.VISIBLE);
		else
			holder.playerImg.setVisibility(View.GONE);
		
		return convertView;
	}

	/*
	 * 控件基类
	 */
	private class ViewHolder {
		SmartImageView picImg;
		TextView nameTxt;
		TextView artistTxt;
		ImageView playerImg;
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
}
