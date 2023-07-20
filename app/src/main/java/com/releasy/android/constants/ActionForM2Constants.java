package com.releasy.android.constants;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.releasy.android.R;
import com.releasy.android.bean.ActionBean;

public class ActionForM2Constants {

	
	public static ActionBean getTestAction(){
		ActionBean bean = new ActionBean();
		
		int[] checkChangeMode = {(short)2,(short)1};bean.setCheckChangeMode(checkChangeMode);
		int[] highTime = {(short)2500}; bean.setHighTime(highTime);
		int[] lowTime = {(short)500}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {(short)6,(short)0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {(short)1000}; bean.setInterval(interval);
		int[] Period = {(short)3000}; bean.setPeriod(Period);
		
		int[] rate = {(short)30}; 
		int[][] powerLV = {{10}};
		
		bean.setRateMin(rate);
		bean.setRateMax(rate);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {(short)60,(short)1}; bean.setMinMaxList(minmax);
		int[] mode12 = {(short)1,(short)1}; bean.setMode12List(mode12);
		
		bean.setStopTime(5);
		
		return bean;
	}
	
	/**************************************经典*****************************************/
	//揉按摩动作
	public static ActionBean getKneadAction(Context context){
		ActionBean bean = new ActionBean(10001,1001,context.getString(R.string.knead),0);
		
		int[] checkChangeMode = {(short)2,(short)1};bean.setCheckChangeMode(checkChangeMode);
		int[] highTime = {2500}; bean.setHighTime(highTime);
		int[] lowTime = {500}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {6,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {1000}; bean.setInterval(interval);
		int[] Period = {3000}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {80}; 
		int[][] powerLV = {{10},{15},{20},{25},{30}
		                  ,{35},{40},{45},{50},{55}
		                  ,{60},{65},{70},{75},{80}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {(short)60,(short)1}; bean.setMinMaxList(minmax);
		int[] mode12 = {(short)1,(short)1}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//按按摩动作
	public static ActionBean getPressAction(Context context){
		ActionBean bean = new ActionBean(10002,1001,context.getString(R.string.press),0);
		
		int[] checkChangeMode = {(short)2,(short)5};bean.setCheckChangeMode(checkChangeMode);
		int[] highTime = {1000}; bean.setHighTime(highTime);
		int[] lowTime = {2000}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {4,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {2000}; bean.setInterval(interval);
		int[] Period = {20000}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {90}; 
		int[][] powerLV = {{10},{15},{21},{27},{32},{38},{44},{49},{55},{61},{67},{72},{78},{84},{90}};
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {(short)60,(short)1}; bean.setMinMaxList(minmax);
		int[] mode12 = {(short)1,(short)1}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//挤按摩动作
	public static ActionBean getSqueezeAction(Context context){
		ActionBean bean = new ActionBean(10003,1001,context.getString(R.string.squeeze),0);
		
		int[] checkChangeMode = {(short)2,(short)4};bean.setCheckChangeMode(checkChangeMode);
		int[] highTime = {800}; bean.setHighTime(highTime);
		int[] lowTime = {1200}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {5,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {1000}; bean.setInterval(interval);
		int[] Period = {2500}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {90}; 
		int[][] powerLV = {{10},{15},{21},{27},{32},{38},{44},{49},{55},{61},{67},{72},{78},{84},{90}};
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {(short)10,(short)80}; bean.setMinMaxList(minmax);
		int[] mode12 = {(short)20,(short)5}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//抚按摩动作
	public static ActionBean getFondleAction(Context context){
		ActionBean bean = new ActionBean(10004,1001,context.getString(R.string.stroke),0);
		
		int[] checkChangeMode = {(short)2,(short)1};bean.setCheckChangeMode(checkChangeMode);
		int[] highTime = {1000}; bean.setHighTime(highTime);
		int[] lowTime = {1000}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {5,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {1000}; bean.setInterval(interval);
		int[] Period = {5000}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {40}; 
		int[][] powerLV = {{10},{12},{14},{16},{18},{20},{22},{24},{26},{28},{30},{32},{34},{37},{40}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {(short)60,(short)1}; bean.setMinMaxList(minmax);
		int[] mode12 = {(short)1,(short)1}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//肘按摩动作
	public static ActionBean getElbowAction(Context context){
		ActionBean bean = new ActionBean(10005,1001,context.getString(R.string.elbow),0);
		
		int[] checkChangeMode = {(short)2,(short)1};bean.setCheckChangeMode(checkChangeMode);
		int[] highTime = {2000}; bean.setHighTime(highTime);
		int[] lowTime = {900}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {6,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {1500}; bean.setInterval(interval);
		int[] Period = {5000}; bean.setPeriod(Period);
		
		int[] rateMin = {15}; 
		int[] rateMax = {90}; 
		int[][] powerLV = {{15},{20},{25},{31},{36},{41},{47},{52},{57},{63},{68},{73},{79},{84},{90}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {(short)100,(short)1}; bean.setMinMaxList(minmax);
		int[] mode12 = {(short)1,(short)1}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//捶按摩动作
	public static ActionBean getHammerAction(Context context){
		ActionBean bean = new ActionBean(10006,1001,context.getString(R.string.beat),0);
		
		int[] checkChangeMode = {(short)2,(short)1};bean.setCheckChangeMode(checkChangeMode);
		int[] highTime = {50}; bean.setHighTime(highTime);
		int[] lowTime = {250}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {10,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {2000}; bean.setInterval(interval);
		int[] Period = {10000}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {90}; 
		int[][] powerLV = {{10},{15},{21},{27},{32},{38},{44},{49},{55},{61},{67},{72},{78},{84},{90}};
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {(short)250,(short)1}; bean.setMinMaxList(minmax);
		int[] mode12 = {(short)1,(short)1}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//推拿按摩动作
	public static ActionBean getMassageAction(Context context){
		ActionBean bean = new ActionBean(10007,1002,context.getString(R.string.massage),0);
		
		int[] checkChangeMode = {(short)4,(short)4,(short)1};bean.setCheckChangeMode(checkChangeMode);
		int[] highTime = {2000,100}; bean.setHighTime(highTime);
		int[] lowTime = {500,150}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {5,0,7,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {500,1000}; bean.setInterval(interval);
		int[] Period = {2500,3000}; bean.setPeriod(Period);
		
		int[] rateMin = {10,10}; 
		int[] rateMax = {90,90}; 
		int[][] powerLV = {{10,10},{15,15},{21,21},{27,27},{32,32}
		                  ,{38,38},{44,44},{49,49},{55,55},{61,61}
		                  ,{67,67},{72,72},{78,78},{84,84},{90,90}};
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {30,150,60,1}; bean.setMinMaxList(minmax);
		int[] mode12 = {1,3,1,1}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//针灸按摩动作
	public static ActionBean getAcupunctureAction(Context context){
		ActionBean bean = new ActionBean(10008,1002,context.getString(R.string.acupuncture),0);
		
		int[] checkChangeMode = {(short)4,(short)1,(short)3};bean.setCheckChangeMode(checkChangeMode);
		int[] highTime = {750,2500}; bean.setHighTime(highTime);
		int[] lowTime = {250,500}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {250,0,25,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {1000,1000}; bean.setInterval(interval);
		int[] Period = {20000,20000}; bean.setPeriod(Period);

		int[] rateMin = {5,5}; 
		int[] rateMax = {90,90}; 
		int[][] powerLV = {{5,5},{11,11},{17,17},{23,23},{29,29}
		                  ,{35,35},{41,41},{47,47},{53,53},{59,59}
		                  ,{65,65},{71,71},{77,77},{83,83},{90,90}};
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {250,1,50,100}; bean.setMinMaxList(minmax);
		int[] mode12 = {1,1,5,1}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//火罐按摩动作
	public static ActionBean getCuppingJarAction(Context context){
		ActionBean bean = new ActionBean(10009,1002,context.getString(R.string.cupping_jar),0);
		
		int[] checkChangeMode = {(short)2,(short)4};bean.setCheckChangeMode(checkChangeMode);
		int[] highTime = {3500}; bean.setHighTime(highTime);
		int[] lowTime = {1500}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {5,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {10}; bean.setInterval(interval);
		int[] Period = {5000}; bean.setPeriod(Period);

		int[] rateMin = {15}; 
		int[] rateMax = {90}; 
		int[][] powerLV = {{15},{20},{25},{31},{36},{41},{47},{52},{57},{63},{68},{73},{79},{84},{90}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {5,25}; bean.setMinMaxList(minmax);
		int[] mode12 = {10,1}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//刮痧摩动作
	public static ActionBean getScrapingAction(Context context){
		ActionBean bean = new ActionBean(10010,1002,context.getString(R.string.scraping),0);
		
		int[] checkChangeMode = {(short)2,(short)1};bean.setCheckChangeMode(checkChangeMode);
		int[] highTime = {150}; bean.setHighTime(highTime);
		int[] lowTime = {150}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {10,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {100}; bean.setInterval(interval);
		int[] Period = {300}; bean.setPeriod(Period);
		
		int[] rateMin = {20}; 
		int[] rateMax = {90}; 
		int[][] powerLV = {{20},{25},{30},{35},{40},{45},{50},{55},{60},{65},{70},{75},{80},{85},{90}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {150,1}; bean.setMinMaxList(minmax);
		int[] mode12 = {1,1}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}

	
	/**************************************场景*****************************************/
	
	//禅按摩动作
	public static ActionBean getDhyanaAction(Context context){
		ActionBean bean = new ActionBean(10011,1003,context.getString(R.string.zen),1);
		
		int[] checkChangeMode = {4,1,1};bean.setCheckChangeMode(checkChangeMode);
		int[] highTime = {1000,2500}; bean.setHighTime(highTime);
		int[] lowTime = {1000,500}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {5,0,6,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {1000,1000}; bean.setInterval(interval);
		int[] Period = {10000,10000}; bean.setPeriod(Period);
		
		int[] rateMin = {10,10}; 
		int[] rateMax = {40,50}; 
		int[][] powerLV = {{10,10},{12,12}
                          ,{14,15},{16,18}
                          ,{18,21},{20,24}
                          ,{22,27},{24,30}
                          ,{26,32},{28,35}
                          ,{30,38},{32,41}
                          ,{35,44},{38,47}
                          ,{40,50}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {60,1,60,1}; bean.setMinMaxList(minmax);
		int[] mode12 = {1,1,1,1}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//宅家按摩动作
	public static ActionBean getHomeAction(Context context){
		ActionBean bean = new ActionBean(10012,1004,context.getString(R.string.home),1);
		
		int[] checkChangeMode = {6,4,5,1};bean.setCheckChangeMode(checkChangeMode);
		
		int[] highTime = {800,1000,2500}; 
		bean.setHighTime(highTime);
		
		int[] lowTime = {1200,2000,500};
		bean.setLowTime(lowTime);
		
		int[] innerHighAndLow = {5,0,4,0,6,0};
		bean.setInnerHighAndLow(innerHighAndLow);
		
		int[] interval = {1000,2000,1000};
		bean.setInterval(interval);
		
		int[] Period = {4000,10000,6000}; 
		bean.setPeriod(Period);
		
		int[] rateMin = {10,10,10}; 
		int[] rateMax = {80,80,80}; 
		int[][] powerLV = {{10,10,10},{15,15,15},{20,20,20},{25,25,25},{30,30,30}
                          ,{35,35,35},{40,40,40},{45,45,45},{50,50,50},{55,55,55}
                          ,{60,60,60},{65,65,65},{70,70,70},{75,75,75},{80,80,80}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {10,80,10,100,60,1}; bean.setMinMaxList(minmax);
		int[] mode12 = {20,5,5,1,1,1}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//办公室按摩动作
	public static ActionBean getWrokAction(Context context){
		ActionBean bean = new ActionBean(10013,1005,context.getString(R.string.office),1);
		
		int[] checkChangeMode = {8,1,1,1,1};bean.setCheckChangeMode(checkChangeMode);
		
		int[] highTime = {200,500,300,100}; 
		bean.setHighTime(highTime);
		
		int[] lowTime = {600,400,500,400};
		bean.setLowTime(lowTime);
		
		int[] innerHighAndLow = {5,0,5,0,5,0,5,0};
		bean.setInnerHighAndLow(innerHighAndLow);
		
		int[] interval = {1000,1000,1000,1000};
		bean.setInterval(interval);
		
		int[] Period = {5000,5000,5000,5000}; 
		bean.setPeriod(Period);
		
		int[] rateMin = {10,10,10,10}; 
		int[] rateMax = {90,90,90,90}; 
		int[][] powerLV = {{10,10,10,10},{15,15,15,15}
                          ,{21,21,21,21},{27,27,27,27}
                          ,{33,33,33,33},{39,39,39,39}
                          ,{44,44,44,44},{50,50,50,50}
                          ,{56,56,56,56},{62,62,62,62}
                          ,{68,68,68,68},{73,73,73,73}
                          ,{79,79,79,79},{85,85,85,85}
                          ,{90,90,90,90}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);

		int[] minmax = {60,1,60,1,60,1,60,1}; bean.setMinMaxList(minmax);
		int[] mode12 = {1,1,1,1,1,1,1,1}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//听潮按摩动作
	public static ActionBean getTideAction(Context context){
		ActionBean bean = new ActionBean(10014,1006,context.getString(R.string.seaside),1);
		
		int[] checkChangeMode = {6,5,4,1}
		;bean.setCheckChangeMode(checkChangeMode);
		
		int[] highTime = {1000,2000,100}; 
		bean.setHighTime(highTime);
		
		int[] lowTime = {2000,500,150};
		bean.setLowTime(lowTime);
		
		int[] innerHighAndLow = {4,0,5,0,7,0};
		bean.setInnerHighAndLow(innerHighAndLow);
		
		int[] interval = {2000,500,1000};
		bean.setInterval(interval);
		
		int[] Period = {10000,5000,5000}; 
		bean.setPeriod(Period);
		
		int[] rateMin = {10,10,10}; 
		int[] rateMax = {80,80,80}; 
		int[][] powerLV = {{10,10,10},{15,15,15}
                          ,{20,20,20},{25,25,25}
                          ,{30,30,30},{35,35,35}
                          ,{40,40,40},{45,45,45}
                          ,{50,50,50},{55,55,55}
                          ,{60,60,60},{65,65,65}
                          ,{70,70,70},{75,75,75}
                          ,{80,80,80}};
		
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {10,100,30,150,60,1};
		bean.setMinMaxList(minmax);
		int[] mode12 = {5,1,1,3,1,1}; 
		bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//空谷按摩动作
	public static ActionBean getValleyAction(Context context){
		ActionBean bean = new ActionBean(10015,1007,context.getString(R.string.valley),1);
		
		int[] checkChangeMode = {10,1,1,1,1,1};bean.setCheckChangeMode(checkChangeMode);
		
		int[] highTime = {300,3000,2000,180,1000}; bean.setHighTime(highTime);
		int[] lowTime = {50,500,900,500,1000}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {5,0,5,0,5,0,8,0,5,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {1000,500,500,500,500}; bean.setInterval(interval);
		int[] Period = {5000,2500,2500,2500,2500}; bean.setPeriod(Period);
		
		int[] rateMin = {10,10,10,10,10}; 
		int[] rateMax = {90,90,90,90,90}; 
		int[][] powerLV = {{10,10,10,10,10},{15,15,15,15,15}
                          ,{21,21,21,21,21},{27,27,27,27,27}
                          ,{33,33,33,33,33},{39,39,39,39,39}
                          ,{44,44,44,44,44},{50,50,50,50,50}
                          ,{56,56,56,56,56},{62,62,62,62,62}
                          ,{68,68,68,68,68},{73,73,73,73,73}
                          ,{79,79,79,79,79},{85,85,85,85,85}
                          ,{90,90,90,90,90}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {60,1,60,1,60,1,60,1,60,1}; bean.setMinMaxList(minmax);
		int[] mode12 = {1,1,1,1,1,1,1,1,1,1}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}

	//在路上按摩动作
	public static ActionBean getOnTheWayAction(Context context){
		ActionBean bean = new ActionBean(10016,1008,context.getString(R.string.on_the_way),1);
		
		int[] checkChangeMode = {4,1,1};bean.setCheckChangeMode(checkChangeMode);
		
		int[] highTime = {2500,50};
		bean.setHighTime(highTime);
		
		int[] lowTime = {500,250};
		bean.setLowTime(lowTime);
		
		int[] innerHighAndLow = {6,0,10,0}; 
		bean.setInnerHighAndLow(innerHighAndLow);
		
		int[] interval = {1000,2000};
		bean.setInterval(interval);
		
		int[] Period = {6000,10000};
		bean.setPeriod(Period);
		
		int[] rateMin = {10,10}; 
		int[] rateMax = {70,70}; 
		int[][] powerLV = {{10,10},{14,14}
                          ,{18,18},{22,22}
                          ,{27,27},{31,31}
                          ,{35,35},{40,40}
                          ,{44,44},{48,48}
                          ,{53,53},{57,57}
                          ,{61,61},{65,65}
                          ,{70,70}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {60,1,50,1}; bean.setMinMaxList(minmax);
		int[] mode12 = {1,1,1,1}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//小憩按摩动作
	public static ActionBean getRespiteAction(Context context){
		ActionBean bean = new ActionBean(10017,1009,context.getString(R.string.taking_a_nap),1);
		
		int[] checkChangeMode = {8,1,1,1,1};bean.setCheckChangeMode(checkChangeMode);
		
		int[] highTime = {20,4,180,100}; bean.setHighTime(highTime);
		int[] lowTime = {160,100,1000,800}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {5,0,4,0,10,0,7,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {500,500,500,1000}; bean.setInterval(interval);
		int[] Period = {1500,2500,2000,4500}; bean.setPeriod(Period);
		
		int[] rateMin = {10,20,10,10}; 
		int[] rateMax = {90,90,90,70};
		int[][] powerLV = {{10,20,10,10},{15,25,15,14}
                          ,{21,30,21,18},{27,35,27,22}
                          ,{32,40,32,27},{38,45,38,31}
                          ,{44,50,44,35},{49,55,49,40}
                          ,{55,60,55,44},{61,65,61,48}
                          ,{67,70,67,53},{72,75,72,57}
                          ,{78,80,78,61},{84,85,84,65}
                          ,{90,90,90,70}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {60,1,60,1,60,1,60,1}; bean.setMinMaxList(minmax);
		int[] mode12 = {1,1,1,1,1,1,1,1}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//旅行按摩动作
	public static ActionBean getTravelAction(Context context){
		ActionBean bean = new ActionBean(10018,1010,context.getString(R.string.travel),1);
		
		int[] checkChangeMode = {4,1,1};bean.setCheckChangeMode(checkChangeMode);
		
		int[] highTime = {150,2000}; bean.setHighTime(highTime);
		int[] lowTime = {150,900}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {10,0,6,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {100,1500}; bean.setInterval(interval);
		int[] Period = {5000,10000}; bean.setPeriod(Period);
		
		int[] rateMin = {10,10}; 
		int[] rateMax = {80,80}; 
		int[][] powerLV = {{10,10},{15,15}
                          ,{20,20},{25,25}
                          ,{30,30},{35,35}
                          ,{40,40},{45,45}
                          ,{50,50},{55,55}
                          ,{60,60},{65,65}
                          ,{70,70},{75,75}
                          ,{80,80}};
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {150,1,60,1}; bean.setMinMaxList(minmax);
		int[] mode12 = {1,1,1,1}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	/*************************************表情识别***********************************************/
	//平静  —— 按
	public static ActionBean getCalmAction(Context context){
		ActionBean bean = new ActionBean(10019,-3,context.getString(R.string.calm),0);
		
		int[] checkChangeMode = {(short)2,(short)5};bean.setCheckChangeMode(checkChangeMode);
		int[] highTime = {1000}; bean.setHighTime(highTime);
		int[] lowTime = {2000}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {4,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {2000}; bean.setInterval(interval);
		int[] Period = {20000}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {90}; 
		int[][] powerLV = {{10},{15},{21},{27},{32},{38},{44},{49},{55},{61},{67},{72},{78},{84},{90}};
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {(short)60,(short)1}; bean.setMinMaxList(minmax);
		int[] mode12 = {(short)1,(short)1}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//害怕  —— 揉
	public static ActionBean getFearAction(Context context){
		ActionBean bean = new ActionBean(10020,-3,context.getString(R.string.fear),0);
		
		int[] checkChangeMode = {(short)2,(short)1};bean.setCheckChangeMode(checkChangeMode);
		int[] highTime = {2500}; bean.setHighTime(highTime);
		int[] lowTime = {500}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {6,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {1000}; bean.setInterval(interval);
		int[] Period = {3000}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {80}; 
		int[][] powerLV = {{10},{15},{20},{25},{30}
		                  ,{35},{40},{45},{50},{55}
		                  ,{60},{65},{70},{75},{80}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {(short)60,(short)1}; bean.setMinMaxList(minmax);
		int[] mode12 = {(short)1,(short)1}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//生气  —— 抚
	public static ActionBean getGetAngryAction(Context context){
		ActionBean bean = new ActionBean(10021,-3,context.getString(R.string.get_angry),0);
		
		int[] checkChangeMode = {(short)2,(short)1};bean.setCheckChangeMode(checkChangeMode);
		int[] highTime = {1000}; bean.setHighTime(highTime);
		int[] lowTime = {1000}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {5,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {1000}; bean.setInterval(interval);
		int[] Period = {5000}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {40}; 
		int[][] powerLV = {{10},{12},{14},{16},{18},{20},{22},{24},{26},{28},{30},{32},{34},{37},{40}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {(short)60,(short)1}; bean.setMinMaxList(minmax);
		int[] mode12 = {(short)1,(short)1}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//开心  —— 挤
	public static ActionBean getHappyAction(Context context){
		ActionBean bean = new ActionBean(10022,-3,context.getString(R.string.happy),0);
		
		int[] checkChangeMode = {(short)2,(short)4};bean.setCheckChangeMode(checkChangeMode);
		int[] highTime = {800}; bean.setHighTime(highTime);
		int[] lowTime = {1200}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {5,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {1000}; bean.setInterval(interval);
		int[] Period = {2500}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {90}; 
		int[][] powerLV = {{10},{15},{21},{27},{32},{38},{44},{49},{55},{61},{67},{72},{78},{84},{90}};
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {(short)10,(short)80}; bean.setMinMaxList(minmax);
		int[] mode12 = {(short)20,(short)5}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//忧愁  —— 肘
	public static ActionBean getSadAction(Context context){
		ActionBean bean = new ActionBean(10023,-3,context.getString(R.string.sad),0);
		
		int[] checkChangeMode = {(short)2,(short)1};bean.setCheckChangeMode(checkChangeMode);
		int[] highTime = {2000}; bean.setHighTime(highTime);
		int[] lowTime = {900}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {6,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {1500}; bean.setInterval(interval);
		int[] Period = {5000}; bean.setPeriod(Period);
		
		int[] rateMin = {15}; 
		int[] rateMax = {90}; 
		int[][] powerLV = {{15},{20},{25},{31},{36},{41},{47},{52},{57},{63},{68},{73},{79},{84},{90}}; 
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {(short)100,(short)1}; bean.setMinMaxList(minmax);
		int[] mode12 = {(short)1,(short)1}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	//伤感  —— 捶
	public static ActionBean getSentimentalAction(Context context){
		ActionBean bean = new ActionBean(10024,-3,context.getString(R.string.sentimental),0);
		
		int[] checkChangeMode = {(short)2,(short)1};bean.setCheckChangeMode(checkChangeMode);
		int[] highTime = {50}; bean.setHighTime(highTime);
		int[] lowTime = {250}; bean.setLowTime(lowTime);
		int[] innerHighAndLow = {10,0}; bean.setInnerHighAndLow(innerHighAndLow);
		int[] interval = {2000}; bean.setInterval(interval);
		int[] Period = {10000}; bean.setPeriod(Period);
		
		int[] rateMin = {10}; 
		int[] rateMax = {90}; 
		int[][] powerLV = {{10},{15},{21},{27},{32},{38},{44},{49},{55},{61},{67},{72},{78},{84},{90}};
		
		bean.setRateMin(rateMin);
		bean.setRateMax(rateMax);
		bean.setPowerLV(powerLV);
		
		int[] minmax = {(short)250,(short)1}; bean.setMinMaxList(minmax);
		int[] mode12 = {(short)1,(short)1}; bean.setMode12List(mode12);
		
		bean.setMaxWorkTime(1800);
		
		return bean;
	}
	
	
	/*************************************初始化***********************************************/
	
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
}
