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
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;

import java.util.ArrayList;
import java.util.List;

import cn.homecaught.ibus_android.model.UserBean;


/**
 * @ClassName: HttpData
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author yangshouzhi
 * @date 2015-7-15 下午10:58:34
 */
public class HttpData {
	private static final int TIMEOUT_CONNECTION = 5000;
	private static final int TIMEOUT_SO = 10000;


	public static final String TRACK_TYPE_BACK = "back";
	public static final String TRACK_TYPE_GO = "go";

	public static final String CHILD_STATUS_ON = "on";
	public static final String CHILD_STATUS_OFF = "off";

	/** 测试url **/

	public static final String FAKE_SERVER = "http://ibus.chinaairplus.com/api/";
	//	 // /** 正式的地址url **/
	public static final String BASE_URL = "http://ibus.chinaairplus.com/";

	public static final String CONNECTION_ERROR_JSON = "{\"status\":0,\"error\":\"%s\"}";
	public static final String CONNECTION_ERROR_URL = "{\"status\":0,\"error\":\"请求地址无效!\"}";

	public static String cookie = null;


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
			httpClient.getParams().setParameter(
					ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2965);
			httpClient.getParams()
					.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
							TIMEOUT_CONNECTION);
			httpClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, TIMEOUT_SO);
			HttpPost post = new HttpPost(url);
			if (cookie != null)
				post.setHeader("Cookie", cookie);
            post.getParams().setParameter(
					ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
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
			httpClient.getParams().setParameter(
					ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2965);
			httpClient.getParams()
					.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
							TIMEOUT_CONNECTION);
			httpClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, TIMEOUT_SO);
			HttpPut put = new HttpPut(url);
			if (cookie != null)
				put.setHeader("Cookie", cookie);

            put.getParams().setParameter(
                    ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
					nameValuePairs, HTTP.UTF_8);
			put.setEntity(entity);
			HttpResponse httpResponse = httpClient.execute(put);
			strResult = EntityUtils.toString(httpResponse.getEntity());
			if (cookie == null && url.equals(FAKE_SERVER + "login/login")){
				cookie = HttpData.getCookies(httpClient);
				Log.i("HttpData", cookie);
			}


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
		SystemUtils.print("GET--URL:" + url);
		SystemUtils.print("entity--:" + nameValuePairs);

		String strResult = null;
		try {


			DefaultHttpClient httpClient = new DefaultHttpClient();

			httpClient.getParams().setParameter(
					ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2965);
			httpClient.getParams()
					.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
							TIMEOUT_CONNECTION);
			httpClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, TIMEOUT_SO);
			HttpGet get = new HttpGet(url);
            get.getParams().setParameter(
					ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
			if (cookie != null)
				get.setHeader("Cookie", cookie);
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

	private static String getCookies(HttpClient httpClient) {
		StringBuilder sb = new StringBuilder();
		List<Cookie> cookies = ((AbstractHttpClient)httpClient).getCookieStore().getCookies();
		for(Cookie cookie: cookies)
			sb.append(cookie.getName() + "=" + cookie.getValue() + ";");

		// 除了HttpClient自带的Cookie，自己还可以增加自定义的Cookie
		// 增加代码...


		return sb.toString();
	}


	public static String chgPassword(String oldPassword, String password) {
		String url = FAKE_SERVER + "aunt/password";
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		BasicNameValuePair old = new BasicNameValuePair("user_pass",
				oldPassword);
		BasicNameValuePair newP = new BasicNameValuePair("user_pass_new",
				password);

		nvps.add(old);
		nvps.add(newP);
		return put(url, nvps);
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
	public static String getWorkWeb() {
		String url = FAKE_SERVER + "site/work";
		return get(url, null);
	}

	public static String getUrgentWeb() {
		String url = FAKE_SERVER + "site/urgent";
		return get(url, null);
	}

	public static String getAboutWeb() {
		String url = FAKE_SERVER + "site/about_us";
		return get(url, null);
	}


	public static String getManagers() {
		String url = FAKE_SERVER + "aunt/managers";
		return get(url, null);
	}

	public static String getBus() {
		String url = FAKE_SERVER + "aunt/bus";
		return  get(url, null);
	}

	public static String getBusChildren() {
		String url = FAKE_SERVER + "aunt/bus_children";
		return  get(url, null);
	}

	public static String setUrgent(String id){
		String url = FAKE_SERVER + "aunt/urgent";
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		BasicNameValuePair uid = new BasicNameValuePair("id",
				id);
		nvps.add(uid);
		return  post(url, nvps);
	}


	public static String getUrgent() {
		String url = FAKE_SERVER + "aunt/urgent";
		return  get(url, null);
	}

	public static String setTravelStart(String travelType, List<UserBean> childs){
		String url = FAKE_SERVER + "aunt/travel_start";
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		BasicNameValuePair ttype = new BasicNameValuePair("travel_type",
				travelType);
		nvps.add(ttype);

		for (int i = 0; i < childs.size(); i++){
			UserBean userBean = childs.get(i);
			BasicNameValuePair childType = new BasicNameValuePair("children["+ i + "][type]", userBean.getUserOnBus());
			BasicNameValuePair childId = new BasicNameValuePair("children["+ i + "][id]", userBean.getId());
			nvps.add(childType);
			nvps.add(childId);
		}
		return  post(url, nvps);
	}

	public static String setTravelArriveStation(List<UserBean> childs){
		String url = FAKE_SERVER + "aunt/travel_arrive_station";
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		for (int i = 0; i < childs.size(); i++){
			UserBean userBean = childs.get(i);
			BasicNameValuePair childType = new BasicNameValuePair("children["+ i + "][type]",
					userBean.getUserOnBus());
			BasicNameValuePair childId = new BasicNameValuePair("children["+ i + "][id]",
					userBean.getId());
			nvps.add(childType);
			nvps.add(childId);
		}
		return  post(url, nvps);
	}


}
