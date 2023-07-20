package com.releasy.android.bean;

/**
 * 按摩动作基类
 * @author Lighting.Z
 *
 */
public class ActionBean {

	private int dbId;                //数据库唯一标示
	private int actionId;            //动作ID
	private int roomId;              //馆ID
	private String actionName;       //动作名字
	private int actionType;          //动作类型  0为基础动作  1为场景动作
	private String actionPicUrl;     //动作ICON
	private boolean isChoose = false;//选择标识
	
	private int strength = 1;        //按摩强度
	
	//按摩动作参数
	private int bytesCheck;          //周期字节数
	private int[] checkChangeMode;   //周期字节数 + 变化模式
	private int[] highTime;          //高电频时间
	private int[] lowTime;           //低电频时间
	private int[] innerHighAndLow;   //高电频中子电频高低电平时间    持续时间各8位
	private int[] interval;          //小周期时间间隔
	private int[] period;            //小周期时间
	private int[] rateMin;           //最小占空比
	private int[] rateMax;           //最大占空比
	private int[] rate;              //占空比
	private int[][] powerLV;       //力度参数  占空比
	
	private int[] minmaxList;        //最小最大值
	private int[] mode12List;        //变化规律12
	
	private int maxWorkTime;         //最大工作时间(S)
	private int stopTime;            //停止时间
	
	/********************************动作基础数据设置*******************************/
	
	/**
	 * 设置数据库Id
	 */
	public void setDBId(int dbId){
		this.dbId = dbId;
	}
	/**
	 * 获取数据库Id
	 */
	public int getDBId(){
		return dbId;
	}
	
	/**
	 * 设置活动Id
	 */
	public void setActionId(int actionId){
		this.actionId = actionId;
	}
	/**
	 * 获取活动Id
	 */
	public int getActionId(){
		return actionId;
	}
	
	/**
	 * 设置馆Id
	 */
	public void setRoomId(int roomId){
		this.roomId = roomId;
	}
	/**
	 * 获取馆Id
	 */
	public int getRoomId(){
		return roomId;
	}
	
	/**
	 * 设置动作名称
	 */
	public void setActionName(String actionName){
		this.actionName = actionName;
	}
	/**
	 * 获取动作名称
	 */
	public String getActionName(){
		return actionName;
	}
	
	/**
	 * 设置动作类型
	 */
	public void setActionType(int actionType){
		this.actionType = actionType;
	}
	/**
	 * 获取动作类型
	 */
	public int getActionType(){
		return actionType;
	}
	
	/**
	 * 设置动作icon
	 */
	public void setActionPicUrl(String actionPicUrl){
		this.actionPicUrl = actionPicUrl;
	}
	/**
	 * 获取动作icon
	 */
	public String getActionPicUrl(){
		return actionPicUrl;
	}
	
	/**
	 * 设置选择标识
	 */
	public void setIsChoose(boolean isChoose){
		this.isChoose = isChoose;
	}
	/**
	 * 获取选择标识
	 */
	public boolean getIsChoose(){
		return isChoose;
	}
	
	/**
	 * 设置设备按摩力度
	 */
	public void setStrength(int strength){
		this.strength = strength;
	}
	/**
	 * 获取设备按摩力度
	 */
	public int getStrength(){
		return strength;
	}
	
	/**********************************传参数据设置*********************************/
	
	/**
	 * 设置周期字节数
	 */
	public void setBytesCheck(int bytesCheck){
		this.bytesCheck = bytesCheck;
	}
	/**
	 * 获取周期字节数
	 */
	public int getBytesCheck(){
		return bytesCheck;
	}
	
	/**
	 * 设置周期字节数 + 变化模式
	 */
	public void setCheckChangeMode(int[] checkChangeMode){
		this.checkChangeMode = checkChangeMode;
	}
	/**
	 * 获取周期字节数 + 变化模式
	 */
	public int[] getCheckChangeMode(){
		return checkChangeMode;
	}
	
	/**
	 * 设置高电频时间
	 */
	public void setHighTime(int[] highTime){
		this.highTime = highTime;
	}
	/**
	 * 获取高电频时间
	 */
	public int[] getHighTime(){
		return highTime;
	}
	
	/**
	 * 设置低电频时间
	 */
	public void setLowTime(int[] lowTime){
		this.lowTime = lowTime;
	}
	/**
	 * 获取低电频时间
	 */
	public int[] getLowTime(){
		return lowTime;
	}
	
	/**
	 * 设置子电频 高低电频时间
	 */
	public void setInnerHighAndLow(int[] innerHighAndLow){
		this.innerHighAndLow = innerHighAndLow;
	}
	/**
	 * 获取子电频 高低电频时间
	 */
	public int[] getInnerHighAndLow(){
		return innerHighAndLow;
	}
	
	/**
	 * 设置小周期时间间隔
	 */
	public void setInterval(int[] interval){
		this.interval = interval;
	}
	/**
	 * 获取小周期时间间隔
	 */
	public int[] getInterval(){
		return interval;
	}

	/**
	 * 设置小周期时间
	 */
	public void setPeriod(int[] period){
		this.period = period;
	}
	/**
	 * 获取小周期时间
	 */
	public int[] getPeriod(){
		return period;
	}
	
	/**
	 * 设置  最小占空比
	 */
	public void setRateMin(int[] rateMin){
		this.rateMin = rateMin;
	}
	/**
	 * 获取最小占空比
	 */
	public int[] getRateMin(){
		return rateMin;
	}
	
	/**
	 * 设置  最大占空比
	 */
	public void setRateMax(int[] rateMax){
		this.rateMax = rateMax;
	}
	/**
	 * 获取最小占空比
	 */
	public int[] getRateMax(){
		return rateMax;
	}
	
	/**
	 * 设置占空比
	 */
	public void setRate(int[] rate){
		this.rate = rate;
	}
	/**
	 * 获取占空比
	 */
	public int[] getRate(){
		return rate;
	}
	
	/**
	 * 设置力量占空比
	 */
	public void setPowerLV(int[][] powerLV){
		this.powerLV = powerLV;
	}
	/**
	 * 获取力量占空比
	 */
	public int[][] getPowerLV(){
		return powerLV;
	}
	
	/**
	 * 设置最小值最大值
	 */
	public void setMinMaxList(int[] minmaxList){
		this.minmaxList = minmaxList;
	}
	/**
	 * 获取最小值最大值
	 */
	public int[] getMinMaxList(){
		return minmaxList;
	}

	/**
	 * 设置变化规律12
	 */
	public void setMode12List(int[] mode12List){
		this.mode12List = mode12List;
	}
	/**
	 * 获取变化规律12
	 */
	public int[] getMode12List(){
		return mode12List;
	}
	
	/**
	 * 设置最大工作时间
	 */
	public void setMaxWorkTime(int maxWorkTime){
		this.maxWorkTime = maxWorkTime;
	}
	/**
	 * 获取最大工作时间
	 */
	public int getMaxWorkTime(){
		return maxWorkTime;
	}
	
	/**
	 * 设置停止时间
	 */
	public void setStopTime(int stopTime){
		this.stopTime = stopTime;
	}
	/**
	 * 获取停止时间
	 */
	public int getStopTime(){
		return stopTime;
	}
	
	
	/************************************构造函数***********************************/
	
	public ActionBean(){}
	
	public ActionBean(int actionId, int roomId, String actionName, int actionType){
		this.actionId = actionId;                 //动作Id
		this.roomId = roomId;                     //馆ID
		this.actionName = actionName;             //动作名称
		this.actionType = actionType;             //动作类型
	}
	
	public ActionBean(int dbId, int actionId, int roomId, String actionName, int actionType){
		this.dbId = dbId;                         //数据库Id
		this.actionId = actionId;                 //动作Id
		this.roomId = roomId;                     //馆ID
		this.actionName = actionName;             //动作名称
		this.actionType = actionType;             //动作类型
	}
	
	public ActionBean(int bytesCheck, int[] highTime, int[] lowTime
			, int[] innerHighAndLow, int[] interval, int[] period, int[] rateMin, int[] rateMax, int stopTime){
		this.bytesCheck = bytesCheck;             //周期字节数
		this.highTime = highTime;                 //高电频时间
		this.lowTime = lowTime;                   //低电频时间
		this.innerHighAndLow = innerHighAndLow;   //高电频中子电频高低电平时间    持续时间各8位
		this.interval = interval;                 //小周期时间间隔
		this.period = period;                     //小周期时间
		this.rateMin = rateMin;                   //最小占空比
		this.rateMax = rateMax;                   //最大占空比
		this.stopTime = stopTime;                 //停止时间
	}
	
	public ActionBean(int[] checkChangeMode, int[] highTime, int[] lowTime
			, int[] innerHighAndLow, int[] interval, int[] period, int[] rateMin, int[] rateMax, int stopTime
			, int[] minmaxList, int[] mode12List){
		this.checkChangeMode = checkChangeMode;   //周期字节数 + 变化模式
		this.highTime = highTime;                 //高电频时间
		this.lowTime = lowTime;                   //低电频时间
		this.innerHighAndLow = innerHighAndLow;   //高电频中子电频高低电平时间    持续时间各8位
		this.interval = interval;                 //小周期时间间隔
		this.period = period;                     //小周期时间
		this.rateMin = rateMin;                   //最小占空比
		this.rateMax = rateMax;                   //最大占空比
		this.stopTime = stopTime;                 //停止时间
		this.minmaxList = minmaxList;             //最小值最大值
		this.mode12List = mode12List;             //变化规律12
	}
	
	public ActionBean(int actionId, int roomId, String actionName
			, int actionType, int bytesCheck, int[] highTime, int[] lowTime
			, int[] innerHighAndLow, int[] interval, int[] period, int[] rateMin, int[] rateMax, int stopTime){
		this.actionId = actionId;                 //动作Id
		this.roomId = roomId;                     //馆ID
		this.actionName = actionName;             //动作名称
		this.actionType = actionType;             //动作类型
		
		this.bytesCheck = bytesCheck;             //周期字节数
		this.highTime = highTime;                 //高电频时间
		this.lowTime = lowTime;                   //低电频时间
		this.innerHighAndLow = innerHighAndLow;   //高电频中子电频高低电平时间    持续时间各8位
		this.interval = interval;                 //小周期时间间隔
		this.period = period;                     //小周期时间
		this.rateMin = rateMin;                   //最小占空比
		this.rateMax = rateMax;                   //最大占空比
		this.stopTime = stopTime;                 //停止时间
	}
	
	public ActionBean(int actionId, int roomId, String actionName
			, int actionType, int[] checkChangeMode, int[] highTime, int[] lowTime
			, int[] innerHighAndLow, int[] interval, int[] period, int[] rateMin, int[] rateMax, int stopTime
			, int[] minmaxList, int[] mode12List){
		this.actionId = actionId;                 //动作Id
		this.roomId = roomId;                     //馆ID
		this.actionName = actionName;             //动作名称
		this.actionType = actionType;             //动作类型
		
		this.checkChangeMode = checkChangeMode;   //周期字节数 + 变化模式
		this.highTime = highTime;                 //高电频时间
		this.lowTime = lowTime;                   //低电频时间
		this.innerHighAndLow = innerHighAndLow;   //高电频中子电频高低电平时间    持续时间各8位
		this.interval = interval;                 //小周期时间间隔
		this.period = period;                     //小周期时间
		this.rateMin = rateMin;                   //最小占空比
		this.rateMax = rateMax;                   //最大占空比
		this.stopTime = stopTime;                 //停止时间
		this.minmaxList = minmaxList;             //最小值最大值
		this.mode12List = mode12List;             //变化规律12
	}
	
	public void setActionInfo(int actionId, int roomId, String actionName, int actionType){
		this.actionId = actionId;                 //动作Id
		this.roomId = roomId;                     //馆ID
		this.actionName = actionName;             //动作名称
		this.actionType = actionType;             //动作类型
	}
	
	public void setActionParameter(int bytesCheck, int[] highTime, int[] lowTime, int[] innerHighAndLow
			, int[] interval, int[] period, int[] rateMin, int[] rateMax, int stopTime){
		this.bytesCheck = bytesCheck;             //周期字节数
		this.highTime = highTime;                 //高电频时间
		this.lowTime = lowTime;                   //低电频时间
		this.innerHighAndLow = innerHighAndLow;   //高电频中子电频高低电平时间    持续时间各8位
		this.interval = interval;                 //小周期时间间隔
		this.period = period;                     //小周期时间
		this.rateMin = rateMin;                   //最小占空比
		this.rateMax = rateMax;                   //最大占空比
		this.stopTime = stopTime;                 //停止时间
	}
	
	public void setActionParameter(int[] checkChangeMode, int[] highTime, int[] lowTime
			, int[] innerHighAndLow, int[] interval, int[] period, int[] rateMin, int[] rateMax, int stopTime
			, int[] minmaxList, int[] mode12List){
		this.checkChangeMode = checkChangeMode;   //周期字节数 + 变化模式
		this.highTime = highTime;                 //高电频时间
		this.lowTime = lowTime;                   //低电频时间
		this.innerHighAndLow = innerHighAndLow;   //高电频中子电频高低电平时间    持续时间各8位
		this.interval = interval;                 //小周期时间间隔
		this.period = period;                     //小周期时间
		this.rateMin = rateMin;                   //最小占空比
		this.rateMax = rateMax;                   //最大占空比
		this.stopTime = stopTime;                 //停止时间
		this.minmaxList = minmaxList;             //最小值最大值
		this.mode12List = mode12List;             //变化规律12
	}
}
