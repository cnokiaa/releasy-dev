package com.releasy.android.activity.device;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.releasy.android.R;
import com.releasy.android.ReleasyApplication;
import com.releasy.android.activity.main.RelesyBaseActivity;
import com.releasy.android.bean.DeviceBean;
import com.releasy.android.constants.ActionConstants;
import com.releasy.android.constants.Constants;
import com.releasy.android.db.DeviceDBUtils;
import com.releasy.android.db.ReleasyDatabaseHelper;
import com.releasy.android.service.BleWorkService;
import com.releasy.android.utils.SendOrderOutTimeThread;
import com.releasy.android.utils.SharePreferenceUtils;
import com.releasy.android.utils.StringUtils;
import com.releasy.android.utils.Utils;
import com.releasy.android.view.OpenLocationDialog;
import com.releasy.android.view.TestButton;
import com.releasy.android.view.TopNavLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 查找设备M1  匹配页面
 *
 * @author Lighting.Z
 */
public class SearchDeviceForM1Activity extends RelesyBaseActivity {

    private TopNavLayout mTopNavLayout;                  //导航菜单栏
    private TranslateAnimation animation;                //平移动画
    private Animation rotateAnim;                        //旋转动画
    private AnimationDrawable sreachAnim;               //扩散动画
    private LinearLayout contantLayout;                  //内容页面
    private RelativeLayout startSearchLayout;            //开始搜索页面
    private ImageView searchingImg;                      //搜索圆形
    private ImageView searchingBarImg;                   //搜索旋转条
    private LinearLayout endSearchLayout;                //结束搜索页面
    private LinearLayout listHeadLayout;                 //listview head 布局
    private AnimationDrawable listSreachAnim;           //扩散动画
    private ImageView listHeadImg;                       //列表头搜索圈
    private ImageView listHeadBarImg;                    //列表头搜索旋转条
    private TextView deviceNameTxt;                      //设备名称
    private TextView deviceNumTxt;                       //设备数量TXT
    private ListView listView;                           //搜索List
    private LeDeviceListAdapter mLeDeviceListAdapter;    //搜索Adapter
    private boolean mScanning;
    private Handler mHandler;                            //用于搜索时间限制
    private BluetoothAdapter mBluetoothAdapter;          //蓝牙
    private static long SCAN_PERIOD = 10000;             //10秒后停止查找搜索.
    private BleWorkService mBluetoothLeService;          //蓝牙service
    private ReleasyDatabaseHelper db;                    //数据库

    private ReleasyApplication app;                      //Application
    private SharePreferenceUtils spInfo;                 //SharePreference
    private OutTimeHandler outTimeHandler;
    private SendOrderOutTimeThread outTimeThread;
    private DeviceBean testDevice;
    private String cursorAddress = "";
    private boolean isBinding = false;
    private boolean noSearchDevice = true;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device_for_m1);

    }

    protected void onResume() {
        super.onResume();

        init(); //初始化

        registerReceiver(mUpdateReceiver, makeGattIntentFilter());  //注册广播
        mLeDeviceListAdapter = new LeDeviceListAdapter();  //初始化adapter
        listView.setAdapter(mLeDeviceListAdapter);         //listview绑定adapter
        //初始化控件显示
        listView.setVisibility(View.GONE);
        endSearchLayout.setVisibility(View.GONE);
        startSearchLayout.setVisibility(View.VISIBLE);
        contantLayout.setVisibility(View.VISIBLE);
        scanLeDevice(true);  //开始搜索
        mBluetoothLeService.openSearch();

    }

    protected void onPause() {
        super.onPause();
        scanLeDevice(false);           //停止搜索
        closeBleGatt();
        mLeDeviceListAdapter.clear();  //清空数据


        try {
            unregisterReceiver(mUpdateReceiver);  //解绑广播
        } catch (Exception e) {
        }

        mBluetoothLeService.closeSearch();

        listSreachAnim.stop();
        sreachAnim.stop();
        searchingBarImg.clearAnimation();
        listHeadBarImg.clearAnimation();
        listView.removeHeaderView(listHeadLayout);
        mLeDeviceListAdapter.clear();

    }

    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 关闭连接的蓝牙GATT
     */
    private void closeBleGatt() {
        if (isBinding) {
            for (int i = 0; i < devicesList.size(); i++) {
                DeviceBean bean = devicesList.get(i);
                if (!bean.getIsCheck())
                    mBluetoothLeService.close(bean.getAddress());
            }
        } else {
            for (int i = 0; i < devicesList.size(); i++) {
                DeviceBean bean = devicesList.get(i);
                mBluetoothLeService.close(bean.getAddress());
            }
        }
    }

    /**
     * 初始化
     */
    private void init() {
        app = (ReleasyApplication) this.getApplication();           //获取Applicatin
        mBluetoothLeService = app.getBleService();                  //获取BleService
        spInfo = new SharePreferenceUtils(this);
        isTest = true;
        selectDevice = Constants.SELECT_DEVICE_M1;

        mHandler = new Handler();
        outTimeHandler = new OutTimeHandler();               //超时Handler
        outTimeThread = new SendOrderOutTimeThread(this, outTimeHandler);                 //超时Thread

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        db = DeviceDBUtils.openData(this);

        initViews();     //初始化控件
        setTopNav();     //初始化导航栏
        initEvents();    //初始化点击事件
        initProgressDialog(this.getString(R.string.are_connected_devices));


        if (Build.VERSION.SDK_INT >= 23 && spInfo.getLocationCursor()) {
            if (!isOPen(this))
                showDialog();
        }

//        if (Build.VERSION.SDK_INT >= 23) {
//            if (spInfo.getLocationCursor()) {
//				/*AlertDialog.Builder builder = new Builder(this);
//		    	builder.setMessage(R.string.gps_permission_msg);
//		    	builder.setTitle(R.string.gps_permission_title);
//		    	builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
//		    		public void onClick(DialogInterface dialog, int which) {
//		    			dialog.dismiss();
//		    		}});
//
//		    	builder.create().show();*/
//
//                OpenLocationDialog dialog = new OpenLocationDialog(this);
//                dialog.show();
//            }
//
//        }

        spInfo.setLocationCursor(false);
    }

    public static final boolean isOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）  
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）  
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }

    public void showDialog() {
        Builder builder = new Builder(this);
        builder.setMessage(R.string.prompt_sdk23);
        builder.setTitle(R.string.prompt);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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

    /**
     * 初始化导航栏
     */
    private void setTopNav() {
        mTopNavLayout.setTitltTxt(R.string.search_device);           //设置导航栏标题
        mTopNavLayout.setLeftImgSrc(R.drawable.ic_nav_bar_back);     //设置导航栏左边按钮
        mTopNavLayout.setRightImgSrc(R.drawable.ic_finish);          //设置导航栏右边按钮
    }

    /**
     * 初始化控件
     */
    protected void initViews() {
        mTopNavLayout = (TopNavLayout) findViewById(R.id.topNavLayout);
        listView = (ListView) findViewById(R.id.listView);

        //加载listview head view 布局
        listHeadLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_search_device_list_head, null);
        listView.addHeaderView(listHeadLayout);
        listHeadImg = (ImageView) listHeadLayout.findViewById(R.id.listHeadImg);
        listHeadBarImg = (ImageView) listHeadLayout.findViewById(R.id.listHeadBarImg);
        deviceNameTxt = (TextView) listHeadLayout.findViewById(R.id.deviceNameTxt);
        deviceNumTxt = (TextView) listHeadLayout.findViewById(R.id.deviceNumTxt);
        deviceNameTxt.setText(R.string.mooyee_m1);

        contantLayout = (LinearLayout) findViewById(R.id.contentLayout);
        startSearchLayout = (RelativeLayout) findViewById(R.id.startSearchLayout);
        searchingImg = (ImageView) findViewById(R.id.searchingImg);
        searchingBarImg = (ImageView) findViewById(R.id.searchingBarImg);
        endSearchLayout = (LinearLayout) findViewById(R.id.endSearchLayout);

        rotateAnim = AnimationUtils.loadAnimation(this, R.anim.sreach_bar_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        rotateAnim.setInterpolator(lin);
        searchingBarImg.startAnimation(rotateAnim);

        sreachAnim = (AnimationDrawable) searchingImg.getDrawable();
        listSreachAnim = (AnimationDrawable) listHeadImg.getDrawable();
        sreachAnim.start();
    }

    /**
     * 初始化点击事件
     */
    protected void initEvents() {
        //导航栏左边按钮点击事件
        mTopNavLayout.setLeftImgOnClick(new OnClickListener() {
            public void onClick(View arg0) {
                SearchDeviceForM1Activity.this.finish();
            }
        });

        //导航栏右边按钮点击事件
        mTopNavLayout.setRightImgOnClick(new OnClickListener() {
            public void onClick(View arg0) {
                if (!checkDevice())
                    return;

                for (int i = 0; i < devicesList.size(); i++) {
                    DeviceBean bean = devicesList.get(i);
                    if (bean.getIsCheck() && !DeviceDBUtils.isAddressExist(db, bean.getAddress())) {
                        //如果数据库中不存在 添加到数据库里面
                        DeviceDBUtils.insertData(db, bean.getName(), bean.getAddress()
                                , bean.getDevicebroadcastName(), bean.getDeviceVersion());
                    }
                    mBluetoothLeService.close(bean.getAddress());
                }


                setResult(Activity.RESULT_OK);
                spInfo.setSelectDevice(Constants.SELECT_DEVICE_M1);
                isBinding = true;

                SearchDeviceForM1Activity.this.finish();
            }
        });

        //搜索列表项点击事件
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                if (position == 0)
                    return;

                DeviceBean device = devicesList.get(position - 1);
                if (device == null)
                    return;
                else {
                    //改变选中状态
                    device.setIsCheck(!device.getIsCheck());
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            }
        });

        endSearchLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                endSearchLayout.setVisibility(View.GONE);
                startSearchLayout.setVisibility(View.VISIBLE);
                scanLeDevice(true);
            }
        });
    }

    /**
     * 检测绑定设备数目
     *
     * @return
     */
    private boolean checkDevice() {
        int checkNum = 0;
        for (int i = 0; i < devicesList.size(); i++) {
            DeviceBean bean = devicesList.get(i);
            if (bean.getIsCheck()) {
                checkNum = checkNum + 1;
            }
        }

        if (checkNum == 0) {
            Toast.makeText(this, R.string.devices_num_0, Toast.LENGTH_LONG).show();
            return false;
        }

        ReleasyDatabaseHelper db = DeviceDBUtils.openData(this);
        List<DeviceBean> list = DeviceDBUtils.searchData(db);
        if (checkNum + list.size() > 4) {
            Toast.makeText(this, R.string.devices_max_4, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    /**
     * 搜索函数
     *
     * @param enable enable 为 true 进行搜索 ; 为 false 停止搜索;
     */
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    mScanning = false;
                    if (ActivityCompat.checkSelfPermission(SearchDeviceForM1Activity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(SearchDeviceForM1Activity.this, R.string.prompt_sdk23, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);

//                    if (devicesList.size() == 0) {
//                        //没有搜索到设备时 显示重新搜索视图
//                        //TODO
//                        //startSearchLayout.setVisibility(View.GONE);
//                        //endSearchLayout.setVisibility(View.VISIBLE);
//                        if (noSearchDevice) {
//                            noSearchDevice = false;
//                            if (!SearchDeviceForM1Activity.this.isFinishing()) {
//                                OpenLocationDialog dialog = new OpenLocationDialog(SearchDeviceForM1Activity.this);
//                                dialog.show();
//                            }
//                        }
//                    }
                    /*else{
                    	SCAN_PERIOD = 10000600000; //搜索到一个设备则一直搜索
                    	scanLeDevice(true);
                    }*/

                    SCAN_PERIOD = 10000; //搜索到一个设备则一直搜索
                    scanLeDevice(true);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }


    // Adapter for holding devices found through scanning.
    private ArrayList<DeviceBean> devicesList;  //设备列表数据

    private class LeDeviceListAdapter extends BaseAdapter {
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            devicesList = new ArrayList<DeviceBean>();
            mInflator = SearchDeviceForM1Activity.this.getLayoutInflater();
        }

        public void addDevice(DeviceBean device) {
            String address = device.getAddress();
            for (int i = 0; i < devicesList.size(); i++) {
                if (address.equals(devicesList.get(i).getAddress()))
                    return;
            }

            devicesList.add(device);

        }

        public void clear() {
            devicesList.clear();
        }

        public int getCount() {
            return devicesList.size();
        }

        public Object getItem(int i) {
            return devicesList.get(i);
        }

        public long getItemId(int i) {
            return i;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.item_search_device_for_m1, null);
                viewHolder = new ViewHolder();
                viewHolder.itemLayout = (LinearLayout) view.findViewById(R.id.itemLayout);
                viewHolder.imgCheck = (ImageView) view.findViewById(R.id.imgCheck);
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                viewHolder.testBtn = (TestButton) view.findViewById(R.id.testBtn);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            //加载数据到控件中
            DeviceBean device = devicesList.get(i);

            //名字
            String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);

            //地址
            String address = SearchDeviceForM1Activity.this.getResources().getString(R.string.address);
            viewHolder.deviceAddress.setText(address + device.getAddress());

            viewHolder.testBtn.setFocusable(false);

            //选中状态
            if (device.getIsCheck()) {
                viewHolder.imgCheck.setImageResource(R.drawable.ic_checked);
                viewHolder.testBtn.setVisibility(View.VISIBLE);
            } else {
                viewHolder.imgCheck.setImageResource(R.drawable.ic_unchecked);
                viewHolder.testBtn.setVisibility(View.GONE);
            }

            viewHolder.testBtn.setOnClickListener(new TestBtnOnClick(device));

            return view;
        }
    }

    /**
     * 列表项控件
     */
    static class ViewHolder {
        LinearLayout itemLayout;
        ImageView imgCheck;           //选项框图片
        TextView deviceName;          //设备名字
        TextView deviceAddress;       //设备地址
        TestButton testBtn;           //测试按钮
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                public void run() {
                    //不为我们的设备时不进行显示
                    if (ActivityCompat.checkSelfPermission(SearchDeviceForM1Activity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    if (device == null || StringUtils.isBlank(device.getName()) || !Utils.checkNameForM1(device.getName())
                            || DeviceDBUtils.isAddressExist(db, device.getAddress())) {
                        return;
                    }

                    if (devicesList.size() == 0 && contantLayout.getVisibility() == View.VISIBLE) {
                        startAnimation();
                    }

                    DeviceBean bean;
                    String name = DeviceDBUtils.getDeviceName(db, device.getAddress());
                    if (StringUtils.isBlank(name)) {
                        String deviceName = device.getName();
                        if (deviceName.equals(Constants.DEVICE_BROADCAST_NAME_M1)){
                            deviceName = Constants.DEVICE_NAME_M1;
                        }
                        bean = new DeviceBean(deviceName, device.getAddress(), deviceName, Constants.DEVICE_VERSION_M1);
                    } else {
                        bean = new DeviceBean(name, device.getAddress(), device.getName(), Constants.DEVICE_VERSION_M1);
                    }
                    bean.setAction(ActionConstants.getTestAction());
                    bean.getAction().setStrength(1);
                    mLeDeviceListAdapter.addDevice(bean);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                    deviceNumTxt.setText(devicesList.size() + "");
                }
            });
        }
    };
    
    /*private boolean checkName(String name){
    	if(name.equals("MOOYEE") || name.equals("MooYee")){
    		return true;
    	}
    	else
    		return false;
    }*/

    /**
     * 开启搜索位移动画
     */
    private void startAnimation() {
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        //showLogD("cy: " + contantLayout.getY() + "    sy: " + startSearchLayout.getY() + "    sy/2: " +startSearchLayout.getHeight()/2);
        //showLogD("statusBarHeight : " + statusBarHeight);

        float y = contantLayout.getY() - startSearchLayout.getY() - statusBarHeight;
        animation = new TranslateAnimation(0, 0, 0, y);
        animation.setDuration(650);//设置动画持续时间
        animation.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation arg0) {
                contantLayout.setVisibility(View.GONE);
                startSearchLayout.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                listSreachAnim.start();
                listHeadBarImg.startAnimation(rotateAnim);
            }

            public void onAnimationRepeat(Animation arg0) {
            }

            public void onAnimationStart(Animation arg0) {
            }

        });
        startSearchLayout.startAnimation(animation);
    }

    /**
     * 测试按钮点击事件
     */
    private class TestBtnOnClick implements OnClickListener {
        private DeviceBean device;

        private TestBtnOnClick(DeviceBean device) {
            this.device = device;
        }

        public void onClick(View arg0) {
            switchParam = CONFIGURE;
            progressDialog.show();
            if (mBluetoothLeService == null) {
                showLogD("mBluetoothLeService == null");
            }

            //scanLeDevice(false);
            testDevice = device;
            cursorAddress = device.getAddress();
            mBluetoothLeService.connect(device.getAddress());
            outTimeHandler.postDelayed(outTimeThread, 10000);
        }
    }

    /**
     * 广播Action
     */
    private static IntentFilter makeGattIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleWorkService.ACTION_GATT_SERVICES_DISCOVERED_SEARCH);
        intentFilter.addAction(BleWorkService.ACTION_GATT_CONNECTED_133_SEARCH);
        intentFilter.addAction(BleWorkService.ACTION_CHARACTERISTIC_WRITE_SUCCESS_SEARCH);

        return intentFilter;
    }

    /**
     * 广播注册
     */
    private final BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            //蓝牙连接成功广播
            if (BleWorkService.ACTION_GATT_SERVICES_DISCOVERED_SEARCH.equals(action)) {
                // Show all the supported services and characteristics on the
                // user interface.
                outTimeHandler.removeCallbacks(outTimeThread);
                if (!cursorAddress.equals(intent.getExtras().getString("data")))
                    return;

                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            }

            if (BleWorkService.ACTION_GATT_CONNECTED_133_SEARCH.equals(action)
                    || BleWorkService.ACTION_GATT_DISCONNECTED.equals(action)) {
                String toast = "error 1113," + SearchDeviceForM1Activity.this.getResources().getString(R.string.connect_failure);
                Toast.makeText(SearchDeviceForM1Activity.this, R.string.device_connect_failure, Toast.LENGTH_LONG).show();
            }

            if (BleWorkService.ACTION_GATT_DISCONNECTED.equals(action)) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Bundle bundle = intent.getExtras();
                String device = getString(R.string.device);
                String connectFailure = getString(R.string.connect_failure);
                Toast.makeText(SearchDeviceForM1Activity.this, device + " " + bundle.getString("data")
                        + " " + connectFailure, Toast.LENGTH_LONG).show();
            }

            //特征值写入成功广播
            if (BleWorkService.ACTION_CHARACTERISTIC_WRITE_SUCCESS_SEARCH.equals(action)) {
                outTimeHandler.removeCallbacks(outTimeThread);

                switch (switchParam) {
                    case CONFIGURE:
                        if (startStep >= 7 && isSendOver) {
                            startStep = 0;
                            sendStep = 0;
                            switchParam = OPEN;
                            startCharacteristicWrite(testDevice, mBluetoothLeService, outTimeHandler, outTimeThread);     //写入开启特征值
                        } else {
                            if (isSendOver) {
                                startStep = startStep + 1;
                                sendStep = 0;
                            } else {
                                sendStep = sendStep + 1;
                            }
                            configureCharacteristicWrite(testDevice, mBluetoothLeService, outTimeHandler, outTimeThread);
                        }
                        break;
                    case OPEN:
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        cursorAddress = "";
                        break;

                }
            }
        }
    };

    // Demonstrates how to iterate through the supported GATT
    // Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the
    // ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        showLogD("displayGattServices");

        if (gattServices == null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            if (testDevice != null)
                mBluetoothLeService.close(testDevice.getAddress());
            Toast.makeText(SearchDeviceForM1Activity.this, R.string.connect_failure, Toast.LENGTH_LONG).show();
            return;
        }

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

        if (characteristicList.size() == 0 || testDevice == null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            if (testDevice != null)
                mBluetoothLeService.close(testDevice.getAddress());
            String toast = "error 1010," + this.getResources().getString(R.string.connect_failure) + this.getString(R.string.please_try_restart_ble);
            Toast.makeText(SearchDeviceForM1Activity.this, toast, Toast.LENGTH_LONG).show();
            return;
        }

        configureCharacteristicWrite(testDevice, mBluetoothLeService, outTimeHandler, outTimeThread); //写入按摩特征值
    }


    /**
     * 超时Handler
     */
    private class OutTimeHandler extends Handler {
        public void handleMessage(Message msg) {
            if (SearchDeviceForM1Activity.this.isFinishing())
                return;

            showLogD("写入超时");
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            mBluetoothLeService.close(cursorAddress);
            cursorAddress = "";
            String toast = " error 1011," + SearchDeviceForM1Activity.this.getResources().getString(R.string.connect_failure);
            Toast.makeText(SearchDeviceForM1Activity.this, toast, Toast.LENGTH_LONG).show();
        }
    }
}
