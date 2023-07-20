package com.releasy.android.constants;

public class HttpConstants {

	/**
	 * 常量
	 */
	public static final int SUCCESS = 1;
	public static final int FAILURE = 0;
	public static final int SERVER_ERROR = -1;
	public static final int NETWORK_ERROR = -2;
	
	/**
	 * 地址URL
	 */
    //public static final String DEFAULT_URL = "http://192.168.199.106:8080/eker/client/";
    public static final String DEFAULT_URL = "http://client.wangyoucaokeji.com/eker/client/";
    //public static final String DEFAULT_URL = "http://10.0.0.22:8080/eker/client/";
    
    //版本检测
    public static final String CHECK_APP_VERSION = DEFAULT_URL + "common/check_appVersion"; 
    
    //注册
    public static final String REGIST = DEFAULT_URL + "user/regist"; 
    
    //注册
    public static final String REGIST_USERINFO = DEFAULT_URL + "user/regist_userInfo"; 
    
    //更新
    public static final String UPDATE_USERINFO = DEFAULT_URL + "user/update_userInfo"; 
    
    //改绑手机
    public static final String CHANGE_PHONE = DEFAULT_URL + "user/change_phone"; 
    
    //获取放松馆版本号
    public static final String GET_RELEASY_VERSION = DEFAULT_URL + "room/get_relaxRoom_versionInfo"; 
    
    //获取放松馆列表
    public static final String GET_RELAX_ROOM_LIST = DEFAULT_URL + "room/get_relaxRoomList"; 
    
    //获取音乐列表
    public static final String GET_MUSIC_LIST = DEFAULT_URL + "music/get_musicList"; 
    
    //获取反馈通知
    public static final String GET_SUGGESTION_NOTIFY = DEFAULT_URL + "suggestion/get_suggestionNotify"; 
    
    //获取会话列表
    public static final String GET_SESSION_LIST = DEFAULT_URL + "suggestion/get_sessionList"; 
    
    //提交反馈
    public static final String SEED_SUGGESTION = DEFAULT_URL + "suggestion/send_suggestion"; 
    
    //操作统计
    public static final String ADD_OPERATION_LOG = DEFAULT_URL + "statistics/add_operationLog"; 
    
    //检测设备ID
    public static final String CHECK_DEVICE_ID = DEFAULT_URL + "device/check_deviceValidity"; 
}
