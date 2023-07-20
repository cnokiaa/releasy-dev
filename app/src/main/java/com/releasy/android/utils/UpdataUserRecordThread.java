package com.releasy.android.utils;


import com.releasy.android.bean.UserRecordBean;
import com.releasy.android.db.ReleasyDatabaseHelper;
import com.releasy.android.db.UserRecordDBUtils;
import android.content.Context;


public class UpdataUserRecordThread extends Thread{

	private Context context;
	private String date;
	private int totalRunTime;
	private String actionRunRecord;
	
	public UpdataUserRecordThread(Context context, String date, int totalRunTime, String actionRunRecord){
		this.context = context;
		this.date = date;
		this.totalRunTime = totalRunTime;
		this.actionRunRecord = actionRunRecord;
	}
	
	public void run() {
		Utils.showLogD(" UpdataUserRecordThread start");
		ReleasyDatabaseHelper db = UserRecordDBUtils.openData(context);
		UserRecordDBUtils.insertData(db, date, totalRunTime, actionRunRecord);
		SharePreferenceUtils spInfo = new SharePreferenceUtils(context); 
		spInfo.setUserRecord(new UserRecordBean(Utils.getTime3()));
	}
}
