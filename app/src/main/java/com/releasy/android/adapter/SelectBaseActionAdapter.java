package com.releasy.android.adapter;

import java.util.List;

import com.releasy.android.R;
import com.releasy.android.bean.ActionBean;
import com.releasy.android.bean.RoomBean;
import com.releasy.android.constants.ActionConstants;
import com.releasy.android.constants.Constants;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * 基础动作选择宫格Adapter
 * @author Lighting.Z
 *
 */
public class SelectBaseActionAdapter extends BaseAdapter{

	private Context mContext;
	private List<ActionBean> actionList;
	
	public SelectBaseActionAdapter(Context mContext, List<ActionBean> list){
		this.mContext = mContext;
		this.actionList = list;
		
		for(int i = 0; i < actionList.size(); i++){
			if(actionList.get(i).getActionId() > 10018){
				actionList.remove(i);
				i = i - 1;
			}
		}
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_select_base_action, null);
			holder = new ViewHolder();
			holder.actionRg = (TextView) convertView.findViewById(R.id.actionRg);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		ActionBean bean = actionList.get(position);
				
		holder.actionRg.setCompoundDrawables(null, ActionConstants.getActionBG(mContext,bean.getActionId())
				, null, null); //设置左图标
		holder.actionRg.setText(bean.getActionName());
		
		return convertView;
	}
	
	/*
	 * 控件基类
	 */
	private class ViewHolder {
		TextView actionRg;
	}

}
