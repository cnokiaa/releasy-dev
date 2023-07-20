package com.releasy.android.activity.more;

import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.releasy.android.R;
import com.releasy.android.ReleasyApplication;
import com.releasy.android.activity.main.BaseActivity;
import com.releasy.android.adapter.UserRecordAdapter;
import com.releasy.android.bean.RunTimeBean;
import com.releasy.android.bean.UserRecordBean;
import com.releasy.android.db.ReleasyDatabaseHelper;
import com.releasy.android.db.UserRecordDBUtils;
import com.releasy.android.view.TopNavLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class UserRecordActivity extends BaseActivity{
	
	private ReleasyApplication app;
	private ReleasyDatabaseHelper db;
	
	private TopNavLayout mTopNavLayout;           //导航菜单栏
	private LinearLayout userRecordLayout;
	private TextView noUserRecordTxt;
	private LineChart mLineChart;  
	private TextView useDetailsTxt;
	private ListView actionListView;
	private UserRecordAdapter actionAdapter;
	
	private List<UserRecordBean> userRecordList;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_user_record);
        
        init();            //初始化
         
		
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		db = UserRecordDBUtils.openData(this);
		app = (ReleasyApplication) this.getApplication();
		userRecordList = new ArrayList<UserRecordBean>();
		
		getData();      //获取数据
		initViews();    //初始化视图
		setTopNav();    //初始化导航栏
		initEvents();   //初始化点击事件
	}
	
	/**
	 * 初始化导航栏
	 */
	private void setTopNav(){
		mTopNavLayout.setTitltTxt(R.string.user_record);
		mTopNavLayout.setLeftImgSrc(R.drawable.ic_nav_bar_back);
	}
	
	/**
	 * 获取数据    
	 */
	private void getData(){
		userRecordList = UserRecordDBUtils.searchAllData(db, this);
		
		UserRecordBean bean = app.getUserRecord();
		if(bean.getTotalRunTime() > 0){
			if(userRecordList.size() > 0){
				for(int i = 0; i < userRecordList.size(); i++){
					if(userRecordList.get(i).getDate().equals(bean.getDate())){
						int time = userRecordList.get(i).getTotalRunTime();
						userRecordList.get(i).setTotalRunTime(time + bean.getTotalRunTime());
						
						List<RunTimeBean> list = bean.getActinRunTimeList();
						for(int k = 0; k < list.size(); k++){
							if(list.get(k).getActionId() == userRecordList.get(i).getActinRunTimeList().get(k).getActionId()){
								int actionTime = list.get(k).getRunTime() + userRecordList.get(i).getActinRunTimeList().get(k).getRunTime();
								userRecordList.get(i).getActinRunTimeList().get(k).setRunTime(actionTime);
							}
						}
					}
				}
			}
			else{
				userRecordList.add(bean);
			}
			
		}
		
	}
	
	/**  
     * 生成一个数据  
     * @param count 表示图表中有多少个坐标点  
     * @param range 用来生成range以内的随机数  
     * @return  
     */    
    private LineData getLineData() {    
        ArrayList<String> xValues = new ArrayList<String>();    
        for (int i = 0; i < userRecordList.size(); i++) {    
            // x轴显示的数据，这里默认使用数字下标显示   
        	Log.d("z17m","i : " + i);
        	String[]arr = userRecordList.get(i).getDate().split("-");
            xValues.add(arr[1]+"-"+arr[2]);    
        }    
    
        // y轴的数据    
        ArrayList<Entry> yValues = new ArrayList<Entry>();    
        for (int i = 0; i < userRecordList.size(); i++) {        
            yValues.add(new Entry((int)(userRecordList.get(i).getTotalRunTime()/60), i));    
        }    
    
        // create a dataset and give it a type    
        // y轴的数据集合    
        LineDataSet lineDataSet = new LineDataSet(yValues, null);    
        // mLineDataSet.setFillAlpha(110);    
        // mLineDataSet.setFillColor(Color.RED);    
    
        //用y轴的集合来设置参数    
        int color = this.getResources().getColor(R.color.color_txt_7FAFC8);
        //lineDataSet.setDrawCubic(true);
        lineDataSet.setLineWidth(1.75f); // 线宽    
        lineDataSet.setCircleSize(3f);// 显示的圆形大小    
        lineDataSet.setColor(color);// 显示颜色    
        lineDataSet.setCircleColor(color);// 圆形的颜色    
        lineDataSet.setHighLightColor(color); // 高亮的线的颜色   
    
        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();    
        lineDataSets.add(lineDataSet); // add the datasets    
    
        // create a data object with the datasets    
        LineData lineData = new LineData(xValues, lineDataSets);    
    
        return lineData;    
    }    
    
    /**
	 * 设置显示的样式    
	 */
    private void showChart(LineChart lineChart, LineData lineData, int color) {    
        lineChart.setDrawBorders(false);  //是否在折线图上添加边框    
    
        // no description text    
        lineChart.setDescription("");// 数据描述    
        // 如果没有数据的时候，会显示这个，类似listview的emtpyview    
        lineChart.setNoDataTextDescription("You need to provide data for the chart.");    
            
        // enable / disable grid background    
        lineChart.setDrawGridBackground(false); // 是否显示表格颜色    
        lineChart.setGridBackgroundColor(this.getResources().getColor(R.color.color_txt_7FAFC8) & 0x70FFFFFF); // 表格的的颜色，在这里是是给颜色设置一个透明度    
    
        // enable touch gestures    
        lineChart.setTouchEnabled(true); // 设置是否可以触摸    
    
        // enable scaling and dragging    
        lineChart.setDragEnabled(true);// 是否可以拖拽    
        //lineChart.setScaleEnabled(true);// 是否可以缩放    
        lineChart.setScaleXEnabled(true);
        lineChart.setScaleYEnabled(false);
        lineChart.setScaleMinima(1, 1);

        // if disabled, scaling can be done on x- and y-axis separately    
        lineChart.setPinchZoom(false);//     
    
        lineChart.setBackgroundColor(color);// 设置背景    
    
        // add data    
        lineChart.setData(lineData); // 设置数据    
    
        // get the legend (only possible after setting data)    
        Legend mLegend = lineChart.getLegend(); // 设置比例图标示，就是那个一组y的value的    
    
        // modify the legend ...   
        mLegend.setEnabled(false);


        XAxis xl = lineChart.getXAxis();  
        xl.setPosition(XAxisPosition.BOTTOM); // 设置X轴的数据在底部显示  
        
        
        lineChart.animateX(2500); // 立即执行的动画,x轴    
    }    
	
	protected void initViews() {
		mTopNavLayout = (TopNavLayout) findViewById(R.id.topNavLayout);
		userRecordLayout = (LinearLayout) findViewById(R.id.user_record_layout);
		noUserRecordTxt = (TextView) findViewById(R.id.no_user_record_txt);
		mLineChart = (LineChart) findViewById(R.id.spread_line_chart); 
		useDetailsTxt = (TextView) findViewById(R.id.use_details_txt); 
		actionListView = (ListView) findViewById(R.id.listView);
		
		if(userRecordList.size() > 0)
			showChart(mLineChart, getLineData(), this.getResources().getColor(R.color.transparent));
			
		else{
			
			userRecordLayout.setVisibility(View.GONE);
			noUserRecordTxt.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 初始化点击事件
	 */
	protected void initEvents() {
		//导航栏返回按钮逻辑
		mTopNavLayout.setLeftImgOnClick(new OnClickListener(){
			public void onClick(View arg0) {
				UserRecordActivity.this.finish();
			}});
		
		
		mLineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener(){
			public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
				Log.d("z17m","e.getXIndex() : " + e.getXIndex());
				actionAdapter = new UserRecordAdapter(UserRecordActivity.this,userRecordList.get(e.getXIndex()).getActinRunTimeList());
				actionListView.setAdapter(actionAdapter);
				useDetailsTxt.setVisibility(View.VISIBLE);
				
			}

			public void onNothingSelected() {
					
			}});
		
	}
	
	
	
	
}
