package com.releasy.android.activity.more;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.releasy.android.R;
import com.releasy.android.ReleasyApplication;
import com.releasy.android.activity.main.BaseFragment;
import com.releasy.android.activity.main.InformationPerfectActivity;
import com.releasy.android.activity.main.MainTabActivity;
import com.releasy.android.utils.SharePreferenceUtils;
import com.releasy.android.view.TopNavLayout;

/**
 * 主页中的其他页面
 * @author Lighting.Z
 *
 */
public class MoreMainFragment extends BaseFragment{

	private View view; 
	private TopNavLayout mTopNavLayout;          //导航菜单栏
	private LinearLayout userLayout;             //个人信息
	private LinearLayout faqLy;                  //常见问题
	private LinearLayout feedbackLy;             //意见反馈
	private TextView newMsgNumTxt;               //反馈通知数
	private LinearLayout aboutLy;                //关于
	private LinearLayout recordLayout;           //使用记录
	private ReleasyApplication app;              //Application
	private MainTabActivity activity;            //绑定Activiy
	private SharePreferenceUtils spInfo;         //SharePreference
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLogD("MoreMainFragment onCreate");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	showLogD("MoreMainFragment onCreateView");
        if (view == null || view.getParent() != null) {
    		//判定视图是否存在   不重复加载
    		view = inflater.inflate(R.layout.fragment_more_main, container, false);
		}
    	init(); //初始化
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showLogD("MoreMainFragment onActivityCreated");
    }

    public void onResume() {
        super.onResume();
        showLogD("MoreMainFragment onResume");
        setActionEntryBtn(this.getActivity(),app,mTopNavLayout);
        setNewMsgNumTxt();  //设置新通知数
        activity.setMoreRbImg();  //设置nav tab 图标
    }

    public void onPause() {
        super.onPause();
        showLogD("MoreMainFragment onPause");
    }

    public void onDestroyView() {
        super.onDestroyView();
        showLogD("MoreMainFragment onDestroyView");
    }

    public void onDestroy() {
        super.onDestroy();
        showLogD("MoreMainFragment onDestroy");
    }

    /**
     * 初始化
     */
    private void init(){
    	app = (ReleasyApplication) this.getActivity().getApplication();
    	spInfo = new SharePreferenceUtils(getActivity()); 
    	activity = (MainTabActivity)getActivity();
    	
    	initViews();   //初始化视图
    	initEvents();  //初始化点击事件
    	setTopNav();   //初始化导航栏
    }
    
    /**
     * 初始化视图
     */
	protected void initViews() {
		mTopNavLayout = (TopNavLayout) view.findViewById(R.id.topNavLayout);
		userLayout = (LinearLayout) view.findViewById(R.id.userLayout);
		faqLy = (LinearLayout) view.findViewById(R.id.faqLayout);
		feedbackLy = (LinearLayout) view.findViewById(R.id.feedbackLayout);
		newMsgNumTxt = (TextView) view.findViewById(R.id.newMsgNumTxt);
		aboutLy = (LinearLayout) view.findViewById(R.id.aboutLayout);
		recordLayout = (LinearLayout) view.findViewById(R.id.recordLayout);
	}
	
	/**
	 * 初始化导航栏
	 */
	private void setTopNav(){
		mTopNavLayout.setTitltTxt(R.string.other_title);
	}

	/**
	 * 设置新通知数
	 */
	private void setNewMsgNumTxt(){
		if(spInfo.getHasFeedbackNotify()){
			newMsgNumTxt.setText(spInfo.getNotificationCount() + "");
			newMsgNumTxt.setVisibility(View.VISIBLE);
		}
		else{
			newMsgNumTxt.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 初始化事件
	 */
	protected void initEvents() {
		//跳转至个人信息
		userLayout.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				if(spInfo.getUId() == 10000){
					Intent intent = new Intent(MoreMainFragment.this.getActivity(),InformationPerfectActivity.class);
					startActivity(intent);	
				}
				else{
					Intent intent = new Intent(MoreMainFragment.this.getActivity(),UserInfoActivity.class);
					startActivity(intent);					
				}
			}});
		
		//跳转至常见问题页面
		faqLy.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				Intent intent = new Intent(MoreMainFragment.this.getActivity(),FAQActivity.class);
		        startActivity(intent);
			}});
		
		//跳转至意见反馈页面
		feedbackLy.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				Intent intent = new Intent(MoreMainFragment.this.getActivity(),FeedbackActivity.class);
		        startActivity(intent);
			}});
		
		//跳转至关于页面
		aboutLy.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				Intent intent = new Intent(MoreMainFragment.this.getActivity(),AboutActivity.class);
		        startActivity(intent);
			}});
		
		//跳转至使用记录页面
		recordLayout.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				Intent intent = new Intent(MoreMainFragment.this.getActivity(),UserRecordActivity.class);
		        startActivity(intent);
			}});
	}
	
	
}
