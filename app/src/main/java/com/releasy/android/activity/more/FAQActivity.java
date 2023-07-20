package com.releasy.android.activity.more;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;

import com.releasy.android.R;
import com.releasy.android.activity.main.BaseActivity;
import com.releasy.android.adapter.FaqAdapter;
import com.releasy.android.bean.FaqBean;
import com.releasy.android.view.TopNavLayout;

public class FAQActivity extends BaseActivity{

	private TopNavLayout mTopNavLayout;           //导航菜单栏
	private ExpandableListView listView;          //常见问题列表
	private FaqAdapter adapter;
	private List<FaqBean> faqDataList ;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        
        init();            //初始化
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		faqDataList = new ArrayList<FaqBean>();
		
		initViews();    //初始化视图
		setTopNav();    //初始化导航栏
		initEvents();   //初始化点击事件
		
		setData();
	}
	
	/**
	 * 初始化导航栏
	 */
	private void setTopNav(){
		mTopNavLayout.setTitltTxt(R.string.faq);
		mTopNavLayout.setLeftImgSrc(R.drawable.ic_nav_bar_back);
	}
	
	/**
	 * 初始化视图
	 */
	protected void initViews() {
		mTopNavLayout = (TopNavLayout) findViewById(R.id.topNavLayout);
		listView = (ExpandableListView) findViewById(R.id.faqList); 
	}

	/**
	 * 初始化点击事件
	 */
	protected void initEvents() {
		//导航栏返回按钮逻辑
		mTopNavLayout.setLeftImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				FAQActivity.this.finish();
			}});
	}
	
	private void setData(){
		FaqBean bean;
		String question = "";
		String answer = "";
		
		question = this.getString(R.string.faq_q_1);
		answer = this.getString(R.string.faq_a_1);
		bean = new FaqBean(question,answer);
		faqDataList.add(bean);
		
		question = this.getString(R.string.faq_q_2);
		answer = this.getString(R.string.faq_a_2);
		bean = new FaqBean(question,answer);
		faqDataList.add(bean);
		
		question = this.getString(R.string.faq_q_3);
		answer = this.getString(R.string.faq_a_3);
		bean = new FaqBean(question,answer);
		faqDataList.add(bean);
		
		question = this.getString(R.string.faq_q_4);
		answer = this.getString(R.string.faq_a_4);
		bean = new FaqBean(question,answer);
		faqDataList.add(bean);
		
		question = this.getString(R.string.faq_q_5);
		answer = this.getString(R.string.faq_a_5);
		bean = new FaqBean(question,answer);
		faqDataList.add(bean);
		
		question = this.getString(R.string.faq_q_6);
		answer = this.getString(R.string.faq_a_6);
		bean = new FaqBean(question,answer);
		faqDataList.add(bean);
		
		question = this.getString(R.string.faq_q_7);
		answer = this.getString(R.string.faq_a_7);
		bean = new FaqBean(question,answer);
		faqDataList.add(bean);
		
		question = this.getString(R.string.faq_q_8);
		answer = this.getString(R.string.faq_a_8);
		bean = new FaqBean(question,answer);
		faqDataList.add(bean);
		
		question = this.getString(R.string.faq_q_9);
		answer = this.getString(R.string.faq_a_9);
		bean = new FaqBean(question,answer);
		faqDataList.add(bean);
		
		question = this.getString(R.string.faq_q_10);
		answer = this.getString(R.string.faq_a_10);
		bean = new FaqBean(question,answer);
		faqDataList.add(bean);
		
		question = this.getString(R.string.faq_q_11);
		answer = this.getString(R.string.faq_a_11);
		bean = new FaqBean(question,answer);
		faqDataList.add(bean);
		
		question = this.getString(R.string.faq_q_12);
		answer = this.getString(R.string.faq_a_12);
		bean = new FaqBean(question,answer);
		faqDataList.add(bean);
		
		question = this.getString(R.string.faq_q_13);
		answer = this.getString(R.string.faq_a_13);
		bean = new FaqBean(question,answer);
		faqDataList.add(bean);
		
		
		adapter = new FaqAdapter(FAQActivity.this,faqDataList);
		listView.setAdapter(adapter);
	}

}
