package com.releasy.android.constants;

import android.os.Environment;

import com.releasy.android.utils.Utils;

public class Constants {

	//写入switch的值
	public static final int WRITE_0XFFE1 = 0;
	public static final int WRITE_0XFFE2 = 1;
	public static final int WRITE_0XFFE3 = 2;
	public static final int WRITE_0XFFE4 = 3;
	public static final int WRITE_0XFFE5 = 4;
	public static final int WRITE_0XFFE6 = 5;
	public static final int WRITE_0XFFE7 = 7;
	public static final int WRITE_0XFFE8 = 6;
	public static final int WRITE_0XFFEA = 8;
	public static final int WRITE_0XFFEB = 9;
	public static final int WRITE_0XFFB1 = 10;
	public static final int WRITE_0XFFE9 = 11;
	
	//UUID
	public final static String CHARACTERISTIC_0XFFE1 = "0000ffe1-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTIC_0XFFE2 = "0000ffe2-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTIC_0XFFE3 = "0000ffe3-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTIC_0XFFE4 = "0000ffe4-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTIC_0XFFE5 = "0000ffe5-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTIC_0XFFE6 = "0000ffe6-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTIC_0XFFE7 = "0000ffe7-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTIC_0XFFE8 = "0000ffe8-0000-1000-8000-00805f9b34fb";
	public final static String CHARACTERISTIC_0XFFE9 = "0000ffe9-0000-1000-8000-00805f9b34fb";
	
	//roomType 
	public final static int SINGLE_TYPE = 0;                           //单一按摩动作类型
	public final static int MULTIPLE_TYPE = 1;                         //多个按摩动作类型
	public final static int USER_DEFINED_TYPE = 2;                     //自定义按摩动作类型
	public final static int ACTION_DISTRIBUTION_FOR_M2_TYPE = 3;       //M2双机动作分配类型
	public final static int ACTION_COUNTENANCE_TYPE = 4;               //表情识别类型
	
	//获取结束字节
	public static Byte getEndByte(){
		return Utils.u8ToByte((short)0xff);
	}
	
	public static final String ROOT_FILE = Environment.getExternalStorageDirectory()+"/Releasy/";
	public static final String MUSIC = ROOT_FILE + "Music/";
	public static final String UPDATA = ROOT_FILE + "Updata/";

	public final static String SHARE = "com.releasy.android.SHARE";
	
	
	public final static String SELECT_DEVICE_UNKNOWN = "unknown";
	public final static String SELECT_DEVICE_M1 = "m1";
	public final static String SELECT_DEVICE_M2 = "m2";
	
	public final static String DEVICE_VERSION_M1 = "M1";
	public final static String DEVICE_VERSION_M2_A = "M2_A";
	public final static String DEVICE_VERSION_M2_B = "M2_B";
	
	public final static String DEVICE_NAME_M1 = "Relaxer";
	public final static String DEVICE_NAME_M2_A = "Relaxer Main";
	public final static String DEVICE_NAME_M2_B = "Relaxer Sub";
	public final static String DEVICE_BROADCAST_NAME_M1 = "MOOYEE";
	public final static String DEVICE_BROADCAST_NAME_M2_A = "MOOYEE-2A";
	public final static String DEVICE_BROADCAST_NAME_M2_B = "MOOYEE-2B";

	public final static String MEDISANA_DEVICE_BROADCAST_NAME_M2_A = "Medisana M2 Main";
	public final static String MEDISANA_DEVICE_BROADCAST_NAME_M2_B = "Medisana M2 Sub";
	public final static String RELAXER_DEVICE_BROADCAST_NAME_M2_A = "Relaxer M2-A";
	public final static String RELAXER_DEVICE_BROADCAST_NAME_M2_B = "Relaxer M2-B";
	
	
	public final static int DB_VERSION = 3;
}
