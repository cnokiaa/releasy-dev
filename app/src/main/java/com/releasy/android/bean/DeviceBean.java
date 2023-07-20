package com.releasy.android.bean;

/**
 * 按摩器设备基类
 * @author Lighting.Z
 *
 */
public class DeviceBean {
	
	//显示数据
	private String name = "";               //设备名称
	private String address = "";            //设备地址
	private String uuid = "";               //设备唯一标示
	private int verifyStatus = 0;           //验证标示  0为未验证   1为已通过  2为未通过
	private int deviceHardwareVersion = 0;  //设备硬件版本 0为默认值 
	private int deviceSoftwareVersion = 0;  //设备软件版本 0为默认值 
	private String broadcastName = "";      //广播名称
	private String deviceVersion ="";       //设备型号  M1、 M2_A、 M2_B
	
	private boolean isCheck = false;        //是否选中（用于搜索）
	private boolean isLoad = true;          //是否有负载
	private int power = -1;                 //设备电量
	
	private String modeName;                //按摩模式名称
	private int mode;                       //按摩模式
	//private int strength = 1;             //按摩强度
	
	//通信参数
	private ActionBean actionBean;          //按摩动作
	
	private boolean connectStatus = false;  //是否连接上
	
	
	/**********************************显示数据设置*********************************/
	
	/**
	 * 设置设备名称
	 */
	public void setName(String name){
		this.name = name;
	}
	/**
	 * 获取设备名称
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * 设置设备地址
	 */
	public void setAddress(String address){
		this.address = address;
	}
	/**
	 * 获取设备地址
	 */
	public String getAddress(){
		return address;
	}
	
	/**
	 * 设置设备唯一标示
	 */
	public void setUuid(String uuid){
		this.uuid = uuid;
	}
	/**
	 * 获取设备唯一标示
	 */
	public String getUuid(){
		return uuid;
	}
	
	/**
	 * 设置设备验证状态
	 */
	public void setVerifyStatus(int verifyStatus){
		this.verifyStatus = verifyStatus;
	}
	/**
	 * 获取设备唯一标示
	 */
	public int getVerifyStatus(){
		return verifyStatus;
	}
	
	/**
	 * 设置设备硬件版本号
	 */
	public void setDeviceHardwareVersion(int deviceHardwareVersion){
		this.deviceHardwareVersion = deviceHardwareVersion;
	}
	/**
	 * 获取设备硬件版本号
	 */
	public int getDeviceHardwareVersion(){
		return deviceHardwareVersion;
	}
	
	/**
	 * 设置设备软件版本号
	 */
	public void setDeviceSoftwareVersion(int deviceSoftwareVersion){
		this.deviceSoftwareVersion = deviceSoftwareVersion;
	}
	/**
	 * 获取设备软件版本号
	 */
	public int getDeviceSoftwareVersion(){
		return deviceSoftwareVersion;
	}
	
	
	/**
	 * 设置设备广播名称
	 */
	public void setDevicebroadcastName(String broadcastName){
		this.broadcastName = broadcastName;
	}
	/**
	 * 获取设备广播名称
	 */
	public String getDevicebroadcastName(){
		return broadcastName;
	}
	
	/**
	 * 设置设备型号
	 */
	public void setDeviceVersion(String deviceVersion){
		this.deviceVersion = deviceVersion;
	}
	/**
	 * 获取设备型号
	 */
	public String getDeviceVersion(){
		return deviceVersion;
	}
	
	
	public void setIsCheck(boolean isCheck){
		this.isCheck = isCheck;
	}
	public boolean getIsCheck(){
		return isCheck;
	}
	
	/**
	 * 设置设备负载
	 */
	public void setIsLoad(boolean isLoad){
		this.isLoad = isLoad;
	}
	/**
	 * 获取设备负载
	 */
	public boolean getIsLoad(){
		return isLoad;
	}
	
	public void setPower(int power){
		this.power = power;
	}
	public int getPower(){
		return power;
	}
	
	/**
	 * 设置设备按摩模式
	 */
	public void setMode(int mode){
		this.mode = mode;
	}
	/**
	 * 获取设备按摩模式
	 */
	public int getMode(){
		return mode;
	}
	
	/**
	 * 设置设备按摩模式名称
	 */
	public void setModeName(String modeName){
		this.modeName = modeName;
	}
	/**
	 * 获取设备按摩模式名称
	 */
	public String getModeName(){
		return modeName;
	}
	
	/**
	 * 设置设备按摩力度
	 *//*
	public void setStrength(int strength){
		this.strength = strength;
	}
	*//**
	 * 获取设备按摩力度
	 *//*
	public int getStrength(){
		return strength;
	}*/
	
	/**
	 * 设置动作基类
	 */
	public void setAction(ActionBean actionBean){
		this.actionBean = actionBean;
	}
	/**
	 * 获取动作基类
	 */
	public ActionBean getAction(){
		return actionBean;
	}
	
	/**
	 * 设置连接状态
	 */
	public void setConnectStatus(boolean connectStatus){
		this.connectStatus = connectStatus;
	}
	/**
	 * 获取连接状态
	 */
	public boolean getConnectStatus(){
		return connectStatus;
	}
	
	/************************************构造函数***********************************/
	
	public DeviceBean(){}
	
	public DeviceBean(String name,String address){
		this.name = name;
		this.address = address;
	}
	
	public DeviceBean(String name,String address, String broadcastName, String deviceVersion){
		this.name = name;
		this.address = address;
		this.broadcastName = broadcastName;
		this.deviceVersion = deviceVersion;
	}
	
	public DeviceBean(String name,String address, String uuid, int verifyStatus){
		this.name = name;
		this.address = address;
		this.uuid = uuid;
		this.verifyStatus = verifyStatus;
	}
}
