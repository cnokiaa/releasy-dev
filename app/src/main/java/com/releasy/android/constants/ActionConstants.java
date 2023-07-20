package com.releasy.android.constants;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.releasy.android.R;
import com.releasy.android.bean.ActionBean;

public class ActionConstants {

	public static final int ACTION_BASE_TYPE = 0;
	public static final int ACTION_SCENE_TYPE = 1;
	public static final int ACTION_GROUP_TYPE = 2;
	
	//测试按摩动作
	public static ActionBean getTestAction(){
		ActionBean bean = new ActionBean();
		
		bean.setBytesCheck((short)2);
		int[] highTime = {(short)2000}; bean.setHighTime(highTime);
		int[] lowTime = {(short)1000}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {(short)1,(short)3}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {(short)100}; bean.setInterval(interval);
		int[] Period = {(short)3000}; bean.setPeriod(Period);
		
		int[] rate = {(short)35}; 
		int[][] powerLV = {{15}};
		
		bean.setRateMin(rate);
		bean.setRateMax(rate);
		bean.setPowerLV(powerLV);
		bean.setStopTime(5);
		
		return bean;
	}
	
	/**************************************经典*****************************************/
	//揉按摩动作
	public static ActionBean getKneadAction(Context context){
		ActionBean bean = new ActionBean(10001,1001,context.getString(R.string.knead),0);
		
		bean.setBytesCheck(2);
		int[] highTime = {4000}; bean.setHighTime(highTime);
		int[] lowTime = {3500}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {1,5}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {500}; bean.setInterval(interval);
		int[] Period = {2501}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {90}; 
		int[][] powerLV = {{10},{15},{21},{27},{32},{38},{44},{49},{55},{61},{67},{72},{78},{84},{90}};
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//按按摩动作
	public static ActionBean getPressAction(Context context){
		ActionBean bean = new ActionBean(10002,1001,context.getString(R.string.press),0);
		
		bean.setBytesCheck(2);
		int[] highTime = {180}; bean.setHighTime(highTime);
		int[] lowTime = {500}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {15,15}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {1000}; bean.setInterval(interval);
		int[] Period = {2001}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {90}; 
		int[][] powerLV = {{10},{15},{21},{27},{32},{38},{44},{49},{55},{61},{67},{72},{78},{84},{90}};
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//挤按摩动作
	public static ActionBean getSqueezeAction(Context context){
		ActionBean bean = new ActionBean(10003,1001,context.getString(R.string.squeeze),0);
		
		bean.setBytesCheck(2);
		int[] highTime = {5}; bean.setHighTime(highTime);
		int[] lowTime = {1}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {3,1}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {200}; bean.setInterval(interval);
		int[] Period = {1001}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {90};
		int[][] powerLV = {{10},{15},{21},{27},{32},{38},{44},{49},{55},{61},{67},{72},{78},{84},{90}};
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//抚按摩动作
	public static ActionBean getFondleAction(Context context){
		ActionBean bean = new ActionBean(10004,1001,context.getString(R.string.stroke),0);
		
		bean.setBytesCheck(2);
		int[] highTime = {1000}; bean.setHighTime(highTime);
		int[] lowTime = {1000}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {0,5}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {500}; bean.setInterval(interval);
		int[] Period = {2501}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {70};
		int[][] powerLV = {{10},{14},{18},{22},{27},{31},{35},{40},{44},{48},{52},{55},{61},{65},{70}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//肘按摩动作
	public static ActionBean getElbowAction(Context context){
		ActionBean bean = new ActionBean(10005,1001,context.getString(R.string.elbow),0);
		
		bean.setBytesCheck(2);
		int[] highTime = {2000}; bean.setHighTime(highTime);
		int[] lowTime = {600}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {5,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {1500}; bean.setInterval(interval);
		int[] Period = {5001}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {90};
		int[][] powerLV = {{10},{15},{21},{27},{32},{38},{44},{49},{55},{61},{67},{72},{78},{84},{90}};
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//捶按摩动作
	public static ActionBean getHammerAction(Context context){
		ActionBean bean = new ActionBean(10006,1001,context.getString(R.string.beat),0);
		
		bean.setBytesCheck(2);
		int[] highTime = {20}; bean.setHighTime(highTime);
		int[] lowTime = {160}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {5,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {1500}; bean.setInterval(interval);
		int[] Period = {5001}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {90};
		int[][] powerLV = {{10},{15},{21},{27},{32},{38},{44},{49},{55},{61},{67},{72},{78},{84},{90}};
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//推拿按摩动作
	public static ActionBean getMassageAction(Context context){
		ActionBean bean = new ActionBean(10007,1002,context.getString(R.string.massage),0);
		
		bean.setBytesCheck(12);
		int[] highTime = {2,2,2,2,2,2}; bean.setHighTime(highTime);
		int[] lowTime = {5,5,20,20,5,5}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {2,0,2,0,2,0,2,0,2,0,2,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {2000,2000,2000,2000,2000,2000}; bean.setInterval(interval);
		int[] Period = {1501,1501,3001,3001,1501,1501}; bean.setPeriod(Period);
		
		int[] rateMin = {10,10,10,10,10,10}; 
		int[] rateMax = {90,90,90,90,90,90}; 
		int[][] powerLV = {{10,10,10,10,10,10},{15,15,15,15,15,15}
		                  ,{21,21,21,21,21,21},{27,27,27,27,27,27}
		                  ,{32,32,32,32,32,32},{38,38,38,38,38,38}
		                  ,{44,44,44,44,44,44},{49,49,49,49,49,49}
		                  ,{55,55,55,55,55,55},{61,61,61,61,61,61}
		                  ,{67,67,67,67,67,67},{72,72,72,72,72,72}
		                  ,{78,78,78,78,78,78},{84,84,84,84,84,84}
		                  ,{90,90,90,90,90,90}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//针灸按摩动作
	public static ActionBean getAcupunctureAction(Context context){
		ActionBean bean = new ActionBean(10008,1002,context.getString(R.string.acupuncture),0);
		
		bean.setBytesCheck(4);
		int[] highTime = {750,250}; bean.setHighTime(highTime);
		int[] lowTime = {250,250}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {250,0,25,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {1000,1000}; bean.setInterval(interval);
		int[] Period = {20001,20001}; bean.setPeriod(Period);

		int[] rateMin = {10,10}; 
		int[] rateMax = {70,70};
		int[][] powerLV = {{10,10},{14,14},{18,18},{22,22}
		                  ,{27,27},{31,31},{35,35},{40,40}
		                  ,{44,44},{48,48},{52,52},{55,55}
		                  ,{61,61},{65,65},{70,70}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//火罐按摩动作
	public static ActionBean getCuppingJarAction(Context context){
		ActionBean bean = new ActionBean(10009,1002,context.getString(R.string.cupping_jar),0);
		
		bean.setBytesCheck(2);
		int[] highTime = {10}; bean.setHighTime(highTime);
		int[] lowTime = {1}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {10,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {1000}; bean.setInterval(interval);
		int[] Period = {10001}; bean.setPeriod(Period);

		int[] rateMin = {10}; 
		int[] rateMax = {90};
		int[][] powerLV = {{10},{15},{21},{27},{32},{38},{44},{49},{55},{61},{67},{72},{78},{84},{90}};
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//刮痧摩动作
	public static ActionBean getScrapingAction(Context context){
		ActionBean bean = new ActionBean(10010,1002,context.getString(R.string.scraping),0);
		
		bean.setBytesCheck(4);
		int[] highTime = {200,100}; bean.setHighTime(highTime);
		int[] lowTime = {1500,500}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {8,0,5,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {1000,1000}; bean.setInterval(interval);
		int[] Period = {10001,10001}; bean.setPeriod(Period);
		
		int[] rateMin = {10,10}; 
		int[] rateMax = {90,90};
		int[][] powerLV = {{10,10},{15,15},{21,21},{27,27}
		                  ,{32,32},{38,38},{44,44},{49,49}
		                  ,{55,55},{61,61},{67,67},{72,72}
		                  ,{78,78},{84,84},{90,90}};
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}

	
	/**************************************场景*****************************************/
	
	//禅按摩动作
	public static ActionBean getDhyanaAction(Context context){
		ActionBean bean = new ActionBean(10011,1003,context.getString(R.string.zen),1);
		
		bean.setBytesCheck(12);
		int[] highTime = {8,6,8,8,50,50}; bean.setHighTime(highTime);
		int[] lowTime = {500,900,600,1000,200,1000}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {3,2,3,1,3,2,3,1,3,6,3,4}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {50,50,50,50,50,50}; bean.setInterval(interval);
		int[] Period = {8001,10001,12001,12001,10001,15001}; bean.setPeriod(Period);
		
		int[] rateMin = {10,10,10,10,10,10}; 
		int[] rateMax = {90,90,90,90,90,90}; 
		int[][] powerLV = {{10,10,10,10,10,10},{15,15,15,15,15,15}
		                  ,{21,21,21,21,21,21},{27,27,27,27,27,27}
		                  ,{32,32,32,32,32,32},{38,38,38,38,38,38}
		                  ,{44,44,44,44,44,44},{49,49,49,49,49,49}
		                  ,{55,55,55,55,55,55},{61,61,61,61,61,61}
		                  ,{67,67,67,67,67,67},{72,72,72,72,72,72}
		                  ,{78,78,78,78,78,78},{84,84,84,84,84,84}
		                  ,{90,90,90,90,90,90}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//宅家按摩动作
	public static ActionBean getHomeAction(Context context){
		ActionBean bean = new ActionBean(10012,1004,context.getString(R.string.home),1);
		
		bean.setBytesCheck(12);
		
		int[] highTime = {4,420,4,280,2,140}; 
		bean.setHighTime(highTime);
		
		int[] lowTime = {96,238,20,238,9,238};
		bean.setLowTime(lowTime);
		
		int[] innerHighAndLow = {1,3,22,118,2,0,22,118,1,0,22,118};
		bean.setInnerHighAndLow(innerHighAndLow);
		
		int[] interval = {1000,1000,1000,1000,1000,1000};
		bean.setInterval(interval);
		
		int[] Period = {40001,30001,10001,30001,5001,30001}; 
		bean.setPeriod(Period);
		
		int[] rateMin = {30,20,30,20,40,10}; 
		int[] rateMax = {90,90,90,80,90,50}; 
		int[][] powerLV = {{30,20,30,20,40,10},{34,25,34,24,43,12}
                          ,{38,30,38,28,47,15},{42,35,42,32,50,18}
                          ,{47,40,47,37,54,21},{51,45,51,41,57,24}
                          ,{55,50,55,45,61,27},{60,55,60,50,64,30}
                          ,{64,60,64,54,68,32},{68,65,68,58,72,35}
                          ,{72,70,72,62,75,38},{77,75,77,67,79,41}
                          ,{81,80,81,71,82,44},{85,85,85,75,86,47}
                          ,{90,90,90,80,90,50}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//办公室按摩动作
	public static ActionBean getWrokAction(Context context){
		ActionBean bean = new ActionBean(10013,1005,context.getString(R.string.office),1);
		
		bean.setBytesCheck(16);
		
		int[] highTime = {4120,8196,4120,4367,4100,4120,4098,4098}; 
		bean.setHighTime(highTime);
		
		int[] lowTime = {800,91,700,271,24,600,14,14};
		bean.setLowTime(lowTime);
		
		int[] innerHighAndLow = {3,5,1,3,3,5,22,118,1,3,3,5,2,0,2,0};
		bean.setInnerHighAndLow(innerHighAndLow);
		
		int[] interval = {1000,1000,1000,500,1000,500,1000,500};
		bean.setInterval(interval);
		
		int[] Period = {10001,10001,5001,7501,2501,7501,2501,2501}; 
		bean.setPeriod(Period);
		
		int[] rateMin = {25,25,25,20,20,20,20,20}; 
		int[] rateMax = {80,70,70,65,65,65,65,65}; 
		int[][] powerLV = {{25,25,25,20,20,20,20,20},{28,28,28,23,23,23,23,23}
                          ,{32,31,31,26,26,26,26,26},{36,34,34,29,29,29,29,29}
                          ,{40,37,37,32,32,32,32,32},{44,45,45,36,36,36,36,36}
                          ,{48,44,44,39,39,39,39,39},{52,47,47,42,42,42,42,42}
                          ,{56,50,50,45,45,45,45,45},{60,53,53,48,48,48,48,48}
                          ,{64,57,57,52,52,52,52,52},{68,60,60,55,55,55,55,55}
                          ,{72,63,63,58,58,58,58,58},{76,66,66,61,61,61,61,61}
                          ,{80,70,70,65,65,65,65,65}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//听潮按摩动作
	public static ActionBean getTideAction(Context context){
		ActionBean bean = new ActionBean(10014,1006,context.getString(R.string.seaside),1);
		
		bean.setBytesCheck(60);
		
		int[] highTime = {4156,4156,4156,4156,4098
		          ,4098,4142,4142,4142,4142
		          ,4126,4126,4126,4126,4116
		          ,4116,4116,4116,4106,4106
		          ,4106,4106,4098,4098,4098
		          ,4098,4097,4097,4097,4097}; 
		bean.setHighTime(highTime);
		
		int[] lowTime = {600,600,600,600,14
		          ,14,800,800,800,800
		          ,900,900,900,900,1000
		          ,1000,1000,1000,1100,1100
		          ,1100,1100,1200,1200,1200
		          ,1200,10000,10000,10000,10000};
		bean.setLowTime(lowTime);
		
		int[] innerHighAndLow = {10,0,10,0,10,0,10,0,2,0
		          ,2,0,6,4,6,4,6,4,6,4
		          ,3,5,3,5,3,5,3,5,2,6
		          ,2,6,2,6,2,6,1,7,1,7
		          ,1,7,1,7,1,7,1,7,1,7
		          ,1,7,0,0,0,0,0,0,0,0};
		bean.setInnerHighAndLow(innerHighAndLow);
		
		int[] interval = {240,240,240,5000,10
		           ,5000,586,586,586,5000
		           ,710,710,710,5000,360
		           ,360,360,5000,300,300
		           ,300,5000,60,60,60
		           ,5000,10,10,10,5000};
		bean.setInterval(interval);
		
		int[] Period = {30001,30001,30001,30001,30001
	             ,30001,30001,30001,30001,30001
	             ,30001,30001,30001,30001,30001
	             ,30001,30001,30001,30001,30001
	             ,30001,30001,30001,30001,30001
	             ,30001,30001,30001,30001,30001}; 
		bean.setPeriod(Period);
		
		int[] rate = {25,25,25,25,30,30,30,30,30,30,30,30,30,30,25,25,25,25,20,20,20,20,13,13,13,13,1,1,1,1}; 
		int[][] powerLV = {{25,25,25,25,30,30,30,30,30,30,30,30,30,30,25,25,25,25,20,20,20,20,13,13,13,13,1,1,1,1}
		                  ,{25,25,25,25,30,30,30,30,30,30,30,30,30,30,25,25,25,25,20,20,20,20,13,13,13,13,1,1,1,1}
		                  ,{25,25,25,25,30,30,30,30,30,30,30,30,30,30,25,25,25,25,20,20,20,20,13,13,13,13,1,1,1,1}
		                  ,{25,25,25,25,30,30,30,30,30,30,30,30,30,30,25,25,25,25,20,20,20,20,13,13,13,13,1,1,1,1}
		                  ,{25,25,25,25,30,30,30,30,30,30,30,30,30,30,25,25,25,25,20,20,20,20,13,13,13,13,1,1,1,1}
		                  ,{25,25,25,25,30,30,30,30,30,30,30,30,30,30,25,25,25,25,20,20,20,20,13,13,13,13,1,1,1,1}
		                  ,{25,25,25,25,30,30,30,30,30,30,30,30,30,30,25,25,25,25,20,20,20,20,13,13,13,13,1,1,1,1}
		                  ,{25,25,25,25,30,30,30,30,30,30,30,30,30,30,25,25,25,25,20,20,20,20,13,13,13,13,1,1,1,1}
		                  ,{25,25,25,25,30,30,30,30,30,30,30,30,30,30,25,25,25,25,20,20,20,20,13,13,13,13,1,1,1,1}
		                  ,{25,25,25,25,30,30,30,30,30,30,30,30,30,30,25,25,25,25,20,20,20,20,13,13,13,13,1,1,1,1}};
		
		bean.setRateMin(rate);
		bean.setRateMax(rate);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//空谷按摩动作
	public static ActionBean getValleyAction(Context context){
		ActionBean bean = new ActionBean(10015,1007,context.getString(R.string.valley),1);
		
		bean.setBytesCheck(6);
		int[] highTime = {4,2000,12}; bean.setHighTime(highTime);
		int[] lowTime = {16,1500,30}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {4,0,1,5,6,6}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {1000,1000,1000}; bean.setInterval(interval);
		int[] Period = {9001,10501,9601}; bean.setPeriod(Period);
		
		int[] rateMin = {30,30,30}; 
		int[] rateMax = {90,90,90}; 
		int[][] powerLV = {{30,30,30},{34,34,34}
                          ,{38,38,38},{42,42,42}
                          ,{47,47,47},{51,51,51}
                          ,{55,55,55},{60,60,60}
                          ,{64,64,64},{68,68,68}
                          ,{72,72,72},{77,77,77}
                          ,{81,81,81},{85,85,85}
                          ,{90,90,90}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}

	//在路上按摩动作
	public static ActionBean getOnTheWayAction(Context context){
		ActionBean bean = new ActionBean(10016,1008,context.getString(R.string.on_the_way),1);
		
		bean.setBytesCheck(16);
		
		int[] highTime = {4,4,2,4,4,12,36,2};
		bean.setHighTime(highTime);
		
		int[] lowTime = {91,24,14,91,24,340,36,14};
		bean.setLowTime(lowTime);
		
		int[] innerHighAndLow = {1,3,1,3,2,0,1,3,1,3,1,3,6,9,2,0}; 
		bean.setInnerHighAndLow(innerHighAndLow);
		
		int[] interval = {10,1000,1000,10,1000,10,10,1000};
		bean.setInterval(interval);
		
		int[] Period = {14001,15001,24001,14001,15001,14001,14001,24001};
		bean.setPeriod(Period);
		
		int[] rateMin = {30,30,30,30,30,30,30,30}; 
		int[] rateMax = {90,90,90,90,90,90,90,90}; 
		int[][] powerLV = {{30,30,30,30,30,30,30,30},{34,34,34,34,34,34,34,34}
                          ,{38,38,38,38,38,38,38,38},{42,42,42,42,42,42,42,42}
                          ,{47,47,47,47,47,47,47,47},{51,51,51,51,51,51,51,51}
                          ,{55,55,55,55,55,55,55,55},{60,60,60,60,60,60,60,60}
                          ,{64,64,64,64,64,64,64,64},{68,68,68,68,68,68,68,68}
                          ,{72,72,72,72,72,72,72,72},{77,77,77,77,77,77,77,77}
                          ,{81,81,81,81,81,81,81,81},{85,85,85,85,85,85,85,85}
                          ,{90,90,90,90,90,90,90,90}}; 
 
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//小憩按摩动作
	public static ActionBean getRespiteAction(Context context){
		ActionBean bean = new ActionBean(10017,1009,context.getString(R.string.taking_a_nap),1);
		
		bean.setBytesCheck(8);
		int[] highTime = {4,20,180,1000}; bean.setHighTime(highTime);
		int[] lowTime = {100,160,180,1000}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {1,3,5,0,15,15,0,5}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {500,500,500,10}; bean.setInterval(interval);
		int[] Period = {10001,7501,7501,6001}; bean.setPeriod(Period);
		
		int[] rateMin = {35,20,15,45}; 
		int[] rateMax = {75,40,40,80}; 
		int[][] powerLV = {{35,20,15,45},{37,21,16,47}
                          ,{40,22,18,50},{43,24,20,52}
                          ,{46,25,22,55},{49,27,23,57}
                          ,{52,28,25,60},{55,30,27,62}
                          ,{57,31,29,65},{60,32,31,67}
                          ,{63,34,32,70},{66,35,34,72}
                          ,{69,37,36,75},{72,38,38,77}
                          ,{75,40,40,80}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//旅行按摩动作
	public static ActionBean getTravelAction(Context context){
		ActionBean bean = new ActionBean(10018,1010,context.getString(R.string.travel),1);
		
		bean.setBytesCheck(14);
		int[] highTime = {20,4,186,260,360,80,50}; bean.setHighTime(highTime);
		int[] lowTime = {2,80,96,238,271,420,950}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {1,1,1,3,18,80,20,110,20,100,4,4,3,2}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {100,100,100,100,100,20,30}; bean.setInterval(interval);
		int[] Period = {5001,20001,15001,15001,10001,10001,10001}; bean.setPeriod(Period);
		
		int[] rateMin = {30,30,20,20,20,20,30}; 
		int[] rateMax = {90,90,90,90,90,90,90}; 
		int[][] powerLV = {{30,30,20,20,20,20,30},{34,34,25,25,25,25,34}
                          ,{38,38,30,30,30,30,38},{42,42,35,35,35,35,42}
                          ,{47,47,40,40,40,40,47},{51,51,45,45,45,45,51}
                          ,{55,55,50,50,50,50,55},{60,60,55,55,55,55,60}
                          ,{64,64,60,60,60,60,64},{68,68,65,65,65,65,68}
                          ,{72,72,70,70,70,70,72},{77,77,75,75,75,75,77}
                          ,{81,81,80,80,80,80,81},{85,85,85,85,85,85,85}
                          ,{90,90,90,90,90,90,90}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	/*************************************表情识别***********************************************/
	//平静  —— 按
	public static ActionBean getCalmAction(Context context){
		ActionBean bean = new ActionBean(10019,-3,context.getString(R.string.calm),0);
		
		bean.setBytesCheck(2);
		int[] highTime = {180}; bean.setHighTime(highTime);
		int[] lowTime = {500}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {15,15}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {1000}; bean.setInterval(interval);
		int[] Period = {2001}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {90}; 
		int[][] powerLV = {{10},{15},{21},{27},{32},{38},{44},{49},{55},{61},{67},{72},{78},{84},{90}};
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//害怕  —— 揉
	public static ActionBean getFearAction(Context context){
		ActionBean bean = new ActionBean(10020,-3,context.getString(R.string.fear),0);
		
		bean.setBytesCheck(2);
		int[] highTime = {4000}; bean.setHighTime(highTime);
		int[] lowTime = {3500}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {1,5}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {500}; bean.setInterval(interval);
		int[] Period = {2501}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {90}; 
		int[][] powerLV = {{10},{15},{21},{27},{32},{38},{44},{49},{55},{61},{67},{72},{78},{84},{90}};
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//生气  —— 抚
	public static ActionBean getGetAngryAction(Context context){
		ActionBean bean = new ActionBean(10021,-3,context.getString(R.string.get_angry),0);
		
		bean.setBytesCheck(2);
		int[] highTime = {1000}; bean.setHighTime(highTime);
		int[] lowTime = {1000}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {0,5}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {500}; bean.setInterval(interval);
		int[] Period = {2501}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {70};
		int[][] powerLV = {{10},{14},{18},{22},{27},{31},{35},{40},{44},{48},{52},{55},{61},{65},{70}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//开心  —— 挤
	public static ActionBean getHappyAction(Context context){
		ActionBean bean = new ActionBean(10022,-3,context.getString(R.string.happy),0);
		
		bean.setBytesCheck(2);
		int[] highTime = {5}; bean.setHighTime(highTime);
		int[] lowTime = {1}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {3,1}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {200}; bean.setInterval(interval);
		int[] Period = {1001}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {90};
		int[][] powerLV = {{10},{15},{21},{27},{32},{38},{44},{49},{55},{61},{67},{72},{78},{84},{90}};
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//忧愁  —— 肘
	public static ActionBean getSadAction(Context context){
		ActionBean bean = new ActionBean(10023,-3,context.getString(R.string.sad),0);
		
		bean.setBytesCheck(2);
		int[] highTime = {2000}; bean.setHighTime(highTime);
		int[] lowTime = {600}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {5,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {1500}; bean.setInterval(interval);
		int[] Period = {5001}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {90};
		int[][] powerLV = {{10},{15},{21},{27},{32},{38},{44},{49},{55},{61},{67},{72},{78},{84},{90}};
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//伤感  —— 捶
	public static ActionBean getSentimentalAction(Context context){
		ActionBean bean = new ActionBean(10024,-3,context.getString(R.string.sentimental),0);
		
		bean.setBytesCheck(2);
		int[] highTime = {20}; bean.setHighTime(highTime);
		int[] lowTime = {160}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {5,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {1500}; bean.setInterval(interval);
		int[] Period = {5001}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {90};
		int[][] powerLV = {{10},{15},{21},{27},{32},{38},{44},{49},{55},{61},{67},{72},{78},{84},{90}};
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	
	//初始按摩动作数据 
	public static List<ActionBean> getInitialActionList(Context context){
		List<ActionBean> actionList = new ArrayList<ActionBean>();
		actionList.add(getKneadAction(context));
		actionList.add(getPressAction(context));
		actionList.add(getSqueezeAction(context));
		actionList.add(getFondleAction(context));
		actionList.add(getElbowAction(context));
		actionList.add(getHammerAction(context));
		
		actionList.add(getMassageAction(context));
		actionList.add(getScrapingAction(context));
		actionList.add(getCuppingJarAction(context));
		actionList.add(getAcupunctureAction(context));
		
		actionList.add(getHomeAction(context));
		actionList.add(getWrokAction(context));
		actionList.add(getTideAction(context));
		actionList.add(getValleyAction(context));
		actionList.add(getDhyanaAction(context));
		actionList.add(getOnTheWayAction(context));
		actionList.add(getRespiteAction(context));
		actionList.add(getTravelAction(context));
		
		actionList.add(getCalmAction(context));
		actionList.add(getFearAction(context));
		actionList.add(getGetAngryAction(context));
		actionList.add(getHappyAction(context));
		actionList.add(getSadAction(context));
		actionList.add(getSentimentalAction(context));
		return actionList;
	}
	
	public static Drawable getActionDrawable(Context context,int actionId){
		Drawable img = null;
		
		switch(actionId){
		case 10001:
			img = context.getResources().getDrawable(R.drawable.ic_action_knead);
			break;
		case 10002:
			img = context.getResources().getDrawable(R.drawable.ic_action_press);
			break;
		case 10003:
			img = context.getResources().getDrawable(R.drawable.ic_action_squeeze);
			break;
		case 10004:
			img = context.getResources().getDrawable(R.drawable.ic_action_fondle);
			break;
		case 10005:
			img = context.getResources().getDrawable(R.drawable.ic_action_elbow);
			break;
		case 10006:
			img = context.getResources().getDrawable(R.drawable.ic_action_hammer);
			break;
		case 10007:
			img = context.getResources().getDrawable(R.drawable.ic_action_massage);
			break;
		case 10008:
			img = context.getResources().getDrawable(R.drawable.ic_action_acupuncture);
			break;
		case 10009:
			img = context.getResources().getDrawable(R.drawable.ic_action_cupping_jar);
			break;
		case 10010:
			img = context.getResources().getDrawable(R.drawable.ic_action_scraping);
			break;
		}
		//调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
		return img;
	}
	
	public static Drawable getActionDrawable(Context context,int actionId, boolean isChoose){
		Drawable img = null;
		
		switch(actionId){
		case 10001:
			if(isChoose)
				img = context.getResources().getDrawable(R.drawable.ic_action_knead_pressed);
			else
				img = context.getResources().getDrawable(R.drawable.ic_action_knead_normal);
			break;
		case 10002:
			if(isChoose)
				img = context.getResources().getDrawable(R.drawable.ic_action_press_pressed);
			else
				img = context.getResources().getDrawable(R.drawable.ic_action_press_normal);
			break;
		case 10003:
			if(isChoose)
				img = context.getResources().getDrawable(R.drawable.ic_action_squeeze_pressed);
			else
				img = context.getResources().getDrawable(R.drawable.ic_action_squeeze_normal);
			break;
		case 10004:
			if(isChoose)
				img = context.getResources().getDrawable(R.drawable.ic_action_fondle_pressed);
			else
				img = context.getResources().getDrawable(R.drawable.ic_action_fondle_normal);
			break;
		case 10005:
			if(isChoose)
				img = context.getResources().getDrawable(R.drawable.ic_action_elbow_pressed);
			else
				img = context.getResources().getDrawable(R.drawable.ic_action_elbow_normal);
			break;
		case 10006:
			if(isChoose)
				img = context.getResources().getDrawable(R.drawable.ic_action_hammer_pressed);
			else
				img = context.getResources().getDrawable(R.drawable.ic_action_hammer_normal);
			break;
		case 10007:
			if(isChoose)
				img = context.getResources().getDrawable(R.drawable.ic_action_massage_pressed);
			else
				img = context.getResources().getDrawable(R.drawable.ic_action_massage_normal);
			break;
		case 10008:
			if(isChoose)
				img = context.getResources().getDrawable(R.drawable.ic_action_acupuncture_pressed);
			else
				img = context.getResources().getDrawable(R.drawable.ic_action_acupuncture_normal);
			break;
		case 10009:
			if(isChoose)
				img = context.getResources().getDrawable(R.drawable.ic_action_cupping_jar_pressed);
			else
				img = context.getResources().getDrawable(R.drawable.ic_action_cupping_jar_normal);
			break;
		case 10010:
			if(isChoose)
				img = context.getResources().getDrawable(R.drawable.ic_action_scraping_pressed);
			else
				img = context.getResources().getDrawable(R.drawable.ic_action_scraping_normal);
			break;
		}
		//调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
		return img;
	}
	
	public static Drawable getActionBG(Context context,int actionId){
		Drawable img = null;
		
		switch(actionId){
		case 10001:
				img = context.getResources().getDrawable(R.drawable.ic_actionic_rou);
			break;
		case 10002:
				img = context.getResources().getDrawable(R.drawable.ic_actionic_an);
			break;
		case 10003:
				img = context.getResources().getDrawable(R.drawable.ic_actionic_ji);
			break;
		case 10004:
				img = context.getResources().getDrawable(R.drawable.ic_actionic_fu);
			break;
		case 10005:
				img = context.getResources().getDrawable(R.drawable.ic_actionic_zhou);
			break;
		case 10006:
				img = context.getResources().getDrawable(R.drawable.ic_actionic_chui);
			break;
		case 10007:
				img = context.getResources().getDrawable(R.drawable.ic_actionic_tuina);
			break;
		case 10008:
				img = context.getResources().getDrawable(R.drawable.ic_actionic_zhenjiu);
			break;
		case 10009:
				img = context.getResources().getDrawable(R.drawable.ic_actionic_huoguan);
			break;
		case 10010:
				img = context.getResources().getDrawable(R.drawable.ic_actionic_guasha);
			break;
		}
		//调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
		return img;
	}
	
	/**
	 * 获取官方放松馆名称
	 * @param roomId
	 * @return
	 */
	public static String getActionName(int actionId,Context context){
		String actionName = null;
		switch(actionId){
		case 10001:
			actionName = context.getString(R.string.knead);
			break;
		case 10002:
			actionName = context.getString(R.string.press);
			break;
		case 10003:
			actionName = context.getString(R.string.squeeze);
			break;
		case 10004:
			actionName = context.getString(R.string.stroke);
			break;
		case 10005:
			actionName = context.getString(R.string.elbow);
			break;
		case 10006:
			actionName = context.getString(R.string.beat);
			break;
		case 10007:
			actionName = context.getString(R.string.massage);
			break;
		case 10008:
			actionName = context.getString(R.string.acupuncture);
			break;
		case 10009:
			actionName = context.getString(R.string.cupping_jar);
			break;
		case 10010:
			actionName = context.getString(R.string.scraping);
			break;
		case 10011:
			actionName = context.getString(R.string.zen);
			break;
		case 10012:
			actionName = context.getString(R.string.home);
			break;
		case 10013:
			actionName = context.getString(R.string.office);
			break;
		case 10014:
			actionName = context.getString(R.string.seaside);
			break;
		case 10015:
			actionName = context.getString(R.string.valley);
			break;
		case 10016:
			actionName = context.getString(R.string.on_the_way);
			break;
		case 10017:
			actionName = context.getString(R.string.taking_a_nap);
			break;
		case 10018:
			actionName = context.getString(R.string.travel);
			break;
		case 10019:
			actionName = context.getString(R.string.calm);
			break;
		case 10020:
			actionName = context.getString(R.string.fear);
			break;
		case 10021:
			actionName = context.getString(R.string.get_angry);
			break;
		case 10022:
			actionName = context.getString(R.string.happy);
			break;
		case 10023:
			actionName = context.getString(R.string.sad);
			break;
		case 10024:
			actionName = context.getString(R.string.sentimental);
			break;
		default:
			break;
		}
		
		return actionName;
	}
	
	
	/**
	 * 获取官方放松馆名称
	 * @param roomId
	 * @return
	 */
	public static int getActionBg(int actionId){
		int actionbg = R.drawable.action_1;
		switch(actionId){
		case 10001:
			actionbg = R.drawable.action_1;
			break;
		case 10002:
			actionbg = R.drawable.action_2;
			break;
		case 10003:
			actionbg = R.drawable.action_3;
			break;
		case 10004:
			actionbg = R.drawable.action_4;
			break;
		case 10005:
			actionbg = R.drawable.action_5;
			break;
		case 10006:
			actionbg = R.drawable.action_6;
			break;
		case 10007:
			actionbg = R.drawable.action_7;
			break;
		case 10008:
			actionbg = R.drawable.action_8;
			break;
		case 10009:
			actionbg = R.drawable.action_9;
			break;
		case 10010:
			actionbg = R.drawable.action_10;
			break;
		case 10019:
			actionbg = R.drawable.calm;
			break;
		case 10020:
			actionbg = R.drawable.fear;
			break;
		case 10021:
			actionbg = R.drawable.get_angry;
			break;
		case 10022:
			actionbg = R.drawable.happy;
			break;
		case 10023:
			actionbg = R.drawable.sad;
			break;
		case 10024:
			actionbg = R.drawable.sentimental;
			break;
		default:
			break;
		}
		
		return actionbg;
	}
}
