package com.releasy.android.activity.music;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.releasy.android.R;
import com.releasy.android.ReleasyApplication;
import com.releasy.android.activity.main.BaseFragment;
import com.releasy.android.adapter.WebMusicAdapter;
import com.releasy.android.bean.MusicBean;
import com.releasy.android.constants.HttpConstants;
import com.releasy.android.db.MusicDBUtils;
import com.releasy.android.db.ReleasyDatabaseHelper;
import com.releasy.android.download.DownloadTask;
import com.releasy.android.http.HttpUtils;
import com.releasy.android.http.ResolveJsonUtils;
import com.releasy.android.service.MusicService;


public class WebMusicFragment extends BaseFragment{
	
    private View view;                        //主视图
	private ListView listView;                //音乐列表
	private LinearLayout listFooterLayout;    //listview footer 布局
	private LinearLayout footerLoadingLayout; //加载更多Layout
	private LinearLayout footerProgressLayout;//加载时Layout
	private TextView footerLoadingTxt;        //加载Layout Txt
	private TextView footerNotDataTxt;        //没有更多数据Layout Txt
	private WebMusicAdapter adapter;          //列表Adapter
	private int roomId;                       //放松馆Id
	private ReleasyDatabaseHelper db;         //数据库
	private List<MusicBean> musicList;        //音乐数据列表
    private MusicRoomActivity activity;       //绑定的Activity
    private ReleasyApplication app;           //Application
	private MusicService musicService;        //音乐service
    
    
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLogD("WebMusicFragment onCreate");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	showLogD("WebMusicFragment onCreateView");
        
        if (view == null || view.getParent() != null) {
    		//判定视图是否存在   不重复加载
    		view = inflater.inflate(R.layout.fragment_web_music, container, false);
		}
    	init(); //初始化
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showLogD("WebMusicFragment onActivityCreated");
    }

    public void onResume() {
        super.onResume();
        showLogD("WebMusicFragment onResume");
        getActivity().registerReceiver(broadcastReceiver, intentFilter()); //绑定广播
        if(activity.getChangeDel()){
        	getUpdataList();
        	activity.setChangeDel(false);
        }
        
    }

    public void onPause() {
        super.onPause();
        showLogD("WebMusicFragment onPause");
    }

    public void onDestroyView() {
        super.onDestroyView();
        showLogD("WebMusicFragment onDestroyView");
    }

    public void onDestroy() {
        super.onDestroy();
        showLogD("WebMusicFragment onDestroy");
        getActivity().unregisterReceiver(broadcastReceiver); //解绑广播
    }
	
    /**
     * 初始化
     */
    private void init(){
    	db = MusicDBUtils.openData(getActivity());
    	app = (ReleasyApplication) getActivity().getApplication(); //获取Application
    	musicService = app.getMusicService();        //获取音乐Service
    	activity = (MusicRoomActivity)getActivity();
    	roomId = activity.getRoomId();
    	musicList = new ArrayList<MusicBean>();

    	initViews();   //初始化视图
    	initEvents();  //初始化点击事件
    	adapter = new WebMusicAdapter(getActivity(),musicList,roomId);
		listView.setAdapter(adapter);
    	putAsyncTask(new GetMusicAsyncTask());
    }
    
    /**
     * 初始化视图
     */
	protected void initViews() {
		listView = (ListView) view.findViewById(R.id.listView);
		listFooterLayout = (LinearLayout)LayoutInflater.from(this.getActivity())
		                   .inflate(R.layout.layout_web_music_list_footer, null);
		listView.addFooterView(listFooterLayout);
		adapter = new WebMusicAdapter(this.getActivity(),musicList,roomId);
		listView.setAdapter(adapter);
		
		footerLoadingLayout = (LinearLayout) listFooterLayout.findViewById(R.id.footerLoadingLayout);
		footerProgressLayout = (LinearLayout) listFooterLayout.findViewById(R.id.footerProgressLayout);
		footerLoadingTxt = (TextView) listFooterLayout.findViewById(R.id.footerLoadingTxt);
		footerNotDataTxt = (TextView) listFooterLayout.findViewById(R.id.footerNotDataTxt);
	}

	/**
     * 初始化点击事件
     */
	protected void initEvents() {
		footerLoadingTxt.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				if(total != -1 && offset >= total){
					Toast.makeText(WebMusicFragment.this.getActivity(), R.string.music_not_data
							, Toast.LENGTH_LONG).show();
					return;
				}
				
				footerProgressLayout.setVisibility(View.VISIBLE);
				footerLoadingTxt.setVisibility(View.GONE);
				footerNotDataTxt.setVisibility(View.GONE);
				putAsyncTask(new GetMusicAsyncTask());
			}});
	}
	
	private void getUpdataList(){
		//musicList.clear();
		boolean isChange = false;
		
		for(int i = 0; i < musicList.size(); i++){
			if(musicList.get(i).getDownloadStatus() == 2){				
				if(!MusicDBUtils.isMusicExist(db, musicList.get(i).getMusicId())){
					musicList.get(i).setDownloadStatus(0);
					isChange = true;
				}
			}
		}
		
		if(isChange){
			adapter = new WebMusicAdapter(getActivity(),musicList,roomId);
			listView.setAdapter(adapter);
		}
	}
	
	/**
	 * 广播Action
	 */
	private static IntentFilter intentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DownloadTask.UPDATA_WEB_MUSIC_ADAPTER_UI);  //音乐更新
		return intentFilter;
	}
	
	/**
	 * 广播注册
	 */
	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (DownloadTask.UPDATA_WEB_MUSIC_ADAPTER_UI.equals(action)) {
				Bundle bundle = intent.getExtras();
				int musicId = bundle.getInt("musicId");
				int result = bundle.getInt("result");
				
				for(int i = 0; i < musicList.size(); i++){
					if(musicList.get(i).getMusicId() == musicId){
						musicList.get(i).setProgress(result);
						if(result == 100){
							musicList.get(i).setDownloadStatus(2);
							//String fileSavePath = bundle.getString("fileSavePath");
							/*MusicDBUtils.insertData(db, roomId, musicId, musicList.get(i).getName()
									, musicList.get(i).getArtPath(), fileSavePath, musicList.get(i).getArtist());*/
							
							MusicDBUtils.searchAllData(db);
							activity.setChangeDownload(true);
							activity.setHaveDownload();          //执行过下载操作
							
							//当前音乐页面属于正在按摩的放松馆的音乐页面
				        	/*if(app.getIsWorking() && roomId == app.getRoomId()){
				        		musicService.refreshMusicList(); //刷新后台音乐Service的音乐列表
				        	}*/
						}
						else{
							musicList.get(i).setDownloadStatus(1);
						}
						adapter.notifyDataSetChanged();
					}
				}
			}
		}
	};
	
	
	/***************************************网络通信*****************************************/
	private int size = 20;
	private int total = -1; 
	private int offset = 0; 
	
	/**
	 * 请求参数封装
	 */
	private List<NameValuePair> getParams(){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("roomid", roomId + ""));
		params.add(new BasicNameValuePair("offset", offset + ""));
		params.add(new BasicNameValuePair("size", size + ""));
		return params;
	}
	
	/**
	 * 获取我的作品线程处理
	 */
	private class GetMusicAsyncTask extends AsyncTask<Void, Void, Boolean>{
		private Bundle mResult;
		private Bundle headBundle;
		private int retStatus;
		private String retMsg ;
		
		protected Boolean doInBackground(Void... param) {
			List<NameValuePair> params = getParams();
			mResult = HttpUtils.doPost(params,HttpConstants.GET_MUSIC_LIST
					,WebMusicFragment.this.getActivity(),mScreenWidth,mScreenHeight);
			if(mResult.getInt("code") != HttpConstants.SUCCESS){
				retStatus = mResult.getInt("code");
				retMsg = getResources().getString(R.string.network_anomaly);
				return null;
			}
			
			headBundle = ResolveJsonUtils.getJsonHead(mResult.getString("content"));
			retStatus = headBundle.getInt("retStatus");
			retMsg = headBundle.getString("retMsg");
			showLogD("retStatus : " + retStatus + "    retMsg : " + retMsg);
			
			if(retStatus == HttpConstants.SUCCESS){
				Bundle bundle = ResolveJsonUtils.getMusicList(mResult.getString("content"));
				total = bundle.getInt("total");
				List<MusicBean> dataArrays = (List<MusicBean>) bundle.getSerializable("dataList");
				
				for(int i = 0; i < dataArrays.size(); i++){
					if(MusicDBUtils.isMusicExist(db, dataArrays.get(i).getMusicId())){
						//showLogD("Id : " + dataArrays.get(i).getMusicId());
						dataArrays.get(i).setDownloadStatus(2);
					}
				}
				
				if(dataArrays != null){
					musicList.addAll(dataArrays);
				}
				
				showLogD("total : " + total);
			}
			
			return null;
		}
		
		protected void onPostExecute(Boolean result) {  
			if(getActivity().isFinishing())
				return;
			
			if(retStatus == HttpConstants.SUCCESS){
				offset = offset + size;
				//adapter = new WebMusicAdapter(getActivity(),musicList,roomId);
				//listView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				
				if(offset >= total){
					footerProgressLayout.setVisibility(View.GONE);
					footerLoadingTxt.setVisibility(View.GONE);
					footerNotDataTxt.setVisibility(View.VISIBLE);
				}
				else{
					footerProgressLayout.setVisibility(View.GONE);
					footerLoadingTxt.setVisibility(View.VISIBLE);
					footerNotDataTxt.setVisibility(View.GONE);
				}
			}
			else{
				footerProgressLayout.setVisibility(View.GONE);
				footerLoadingTxt.setVisibility(View.VISIBLE);
				footerNotDataTxt.setVisibility(View.GONE);
				Toast.makeText(WebMusicFragment.this.getActivity(), R.string.loading_failure
						, Toast.LENGTH_LONG).show();
			}
		}
	}
}
