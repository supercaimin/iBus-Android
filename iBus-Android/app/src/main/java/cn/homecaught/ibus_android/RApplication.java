package cn.homecaught.ibus_android;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * cn.powerkeeper
 * Created by Rakey.Zhao on 2015/9/5.
 */
public class RApplication extends Application {
    public static RApplication instance;
    public final static String app_canche_camera = Environment
            .getExternalStorageDirectory() + "/VEGETABLE/images/";

    private List<Activity> activitys = new ArrayList<Activity>();


    public RApplication() {
        instance = this;
    }

    public static RApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        /**
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {

            /**
             * IMKit SDK调用第一步 初始化
             */
            RongIM.init(this);
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
}