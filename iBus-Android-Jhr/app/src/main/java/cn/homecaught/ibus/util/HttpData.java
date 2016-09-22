/**
 * @Package net.zhomi.negotiation.view.utils
 * @Description: TODO(用一句话描述该文件做什么)
 * @author yangshouzhi
 * @date 2015-7-15 下午10:58:34
 */
package cn.homecaught.ibus.util;

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

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;


import java.util.ArrayList;
import java.util.List;

import cn.homecaught.ibus.model.UserBean;


/**
 * @author yangshouzhi
 * @ClassName: HttpData
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2015-7-15 下午10:58:34
 */
public class HttpData {
    private static final int TIMEOUT_CONNECTION = 5000 * 100;
    private static final int TIMEOUT_SO = 10000 * 100;


    public static final String TRACK_TYPE_BACK = "back";
    public static final String TRACK_TYPE_GO = "go";

    public static final String CHILD_STATUS_ON = "on";
    public static final String CHILD_STATUS_OFF = "off";

    /**
     * 测试url
     **/

    public static final String FAKE_SERVER = "http://ibus.chinaairplus.com/api/";
    //	 // /** 正式的地址url **/
    public static final String BASE_URL = "http://ibus.chinaairplus.com/";

    public static final String CONNECTION_ERROR_JSON = "{\"status\":false,\"msg\":\"请求失败%s\"}";
    public static final String CONNECTION_ERROR_URL = "{\"status\":false,\"msg\":\"请求地址无效!\"}";

    public static String cookie = null;


    /**
     * HttpPost请求
     *
     * @param url
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
            if (cookie != null) {
                post.setHeader("Cookie", cookie);
                Log.i("Cookie", cookie);
            }
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
            e.printStackTrace();
        }
        SystemUtils.print("result:" + strResult);
        return strResult;
    }


    /**
     * HttpPost请求
     *
     * @param url
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
            if (url.equals(FAKE_SERVER + "login/login")) {
                cookie = HttpData.getCookies(httpClient);
                Log.i("HttpData cookie", cookie);
            }


        } catch (IllegalArgumentException e) {
            strResult = CONNECTION_ERROR_URL;
        } catch (Exception e) {
            Log.e("111", "result:" + strResult);
            strResult = String.format(CONNECTION_ERROR_JSON,
                    e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        }
        SystemUtils.print("result:" + strResult);
        return strResult;
    }

    /**
     * HttpPost请求
     *
     * @param url
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
            if (cookie != null)
                get.setHeader("Cookie", cookie);
//                Log.i("Set Cookie", cookie);
            get.getParams().setParameter(
                    ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
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

    public static String submitPost(String url, String filepath) {

        HttpClient httpclient = new DefaultHttpClient();
        String strResult = null;

        try {

            HttpPost httppost = new HttpPost(url);

            FileBody bin = new FileBody(new File(filepath));

            StringBody comment = new StringBody(filepath);

            MultipartEntity reqEntity = new MultipartEntity();

            reqEntity.addPart("upfile", bin);//file1为请求后台的File upload;属性
            reqEntity.addPart("filename", comment);//filename1为请求后台的普通参数;属性
            httppost.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(httppost);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {

                strResult = EntityUtils.toString(response.getEntity());
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                httpclient.getConnectionManager().shutdown();
            } catch (Exception ignore) {

            }
        }

        return strResult;
    }


    private static String getCookies(HttpClient httpClient) {
        StringBuilder sb = new StringBuilder();
        List<Cookie> cookies = ((AbstractHttpClient) httpClient).getCookieStore().getCookies();
        for (Cookie cookie : cookies)
            sb.append(cookie.getName() + "=" + cookie.getValue() + ";");
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
        String url = FAKE_SERVER + "site/timetable";
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
        return get(url, null);
    }

    public static String getBusChildren() {
        String url = FAKE_SERVER + "aunt/bus_children";
        return get(url, null);
    }

    public static String setUrgent(String id) {
        String url = FAKE_SERVER + "aunt/urgent";
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        BasicNameValuePair uid = new BasicNameValuePair("id",
                id);
        nvps.add(uid);
        return post(url, nvps);
    }


    public static String getUrgent() {
        String url = FAKE_SERVER + "aunt/urgent";
        return get(url, null);
    }

    public static String setTravelStart(String travelType, List<UserBean> childs) {
        String url = FAKE_SERVER + "aunt/travel_start";
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        BasicNameValuePair ttype = new BasicNameValuePair("travel_type",
                travelType);
        nvps.add(ttype);

        for (int i = 0; i < childs.size(); i++) {
            UserBean userBean = childs.get(i);
            BasicNameValuePair childType = new BasicNameValuePair("children[" + i + "][type]", userBean.getUserOnBus());
            BasicNameValuePair childId = new BasicNameValuePair("children[" + i + "][id]", userBean.getId());
            nvps.add(childType);
            nvps.add(childId);
        }
        return post(url, nvps);
    }

    public static String setTravelArriveStation(List<UserBean> childs) {
        String url = FAKE_SERVER + "aunt/travel_arrive_station";
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        for (int i = 0; i < childs.size(); i++) {
            UserBean userBean = childs.get(i);
            BasicNameValuePair childType = new BasicNameValuePair("children[" + i + "][type]",
                    userBean.getUserOnBus());
            BasicNameValuePair childId = new BasicNameValuePair("children[" + i + "][id]",
                    userBean.getId());
            nvps.add(childType);
            nvps.add(childId);
        }
        return post(url, nvps);
    }

    public static String addChild(String userHead, String userSN, String userFirstName, String userLastName) {
        String url = FAKE_SERVER + "aunt/child";
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        BasicNameValuePair vpUserHead = new BasicNameValuePair("user_head",
                userHead);
        BasicNameValuePair vpUserSN = new BasicNameValuePair("user_sn",
                userSN);
        BasicNameValuePair vpUserFirstName = new BasicNameValuePair("user_first_name",
                userFirstName);
        BasicNameValuePair vpUserLastName = new BasicNameValuePair("user_last_name",
                userLastName);
        nvps.add(vpUserHead);
        nvps.add(vpUserSN);
        nvps.add(vpUserFirstName);
        nvps.add(vpUserLastName);

        return post(url, nvps);
    }
    public static String getUser(String userId) {
        String url = FAKE_SERVER + "aunt/user/" + userId;
        return get(url, null);
    }

    public  static String uploadImage(String filepath){

        return submitPost(HttpData.FAKE_SERVER + "upload/image", filepath);
    }

    public static String chgInfo(String userHead, String userFirstName, String userLastName) {
        String url = FAKE_SERVER + "aunt/user";
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        BasicNameValuePair vpUserHead = new BasicNameValuePair("user_head",
                userHead);
        BasicNameValuePair vpUserFirstName = new BasicNameValuePair("user_first_name",
                userFirstName);
        BasicNameValuePair vpUserLastName = new BasicNameValuePair("user_last_name",
                userLastName);
        nvps.add(vpUserHead);
        nvps.add(vpUserFirstName);
        nvps.add(vpUserLastName);

        return put(url, nvps);
    }

    public static String register(String userMobile, String userEmail, String userPass, String userFirstName, String userLastName, String childFirstName, String childLastName, String childSN) {
        String url = FAKE_SERVER + "login/reg";
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        BasicNameValuePair vpUserMobile = new BasicNameValuePair("user_mobile",
                userMobile);
        BasicNameValuePair vpUserEmail = new BasicNameValuePair("user_email",
                userEmail);
        BasicNameValuePair vpUserPass = new BasicNameValuePair("user_pass",
                userPass);
        BasicNameValuePair vpUserFistName = new BasicNameValuePair("user_first_name",
                userFirstName);
        BasicNameValuePair vpUserLastName = new BasicNameValuePair("user_last_name",
                userLastName);
        BasicNameValuePair vpChildFirstName = new BasicNameValuePair("child_first_name",
                childFirstName);
        BasicNameValuePair vpChildLastName = new BasicNameValuePair("child_last_name",
                childLastName);
        BasicNameValuePair vpChildSN = new BasicNameValuePair("child_sn",
                childSN);
        nvps.add(vpUserMobile);
        nvps.add(vpUserEmail);
        nvps.add(vpUserPass);
        nvps.add(vpUserFistName);
        nvps.add(vpUserLastName);
        nvps.add(vpChildFirstName);
        nvps.add(vpChildLastName);
        nvps.add(vpChildSN);

        return post(url, nvps);
    }

    public static String getChilds() {
        String url = FAKE_SERVER + "guardian/children/";
        return get(url, null);
    }

    public static String getChildLines(String id) {
        String url = FAKE_SERVER + "guardian/child_lines/?id=" + id;
        return get(url, null);
    }

    public static String getSelectLines() {
        String url = FAKE_SERVER + "guardian/select_lines/";
        return get(url, null);
    }

    public static String addChild( String userFirstName,
                                       String userLastName,
                                       String childSN,
                                       String grade,
                                       String pickUpId,
                                       String dropOffId,
                                       String userHead) {
        String url = FAKE_SERVER + "guardian/child/";
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        BasicNameValuePair vpUserFistName = new BasicNameValuePair("user_first_name",
                userFirstName);
        BasicNameValuePair vpUserLastName = new BasicNameValuePair("user_last_name",
                userLastName);
        BasicNameValuePair vpChildSN = new BasicNameValuePair("user_sn",
                childSN);
        BasicNameValuePair vpGrade = new BasicNameValuePair("user_grade",
                grade);
        BasicNameValuePair vpPickUpId = new BasicNameValuePair("user_on_line_id",
                pickUpId);
        BasicNameValuePair vpDropOffId = new BasicNameValuePair("user_off_line_id",
                dropOffId);
        BasicNameValuePair vpUserHead = new BasicNameValuePair("user_head",
                userHead);


        nvps.add(vpUserFistName);
        nvps.add(vpUserLastName);
        nvps.add(vpChildSN);
        nvps.add(vpGrade);
        nvps.add(vpPickUpId);
        nvps.add(vpDropOffId);
        nvps.add(vpUserHead);

        return post(url, nvps);
    }
}
