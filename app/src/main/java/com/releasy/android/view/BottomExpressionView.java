package com.releasy.android.view;

import com.releasy.android.R;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;

public class BottomExpressionView implements OnTouchListener{
	
	private PopupWindow popupWindow;
    private View bottomExpressionView;
    
	private ImageView cancelBtn;
    private Activity mContext;
    
    private OnItemClickListener clickListener;

    public BottomExpressionView(Activity context,OnItemClickListener clickListener) {
        this.clickListener = clickListener;
        mContext = context;
        
        init();
    }
    
	/**
     * 初始化
     */
    private void init(){
    	
    	LayoutInflater inflater = LayoutInflater.from(mContext);
    	bottomExpressionView = inflater.inflate(R.layout.layout_action_popwindow, null);
    	
    	initView();
    	initEvents();
    	
    	popupWindow = new PopupWindow(bottomExpressionView,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,true);
        popupWindow.setAnimationStyle(R.style.popwin_anim_style);
        ColorDrawable dw = new ColorDrawable(mContext.getResources().getColor(R.color.color_33333333));
        popupWindow.setBackgroundDrawable(dw);
        bottomExpressionView.setOnTouchListener(this);
    }
    
    /**
     * 初始化视图
     */
    private void initView(){
    	
    	cancelBtn = (ImageView) bottomExpressionView.findViewById(R.id.cancel_btn);
    }
	
	/**
	 * 动作菜单切换点击事件
	 */
	private void initEvents(){
		cancelBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				popupWindow.dismiss();
			}});
	}
    
	/**
     * 显示菜单
     */
    public void show(){
    	//得到当前activity的rootView
    	View rootView=((ViewGroup)mContext.findViewById(android.R.id.content)).getChildAt(0);
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); 
    }
	
	public boolean onTouch(View arg0, MotionEvent event) {
		int height = bottomExpressionView.findViewById(R.id.action_pop_layout).getTop();
        int y=(int) event.getY();
        if(event.getAction()==MotionEvent.ACTION_UP){
            if(y<height){
            	popupWindow. dismiss();
            }
        }
        return true;
	}
	
	public void dismiss(){
		popupWindow.dismiss();
	}
}
