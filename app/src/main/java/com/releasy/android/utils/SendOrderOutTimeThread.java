package com.releasy.android.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SendOrderOutTimeThread extends Thread{
	
	public Activity activity;
	public Handler outTimeHandler;
	
	public SendOrderOutTimeThread(Activity activity, Handler outTimeHandler){
		this.activity = activity;
		this.outTimeHandler = outTimeHandler;
	}
	
	public void run() {
		if(activity.isFinishing())
			return;
		
		Log.d("z17m", "OutTimeThread........");
		Message msg = new Message();
		outTimeHandler.sendMessage(msg); // 向Handler发送消息,更新UI
	}
}
