package com.releasy.android.activity.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.releasy.android.R;
import com.releasy.android.ReleasyApplication;
import com.releasy.android.activity.releasy.ActionDistributionForM2activity;
import com.releasy.android.activity.releasy.MultipleActionActivity;
import com.releasy.android.activity.releasy.ReleasyMainFragment;
import com.releasy.android.activity.releasy.SingleActionActivity;
import com.releasy.android.activity.releasy.UserDefindActionActivity;
import com.releasy.android.constants.Constants;
import com.releasy.android.constants.RoomConstants;
import com.releasy.android.view.TopNavLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class BaseFragment extends Fragment {
	
	protected Activity mActivity;
	protected Context mContext;
	protected View mView;
	
	/**
	 * 屏幕的宽度、高度、密度
	 */
	protected int mScreenWidth = 0;
	protected int mScreenHeight = 0;
	protected float mDensity;
	protected ProgressDialog progressDialog;       //数据设置dialog
	
	protected List<AsyncTask<Void, Void, Boolean>> mAsyncTasks = new ArrayList<AsyncTask<Void, Void, Boolean>>();
	protected AnimationDrawable workingAnim;
	
	public BaseFragment() {
		super();
	}

	public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics metric = new DisplayMetrics();
		Objects.requireNonNull(this.getActivity()).getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		mDensity = metric.density;
	}
	/*public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initViews();
		initEvents();
		return mView;
	}*/

	@Override
	public void onDestroy() {
		clearAsyncTask();
		super.onDestroy();
	}

	protected abstract void initViews();

	protected abstract void initEvents();

	public View findViewById(int id) {
		return mView.findViewById(id);
	}
	
	protected void putAsyncTask(AsyncTask<Void, Void, Boolean> asyncTask) {
		mAsyncTasks.add(asyncTask.execute());
	}

	protected void clearAsyncTask() {
		Iterator<AsyncTask<Void, Void, Boolean>> iterator = mAsyncTasks
				.iterator();
		while (iterator.hasNext()) {
			AsyncTask<Void, Void, Boolean> asyncTask = iterator.next();
			if (asyncTask != null && !asyncTask.isCancelled()) {
				asyncTask.cancel(true);
			}
		}
		mAsyncTasks.clear();
	}

	/**
	 * 初始化加载Dailog
	 */
	protected void initProgressDialog(String string){
		progressDialog = new ProgressDialog(this.getActivity());
		progressDialog.setMessage(string);
		progressDialog.setCanceledOnTouchOutside(false);
	} 
	
	/** 通过Class跳转界面 **/
	protected void startActivity(Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(mContext, cls);
		startActivity(intent);
	}
	
	/** 含有标题、内容、两个按钮的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message,
			String positiveText,
			DialogInterface.OnClickListener onPositiveClickListener,
			String negativeText,
			DialogInterface.OnClickListener onNegativeClickListener) {
		AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setTitle(title)
				.setMessage(message)
				.setPositiveButton(positiveText, onPositiveClickListener)
				.setNegativeButton(negativeText, onNegativeClickListener)
				.show();
		return alertDialog;
	}
	
	protected void showLogD(String log){
		Log.d("z17m", log);
	}
	
	/**
	 * 如果在按摩状态设置右上角快捷进入按钮
	 */
	protected void setActionEntryBtn(final Context context,final ReleasyApplication app
			,TopNavLayout mTopNavLayout){
		if(app.getLastRoomId() <= -100){
			mTopNavLayout.setRightImgGone();
			return;
		}
		
		if(app.getIsWorking()){
			mTopNavLayout.setRightImgSrc(R.drawable.ic_working);
			workingAnim = (AnimationDrawable) mTopNavLayout.getRightImg().getDrawable();  
			workingAnim.start();
		}
		else{
			mTopNavLayout.setRightImgSrc(R.drawable.ic_action_entry);
			if(workingAnim != null && workingAnim.isRunning())
				workingAnim.stop();
		}
		
		mTopNavLayout.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				Intent intent = null;
				switch(app.getLastRoomType()){
				case Constants.MULTIPLE_TYPE:
					intent = new Intent(context,MultipleActionActivity.class);
					break;
				case Constants.SINGLE_TYPE:
					intent = new Intent(context,SingleActionActivity.class);
					break;
				case Constants.USER_DEFINED_TYPE:
					intent = new Intent(context,UserDefindActionActivity.class);
					break;
				case Constants.ACTION_DISTRIBUTION_FOR_M2_TYPE:
					intent = new Intent(context,ActionDistributionForM2activity.class);
					break;
				}
				
				intent.putExtra("roomName", app.getLastRoomName());
				intent.putExtra("roomId", app.getLastRoomId());
				intent.putExtra("roomType", app.getLastRoomType());
				
				if(app.getLastRoomId() == RoomConstants.ACTION_COUNTENANCE_TYPE)
					intent.putExtra("reason", "");
				
				startActivity(intent);
			}});
	}
}
