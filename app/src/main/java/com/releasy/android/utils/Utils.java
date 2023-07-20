package com.releasy.android.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import com.releasy.android.ReleasyApplication;
import com.releasy.android.constants.Constants;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;

public class Utils {
	
	/**
     * 无符号16bit int 转化为 byte
     *
     * @return
     */
    static public byte[] shortToByteArray(int param)
    {

        byte[] result = new byte[2];
        result[0] = (byte) ((param >> 8) & 0xFF);
        result[1] = (byte) (param & 0xFF);
        return result;

    }
	
    /**
     * 无符号8bit short 转化为 byte
     *
     * @return
     */
	public static byte u8ToByte(int param)
    {
        byte result = (byte) (param & 0xFF);
        return result;
    }
	
	/**
	 * 获取当前时间
	 * 返回格式为yyyy-mm-dd hh:mm:ss
	 * 如：2013-11-05 12:00:00
	 */
	public static String getTime(){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     
		String date = sDateFormat.format(new java.util.Date()); 
		return date;
	}
	
	/**
	 * 获取当前时间
	 * 返回格式为yyyy-mm-dd hh:mm:ss
	 * 如：2013-11-05 12:00:00
	 */
	public static String getTime2(){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");     
		String date = sDateFormat.format(new java.util.Date()); 
		return date;
	}
	
	/**
	 * 获取当前时间
	 * 返回格式为yyyy-mm-dd
	 * 如：2013-11-05
	 */
	public static String getTime3(){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");     
		String date = sDateFormat.format(new java.util.Date()); 
		return date;
	}
	
	/**
	 * 单位换算 dp转为px
	 * 返回dp对应的px象数值
	 */
	public static int dp2px(int dp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}
	
	/**
	 * 根据最小、最大占空比 以及力度计算出当前占空比
	 * 传入RateMin RateMin Strength
	 * 返回Rate
	 */
	public static int[] getRate(int[] rateMin, int[] rateMax, int strength){
		int[] rate = new int[rateMin.length];
		Log.d("z17m","strength : " + strength);
		
		if(strength == 1)
			return rateMin;
		else if(strength == 15)
			return rateMax;
		else{
			for(int i = 0; i < rate.length; i++){
				float increase = (float)(rateMax[i] - rateMin[i])/(float)14.0 * (float)(strength-1);
				rate[i] = (int) (rateMin[i] + increase);
				Log.d("z17m","increase : " + increase + "    rate " + i + ": " + rate[i]);
			}
		}
		
		return rate;
	}
	
	/**
	 * 判断是否存在SD卡
	 * @return
	 */
	public static boolean hasSdcard() {
	     String status = Environment.getExternalStorageState();
	     if (status.equals(Environment.MEDIA_MOUNTED)) {
	         return true;
	     } else {
	         return false;
	     }
	 }
	
	/**
	 * 删除文件夹下所有文件
	 */
    public static void deleteFile(String filePath) {  
    	Log.d("z17m","filePath : " + filePath);
    	File file = new File(filePath);
    	if(file == null)
    		return;
    	
        if (file.isFile()) {  
            file.delete();  
            return;  
        }  
  
        if(file.isDirectory()){  
            File[] childFiles = file.listFiles();  
            for (int i = 0; i < childFiles.length; i++) {  
            	deleteFile(childFiles[i].getPath()); 
            }   
        }  
    } 
    
    /**
	 * 获取手机唯一编号
	 * 返回值为String 记录手机唯一编号
	 * 如：012682006095510
	 */
	public static String getDeviceId(Context context){
		String device_id = null;
		
		ReleasyApplication app = (ReleasyApplication) context.getApplicationContext();
		device_id = app.getDeviceId();
		
		if(StringUtils.isBlank(device_id)){
//			TelephonyManager tm = (TelephonyManager) context
//			                       .getSystemService(Context.TELEPHONY_SERVICE);//
//			device_id = tm.getDeviceId();
			app.setDeviceId("012682006095510");
		}
	
		return device_id;
	}
	
	/**
	 * 获取设备版本信息，用于适配和统计。
	 * 返回格式为 系统名称_系统版本_分辨率。
	 * 如：android_4.0.4_480*800
	 */
	public static String getDeviceV(Context context,int width, int height){
		String device_v = null;
		
		ReleasyApplication app = (ReleasyApplication) context.getApplicationContext();
		device_v = app.getDeviceV();
		
		if(StringUtils.isBlank(device_v)){
	        String version = android.os.Build.VERSION.RELEASE;
	        device_v = "Android_" + version +"_" + width + "*" + height;
	        app.setDeviceV(device_v);
		}

        return device_v;
	}
	
	/**
	 * 字符转整型数组
	 * @param data
	 * @return
	 */
	public static int[] arrayString2Int(String dataStr){
		String[]arr = dataStr.split(",");

		int[] data = new int[arr.length];
		for(int i = 0; i < arr.length; i++){
			data[i] = Integer.parseInt(arr[i]);
		}
		
		/*String test = "";
		for(int i = 0; i < data.length ; i++){
			test = test +  data[i] + "  ";
		}
		
		Log.d("z17m",test);*/
		
		return data;
	}
	
	/**
	 * 整型数组转字符
	 * @param data
	 * @return
	 */
	public static String arrayInt2String(int[] data){
		String dataStr = "";
		for(int i = 0; i < data.length-1; i++){
			dataStr = dataStr + data[i] + ",";
		}
		
		dataStr = dataStr + data[data.length-1];
		
		return dataStr;
	}
	
	/**
	 * 字符转整型数组
	 * @param data
	 * @return
	 */
	public static int[][] arrayStringTo2dimensionalArray(String dataStr){
		String[]arr = dataStr.split(";");

		int[][] data = new int[arr.length][];
		for(int i = 0; i < arr.length; i++){
			String[] arr2 = arr[i].split(",");
			data[i] = new int[arr2.length];
			
			for(int j = 0; j < arr2.length; j++){
				data[i][j] = Integer.parseInt(arr2[j]);
			}
		}
		
		String test = "";
		for(int i = 0; i < data.length ; i++){
			for(int j = 0; j < data[i].length; j++)
			{				
				test = test +  data[i][j] + ",";
			}
			test = test + ";";
		}
		Log.d("z17m",test);
		
		return data;
	}
	
	/**
	 * 整型数组转字符
	 * @param data
	 * @return
	 */
	public static String twodimensionalArrayToArrayString(int[][] data){
		String dataStr = "";
		for(int i = 0; i < data.length; i++){
			for(int k = 0; k < data[i].length-1; k++){
				dataStr = dataStr + data[i][k] + ",";
			}
			dataStr = dataStr + data[i][data[i].length-1] + ";";
		}
		
		//Log.d("z17m"," dataStr : " + dataStr);
		
		return dataStr;
	}
	
	
	public static boolean isBackground(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
	    for (RunningAppProcessInfo appProcess : appProcesses) {
	    	if (appProcess.processName.equals(context.getPackageName())) {
	    		if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
	    			Log.i("后台", appProcess.processName);
	                return true;
	            }else{
	            	Log.i("前台", appProcess.processName);
	            	return false;
	            }
	       }
	    }
	    return false;
	}
	
	/** Debug输出Log日志 **/
	public static void showLogD(String msg) {
		Log.d("z17m", msg);
	}
	
	public static boolean checkNameForM1(String name){
    	if(name.equals("MOOYEE") || name.equals("MooYee")){
    		return true;
    	}
    	else
    		return false;
    }
	
	public static boolean checkNameForM2(String name){
    	if(name.equals("MOOYEE-2A") || name.equals("MOOYEE-2B")
    			|| name.equals(Constants.MEDISANA_DEVICE_BROADCAST_NAME_M2_A)
    			|| name.equals(Constants.MEDISANA_DEVICE_BROADCAST_NAME_M2_B)
				|| name.equals(Constants.RELAXER_DEVICE_BROADCAST_NAME_M2_A)
    			|| name.equals(Constants.RELAXER_DEVICE_BROADCAST_NAME_M2_B)){
    		return true;
    	}
    	else
    		return false;
    }
}
