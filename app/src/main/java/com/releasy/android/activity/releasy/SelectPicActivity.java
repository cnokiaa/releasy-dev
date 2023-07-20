package com.releasy.android.activity.releasy;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import com.releasy.android.R;
import com.releasy.android.activity.main.BaseActivity;
import com.releasy.android.adapter.SelectPicAdapter;
import com.releasy.android.bean.PicBean;
import com.releasy.android.utils.StringUtils;
import com.releasy.android.view.TopNavLayout;

/**
 * 自定义按摩动作中 选择封面的页面
 * @author Lighting.Z
 *
 */
public class SelectPicActivity extends BaseActivity{

	private TopNavLayout mTopNavLayout;           //导航菜单栏
	private List<PicBean> picList;             //图片地址
	private GridView gridView;
	private SelectPicAdapter adapter;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_select_pic);
	        
	    init();    //初始化
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		picList = new ArrayList<PicBean>();
		
		initViews();    //初始化控件
		initEvents();   //初始化点击事件
		setTopNav();    //初始化导航栏
		
		putAsyncTask(new GetFileListAsyncTask());
	} 
	
	/**
	 * 初始化导航栏
	 */
	private void setTopNav(){
		mTopNavLayout.setTitltTxt(R.string.select_pic);
		mTopNavLayout.setLeftImgSrc(R.drawable.ic_nav_bar_back);
		mTopNavLayout.setRightTxt(R.string.confirm);
	}
	
	/**
	 * 初始化控件
	 */
	protected void initViews() {
		mTopNavLayout = (TopNavLayout) findViewById(R.id.topNavLayout);
		gridView = (GridView) findViewById(R.id.gridView);
	}

	/**
	 * 初始化点击事件
	 */
	protected void initEvents() {
		//导航栏返回按钮逻辑
		mTopNavLayout.setLeftImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				SelectPicActivity.this.finish();
			}});
		
		//导航栏确定按钮逻辑
		mTopNavLayout.setRightTxtOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				String path = null;
				for(int i = 0; i <  picList.size(); i++){
					if(picList.get(i).getIsChoose())
						path = picList.get(i).getPicPath();
				}
				
				if(StringUtils.isBlank(path)){
					Toast.makeText(SelectPicActivity.this, R.string.no_select_pic_toast, Toast.LENGTH_LONG).show();
				}
				else{
					Intent data=new Intent();  
		            data.putExtra("picPath", path);  
		            setResult(Activity.RESULT_OK, data);  
		            finish();  
				}
			}});
		
		//图片宫格点击逻辑
		gridView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				for(int i = 0; i < picList.size(); i++){
					picList.get(i).setIsChoose(false);
				}
				picList.get(position).setIsChoose(true);
				adapter.notifyDataSetChanged();
			}});
	}
	
	/**
	 * 获取SD卡图片文件列表
	 */
	private void getPictureFile(){
		ContentResolver cr = this.getContentResolver();
		String[] projectionMedia = {Media.DATA};
    	Cursor cursorMedia = cr.query(Media.EXTERNAL_CONTENT_URI, projectionMedia,
    			null, null, Media.TITLE + " asc");
    	if(cursorMedia.moveToFirst()){
    		do{
    			String path = cursorMedia.getString(cursorMedia.getColumnIndex(Media.DATA));
        		
        		//showLogD("path : " + path) ;
    			PicBean bean = new PicBean(path,false);
        		picList.add(bean);
    		}
    		while (cursorMedia.moveToNext());
    	}
    	
    	cursorMedia.close();
    	
    	
	}

	/**
	 * 获取SD卡图片文件列表线程
	 */
	private class GetFileListAsyncTask extends AsyncTask<Void, Void, Boolean>{
		
		protected Boolean doInBackground(Void... param) {
			getPictureFile();
			return null;
		}
		
		protected void onPostExecute(Boolean result) {
			adapter = new SelectPicAdapter(SelectPicActivity.this,picList);
	    	gridView.setAdapter(adapter);
		}
	}
}
