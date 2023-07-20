package com.releasy.android.activity.releasy;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.releasy.android.R;
import com.releasy.android.activity.main.BaseActivity;
import com.releasy.android.adapter.SelectBaseActionAdapter;
import com.releasy.android.adapter.SelectSceneActionAdapter;
import com.releasy.android.bean.ActionBean;
import com.releasy.android.constants.ActionConstants;
import com.releasy.android.db.ActionDBUtils;
import com.releasy.android.db.ReleasyDatabaseHelper;
import com.releasy.android.view.PowerBar;
import com.releasy.android.view.TopNavLayout;

/**
 * 自定义放松馆 选择按摩动作页面
 * @author Lighting.Z
 *
 */
public class SelectActionActivity extends BaseActivity{

	private TopNavLayout mTopNavLayout;                   //导航菜单栏
	private RadioGroup actionRG;                          //动作单选菜单
	private GridView gridView;                            //动作列表
	private PowerBar powerBar;                            //力度调整控件
	private ReleasyDatabaseHelper db;                     //数据库
	private List<ActionBean> baseActionList;              //基础动作列表
	private List<ActionBean> sceneActionList;             //场景动作列表
	private SelectBaseActionAdapter baseActionAdapter;
	private SelectSceneActionAdapter sceneActionAdapter;
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_action);
        
        getBundle();       //获取传入数据
        init();            //初始化
	}
	
	/**
	 * 获取传入的数据
	 */
	private int item;
	private boolean haveAction;
	private int power;
	private int actionId;
	private void getBundle(){
		Bundle bundle = this.getIntent().getExtras();
		item = bundle.getInt("deviceItem");     //设备Item
		power = bundle.getInt("power");         //设备力度
		haveAction = bundle.getBoolean("haveAction");     //设备是否有动作信息
		if(haveAction){
			actionId = bundle.getInt("actionId");         //设备按摩动作数据库ID
			showLogD("SelectActionActivity actionId : " + actionId);
		}
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		db = ActionDBUtils.openData(this);  //获取DB
		baseActionList = new ArrayList<ActionBean>();
		sceneActionList = new ArrayList<ActionBean>();
		
		getDbData();    //获取数据库数据
		initViews();    //初始化视图
		setTopNav();    //初始化导航栏
		initEvents();   //初始化点击事件
		setAdapter();   //设置宫格Adapter
		loadData();     //如果有动作信息 载入信息
	}
	
	/**
	 * 初始化导航栏
	 */
	private void setTopNav(){
		mTopNavLayout.setTitltTxt(R.string.set_device_action);
		mTopNavLayout.setLeftImgSrc(R.drawable.ic_nav_bar_back);
		mTopNavLayout.setRightTxt(R.string.confirm);
	}
	
	/**
	 * 初始化视图
	 */
	protected void initViews() {
		mTopNavLayout = (TopNavLayout) findViewById(R.id.topNavLayout);
		actionRG = (RadioGroup) findViewById(R.id.tabRG);
		gridView = (GridView) findViewById(R.id.gridView);
		powerBar = (PowerBar) findViewById(R.id.powerBar);
	}

	/**
	 * 设置宫格Adapter
	 */
	private void setAdapter(){
		if(actionRG.getCheckedRadioButtonId() == R.id.baseActionRG){
			gridView.setAdapter(baseActionAdapter);
		}
		else if(actionRG.getCheckedRadioButtonId() == R.id.sceneActionRG){
			gridView.setAdapter(sceneActionAdapter);
		}
	}
	
	/**
	 * 初始化点击事件
	 */
	protected void initEvents() {
		//导航栏返回按钮点击事件
		mTopNavLayout.setLeftImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				SelectActionActivity.this.finish();
			}});
		
		//导航栏确定按钮点击事件
		mTopNavLayout.setRightTxtOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				int dbId = -1 ;
				if(actionRG.getCheckedRadioButtonId() == R.id.baseActionRG){
					for(int i = 0; i < baseActionList.size(); i++){
						if(baseActionList.get(i).getIsChoose()){
							dbId = baseActionList.get(i).getDBId();
						}
					}
					
				}
				else if(actionRG.getCheckedRadioButtonId() == R.id.sceneActionRG){
					for(int i = 0; i < sceneActionList.size(); i++){
						if(sceneActionList.get(i).getIsChoose()){
							dbId = sceneActionList.get(i).getDBId();
						}
					}
				}
				
				//没有设置按摩动作时 提示用户
				if(dbId == -1){
					Toast.makeText(SelectActionActivity.this, R.string.pls_selsct_action, Toast.LENGTH_LONG).show();
					return;
				}
				
				//设置返回参数
				Intent data=new Intent();  
				data.putExtra("deviceItem", item); 
	            data.putExtra("dbId", dbId);  
	            data.putExtra("power",powerBar.getPower());
	            setResult(Activity.RESULT_OK, data);  
	            finish(); 
			}});
		
		/**
		 * 力度控件增加力度的点击事件
		 */
		powerBar.setAddPowerOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				powerBar.addPower();
			}});
		
		/**
		 * 力度控件降低力度的点击事件
		 */
		powerBar.setSubtractOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				powerBar.subtractPower();
			}});
		
		/**
		 * 动作菜单切换点击事件
		 */
		actionRG.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
				setAdapter();
			}});
		
		/**
		 * 动作宫格点击事件
		 */
		gridView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				if(actionRG.getCheckedRadioButtonId() == R.id.baseActionRG){
					for(int i = 0; i < baseActionList.size(); i++){
						baseActionList.get(i).setIsChoose(false);
					}
					baseActionList.get(position).setIsChoose(true);
					baseActionAdapter.notifyDataSetChanged();
				}
				else if(actionRG.getCheckedRadioButtonId() == R.id.sceneActionRG){
					for(int i = 0; i < sceneActionList.size(); i++){
						sceneActionList.get(i).setIsChoose(false);
					}
					sceneActionList.get(position).setIsChoose(true);
					sceneActionAdapter.notifyDataSetChanged();
				}
				
			}});
	}
	
	/**
	 * 获取数据库数据
	 */
	private void getDbData(){
		baseActionList = ActionDBUtils.searchBaseActionData(db,this);
		sceneActionList = ActionDBUtils.searchSceneActionData(db,this);
		baseActionAdapter = new SelectBaseActionAdapter(this,baseActionList);
		sceneActionAdapter = new SelectSceneActionAdapter(this,sceneActionList);
	}
	
	/**
	 * 如果有动作信息 载入信息
	 */
	private void loadData(){
		powerBar.setPower(power);
		if(haveAction){
			for(int i = 0; i < baseActionList.size(); i++){
				if(baseActionList.get(i).getActionId() == actionId){
					baseActionList.get(i).setIsChoose(true);
					actionRG.check(R.id.baseActionRG);
					return;
				}
			}
			
			for(int i = 0; i < sceneActionList.size(); i++){
				if(sceneActionList.get(i).getActionId() == actionId){
					sceneActionList.get(i).setIsChoose(true);
					actionRG.check(R.id.sceneActionRG);
					return;
				}
			}
		}
	}
	

}
