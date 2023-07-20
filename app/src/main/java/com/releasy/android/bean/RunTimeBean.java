package com.releasy.android.bean;

public class RunTimeBean {

	private int actionId ;
	private int runTime;
	
	public void setActionId(int actionId){
		this.actionId = actionId;
	}
	
	public int getActionId(){
		return actionId;
	}
	
	public void setRunTime(int runTime){
		this.runTime = runTime;
	}
	
	public int getRunTime(){
		return runTime;
	}
	
	public RunTimeBean(){}
	
	public RunTimeBean(int actionId, int runTime){
		this.actionId = actionId;
		this.runTime = runTime;
	}
}
