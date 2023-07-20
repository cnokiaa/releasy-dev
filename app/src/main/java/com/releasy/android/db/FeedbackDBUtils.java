package com.releasy.android.db;

import java.util.ArrayList;
import java.util.List;

import com.releasy.android.bean.FeedbackBean;
import com.releasy.android.bean.MusicBean;
import com.releasy.android.constants.Constants;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FeedbackDBUtils {

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
	public static void insertData(ReleasyDatabaseHelper myDb, int uid, String data, String msg, int source){
		SQLiteDatabase db = myDb.getReadableDatabase();
		db.execSQL("insert into Feedback values(null, ?, ?, ?, ?)"
				, new Object[]{uid, data, msg, source});
		myDb.close();
	}
	
	/**
	 * 删除分类 
	 */
	public static void deleteData(ReleasyDatabaseHelper myDb){
		SQLiteDatabase db = myDb.getReadableDatabase();
		
		db.delete("Feedback", "source<?", new String[]{"2"});
		myDb.close();
	}
	
	/**
	 * 查询特定Uid意见反馈数据列表 
	 * 返回List<MusicBean>
	 */
	public static List<FeedbackBean> searchRoomMusicData(ReleasyDatabaseHelper myDb, int uid){
		List<FeedbackBean> dataList = new ArrayList<FeedbackBean>();
		
		SQLiteDatabase db = myDb.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from Feedback where uid = \"" + uid + "\" ORDER BY id DESC", null);
		while(cursor.moveToNext()){
			int dbId = cursor.getInt(0);
			int uId = cursor.getInt(1);
			String data = cursor.getString(2);
			String msg = cursor.getString(3);
			int source = cursor.getInt(4);
			
			FeedbackBean bean = new FeedbackBean(data,msg,source);
			//Log.d("z17m","dbId:" + dbId + "    data:" + data + "    msg:" + msg +"    source:" + source);
			
			dataList.add(bean);
		}
		
		if (cursor != null) {
	        cursor.close();
	    }
		
		myDb.close();
		return dataList;
	}
}
