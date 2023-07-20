package com.releasy.android.http;

import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import com.releasy.android.constants.HttpConstants;
import com.releasy.android.utils.Utils;

import android.content.Context;
import android.os.Bundle;



public class HttpUtils {

	public static Bundle doPost (List<NameValuePair> params,String requestUrl) {
		
		Bundle bundle = new Bundle();
		
		// 定义待请求的URL
		// 创建HttpClient实例
		HttpClient client = new DefaultHttpClient();
		// 请求超时
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        // 读取超时
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		
		
		// 根据URL创建HttpPost实例
		HttpPost post = new HttpPost(requestUrl);

		try {
			// 设置URL编码
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// 发送请求并获取反馈
			HttpResponse response = client.execute(post);

			// 判断请求是否成功处理
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 解析返回的内容
				String respJsonStr = EntityUtils.toString(response.getEntity(),"utf-8");
				bundle.putInt("code", HttpConstants.SUCCESS);
				bundle.putString("content", respJsonStr);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			bundle.putInt("code", HttpConstants.NETWORK_ERROR); //提示链接服务器失败
		}
		
		return bundle;
	}
	
	
	public static Bundle doPost (List<NameValuePair> params,String requestUrl
			, Context context, int width, int height) {
		
		Bundle bundle = new Bundle();
		
		// 定义待请求的URL
		// 创建HttpClient实例
		HttpClient client = new DefaultHttpClient();
		
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        // 读取超时
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		
		// 根据URL创建HttpPost实例
		HttpPost post = new HttpPost(requestUrl);

		try {
			// 设置URL编码
			post.addHeader("Device-Id", /*Utils.getDeviceId(context)*/null);
			post.addHeader("Device-V", Utils.getDeviceV(context, width, height));
			post.addHeader("Language", Locale.getDefault().getLanguage());
			post.addHeader("Country", Locale.getDefault().getCountry());

			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// 发送请求并获取反馈
			HttpResponse response = client.execute(post);

			// 判断请求是否成功处理
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 解析返回的内容
				String respJsonStr = EntityUtils.toString(response.getEntity(),"utf-8");
				bundle.putInt("code", HttpConstants.SUCCESS);
				bundle.putString("content", respJsonStr);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			bundle.putInt("code", HttpConstants.NETWORK_ERROR); //提示链接服务器失败
		}
		
		return bundle;
	}

}
