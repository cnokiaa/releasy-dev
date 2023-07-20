package com.releasy.android.constants;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.releasy.android.R;
import com.releasy.android.bean.MusicBean;
import com.releasy.android.bean.RoomBean;
import com.releasy.android.utils.StringUtils;

public class RoomConstants {

	public static final int ROOM_BELONG_TO_CURRENCY = 0;
	public static final int ROOM_BELONG_TO_M1 = 1;
	public static final int ROOM_BELONG_TO_M2 = 2;
	
	public static final int ADD_ROOM = -1;
	public static final int ACTION_DISTRIBUTION_FOR_M2 = -2;
	public static final int ACTION_COUNTENANCE_TYPE = -3;
	
	public static final int ROOM_SINGLE_TYPE = 0;
	public static final int ROOM_MULTIPLE_TYPE = 1;
	public static final int ROOM_USER_DEFIND = 2;
	
	public static final int ROOM_USER_DEFIND_ID = 20000;
	
	public static final int NO_INITIAL = -10;
	public static final int RELEASY_ADD_ROOM = -1;
	public static final int RELEASY_CLASSICS = 1001;
	public static final int RELEASY_PYHSIOTHERAPY = 1002;
	public static final int RELEASY_DHYANA = 1003;
	public static final int RELEASY_HOME = 1004;
	public static final int RELEASY_WORK = 1005;
	public static final int RELEASY_TIDE = 1006;
	public static final int RELEASY_VALLEY = 1007;
	public static final int RELEASY_ON_THE_WAY = 1008;
	public static final int RELEASY_RESPITE = 1009;
	public static final int RELEASY_TRAVEL = 1010;
	
	//初始放松馆数据 
	public static List<RoomBean> getInitialRoomList(Context context){
		List<RoomBean> roomList = new ArrayList<RoomBean>();
		RoomBean room;
		
		room = new RoomBean(RELEASY_CLASSICS,context.getString(R.string.classic),ROOM_MULTIPLE_TYPE,"",ROOM_BELONG_TO_CURRENCY); roomList.add(room);
		room = new RoomBean(RELEASY_PYHSIOTHERAPY,context.getString(R.string.physiotherapy),ROOM_MULTIPLE_TYPE,"",ROOM_BELONG_TO_CURRENCY); roomList.add(room);
		room = new RoomBean(RELEASY_DHYANA,context.getString(R.string.zen),ROOM_SINGLE_TYPE,"",ROOM_BELONG_TO_CURRENCY); roomList.add(room);
		room = new RoomBean(RELEASY_HOME,context.getString(R.string.home),ROOM_SINGLE_TYPE,"",ROOM_BELONG_TO_CURRENCY); roomList.add(room);
		room = new RoomBean(RELEASY_WORK,context.getString(R.string.office),ROOM_SINGLE_TYPE,"",ROOM_BELONG_TO_CURRENCY); roomList.add(room);
		room = new RoomBean(RELEASY_TIDE,context.getString(R.string.seaside),ROOM_SINGLE_TYPE,"",ROOM_BELONG_TO_CURRENCY); roomList.add(room);
		room = new RoomBean(RELEASY_VALLEY,context.getString(R.string.valley),ROOM_SINGLE_TYPE,"",ROOM_BELONG_TO_CURRENCY); roomList.add(room);
		room = new RoomBean(RELEASY_ON_THE_WAY,context.getString(R.string.on_the_way),ROOM_SINGLE_TYPE,"",ROOM_BELONG_TO_CURRENCY); roomList.add(room);
		room = new RoomBean(RELEASY_RESPITE,context.getString(R.string.taking_a_nap),ROOM_SINGLE_TYPE,"",ROOM_BELONG_TO_CURRENCY); roomList.add(room);
		room = new RoomBean(RELEASY_TRAVEL,context.getString(R.string.travel),ROOM_SINGLE_TYPE,"",ROOM_BELONG_TO_CURRENCY); roomList.add(room);
		
		return roomList;
	}
	
	/**
	 * 获取官方放松馆封面图片
	 * @param roomId
	 * @return
	 */
	public static int getRoomPic(int roomId,String picUrl){
		int roomPic = NO_INITIAL;
		switch(roomId){
		case RELEASY_ADD_ROOM:
			roomPic = R.drawable.ic_releasy_add_room;
			break;
		case RELEASY_CLASSICS:
			if(!StringUtils.isBlank(picUrl) && picUrl.indexOf("jingdian1") >= 0)
				roomPic = R.drawable.jingdian1;
			break;
		case RELEASY_PYHSIOTHERAPY:
			if(!StringUtils.isBlank(picUrl) && picUrl.indexOf("zhongyi1") >= 0)
				roomPic = R.drawable.zhongyi1;
			break;
		case RELEASY_DHYANA:
			if(!StringUtils.isBlank(picUrl) && picUrl.indexOf("chan1") >= 0)
				roomPic = R.drawable.chan1;
			break;
		case RELEASY_HOME:
			if(!StringUtils.isBlank(picUrl) && picUrl.indexOf("zhai1") >= 0)
				roomPic = R.drawable.zhai1;
			break;
		case RELEASY_WORK:
			if(!StringUtils.isBlank(picUrl) && picUrl.indexOf("bangongshi1") >= 0)
				roomPic = R.drawable.bangongshi1;
			break;
		case RELEASY_TIDE:
			if(!StringUtils.isBlank(picUrl) && picUrl.indexOf("tingchao1") >= 0)
				roomPic = R.drawable.tingchao1;
			break;
		case RELEASY_VALLEY:
			if(!StringUtils.isBlank(picUrl) && picUrl.indexOf("konggu1") >= 0)
				roomPic = R.drawable.konggu1;
			break;
		case RELEASY_ON_THE_WAY:
			if(!StringUtils.isBlank(picUrl) && picUrl.indexOf("zailushang1") >= 0)
				roomPic = R.drawable.zailushang1;
			break;
		case RELEASY_RESPITE:
			if(!StringUtils.isBlank(picUrl) && picUrl.indexOf("xiaoqi1") >= 0)
				roomPic = R.drawable.xiaoqi1;
			break;
		case RELEASY_TRAVEL:
			if(!StringUtils.isBlank(picUrl) && picUrl.indexOf("lvxing1") >= 0)
				roomPic = R.drawable.lvxing1;
			break;
		default:
			roomPic = NO_INITIAL;
			break;
		}
		
		return roomPic;
	}
	
	/**
	 * 获取官方放松馆封面图片
	 * @param roomId
	 * @return
	 */
	public static int getRoomPic(int roomId){
		int roomPic = R.drawable.ic_acquiesce_img;
		switch(roomId){
		case RELEASY_ADD_ROOM:
			roomPic = R.drawable.ic_releasy_add_room;
			break;
		case RELEASY_CLASSICS:
			roomPic = R.drawable.jingdian1;
			break;
		case RELEASY_PYHSIOTHERAPY:
			roomPic = R.drawable.zhongyi1;
			break;
		case RELEASY_DHYANA:
			roomPic = R.drawable.chan1;
			break;
		case RELEASY_HOME:
			roomPic = R.drawable.zhai1;
			break;
		case RELEASY_WORK:
			roomPic = R.drawable.bangongshi1;
			break;
		case RELEASY_TIDE:
			roomPic = R.drawable.tingchao1;
			break;
		case RELEASY_VALLEY:
			roomPic = R.drawable.konggu1;
			break;
		case RELEASY_ON_THE_WAY:
			roomPic = R.drawable.zailushang1;
			break;
		case RELEASY_RESPITE:
			roomPic = R.drawable.xiaoqi1;
			break;
		case RELEASY_TRAVEL:
			roomPic = R.drawable.lvxing1;
			break;
		default:
			roomPic = R.drawable.ic_acquiesce_img;
			break;
		}
		
		return roomPic;
	}
	
	
	/**
	 * 获取官方放松馆名称
	 * @param roomId
	 * @return
	 */
	public static String getRoomName(int roomId,Context context){
		String roomName = null;
		switch(roomId){
		case RELEASY_CLASSICS:
			roomName = context.getString(R.string.classic);
			break;
		case RELEASY_PYHSIOTHERAPY:
			roomName = context.getString(R.string.physiotherapy);
			break;
		case RELEASY_DHYANA:
			roomName = context.getString(R.string.zen);
			break;
		case RELEASY_HOME:
			roomName = context.getString(R.string.home);
			break;
		case RELEASY_WORK:
			roomName = context.getString(R.string.office);
			break;
		case RELEASY_TIDE:
			roomName = context.getString(R.string.seaside);
			break;
		case RELEASY_VALLEY:
			roomName = context.getString(R.string.valley);
			break;
		case RELEASY_ON_THE_WAY:
			roomName = context.getString(R.string.on_the_way);
			break;
		case RELEASY_RESPITE:
			roomName = context.getString(R.string.taking_a_nap);
			break;
		case RELEASY_TRAVEL:
			roomName = context.getString(R.string.travel);
			break;
		default:
			break;
		}
		
		return roomName;
	}
	
	/**
	 * 获取官方放松馆封面图片
	 * @param roomId
	 * @return
	 */
	public static int getRoomTopNavPic(int roomId){
		int roomPic = R.drawable.bg_1;
		switch(roomId){
		case RELEASY_CLASSICS:
			roomPic = R.drawable.bg_1;
			break;
		case RELEASY_PYHSIOTHERAPY:
			roomPic = R.drawable.bg_2;
			break;
		case RELEASY_DHYANA:
			roomPic = R.drawable.bg_3;
			break;
		case RELEASY_HOME:
			roomPic = R.drawable.bg_4;
			break;
		case RELEASY_WORK:
			roomPic = R.drawable.bg_5;
			break;
		case RELEASY_TIDE:
			roomPic = R.drawable.bg_6;
			break;
		case RELEASY_VALLEY:
			roomPic = R.drawable.bg_7;
			break;
		case RELEASY_ON_THE_WAY:
			roomPic = R.drawable.bg_8;
			break;
		case RELEASY_RESPITE:
			roomPic = R.drawable.bg_9;
			break;
		case RELEASY_TRAVEL:
			roomPic = R.drawable.bg_10;
			break;
		default:
			roomPic = R.drawable.bg_1;
			break;
		}
		
		return roomPic;
	}
}
