package com.releasy.android.activity.device;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.releasy.android.R;
import com.releasy.android.ReleasyApplication;
import com.releasy.android.activity.main.BaseFragment;
import com.releasy.android.adapter.DeviceManageAdapter;
import com.releasy.android.bean.DeviceBean;
import com.releasy.android.constants.Constants;
import com.releasy.android.db.DeviceDBUtils;
import com.releasy.android.db.ReleasyDatabaseHelper;
import com.releasy.android.service.BleWorkService;
import com.releasy.android.utils.SendOrderOutTimeThread;
import com.releasy.android.utils.SharePreferenceUtils;
import com.releasy.android.utils.StringUtils;
import com.releasy.android.utils.Utils;
import com.releasy.android.view.TopNavLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备管理页面
 *
 * @author Lighting.Z
 */
public class DeviceMainFragment extends BaseFragment {

    private final int TO_SEARCH_DEVICE = 100;
    private View view;
    private TopNavLayout mTopNavLayout;            //导航菜单栏
    private SwipeMenuListView listView;            //设备listview
    private LinearLayout listFooterLayout;         //listview footer 布局
    private LinearLayout addDeviceLayout;          //添加设备按钮
    private DeviceManageAdapter adapter;           //设备adapter
    private List<DeviceBean> deviceList;
    private ReleasyDatabaseHelper db;              //数据库
    private ReleasyApplication app;                //Application
    private BleWorkService mBluetoothLeService;    //蓝牙service
    protected int current = 0;                     //设备游标

    private OutTimeHandler outTimeHandler;
    private SendOrderOutTimeThread outTimeThread;
    private int operatingState = GET_ELECTRICITY;
    private static int GET_ELECTRICITY = 0;
    private static int CLOSE = 1;
    private static int GET_UUID = 2;
    private SharePreferenceUtils spInfo;  //SharePreference

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLogD("DeviceMainFragment onCreate");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        showLogD("DeviceMainFragment onCreateView");

        if (view == null || view.getParent() != null) {
            //判定视图是否存在   不重复加载
            view = inflater.inflate(R.layout.fragment_device_main, container, false);
        }
        init(); //初始化
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showLogD("DeviceMainFragment onActivityCreated");
    }

    public void onResume() {
        super.onResume();

        setActionEntryBtn(this.getActivity(), app, mTopNavLayout);  //设置快捷按摩页面进入按钮
        this.getActivity().registerReceiver(mUpdateReceiver, makeGattIntentFilter());  //注册广播

        if (deviceList == null) {
            deviceList = DeviceDBUtils.searchData(db);  //获取数据库设备表信息
            adapter = new DeviceManageAdapter(this.getActivity(), deviceList);  //初始化Adapter
            listView.setAdapter(adapter);  //设置Adapter
        }

        operateDevice();
        showLogD("DeviceMainFragment onResume");
    }

    public void onPause() {
        super.onPause();
        current = 0;

        try {
            this.getActivity().unregisterReceiver(mUpdateReceiver);  //解绑广播
        } catch (Exception e) {
        }

        showLogD("DeviceMainFragment onPause");
    }

    public void onDestroyView() {
        super.onDestroyView();
        showLogD("ReasyMainFragment onDestroyView");
    }

    public void onDestroy() {
        super.onDestroy();
        showLogD("DeviceMainFragment onDestroy");
    }

    /**
     * 回调函数
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (TO_SEARCH_DEVICE == requestCode && resultCode == Activity.RESULT_OK) {
            deviceList = DeviceDBUtils.searchData(db);
            adapter = new DeviceManageAdapter(this.getActivity(), deviceList);
            listView.setAdapter(adapter);
        }
    }

    /**
     * 初始化
     */
    private void init() {
        spInfo = new SharePreferenceUtils(this.getActivity());
        app = (ReleasyApplication) this.getActivity().getApplication();
        mBluetoothLeService = app.getBleService();
        db = DeviceDBUtils.openData(this.getActivity());
        outTimeHandler = new OutTimeHandler();               //超时Handler
        outTimeThread = new SendOrderOutTimeThread(this.getActivity(), outTimeHandler);                 //超时Thread

        initProgressDialog(this.getString(R.string.closing_device));
        initViews();    //初始化视图
        initEvents();   //初始化点击事件
        setTopNav();    //初始化导航栏
    }

    /**
     * 初始化视图
     */
    protected void initViews() {
        mTopNavLayout = (TopNavLayout) view.findViewById(R.id.topNavLayout);
        listView = (SwipeMenuListView) view.findViewById(R.id.listView);

        listFooterLayout = (LinearLayout) LayoutInflater.from(this.getActivity())
                .inflate(R.layout.layout_device_list_footer, null);
        listView.addFooterView(listFooterLayout);
        addDeviceLayout = (LinearLayout) listFooterLayout.findViewById(R.id.addDeviceLayout);

        initSwipeMenuListView();  //初始化侧滑ListView
    }

    /**
     * 初始化导航栏
     */
    private void setTopNav() {
        mTopNavLayout.setTitltTxt(R.string.device_title);    //设置导航栏标题
    }

    /**
     * 初始化侧滑ListView
     */
    private void initSwipeMenuListView() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            public void create(SwipeMenu menu) {
                int txtSize = (int) DeviceMainFragment.this.getResources().getDimension(R.dimen.swipe_menu_txt_size);
                int whiteColor = DeviceMainFragment.this.getResources().getColor(R.color.white);
                int blueColor = DeviceMainFragment.this.getResources().getColor(R.color.color_txt_01b2e8);

                //关闭
                SwipeMenuItem closeItem = new SwipeMenuItem(
                        DeviceMainFragment.this.getActivity().getApplicationContext());
                closeItem.setBackground(new ColorDrawable(Color.rgb(0xe4,  // set item background
                        0xe4, 0xe4)));
                closeItem.setWidth(Utils.dp2px(85, DeviceMainFragment.this.getActivity())); // set item width
                //deleteItem.setIcon(R.drawable.ic_delete); // set a icon
                closeItem.setTitle(R.string.close);
                closeItem.setTitleColor(blueColor);
                closeItem.setTitleSize(txtSize);
                menu.addMenuItem(closeItem); // add to menu

                //删除
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        DeviceMainFragment.this.getActivity().getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,  // set item background
                        0x5a, 0x59)));
                deleteItem.setWidth(Utils.dp2px(85, DeviceMainFragment.this.getActivity())); // set item width
                //deleteItem.setIcon(R.drawable.ic_delete); // set a icon
                deleteItem.setTitle(R.string.delete);
                deleteItem.setTitleColor(whiteColor);
                deleteItem.setTitleSize(txtSize);
                menu.addMenuItem(deleteItem); // add to menu
            }
        };

        listView.setMenuCreator(creator);
    }

    /**
     * 初始化事件
     */
    protected void initEvents() {
        addDeviceLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (deviceList.size() == 0) {
                    spInfo.setSelectDevice(Constants.SELECT_DEVICE_UNKNOWN);
                    Intent intent = new Intent(DeviceMainFragment.this.getActivity(), ChoiceDeviceActivity.class);
                    startActivityForResult(intent, TO_SEARCH_DEVICE);

                    return;
                }

                if (spInfo.getSelectDevice().equals(Constants.SELECT_DEVICE_M1)) {
                    if (deviceList.size() >= 4) {
                        Toast.makeText(DeviceMainFragment.this.getActivity()
                                , R.string.devices_max_4, Toast.LENGTH_LONG).show();
                        return;
                    }

                    Intent intent = new Intent(DeviceMainFragment.this.getActivity(), SearchDeviceForM1Activity.class);
                    startActivityForResult(intent, TO_SEARCH_DEVICE);
                } else if (spInfo.getSelectDevice().equals(Constants.SELECT_DEVICE_M2)) {
                    Toast.makeText(DeviceMainFragment.this.getActivity()
                            , R.string.devices_max_1, Toast.LENGTH_LONG).show();
                }

            }
        });

        //侧滑ListView 侧滑按钮点击事件
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        //判断是不是2代
                        if (spInfo.getSelectDevice().equals(Constants.SELECT_DEVICE_M2)) {
                            operatingState = CLOSE;
                            operateDevice(current);

                        } else {
                            if (deviceList.get(position).getConnectStatus()) {
                                progressDialog.show();
                                //isCloseing = true;
                                operatingState = CLOSE;
                                operateDevice(position);
                            } else {
                                Toast.makeText(DeviceMainFragment.this.getActivity(), R.string.device_closed_or_not, Toast.LENGTH_LONG).show();
                            }
                        }

                        break;
                    case 1:
                        if (app.getIsWorking()) {
                            showDeleteDialog(position);
                            return false;
                        }

                        if (spInfo.getSelectDevice().equals(Constants.SELECT_DEVICE_M2)) {
                            for (int i = 0; i < deviceList.size(); i++) {
                                DeviceBean bean = deviceList.get(i);
                                DeviceDBUtils.deleteData(db, bean.getAddress());
                                mBluetoothLeService.close(bean.getAddress());
                            }
                            deviceList.clear();
                        } else {
                            DeviceBean bean = deviceList.get(position);
                            DeviceDBUtils.deleteData(db, bean.getAddress());
                            mBluetoothLeService.close(bean.getAddress());
                            deviceList.remove(position);
                        }


                        if (deviceList.size() == 0)
                            spInfo.setSelectDevice(Constants.SELECT_DEVICE_UNKNOWN);

                        adapter.notifyDataSetChanged();
                        break;

                }

                return false;
            }
        });

    }

    /**
     * 在运行状态删除最后一个设备时弹出dialog
     */
    private void showDeleteDialog(final int position) {
        // 发现新版本，提示用户更新
        AlertDialog.Builder alert = new AlertDialog.Builder(DeviceMainFragment.this.getActivity());
        alert.setTitle(R.string.del_device)
                .setMessage(R.string.del_runing_device)
                .setPositiveButton(R.string.confirm,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                if (spInfo.getSelectDevice().equals(Constants.SELECT_DEVICE_M2)) {
                                    for (int i = 0; i < deviceList.size(); i++) {
                                        DeviceBean bean = deviceList.get(i);
                                        DeviceDBUtils.deleteData(db, bean.getAddress());
                                        mBluetoothLeService.close(bean.getAddress());
                                    }
                                    deviceList.clear();
                                } else {
                                    DeviceBean bean = deviceList.get(position);
                                    DeviceDBUtils.deleteData(db, bean.getAddress());
                                    mBluetoothLeService.close(bean.getAddress());
                                    deviceList.remove(position);
                                }

                                adapter.notifyDataSetChanged();
                                app.CountdownTimerUtilsStop();
                                app.setLastRoomId(-100);
                                setActionEntryBtn(DeviceMainFragment.this.getActivity(), app, mTopNavLayout);  //设置快捷按摩页面进入按钮
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
        alert.create().show();
    }

    /**
     * 广播Action
     */
    private static IntentFilter makeGattIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleWorkService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleWorkService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleWorkService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleWorkService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BleWorkService.ACTION_GATT_CONNECTED_133);
        intentFilter.addAction(BleWorkService.ACTION_CHARACTERISTIC_WRITE_SUCCESS);  //蓝牙UUID数据写入成功
        return intentFilter;
    }

    /**
     * 广播注册
     */
    private final BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            showLogD("isResumed : " + DeviceMainFragment.this.isResumed());
            if (!DeviceMainFragment.this.isResumed())
                return;

            //蓝牙连接成功广播
            showLogD("DeviceMainFragment BroadcastReceiver mUpdateReceiver ");
            if (BleWorkService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the
                // user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            }

            if (BleWorkService.ACTION_GATT_CONNECTED_133.equals(action)
                    || BleWorkService.ACTION_GATT_DISCONNECTED.equals(action)) {
                if (current >= deviceList.size() || current < 0)
                    return;

                String deviceStr = DeviceMainFragment.this.getActivity().getString(R.string.device);
                String connectFailuer = DeviceMainFragment.this.getActivity().getString(R.string.connect_failure);
                Toast.makeText(DeviceMainFragment.this.getActivity(), deviceStr + deviceList.get(current).getAddress()
                        + connectFailuer, Toast.LENGTH_LONG).show();
                if (/*!isCloseing*/operatingState != CLOSE) {
                    current = current + 1;
                    operateDevice();
                }
            }

            //特征值读取成功广播
            if (BleWorkService.ACTION_DATA_AVAILABLE.equals(action)) {
                showLogD("DeviceMainFragment BroadcastReceiver mUpdateReceiver ACTION_DATA_AVAILABLE");
                if (current >= deviceList.size() || current < 0)
                    return;

                Bundle bundle = intent.getExtras();
                if (bundle == null) {
                    current = current + 1;
                    operateDevice();
                    return;
                }

                if (operatingState == GET_ELECTRICITY) {
                    if (bundle.getByteArray("data") == null) {
                        current = current + 1;
                        operateDevice();
                        return;
                    }

                    String dataStr = new String(bundle.getByteArray("data"));
                    int data = /*bundle.getString("data")*/dataStr.charAt(0);
                    deviceList.get(current).setPower(data);
                    deviceList.get(current).setConnectStatus(true);
                    adapter.notifyDataSetChanged();

                    if (StringUtils.isBlank(deviceList.get(current).getUuid())) {
                        operatingState = GET_UUID;
                        getUuid();
                    } else {
                        current = current + 1;
                        operateDevice();
                    }

                } else if (operatingState == GET_UUID) {
                    if (bundle.getByteArray("data") == null) {
                        current = current + 1;
                        operateDevice();
                        return;
                    }

                    String uuid = printHexString(bundle.getByteArray("data"));
                    Utils.showLogD("____uuid : " + uuid);
                    deviceList.get(current).setUuid(uuid);
                    DeviceDBUtils.UpdataDeviceUuid(db, deviceList.get(current).getAddress(), uuid);
                    //DeviceDBUtils.searchData(db);
                    operatingState = GET_ELECTRICITY;
                    current = current + 1;
                    operateDevice();
                } else {
                    operatingState = GET_ELECTRICITY;
                    current = current + 1;
                    operateDevice();
                }
            }

            if (BleWorkService.ACTION_CHARACTERISTIC_WRITE_SUCCESS.equals(action)) {
                showLogD("ACTION_CHARACTERISTIC_WRITE_SUCCESS");
                if (spInfo.getSelectDevice().equals(Constants.SELECT_DEVICE_M2)) {
                    deviceList.get(closeItem).setConnectStatus(false);
                    deviceList.get(closeItem).setPower(-1);
                    adapter.notifyDataSetChanged();
                    String deviceStr = DeviceMainFragment.this.getActivity().getString(R.string.device);
                    String closeSucces = DeviceMainFragment.this.getActivity().getString(R.string.close_success_device);
                    Toast.makeText(DeviceMainFragment.this.getActivity(), deviceStr + deviceList.get(closeItem).getAddress()
                            + closeSucces, Toast.LENGTH_LONG).show();

                    current = current + 1;
                    if (current >= deviceList.size()) {
                        progressDialog.dismiss();
                        closeItem = -1;
                        operatingState = GET_ELECTRICITY;
                        DeviceMainFragment.this.getActivity().unregisterReceiver(mUpdateReceiver);  //解绑广播
                        current = 0;
                    } else
                        operateDevice(current);
                } else {
                    progressDialog.dismiss();
                    outTimeHandler.removeCallbacks(outTimeThread);
                    deviceList.get(closeItem).setConnectStatus(false);
                    deviceList.get(closeItem).setPower(-1);
                    adapter.notifyDataSetChanged();
                    String deviceStr = DeviceMainFragment.this.getActivity().getString(R.string.device);
                    String closeSucces = DeviceMainFragment.this.getActivity().getString(R.string.close_success_device);
                    Toast.makeText(DeviceMainFragment.this.getActivity(), deviceStr + deviceList.get(closeItem).getAddress()
                            + closeSucces, Toast.LENGTH_LONG).show();
                    closeItem = -1;
                    //isCloseing = false;
                    operatingState = GET_ELECTRICITY;
                    DeviceMainFragment.this.getActivity().unregisterReceiver(mUpdateReceiver);  //解绑广播
                }

            }
        }
    };

    // Demonstrates how to iterate through the supported GATT
    // Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the
    // ExpandableListView
    // on the UI.
    protected ArrayList<BluetoothGattCharacteristic> characteristicList = new ArrayList<BluetoothGattCharacteristic>();

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        showLogD("displayGattServices");

        if (gattServices == null)
            return;

        characteristicList = new ArrayList<BluetoothGattCharacteristic>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                    .getCharacteristics();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                characteristicList.add(gattCharacteristic);
            }
        }

        if (characteristicList.size() == 0 && operatingState != CLOSE) {
            showLogD("characteristicList.size() == 0");
            current = current + 1;
            operateDevice();
            return;
        }

        if (operatingState == CLOSE)
            closeDevice();
        else if (operatingState == GET_ELECTRICITY)
            getElectricity();
        else if (operatingState == GET_UUID) {
            getUuid();
        }
    }

    /**
     * 顺序连接设备
     */
    private void operateDevice() {
        if (deviceList == null || deviceList.size() == 0) {
            this.getActivity().unregisterReceiver(mUpdateReceiver);  //解绑广播
            return;
        }

        if (current >= deviceList.size()) {
            this.getActivity().unregisterReceiver(mUpdateReceiver);  //解绑广播
            current = 0;
            return;
        }

        showLogD("operateDevice to get electricity");
        mBluetoothLeService.connect(deviceList.get(current).getAddress());
    }

    int closeItem = -1;

    private void operateDevice(int position) {
        closeItem = position;
        this.getActivity().registerReceiver(mUpdateReceiver, makeGattIntentFilter());  //注册广播
        if (deviceList == null || deviceList.size() == 0) {
            this.getActivity().unregisterReceiver(mUpdateReceiver);  //解绑广播
            return;
        }

        if (closeItem >= deviceList.size() || closeItem < 0) {
            this.getActivity().unregisterReceiver(mUpdateReceiver);  //解绑广播
            return;
        }

        showLogD("operateDevice to close");
        mBluetoothLeService.connect(deviceList.get(closeItem).getAddress());
    }

    /**
     * 读取电量
     */
    private void getElectricity() {
        if (characteristicList != null) {
            for (int i = 0; i < characteristicList.size(); i++) {
                BluetoothGattCharacteristic characteristicMode = characteristicList.get(i);

                if (characteristicMode.getUuid().toString()
                        .equals("00002a19-0000-1000-8000-00805f9b34fb")) {

                    mBluetoothLeService.readCharacteristic(characteristicMode);
                    showLogD(deviceList.get(current).getAddress() + "  read characteristic ");
                }
            }
        }
    }

    /**
     * 读取硬件版本
     */
    private void getDeviceVersion() {
        if (characteristicList != null) {
            for (int i = 0; i < characteristicList.size(); i++) {
                BluetoothGattCharacteristic characteristicMode = characteristicList.get(i);

                if (characteristicMode.getUuid().toString()
                        .equals("00002a1a-0000-1000-8000-00805f9b34fb")) {

                    mBluetoothLeService.readCharacteristic(characteristicMode);
                    showLogD(deviceList.get(current).getAddress() + "  read characteristic ");
                }
            }
        }
    }

    /**
     * 读取UUID
     */
    private void getUuid() {
        if (characteristicList != null) {
            for (int i = 0; i < characteristicList.size(); i++) {
                BluetoothGattCharacteristic characteristicMode = characteristicList.get(i);

                if (characteristicMode.getUuid().toString()
                        .equals("00002a23-0000-1000-8000-00805f9b34fb")) {

                    mBluetoothLeService.readCharacteristic(characteristicMode);
                    showLogD(deviceList.get(current).getAddress() + "  read characteristic ");
                }
            }
        }
    }

    /**
     * 关闭设备
     */
    private void closeDevice() {
        if (characteristicList != null) {
            for (int i = 0; i < characteristicList.size(); i++) {
                BluetoothGattCharacteristic characteristicMode = characteristicList.get(i);
                if (characteristicMode.getUuid().toString().equals("0000ffa1-0000-1000-8000-00805f9b34fb")) {
                    byte[] sendTmp = new byte[1];
                    sendTmp[0] = Utils.u8ToByte((short) 0x01);
                    characteristicMode.setValue(sendTmp);
                    mBluetoothLeService.wirteCharacteristic(characteristicMode);
                    showLogD("closeDevice : " + deviceList.get(closeItem).getAddress());
                    outTimeHandler.postDelayed(outTimeThread, 5000);
                }
            }
        }
    }

    /**
     * 超时Handler
     */
    private class OutTimeHandler extends Handler {
        public void handleMessage(Message msg) {
            if (DeviceMainFragment.this.getActivity().isFinishing())
                return;

            showLogD("连接超时");
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            //isCloseing = false;
            operatingState = GET_ELECTRICITY;
            Toast.makeText(DeviceMainFragment.this.getActivity(), R.string.device_connect_failure, Toast.LENGTH_LONG).show();
        }
    }

    private String printHexString(byte[] b) {
        String uuid = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            uuid = uuid + hex.toUpperCase();
        }

        return uuid;
    }

}
