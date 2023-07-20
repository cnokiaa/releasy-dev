package com.releasy.android.db;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.releasy.android.bean.RunTimeBean;
import com.releasy.android.bean.UserRecordBean;
import com.releasy.android.constants.Constants;
import com.releasy.android.utils.Utils;


public class UserRecordDBUtils {

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
	public static void insertData(ReleasyDatabaseHelper myDb, String date, int totalRunTime, String actionRunRecord){
		Utils.showLogD("-------------------insertData------------------");
		
		UserRecordBean userRecordBean = isExist(myDb,date);
		
		if(userRecordBean != null){
			int time = totalRunTime + userRecordBean.getTotalRunTime();
			List<RunTimeBean> actionRunList = arrayStringToRunTimeList(actionRunRecord);
			for(int i = 0; i < actionRunList.size(); i++){
				if(actionRunList.get(i).getActionId() == userRecordBean.getActinRunTimeList().get(i).getActionId() ){
					actionRunList.get(i).setRunTime(actionRunList.get(i).getRunTime() 
							+ userRecordBean.getActinRunTimeList().get(i).getRunTime());
				}
			}
			
			
			updataData(myDb,date,time,runTimeListToArrayString(actionRunList));
			return ;
		}
		
		SQLiteDatabase db = myDb.getReadableDatabase();
		db.execSQL("insert into UserRecord values(null, ?, ?, ?)"
				, new Object[]{date, totalRunTime, actionRunRecord});
		myDb.close();
	}
	
	/**
	 * 判断是否存在
	 */
	public static UserRecordBean isExist(ReleasyDatabaseHelper myDb, String date){
		UserRecordBean bean = null ;
		SQLiteDatabase db = myDb.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from UserRecord where date = \"" + date + "\"", null);
		
		if(cursor == null){
			return bean;
		}
		
		while(cursor.moveToNext()){
			int dbId = cursor.getInt(0);
			String data = cursor.getString(1);
			int totalRunTime = cursor.getInt(2);
			String actinRunTimeRecord = cursor.getString(3);
			
			
			bean = new UserRecordBean(data,totalRunTime,arrayStringToRunTimeList(actinRunTimeRecord));
			//Log.d("z17m","dbId:" + dbId + "    roomId:" + roomId + "    roomName:" + roomName
			//		+ "    roomType:" + roomType + "    roomUrl:" + roomUrl);
		}
		
		if (cursor != null) {
	        cursor.close();
	    }
		
		myDb.close();
		return bean;
	}
	
	/**
	 * 删除某个记录
	 */
	public static void deleteData(ReleasyDatabaseHelper myDb, String date){
		SQLiteDatabase db = myDb.getReadableDatabase();
		db.delete("UserRecord", "date" +"=\"" + date +"\"" , null);
		myDb.close();
	}
	
	/**
	 * 删除全部记录
	 */
	public static void deleteAllData(ReleasyDatabaseHelper myDb){
		SQLiteDatabase db = myDb.getReadableDatabase();
		db.delete("UserRecord", "totalRunTime>?", new String[]{"-1"});
		myDb.close();
	}
	
	/**
	 * 更新记录
	 */
	public static void updataData(ReleasyDatabaseHelper myDb, String date, int totalRunTime, String actionRunRecord){
		Utils.showLogD("-------------------updataData------------------");
		Utils.showLogD("updata date : " + date);
		Utils.showLogD("updata totalRunTime : " + totalRunTime);
		Utils.showLogD("updata actionRunRecord : " + actionRunRecord);
		
		SQLiteDatabase db = myDb.getReadableDatabase();
		db.execSQL("update UserRecord set totalRunTime=\"" + totalRunTime + "\" , actionRunRecord =\"" + actionRunRecord + "\""+
				" where date = \"" + date + "\"" );
		myDb.close();
	}
	
	/**
	 * 查询所有数据 
	 * 返回List<DeviceBean>
	 */
	public static List<UserRecordBean> searchAllData(ReleasyDatabaseHelper myDb, Context context){
		List<UserRecordBean> dataList = new ArrayList<UserRecordBean>();
		
		SQLiteDatabase db = myDb.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from UserRecord ORDER BY date ASC", null);
		while(cursor.moveToNext()){
			int dbId = cursor.getInt(0);
			String date = cursor.getString(1);
			int totalRunTime = cursor.getInt(2);
			String actinRunRecord = cursor.getString(3);

			UserRecordBean bean = new UserRecordBean(date,totalRunTime,arrayStringToRunTimeList(actinRunRecord));
			Log.d("z17m","searchAllData ----- dbId:" + dbId + "    date:" + date + "    totalRunTime:" + totalRunTime
					+ "    actinRunRecord:" + actinRunRecord);
			
			dataList.add(bean);
		}
		
		if (cursor != null) {
	        cursor.close();
	    }
		
		myDb.close();
		return dataList;
	}
	
	private static List<RunTimeBean> arrayStringToRunTimeList(String dataStr){
		List<RunTimeBean> runTimeList = new ArrayList<RunTimeBean>();
		
		String[]arr = dataStr.split(";");

		for(int i = 0; i < arr.length; i++){
			String[] arr2 = arr[i].split(",");
			
			RunTimeBean bean = new RunTimeBean(Integer.parseInt(arr2[0]),Integer.parseInt(arr2[1]));
			runTimeList.add(bean);
		
		}
		
		return runTimeList;
	}
	
	private static String runTimeListToArrayString(List<RunTimeBean> runTimeList){
		String actionRunList = "";
		
		
		for(int i = 0; i < runTimeList.size(); i++){
			actionRunList = actionRunList + runTimeList.get(i).getActionId() + ","
			                + runTimeList.get(i).getRunTime() + ";";
		}
		
		return actionRunList;
	}
}
