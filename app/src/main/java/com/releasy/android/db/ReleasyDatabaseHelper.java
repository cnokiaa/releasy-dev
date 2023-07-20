package com.releasy.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class ReleasyDatabaseHelper extends SQLiteOpenHelper{

	final String CREATE_DEVICE_SQL = 
		"create table Device(id integer primary key autoincrement, name String, address String, uuid String" +
		", delStatus integer, verifyStatus integer, deviceHardwareVersion integer, deviceSoftwareVersion integer" + 
		", broadcastName String,  deviceVersion String)";
	
	final String CREATE_ROOM_SQL = 
		"create table Room(id integer primary key autoincrement, roomId integer, roomName String" +
		", roomType integer, roomUrl String, roomBelong integer)";
	
	final String CREATE_ACTION_SQL = 
		"create table Action(id integer primary key autoincrement, actionID integer, roomID integer" +
		", actionName String, actionType integer, actionPic String, bytesCheck int, highTime String" +
		", lowTime String, innerHighAndLow String, period String, interval String, minRate String" + 
		", maxRate String, powerLV String, maxWorkTime integer, strength integer)";
	
	final String CREATE_ACTION_FOE_M2_SQL = 
		"create table ActionForM2(id integer primary key autoincrement, actionID integer, roomID integer" +
		", actionName String, actionType integer, actionPic String, checkChangeMode String, highTime String" +
		", lowTime String, innerHighAndLow String, period String, interval String, minRate String, maxRate String" +
		", powerLV String, minMax String, mode12 String, maxWorkTime integer, strength integer)";
	
	final String CREATE_MUSIC_SQL = 
		"create table Music(id integer primary key autoincrement, roomId integer, musicId integer" +
		", musicName String, musicPic String, musicPath String, musicArtist String)";
	
	final String CREATE_FILE_DOWNLOAD_SQL = 
		"create table FileDownlog(id integer primary key autoincrement, downpath varchar(100), threadid INTEGER, downlength INTEGER)";
	
	final String CREATE_FEEDBACK_SQL = 
		"create table Feedback(id integer primary key autoincrement,uid INTEGER, data String, msg String, source INTEGER)";
	
	final String USER_RECORD_SQL = 
		"create table UserRecord(id integer primary key autoincrement,date String, totalRunTime INTEGER, actionRunRecord String)";
	
	
	public ReleasyDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_DEVICE_SQL);
		db.execSQL(CREATE_ROOM_SQL);
		db.execSQL(CREATE_ACTION_SQL);
		db.execSQL(CREATE_MUSIC_SQL);
		db.execSQL(CREATE_FILE_DOWNLOAD_SQL);
		db.execSQL(CREATE_FEEDBACK_SQL);
		
		db.execSQL(CREATE_ACTION_FOE_M2_SQL);
		db.execSQL(USER_RECORD_SQL);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("z17m", "onUpgrade" + db.getVersion());
		
		/*if(oldVersion == 1){
			//db.execSQL("delete from Device");
			
			db.execSQL("ALTER TABLE Device ADD deviceHardwareVersion integer");
			db.execSQL("ALTER TABLE Device ADD deviceSoftwareVersion integer");
			
			//20160923
			db.execSQL("ALTER TABLE Device ADD broadcastName String");
			db.execSQL("ALTER TABLE Device ADD deviceVersion String");
			
			//20161008
			db.execSQL(CREATE_ACTION_FOE_M2_SQL);
		}
		else if(oldVersion == 2){
			//20160923
			db.execSQL("ALTER TABLE Device ADD broadcastName String");
			db.execSQL("ALTER TABLE Device ADD deviceVersion String");
			
			//20161008
			db.execSQL(CREATE_ACTION_FOE_M2_SQL);
			
		}*/
		
		if(oldVersion < 3){
			db.execSQL("DROP TABLE Device");  
			db.execSQL("DROP TABLE Room");  
			db.execSQL("DROP TABLE Action");  
			db.execSQL("DROP TABLE Music");  
			db.execSQL("DROP TABLE FileDownlog"); 
			db.execSQL("DROP TABLE Feedback"); 
			
			
			db.execSQL(CREATE_DEVICE_SQL);
			db.execSQL(CREATE_ROOM_SQL);
			db.execSQL(CREATE_ACTION_SQL);
			db.execSQL(CREATE_MUSIC_SQL);
			db.execSQL(CREATE_FILE_DOWNLOAD_SQL);
			db.execSQL(CREATE_FEEDBACK_SQL);
			
			db.execSQL(CREATE_ACTION_FOE_M2_SQL);
			db.execSQL(USER_RECORD_SQL);
		}
		
	}
}
