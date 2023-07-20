package com.releasy.android.adapter;

import java.util.List;
import com.releasy.android.R;
import com.releasy.android.bean.RunTimeBean;
import com.releasy.android.constants.ActionConstants;
import com.releasy.android.utils.ActionUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserRecordAdapter extends BaseAdapter{

	private Context mContext;
	private List<RunTimeBean> actinRunTimeList;  //设备数据
	
	public UserRecordAdapter(Context context, List<RunTimeBean> actinRunTimeList){
		mContext = context;
		this.actinRunTimeList = actinRunTimeList;
	}
	
	
	public int getCount() {
		return actinRunTimeList.size();
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_user_record, null);
			holder = new ViewHolder();
			holder.actionIcon = (ImageView) convertView.findViewById(R.id.action_img);
			holder.actionName = (TextView) convertView.findViewById(R.id.action_name);
			holder.runTime = (TextView) convertView.findViewById(R.id.run_time);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		RunTimeBean bean = actinRunTimeList.get(position);
		
		holder.actionIcon.setImageResource(ActionUtils.getActionIcon(bean.getActionId()));
		holder.actionName.setText(ActionConstants.getActionName(bean.getActionId(), mContext));
		
		int m = bean.getRunTime()/60;
		int s = bean.getRunTime()%60;
		holder.runTime.setText(m + "m" + s + "s");
		
		
		return convertView;
	}
	
	/*
	 * 控件基类
	 */
	private class ViewHolder {
		ImageView actionIcon;
		TextView actionName;
		TextView runTime;
	}

}
