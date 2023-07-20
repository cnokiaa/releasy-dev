package com.releasy.android.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.releasy.android.bean.MusicBean;
import com.releasy.android.constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class MusicDBUtils {

    /**
     * 获取DatabaseHelper对象
     */
    public static ReleasyDatabaseHelper openData(Context context) {
        ReleasyDatabaseHelper db = new ReleasyDatabaseHelper(context, "releasy.db3", null, Constants.DB_VERSION);
        return db;
    }

    /**
     * 插入数据
     */
    public static void insertData(ReleasyDatabaseHelper myDb, int roomId, int musicId, String musicName, String musicPic
            , String musicPath, String musicArtist) {
        SQLiteDatabase db = myDb.getReadableDatabase();
        db.execSQL("insert into Music values(null, ?, ?, ?, ?, ?, ?)"
                , new Object[]{roomId, musicId, musicName, musicPic, musicPath, musicArtist});
        myDb.close();
    }

    /**
     * 判断是否存在
     */
    public static boolean isMusicExist(ReleasyDatabaseHelper myDb, int musicId) {
        SQLiteDatabase db = myDb.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from Music where musicId = \"" + musicId + "\"", null);

        if (cursor != null) {
            //Log.d("z17m","cursor.getCount() = " + cursor.getCount());
            if (cursor.getCount() > 0) {
                //Log.d("z17m","Address is exist");
                return true;
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        //Log.d("z17m","Address is not exist");
        return false;

    }

    /**
     * 判断是否存在
     */
    public static boolean isISOMusicExist(ReleasyDatabaseHelper myDb, int roomId) {
        SQLiteDatabase db = myDb.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from Music where roomId = \"" + roomId + "\""
                + " AND musicId = \"" + 0 + "\"", null);

        if (cursor != null) {
            //Log.d("z17m","cursor.getCount() = " + cursor.getCount());
            if (cursor.getCount() > 0) {
                //Log.d("z17m","Address is exist");
                return true;
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        //Log.d("z17m","Address is not exist");
        return false;

    }

    /**
     * 删除特定音乐
     */
    public static void deleteData(ReleasyDatabaseHelper myDb, int musicId) {
        SQLiteDatabase db = myDb.getReadableDatabase();
        db.delete("Music", "musicId" + "=\"" + musicId + "\"", null);
        myDb.close();
    }

    /**
     * 删除房间音乐
     */
    public static void deleteRoomMuisc(ReleasyDatabaseHelper myDb, int roomId) {
        SQLiteDatabase db = myDb.getReadableDatabase();
        int row = db.delete("Music", "roomId" + "=\"" + roomId + "\"", null);
        Log.d("z17m", "deleteRoomMuisc: " + row);
        myDb.close();
    }

    /**
     * 查询特定放松馆音乐数据列表
     * 返回List<MusicBean>
     */
    public static List<MusicBean> searchRoomMusicData(ReleasyDatabaseHelper myDb, int room_Id) {
        List<MusicBean> dataList = new ArrayList<MusicBean>();

        SQLiteDatabase db = myDb.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from Music where roomId = \"" + room_Id + "\"", null);
        while (cursor.moveToNext()) {
            int dbId = cursor.getInt(0);
            int roomId = cursor.getInt(1);
            int musicId = cursor.getInt(2);
            String musicName = cursor.getString(3);
            String musicPic = cursor.getString(4);
            String musicPath = cursor.getString(5);
            String musicArtist = cursor.getString(6);

            MusicBean bean = new MusicBean(roomId, musicId, musicName, musicPic, musicPath, musicArtist);
            //Log.d("z17m","dbId:" + dbId + "    musicName:" + musicName + "    musicId:" + musicId);

            dataList.add(bean);
        }

        if (cursor != null) {
            cursor.close();
        }

        myDb.close();
        return dataList;
    }

    /**
     * 查询特定放松馆音乐数据列表
     * 返回List<MusicBean>
     */
    public static List<MusicBean> searchRoomFristMusicData(ReleasyDatabaseHelper myDb, int room_Id) {
        List<MusicBean> dataList = new ArrayList<MusicBean>();

        SQLiteDatabase db = myDb.getReadableDatabase();
        Cursor cursor = db.rawQuery("select MIN(id) from Music where roomId = \"" + room_Id + "\"", null);
        if (cursor == null || cursor.getCount() == 0) {
            return dataList;
        }

        while (cursor.moveToNext()) {
            int dbId = cursor.getInt(0);
            int roomId = cursor.getInt(1);
            int musicId = cursor.getInt(2);
            String musicName = cursor.getString(3);
            String musicPic = cursor.getString(4);
            String musicPath = cursor.getString(5);
            String musicArtist = cursor.getString(6);

            MusicBean bean = new MusicBean(roomId, musicId, musicName, musicPic, musicPath, musicArtist);
            //Log.d("z17m","dbId:" + dbId + "    name:" + name + "    address:" + address);
            dataList.add(bean);
        }

        if (cursor != null) {
            cursor.close();
        }

        myDb.close();
        return dataList;
    }


    /**
     * 查询全部音乐数据列表
     * 返回List<MusicBean>
     */
    public static List<MusicBean> searchAllData(ReleasyDatabaseHelper myDb) {
        List<MusicBean> dataList = new ArrayList<MusicBean>();

        SQLiteDatabase db = myDb.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from Music", null);
        while (cursor.moveToNext()) {
            int dbId = cursor.getInt(0);
            int roomId = cursor.getInt(1);
            int musicId = cursor.getInt(2);
            String musicName = cursor.getString(3);
            String musicPic = cursor.getString(4);
            String musicPath = cursor.getString(5);
            String musicArtist = cursor.getString(6);

            MusicBean bean = new MusicBean(roomId, musicId, musicName, musicPic, musicPath, musicArtist);
            //Log.d("z17m","dbId:" + dbId + "    roomId:" + roomId + "    musicId:" + musicId
            //		+ "    musicName:" + musicName + "    musicPic:" + musicPic
            //		+ "    musicPath:" + musicPath + "    musicArtist:" + musicArtist);

            dataList.add(bean);
        }

        if (cursor != null) {
            cursor.close();
        }

        myDb.close();
        return dataList;
    }

    /**
     * 更新音乐信息
     */
    public static void updataMusic(ReleasyDatabaseHelper myDb, int roomId, int musicId, String musicName, String musicPic
            , String musicPath, String musicArtist) {
        SQLiteDatabase db = myDb.getReadableDatabase();
        db.execSQL("update Music set musicName=\"" + musicName + "\" , musicPic =\"" + musicPic
                + "\" , musicPath =\"" + musicPath + "\" , musicArtist =\"" + musicArtist + "\"" +
                " where roomId = \"" + roomId + "\"");
        myDb.close();
    }

    /**
     * 更新音乐信息
     */
    public static void updataISOMusic(ReleasyDatabaseHelper myDb, int roomId, int musicId, String musicName, String musicPic
            , String musicPath, String musicArtist) {
        SQLiteDatabase db = myDb.getReadableDatabase();
        db.execSQL("update Music set musicName=\"" + musicName + "\" , musicPic =\"" + musicPic
                + "\" , musicPath =\"" + musicPath + "\" , musicArtist =\"" + musicArtist + "\"" +
                " where roomId = \"" + roomId + "\"");
        myDb.close();
    }

    /**
     * 查询最大的roomId
     * 数据 返回int
     */
    public static int searchMaxMusicIdData(ReleasyDatabaseHelper myDb) {
        int id = 0;
        SQLiteDatabase db = myDb.getReadableDatabase();
        Cursor cursor = db.rawQuery("select MAX(musicId) from Music", null);
        if (cursor == null || cursor.getCount() == 0) {
            return 20000;
        }

        while (cursor.moveToNext()) {
            int musicId = cursor.getInt(0);
            if (musicId <= 20000)
                id = 20000;
            else
                id = musicId;
        }

        if (cursor != null) {
            cursor.close();
        }

        myDb.close();
        return id;
    }
}
