package com.releasy.android.bean;

/**
 * 放松馆基类
 * @author Lighting.Z
 *
 */
public class RoomBean {

	private int roomId;                  //馆Id
	private String roomName;             //馆名称
	private int roomType;                //馆类型   0为复合动作  1为单一动作  2为自定义 3M2动作分配
	private String roomPic;              //馆封面
	private int roomBelong;              //属于   0通用  1M1专用  2M2专用
	
	/**********************************基础数据设置*********************************/
	
	/**
	 * 设置放松馆Id
	 */
	public void setRoomId(int roomId){
		this.roomId = roomId;
	}
	/**
	 * 获取放松馆Id
	 */
	public int getRoomId(){
		return roomId;
	}
	
	/**
	 * 设置放松馆名称
	 */
	public void setRoomName(String roomName){
		this.roomName = roomName;
	}
	/**
	 * 获取放松馆名称
	 */
	public String getRoomName(){
		return roomName;
	}
	
	/**
	 * 设置放松馆类型
	 */
	public void setRoomType(int roomType){
		this.roomType = roomType;
	}
	/**
	 * 获取放松馆类型
	 */
	public int getRoomType(){
		return roomType;
	}
	
	/**
	 * 设置放松馆封面
	 */
	public void setRoomPic(String roomPic){
		this.roomPic = roomPic;
	}
	/**
	 * 获取放松馆封面
	 */
	public String getRoomPic(){
		return roomPic;
	}
	
	public void setRoomBelong(int roomBelong){
		this.roomBelong = roomBelong;
	}
	public int getRoomBelong(){
		return roomBelong;
	}
	
	
	/**********************************构造函数设置*********************************/
	public RoomBean(){}
	
	public RoomBean(int roomId, String roomName, int roomType, String roomPic, int roomBelong){
		this.roomId = roomId;               //馆Id
		this.roomName = roomName;           //馆名称
		this.roomType = roomType;           //馆类型   0为复合动作  1为单一动作  2为自定义
		this.roomPic = roomPic;             //馆封面
	}
	
}
