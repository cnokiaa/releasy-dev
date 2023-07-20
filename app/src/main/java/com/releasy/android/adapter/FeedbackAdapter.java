package com.releasy.android.adapter;

import java.util.List;
import com.releasy.android.R;
import com.releasy.android.bean.FeedbackBean;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FeedbackAdapter extends BaseAdapter{

	public static interface IMsgViewType
	{
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}
	
    private List<FeedbackBean> coll;
    private LayoutInflater mInflater;
    
    public FeedbackAdapter(Context context, List<FeedbackBean> coll) {
        this.coll = coll;
        mInflater = LayoutInflater.from(context);
    }

    public void setDataList(List<FeedbackBean> coll){
    	this.coll = coll;
    }

    public int getCount() {
        return coll.size();
    }

    public Object getItem(int position) {
        return coll.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
	public int getItemViewType(int position) {
		FeedbackBean entity = coll.get(position);
	 	
	 	if (entity.getMsgType() == 0)
	 	{
	 		return IMsgViewType.IMVT_COM_MSG;
	 	}else{
	 		return IMsgViewType.IMVT_TO_MSG;
	 	}
	 	
	}

	public int getViewTypeCount() {
		return 2;
	}
	
    public View getView(int position, View convertView, ViewGroup parent) {
    	FeedbackBean entity = coll.get(coll.size()- 1 - position);
    	int isComMsg = entity.getMsgType();
    		
    	ViewHolder viewHolder = null;	
	    if (convertView == null)
	    {
	    	  if (isComMsg == 0)
			  {
				  convertView = mInflater.inflate(R.layout.item_feedback_left, null);
			  }else{
				  convertView = mInflater.inflate(R.layout.item_feedback_right, null);
			  }

	    	  viewHolder = new ViewHolder();
			  viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
			  viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
			  viewHolder.isComMsg = isComMsg;
			  
			  convertView.setTag(viewHolder);
	    }else{
	        viewHolder = (ViewHolder) convertView.getTag();
	    }
	
	    viewHolder.tvSendTime.setText(entity.getDate());
	    viewHolder.tvContent.setText(entity.getMsg());

	    /*if(position > 0){
	    	FeedbackBean ce = coll.get(position-1);
	    	String date1 = ce.getDate();
	    	String ydate1 = date1.substring(0, 10);
	    	String date2 = entity.getDate();
	    	String ydate2 = date2.substring(0, 10);
	    	
	    	if(ydate2.equals(ydate1)){
	    		viewHolder.tvSendTime.setVisibility(View.GONE);
	    	}
	    	else{
	    		viewHolder.tvSendTime.setVisibility(View.VISIBLE);
	    	}
	    	viewHolder.tvSendTime.setText(date2);
	    }
	    else{
	    	String date = entity.getDate();
	    	String ydate = date.substring(0, 10);
	    	viewHolder.tvSendTime.setText(date);
	    	viewHolder.tvSendTime.setVisibility(View.VISIBLE);
	    }*/

	    return convertView;
    }

    static class ViewHolder { 
        public TextView tvSendTime;
        public TextView tvContent;
        public int isComMsg = 0;
    }

}
