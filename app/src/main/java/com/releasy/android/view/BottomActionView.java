package com.releasy.android.view;

import java.util.ArrayList;
import java.util.List;
import com.releasy.android.R;
import com.releasy.android.activity.releasy.ActionDistributionForM2activity;
import com.releasy.android.adapter.SelectBaseActionAdapter;
import com.releasy.android.adapter.SelectSceneActionAdapter;
import com.releasy.android.bean.ActionBean;
import com.releasy.android.db.ActionDBUtils;
import com.releasy.android.db.ActionForM2DBUtils;
import com.releasy.android.db.ReleasyDatabaseHelper;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class BottomActionView implements OnTouchListener{
	
	private PopupWindow popupWindow;
    private View bottomActionView;
    private RadioGroup actionRG;                          //动作单选菜单
	private GridView gridView;                            //动作列表
	private ImageView cancelBtn;
    private Activity mContext;
    private ReleasyDatabaseHelper db;                     //数据库
    private List<ActionBean> baseActionList;              //基础动作列表
	private List<ActionBean> sceneActionList;             //场景动作列表
	private SelectBaseActionAdapter baseActionAdapter;
	private SelectSceneActionAdapter sceneActionAdapter;
    private OnItemClickListener clickListener;

    public BottomActionView(Activity context,OnItemClickListener clickListener) {
        this.clickListener = clickListener;
        mContext = context;
        
        init();
    }
    
    /**
	 * 获取数据库数据
	 */
	private void getDbData(){
		baseActionList = ActionDBUtils.searchBaseActionData(db,mContext);
		sceneActionList = ActionDBUtils.searchSceneActionData(db,mContext);
		baseActionAdapter = new SelectBaseActionAdapter(mContext,baseActionList);
		sceneActionAdapter = new SelectSceneActionAdapter(mContext,sceneActionList);
	}
    
	/**
     * 初始化
     */
    private void init(){
    	db = ActionDBUtils.openData(mContext);  //获取DB
		baseActionList = new ArrayList<ActionBean>();
		sceneActionList = new ArrayList<ActionBean>();
    	LayoutInflater inflater = LayoutInflater.from(mContext);
    	bottomActionView = inflater.inflate(R.layout.layout_action_popwindow, null);
    	
    	getDbData();
    	initView();
    	setAdapter();
    	initEvents();
    	
    	popupWindow = new PopupWindow(bottomActionView,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,true);
        popupWindow.setAnimationStyle(R.style.popwin_anim_style);
        ColorDrawable dw = new ColorDrawable(mContext.getResources().getColor(R.color.color_33333333));
        popupWindow.setBackgroundDrawable(dw);
        bottomActionView.setOnTouchListener(this);
    }
    
    /**
     * 初始化视图
     */
    private void initView(){
    	actionRG = (RadioGroup) bottomActionView.findViewById(R.id.tabRG);
    	gridView = (GridView) bottomActionView.findViewById(R.id.gridView);
    	cancelBtn = (ImageView) bottomActionView.findViewById(R.id.cancel_btn);
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
	 * 动作菜单切换点击事件
	 */
	private void initEvents(){
		
		actionRG.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
				setAdapter();
			}});
		
		gridView.setOnItemClickListener(clickListener);
		
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
		int height = bottomActionView.findViewById(R.id.action_pop_layout).getTop();
        int y=(int) event.getY();
        if(event.getAction()==MotionEvent.ACTION_UP){
            if(y<height){
            	popupWindow. dismiss();
            }
        }
        return true;
	}

	public ActionBean getCheckedRadio(int position){
		List<ActionBean> list = null;
		
		if(actionRG.getCheckedRadioButtonId() == R.id.baseActionRG){
			list = baseActionList;
		}
		else if(actionRG.getCheckedRadioButtonId() == R.id.sceneActionRG){
			list = sceneActionList;
		}
		
		if(list.get(position) == null)
			return null;
		else
			return ActionForM2DBUtils.searchDBIdActionData(db,list.get(position).getDBId(),mContext);/*list.get(position)*/
	}
	
	public void dismiss(){
		popupWindow.dismiss();
	}
}
