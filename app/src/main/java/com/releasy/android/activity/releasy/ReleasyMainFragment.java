package com.releasy.android.activity.releasy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.emokit.sdk.InitListener;
import com.emokit.sdk.basicinfo.AdvancedInformation;
import com.emokit.sdk.netaccess.NetTransfer;
import com.emokit.sdk.senseface.ExpressionDetect;
import com.emokit.sdk.senseface.ExpressionListener;
import com.emokit.sdk.util.SDKAppInit;
import com.emokit.sdk.util.SDKConstant;
import com.releasy.android.R;
import com.releasy.android.ReleasyApplication;
import com.releasy.android.activity.main.BaseFragment;
import com.releasy.android.activity.main.MainTabActivity;
import com.releasy.android.activity.more.FeedbackActivity;
import com.releasy.android.adapter.FeedbackAdapter;
import com.releasy.android.adapter.ReleasyRoomAdapter;
import com.releasy.android.bean.FeedbackBean;
import com.releasy.android.bean.RoomBean;
import com.releasy.android.constants.Constants;
import com.releasy.android.constants.HttpConstants;
import com.releasy.android.constants.RoomConstants;
import com.releasy.android.db.ActionDBUtils;
import com.releasy.android.db.MusicDBUtils;
import com.releasy.android.db.ReleasyDatabaseHelper;
import com.releasy.android.db.RoomDBUtils;
import com.releasy.android.http.HttpUtils;
import com.releasy.android.http.ResolveJsonUtils;
import com.releasy.android.service.UpdataAppService;
import com.releasy.android.utils.SharePreferenceUtils;
import com.releasy.android.utils.Utils;
import com.releasy.android.view.TopNavLayout;

/**
 * 放松馆主页
 * @author Lighting.Z
 *
 */
public class ReleasyMainFragment extends BaseFragment{

	private final int USER_DEFIND_EDIT = 100;
	private final int USER_DEFIND_ACTION = 101;
	private final int CAMERA = 102;
	private View view;                           //主视图
	private TopNavLayout mTopNavLayout;          //导航菜单栏
	private GridView gridView;                   //宫格视图
	private ReleasyRoomAdapter adapter;          //宫格Adapter
	private List<RoomBean> roomList;             //放松馆列表
	private ReleasyApplication app;              //Application
	private ReleasyDatabaseHelper db;            //数据库
	private SharePreferenceUtils spInfo;         //SharePreference
	private ExpressionDetect expressdetect;      //表情识别
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLogD("ReasyMainFragment onCreate");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	showLogD("ReasyMainFragment onCreateView");
    	
    	if (view == null || view.getParent() != null) {
    		//判定视图是否存在   不重复加载
    		view = inflater.inflate(R.layout.fragment_releasy_main, container, false);
		}
    	init(); //初始化
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showLogD("ReasyMainFragment onActivityCreated");
        
    }

    public void onResume() {
        super.onResume();
        showLogD("ReasyMainFragment onResume");
        initData();    //初始化数据
        setActionEntryBtn(this.getActivity(),app,mTopNavLayout);
    }

    public void onPause() {
        super.onPause();
        showLogD("ReasyMainFragment onPause");
    }

    public void onDestroyView() {
        super.onDestroyView();
        showLogD("ReasyMainFragment onDestroyView");
    }

    public void onDestroy() {
        super.onDestroy();
        showLogD("ReasyMainFragment onDestroy");
    }

    /**
     * 初始化
     */
    private void init(){
    	spInfo = new SharePreferenceUtils(this.getActivity()); //获取SharePreference 存储
    	app = (ReleasyApplication) this.getActivity().getApplication();
    	db = RoomDBUtils.openData(this.getActivity());
    	roomList = new ArrayList<RoomBean>();
    	expressdetect = ExpressionDetect.createRecognizer(this.getActivity(), mInitListener);
    	
    	initViews();   //初始化视图
    	initEvents();  //初始化点击事件
    	setTopNav();   //初始化导航栏
    	
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
				Intent intent = null;
				/*if(position == roomList.size()-1){
					intent = new Intent(ReleasyMainFragment.this.getActivity(),UserDefindEditActivity.class);
					intent.putExtra("type", UserDefindEditActivity.TO_NEW);
					startActivityForResult(intent, USER_DEFIND_EDIT);
					return;
				}*/
				
				switch(roomList.get(position).getRoomType()){
				case Constants.SINGLE_TYPE:
					intent = new Intent(ReleasyMainFragment.this.getActivity(),SingleActionActivity.class);
					intent.putExtra("roomName", roomList.get(position).getRoomName());
					intent.putExtra("roomId", roomList.get(position).getRoomId());
					intent.putExtra("roomType", roomList.get(position).getRoomType());
					startActivity(intent);
					break;
				case Constants.MULTIPLE_TYPE:
					intent = new Intent(ReleasyMainFragment.this.getActivity(),MultipleActionActivity.class);
					intent.putExtra("roomName", roomList.get(position).getRoomName());
					intent.putExtra("roomId", roomList.get(position).getRoomId());
					intent.putExtra("roomType", roomList.get(position).getRoomType());
					startActivity(intent);
					break;
				case Constants.USER_DEFINED_TYPE:
					intent = new Intent(ReleasyMainFragment.this.getActivity(),UserDefindActionActivity.class);
					intent.putExtra("roomName", roomList.get(position).getRoomName());
					intent.putExtra("roomId", roomList.get(position).getRoomId());
					intent.putExtra("roomType", roomList.get(position).getRoomType());
					startActivityForResult(intent, USER_DEFIND_EDIT);
					break;
				case Constants.ACTION_DISTRIBUTION_FOR_M2_TYPE:
					intent = new Intent(ReleasyMainFragment.this.getActivity(),ActionDistributionForM2activity.class);
					intent.putExtra("roomName", roomList.get(position).getRoomName());
					intent.putExtra("roomId", roomList.get(position).getRoomId());
					intent.putExtra("roomType", roomList.get(position).getRoomType());
					startActivity(intent);
					//startActivityForResult(intent, USER_DEFIND_EDIT);
					Log.d("z17m","ACTION_DISTRIBUTION_FOR_M2_TYPE");
					break;
				case Constants.ACTION_COUNTENANCE_TYPE:
					//TODO
					
					if(app.getIsWorking() && app.getRoomType() == Constants.MULTIPLE_TYPE
							&& app.getRoomId() == RoomConstants.ACTION_COUNTENANCE_TYPE){
						intent = new Intent(ReleasyMainFragment.this.getActivity(),MultipleActionActivity.class);
						intent.putExtra("roomName", app.getLastRoomName());
						intent.putExtra("roomId", app.getLastRoomId());
						intent.putExtra("roomType", app.getLastRoomType());
						intent.putExtra("reason", "");
						startActivity(intent);
					}
					else{
						Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					    startActivityForResult(camera, CAMERA);
					}
					
				    
					/*expressdetect.setParameter(SDKConstant.FACING,
							SDKConstant.CAMERA_FACING_FRONT);
					expressdetect.startRateListening(expresslisten);*/
					
					
					//NetTransfer netTransfer = new NetTransfer();
					//String result = netTransfer.recognizeFace("0", picLocalFilePath);

					break;
				}
			}});
		
		/*gridView.setOnItemLongClickListener(new OnItemLongClickListener(){
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
				final int roomId = roomList.get(position).getRoomId();
				final int p = position;
				if(roomId < 0){}
				else if(roomId < 20000){
					String string = ReleasyMainFragment.this.getString(R.string.room_can_not_delete);
					Toast.makeText(ReleasyMainFragment.this.getActivity()
							, roomList.get(position).getRoomName() + string, Toast.LENGTH_LONG).show();
				}
				else{
					String msgStart = ReleasyMainFragment.this.getResources().getString(R.string.del_room_dialog_msg_start);
					String msgEnd = ReleasyMainFragment.this.getResources().getString(R.string.del_room_dialog_msg_end);
					AlertDialog.Builder alert = new AlertDialog.Builder(ReleasyMainFragment.this.getActivity());
					alert.setTitle(R.string.delete)
							.setMessage(msgStart + roomList.get(position).getRoomName() + msgEnd)
							.setPositiveButton(R.string.confirm,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,int which) {
											MusicDBUtils.deleteRoomMuisc(db, roomId);
											ActionDBUtils.deleteRoomActionData(db, roomId);
											RoomDBUtils.deleteData(db, roomId);
											roomList.remove(p);
											adapter.notifyDataSetChanged();
										}
									})
							.setNegativeButton(R.string.cancel,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									});
					alert.create().show();
				}
				showLogD("setOnItemLongClickListener");
				return true;
			}});*/
	}
	
	/**
	 * 初始化导航栏
	 */
	private void setTopNav(){
		mTopNavLayout.setTitltTxt(R.string.relax_shop);
	}
	
	
	/**
	 * 获取放松馆数据
	 */
	private void initData(){
		roomList.clear();
		
		roomList = RoomDBUtils.searchAllData(db,this.getActivity());
		
		
		if(spInfo.getSelectDevice().equals(Constants.SELECT_DEVICE_M2)){
			RoomBean actionDistributionForM2Room = new RoomBean(RoomConstants.ACTION_DISTRIBUTION_FOR_M2
					,this.getString(R.string.free_combination)
					,Constants.ACTION_DISTRIBUTION_FOR_M2_TYPE,"",RoomConstants.ROOM_BELONG_TO_M2); 
			roomList.add(actionDistributionForM2Room);	
			
			/*RoomBean actionCountenanceRoom = new RoomBean(RoomConstants.ACTION_COUNTENANCE_TYPE
					,this.getString(R.string.expression_recognition)
					,Constants.ACTION_COUNTENANCE_TYPE,"",RoomConstants.ROOM_BELONG_TO_CURRENCY); 
			roomList.add(actionCountenanceRoom);*/
		}
		else{
			for(int i = 0; i < roomList.size(); i++){
				if(roomList.get(i).getRoomBelong() == RoomConstants.ROOM_BELONG_TO_M2){
					roomList.remove(i);
					i = i - 1;
				}
			}
		}
		
		//RoomBean addRoom = new RoomBean(ReleasyRoomAdapter.ADD_ROOM,"",0,""); 
		//roomList.add(addRoom);
		
		adapter = new ReleasyRoomAdapter(this.getActivity(),roomList);
		gridView.setAdapter(adapter);
	}
	
	@SuppressLint("SuspiciousIndentation")
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
		showLogD("ReleasyMainFragment onActivityResult");
		if(requestCode == USER_DEFIND_EDIT && resultCode == Activity.RESULT_OK){
			initData();
		}
		else if(requestCode == USER_DEFIND_ACTION && resultCode == Activity.RESULT_OK){
			initData();
		}
		
		else if(requestCode == CAMERA && resultCode == Activity.RESULT_OK && null != intent){
			String sdState = Environment.getExternalStorageState();
			if(!sdState.equals(Environment.MEDIA_MOUNTED)){
				Utils.showLogD("sd card unmount");
			    return;
			}
			String name = "/" + Utils.getTime2()+".jpg";
			Bundle bundle = intent.getExtras();
			//获取相机返回的数据，并转换为图片格式
			Bitmap bitmap = (Bitmap)bundle.get("data");
			FileOutputStream fout = null;
			File file = new File("/sdcard/emokit/");
			file.mkdirs();
			String filename = file.getPath() + name;
			Utils.showLogD("filename : " + filename);
			try {
				fout = new FileOutputStream(filename);
			    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
			    
			    initProgressDialog(ReleasyMainFragment.this.getString(R.string.recognition_of_emotion));
			    progressDialog.show();
			    putAsyncTask(new EmokitAsyncTask(filename));
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			finally{
				try {
					fout.flush();
					fout.close();
			    }
				catch (IOException e) {
					e.printStackTrace();
			    }
			}
		}
	}
	
	/**
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {
		public void onInit(int code) {
			// 获取设备ID
			SDKAppInit.registerforuid("Android","funeral","123456");
		}
	};
	
	/**
	 * 面部表情识别器
	 */
	private ExpressionListener expresslisten = new ExpressionListener() {
		public void endDetect(String result, String picFile) {
			//TODO
			String reason = "";
			try {
				JSONObject json =  new JSONObject(result);
				reason = json.getString("rc_main");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.d("z17m", result + "/n reason : " + reason );
			
			Intent intent = new Intent(ReleasyMainFragment.this.getActivity(),MultipleActionActivity.class);
			intent.putExtra("roomName", ReleasyMainFragment.this.getString(R.string.free_combination));
			intent.putExtra("roomId", RoomConstants.ACTION_COUNTENANCE_TYPE);
			intent.putExtra("roomType", Constants.MULTIPLE_TYPE);
			intent.putExtra("reason", reason);
			startActivity(intent);
		}

		public void beginDetect() {}
	};
	
	private void ResultEmokit(String result){
		String reason = "";
		try {
			JSONObject json =  new JSONObject(result);
			reason = json.getString("rc_main");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d("z17m", result + "/n reason : " + reason );
		
		Intent intent = new Intent(ReleasyMainFragment.this.getActivity(),MultipleActionActivity.class);
		intent.putExtra("roomName", ReleasyMainFragment.this.getString(R.string.expression_recognition));
		intent.putExtra("roomId", RoomConstants.ACTION_COUNTENANCE_TYPE);
		intent.putExtra("roomType", Constants.MULTIPLE_TYPE);
		intent.putExtra("reason", reason);
		startActivity(intent);
	}
	
	private class EmokitAsyncTask extends AsyncTask<Void, Void, Boolean>{
		
		private String fileName;
		private String resultMsg;
		
		public EmokitAsyncTask(String fileName){
			this.fileName = fileName;
		}
		
		protected Boolean doInBackground(Void... param) {
			NetTransfer netTransfer = new NetTransfer();
			resultMsg = netTransfer.recognizeFace("0", fileName);
			return null;
		}
		
		protected void onPostExecute(Boolean result) { 
			if(progressDialog.isShowing()){
				progressDialog.dismiss();
				ResultEmokit(resultMsg);
			}
		}
	}
}
