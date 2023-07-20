package com.releasy.android.activity.music;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

import com.releasy.android.R;
import com.releasy.android.ReleasyApplication;
import com.releasy.android.activity.main.BaseFragment;
import com.releasy.android.activity.releasy.ReleasyMainFragment;
import com.releasy.android.activity.releasy.SelectPicActivity;
import com.releasy.android.adapter.ReleasyRoomAdapter;
import com.releasy.android.bean.RoomBean;
import com.releasy.android.db.ReleasyDatabaseHelper;
import com.releasy.android.db.RoomDBUtils;
import com.releasy.android.view.TopNavLayout;

/**
 * 音乐主页面
 * @author Lighting.Z
 *
 */
public class MusicMainFragment extends BaseFragment{
	
	private View view;                           //主视图
	private TopNavLayout mTopNavLayout;          //导航菜单栏
	private GridView gridView;                   //宫格视图
	private ReleasyRoomAdapter adapter;          //宫格Adapter
	private List<RoomBean> roomList;             //放松馆列表
	private ReleasyApplication app;              //Application
	private ReleasyDatabaseHelper db;            //数据库
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLogD("MusicMainFragment onCreate");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	showLogD("MusicMainFragment onCreateView");
        
        if (view == null || view.getParent() != null) {
    		//判定视图是否存在   不重复加载
    		view = inflater.inflate(R.layout.fragment_music_main, container, false);
		}
    	init(); //初始化
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showLogD("MusicMainFragment onActivityCreated");
    }

    public void onResume() {
        super.onResume();
        showLogD("MusicMainFragment onResume");
        setActionEntryBtn(this.getActivity(),app,mTopNavLayout);
    }

    public void onPause() {
        super.onPause();
        showLogD("MusicMainFragment onPause");
    }

    public void onDestroyView() {
        super.onDestroyView();
        showLogD("MusicMainFragment onDestroyView");
    }

    public void onDestroy() {
        super.onDestroy();
        showLogD("MusicMainFragment onDestroy");
    }

    /**
     * 初始化
     */
    private void init(){
    	app = (ReleasyApplication) this.getActivity().getApplication();
    	db = RoomDBUtils.openData(this.getActivity());
    	roomList = new ArrayList<RoomBean>();
    	
    	initViews();   //初始化视图
    	initEvents();  //初始化点击事件
    	setTopNav();   //初始化导航栏
    	initData();    //初始化数据
    }
    
    /**
     * 初始化视图
     */
	protected void initViews() {
		mTopNavLayout = (TopNavLayout) view.findViewById(R.id.topNavLayout);
		gridView = (GridView) view.findViewById(R.id.gridView);
	}

	/**
	 * 初始化事件
	 */
	protected void initEvents() {
		gridView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				Intent intent = new Intent(MusicMainFragment.this.getActivity(),MusicRoomActivity.class);
				intent.putExtra("roomId", roomList.get(position).getRoomId());
				startActivity(intent);
			}});
	}
	
	/**
	 * 初始化导航栏
	 */
	private void setTopNav(){
		mTopNavLayout.setTitltTxt(R.string.music_title);
	}
	
	/**
	 * 获取放松馆数据
	 */
	private void initData(){
		roomList = RoomDBUtils.searchAuthorityData(db);
		for(int i = 0; i < roomList.size(); i++){
			String name = "♫ " + roomList.get(i).getRoomName(); 
			roomList.get(i).setRoomName(name);
		}
		
		adapter = new ReleasyRoomAdapter(this.getActivity(),roomList);
		gridView.setAdapter(adapter);
	}
}
