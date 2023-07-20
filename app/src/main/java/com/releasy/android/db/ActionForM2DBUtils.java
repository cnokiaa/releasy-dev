package com.releasy.android.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.releasy.android.bean.ActionBean;
import com.releasy.android.constants.ActionConstants;
import com.releasy.android.constants.Constants;
import com.releasy.android.utils.StringUtils;
import com.releasy.android.utils.Utils;

public class ActionForM2DBUtils {

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
	public static void insertData(ReleasyDatabaseHelper myDb, int actionId, int roomId, String actionName
			, int actionType, String actionPic, int[] checkChangeMode, int[] highTime, int[] lowTime
			, int[] innerHighAndLow, int[] period, int[] interval, int[] minRate, int[] maxRate
			, int[][] powerLV, int[] minMax, int[] mode12, int maxWorkTime, int strength){
		SQLiteDatabase db = myDb.getReadableDatabase();
		db.execSQL("insert into ActionForM2 values(null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
				, new Object[]{actionId, roomId, actionName, actionType, actionPic, Utils.arrayInt2String(checkChangeMode)
						, Utils.arrayInt2String(highTime), Utils.arrayInt2String(lowTime), Utils.arrayInt2String(innerHighAndLow)
						, Utils.arrayInt2String(period), Utils.arrayInt2String(interval), Utils.arrayInt2String(minRate)
						, Utils.arrayInt2String(maxRate), Utils.twodimensionalArrayToArrayString(powerLV)
						, Utils.arrayInt2String(minMax), Utils.arrayInt2String(mode12)
						, maxWorkTime, strength});
		myDb.close();
	}
	
	/**
	 * 删除某个动作
	 */
	public static void deleteActionData(ReleasyDatabaseHelper myDb, int actionId){
		SQLiteDatabase db = myDb.getReadableDatabase();
		db.delete("ActionForM2", "actionId" +"=\"" + actionId +"\"" , null);
		myDb.close();
	}
	
	/**
	 * 删除某个馆的动作
	 */
	public static void deleteRoomActionData(ReleasyDatabaseHelper myDb, int roomId){
		SQLiteDatabase db = myDb.getReadableDatabase();
		db.delete("ActionForM2", "roomId" +"=\"" + roomId +"\"" , null);
		myDb.close();
	}
	
	/**
	 * 删除所有动作
	 */
	public static void deleteAllData(ReleasyDatabaseHelper myDb){
		SQLiteDatabase db = myDb.getReadableDatabase();
		db.delete("ActionForM2", "roomId<?", new String[]{"20000"});
		myDb.close();
	}
	
	/**
	 * 查询特定放松馆的按摩动作数据 返回List<ActionBean>
	 */
	public static List<ActionBean> searchRoomActionData(ReleasyDatabaseHelper myDb, int room_Id, Context context){
		List<ActionBean> dataList = new ArrayList<ActionBean>();
		
		SQLiteDatabase db = myDb.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from ActionForM2 where roomId = \"" + room_Id + "\"", null);
		
		while(cursor.moveToNext()){
			dataList.add(getAllInfoAction(cursor,context));
		}
		
		if (cursor != null) {
	        cursor.close();
	    }
		
		myDb.close();
		return dataList;
	}
	
	/**
	 * 查询基础动作放松馆的按摩动作数据 返回List<ActionBean>
	 */
	public static List<ActionBean> searchBaseActionData(ReleasyDatabaseHelper myDb,Context context){
		List<ActionBean> dataList = new ArrayList<ActionBean>();
		
		SQLiteDatabase db = myDb.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from ActionForM2 where actionType = \"" + 0 + "\"", null);
		
		while(cursor.moveToNext()){
			dataList.add(getPartInfoAction(cursor,context));
		}
		
		if (cursor != null) {
	        cursor.close();
	    }
		
		myDb.close();
		return dataList;
	}
	
	/**
	 * 查询场景动作放松馆的按摩动作数据 返回List<ActionBean>
	 */
	public static List<ActionBean> searchSceneActionData(ReleasyDatabaseHelper myDb,Context context){
		List<ActionBean> dataList = new ArrayList<ActionBean>();
		
		SQLiteDatabase db = myDb.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from ActionForM2 where actionType = \"" + 1 + "\"", null);
		
		while(cursor.moveToNext()){
			dataList.add(getPartInfoAction(cursor,context));
		}
		
		if (cursor != null) {
	        cursor.close();
	    }
		
		myDb.close();
		return dataList;
	}
	
	/**
	 * 查询特定动作放松馆的按摩动作数据 返回List<ActionBean>
	 */
	public static ActionBean searchDBIdActionData(ReleasyDatabaseHelper myDb, int dbId, Context context){
		ActionBean data = null;
		
		SQLiteDatabase db = myDb.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from ActionForM2 where id = \"" + dbId + "\"", null);
		
		while(cursor.moveToNext()){
			data = getAllInfoAction(cursor,context);
		}
		
		if (cursor != null) {
	        cursor.close();
	    }
		
		myDb.close();
		return data;
	}
	
	/**
	 * 获取动作部分数据信息
	 * @param cursor
	 * @return
	 */
	private static ActionBean getPartInfoAction(Cursor cursor,Context context){
		int dbId = cursor.getInt(0);
		int actionId = cursor.getInt(1);
		int roomId = cursor.getInt(2);
		String actionName = cursor.getString(3);
		int actionType = cursor.getInt(4);
		String actionPic = cursor.getString(5);
		
		String name = ActionConstants.getActionName(actionId,context);
		if(!StringUtils.isBlank(name))
			actionName = name;
		
		ActionBean bean = new ActionBean(dbId,actionId,roomId,actionName,actionType);
		bean.setActionPicUrl(actionPic);
		
		/*Log.d("z17m","dbId:" + dbId + "    actionId:" + actionId + "    roomId:" + roomId
				+ "    actionName:" + actionName + "    actionType:" + actionType);*/
		
		return bean;
	}
	
	/**
	 * 获取动作全部数据信息
	 * @param cursor
	 * @return
	 */
	private static ActionBean getAllInfoAction(Cursor cursor,Context context){
		int dbId = cursor.getInt(0);
		int actionId = cursor.getInt(1);
		int roomId = cursor.getInt(2);
		String actionName = cursor.getString(3);
		int actionType = cursor.getInt(4);
		String actionPic = cursor.getString(5);
		String checkChangeMode = cursor.getString(6);
		String highTime = cursor.getString(7);
		String lowTime = cursor.getString(8);
		String innerHighAndLow = cursor.getString(9);
		String period = cursor.getString(10);
		String interval = cursor.getString(11);
		String minRate = cursor.getString(12);
		String maxRate = cursor.getString(13);
		String powerLV = cursor.getString(14);
		String minMax = cursor.getString(15);
		String mode12 = cursor.getString(16);
		int maxWorkTime = cursor.getInt(17);
		int strength = cursor.getInt(18);
		
		String name = ActionConstants.getActionName(actionId,context);
		if(!StringUtils.isBlank(name))
			actionName = name;
		
		ActionBean bean = new ActionBean(actionId,roomId,actionName,actionType);
		bean.setDBId(dbId);
		bean.setActionPicUrl(actionPic);
		
		bean.setCheckChangeMode(Utils.arrayString2Int(checkChangeMode));
		bean.setHighTime(Utils.arrayString2Int(highTime));
		bean.setLowTime(Utils.arrayString2Int(lowTime));
		bean.setInnerHighAndLow(Utils.arrayString2Int(innerHighAndLow));
		bean.setPeriod(Utils.arrayString2Int(period));
		bean.setInterval(Utils.arrayString2Int(interval));
		bean.setRateMin(Utils.arrayString2Int(minRate));
		bean.setRateMax(Utils.arrayString2Int(maxRate));
		bean.setPowerLV(Utils.arrayStringTo2dimensionalArray(powerLV));
		bean.setMinMaxList(Utils.arrayString2Int(minMax));
		bean.setMode12List(Utils.arrayString2Int(mode12));
		bean.setMaxWorkTime(maxWorkTime);
		
		bean.setStrength(strength);
		
		Log.d("z17m","dbId:" + dbId + "    actionId:" + actionId + "    roomId:" + roomId
				+ "    actionName:" + actionName + "    actionType:" + actionType);
		//Log.d("z17m","bytesCheck : " + bytesCheck);
		//Log.d("z17m","highTime : "); 
		//Utils.arrayString2Int(highTime);
		//Log.d("z17m","lowTime : " );
		//Utils.arrayString2Int(lowTime);
		//Log.d("z17m","innerHighAndLow : " );
		//Utils.arrayString2Int(innerHighAndLow);
		//Log.d("z17m","period : ");
		//Utils.arrayString2Int(period);
		//Log.d("z17m","interval : " );
		//Utils.arrayString2Int(interval);
		//Log.d("z17m","minRate : " );
		//Utils.arrayString2Int(minRate);
		//Log.d("z17m","maxRate : " );
		//Utils.arrayString2Int(maxRate);
		Log.d("z17m","powerLV : " );
		Utils.arrayStringTo2dimensionalArray(powerLV);
		//Log.d("z17m","strength : " + strength);
		
		return bean;
	}
	
	/**
	 * 查询全部放松馆的按摩动作数据 返回List<ActionBean>
	 */
	/*public static List<ActionBean> searchAllData(ReleasyDatabaseHelper myDb){
		List<ActionBean> dataList = new ArrayList<ActionBean>();
		
		SQLiteDatabase db = myDb.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from ActionForM2", null);
		
		while(cursor.moveToNext()){
			int dbId = cursor.getInt(0);
			int actionId = cursor.getInt(1);
			int roomId = cursor.getInt(2);
			String actionName = cursor.getString(3);
			int actionType = cursor.getInt(4);
			String actionPic = cursor.getString(5);
			String checkChangeMode = cursor.getString(6);
			String highTime = cursor.getString(7);
			String lowTime = cursor.getString(8);
			String innerHighAndLow = cursor.getString(9);
			String period = cursor.getString(10);
			String interval = cursor.getString(11);
			String minRate = cursor.getString(12);
			String maxRate = cursor.getString(13);
			String powerLV = cursor.getString(14);
			String minMax = cursor.getString(15);
			String mode12 = cursor.getString(16);
			int maxWorkTime = cursor.getInt(17);
			int strength = cursor.getInt(18);
			
			//ActionBean bean = new ActionBean(actionId,roomId,actionName,actionType);
			Log.d("z17m","dbId:" + dbId + "    actionId:" + actionId + "    roomId:" + roomId
					+ "    actionName:" + actionName + "    actionType:" + actionType);
			Log.d("z17m","checkChangeMode : ");
			Utils.arrayString2Int(checkChangeMode);
			Log.d("z17m","highTime : ");
			Utils.arrayString2Int(highTime);
			Log.d("z17m","lowTime : " );
			Utils.arrayString2Int(lowTime);
			Log.d("z17m","innerHighAndLow : " );
			Utils.arrayString2Int(innerHighAndLow);
			Log.d("z17m","period : ");
			Utils.arrayString2Int(period);
			Log.d("z17m","interval : " );
			Utils.arrayString2Int(interval);
			Log.d("z17m","minRate : " );
			Utils.arrayString2Int(minRate);
			Log.d("z17m","maxRate : " );
			Utils.arrayString2Int(maxRate);
			Log.d("z17m","powerLV : " );
			Utils.arrayStringTo2dimensionalArray(powerLV);
			Log.d("z17m","minMax : " );
			Utils.arrayString2Int(minMax);
			Log.d("z17m","mode12 : " );
			Utils.arrayString2Int(mode12);
			
			Log.d("z17m","maxWorkTime : " + maxWorkTime);
			Log.d("z17m","strength : " + strength);
			//dataList.add(bean);
		}
		
		if (cursor != null) {
	        cursor.close();
	    }
		
		myDb.close();
		return dataList;
	}*/
	
}
