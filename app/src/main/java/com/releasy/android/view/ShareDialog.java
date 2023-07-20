package com.releasy.android.view;

import com.releasy.android.R;
import com.releasy.android.constants.Constants;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ShareDialog extends Dialog{

	private Context context;
	private ImageView closeImg;
	private ImageView shareIcon;
	private TextView shareIconTxt;
	private TextView shareContentTxt;
	private Button shareBtn;
	
	public ShareDialog(Context context) {
		super(context, R.style.Theme_Light_FullScreenDialogAct);
		this.context = context;
		
		setContentView(R.layout.layout_share_dialog);
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
		shareIcon = (ImageView) findViewById(R.id.shareIcon);
		shareIconTxt = (TextView) findViewById(R.id.shareIconTxt);
		shareContentTxt = (TextView) findViewById(R.id.shareContentTxt);
		shareBtn = (Button) findViewById(R.id.shareBtn);
	}
	
	/**
	 * 初始化点击事件
	 */
	private void initEvents(){
		closeImg.setOnClickListener(new ImageView.OnClickListener(){
			public void onClick(View arg0) {
				dismiss();
			}});
		
		shareBtn.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View arg0) {
				Intent intent = new Intent(Constants.SHARE);
				context.sendBroadcast(intent);
				dismiss();
			}});
	}
	
	public void setInfo(int time){
		if(time <= 5){
			shareIcon.setImageResource(R.drawable.ic_share_bad);
			shareIconTxt.setText("不爽呀!");
			shareContentTxt.setText("你才按摩了 " + time + " 分钟,再来嘛~!");
		}
		else if(time > 5 && time <= 10){
			shareIcon.setImageResource(R.drawable.ic_share_general);
			shareIconTxt.setText("一般般!");
			shareContentTxt.setText("你按摩了 " + time + " 分钟,身子还是痒痒的啦!");
		}
		else{
			shareIcon.setImageResource(R.drawable.ic_share_good);
			shareIconTxt.setText("太棒啦!");
			shareContentTxt.setText("你按摩了 " + time + " 分钟,精神焕发了耶!");
		}
	}
}
