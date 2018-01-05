package com.etouchsky;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Process;
import android.util.Log;

import com.ddclient.dongsdk.DongSDK;
import com.etouchsky.gs.ETService;
import com.etouchsky.wisdom.R;
import com.gViewerX.util.LogUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

public class GViewerXApplication extends Application {
    public static Context mContext;
    /**
     * 小型数据库读取
     */
    public static SharedPreferences preferences_userInfo;
    /**
     * 小型数据库写入
     */
    public static SharedPreferences.Editor preferences_editor;
    // 小米 your appid the key.
    private static final String APP_ID = "2882303761517604504";
    // 小米 your appid the key.
    private static final String APP_KEY = "5951760482504";
    // 此TAG在adb logcat中检索自己所需要的信息， 只需在命令行终端输入 adb logcat | grep
    // com.xiaomi.mipushdemo
    public static final String TAG = "com.etouchsky.wisdom ";

    @Override
    public void onCreate() {
        super.onCreate();
//        DongSDK.dongSdk_Init();
        int result = DongSDK.initDongSDK(this);
        // 初始化小型数据库的读写
        preferences_userInfo = getSharedPreferences("userInfo", MODE_PRIVATE);
        preferences_editor = preferences_userInfo.edit();
        startService(new Intent(this, ETService.class));
        //MqttTools.init(this);
        LogUtils.i("GViewerXApplication.clazz--->>>onCreate initDongSDK result:" + result);
        mContext=getApplicationContext();

        if (shouldInit()) {
            Log.e("DemoApplication" , "初始化成功");
            MiPushClient.registerPush(this, APP_ID, APP_KEY);      //传入注册的app_id和app_key
        }
        initImageLoader();
    }

    public static synchronized GViewerXApplication context() {
        return (GViewerXApplication) mContext;
    }

    //    初始化ImageLoader
    private  void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(GViewerXApplication.this).defaultDisplayImageOptions(getDefaultDisplayOption())
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .imageDownloader(new BaseImageDownloader(GViewerXApplication.this))
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }
    private DisplayImageOptions getDefaultDisplayOption() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.cut_off) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.cut_off) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .showImageOnLoading(R.mipmap.cut_off).build(); // 创建配置过得DisplayImageOption对象
        return options;
    }


    //初始化小米推送
    //初始化小米推送方法
    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }


}
