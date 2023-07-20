package com.releasy.android;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.releasy.android.activity.main.BaseActivity;
import com.releasy.android.activity.main.MainTabActivity;
import com.releasy.android.bean.ActionBean;
import com.releasy.android.bean.MusicBean;
import com.releasy.android.bean.RoomBean;
import com.releasy.android.constants.ActionConstants;
import com.releasy.android.constants.ActionForM2Constants;
import com.releasy.android.constants.HttpConstants;
import com.releasy.android.constants.MusicConstants;
import com.releasy.android.constants.RoomConstants;
import com.releasy.android.db.ActionDBUtils;
import com.releasy.android.db.ActionForM2DBUtils;
import com.releasy.android.db.MusicDBUtils;
import com.releasy.android.db.ReleasyDatabaseHelper;
import com.releasy.android.db.RoomDBUtils;
import com.releasy.android.dialog.PrivacyDialogFragment;
import com.releasy.android.http.HttpUtils;
import com.releasy.android.http.ResolveJsonUtils;
import com.releasy.android.utils.SharePreferenceUtils;
import com.releasy.android.utils.StringUtils;
import com.releasy.android.utils.UpdataUserRecordThread;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 启动页面
 * 检测蓝牙版本  检测按摩动作更新都在此页面
 *
 * @author Lighting.Z
 */
public class MainActivity extends BaseActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private SharePreferenceUtils spInfo;              //SharePreference
    private ReleasyDatabaseHelper db;                 //数据库
    private ReleasyApplication app;                   //Application
    String[] perms = {
            Permission.ACCESS_FINE_LOCATION,
            Permission.ACCESS_COARSE_LOCATION,
            Permission.BLUETOOTH_SCAN,
            Permission.BLUETOOTH_CONNECT,
            Permission.BLUETOOTH_ADVERTISE,
            Permission.READ_EXTERNAL_STORAGE,
            Permission.WRITE_EXTERNAL_STORAGE
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //SDKAppInit.createInstance(this);
        app = (ReleasyApplication) this.getApplication();
        spInfo = new SharePreferenceUtils(this);
        toCheckLog();
        toCheckUserRecord();
        if (toCheckIsFristOpen()) {
            privacyDialogFragment();
        } else {
            requestPermission();
        }
    }

    protected void privacyDialogFragment() {
        PrivacyDialogFragment privacyDialogFragment = PrivacyDialogFragment.newInstance();
        privacyDialogFragment.setListener(new PrivacyDialogFragment.OnInteractionListener() {
            @Override
            public void cancel() {
                finish();
            }

            @Override
            public void confirm() {
                requestPermission();
                spInfo.setIsV_20FristOpen(false);
            }
        });
        privacyDialogFragment.show(getSupportFragmentManager(), "PrivacyDialogFragment");
    }

    private void requestPermission() {
        XXPermissions.with(this)
                .permission(perms)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            toCheckBLE();
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        OnPermissionCallback.super.onDenied(permissions, never);
                        Toast.makeText(MainActivity.this, R.string.prompt_sdk23, Toast.LENGTH_SHORT).show();
                        showDialog();
                    }
                });
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.prompt_sdk23);
        builder.setTitle(R.string.prompt);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                startActivityForResult(intent, 201); //此为设置完成后返回到获取界面
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                toLoadInitialData();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    protected void initViews() {
    }

    protected void initEvents() {
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }

        showLogD("onActivityResult");
        toLoadInitialData();
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 检测蓝牙
     */
    private void toCheckBLE() {

        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {

                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, R.string.prompt_sdk23, Toast.LENGTH_SHORT).show();
                    toLoadInitialData();
                    return;
                }
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            showLogD("toCheckBLE else");
            toLoadInitialData();
        }
    }

    /**
     * 检查是否第一次打开app
     *
     * @return 返回true为第一次打开  返回false为不是第一次打开
     */
    private boolean toCheckIsFristOpen() {
        return spInfo.getIsV_20FristOpen();
    }

    /**
     * 检查是否第一次打开app
     *
     * @return 返回true为第一次打开  返回false为不是第一次打开
     */
    private boolean toCheckLoadInitDB() {
        return spInfo.getIsV_20LoadInitDB();
    }

    /**
     * 加载初始数据到数据库中
     */
    private void toLoadInitialData() {
        showLogD("toLoadInitialData");
        if (!toCheckLoadInitDB()) {
            showLogD("toLoadInitialData Start MainAsyncTask");
            MainAsyncTask mainAsyncTask = new MainAsyncTask();
            putAsyncTask(mainAsyncTask);
            new OutTimeThread(mainAsyncTask).start();
            return;
        }

        showLogD("insertData!");
        db = RoomDBUtils.openData(this);
        List<RoomBean> roomList = RoomConstants.getInitialRoomList(this);

        for (int i = 0; i < roomList.size(); i++) {
            RoomBean bean = roomList.get(i);
            RoomDBUtils.insertData(db, bean.getRoomId(), bean.getRoomName()
                    , bean.getRoomType(), bean.getRoomPic(), bean.getRoomBelong());
        }

        //RoomDBUtils.searchAllData(db,this);

        List<ActionBean> actionList = ActionConstants.getInitialActionList(this);
        for (int i = 0; i < actionList.size(); i++) {
            ActionBean bean = actionList.get(i);

            ActionDBUtils.insertData(db, bean.getActionId(), bean.getRoomId()
                    , bean.getActionName(), bean.getActionType(), bean.getActionPicUrl()
                    , bean.getBytesCheck(), bean.getHighTime(), bean.getLowTime()
                    , bean.getInnerHighAndLow(), bean.getPeriod(), bean.getInterval()
                    , bean.getRateMin(), bean.getRateMax(), bean.getPowerLV(), bean.getMaxWorkTime()
                    , bean.getStrength());
        }

        //ActionDBUtils.searchAllData(db);

        List<ActionBean> actionForM2List = ActionForM2Constants.getInitialActionList(this);
        for (int i = 0; i < actionForM2List.size(); i++) {
            ActionBean bean = actionForM2List.get(i);
            ActionForM2DBUtils.insertData(db, bean.getActionId(), bean.getRoomId()
                    , bean.getActionName(), bean.getActionType(), bean.getActionPicUrl()
                    , bean.getCheckChangeMode(), bean.getHighTime(), bean.getLowTime()
                    , bean.getInnerHighAndLow(), bean.getPeriod(), bean.getInterval()
                    , bean.getRateMin(), bean.getRateMax(), bean.getPowerLV(), bean.getMinMaxList()
                    , bean.getMode12List(), bean.getMaxWorkTime(), bean.getStrength());
        }
        //ActionForM2DBUtils.searchAllData(db);File

        List<MusicBean> MusicList = MusicConstants.getInitialMusicList();
        for (int i = 0; i < MusicList.size(); i++) {
            MusicBean bean = MusicList.get(i);
            MusicDBUtils.insertData(db, bean.getRoomId(), bean.getMusicId(), bean.getName(), bean.getArtPath()
                    , bean.getFilePath(), bean.getArtist());
        }

        spInfo.setIsV_20LoadInitDB(false);
        toNextActivity(); //跳转
    }

    /**
     * 跳转到下一个页面
     * 若第一次使用跳转到注册
     * 否则跳转到主页
     */
    private void toNextActivity() {
//        if (toCheckIsFristOpen()) {
        //第一次进入时，跳转注册

        //Intent intent = new Intent(MainActivity.this,MainTabActivity.class);
        //startActivity(intent);
        //intent = new Intent(MainActivity.this,SearchDeviceActivity.class);
        //startActivity(intent);
        //spInfo.setIsFristOpen(false);
//            Intent intent = new Intent(MainActivity.this, InformationPerfectActivity.class);
//            startActivity(intent);
//            this.finish();
//        } else {
        //不是第一次进入，跳转主页
        Intent intent = new Intent(MainActivity.this, MainTabActivity.class);
        startActivity(intent);
        this.finish();
//        }

    }

    /**
     * 检测运行记录
     * 如果不为空
     * 则发送运行记录到服务器
     */
    private void toCheckLog() {
        if (!StringUtils.isBlank(spInfo.getLogout()) || spInfo.getUId() == 10000) {
            putAsyncTask(new AddOperationLogAsyncTask());
            showLogD("toCheckLog true");
        }

    }

    /**
     * 检测使用记录
     * 如果不为空
     * 则保存至数据库
     */
    private void toCheckUserRecord() {
        if (spInfo.getUserRecordRumTime() > 0) {
            String date = spInfo.getUserRecordData();
            int totalRunTime = spInfo.getUserRecordRumTime();
            String actionRunRecord = spInfo.getUserRecordActionRumTime();
            UpdataUserRecordThread thread = new UpdataUserRecordThread(this, date, totalRunTime, actionRunRecord);
            thread.start();
        }
    }

    /***************************************网络通信*****************************************/

    private class OutTimeThread extends Thread {
        AsyncTask<Void, Void, Boolean> asyncTask;

        public OutTimeThread(AsyncTask<Void, Void, Boolean> asyncTask) {
            this.asyncTask = asyncTask;
        }

        public void run() {
            try {
                asyncTask.get(15000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (TimeoutException e) {
                clearAsyncTask();
                toNextActivity();
                showLogD("OutTimeThread");
                e.printStackTrace();
            }
        }
    }

    /**
     * 请求参数封装
     */
    private List<NameValuePair> getNotifyParams() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("uid", spInfo.getUId() + ""));
        return params;
    }

    /**
     * 请求参数封装
     */
    private List<NameValuePair> getRegisterParams() {
        String phone = spInfo.getPhoneNum();
        showLogD("PhoneNum : " + phone);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("phone", phone));
        return params;
    }

    /**
     * 配置信息线程处理
     */
    private String releasyVersion;

    private class MainAsyncTask extends AsyncTask<Void, Void, Boolean> {

        protected Boolean doInBackground(Void... param) {
            if (spInfo.getUId() == 10000) {
                List<NameValuePair> registerParams = getRegisterParams();
                Bundle registerNotifyResult = HttpUtils.doPost(registerParams, HttpConstants.REGIST
                        , MainActivity.this, mScreenWidth, mScreenHeight);
                toRegisterNotifyResult(registerNotifyResult);
            }

            List<NameValuePair> appVParams = new ArrayList<NameValuePair>();
            Bundle appVersionResult = HttpUtils.doPost(appVParams, HttpConstants.CHECK_APP_VERSION
                    , MainActivity.this, mScreenWidth, mScreenHeight);
            toGetAppVersionResult(appVersionResult);

            //获取反馈通知
            List<NameValuePair> notifyParams = getNotifyParams();
            Bundle suggestionNotifyResult = HttpUtils.doPost(notifyParams, HttpConstants.GET_SUGGESTION_NOTIFY
                    , MainActivity.this, mScreenWidth, mScreenHeight);
            toGetSuggestionNotifyResult(suggestionNotifyResult);

            //放松馆版本号
            List<NameValuePair> versionParams = new ArrayList<NameValuePair>();
            Bundle releasyVersionResult = HttpUtils.doPost(versionParams, HttpConstants.GET_RELEASY_VERSION
                    , MainActivity.this, mScreenWidth, mScreenHeight);
            releasyVersion = toGetReleasyVersion(releasyVersionResult);

            return null;
        }

        protected void onPostExecute(Boolean result) {
            toNextActivity(); //跳转
			
			/*if(StringUtils.isBlank(releasyVersion)){
				toNextActivity(); //跳转
				return;
			}
			
			if(releasyVersion.equals(spInfo.getReleasyVersion()))
				toNextActivity(); //跳转
			else{
				showLogD("start GetRelaxRoomListAsyncTask");
				//putAsyncTask(new GetRelaxRoomListAsyncTask());/
				GetRelaxRoomListAsyncTask getRelaxRoomListAsyncTask = new GetRelaxRoomListAsyncTask();
	    		putAsyncTask(getRelaxRoomListAsyncTask);
	    		new OutTimeThread(getRelaxRoomListAsyncTask).start();
			}*/
        }
    }

    /**
     * 注册
     *
     * @param mResult
     * @return
     */
    private void toRegisterNotifyResult(Bundle mResult) {
        Bundle headBundle;
        int retStatus;
        String retMsg;

        if (mResult.getInt("code") != HttpConstants.SUCCESS) {
            retStatus = mResult.getInt("code");
            retMsg = getResources().getString(R.string.network_anomaly);
            return;
        }

        headBundle = ResolveJsonUtils.getJsonHead(mResult.getString("content"));
        retStatus = headBundle.getInt("retStatus");
        retMsg = headBundle.getString("retMsg");

        if (retStatus == HttpConstants.SUCCESS) {
            int uid = ResolveJsonUtils.regist(mResult.getString("content"));
            spInfo.setUId(uid);
            showLogD("toRegisterNotifyResult uid : " + uid);
            showLogD("spInfo uid : " + spInfo.getUId());
        }
    }

    /**
     * 获取App版本信息
     *
     * @param mResult
     * @return
     */
    private void toGetAppVersionResult(Bundle mResult) {
        Bundle headBundle;
        int retStatus;
        String retMsg;

        if (mResult.getInt("code") != HttpConstants.SUCCESS) {
            retStatus = mResult.getInt("code");
            retMsg = getResources().getString(R.string.network_anomaly);
            return;
        }

        headBundle = ResolveJsonUtils.getJsonHead(mResult.getString("content"));
        retStatus = headBundle.getInt("retStatus");
        retMsg = headBundle.getString("retMsg");
        showLogD("App版本信息   retStatus : " + retStatus + "    retMsg : " + retMsg);


        if (retStatus == HttpConstants.SUCCESS) {
            Bundle bundle = ResolveJsonUtils.getAppVersion(mResult.getString("content"));
            String message = bundle.getString("message");
            String updateStrategy = bundle.getString("updateStrategy");
            String downloadUrl = bundle.getString("downloadUrl");
            String newVersion = bundle.getString("newVersion");
            app.setAppUpdataInfo(message, downloadUrl, newVersion);

            showLogD("message : " + message + "    updateStrategy : " + updateStrategy
                    + "    downloadUrl : " + downloadUrl + "    newVersion : " + newVersion);
        }
    }

    /**
     * 获取反馈通知
     *
     * @param mResult
     * @return
     */
    private void toGetSuggestionNotifyResult(Bundle mResult) {
        Bundle headBundle;
        int retStatus;
        String retMsg;

        if (mResult.getInt("code") != HttpConstants.SUCCESS) {
            retStatus = mResult.getInt("code");
            retMsg = getResources().getString(R.string.network_anomaly);
            return;
        }

        headBundle = ResolveJsonUtils.getJsonHead(mResult.getString("content"));
        retStatus = headBundle.getInt("retStatus");
        retMsg = headBundle.getString("retMsg");
        showLogD("反馈通知   retStatus : " + retStatus + "    retMsg : " + retMsg);


        if (retStatus == HttpConstants.SUCCESS) {
            int notificationCount = ResolveJsonUtils.getSuggestionNotifyResult(mResult.getString("content"));
            spInfo.setHasFeedbackNotify(notificationCount > 0);

            spInfo.setNotificationCount(notificationCount);
            showLogD("反馈通知  :  " + notificationCount);
        }
    }

    /**
     * 获取放松馆版本号
     *
     * @param mResult
     * @return
     */
    private String toGetReleasyVersion(Bundle mResult) {
        Bundle headBundle;
        int retStatus;
        String retMsg;
        String releasyVersion = null;

        if (mResult.getInt("code") != HttpConstants.SUCCESS) {
            retStatus = mResult.getInt("code");
            retMsg = getResources().getString(R.string.network_anomaly);
            return releasyVersion;
        }

        headBundle = ResolveJsonUtils.getJsonHead(mResult.getString("content"));
        retStatus = headBundle.getInt("retStatus");
        retMsg = headBundle.getString("retMsg");
        showLogD("放松馆版本号   retStatus : " + retStatus + "    retMsg : " + retMsg);

        if (retStatus == HttpConstants.SUCCESS) {
            Bundle bundle = ResolveJsonUtils.getReleasyVersion(mResult.getString("content"));
            String updateLog = bundle.getString("updateLog");
            releasyVersion = bundle.getString("version");
            showLogD("放松馆版本号  :  " + releasyVersion);
        }

        return releasyVersion;
    }


    /**
     * 放松馆更新线程处理
     */
    private class GetRelaxRoomListAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private Bundle mResult;
        private Bundle headBundle;
        private int retStatus;
        private String retMsg;

        protected Boolean doInBackground(Void... param) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            Bundle mResult = HttpUtils.doPost(params, HttpConstants.GET_RELAX_ROOM_LIST
                    , MainActivity.this, mScreenWidth, mScreenHeight);
            if (mResult.getInt("code") != HttpConstants.SUCCESS) {
                retStatus = mResult.getInt("code");
                retMsg = getResources().getString(R.string.network_anomaly);
                return null;
            }

            headBundle = ResolveJsonUtils.getJsonHead(mResult.getString("content"));
            retStatus = headBundle.getInt("retStatus");
            retMsg = headBundle.getString("retMsg");
            showLogD("retStatus : " + retStatus + "    retMsg : " + retMsg);

            if (retStatus == HttpConstants.SUCCESS) {
                Bundle bundle = ResolveJsonUtils.getRelaxRoomList(mResult.getString("content"));
                int total = bundle.getInt("total");
                List<RoomBean> roomList = (List<RoomBean>) bundle.getSerializable("roomList");
                List<ActionBean> actionList = (List<ActionBean>) bundle.getSerializable("actionList");
				
				/*showLogD("Room List ! \n");
				for(int i = 0; i < roomList.size(); i++){
					RoomBean room = roomList.get(i);
					showLogD("room id : " + room.getRoomId());
					showLogD("room name : " + room.getRoomName());
					showLogD("room type : " + room.getRoomType());
					showLogD("room pic : " + room.getRoomPic());
					showLogD("\n");
				}
				
				showLogD("\n Action List ! \n");
				for(int i = 0; i < actionList.size(); i++){
					ActionBean action = actionList.get(i);
					showLogD("action room id : " + action.getRoomId());
					showLogD("action id : " + action.getActionId());
					showLogD("action name : " + action.getActionName());
					showLogD("action type : " + action.getActionType());
					showLogD("action pic : " + action.getActionPicUrl());
					
					showLogD("action bytesCheck : " + action.getBytesCheck());
					showLogD("action highTime : " + Utils.arrayInt2String(action.getHighTime()));
					showLogD("action lowTime : " + Utils.arrayInt2String(action.getLowTime()));
					showLogD("action innerHighAndLow : " + Utils.arrayInt2String(action.getInnerHighAndLow()));
					showLogD("action interval : " + Utils.arrayInt2String(action.getInterval()));
					showLogD("action period : " + Utils.arrayInt2String(action.getPeriod()));
					showLogD("action rateMax : " + Utils.arrayInt2String(action.getRateMax()));
					showLogD("action rateMin : " + Utils.arrayInt2String(action.getRateMin()));
					showLogD("\n");
				}*/

                db = RoomDBUtils.openData(MainActivity.this);
                RoomDBUtils.deleteAllData(db);
                ActionDBUtils.deleteAllData(db);

                for (int i = 0; i < roomList.size(); i++) {
                    RoomBean bean = roomList.get(i);
                    RoomDBUtils.insertData(db, bean.getRoomId(), bean.getRoomName()
                            , bean.getRoomType(), bean.getRoomPic(), bean.getRoomBelong());
                }
                //RoomDBUtils.searchAllData(db,MainActivity.this);

                for (int i = 0; i < actionList.size(); i++) {
                    ActionBean bean = actionList.get(i);
                    ActionDBUtils.insertData(db, bean.getActionId(), bean.getRoomId()
                            , bean.getActionName(), bean.getActionType(), bean.getActionPicUrl()
                            , bean.getBytesCheck(), bean.getHighTime(), bean.getLowTime()
                            , bean.getInnerHighAndLow(), bean.getPeriod(), bean.getInterval()
                            , bean.getRateMin(), bean.getRateMax(), bean.getPowerLV(), bean.getMaxWorkTime()
                            , bean.getStrength());
                }
                //ActionDBUtils.searchAllData(db);

                spInfo.setReleasyVersion(releasyVersion);
                showLogD("total : " + total);
            }


            return null;
        }

        protected void onPostExecute(Boolean result) {
            toNextActivity(); //跳转
        }
    }


    /**
     * 请求参数封装
     */
    private List<NameValuePair> getLogParams() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("uid", spInfo.getUId() + ""));
        params.add(new BasicNameValuePair("log", spInfo.getLogout()));
        return params;
    }

    /**
     * 操作记录线程处理
     */
    private class AddOperationLogAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private Bundle mResult;
        private Bundle headBundle;
        private int retStatus;
        private String retMsg;

        protected Boolean doInBackground(Void... param) {
            List<NameValuePair> params = getLogParams();
            Bundle mResult = HttpUtils.doPost(params, HttpConstants.ADD_OPERATION_LOG
                    , MainActivity.this, mScreenWidth, mScreenHeight);
            if (mResult.getInt("code") != HttpConstants.SUCCESS) {
                retStatus = mResult.getInt("code");
                retMsg = getResources().getString(R.string.network_anomaly);
                return null;
            }

            headBundle = ResolveJsonUtils.getJsonHead(mResult.getString("content"));
            retStatus = headBundle.getInt("retStatus");
            retMsg = headBundle.getString("retMsg");
            showLogD("retStatus : " + retStatus + "    retMsg : " + retMsg);

            if (retStatus == HttpConstants.SUCCESS)
                spInfo.setLogout("");
            else
                showLogD(retMsg);

            return null;
        }

        protected void onPostExecute(Boolean result) {

        }
    }

}
