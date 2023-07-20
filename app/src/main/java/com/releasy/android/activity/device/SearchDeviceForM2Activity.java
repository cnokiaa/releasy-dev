package com.releasy.android.activity.device;

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
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.releasy.android.R;
import com.releasy.android.ReleasyApplication;
import com.releasy.android.activity.main.RelesyBaseActivity;
import com.releasy.android.bean.DeviceBean;
import com.releasy.android.constants.ActionForM2Constants;
import com.releasy.android.constants.Constants;
import com.releasy.android.db.DeviceDBUtils;
import com.releasy.android.db.ReleasyDatabaseHelper;
import com.releasy.android.service.BleWorkService;
import com.releasy.android.utils.SendOrderOutTimeThread;
import com.releasy.android.utils.SharePreferenceUtils;
import com.releasy.android.utils.StringUtils;
import com.releasy.android.utils.Utils;
import com.releasy.android.view.TestButton;
import com.releasy.android.view.TopNavLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 查找设备M2  匹配页面
 *
 * @author Lighting.Z
 */
public class SearchDeviceForM2Activity extends RelesyBaseActivity {

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
    private ExpandableListView expandableList;
    private M2DeviceListAdapter m2DeviceListAdapter;     //搜索Adapter
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
    //private DeviceBean testDevice;
    private List<DeviceBean> testDeviceList;
    private DeviceBean m2mTestDevice;                    //记录选中的主机
    private DeviceBean m2sTestDevice;                    //记录选中的副机

    private String cursorAddress = "";
    private boolean isBinding = false;
    private final static int REQUEST_PERMISSION_BT = 1;
    private boolean noSearchDevice = true;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device_for_m2);

    }

    protected void onResume() {
        super.onResume();

        init(); //初始化

        registerReceiver(mUpdateReceiver, makeGattIntentFilter());  //注册广播
        m2DeviceListAdapter = new M2DeviceListAdapter();   //初始化adapter
        expandableList.setAdapter(m2DeviceListAdapter);    //listview绑定adapter
        //初始化控件显示
        //listView.setVisibility(View.GONE);
        expandableList.setVisibility(View.GONE);
        endSearchLayout.setVisibility(View.GONE);
        startSearchLayout.setVisibility(View.VISIBLE);
        contantLayout.setVisibility(View.VISIBLE);
        scanLeDevice(true);  //开始搜索
        mBluetoothLeService.openSearch();

    }

    protected void onPause() {
        super.onPause();
        scanLeDevice(false);           //停止搜索\
        mBluetoothLeService.closeSearch();
        closeBleGatt();
        expandableList.setVisibility(View.GONE);

        try {
            unregisterReceiver(mUpdateReceiver);  //解绑广播
        } catch (Exception e) {
        }

        listSreachAnim.stop();
        sreachAnim.stop();
        searchingBarImg.clearAnimation();
        listHeadBarImg.clearAnimation();

        expandableList.removeHeaderView(listHeadLayout);
        m2DeviceListAdapter.clearM2BDevice();  //清空数据
        m2DeviceListAdapter.clearM2ADevice();  //清空数据
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 关闭连接的蓝牙GATT
     */
    private void closeBleGatt() {
        if (isBinding) {
            for (int i = 0; i < devicesM2AList.size(); i++) {
                DeviceBean bean = devicesM2AList.get(i);
                if (!bean.getIsCheck())
                    mBluetoothLeService.close(bean.getAddress());
            }

            for (int i = 0; i < devicesM2BList.size(); i++) {
                DeviceBean bean = devicesM2BList.get(i);
                if (!bean.getIsCheck())
                    mBluetoothLeService.close(bean.getAddress());
            }
        } else {
            for (int i = 0; i < devicesM2AList.size(); i++) {
                DeviceBean bean = devicesM2AList.get(i);
                mBluetoothLeService.close(bean.getAddress());
            }

            for (int i = 0; i < devicesM2BList.size(); i++) {
                DeviceBean bean = devicesM2BList.get(i);
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
        selectDevice = Constants.SELECT_DEVICE_M2;

        mHandler = new Handler();
        outTimeHandler = new OutTimeHandler();               //超时Handler
        outTimeThread = new SendOrderOutTimeThread(this, outTimeHandler);                 //超时Thread

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        db = DeviceDBUtils.openData(this);
        testDeviceList = new ArrayList<DeviceBean>();

        initViews();     //初始化控件
        setTopNav();     //初始化导航栏
        initEvents();    //初始化点击事件
        initProgressDialog(this.getString(R.string.are_connected_devices));

        if (Build.VERSION.SDK_INT >= 23 && spInfo.getLocationCursor()) {
            LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

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

        //Test
		/*if(ContextCompat.checkSelfPermission(SearchDeviceForM2Activity.this, Manifest.permission.ACCESS_FINE_LOCATION)
		                != PackageManager.PERMISSION_GRANTED){
			//未开启定位权限
			//开启定位权限,200是标识码
			ActivityCompat.requestPermissions(SearchDeviceForM2Activity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},200);
		}*/

    }

    /**
     * 判断GPS  6.0蓝牙搜索需要打开GPS
     */
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

    /**
     * 展示需要打开GPS的Dialog
     */
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
        expandableList = (ExpandableListView) findViewById(R.id.expandableList);

        //加载listview head view 布局
        listHeadLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_search_device_list_head, null);
        expandableList.addHeaderView(listHeadLayout);

        listHeadImg = (ImageView) listHeadLayout.findViewById(R.id.listHeadImg);
        listHeadBarImg = (ImageView) listHeadLayout.findViewById(R.id.listHeadBarImg);
        deviceNameTxt = (TextView) listHeadLayout.findViewById(R.id.deviceNameTxt);
        deviceNumTxt = (TextView) listHeadLayout.findViewById(R.id.deviceNumTxt);
        deviceNameTxt.setText(R.string.mooyee_m2);

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
                SearchDeviceForM2Activity.this.finish();
            }
        });

        //导航栏右边按钮点击事件
        mTopNavLayout.setRightImgOnClick(new OnClickListener() {
            public void onClick(View arg0) {
                if (!checkDevice())
                    return;

                for (int i = 0; i < devicesM2AList.size(); i++) {
                    DeviceBean bean = devicesM2AList.get(i);
                    if (bean.getIsCheck() && !DeviceDBUtils.isAddressExist(db, bean.getAddress())) {
                        //如果数据库中不存在 添加到数据库里面
                        DeviceDBUtils.insertData(db, bean.getName(), bean.getAddress()
                                , bean.getDevicebroadcastName(), bean.getDeviceVersion());
                    }
                    mBluetoothLeService.close(bean.getAddress());
                }

                for (int i = 0; i < devicesM2BList.size(); i++) {
                    DeviceBean bean = devicesM2BList.get(i);
                    if (bean.getIsCheck() && !DeviceDBUtils.isAddressExist(db, bean.getAddress())) {
                        //如果数据库中不存在 添加到数据库里面
                        DeviceDBUtils.insertData(db, bean.getName(), bean.getAddress()
                                , bean.getDevicebroadcastName(), bean.getDeviceVersion());
                    }
                    mBluetoothLeService.close(bean.getAddress());
                }

                setResult(Activity.RESULT_OK);
                spInfo.setSelectDevice(Constants.SELECT_DEVICE_M2);
                isBinding = true;

                SearchDeviceForM2Activity.this.finish();
            }
        });

        expandableList.setOnGroupClickListener(new OnGroupClickListener() {
            public boolean onGroupClick(ExpandableListView arg0, View v,
                                        int position, long arg3) {
                DeviceBean device = devicesM2AList.get(position);
                if (device == null)
                    return false;
                else {
                    //改变选中状态
                    for (int i = 0; i < devicesM2AList.size(); i++) {
                        if (i == position) {
                            devicesM2AList.get(position).setIsCheck(!device.getIsCheck());
                            if (devicesM2AList.get(position).getIsCheck()) {
                                m2mTestDevice = devicesM2AList.get(position);
                            }
                        } else
                            devicesM2AList.get(i).setIsCheck(false);
                    }

                    for (int i = 0; i < devicesM2BList.size(); i++) {
                        if (i == 0) {
                            devicesM2BList.get(i).setIsCheck(true/*!device.getIsCheck()*/);
                            m2sTestDevice = devicesM2BList.get(i);
                        } else
                            devicesM2BList.get(i).setIsCheck(false);
                    }

                    m2DeviceListAdapter.notifyDataSetChanged();
                }

                return false;
            }
        });

        expandableList.setOnChildClickListener(new OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                DeviceBean device = devicesM2BList.get(childPosition);
                if (device == null)
                    return false;
                else {
                    //改变选中状态
                    for (int i = 0; i < devicesM2BList.size(); i++) {
                        if (i == childPosition) {
                            device.setIsCheck(true/*!device.getIsCheck()*/);
                            m2sTestDevice = devicesM2BList.get(i);
                        } else
                            devicesM2BList.get(i).setIsCheck(false);
                    }

                    m2DeviceListAdapter.notifyDataSetChanged();
                }

                return false;
            }
        });

        expandableList.setOnGroupExpandListener(new OnGroupExpandListener() {
            public void onGroupExpand(int groupPosition) {
                for (int i = 0, count = expandableList.getExpandableListAdapter().getGroupCount(); i < count; i++) {
                    if (groupPosition != i) {// 关闭其他分组  
                        expandableList.collapseGroup(i);
                    }
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
        boolean checkM2A = false;
        boolean checkM2B = false;

        for (int i = 0; i < devicesM2AList.size(); i++) {
            DeviceBean bean = devicesM2AList.get(i);
            if (bean.getIsCheck())
                checkM2A = true;
        }

        for (int i = 0; i < devicesM2BList.size(); i++) {
            DeviceBean bean = devicesM2BList.get(i);
            if (bean.getIsCheck())
                checkM2B = true;
        }

        if (!checkM2A) {
            Toast.makeText(this, R.string.please_select_the_m2_a, Toast.LENGTH_LONG).show();
            return false;
        }

        if (!checkM2B) {
            Toast.makeText(this, R.string.please_select_the_m2_b, Toast.LENGTH_LONG).show();
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
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);

//                    if (devicesM2AList.size() == 0) {
//                        //没有搜索到设备时 显示重新搜索视图
//                        //TODO
//                        //startSearchLayout.setVisibility(View.GONE);
//                        //endSearchLayout.setVisibility(View.VISIBLE);
//                        if (noSearchDevice) {
//                            noSearchDevice = false;
//                            if (!SearchDeviceForM2Activity.this.isFinishing()) {
//                                OpenLocationDialog dialog = new OpenLocationDialog(SearchDeviceForM2Activity.this);
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


    private ArrayList<DeviceBean> devicesM2AList;  //设备列表数据
    private ArrayList<DeviceBean> devicesM2BList;  //设备列表数据

    private class M2DeviceListAdapter extends BaseExpandableListAdapter {
        public M2DeviceListAdapter() {
            super();
            devicesM2AList = new ArrayList<DeviceBean>();
            devicesM2BList = new ArrayList<DeviceBean>();
        }

        public void addM2ADevice(DeviceBean device) {
            String address = device.getAddress();
            for (int i = 0; i < devicesM2AList.size(); i++) {
                if (address.equals(devicesM2AList.get(i).getAddress()))
                    return;
            }
            devicesM2AList.add(device);
        }

        public void clearM2ADevice() {
            devicesM2AList.clear();
        }

        public void addM2BDevice(DeviceBean device) {
            String address = device.getAddress();
            for (int i = 0; i < devicesM2BList.size(); i++) {
                if (address.equals(devicesM2BList.get(i).getAddress()))
                    return;
            }
            devicesM2BList.add(device);
        }

        public void clearM2BDevice() {
            devicesM2BList.clear();
        }

        public Object getChild(int groupPosition, int childPosition) {
            return devicesM2BList.get(childPosition);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild
                , View convertView, ViewGroup parent) {
            ChildViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(SearchDeviceForM2Activity.this).inflate(R.layout.item_search_device_for_m2_child, null);
                holder = new ChildViewHolder();
                holder.itemLayout = (LinearLayout) convertView.findViewById(R.id.itemLayout);
                holder.imgCheck = (ImageView) convertView.findViewById(R.id.imgCheck);
                holder.deviceAddress = (TextView) convertView.findViewById(R.id.device_address);
                holder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
                convertView.setTag(holder);
            } else {
                holder = (ChildViewHolder) convertView.getTag();
            }

            if (devicesM2BList.size() == 0)
                return convertView;
            //加载数据到控件中
            DeviceBean device = devicesM2BList.get(childPosition);

            if (device == null)
                return convertView;

            //名字
            String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                holder.deviceName.setText(deviceName);
            else
                holder.deviceName.setText(R.string.unknown_device);

            //地址
            String address = SearchDeviceForM2Activity.this.getResources().getString(R.string.address);
            holder.deviceAddress.setText(address + device.getAddress());

            //选中状态
            if (device.getIsCheck()) {
                holder.imgCheck.setImageResource(R.drawable.ic_checked);
            } else {
                holder.imgCheck.setImageResource(R.drawable.ic_unchecked);
            }

            return convertView;
        }

        public int getChildrenCount(int childPosition) {
            return devicesM2BList.size();
        }

        public Object getGroup(int groupPosition) {
            return devicesM2AList.get(groupPosition);
        }

        public int getGroupCount() {
            return devicesM2AList.size();
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(SearchDeviceForM2Activity.this).inflate(R.layout.item_search_device_for_m2_group, null);
                holder = new GroupViewHolder();
                holder.itemLayout = (LinearLayout) convertView.findViewById(R.id.itemLayout);
                holder.imgCheck = (ImageView) convertView.findViewById(R.id.imgCheck);
                holder.deviceAddress = (TextView) convertView.findViewById(R.id.device_address);
                holder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
                holder.testBtn = (TestButton) convertView.findViewById(R.id.testBtn);
                convertView.setTag(holder);
            } else {
                holder = (GroupViewHolder) convertView.getTag();
            }

            //加载数据到控件中
            if (devicesM2AList.size() == 0)
                return convertView;

            DeviceBean device = devicesM2AList.get(groupPosition);

            if (device == null)
                return convertView;

            //名字
            String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                holder.deviceName.setText(deviceName);
            else
                holder.deviceName.setText(R.string.unknown_device);

            //地址
            String address = SearchDeviceForM2Activity.this.getResources().getString(R.string.address);
            holder.deviceAddress.setText(address + device.getAddress());

            holder.testBtn.setFocusable(false);

            //选中状态
            if (device.getIsCheck()) {
                holder.imgCheck.setImageResource(R.drawable.ic_checked);
                holder.testBtn.setVisibility(View.VISIBLE);
            } else {
                holder.imgCheck.setImageResource(R.drawable.ic_unchecked);
                holder.testBtn.setVisibility(View.GONE);
            }

            holder.testBtn.setOnClickListener(new TestBtnOnClick(device));

            return convertView;
        }

        /*
         * 控件基类
         */
        private class ChildViewHolder {
            LinearLayout itemLayout;
            ImageView imgCheck;           //选项框图片
            TextView deviceName;          //设备名字
            TextView deviceAddress;       //设备地址
        }

        /*
         * 控件基类
         */
        private class GroupViewHolder {
            LinearLayout itemLayout;
            ImageView imgCheck;           //选项框图片
            TextView deviceName;          //设备名字
            TextView deviceAddress;       //设备地址
            TestButton testBtn;           //测试按钮
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isChildSelectable(int arg0, int arg1) {
            return true;
        }

    }


    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                public void run() {
                    //不为我们的设备时不进行显示
                    if (device == null || StringUtils.isBlank(device.getName()) || !Utils.checkNameForM2(device.getName())
                            || DeviceDBUtils.isAddressExist(db, device.getAddress())) {
                        return;
                    }

                    if (devicesM2AList.size() == 0 && contantLayout.getVisibility() == View.VISIBLE) {
                        startAnimation();
                    }

                    DeviceBean bean;
                    String name = DeviceDBUtils.getDeviceName(db, device.getAddress());
                    if (StringUtils.isBlank(name)) {
                        String deviceName = device.getName();
                        if (device.getName().equals(Constants.DEVICE_BROADCAST_NAME_M2_A) || device.getName().equals(Constants.RELAXER_DEVICE_BROADCAST_NAME_M2_A))
                            deviceName = Constants.RELAXER_DEVICE_BROADCAST_NAME_M2_A;
                        else if (device.getName().equals(Constants.DEVICE_BROADCAST_NAME_M2_B) || device.getName().equals(Constants.RELAXER_DEVICE_BROADCAST_NAME_M2_B))
                            deviceName = Constants.RELAXER_DEVICE_BROADCAST_NAME_M2_B;
                        if (device.getName().equals(Constants.MEDISANA_DEVICE_BROADCAST_NAME_M2_A))
                            deviceName = SearchDeviceForM2Activity.this.getString(R.string.medisana_m2_a_name);
                        else if (device.getName().equals(Constants.MEDISANA_DEVICE_BROADCAST_NAME_M2_B))
                            deviceName = SearchDeviceForM2Activity.this.getString(R.string.medisana_m2_b_name);

                        bean = new DeviceBean(deviceName, device.getAddress());
                    } else {
                        bean = new DeviceBean(name, device.getAddress());
                    }
                    bean.setAction(ActionForM2Constants.getTestAction());
                    bean.getAction().setStrength(1);

                    bean.setDevicebroadcastName(device.getName());
                    if (device.getName().equals(Constants.DEVICE_BROADCAST_NAME_M2_A)
                            || device.getName().equals(Constants.MEDISANA_DEVICE_BROADCAST_NAME_M2_A)
                            || device.getName().equals(Constants.RELAXER_DEVICE_BROADCAST_NAME_M2_A)) {
                        bean.setDeviceVersion(Constants.DEVICE_VERSION_M2_A);
                        m2DeviceListAdapter.addM2ADevice(bean);
                    } else if (device.getName().equals(Constants.DEVICE_BROADCAST_NAME_M2_B)
                            || device.getName().equals(Constants.MEDISANA_DEVICE_BROADCAST_NAME_M2_B)
                            || device.getName().equals(Constants.RELAXER_DEVICE_BROADCAST_NAME_M2_B)) {
                        bean.setDeviceVersion(Constants.DEVICE_VERSION_M2_B);
                        m2DeviceListAdapter.addM2BDevice(bean);
                    }

                    m2DeviceListAdapter.notifyDataSetChanged();
                    deviceNumTxt.setText(devicesM2AList.size() + "");
                }
            });
        }
    };

    /**
     * 开启搜索位移动画
     */
    private void startAnimation() {
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        float y = contantLayout.getY() - startSearchLayout.getY() - statusBarHeight;
        animation = new TranslateAnimation(0, 0, 0, y);
        animation.setDuration(650);//设置动画持续时间
        animation.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation arg0) {
                contantLayout.setVisibility(View.GONE);
                startSearchLayout.setVisibility(View.GONE);
                expandableList.setVisibility(View.VISIBLE);
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


            testDeviceList.clear();
            testDeviceList.add(m2mTestDevice);
            testDeviceList.add(m2sTestDevice);

            //scanLeDevice(false);
            //testDevice = device;
            //cursorAddress = device.getAddress();
            //mBluetoothLeService.connect(device.getAddress());
            //outTimeHandler.postDelayed(outTimeThread, 10000);

            toWriteOrderConnectDevice();
        }
    }

    /**
     * 操作设备写入动作指令
     */
    private void toWriteOrderConnectDevice() {
        if (testDeviceList == null) {
            Toast.makeText(SearchDeviceForM2Activity.this, R.string.device_connect_failure, Toast.LENGTH_LONG).show();
            return;
        }

        if (current >= testDeviceList.size()) {
            current = 0;

            if (switchParam == CONFIGURE)
                switchParam = OPEN;
            else if (switchParam == OPEN) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                cursorAddress = "";
                return;
            }

        }
        showLogD("current : " + current + "      address : " + testDeviceList.get(current).getAddress());

        cursorAddress = testDeviceList.get(current).getAddress();
        if (!mBluetoothLeService.connect(testDeviceList.get(current).getAddress())) {
            showLogD("connect failure");
            //链接失败 TODO
        } else {
            isConfigure = true;
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
                String toast = "error 1113," + SearchDeviceForM2Activity.this.getResources().getString(R.string.connect_failure);
                Toast.makeText(SearchDeviceForM2Activity.this, R.string.device_connect_failure, Toast.LENGTH_LONG).show();
            }

            if (BleWorkService.ACTION_GATT_DISCONNECTED.equals(action)) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                Bundle bundle = intent.getExtras();
                String device = getString(R.string.device);
                String connectFailure = getString(R.string.connect_failure);
                Toast.makeText(SearchDeviceForM2Activity.this, device + " " + bundle.getString("data")
                        + " " + connectFailure, Toast.LENGTH_LONG).show();
            }

            //特征值写入成功广播
            if (BleWorkService.ACTION_CHARACTERISTIC_WRITE_SUCCESS_SEARCH.equals(action)) {
                outTimeHandler.removeCallbacks(outTimeThread);

                switch (switchParam) {
                    case CONFIGURE:
                        if (startStep >= 9 && isSendOver) {
                            startStep = 0;
                            sendStep = 0;
                            current = current + 1;
                            toWriteOrderConnectDevice();
                            //switchParam = OPEN;
                            //startCharacteristicWrite(testDevice,mBluetoothLeService,outTimeHandler, outTimeThread);     //写入开启特征值
                        } else {
                            if (isSendOver) {
                                startStep = startStep + 1;
                                sendStep = 0;
                            } else {
                                sendStep = sendStep + 1;
                            }
                            configureCharacteristicWrite(testDeviceList.get(current), mBluetoothLeService, outTimeHandler, outTimeThread);
                        }
                        break;
                    case OPEN:
                        current = current + 1;
                        toWriteOrderConnectDevice();
					
					/*if(progressDialog.isShowing())
						progressDialog.dismiss();
					
					cursorAddress = "";*/
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
            if (testDeviceList.get(current) != null)
                mBluetoothLeService.close(testDeviceList.get(current).getAddress());
            Toast.makeText(SearchDeviceForM2Activity.this, R.string.connect_failure, Toast.LENGTH_LONG).show();
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

        if (characteristicList.size() == 0) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            if (testDeviceList.get(current) != null)
                mBluetoothLeService.close(testDeviceList.get(current).getAddress());
            String toast = "error 1010," + this.getResources().getString(R.string.connect_failure) + this.getString(R.string.please_try_restart_ble);
            Toast.makeText(SearchDeviceForM2Activity.this, toast, Toast.LENGTH_LONG).show();
            return;
        }

        //onfigureCharacteristicWrite(testDevice,mBluetoothLeService,outTimeHandler, outTimeThread); //写入按摩特征值

        //指令处理
        if (switchParam == CONFIGURE)
            configureCharacteristicWrite(testDeviceList.get(current), mBluetoothLeService, outTimeHandler, outTimeThread); //写入按摩特征值
        else if (switchParam == OPEN)
            startCharacteristicWrite(testDeviceList.get(current), mBluetoothLeService, outTimeHandler, outTimeThread);     //写入开启特征值
    }


    /**
     * 超时Handler
     */
    private class OutTimeHandler extends Handler {
        public void handleMessage(Message msg) {
            if (SearchDeviceForM2Activity.this.isFinishing())
                return;

            showLogD("写入超时");
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            mBluetoothLeService.close(cursorAddress);
            cursorAddress = "";
            String toast = " error 1011," + SearchDeviceForM2Activity.this.getResources().getString(R.string.connect_failure);
            Toast.makeText(SearchDeviceForM2Activity.this, toast, Toast.LENGTH_LONG).show();
        }
    }

}
