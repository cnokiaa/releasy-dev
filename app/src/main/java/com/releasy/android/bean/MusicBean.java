package com.releasy.android.bean;

/**
 * 音乐基类
 * @author Lighting.Z
 *
 */
public class MusicBean {

	private int musicId;               //音乐Id
	private int roomId;                //馆Id
	private String name;               //音乐名称
	private String artist;             //音乐作者
	private String artPath;            //封面地址
	private String artUrl;             //封面URL
	private String filePath;           //文件地址
	private String fileUrl;            //文件URL
	private boolean isChoose = false;  //选择标识
	private boolean isAssets = false;  //是否资源音乐文件
	private int downloadStatus = 0;    //下载状态   0未下载   1正在下载  2已下载
	private int progress = 0;          //下载进度
 	
	/**
	 * 设置音乐Id
	 */
	public void setMusicId(int musicId){
		this.musicId = musicId;
	}
	/**
	 * 获取音乐Id
	 */
	public int getMusicId(){
		return musicId;
	}
	
	/**
	 * 设置馆Id
	 */
	public void setRoomId(int roomId){
		this.roomId = roomId;
	}
	/**
	 * 获取馆Id
	 */
	public int getRoomId(){
		return roomId;
	}

	/**
	 * 设置音乐名称
	 */
	public void setName(String name){
		this.name = name;
	}
	/**
	 * 获取音乐名称
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * 设置音乐作者
	 */
	public void setArtist(String artist){
		this.artist = artist;
	}
	/**
	 * 获取音乐作者
	 */
	public String getArtist(){
		return artist;
	}
	
	/**
	 * 设置音乐封面
	 */
	public void setArtPath(String artPath){
		this.artPath = artPath;
	}
	/**
	 * 获取音乐封面
	 */
	public String getArtPath(){
		return artPath;
	}
	
	/**
	 * 设置音乐Url
	 */
	public void setArtUrl(String artUrl){
		this.artUrl = artUrl;
	}
	/**
	 * 获取音乐Url
	 */
	public String getArtUrl(){
		return artUrl;
	}
	
	/**
	 * 设置音乐文件地址
	 */
	public void setFilePath(String filePath){
		this.filePath = filePath;
	}
	/**
	 * 获取音乐文件地址
	 */
	public String getFilePath(){
		return filePath;
	}
	
	/**
	 * 设置音乐文件Url
	 */
	public void setFileUrl(String fileUrl){
		this.fileUrl = fileUrl;
	}
	/**
	 * 获取音乐文件Url
	 */
	public String getFileUrl(){
		return fileUrl;
	}
	
	//设置选择标识
	public void setIsChoose(boolean isChoose){
		this.isChoose = isChoose;
	}
	//获取选择标识
	public boolean getIsChoose(){
		return isChoose;
	}
	
	//设置是否资源音乐文件标识
	public void setIsAssets(boolean isAssets){
		this.isAssets = isAssets;
	}
	//获取是否资源音乐文件标识
	public boolean getIsAssets(){
		return isAssets;
	}
	
	//设置文件是否已经下载标识
	public void setDownloadStatus(int downloadStatus){
		this.downloadStatus = downloadStatus;
	}
	//获取文件是否已经下载标识
	public int getDownloadStatus(){
		return downloadStatus;
	}
	
	//设置文件是否已经下载标识
	public void setProgress(int progress){
		this.progress = progress;
	}
	//获取文件是否已经下载标识
	public int getProgress(){
		return progress;
	}
	
	public MusicBean(){}
	
	public MusicBean(String name, String artist, String artPath, String filePath){
		this.name = name;
		this.artist = artist;
		this.artPath = artPath;
		this.filePath = filePath;
	}
	
	public MusicBean(int roomId, int musicId, String name, String artPath, String filePath, String artist){
		this.roomId = roomId;
		this.musicId = musicId;
		this.name = name;
		this.artist = artist;
		this.artPath = artPath;
		this.filePath = filePath;
	}
	
	public MusicBean(int musicId, String name, String artist, String artPath, String fileUrl){
		this.musicId = musicId;
		this.name = name;
		this.artist = artist;
		this.artPath = artPath;
		this.fileUrl = fileUrl;
	}
}
