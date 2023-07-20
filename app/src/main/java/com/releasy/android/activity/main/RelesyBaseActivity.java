package com.releasy.android.activity.main;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.releasy.android.R;
import com.releasy.android.ReleasyApplication;
import com.releasy.android.bean.DeviceBean;
import com.releasy.android.constants.Constants;
import com.releasy.android.service.BleWorkService;
import com.releasy.android.utils.SendOrderOutTimeThread;
import com.releasy.android.utils.SharePreferenceUtils;
import com.releasy.android.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class RelesyBaseActivity extends BaseActivity{
	
	protected ArrayList<BluetoothGattCharacteristic> characteristicList = new ArrayList<BluetoothGattCharacteristic>();
	
	protected static final int CONFIGURE = 0;      //写入按摩参数
	protected static final int OPEN = 1;           //进行开始写入
	protected static final int CLOSE = 2;          //没有进行写入
	protected static final int UPDATA_POWER = 3;   //更新力度
	protected static final int UPDATA_OPEN = 4;    //更新力度开始
	protected int switchParam = CLOSE;             //操作状态游标
	protected int presentParam = CLOSE;            //当前操作状态
	protected boolean isConfigure = false;         //连接状态
	
	protected boolean isChangePower = false;       //是否为改变力度操作
	protected int current = 0;                     //设备游标
	protected int startStep = 0;                   //开启步骤
	protected boolean isSendOver = true;           //数据分包发送结束
	protected int sendStep = 0;                    //发送步骤
	protected int failCount = 0;                   //失败次数
	private SharePreferenceUtils spInfo;           //SharePreference
	protected boolean isTest = false;
	protected String selectDevice = "unknown";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		registerReceiver(mHomeKeyEventReceiver, new IntentFilter(  
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));  
		
		spInfo = new SharePreferenceUtils(this); //获取SharePreference 存储
	}

	protected void onDestroy() {
		super.onDestroy();
		
		unregisterReceiver(mHomeKeyEventReceiver); //解绑广播
	}
	
	/**
	 * 写入按摩特征值
	 */
	protected void configureCharacteristicWrite(DeviceBean device, BleWorkService service
			, Handler outTimeHandler, SendOrderOutTimeThread outTimeThread){
		switch(startStep){
		case Constants.WRITE_0XFFE1:
			if (characteristicList != null) {
				for(int i = 0; i < characteristicList.size(); i++){
					BluetoothGattCharacteristic characteristicMode = characteristicList.get(i);
					
					if (characteristicMode.getUuid().toString().equals("0000ffe1-0000-1000-8000-00805f9b34fb")) {
						if(isTest){
							if(selectDevice.equals(Constants.SELECT_DEVICE_M2))
								listDataWriteU8ToByte(service,characteristicMode,device.getAction().getCheckChangeMode());
							else
								shortDataWriteU8ToByte(service,characteristicMode,device.getAction().getBytesCheck());
						}
						else{
							if(spInfo.getSelectDevice().equals(Constants.SELECT_DEVICE_M2))
								listDataWriteU8ToByte(service,characteristicMode,device.getAction().getCheckChangeMode());
							else
								shortDataWriteU8ToByte(service,characteristicMode,device.getAction().getBytesCheck());
						}
						showLogD(device.getAddress() + "  characteristic 0xFFE1 ");
						outTimeHandler.postDelayed(outTimeThread, 5000);
					}	
				}
			}
			
			break;
			
		case Constants.WRITE_0XFFE2:
			if (characteristicList != null) {
				for(int i = 0; i < characteristicList.size(); i++){
					BluetoothGattCharacteristic characteristicMode = characteristicList.get(i);
					
					if (characteristicMode.getUuid().toString().equals("0000ffe2-0000-1000-8000-00805f9b34fb")) {
						listDataWriteU16ToByte(service,characteristicMode,device.getAction().getHighTime());
						showLogD(device.getAddress() + "  characteristic 0xFFE2 ");
						outTimeHandler.postDelayed(outTimeThread, 5000);
					}	
				}
			}
			break;
		
		case Constants.WRITE_0XFFE3:	
			if (characteristicList != null) {
				for(int i = 0; i < characteristicList.size(); i++){
					BluetoothGattCharacteristic characteristicMode = characteristicList.get(i);
					
					if (characteristicMode.getUuid().toString().equals("0000ffe3-0000-1000-8000-00805f9b34fb")) {
						listDataWriteU16ToByte(service,characteristicMode,device.getAction().getLowTime());
						showLogD(device.getAddress() + "  characteristic 0xFFE3 ");
						outTimeHandler.postDelayed(outTimeThread, 5000);
					}	
				}
			}
			break;
		
		case Constants.WRITE_0XFFE4:	
			if (characteristicList != null) {
				for(int i = 0; i < characteristicList.size(); i++){
					BluetoothGattCharacteristic characteristicMode = characteristicList.get(i);
					
					if (characteristicMode.getUuid().toString().equals("0000ffe4-0000-1000-8000-00805f9b34fb")) {
						listDataWriteU8ToByte(service,characteristicMode,device.getAction().getInnerHighAndLow());
						showLogD(device.getAddress() + "  characteristic 0xFFE4 ");
						outTimeHandler.postDelayed(outTimeThread, 5000);
					}	
				}
			}
			break;	
			
		case Constants.WRITE_0XFFE5:	
			if (characteristicList != null) {
				for(int i = 0; i < characteristicList.size(); i++){
					BluetoothGattCharacteristic characteristicMode = characteristicList.get(i);
					
					if (characteristicMode.getUuid().toString().equals("0000ffe5-0000-1000-8000-00805f9b34fb")) {
						listDataWriteU16ToByte(service,characteristicMode,device.getAction().getInterval());
						showLogD(device.getAddress() + "  characteristic 0xFFE5 ");
						outTimeHandler.postDelayed(outTimeThread, 5000);
					}	
				}
			}
			break;
		
		case Constants.WRITE_0XFFE6:	
			if (characteristicList != null) {
				for(int i = 0; i < characteristicList.size(); i++){
					BluetoothGattCharacteristic characteristicMode = characteristicList.get(i);
					
					if (characteristicMode.getUuid().toString().equals("0000ffe6-0000-1000-8000-00805f9b34fb")) {
						listDataWriteU16ToByte(service,characteristicMode,device.getAction().getPeriod());
						showLogD(device.getAddress() + "  characteristic 0xFFE6 ");
						outTimeHandler.postDelayed(outTimeThread, 5000);
					}	
				}
			}
			break;	
			
		case Constants.WRITE_0XFFE8:	
			if (characteristicList != null) {
				for(int i = 0; i < characteristicList.size(); i++){
					BluetoothGattCharacteristic characteristicMode = characteristicList.get(i);
					
					if (characteristicMode.getUuid().toString().equals("0000ffe8-0000-1000-8000-00805f9b34fb")) {
						showLogD("device.getAction().getStopTime() : " + device.getAction().getStopTime());
						shortDataWriteU16ToByte(service,characteristicMode,device.getAction().getStopTime());
						showLogD(device.getAddress() + "  characteristic 0xFFE8 ");
						outTimeHandler.postDelayed(outTimeThread, 5000);
					}	
				}
			}
			break;
			
		case Constants.WRITE_0XFFE7:	
			if (characteristicList != null) {
				for(int i = 0; i < characteristicList.size(); i++){
					BluetoothGattCharacteristic characteristicMode = characteristicList.get(i);
					
					if (characteristicMode.getUuid().toString().equals("0000ffe7-0000-1000-8000-00805f9b34fb")) {
						
						int [] rate;
						int PowerLV[][] = device.getAction().getPowerLV();
						rate = PowerLV[device.getAction().getStrength() - 1];
						
						for(int k = 0; k < rate.length; k++){
							Log.d("z17m","    rate " + k + ": " + rate[k]);
						}
						
						
						/*if(spInfo.getSelectDevice().equals(Constants.SELECT_DEVICE_M2))
							rate = Utils.getRate(device.getAction().getRateMin(),device.getAction().getRateMax()
									,device.getAction().getStrength());
						else
							rate = Utils.getRate(device.getAction().getRateMin(),device.getAction().getRateMax()
									,device.getAction().getStrength());*/
						
						
						//showLogD("device.getAction().getStrength() : " + device.getAction().getStrength());
						listDataWriteU16ToByte(service,characteristicMode,rate);
						showLogD(device.getAddress() + "  characteristic 0xFFE7 ");
						outTimeHandler.postDelayed(outTimeThread, 5000);
					}	
				}
			}
			break;
			
		case Constants.WRITE_0XFFEA:	
			if (characteristicList != null) {
				for(int i = 0; i < characteristicList.size(); i++){
					BluetoothGattCharacteristic characteristicMode = characteristicList.get(i);
					
					if (characteristicMode.getUuid().toString().equals("0000ffea-0000-1000-8000-00805f9b34fb")) {
						listDataWriteU8ToByte(service,characteristicMode,device.getAction().getMinMaxList());
						showLogD(device.getAddress() + "  characteristic 0xFFEa ");
						outTimeHandler.postDelayed(outTimeThread, 5000);
					}	
				}
			}
			break;
			
		case Constants.WRITE_0XFFEB:	
			if (characteristicList != null) {
				for(int i = 0; i < characteristicList.size(); i++){
					BluetoothGattCharacteristic characteristicMode = characteristicList.get(i);
					
					if (characteristicMode.getUuid().toString().equals("0000ffeb-0000-1000-8000-00805f9b34fb")) {
						listDataWriteU8ToByte(service,characteristicMode,device.getAction().getMode12List());
						showLogD(device.getAddress() + "  characteristic 0xFFEb ");
						outTimeHandler.postDelayed(outTimeThread, 5000);
					}	
				}
			}
			break;
		case Constants.WRITE_0XFFB1:	
			if (characteristicList != null) {
				for(int i = 0; i < characteristicList.size(); i++){
					BluetoothGattCharacteristic characteristicMode = characteristicList.get(i);
					
					if (characteristicMode.getUuid().toString().equals("0000ffb1-0000-1000-8000-00805f9b34fb")) {
						service.setCharacteristicNotification(characteristicMode, true);
						showLogD(device.getAddress() + "  characteristic 0xFFb1 ");
						outTimeHandler.postDelayed(outTimeThread, 5000);
					}	
				}
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 写入开始特征值
	 * @throws InterruptedException 
	 */
	protected void startCharacteristicWrite(DeviceBean device, BleWorkService service
			,Handler outTimeHandler, SendOrderOutTimeThread outTimeThread){
		if (characteristicList != null) {
			for(int i = 0; i < characteristicList.size(); i++){
				BluetoothGattCharacteristic characteristicMode = characteristicList.get(i);
				
				if (characteristicMode.getUuid().toString().equals("0000ffe9-0000-1000-8000-00805f9b34fb")) {
					byte[] sendTmp = new byte[2];
					sendTmp[0] = Utils.u8ToByte((short)1);
					sendTmp[1] = Constants.getEndByte();
					characteristicMode.setValue(sendTmp);
					service.wirteCharacteristic(characteristicMode);
					showLogD(device.getAddress() + "  characteristic 0xFFE9 ");
					outTimeHandler.postDelayed(outTimeThread, 5000);
				}	
			}
		}
	}
	
	/**
	 * 写入停止特征值
	 * @throws InterruptedException 
	 */
	protected void stopCharacteristicWrite(DeviceBean device, BleWorkService service,
			Handler outTimeHandler, SendOrderOutTimeThread outTimeThread){
		if (characteristicList != null) {
			 for(int i = 0; i < characteristicList.size(); i++){
				BluetoothGattCharacteristic characteristicMode = characteristicList.get(i);
				if (characteristicMode.getUuid().toString().equals("0000ffe9-0000-1000-8000-00805f9b34fb")) {
					byte[] sendTmp = new byte[2];
					sendTmp[0] = Utils.u8ToByte((short)3);
					sendTmp[1] = Constants.getEndByte();
					characteristicMode.setValue(sendTmp);
					service.wirteCharacteristic(characteristicMode);
					showLogD(" characteristic 0xFFE9 ");
					outTimeHandler.postDelayed(outTimeThread, 5000);
				}	
			}
		 }
	}
	
	/**
	 * short型数据转为8位Byte
	 */
	private void shortDataWriteU8ToByte(BleWorkService service
			, BluetoothGattCharacteristic characteristicMode, int shortData){
		byte[] sendTmp;
		sendTmp = new byte[2];
		sendTmp[0] = Utils.u8ToByte(shortData);
		sendTmp[1] = Constants.getEndByte();
		characteristicMode.setValue(sendTmp);
		service.wirteCharacteristic(characteristicMode);
	}
	
	/**
	 * short型数据转为16位Byte
	 */
	private void shortDataWriteU16ToByte(BleWorkService service
			, BluetoothGattCharacteristic characteristicMode, int shortData){
		byte[] sendTmp;
		sendTmp = new byte[3];
		byte[] temporary = new byte[2];
		temporary = Utils.shortToByteArray(shortData);
		sendTmp[0] = temporary[0];
		sendTmp[1] = temporary[1];
		sendTmp[2] = Constants.getEndByte();
		characteristicMode.setValue(sendTmp);
		service.wirteCharacteristic(characteristicMode);
	}
	
	/**
	 * short[]数组型数据转为8位Byte
	 */
	private void listDataWriteU8ToByte(BleWorkService service
			, BluetoothGattCharacteristic characteristicMode, int[] listData){
		byte[] sendTmp;
		
		if(listData.length - 20*sendStep >= 20){
			sendTmp = new byte[20];
			isSendOver = false;
			
			for(int j = 0; j < 20; j++){
				sendTmp[j] = Utils.u8ToByte(listData[j + 20*sendStep]);
			}
		}
		else{
			sendTmp = new byte[listData.length%20+1];
			isSendOver = true;
			
			for(int j = 20*sendStep; j < listData.length; j++){
				sendTmp[j%20] = Utils.u8ToByte(listData[j]);
			}
			
			sendTmp[listData.length%20] = Constants.getEndByte();
		}

		
		characteristicMode.setValue(sendTmp);
		service.wirteCharacteristic(characteristicMode);
	}
	
	/**
	 * short[]数组型数据转为16位Byte
	 */
	private void listDataWriteU16ToByte(BleWorkService service
			, BluetoothGattCharacteristic characteristicMode, int[] listData){
		showLogD("listDataWriteU16ToByte listData.length : " + listData.length);
		byte[] sendTmp;
		if(listData.length - 10*sendStep >= 10){
			sendTmp = new byte[20];
			isSendOver = false;
			
			for(int j = 0; j < 10; j++){
				byte[] temporary = new byte[2];
				temporary = Utils.shortToByteArray(listData[j + 10*sendStep]);
				sendTmp[j*2] = temporary[0];
				sendTmp[j*2+1] = temporary[1];
			}
		}
		else{
			sendTmp = new byte[(listData.length%10)*2+1];
			isSendOver = true;
			
			for(int j = 10*sendStep; j < listData.length; j++){
				byte[] temporary = new byte[2];
				temporary = Utils.shortToByteArray(listData[j]);
				sendTmp[(j%10)*2] = temporary[0];
				sendTmp[(j%10)*2+1] = temporary[1];
			}
			
			sendTmp[(listData.length%10)*2] = Constants.getEndByte();
		}
		
		characteristicMode.setValue(sendTmp);
		service.wirteCharacteristic(characteristicMode);
	}
	
	protected void initViews() {}
	protected void initEvents() {}
	
	/** 
     * 监听是否点击了home键将客户端推到后台 
     */  
    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {  
        String SYSTEM_REASON = "reason";  
        String SYSTEM_HOME_KEY = "homekey";  
        String SYSTEM_HOME_KEY_LONG = "recentapps";  
            
        public void onReceive(Context context, Intent intent) {  
            String action = intent.getAction();  
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {  
                String reason = intent.getStringExtra(SYSTEM_REASON);  
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {  
                     //表示按了home键,程序到了后台  
                	ReleasyApplication app = (ReleasyApplication) RelesyBaseActivity.this.getApplication();
                	if(!Utils.isBackground(RelesyBaseActivity.this) && app.getIsWorking())
                		RelesyBaseActivity.this.finish();
                    
                }else if(TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)){  
                    //表示长按home键,显示最近使用的程序列表  
                }  
            }   
        }  
    };
    
    protected boolean checkDeviceVerify(List<DeviceBean> deviceList){
    	boolean hasPiracy = false;
//    	for(int i = 0; i < deviceList.size(); i++){
//    		if(deviceList.get(i).getVerifyStatus() == 2){
//    			hasPiracy = true;
//    			Toast.makeText(this, deviceList.get(i).getAddress()
//    					+ " " + this.getString(R.string.piracy_devices), Toast.LENGTH_LONG).show();
//    		}
//    	}
    	
		return hasPiracy;
    }
    
    
    //分享
    protected void openShare(){}

}
