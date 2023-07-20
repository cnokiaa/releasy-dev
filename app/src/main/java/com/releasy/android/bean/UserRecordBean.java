package com.releasy.android.bean;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class UserRecordBean {

	private String date;
	private int totalRunTime;
	private List<RunTimeBean> actinRunTimeList;
	
	public void setDate(String date){
		this.date = date;
	}
	
	public String getDate(){
		return date;
	}
	
	public void setTotalRunTime(int totalRunTime){
		this.totalRunTime = totalRunTime;
	}
	
	public int getTotalRunTime(){
		return totalRunTime;
	}
	
	public void setActinRunTimeList(List<RunTimeBean> actinRunTimeList){
		this.actinRunTimeList = actinRunTimeList;
	}
	
	public List<RunTimeBean> getActinRunTimeList(){
		return actinRunTimeList;
	}
	
	public UserRecordBean(){}
	
	public UserRecordBean(String date, int totalRunTime, List<RunTimeBean> actinRunTimeList){
		this.date = date;
		this.totalRunTime = totalRunTime;
		this.actinRunTimeList = actinRunTimeList;
	}
	
	public UserRecordBean(String date){
		this.date = date;
		this.totalRunTime = 0;
		this.actinRunTimeList = new ArrayList<RunTimeBean>();
		
		for(int i = 10001; i <= 10024; i++){
			RunTimeBean bean = new RunTimeBean(i,0);
			actinRunTimeList.add(bean);
		}
	}
	
	public void updata(int acitonId, int runTime){
		totalRunTime = totalRunTime + runTime;
		for(int i = 0; i < actinRunTimeList.size(); i++){
			RunTimeBean bean = actinRunTimeList.get(i);
			if(bean.getActionId() == acitonId){
				bean.setRunTime(bean.getRunTime() + runTime);
			}
		}
	}
	
	public String getActinRunTimeRecord(){
		String actinRunTimeRecord = "";
		
		for(int i = 0; i < actinRunTimeList.size(); i++){
			actinRunTimeRecord = actinRunTimeRecord 
			                     + actinRunTimeList.get(i).getActionId() + ","
			                     + actinRunTimeList.get(i).getRunTime() + ";";
		}
		
		return actinRunTimeRecord;
	}
}
