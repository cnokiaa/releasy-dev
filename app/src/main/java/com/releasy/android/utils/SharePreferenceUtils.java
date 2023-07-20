package com.releasy.android.utils;

import com.releasy.android.bean.UserRecordBean;
import com.releasy.android.constants.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharePreferenceUtils {

	SharedPreferences spInfo;
	
	/**
	 * 初始化SharedPreferences
	 */
	public SharePreferenceUtils(Context context){
		spInfo = context.getSharedPreferences("releasy_preferences", 0);
	}
	
	/**
	 * 记住是否第一次打开应用的
	 *//*
	public void setIsFristOpen(boolean isFristOpen){
		spInfo.edit().putBoolean("isFristOpen", isFristOpen).commit();  
	}
	public boolean getIsFristOpen(){
		return spInfo.getBoolean("isFristOpen", true);
	}*/
	
	/**
	 * 记住是否第一次打开应用的
	 */
	public void setIsV_20FristOpen(boolean isFristOpen){
		spInfo.edit().putBoolean("isV_20FristOpen", isFristOpen).commit();  
	}
	public boolean getIsV_20FristOpen(){
		return spInfo.getBoolean("isV_20FristOpen", true);
	}
	
	/**
	 * 记住选择的产品型号
	 */
	public void setSelectDevice(String selectDevice){
		spInfo.edit().putString("selectDevice", selectDevice).commit();  
	}
	public String getSelectDevice(){
		return spInfo.getString("selectDevice", Constants.SELECT_DEVICE_UNKNOWN);
	}
	
	/**
	 * 记住是否加载初始化DB数据
	 *//*
	public void setIsLoadInitDB(boolean isLoadInitDB){
		spInfo.edit().putBoolean("isLoadInitDB", isLoadInitDB).commit();  
	}
	public boolean getIsLoadInitDB(){
		return spInfo.getBoolean("isLoadInitDB", true);
	}*/
	
	/**
	 * 记住是否加载2.0初始化DB数据
	 */
	public void setIsV_20LoadInitDB(boolean isLoadInitDB){
		spInfo.edit().putBoolean("isV20LoadInitDB", isLoadInitDB).commit();  
	}
	public boolean getIsV_20LoadInitDB(){
		return spInfo.getBoolean("isV20LoadInitDB", true);
	}
	
	/**
	 * 记录是否播放音乐
	 */
	public void setIsMusicPlay(boolean isMusicPlay){
		spInfo.edit().putBoolean("isMusicPlay", isMusicPlay).commit();  
	}
	public boolean getIsMusicPlay(){
		return spInfo.getBoolean("isMusicPlay", true);
	}
	
	
	/**
	 * 记住登录用户ID
	 */
	public void setUId(int UId){
		spInfo.edit().putInt("UId", UId).commit();  
	}
	public int getUId(){
		return spInfo.getInt("UId", 10000);
	}
	
	/**
	 * 记住用户电话号码
	 */
	public void setPhoneNum(String phoneNum){
		spInfo.edit().putString("phoneNum", phoneNum).commit();  
	}
	public String getPhoneNum(){
		return spInfo.getString("phoneNum", "");
	}
	
	/**
	 * 记住用户电话号码
	 */
	public void setEmail(String email){
		spInfo.edit().putString("email", email).commit();  
	}
	public String getEmail(){
		return spInfo.getString("email", "");
	}
	
	/**
	 * 记住用户性别
	 */
	public void setUserSex(String sex){
		spInfo.edit().putString("sex", sex).commit();  
	}
	public String getUserSex(){
		return spInfo.getString("sex", "");
	}
	
	/**
	 * 记住用户年龄
	 */
	public void setUserAge(String age){
		spInfo.edit().putString("age", age).commit();  
	}
	public String getUserAge(){
		return spInfo.getString("age", "");
	}
	
	/**
	 * 记住用户生日
	 */
	public void setUserBirthday(String birthday){
		spInfo.edit().putString("birthday", birthday).commit();  
	}
	public String getUserBirthday(){
		return spInfo.getString("birthday", "");
	}
	
	/**
	 * 记住用户身高
	 */
	public void setUserHeight(int height){
		spInfo.edit().putInt("height", height).commit();  
	}
	public int getUserHeight(){
		return spInfo.getInt("height", 0);
	}
	
	/**
	 * 记住用户体重
	 */
	public void setUserWeight(int weight){
		spInfo.edit().putInt("weight", weight).commit();  
	}
	public int getUserWeight(){
		return spInfo.getInt("weight", 0);
	}
	
	
	/**
	 * 放松馆版本号
	 */
	public void setReleasyVersion(String releasyVersion){
		spInfo.edit().putString("releasyVersion", releasyVersion).commit();  
	}
	public String getReleasyVersion(){
		return spInfo.getString("releasyVersion", "1.0.0");
	}
	
	
	/**
	 * 是否有反馈通知
	 */
	public void setHasFeedbackNotify(boolean hasFeedbackNotify){
		spInfo.edit().putBoolean("hasFeedbackNotify", hasFeedbackNotify).commit();  
	}
	public boolean getHasFeedbackNotify(){
		return spInfo.getBoolean("hasFeedbackNotify", false);
	}
	
	/**
	 * 反馈通知数
	 */
	public void setNotificationCount(int notificationCount){
		spInfo.edit().putInt("notificationCount", notificationCount).commit();  
	}
	public int getNotificationCount(){
		return spInfo.getInt("notificationCount", 1);
	}
	
	/**
	 * 操作记录
	 */
	public void setLogout(String logout){
		spInfo.edit().putString("logout", logout).commit();  
	}
	public String getLogout(){
		return spInfo.getString("logout", "");
	}
	
	/**
	 * 定位
	 */
	public void setLocationCursor(boolean locationCursor){
		spInfo.edit().putBoolean("locationCursor", locationCursor).commit();  
	}
	public boolean getLocationCursor(){
		return spInfo.getBoolean("locationCursor", true);
	}
	
	/**
	 * SD卡
	 */
	public void setMusicCursor(boolean musicCursor){
		spInfo.edit().putBoolean("musicCursor", musicCursor).commit();  
	}
	public boolean getMusicCursor(){
		return spInfo.getBoolean("musicCursor", true);
	}
	
	/**
	 * 音乐提示
	 */
	public void setIOSMusic(boolean iosmusicToast){
		spInfo.edit().putBoolean("iosmusicToast", iosmusicToast).commit();  
	}
	public boolean getIOSMusic(){
		return spInfo.getBoolean("iosmusicToast", true);
	}
	
	/**
	 * 使用记录
	 */
	public void setUserRecord(UserRecordBean bean){
		Utils.showLogD("setUserRecord ----- userRecordData : " + bean.getDate());
		Utils.showLogD("setUserRecord ----- userRecordRumTime : " + bean.getTotalRunTime());
		Utils.showLogD("setUserRecord ----- userRecordActionRumTime : " + bean.getActinRunTimeRecord());
		
		spInfo.edit().putString("userRecordData", bean.getDate()).commit();  
		spInfo.edit().putInt("userRecordRumTime", bean.getTotalRunTime()).commit();  
		spInfo.edit().putString("userRecordActionRumTime", bean.getActinRunTimeRecord()).commit();  
	}
	public String getUserRecordData(){
		return spInfo.getString("userRecordData", "");
	}
	public int getUserRecordRumTime(){
		return spInfo.getInt("userRecordRumTime", 0);
	}
	public String getUserRecordActionRumTime(){
		return spInfo.getString("userRecordActionRumTime", "");
	}
}
