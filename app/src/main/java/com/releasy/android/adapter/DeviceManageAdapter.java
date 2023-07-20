package com.releasy.android.adapter;

import java.util.List;

import com.releasy.android.R;
import com.releasy.android.activity.device.DeviceMainFragment;
import com.releasy.android.bean.DeviceBean;
import com.releasy.android.constants.Constants;
import com.releasy.android.db.DeviceDBUtils;
import com.releasy.android.db.ReleasyDatabaseHelper;
import com.releasy.android.utils.StringUtils;
import com.releasy.android.utils.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 设备管理页面 搜索adapter
 * @author Lighting.Z
 *
 */
public class DeviceManageAdapter extends BaseAdapter{

	private Context mContext;
	private List<DeviceBean> deviceList;  //设备数据
	
	public DeviceManageAdapter(Context context, List<DeviceBean> deviceList){
		mContext = context;
		this.deviceList = deviceList;
	}
	
	public int getCount() {
		return deviceList.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	 public int getViewTypeCount() {
         // menu type count
         return 2;
     }

     @Override
     public int getItemViewType(int position) {
         // current menu type
    	 if(deviceList.get(position).getDeviceVersion().equals(Constants.DEVICE_VERSION_M2_B))
    		 return 1;
    	 else
    		 return 0;
     }
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_device_manage, null);
			holder = new ViewHolder();
			holder.deviceIcon = (ImageView) convertView.findViewById(R.id.device_icon);
			holder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
			holder.deviceInfo = (TextView) convertView.findViewById(R.id.device_info);
			holder.batteryTxt = (TextView) convertView.findViewById(R.id.batteryTxt);
			holder.batteryImg = (ImageView) convertView.findViewById(R.id.batteryImg);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		DeviceBean bean = deviceList.get(position);
		
		Log.d("z17m","bean.getDeviceVersion() = " + bean.getDeviceVersion());
		
		if(bean.getDeviceVersion().equals(Constants.DEVICE_VERSION_M2_A))
			holder.deviceIcon.setImageResource(R.drawable.ic_mooyee_m2m);
		else if(bean.getDeviceVersion().equals(Constants.DEVICE_VERSION_M2_B))
			holder.deviceIcon.setImageResource(R.drawable.ic_mooyee_m2s);
		else
			holder.deviceIcon.setImageResource(R.drawable.ic_mooyee_m1);
		holder.deviceName.setText(bean.getName());
		holder.deviceName.setOnClickListener(new RenameListener(position));
		
		String address = mContext.getResources().getString(R.string.address);
		holder.deviceInfo.setText(address + bean.getAddress());
		if(bean.getPower() >= 0)
			holder.batteryTxt.setText(bean.getPower()+"%");
		else
			holder.batteryTxt.setText(R.string.unknow);
		holder.batteryImg.setImageResource(setBatteryImg(bean.getPower()));
		
		return convertView;
	}
	
	/*
	 * 控件基类
	 */
	private class ViewHolder {
		ImageView deviceIcon;
		TextView deviceName;
		TextView deviceInfo;
		TextView batteryTxt;
		ImageView batteryImg;
	}
	
	private int setBatteryImg(int power){
		int img = R.drawable.ic_battery_81_100;
		if(power<=100&&power>80)
			img = R.drawable.ic_battery_81_100;
		else if(power<=80&&power>60)
			img = R.drawable.ic_battery_61_80;
		else if(power<=60&&power>40)
			img = R.drawable.ic_battery_41_60;
		else if(power<=40&&power>20)
			img = R.drawable.ic_battery_21_40;
		else if(power<=20&&power>10)
			img = R.drawable.ic_battery_11_20;
		else if(power<=10&&power>=0)
			img = R.drawable.ic_battery_0_10;
		else
			img = R.drawable.ic_battery_unknown;
		return img;
	}
	
	private class RenameListener implements OnClickListener{
		private int position ;
		private RenameListener(int pos){
			position = pos;}
		
		public void onClick(View arg0) {
			final DeviceBean device = deviceList.get(position);
			
			final EditText edittext = new EditText(mContext);
			edittext.setText(device.getName());
			int txtSize = (int) mContext.getResources().getDimension(R.dimen.rename_edit_txt_size);
			edittext.setTextSize(txtSize);
		    new AlertDialog.Builder(mContext).setTitle(R.string.modify_the_note_name)
		                   .setView(edittext)
		                   .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
		                	   public void onClick(DialogInterface arg0, int arg1) {
		                		   String rename = edittext.getText().toString();
		                		   if(StringUtils.isBlank(rename)){
		                			   Toast.makeText(mContext, R.string.please_fill_in_the_note_name, Toast.LENGTH_LONG).show();
		                			   return;
		                		   }
		                		   
		                		   if(rename.length() > 8){
		                			   Toast.makeText(mContext, R.string.note_name_length_not_more_than_8, Toast.LENGTH_LONG).show();
		                			   return;
		                		   }
		                		   
		                		   if(rename.equals(device.getName())){
		                			   return;
		                		   }
		                		   
		                		   ReleasyDatabaseHelper db = DeviceDBUtils.openData(mContext);
		                		   DeviceDBUtils.UpdataName(db,deviceList.get(position).getAddress(),edittext.getText().toString());
		                		   deviceList.get(position).setName(rename);
		                		   DeviceManageAdapter.this.notifyDataSetChanged();
		                	   }})
		                   .setNegativeButton(R.string.cancel, null)
		                   .show();  
		}
	}

	
}

