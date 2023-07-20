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
import com.releasy.android.bean.PicBean;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class SelectPicAdapter extends BaseAdapter{

	private Context mContext;
	private List<PicBean> picList;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions displayImageOptions;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	
	public SelectPicAdapter(Context context, List<PicBean> picList){
		mContext = context;
		this.picList = picList;
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
		return picList.size();
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_select_pic, null);
			holder = new ViewHolder();
			holder.picImg = (ImageView) convertView.findViewById(R.id.picImg);
			holder.checkedLayout = (RelativeLayout) convertView.findViewById(R.id.checkedLayout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		PicBean bean = picList.get(position);
		
		imageLoader.displayImage("file://" + bean.getPicPath(), holder.picImg
				, displayImageOptions, animateFirstListener);
		
		if(bean.getIsChoose())
			holder.checkedLayout.setVisibility(View.VISIBLE);
		else
			holder.checkedLayout.setVisibility(View.GONE);
		
		return convertView;
	}

	/*
	 * 控件基类
	 */
	private class ViewHolder {
		ImageView picImg;
		RelativeLayout checkedLayout;
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
