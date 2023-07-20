package com.releasy.android.db;

import java.util.ArrayList;
import java.util.List;
import com.releasy.android.bean.RoomBean;
import com.releasy.android.constants.Constants;
import com.releasy.android.constants.RoomConstants;
import com.releasy.android.utils.StringUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RoomDBUtils {

	/**
	 * 获取DatabaseHelper对象
	 */
	public static ReleasyDatabaseHelper openData(Context context){
		ReleasyDatabaseHelper db = new ReleasyDatabaseHelper(context, "releasy.db3" , null, Constants.DB_VERSION);
		return db;
	}
	
	/**
	 * 插入数据
	 */
	public static void insertData(ReleasyDatabaseHelper myDb, int roomId, String roomName
			, int roomType, String roomUrl, int roomBelong){
		SQLiteDatabase db = myDb.getReadableDatabase();
		db.execSQL("insert into Room values(null, ?, ?, ?, ?, ?)"
				, new Object[]{roomId, roomName, roomType, roomUrl, roomBelong});
		myDb.close();
	}
	
	/**
	 * 判断是否存在
	 */
	public static boolean isRoomExist(ReleasyDatabaseHelper myDb, int roomId){
		SQLiteDatabase db = myDb.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from Room where roomId = \"" + roomId + "\"", null);
		
		if(cursor != null){
			if(cursor.getCount() > 0){
				myDb.close();
				return true;
			}
		}
		
		if (cursor != null) {
	        cursor.close();
	    }
		
		myDb.close();
		return false;
	}
	
	/**
	 * 删除某个馆
	 */
	public static void deleteData(ReleasyDatabaseHelper myDb, int roomId){
		SQLiteDatabase db = myDb.getReadableDatabase();
		db.delete("Room", "roomId" +"=\"" + roomId +"\"" , null);
		myDb.close();
	}
	
	/**
	 * 删除全部馆
	 */
	public static void deleteAllData(ReleasyDatabaseHelper myDb){
		SQLiteDatabase db = myDb.getReadableDatabase();
		db.delete("Room", "roomId<?", new String[]{"20000"});
		myDb.close();
	}
	
	/**
	 * 查询特定的一个数据 
	 * 返回List<DeviceBean>
	 */
	public static RoomBean searchRoomIdData(ReleasyDatabaseHelper myDb, int id){
		RoomBean room = null;
		
		SQLiteDatabase db = myDb.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from Room where roomId = \"" + id + "\"", null);
		while(cursor.moveToNext()){
			int dbId = cursor.getInt(0);
			int roomId = cursor.getInt(1);
			String roomName = cursor.getString(2);
			int roomType = cursor.getInt(3);
			String roomUrl = cursor.getString(4);
			int roomBelog = cursor.getInt(5);
			
			room = new RoomBean(roomId,roomName,roomType,roomUrl,roomBelog);
			//Log.d("z17m","dbId:" + dbId + "    roomId:" + roomId + "    roomName:" + roomName
			//		+ "    roomType:" + roomType + "    roomUrl:" + roomUrl);
		}
		
		if (cursor != null) {
	        cursor.close();
	    }
		
		myDb.close();
		return room;
	}
	
	/**
	 * 查询官方数据 
	 * 返回List<DeviceBean>
	 */
	public static List<RoomBean> searchAuthorityData(ReleasyDatabaseHelper myDb){
		List<RoomBean> dataList = new ArrayList<RoomBean>();
		
		SQLiteDatabase db = myDb.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from Room where roomType = \"" + 0 + "\"" + " OR roomType = \"" + 1 + "\"", null);
		while(cursor.moveToNext()){
			int dbId = cursor.getInt(0);
			int roomId = cursor.getInt(1);
			String roomName = cursor.getString(2);
			int roomType = cursor.getInt(3);
			String roomUrl = cursor.getString(4);
			int roomBelog = cursor.getInt(5);
			
			RoomBean bean = new RoomBean(roomId,roomName,roomType,roomUrl,roomBelog);
			//Log.d("z17m","dbId:" + dbId + "    roomId:" + roomId + "    roomName:" + roomName
			//		+ "    roomType:" + roomType + "    roomUrl:" + roomUrl);
			
			dataList.add(bean);
		}
		
		if (cursor != null) {
	        cursor.close();
	    }
		
		myDb.close();
		return dataList;
	}
	
	/**
	 * 查询所有数据 
	 * 返回List<DeviceBean>
	 */
	public static List<RoomBean> searchAllData(ReleasyDatabaseHelper myDb, Context context){
		List<RoomBean> dataList = new ArrayList<RoomBean>();
		
		SQLiteDatabase db = myDb.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from Room ORDER BY roomId ASC", null);
		while(cursor.moveToNext()){
			int dbId = cursor.getInt(0);
			int roomId = cursor.getInt(1);
			String roomName = cursor.getString(2);
			int roomType = cursor.getInt(3);
			String roomUrl = cursor.getString(4);
			int roomBelog = cursor.getInt(5);
			
			String name = RoomConstants.getRoomName(roomId,context);
			if(!StringUtils.isBlank(name))
				roomName = name;
			
			RoomBean bean = new RoomBean(roomId,roomName,roomType,roomUrl,roomBelog);
			Log.d("z17m","dbId:" + dbId + "    roomId:" + roomId + "    roomName:" + roomName
					+ "    roomType:" + roomType + "    roomUrl:" + roomUrl + "    roomBelog :" + roomBelog );
			
			dataList.add(bean);
		}
		
		if (cursor != null) {
	        cursor.close();
	    }
		
		myDb.close();
		return dataList;
	}
	
	/**
	 * 查询最大的roomId 
	 * 数据 返回int
	 */
	public static int searchMaxRoomIdData(ReleasyDatabaseHelper myDb){
		int id = 0 ;
		SQLiteDatabase db = myDb.getReadableDatabase();
		Cursor cursor = db.rawQuery("select MAX(roomId) from Room", null);
		while(cursor.moveToNext()){
			int roomId = cursor.getInt(0);
			if(roomId <= 20000)
				id = 20000;
			else
				id = roomId;
		}
		
		if (cursor != null) {
	        cursor.close();
	    }
		
		myDb.close();
		return id;
	}
	
	/**
	 * 更改分组名称
	 */
	public static void updataRoom(ReleasyDatabaseHelper myDb, int roomId, String roomName, String roomUrl){
		SQLiteDatabase db = myDb.getReadableDatabase();
		db.execSQL("update Room set roomName=\"" + roomName + "\" , roomUrl =\"" + roomUrl + "\""+
				" where roomId = \"" + roomId + "\"" );
		myDb.close();
	}
}
