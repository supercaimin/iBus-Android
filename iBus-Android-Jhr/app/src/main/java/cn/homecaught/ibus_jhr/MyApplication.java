package cn.homecaught.ibus_jhr;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;


import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.homecaught.ibus_jhr.model.UserBean;
import cn.homecaught.ibus_jhr.util.SharedPreferenceManager;
import cn.homecaught.ibus_jhr.util.HttpData;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import io.rong.push.RongPushClient;

import io.rong.imkit.RongIM.UserInfoProvider;


import java.security.SignatureException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import cn.homecaught.ibus_jhr.util.base64.Base64;

/**
 * cn.powerkeeper
 * Created by Rakey.Zhao on 2015/9/5.
 */
public class MyApplication extends Application {
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    public static MyApplication instance;

    public UserBean getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(UserBean loginUser) {
        this.loginUser = loginUser;
    }

    private UserBean loginUser;

    public final static String app_canche_camera = Environment
            .getExternalStorageDirectory() + "/VEGETABLE/images/";

    public SharedPreferenceManager getSharedPreferenceManager() {
        return sharedPreferenceManager;
    }

    private SharedPreferenceManager sharedPreferenceManager = null;

    private List<Activity> activitys = new ArrayList<Activity>();

    private static DisplayImageOptions options;



    public MyApplication() {
        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public static String calculateHMAC(String data, String key)
            throws java.security.SignatureException
    {
        String result;
        try {
            // Get an hmac_sha256 key from the raw key bytes.
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("UTF8"), HMAC_SHA256_ALGORITHM);
            // Get an hmac_sha256 Mac instance and initialize with the signing key.
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            // Compute the hmac on input data bytes.
            byte[] rawHmac = mac.doFinal(data.getBytes("UTF8"));
            // Base64-encode the hmac by using the utility in the SDK
            result = Base64.encodeToString(rawHmac, Base64.DEFAULT);
        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
        return result;
    }

    @Override
    public void onCreate() {

        super.onCreate();

//        RongPushClient.registerHWPush(this);
//        RongPushClient.registerMiPush(this, "2882303761517473625", "5451747338625");
        RongIM.init(this);


        sharedPreferenceManager = new SharedPreferenceManager(this, SharedPreferenceManager.PREFERENCE_FILE);

        options = new DisplayImageOptions.Builder()
                .displayer(new FadeInBitmapDisplayer(300))
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        //初始化图片下载组件
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(200)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .defaultDisplayImageOptions(options)
                .build();

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

        try {
            String strToSign = "GET\n"
                    + "create_meeting\n"
                    + "end_time=1450923006431&"
                    + "enterprise_id=516f75fcc4afc2093c804b5f03b2efd1218d3be9&"
                    + "max_participant=50&"
                    + "meeting_name=test&"
                    + "require_password=true&"
                    + "start_time=1450923006431\n"
                    + "47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=";

            strToSign = "123";


          String res =  calculateHMAC(strToSign, "123");
            Log.i("ssssssssssssss", URLEncoder.encode(res, "UTF-8"));

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return 进程号
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
    /**
     * 添加activity
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        activitys.add(activity);
    }

    public void removeActivity(Activity activity) {
        activitys.remove(activity);
    }

    public void clearActivity() {
        for (Activity activity : activitys) {
            activity.finish();
        }
        activitys.clear();
    }

    public static void initImageLoader(Context context) {
        @SuppressWarnings("static-access")
        File cacheDir = StorageUtils.getOwnCacheDirectory(context,
                instance.app_canche_camera);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .threadPoolSize(2)
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .diskCache(new UnlimitedDiskCache(cacheDir))
                        // .memoryCacheExtraOptions(480, 800)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheFileCount(100).diskCacheSize(10 * 1024 * 1024)
                .memoryCache(new WeakMemoryCache())
                .memoryCacheSize(10 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs().build();
        ImageLoader.getInstance().init(config);
    }

    /**
     * 退出程序
     */
    public void exit() {
        for (Activity activity : activitys) {
            activity.finish();
        }
        activitys.clear();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static Context getContext() {
        return getInstance();
    }

    public List<UserInfo> getFriendList(){return  null;};

    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    public void connect(String token) {
        /**
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ) {

            /**
             * IMKit SDK调用第一步 初始化
             */
            final String schoolId = getSharedPreferenceManager().getSchoolId();
            RongIM.getInstance().setCurrentUserInfo(new UserInfo(schoolId + "_" + getLoginUser().getId(),
                    getLoginUser().getUserFirstName() + " " + getLoginUser().getUserLastName(),
                    Uri.parse(HttpData.getBaseUrl() + getLoginUser().getUserHead())));

            RongIM.setUserInfoProvider(new UserInfoProvider() {
                @Override
                public UserInfo getUserInfo(String s) {
                    String jsonString = HttpData.getUser(s.split("_")[1]);
                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        boolean status = jsonObject.getBoolean("status");
                        if(status){

                            UserBean userBean = new UserBean(jsonObject.getJSONObject("info"));
                            UserInfo userInfo = new UserInfo(schoolId + "_" + userBean.getId(),
                                    userBean.getUserFirstName() + " " + userBean.getUserLastName(),
                                    Uri.parse(HttpData.getBaseUrl() + userBean.getUserHead()));
                            Log.v("UserInfo", userInfo.getName() + "   " + userInfo.getPortraitUri());
                            return userInfo;
                        }else {
                            Toast.makeText(getApplicationContext(), jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                            return  null;
                        }
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                    return null;
                }
            }, false);


                /**
                 * IMKit SDK调用第二步,建立与服务器的连接
                 */
                RongIM.connect(token, new RongIMClient.ConnectCallback()

                        {

                            /**
                             * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                             */
                            @Override
                            public void onTokenIncorrect() {

                                Log.d("MyApplication", "--onTokenIncorrect");
                            }

                            /**
                             * 连接融云成功
                             * @param userid 当前 token
                             */
                            @Override
                            public void onSuccess(String userid) {

                                Log.d("MyApplication", "--onSuccess" + userid);

                            }

                            /**
                             * 连接融云失败
                             * @param errorCode 错误码，可到官网 查看错误码对应的注释
                             */
                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {

                                Log.d("MyApplication", "--onError" + errorCode);
                            }
                        }

                );

            }
        }
}