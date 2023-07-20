package com.releasy.android.utils;

import com.releasy.android.ReleasyApplication;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;

public class CountdownTimerUtils extends CountDownTimer{

	public final static String COUNTDOWN_TIMER_FINISH = "com.releasy.android.COUNTDOWN_TIMER_FINISH";
	public final static String COUNTDOWN_TIMER_TICK = "com.releasy.android.COUNTDOWN_TIMER_TICK";
	private Context context;
	private long totalTime;      //总按摩时间
	private long overplusTime;   //剩余按摩时间
	
	public CountdownTimerUtils(Context context, long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
		this.context = context;
		totalTime = millisInFuture;
		overplusTime = millisInFuture;
	}

	public void onFinish() {
		Intent intent = new Intent(COUNTDOWN_TIMER_FINISH);
		ReleasyApplication app = (ReleasyApplication)context.getApplicationContext();
		context.sendBroadcast(intent);
		app.CountdownTimerUtilsStop();
		//app.cleanWorkingInfo();
		
		if(app.getHasBackstageNotify())
			app.updataBackstageNotificition();

	}


	public void onTick(long millisUntilFinished) {
		Intent intent = new Intent(COUNTDOWN_TIMER_TICK);
		intent.putExtra("millisUntilFinished", millisUntilFinished);
		overplusTime = millisUntilFinished;
		//Log.d("z17m", "millisUntilFinished : " + millisUntilFinished);
		context.sendBroadcast(intent);
	}
	
	public long getOverplusTime(){
		return overplusTime;
	}

	public long getRuntime(){
		return totalTime - overplusTime;
	} 
}
