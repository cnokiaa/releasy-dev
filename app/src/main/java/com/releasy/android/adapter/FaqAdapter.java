package com.releasy.android.adapter;

import java.util.List;

import com.releasy.android.R;
import com.releasy.android.bean.FaqBean;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class FaqAdapter extends BaseExpandableListAdapter{

	private Context context;
	private List<FaqBean> faqList;
	
	public FaqAdapter(Context context, List<FaqBean> faqList){
		this.context = context;
		this.faqList = faqList;
	}
	
	public Object getChild(int groupPosition, int childPosition) {
		return faqList.get(groupPosition).getAnswer();   //获取父类下面的每一个子类项
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;  //子类位置
	}

	
	public View getChildView(int groupPosition, int childPosition,boolean isLastChild
			, View convertView, ViewGroup parent) {
		ChildViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_faq_child, null);
			holder = new ChildViewHolder();
			holder.titleTxt = (TextView) convertView.findViewById(R.id.answerTxt);
			convertView.setTag(holder);
		} else {
			holder = (ChildViewHolder) convertView.getTag();
		}
		
		FaqBean bean = faqList.get(groupPosition);
		holder.titleTxt.setText(bean.getAnswer());
		
		return convertView;
	}

	/*
	 * 控件基类
	 */
	private class ChildViewHolder {
		TextView titleTxt;
	}
	
	public int getChildrenCount(int arg0) {
		return 1;
	}

	public Object getGroup(int groupPosition) {
		return faqList.get(groupPosition);
	}

	public int getGroupCount() {
		return faqList.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
		GroupViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_faq_group, null);
			holder = new GroupViewHolder();
			holder.titleTxt = (TextView) convertView.findViewById(R.id.questionTxt);
			convertView.setTag(holder);
		} else {
			holder = (GroupViewHolder) convertView.getTag();
		}
		
		holder.titleTxt.setText(faqList.get(groupPosition).getQuestion());
		
		return convertView;
	}

	/*
	 * 控件基类
	 */
	private class GroupViewHolder {
		TextView titleTxt;
	}
	
	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) { //点击子类触发事件
		return true;
	}

}
