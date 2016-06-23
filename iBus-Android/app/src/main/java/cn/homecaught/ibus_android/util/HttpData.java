/**  
 * @Package net.zhomi.negotiation.view.utils 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author yangshouzhi
 * @date 2015-7-15 下午10:58:34 
 */
package cn.homecaught.ibus_android.util;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName: HttpData
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author yangshouzhi
 * @date 2015-7-15 下午10:58:34
 */
public class HttpData {
	private static final int TIMEOUT_CONNECTION = 5000;
	private static final int TIMEOUT_SO = 10000;
	/** 测试url **/ 

	public static final String FAKE_SERVER = "http://ibus.chinaairplus.com/api/";
	//	 // /** 正式的地址url **/
	public static final String BASE_URL = "http://ibus.chinaairplus.com/";

	public static final String CONNECTION_ERROR_JSON = "{\"status\":0,\"error\":\"%s\"}";
	public static final String CONNECTION_ERROR_URL = "{\"status\":0,\"error\":\"请求地址无效!\"}";

	/**
	 * HttpPost请求
	 * 
	 * @param url
	 *
	 * @return
	 */
	private static String post(String url, List<NameValuePair> nameValuePairs) {
		SystemUtils.print("POST--URL:" + url);
		SystemUtils.print("entity--:" + nameValuePairs);

		String strResult = null;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			httpClient.getParams()
					.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
							TIMEOUT_CONNECTION);
			httpClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, TIMEOUT_SO);
			HttpPost post = new HttpPost(url);
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
					nameValuePairs, HTTP.UTF_8);
			post.setEntity(entity);
			HttpResponse httpResponse = httpClient.execute(post);
			strResult = EntityUtils.toString(httpResponse.getEntity());
		} catch (IllegalArgumentException e) {
			strResult = CONNECTION_ERROR_URL;
		} catch (Exception e) {
			Log.e("111", "result:" + strResult);
			strResult = String.format(CONNECTION_ERROR_JSON,
					e.getMessage() == null ? "" : e.getMessage());
		}
		SystemUtils.print("result:" + strResult);
		return strResult;
	}


	/**
	 * HttpPost请求
	 *
	 * @param url
	 *
	 * @return
	 */
	private static String put(String url, List<NameValuePair> nameValuePairs) {
		SystemUtils.print("PUT--URL:" + url);
		SystemUtils.print("entity--:" + nameValuePairs);

		String strResult = null;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			httpClient.getParams()
					.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
							TIMEOUT_CONNECTION);
			httpClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, TIMEOUT_SO);
			HttpPut put = new HttpPut(url);
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
					nameValuePairs, HTTP.UTF_8);
			put.setEntity(entity);
			HttpResponse httpResponse = httpClient.execute(put);
			strResult = EntityUtils.toString(httpResponse.getEntity());
		} catch (IllegalArgumentException e) {
			strResult = CONNECTION_ERROR_URL;
		} catch (Exception e) {
			Log.e("111", "result:" + strResult);
			strResult = String.format(CONNECTION_ERROR_JSON,
					e.getMessage() == null ? "" : e.getMessage());
		}
		SystemUtils.print("result:" + strResult);
		return strResult;
	}

	/**
	 * HttpPost请求
	 *
	 * @param url
	 *
	 * @return
	 */
	private static String get(String url, List<NameValuePair> nameValuePairs) {
		SystemUtils.print("POST--URL:" + url);
		SystemUtils.print("entity--:" + nameValuePairs);

		String strResult = null;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			httpClient.getParams()
					.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
							TIMEOUT_CONNECTION);
			httpClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, TIMEOUT_SO);
			HttpGet get = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(get);
			strResult = EntityUtils.toString(httpResponse.getEntity());
			SystemUtils.print("code:" + httpResponse.getStatusLine());

		} catch (IllegalArgumentException e) {
			strResult = CONNECTION_ERROR_URL;
		} catch (Exception e) {
			Log.e("111", "result:" + strResult);
			strResult = String.format(CONNECTION_ERROR_JSON,
					e.getMessage() == null ? "" : e.getMessage());
			SystemUtils.print("result err:" + e.getMessage());
			e.printStackTrace();

		}
		SystemUtils.print("result:" + strResult);
		return strResult;
	}
	/**
	 * HttpPost请求
	 * 
	 * @param url
	 *
	 * @return
	 */
	private static String postWithHeader(String url,
			List<NameValuePair> nameValuePairs) {
		SystemUtils.print("POST--URL:" + url);
		SystemUtils.print("entity--:" + nameValuePairs);

		String strResult = null;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			httpClient.getParams()
					.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
							TIMEOUT_CONNECTION);
			httpClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, TIMEOUT_SO);
			HttpPost post = new HttpPost(url);
			post.addHeader("Content-Type",
					"multipart/form-data; boundary=----WebKitFormBoundaryoX42YtuZV3NnqQOy");
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
					nameValuePairs, HTTP.UTF_8);
			post.setEntity(entity);
			HttpResponse httpResponse = httpClient.execute(post);
			strResult = EntityUtils.toString(httpResponse.getEntity());
		} catch (IllegalArgumentException e) {
			strResult = CONNECTION_ERROR_URL;
		} catch (Exception e) {
			strResult = String.format(CONNECTION_ERROR_JSON,
					e.getMessage() == null ? "" : e.getMessage());
			Log.e("111", "result:" + strResult);
			SystemUtils.print(e.getMessage());

		}
		SystemUtils.print("result:" + strResult);
		return strResult;
	}

	/**
	 * 登陆
	 * 
	 * @param email
	 * @param password
	 * @return
	 */
	public static String register(String email, String password, String schoolId) {
		String url = FAKE_SERVER + "reg";
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		BasicNameValuePair lonParam = new BasicNameValuePair("email",
				email);
		BasicNameValuePair latParam = new BasicNameValuePair("password",
				password);

		BasicNameValuePair sParam = new BasicNameValuePair("school_id",
				schoolId);
		nvps.add(lonParam);
		nvps.add(latParam);
		nvps.add(sParam);
		return post(url, nvps);
	}

	/**
	 * 登陆
	 *
	 * @param email
	 * @param password
	 * @return
	 */
	public static String login(String email, String password) {
		String url = FAKE_SERVER + "login/login";
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		BasicNameValuePair userMobile = new BasicNameValuePair("user_mobile",
				email);
		BasicNameValuePair userPass = new BasicNameValuePair("user_pass",
				password);
		nvps.add(userMobile);
		nvps.add(userPass);
		return put(url, nvps);
	}
	public static String handbook() {
		String url = FAKE_SERVER + "site/handbook";
		return get(url, null);
	}

	public static String timetable() {
		String url = FAKE_SERVER + "site/handbook";
		return get(url, null);
	}

	public static String about() {
		String url = FAKE_SERVER + "site/about_us";
		return get(url, null);
	}



}
