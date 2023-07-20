package com.releasy.android.adapter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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

public class SelectMusicAdapter extends BaseAdapter {

	private Context mContext;
	private List<MusicBean> musicList;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions displayImageOptions;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	
	public SelectMusicAdapter(Context context, List<MusicBean> musicList){
		mContext = context;
		this.musicList = musicList;
		initDisplayImageOptions(); //初始化DisplayImageOptions
	}
	
	/*
	 * 初始化DisplayImageOptions
	 * DisplayImageOptions是异步加载图片的参数
	 */
	private void initDisplayImageOptions(){
		displayImageOptions = new DisplayImageOptions.Builder()
		          .showStubImage(R.drawable.ic_acquiesce_img)
		          .showImageForEmptyUri(R.drawable.ic_acquiesce_img)
	              .showImageOnFail(R.drawable.ic_acquiesce_img)
	              .resetViewBeforeLoading(true)
	              .cacheOnDisc(true)
	              .cacheInMemory(true)
	              .imageScaleType(ImageScaleType.EXACTLY)
	              .bitmapConfig(Bitmap.Config.RGB_565)
	              .displayer(new FadeInBitmapDisplayer(300))
	              .build();
	}
	
	public int getCount() {
		return musicList.size();
	}
	
	public Object getItem(int position) {
		return null;
	}
	
	public long getItemId(int position) {
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_select_music, null);
			holder = new ViewHolder();
			holder.picImg = (ImageView) convertView.findViewById(R.id.picImg);
			holder.nameTxt = (TextView) convertView.findViewById(R.id.nameTxt);
			holder.artistTxt = (TextView) convertView.findViewById(R.id.artistTxt);
			holder.checkedImg = (ImageView) convertView.findViewById(R.id.checkedImg);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		MusicBean bean = musicList.get(position);
		String artPath =  bean.getArtPath();
		if(!StringUtils.isBlank(artPath)){
			imageLoader.displayImage("file://" + artPath, holder.picImg
					, displayImageOptions, animateFirstListener);			
		}
		else{
			holder.picImg.setImageResource(R.drawable.ic_acquiesce_img);
		}
		
		holder.nameTxt.setText(bean.getName());
		holder.artistTxt.setText(bean.getArtist());
		
		if(bean.getIsChoose())
			holder.checkedImg.setVisibility(View.VISIBLE);
		else 
			holder.checkedImg.setVisibility(View.GONE);
		
		return convertView;
	}

	/*
	 * 控件基类
	 */
	private class ViewHolder {
		ImageView picImg;
		TextView nameTxt;
		TextView artistTxt;
		ImageView checkedImg;
	}
	
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
}
