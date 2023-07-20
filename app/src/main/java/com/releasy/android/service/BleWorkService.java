/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.releasy.android.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.releasy.android.R;
import com.releasy.android.ReleasyApplication;
import com.releasy.android.utils.Utils;


/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 */
@SuppressLint("NewApi")
public class BleWorkService extends Service {
	private final static String TAG = BleWorkService.class.getSimpleName();
	
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	//private BluetoothGatt mBluetoothGatt;
	private int mConnectionState = STATE_DISCONNECTED;

	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;

	public final static String ACTION_GATT_CONNECTED = "com.releasy.android.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.releasy.android.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.releasy.android.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "com.releasy.android.ACTION_DATA_AVAILABLE";
	public final static String EXTRA_DATA = "com.releasy.android.EXTRA_DATA";
	public final static String ACTION_GATT_CONNECTED_133 = "com.releasy.android.ACTION_GATT_CONNECTED_133";
	public final static String ACTION_CHARACTERISTIC_WRITE_SUCCESS = "com.releasy.android.CHARACTERISTIC_WRITE_SUCCESS";
	public final static String ACTION_CHARACTERISTIC_WRITE_133 = "com.releasy.android.CHARACTERISTIC_WRITE_133";
	
	public final static String ACTION_GATT_SERVICES_DISCOVERED_SEARCH = "com.releasy.android.ACTION_GATT_SERVICES_DISCOVERED_SEARCH";
	public final static String ACTION_GATT_CONNECTED_133_SEARCH = "com.releasy.android.ACTION_GATT_CONNECTED_133_SEARCH";
	public final static String ACTION_CHARACTERISTIC_WRITE_SUCCESS_SEARCH = "com.releasy.android.CHARACTERISTIC_WRITE_SUCCESS_SEARCH";
	public final static String ACTION_CHARACTERISTIC_WRITE_133_SEARCH = "com.releasy.android.CHARACTERISTIC_WRITE_133_SEARCH";
	
	public final static String DEVICE_IS_NOT_LOAD = "com.releasy.android.DEVICE_IS_NOT_LOAD";
	public final static String DEVICE_ALL_IS_NOT_LOAD = "com.releasy.android.DEVICE_ALL_IS_NOT_LOAD";
	
	public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID
			.fromString("0000C004-0000-1000-8000-00805f9b34fb");

	private HashMap<String,BluetoothGatt> mapGatt = new HashMap<String,BluetoothGatt>();
	//private int reconnection = 0;
	
	private boolean isSearch = false;
	
	private ReleasyApplication app;                      //Application
	
	// Implements callback methods for GATT events that the app cares about. For
	// example,
	// connection change and services discovered.
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			String intentAction;
			Utils.showLogD("BluetoothGattCallback onConnectionStateChange ----- status:" + status);
			
			if(status == 133){
				intentAction = ACTION_GATT_CONNECTED_133;
				broadcastUpdate(intentAction);
				
				if(isSearch){
					intentAction = ACTION_GATT_CONNECTED_133_SEARCH;
					broadcastUpdate(intentAction);
				}
				return;
			}
			
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				broadcastUpdate(intentAction);
				Log.i(TAG, "Connected to GATT server.");
				// Attempts to discover services after successful connection.
				Log.i(TAG, "Attempting to start service discovery:"
						+ mapGatt.get(mBluetoothDeviceAddress).discoverServices());

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				intentAction = ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				Utils.showLogD("Disconnected from GATT server.");
				//Log.d("z17m", "gatt.getDevice().getAddress() : " + gatt.getDevice().getAddress());
				String address = gatt.getDevice().getAddress();
				mapGatt.remove(gatt.getDevice().getAddress());
				
				broadcastUpdate(intentAction,address);
			}
		}

		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			Utils.showLogD("BluetoothGattCallback onServicesDiscovered ----- status:" + status);
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED,gatt.getDevice().getAddress());
				if(isSearch)
					broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED_SEARCH,gatt.getDevice().getAddress());
			} else {
				Log.w(TAG, "onServicesDiscovered received: " + status);
			}
		}

		//读取特征值的回调函数
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			Utils.showLogD("BluetoothGattCallback onCharacteristicRead ----- status:" + status);
			/*if (status == BluetoothGatt.GATT_SUCCESS) {
				//broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
				Log.d("z17m","characteristic : " + characteristic.getStringValue(0));
			}*/
			//broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			
			//String data = new String(characteristic.getValue());
			//Log.d("z17m","data : " + data);  
			broadcastUpdate(ACTION_DATA_AVAILABLE,characteristic.getValue());
		}

		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			
			Utils.showLogD("BluetoothGattCallback onDescriptorWriteonDescriptorWrite ----- status:" + status
					+ ", descriptor =" + descriptor.getUuid().toString());
			
			//TODO
			broadcastUpdate(ACTION_CHARACTERISTIC_WRITE_SUCCESS);
		}

		//特征值改变的回调函数
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			//broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			if (characteristic.getUuid().toString().equals("0000ffb1-0000-1000-8000-00805f9b34fb")) {
				Log.d("z17m","onCharacteristicChanged ------- gatt.getDevice().getAddress() : " + gatt.getDevice().getAddress());
				ReleasyApplication app = (ReleasyApplication) BleWorkService.this.getApplication(); //获取Application
				app.setDeviceIsNotLoad(gatt.getDevice().getAddress());
			}
			else{
				if (characteristic.getValue() != null) {
					
					System.out.println(characteristic.getStringValue(0));
					
					
					Utils.showLogD("BluetoothGattCallback onCharacteristicChanged ----- " + characteristic.getStringValue(0));
				}
				Utils.showLogD("BluetoothGattCallback onCharacteristicChanged");
			}
		}

		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			System.out.println("rssi = " + rssi);
			Utils.showLogD("BluetoothGattCallback onReadRemoteRssi ----- rssi = " + rssi);
		}

		//写入特征值的回调函数
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			Utils.showLogD("BluetoothGattCallback onCharacteristicWrite ----- write success----- status:" + status);
			
			
			//出现133时的处理
			if(status == 133){
				//mapGatt.get(mBluetoothDeviceAddress).close();
				//mapGatt.remove(mBluetoothDeviceAddress);
				//connect(mBluetoothDeviceAddress);
				//broadcastUpdate(ACTION_CHARACTERISTIC_WRITE_133);
				return;
			}
			
			broadcastUpdate(ACTION_CHARACTERISTIC_WRITE_SUCCESS);
			if(isSearch)
				broadcastUpdate(ACTION_CHARACTERISTIC_WRITE_SUCCESS_SEARCH);
		};
	};

	//广播发送
	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}
	
	//广播发送
	private void broadcastUpdate(final String action,String data) {
		final Intent intent = new Intent(action);
		intent.putExtra("data", data);
		sendBroadcast(intent);
	}
	
	//广播发送
	private void broadcastUpdate(final String action,byte[] data) {
		final Intent intent = new Intent(action);
		intent.putExtra("data", data);
		sendBroadcast(intent);
	}

	//广播发送
	private void broadcastUpdate(final String action,
			final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);

		// This is special handling for the Heart Rate Measurement profile. Data
		// parsing is
		// carried out as per profile specifications:
		// http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
		if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
			int flag = characteristic.getProperties();
			int format = -1;
			if ((flag & 0x01) != 0) {
				format = BluetoothGattCharacteristic.FORMAT_UINT16;
				//Log.d(TAG, "Heart rate format UINT16.");
			} else {
				format = BluetoothGattCharacteristic.FORMAT_UINT8;
				//Log.d(TAG, "Heart rate format UINT8.");
			}
			final int heartRate = characteristic.getIntValue(format, 1);
			//Log.d("z17m","Received heart rate: %d" + heartRate);
			//Log.d("z17m", String.format("Received heart rate: %d", heartRate));
			intent.putExtra("data", String.valueOf(heartRate));
		} else {
			// For all other profiles, writes the data formatted in HEX.
			final byte[] data = characteristic.getValue();
			if (data != null && data.length > 0) {
				final StringBuilder stringBuilder = new StringBuilder(
						data.length);
				for (byte byteChar : data)
					stringBuilder.append(String.format("%02X ", byteChar));

				Log.d("z17m","data : " + new String(data));
				intent.putExtra("data", new String(data));
			}
		}
		sendBroadcast(intent);
	}

	public class LocalBinder extends Binder {
		public BleWorkService getService() {
			return BleWorkService.this;
		}
	}

	public IBinder onBind(Intent intent) {
		Utils.showLogD(" BluetoothLeService onBind");
		return mBinder;
	}

	public boolean onUnbind(Intent intent) {
		// After using a given device, you should make sure that
		// BluetoothGatt.close() is called
		// such that resources are cleaned up properly. In this particular
		// example, close() is
		// invoked when the UI is disconnected from the Service.
		Utils.showLogD(" BluetoothLeService onUnbind");
		closeAll();
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 * 
	 * @return Return true if the initialization is successful.
	 */
	//
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter
		// through
		// BluetoothManager.
		//判断蓝牙是否可用
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 * 
	 * @param address
	 *            The device address of the destination device.
	 * 
	 * @return Return true if the connection is initiated successfully. The
	 *         connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	//链接蓝牙方法
	public boolean connect(final String address) {
		if (mBluetoothAdapter == null || address == null) {
			Utils.showLogD("BluetoothAdapter not initialized or unspecified address.");
			return false;
		}
		
		
		Utils.showLogD("BondState : " + mBluetoothAdapter.getRemoteDevice(address).getBondState()
				+ "    Type : " + mBluetoothAdapter.getRemoteDevice(address).getType());
		
		//传入地址为当前已经连接设备
		if(mapGatt.get(address) != null){
			mBluetoothDeviceAddress = address;
			broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED,address);
			if(isSearch){
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED_SEARCH,address);
			}
		}
		else{
			final BluetoothDevice device = mBluetoothAdapter
			.getRemoteDevice(address);
			
			if (device == null) {
				Utils.showLogD("Device not found.  Unable to connect.");
				broadcastUpdate(ACTION_GATT_CONNECTED_133);
				return false;
			}
			
			// We want to directly connect to the device, so we are setting the
			// autoConnect
			// parameter to false.
			Utils.showLogD("mapGatt.get(address) == null");
			BluetoothGatt mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
			mBluetoothDeviceAddress = address;
			mConnectionState = STATE_CONNECTING;

			
			Utils.showLogD("put in map , address : " + address);
			mapGatt.put(address, mBluetoothGatt);
			
		}
		
		return true;
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	//蓝牙断链方法  断开当前设备
	public void disconnect() {
		if (mBluetoothAdapter == null || mapGatt.get(mBluetoothDeviceAddress) == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mapGatt.get(mBluetoothDeviceAddress).disconnect();
		mapGatt.remove(mBluetoothDeviceAddress);
	}
	
	//蓝牙断链方法  断开当前设备
	public void disconnect(String address) {
		if (mBluetoothAdapter == null || mapGatt.get(address) == null) {
			Utils.showLogD("BluetoothAdapter not initialized   " + address);
			return;
		}
		mapGatt.get(address).disconnect();
		mapGatt.remove(address);
	}
	
	//蓝牙断链方法  断开所有设备
	public void disconnectAll(){
		if (mapGatt == null) {
			return;
		}

		Utils.showLogD("mapGatt size : " + mapGatt.size());
		
        Iterator itor = mapGatt.keySet().iterator();  
        while(itor.hasNext())  {  
        	String key = (String)itor.next();  
        	mapGatt.get(key).disconnect();  
        }  
        mapGatt.clear();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	//蓝牙关闭方法  关闭当前Gatt
	public void close() {
		if (mapGatt.get(mBluetoothDeviceAddress) == null) {
			return;
		}
		mapGatt.get(mBluetoothDeviceAddress).close();
		mapGatt.remove(mBluetoothDeviceAddress);
	}
	
	//蓝牙关闭方法  关闭指定Gatt
	public void close(String address) {
		if (mapGatt.get(address) == null) {
			return;
		}
		mapGatt.get(address).close();
		mapGatt.remove(address);
	}
	
	//蓝牙关闭方法  关闭所有Gatt
	public void closeAll(){
		if (mapGatt == null || mapGatt.size() == 0) {
			return;
		}

		Utils.showLogD("mapGatt size : " + mapGatt.size());
		
        Iterator itor = mapGatt.keySet().iterator();  
        while(itor.hasNext())  {  
        	String key = (String)itor.next();  
        	mapGatt.get(key).close();  
        }  
        
        mapGatt.clear();
	}

	//特征值写入方法
	public void wirteCharacteristic(BluetoothGattCharacteristic characteristic) {

		if (mBluetoothAdapter == null || mapGatt.get(mBluetoothDeviceAddress) == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}

		mapGatt.get(mBluetoothDeviceAddress).writeCharacteristic(characteristic);
		
		
	}
	
	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 * 
	 * @param characteristic
	 *            The characteristic to read from.
	 */
	//特征值读取方法
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mapGatt.get(mBluetoothDeviceAddress) == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mapGatt.get(mBluetoothDeviceAddress).readCharacteristic(characteristic);
	}

	/**
	 * Enables or disables notification on a give characteristic.
	 * 
	 * @param characteristic
	 *            Characteristic to act on.
	 * @param enabled
	 *            If true, enable notification. False otherwise.
	 */
	public void setCharacteristicNotification(
			BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mapGatt.get(mBluetoothDeviceAddress) == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mapGatt.get(mBluetoothDeviceAddress).setCharacteristicNotification(characteristic, enabled);
		BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID
				.fromString("00002902-0000-1000-8000-00805f9b34fb"));
		if (descriptor != null) {
			System.out.println("write descriptor");
			descriptor
					.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mapGatt.get(mBluetoothDeviceAddress).writeDescriptor(descriptor);
		}
		/*
		 * // This is specific to Heart Rate Measurement. if
		 * (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
		 * System
		 * .out.println("characteristic.getUuid() == "+characteristic.getUuid
		 * ()+", "); BluetoothGattDescriptor descriptor =
		 * characteristic.getDescriptor
		 * (UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
		 * descriptor
		 * .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		 * mBluetoothGatt.writeDescriptor(descriptor); }
		 */
	}

	/**
	 * Retrieves a list of supported GATT services on the connected device. This
	 * should be invoked only after {@code BluetoothGatt#discoverServices()}
	 * completes successfully.
	 * 
	 * @return A {@code List} of supported services.
	 */
	//获取蓝牙UUID服务列表
	public List<BluetoothGattService> getSupportedGattServices() {
		if (mapGatt.get(mBluetoothDeviceAddress) == null)
			return null;

		//mapGatt.get(mBluetoothDeviceAddress).discoverServices();
		return mapGatt.get(mBluetoothDeviceAddress).getServices();
	}

	/**
	 * Read the RSSI for a connected remote device.
	 * */
	public boolean getRssiVal() {
		if (mapGatt.get(mBluetoothDeviceAddress) == null)
			return false;

		return mapGatt.get(mBluetoothDeviceAddress).readRemoteRssi();
	}
	
	
	/**
	 * 打开搜索
	 * */
	public void openSearch(){
		isSearch = true;
	}
	/**
	 * 关闭搜索
	 * */
	public void closeSearch(){
		isSearch = false;
	}
	
}
