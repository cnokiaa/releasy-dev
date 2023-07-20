package com.releasy.android.activity.device;

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
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.releasy.android.R;
import com.releasy.android.activity.main.BaseActivity;
import com.releasy.android.utils.SharePreferenceUtils;
import com.releasy.android.view.TopNavLayout;

import java.util.List;


public class ChoiceDeviceActivity extends BaseActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    private final int TO_SEARCH_DEVICE = 100;
    private TopNavLayout mTopNavLayout;                     //导航菜单栏
    private SharePreferenceUtils spInfo;                    //SharePreference
    private LinearLayout m1Layout;                          //M1 Icon
    private LinearLayout m2Layout;
    private int mDeviceType = 0;                            //设备类型 0：M1 1：M2
    //M2 Icon
    String[] perms = {
            Permission.ACCESS_FINE_LOCATION,
            Permission.ACCESS_COARSE_LOCATION,
            Permission.BLUETOOTH_SCAN,
            Permission.BLUETOOTH_CONNECT,
            Permission.BLUETOOTH_ADVERTISE,
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_device);

        init();            //初始化
    }

    /**
     * 初始化
     */
    private void init() {
        spInfo = new SharePreferenceUtils(this);

        initViews();    //初始化视图
        setTopNav();    //初始化导航栏
        initEvents();   //初始化点击事件
    }

    /**
     * 初始化导航栏
     */
    private void setTopNav() {
        mTopNavLayout.setTitltTxt(R.string.choice_device);
        mTopNavLayout.setLeftImgSrc(R.drawable.ic_nav_bar_back);
    }

    /**
     * 初始化控件
     */
    protected void initViews() {
        mTopNavLayout = (TopNavLayout) findViewById(R.id.topNavLayout);

        m1Layout = (LinearLayout) findViewById(R.id.m1_layout);
        m2Layout = (LinearLayout) findViewById(R.id.m2_layout);
    }

    /**
     * 初始化监听函数
     */
    protected void initEvents() {
        mTopNavLayout.setLeftImgOnClick(new OnClickListener() {
            public void onClick(View arg0) {
                ChoiceDeviceActivity.this.finish();
            }
        });

        m1Layout.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                requestPermission(1);
            }
        });

        m2Layout.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                requestPermission(2);
            }
        });
    }

    private void requestPermission(int type) {

        XXPermissions.with(this)
                .permission(perms)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        mDeviceType = type;
                        if (all) {
                            toCheckBLE();
                        } else {
                            Toast.makeText(ChoiceDeviceActivity.this, R.string.prompt_sdk23, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        OnPermissionCallback.super.onDenied(permissions, never);
                        showDialog();
                    }
                });
    }

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
            return;
        }

        // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ChoiceDeviceActivity.this, R.string.prompt_sdk23, Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            toActivity();
        }
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.prompt_sdk23);
        builder.setTitle(R.string.prompt);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + ChoiceDeviceActivity.this.getPackageName()));

                startActivityForResult(intent, 0); //此为设置完成后返回到获取界面
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void toActivity() {
        Intent intent;
        if (mDeviceType == 1) {
            intent = new Intent(ChoiceDeviceActivity.this, SearchDeviceForM1Activity.class);
        } else {
            intent = new Intent(ChoiceDeviceActivity.this, SearchDeviceForM2Activity.class);
        }
        startActivityForResult(intent, TO_SEARCH_DEVICE);
    }

    /**
     * 回调函数
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (TO_SEARCH_DEVICE == requestCode && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK);
            ChoiceDeviceActivity.this.finish();
        } else if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            toActivity();
        }
    }

}
