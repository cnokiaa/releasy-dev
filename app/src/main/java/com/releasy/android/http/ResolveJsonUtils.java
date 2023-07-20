package com.releasy.android.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.releasy.android.bean.ActionBean;
import com.releasy.android.bean.FeedbackBean;
import com.releasy.android.bean.MusicBean;
import com.releasy.android.bean.RoomBean;
import com.releasy.android.utils.Utils;

import android.os.Bundle;
import android.util.Log;

/**
 * 此类用于解析httppost返回的json参数中的头部分
 * 接口详情参考接口文档《E客 1.0 服务端API》
 */
public class ResolveJsonUtils {

	/***********************************接口头******************************************/
	/**
	 * uri：所有
	 * 获取Json数据头 数据头包含请求成功失败，失败原因的信息。
	 * 如"retStatus":1,"retMsg":"请求成功"
	 * 根据返回值在AsyncTask中做判断，更新UI
	 */
	public static Bundle getJsonHead(String respJsonStr){
		Bundle bundle = new Bundle();
		JSONObject json;
		try {
			json = new JSONObject(respJsonStr);
			int retStatus = json.getInt("retStatus");
			String retMsg = json.getString("retMsg");
			bundle.putInt("retStatus", retStatus);
			bundle.putString("retMsg", retMsg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return bundle;
	}
	
	
	/*************************************注册******************************************/
	/**
	 * uri：user/regist
	 * 获取注册接口中返回Json中的Data数据
	 * 当JsonHead中的retStatus为1时才需要调用此函数解析用户ID
	 */
	public static int regist(String respJsonStr){
		JSONObject json;
		try {
			json = new JSONObject(respJsonStr);
			JSONObject dataJson = json.getJSONObject("data");
			int uid = dataJson.getInt("uid");
			return uid;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return 10000;
	}
	
	/***********************************App版本号****************************************/
	/**
	 * uri：common/check_appVersion
	 * 获取App版本号接口中返回Json中的Data数据
	 * 当JsonHead中的retStatus为1时才需要调用此函数解析App版本号信息
	 */
	public static Bundle getAppVersion(String respJsonStr){
		Bundle bundle = new Bundle();
		JSONObject json;
		//Json返回字段可能有所变动,函数返回值可能有所变动
		try {
			json = new JSONObject(respJsonStr);
			JSONObject dataJson = json.getJSONObject("data");
			String message = dataJson.getString("message");
			String updateStrategy = dataJson.getString("updateStrategy");
			String downloadUrl = dataJson.getString("downloadUrl");
			String newVersion = dataJson.getString("newVersion");
			bundle.putString("message", message);
			bundle.putString("updateStrategy", updateStrategy);
			bundle.putString("downloadUrl", downloadUrl);
			bundle.putString("newVersion", newVersion);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bundle;
	}
	
	/*************************************音乐列表******************************************/
	/**
	 * uri：get_musicList
	 * 获取音乐列表接口中返回Json中的list数据
	 */
	public static Bundle getMusicList(String respJsonStr){
		Bundle bundle = new Bundle();
		JSONObject json;
		List<MusicBean> dataList = new ArrayList<MusicBean>();
		try {
			json = new JSONObject(respJsonStr);
			JSONObject dataJson = json.getJSONObject("list");
			int total = dataJson.getInt("total");
			JSONArray array = dataJson.getJSONArray("rows");
			for(int i = 0 ; i < array.length(); i ++){
				JSONObject itemJson = array.getJSONObject(i);
				int fileSize = itemJson.getInt("fileSize");
				int downloadCount = itemJson.getInt("downloadCount");
				String musicUrl = itemJson.getString("musicUrl");
				String musicName = itemJson.getString("musicName");
				String artist = itemJson.getString("artist");
				int musicID = itemJson.getInt("musicID");
				String musicPic = itemJson.getString("musicPic");

				Log.d("z17m","musicPic : " + musicPic +"    musicUrl : " + musicUrl);
				MusicBean bean = new MusicBean(musicID, musicName, artist, musicPic, musicUrl);
				dataList.add(bean);
			}
			
			bundle.putInt("total", total);
			bundle.putSerializable("dataList", (Serializable) dataList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bundle;
	}
	
	
	
	/***********************************放松馆版本号****************************************/
	/**
	 * uri：room/get_relaxRoom_versionInfo
	 * 获取放松馆版本号接口中返回Json中的Data数据
	 * 当JsonHead中的retStatus为1时才需要调用此函数解析放松馆版本号信息
	 */
	public static Bundle getReleasyVersion(String respJsonStr){
		Bundle bundle = new Bundle();
		JSONObject json;
		//Json返回字段可能有所变动,函数返回值可能有所变动
		try {
			json = new JSONObject(respJsonStr);
			JSONObject dataJson = json.getJSONObject("data");
			String updateLog = dataJson.getString("updateLog");
			String version = dataJson.getString("version");
			bundle.putString("updateLog", updateLog);
			bundle.putString("version", version);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bundle;
	}
	
	
	/*************************************放松馆列表******************************************/
	/**
	 * uri：get_musicList
	 * 获取音乐列表接口中返回Json中的list数据
	 */
	public static Bundle getRelaxRoomList(String respJsonStr){
		Bundle bundle = new Bundle();
		JSONObject json;
		List<RoomBean> roomList = new ArrayList<RoomBean>();
		List<ActionBean> actionList = new ArrayList<ActionBean>();
		try {
			json = new JSONObject(respJsonStr);
			JSONObject dataJson = json.getJSONObject("list");
			int total = dataJson.getInt("total");
			JSONArray array = dataJson.getJSONArray("rows");
			for(int i = 0 ; i < array.length(); i ++){
				JSONObject itemJson = array.getJSONObject(i);
				
				int roomID = itemJson.getInt("roomID");
				int roomType = itemJson.getInt("roomType");
				String roomName = itemJson.getString("roomName");
				String roomPic = itemJson.getString("roomPic");
				RoomBean room = new RoomBean(roomID,roomName,roomType,roomPic,0);
				roomList.add(room);
				
				JSONArray actions = itemJson.getJSONArray("actions");
				for(int j = 0 ; j < actions.length(); j ++){
					JSONObject actionJson = actions.getJSONObject(j);
					JSONObject actionData = actionJson.getJSONObject("actionData");
					int bytesCheck = actionData.getInt("bytesCheck");
					String highTime = actionData.getString("highTime");
					String lowTime = actionData.getString("lowTime");
					String innerHighAndLow = actionData.getString("innerHighAndLow");
					String interval = actionData.getString("interval");
					String period = actionData.getString("period");
					String maxRate = actionData.getString("maxRate");
					String minRate = actionData.getString("minRate");
					int actionID = actionJson.getInt("actionID");
					String actionName = actionJson.getString("actionName");
					String actionPic = actionJson.getString("actionPic");
					int actionType = actionJson.getInt("actionType");
					
					ActionBean action = new ActionBean(actionID,roomID,actionName,actionType);
					action.setBytesCheck(bytesCheck);
					action.setHighTime(Utils.arrayString2Int(highTime));
					action.setLowTime(Utils.arrayString2Int(lowTime));
					action.setInnerHighAndLow(Utils.arrayString2Int(innerHighAndLow));
					action.setInterval(Utils.arrayString2Int(interval));
					action.setPeriod(Utils.arrayString2Int(period));
					action.setRateMin(Utils.arrayString2Int(minRate));
					action.setRateMax(Utils.arrayString2Int(maxRate));
					action.setActionPicUrl(actionPic);
					
					actionList.add(action);
				}
			}
			
			bundle.putInt("total", total);
			bundle.putSerializable("roomList", (Serializable) roomList);
			bundle.putSerializable("actionList", (Serializable) actionList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bundle;
	}
	
	
	/*************************************反馈通知******************************************/
	/**
	 * uri：suggestion/get_suggestionNotify
	 * 获取反馈通知接口中返回Json中的Data数据
	 * 当JsonHead中的retStatus为1时才需要调用此函数解析反馈通知
	 */
	public static int getSuggestionNotifyResult(String respJsonStr){
		JSONObject json;
		int notificationCount = 0;
		//Json返回字段可能有所变动,函数返回值可能有所变动
		try {
			json = new JSONObject(respJsonStr);
			JSONObject dataJson = json.getJSONObject("data");
			notificationCount = dataJson.getInt("notificationCount");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return notificationCount;
	}
	
	
	/*************************************会话列表******************************************/
	/**
	 * uri：get_msgList
	 * 获取消息列表接口中返回Json中的list数据
	 */
	public static Bundle getSessionList(String respJsonStr){
		Bundle bundle = new Bundle();
		JSONObject json;
		List<FeedbackBean> dataList = new ArrayList<FeedbackBean>();
		try {
			json = new JSONObject(respJsonStr);
			JSONObject dataJson = json.getJSONObject("list");
			int total = dataJson.getInt("total");
			JSONArray array = dataJson.getJSONArray("rows");
			for(int i = 0 ; i < array.length(); i ++){
				JSONObject itemJson = array.getJSONObject(i);
				String content = itemJson.getString("content");
				String time = itemJson.getString("time");
				int source = itemJson.getInt("source");
				
				FeedbackBean bean = new FeedbackBean(time, content, source);
				dataList.add(bean);
			}
			bundle.putInt("total", total);
			bundle.putSerializable("dataList", (Serializable) dataList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bundle;
	}
	
	
	/*************************************检测设备******************************************/
	/**
	 * uri：suggestion/get_suggestionNotify
	 * 获取反馈通知接口中返回Json中的Data数据
	 * 当JsonHead中的retStatus为1时才需要调用此函数解析反馈通知
	 */
	public static int checkDeviceIdResult(String respJsonStr){
		JSONObject json;
		int isValid = 0;
		//Json返回字段可能有所变动,函数返回值可能有所变动
		try {
			json = new JSONObject(respJsonStr);
			JSONObject dataJson = json.getJSONObject("data");
			isValid = dataJson.getInt("isValid");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return isValid;
	}
}
