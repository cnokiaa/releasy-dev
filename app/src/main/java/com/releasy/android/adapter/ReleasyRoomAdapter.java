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
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.releasy.android.R;
import com.releasy.android.bean.RoomBean;
import com.releasy.android.constants.RoomConstants;
import com.releasy.android.utils.StringUtils;
import com.releasy.android.utils.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 放松馆视图Adapter
 * @author Lighting.Z
 *
 */

public class ReleasyRoomAdapter extends BaseAdapter{

	//public static boolean isMusic = false;
	private Context mContext;
	private List<RoomBean> roomList;;
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions displayImageOptions;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	
	public ReleasyRoomAdapter(Context context, List<RoomBean> roomList){
		mContext = context;
		this.roomList = roomList;
		//this.isMusic = isMusic;
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
	              //.displayer(new RoundedBitmapDisplayer(4))//是否设置为圆角，弧度为多少  
	              //.displayer(new FadeInBitmapDisplayer(100))
	              .build();
	}
	
	public int getCount() {
		return roomList.size();
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_releasy_room, null);
			holder = new ViewHolder();
			holder.picImg = (ImageView) convertView.findViewById(R.id.picImg);
			holder.nameTxt = (TextView) convertView.findViewById(R.id.nameTxt);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		RoomBean bean = roomList.get(position);
		if(bean.getRoomId() == RoomConstants.ADD_ROOM)
			holder.nameTxt.setText(mContext.getString(R.string.user_defined));
		else
			holder.nameTxt.setText(bean.getRoomName());
		
		
		//设置图标
		String picUrl = bean.getRoomPic();
		if(bean.getRoomId() == RoomConstants.ADD_ROOM){
			holder.picImg.setImageResource(R.drawable.ic_releasy_add_room);
		}
		else if(bean.getRoomId() == RoomConstants.ACTION_DISTRIBUTION_FOR_M2){
			holder.picImg.setImageResource(R.drawable.zuhe_icon);
		}
		else if(bean.getRoomId() == RoomConstants.ACTION_COUNTENANCE_TYPE){
			holder.picImg.setImageResource(R.drawable.ic_emoji);
		}
		else{
			holder.picImg.setImageResource(RoomConstants.getRoomPic(bean.getRoomId()));
			/*if(StringUtils.isBlank(picUrl)){
				holder.picImg.setImageResource(RoomConstants.getRoomPic(bean.getRoomId()));
			}
			else if(picUrl.indexOf("http://") >= 0){
				int drawable = RoomConstants.getRoomPic(bean.getRoomId(),bean.getRoomPic());
				if(drawable < 0){
					String urlStart = picUrl.substring(0, picUrl.lastIndexOf("."));
					String urlEnd = picUrl.substring(picUrl.lastIndexOf("."), picUrl.length());
					String url = urlStart + "_203" + urlEnd;
					imageLoader.displayImage(url, holder.picImg
							, displayImageOptions, animateFirstListener);
				}
				else{
					holder.picImg.setImageResource(drawable);
				}
				
			}
			else{
				imageLoader.displayImage("file://" + bean.getRoomPic(), holder.picImg
						, displayImageOptions, animateFirstListener);
			}*/
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
