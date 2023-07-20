package com.releasy.android.utils;

import com.releasy.android.R;

public class ActionUtils {

	
	/**
	 * 获取按摩动作ID获取背景大图
	 * @param actionId
	 * @return
	 */
	public static int getActionIcon(int actionId){
		int actionIcon = R.drawable.ic_actionic_rou;
		switch(actionId){
		case 10001:
			actionIcon = R.drawable.ic_actionic_rou;
			break;
		case 10002:
			actionIcon = R.drawable.ic_actionic_an;
			break;
		case 10003:
			actionIcon = R.drawable.ic_actionic_ji;
			break;
		case 10004:
			actionIcon = R.drawable.ic_actionic_fu;
			break;
		case 10005:
			actionIcon = R.drawable.ic_actionic_zhou;
			break;
		case 10006:
			actionIcon = R.drawable.ic_actionic_chui;
			break;
		case 10007:
			actionIcon = R.drawable.ic_actionic_tuina;
			break;
		case 10008:
			actionIcon = R.drawable.ic_actionic_zhenjiu;
			break;
		case 10009:
			actionIcon = R.drawable.ic_actionic_huoguan;
			break;
		case 10010:
			actionIcon = R.drawable.ic_actionic_guasha;
			break;
		case 10011:
			actionIcon = R.drawable.chan1;
			break;
		case 10012:
			actionIcon = R.drawable.zhai1;
			break;
		case 10013:
			actionIcon = R.drawable.bangongshi1;
			break;
		case 10014:
			actionIcon = R.drawable.tingchao1;
			break;
		case 10015:
			actionIcon = R.drawable.konggu1;
			break;
		case 10016:
			actionIcon = R.drawable.zailushang1;
			break;
		case 10017:
			actionIcon = R.drawable.xiaoqi1;
			break;
		case 10018:
			actionIcon = R.drawable.lvxing1;
			break;
		case 10019:
			actionIcon = R.drawable.ic_mood_2;
			break;
		case 10020:
			actionIcon = R.drawable.ic_mood_5;
			break;
		case 10021:
			actionIcon = R.drawable.ic_mood_4;
			break;
		case 10022:
			actionIcon = R.drawable.ic_mood_3;
			break;
		case 10023:
			actionIcon = R.drawable.ic_mood_1;
			break;
		case 10024:
			actionIcon = R.drawable.ic_mood_6;
			break;
		default:
			break;
		}
		
		return actionIcon;
	}
}
