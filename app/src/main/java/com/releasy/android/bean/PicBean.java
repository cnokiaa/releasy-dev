package com.releasy.android.bean;

/**
 * 
 * @author Lighting.Z
 *
 */
public class PicBean {

	private String picPath;    //图片地址
	private boolean isChoose;  //选择标识
	
	//设置图片地址
	public void setPicPath(String picPath){
		this.picPath = picPath;
	}
	//获取图片地址
	public String getPicPath(){
		return picPath;
	}
	
	//设置选择标识
	public void setIsChoose(boolean isChoose){
		this.isChoose = isChoose;
	}
	//获取选择标识
	public boolean getIsChoose(){
		return isChoose;
	}
	
	public PicBean(){}
	
	public PicBean(String picPath,boolean isChoose){
		this.picPath = picPath;
		this.isChoose = isChoose;
	}
}
