package com.releasy.android.db;

import java.util.ArrayList;
import java.util.List;

import com.releasy.android.bean.DeviceBean;
import com.releasy.android.constants.Constants;
import com.releasy.android.utils.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 数据库设备表管理工具类
 * 包括 插入 查询 删除
 * 字段name为设备名称
 * 字段address为设备Mac地址
 * 字段uuid为设备标示ID
 * 字段delStatus为设备删除状态    0为保存 1为删除  表中做假删除
 * 字段verifyStatus为设备验证状态    0为未验证  1为已通过  2未通过
 * 字段deviceHardwareVersion为设备硬件版本
 * 字段deviceSoftwareVersion为设备软件版本
 * 字段broadcastName为设备广播名称 真实名称
 * 字段deviceVersion为设备型号  M1、 M2_A、 M2_B
 * @author Lighting.Z
 *
 */
public class DeviceDBUtils {

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
	public static void insertData(ReleasyDatabaseHelper myDb, String name, String address, String broadcastName, String deviceVersion){
		SQLiteDatabase db = myDb.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("select * from Device where address = \"" + address + "\"", null);
		if(cursor != null){
			if(cursor.getCount() > 0){
				db.execSQL("update Device set delStatus =\"" + 0 + "\"" + " where address = \"" + address + "\"" );
			}
			else{
				//Utils.showLogD("cursor.getCount() = 0");
				db.execSQL("insert into Device values(null, ?, ?, ?, ? ,? ,?, ?, ?, ?)"
						, new Object[]{name, address, "", 0, 0, 0, 0, broadcastName, deviceVersion});
			}
		}
		else{
			//Utils.showLogD("cursor is null");
			db.execSQL("insert into Device values(null, ?, ?, ?, ? ,?, ?, ?, ? ,?)"
					, new Object[]{name, address, "", 0, 0, 0, 0, broadcastName, deviceVersion});
		}
		
		if (cursor != null) {
	        cursor.close();
	    }
		
		myDb.close();
	}
	
	/**
	 * 判断是否存在
	 */
	public static boolean isAddressExist(ReleasyDatabaseHelper myDb, String address){
		SQLiteDatabase db = myDb.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("select * from Device where address = \"" + address + "\""
				+ " AND delStatus = \"" + 0 + "\"", null);
		
		if(cursor != null){
			//Utils.showLogD("cursor.getCount() = " + cursor.getCount());
			if(cursor.getCount() > 0){
				//Utils.showLogD("Address is exist");
				return true;
			}
		}
		
		if (cursor != null) {
	        cursor.close();
	    }
		
		//Utils.showLogD("Address is not exist");
		return false;
		
	}
	
	/**
	 * 判断是否存在
	 */
	public static String getDeviceName(ReleasyDatabaseHelper myDb, String address){
		SQLiteDatabase db = myDb.getReadableDatabase();
		String rename = null;
		
		Cursor cursor = db.rawQuery("select * from Device where address = \"" + address + "\""
				, null);
		
		if(cursor == null){
			return null;
		}
		
		while(cursor.moveToNext()){
			int dbId = cursor.getInt(0);
			String deviceName = cursor.getString(1);
			String deviceAddress = cursor.getString(2);
			String uuid = cursor.getString(3);
			int delStatus = cursor.getInt(4);
			int verifyStatus = cursor.getInt(5);
			uuid = uuid.replace("$", "");
			
			rename = deviceName;
			//bean = new DeviceBean(deviceName,deviceAddress,uuid,verifyStatus);
			//Log.d("z17m","dbId:" + dbId + "    name:" + name + "    address:" + address 
			//		+ "    uuid:" + uuid + "    verifyStatus:" + verifyStatus);
		}
		
		if (cursor != null) {
	        cursor.close();
	    }
		
		myDb.close();
		return rename;
	}
	
	
	/**
	 * 删除分类 
	 */
	public static void deleteData(ReleasyDatabaseHelper myDb, String address){
		SQLiteDatabase db = myDb.getReadableDatabase();
		//db.delete("Device", "address" +"=\"" + address +"\"" , null);
		db.execSQL("update Device set delStatus = \"" + 1 + "\"" + " where address = \"" + address + "\"" );
		myDb.close();
	}
	
	/**
	 * 更新Name
	 */
	public static void UpdataName(ReleasyDatabaseHelper myDb, String address, String name){
		SQLiteDatabase db = myDb.getReadableDatabase();
		db.execSQL("update Device set name =\"" + name + "\"" + " where address = \"" + address + "\"" );
		myDb.close();
	}
	
	/**
	 * 更新Uuid
	 */
	public static void UpdataDeviceUuid(ReleasyDatabaseHelper myDb, String address, String uuid){
		SQLiteDatabase db = myDb.getReadableDatabase();
		uuid = "$" + uuid;
		db.execSQL("update Device set uuid =\"" + uuid + "\"" + " where address = \"" + address + "\"" );
		myDb.close();
	}
	
	/**
	 * 更新verify
	 */
	public static void UpdataDeviceVerify(ReleasyDatabaseHelper myDb, String address, int verifyStatus){
		SQLiteDatabase db = myDb.getReadableDatabase();
		db.execSQL("update Device set verifyStatus =\"" + verifyStatus + "\"" + " where address = \"" + address + "\"" );
		myDb.close();
	}
	
	/**
	 * 查询数据 返回List<DeviceBean>
	 */
	public static List<DeviceBean> searchData(ReleasyDatabaseHelper myDb){
		List<DeviceBean> dataList = new ArrayList<DeviceBean>();
		
		SQLiteDatabase db = myDb.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from Device where delStatus = \"" + 0 + "\"", null);
		while(cursor.moveToNext()){
			int dbId = cursor.getInt(0);
			String name = cursor.getString(1);
			String address = cursor.getString(2);
			String uuid = cursor.getString(3);
			int delStatus = cursor.getInt(4);
			int verifyStatus = cursor.getInt(5);
			int deviceHardwareVersion = cursor.getInt(6);
			int deviceSoftwareVersion = cursor.getInt(7);
			String broadcastName = cursor.getString(8);
			String deviceVersion = cursor.getString(9);
			uuid = uuid.replace("$", "");
			
			DeviceBean bean = new DeviceBean(name,address,uuid,verifyStatus);
			bean.setDevicebroadcastName(broadcastName);
			bean.setDeviceVersion(deviceVersion);
			
			Utils.showLogD("dbId:" + dbId + "    name:" + name + "    address:" + address 
					+ "    uuid:" + uuid + "    verifyStatus:" + verifyStatus 
					+ "    deviceHardwareVersion:" + deviceHardwareVersion + "    deviceSoftwareVersion:" + deviceSoftwareVersion
					+ "    broadcastName:" + broadcastName + "    deviceVersion:" + deviceVersion);
			
			dataList.add(bean);
		}
		
		if (cursor != null) {
	        cursor.close();
	    }
		
		myDb.close();
		return dataList;
	}
}