package com.releasy.android.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.releasy.android.R;
import com.releasy.android.constants.Constants;

public class ActionFeedbackDialog extends Dialog{

	private Context context;
	private ImageView closeImg;
	private ImageView feedbackIcon;
	private TextView feedbackTxt;
	private Button goodBtn;
	private Button badBtn;
	private EditText feedbackEdit;
	private LinearLayout optionsLayout;
	private Button seedBtn;
	
	public ActionFeedbackDialog(Context context) {
		super(context, R.style.Theme_Light_FullScreenDialogAct);
		this.context = context;
		
		setContentView(R.layout.layout_action_feedback_dialog);
		setCancelable(true);
		setCanceledOnTouchOutside(false);
		
		init();
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		initView();
		initEvents();
	}
	
	/**
	 * 初始化视图
	 */
	private void initView(){
		closeImg = (ImageView) findViewById(R.id.closeDialog);
		feedbackIcon = (ImageView) findViewById(R.id.feedbackIcon);
		feedbackTxt = (TextView) findViewById(R.id.feedbackTxt);
		goodBtn = (Button) findViewById(R.id.goodBtn);
		badBtn = (Button) findViewById(R.id.badBtn);
		feedbackEdit = (EditText) findViewById(R.id.feedbackEdit);
		optionsLayout = (LinearLayout) findViewById(R.id.optionsLayout);
		seedBtn = (Button) findViewById(R.id.seedBtn);
	}
	
	/**
	 * 初始化点击事件
	 */
	private void initEvents(){
		closeImg.setOnClickListener(new ImageView.OnClickListener(){
			public void onClick(View arg0) {
				dismiss();
			}});
		
		goodBtn.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View arg0) {
				
				dismiss();
			}});
		
		badBtn.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View arg0) {
				feedbackTxt.setVisibility(View.GONE);
				feedbackEdit.setVisibility(View.VISIBLE);
				optionsLayout.setVisibility(View.GONE);
				seedBtn.setVisibility(View.VISIBLE);
			}});
		
		seedBtn.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View arg0) {
				
				dismiss();
			}});
	}
	
	public void setInfo(){
		feedbackTxt.setText("看看效果");
	}
}

