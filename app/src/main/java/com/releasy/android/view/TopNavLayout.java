package com.releasy.android.view;

import com.releasy.android.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 导航栏控件
 * @author Lighting.Z
 *
 */
public class TopNavLayout extends LinearLayout{
	
	private LayoutInflater mInflater;
	private View mTobNav;                     //主视图
	private RelativeLayout topNavView;
	private TextView titleTxt;                //标题文字  
	private ImageView topNavLeftImg;          //左侧图标按钮
	private ImageView topNavRightImg;         //右侧图标按钮
	private ImageView tobNavRightSecondImg;   //右侧第2按钮
	private TextView topNavRightTxt;          //右侧文字按钮
	
	public TopNavLayout(Context context) {
		super(context);
		init(context);
	}

	public TopNavLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	/**
	 * 初始化
	 * @param context
	 */
	private void init(Context context) {
		mInflater = LayoutInflater.from(context);
		mTobNav = mInflater.inflate(R.layout.common_top_nav, null);  //加载XML
		addView(mTobNav);  //添加视图
		initViews();       //初始化控件

	}

	/**
	 * 初始化视图
	 */
	private void initViews(){
		topNavView = (RelativeLayout) findViewByTopNavId(R.id.top_nav_view);
		titleTxt = (TextView) findViewByTopNavId(R.id.titleTxt);
		topNavLeftImg = (ImageView) findViewByTopNavId(R.id.tobNavLeftImg);
		topNavRightImg = (ImageView) findViewByTopNavId(R.id.tobNavRightImg);
		tobNavRightSecondImg = (ImageView) findViewByTopNavId(R.id.tobNavRightSecondImg);
		topNavRightTxt = (TextView) findViewByTopNavId(R.id.tobNavRightTxt);
	}
	
	//findViewById
	private View findViewByTopNavId(int id) {
		return mTobNav.findViewById(id);
	}
	
	public void setTopNavViewBackground(int id){
		topNavView.setBackgroundResource(id);
	}
	
	/**
	 * 设置标题文字
	 * @param strTxt
	 */
	public void setTitltTxt(String strTxt){
		titleTxt.setText(strTxt);
	}
	
	/**
	 * 设置标题文字
	 * @param strId
	 */
	public void setTitltTxt(int strId){
		titleTxt.setText(strId);
	}
	
	/**
	 * 设置标题文字
	 * @param strId
	 */
	public void setTitltColor(int colorId){
		titleTxt.setTextColor(colorId);
	}
	
	/**
	 * 设置左侧图标
	 * @param strId
	 */
	public void setLeftImgSrc(int srcId){
		topNavLeftImg.setImageResource(srcId);
		topNavLeftImg.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 隐藏左侧图标
	 */
	public void setLeftImgGone(){
		topNavLeftImg.setVisibility(View.GONE);
	}

	/**
	 * 显示左侧图标
	 */
	public void setLeftImgVisible(){
		topNavLeftImg.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 设置右侧图标
	 * @param srcId
	 */
	public void setRightImgSrc(int srcId){
		topNavRightImg.setImageResource(srcId);
		topNavRightImg.setVisibility(View.VISIBLE);
	}
	
	public ImageView getRightImg(){
		return topNavRightImg;
	}
	
	/**
	 * 隐藏右侧图标
	 */
	public void setRightImgGone(){
		topNavRightImg.setVisibility(View.GONE);
	}
	
	/**
	 * 显示右侧图标
	 */
	public void setRightImgVisible(){
		topNavRightImg.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 设置右侧第2图标
	 * @param srcId
	 */
	public void setRightSecondImgSrc(int srcId){
		tobNavRightSecondImg.setImageResource(srcId);
		tobNavRightSecondImg.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 隐藏右侧第2图标
	 */
	public void setRightSecondImgGone(){
		tobNavRightSecondImg.setVisibility(View.GONE);
	}
	
	/**
	 * 显示右侧图标
	 */
	public void setRightSecondImgVisible(){
		tobNavRightSecondImg.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 设置右侧文字
	 * @param strTxt
	 */
	public void setRightTxt(String strTxt){
		topNavRightTxt.setText(strTxt);
		topNavRightTxt.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 设置右侧文字
	 * @param strId
	 */
	public void setRightTxt(int strId){
		topNavRightTxt.setText(strId);
		topNavRightTxt.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 隐藏右侧文字
	 */
	public void setRightTxtGone(){
		topNavRightTxt.setVisibility(View.GONE);
	}
	
	/**
	 * 显示右侧文字
	 */
	public void setRightTxtVisible(){
		topNavRightTxt.setVisibility(View.VISIBLE);
	}
	
	//设置顶端左侧图片按钮监听事件
	public void setLeftImgOnClick(OnClickListener onClick){
		topNavLeftImg.setOnClickListener(onClick);
	}
	
	//设置顶端右侧图片按钮监听事件
	public void setRightImgOnClick(OnClickListener onClick){
		topNavRightImg.setOnClickListener(onClick);
	}
	
	//设置顶端右侧第2图片按钮监听事件
	public void setRightSecondImgOnClick(OnClickListener onClick){
		tobNavRightSecondImg.setOnClickListener(onClick);
	}
	
	//设置顶端右侧文字按钮监听事件
	public void setRightTxtOnClick(OnClickListener onClick){
		topNavRightTxt.setOnClickListener(onClick);
	}
}
