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
import com.releasy.android.bean.ActionBean;
import com.releasy.android.constants.RoomConstants;
import com.releasy.android.utils.StringUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SelectSceneActionAdapter extends BaseAdapter{

	private Context mContext;
	private List<ActionBean> actionList;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions displayImageOptions;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	
	public SelectSceneActionAdapter(Context mContext, List<ActionBean> actionList){
		this.mContext = mContext;
		this.actionList = actionList;
		initDisplayImageOptions();
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
		return actionList.size();
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_select_scene_action, null);
			holder = new ViewHolder();
			holder.picImg = (ImageView) convertView.findViewById(R.id.picImg);
			holder.nameTxt = (TextView) convertView.findViewById(R.id.nameTxt);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		ActionBean bean = actionList.get(position);
		holder.nameTxt.setText(bean.getActionName());
		
		String picUrl = bean.getActionPicUrl();
		if(bean.getRoomId() == -1){
			holder.picImg.setImageResource(R.drawable.ic_releasy_add_room);
		}
		else{
			if(StringUtils.isBlank(picUrl)){
				holder.picImg.setImageResource(RoomConstants.getRoomPic(bean.getRoomId()));
			}
			else if(picUrl.indexOf("http://") >= 0){
				int drawable = RoomConstants.getRoomPic(bean.getRoomId(),bean.getActionPicUrl());
				if(drawable < 0){
					String urlStart = picUrl.substring(0, picUrl.lastIndexOf(".") + 1);
					String urlEnd = picUrl.substring(picUrl.lastIndexOf("."), picUrl.length());
					String url = urlStart + "_230" + urlEnd;
					
					imageLoader.displayImage(url, holder.picImg
							, displayImageOptions, animateFirstListener);
				}
				else{
					holder.picImg.setImageResource(drawable);
				}
				
			}
			else{
				imageLoader.displayImage("file://" + bean.getActionPicUrl(), holder.picImg
						, displayImageOptions, animateFirstListener);
			}
		}
		
		return convertView;
	}
	
	/*
	 * 控件基类
	 */
	private class ViewHolder {
		ImageView picImg;
		TextView nameTxt;
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
