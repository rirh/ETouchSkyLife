package com.etouchsky.wisdom;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongCallbackProxy;
import com.ddclient.dongsdk.AbstractDongCallbackProxy.DongAccountCallbackImp;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDK;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.dongsdk.PushInfo;
import com.ddclient.jnisdk.InfoUser;
import com.ddclient.push.DongPushMsgManager;
import com.etouchsky.activity.VideoViewActivity;
import com.etouchsky.adapter.FragmentTabAdapter;
import com.etouchsky.data.DataHelper;
import com.etouchsky.pojo.HouseMoreLineInfo;
import com.etouchsky.util.CacheUtils;
import com.etouchsky.util.Config;
import com.etouchsky.util.OkAndGsonUtil;
import com.gViewerX.util.LogUtils;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity {
    /**
     * Called when the activity is first created.
     */
    private RadioGroup rgs;
    public List<Fragment> fragments = new ArrayList<Fragment>();
    RadioButton rb1;
    RadioButton rb2;
    RadioButton rb3;
    RadioButton rb4;
    RadioButton rb5;
    public static boolean callFlag=false;
    public static String dwDeviceID=null;
    private ListActivityDongAccountProxy mAccountProxy = new ListActivityDongAccountProxy();
    LoginActivityDongAccountProxy mDongAccountProxy = new LoginActivityDongAccountProxy();
    public static Activity mainActivity ;
    SharedPreferences sp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Config.setNoTheme(this);
        setContentView(R.layout.main);
        bindXiaoMi(); //绑定推送

        sp = this.getSharedPreferences("userInfo", Context.MODE_APPEND );
        Boolean flagLogin=sp.getBoolean("flagLogin",false);
        callFlag=this.getIntent().hasExtra("call");
        if(flagLogin && callFlag){
            if(this.getIntent().hasExtra("dwDeviceID")){
                dwDeviceID=this.getIntent().getStringExtra("dwDeviceID");
            }
            DongSDK.initializePush(this, DongPushMsgManager.PUSH_TYPE_ALL);  //初始化所有推送
            boolean initDongAccount = DongSDKProxy.initCompleteDongAccount(); //初始化sdk
            LogUtils.d("initDongAccount=="+initDongAccount);
            if (!initDongAccount) {
                DongSDKProxy.initDongAccount(mAccountProxy);   //初始化电话呼叫
            }
            DongSDKProxy.login(sp.getString("account", CacheUtils.getAccount(MainActivity.this,CacheUtils.USER_ACCOUNT,"000")),sp.getString("pwd","123456"));
        }
        fragments.add(new TabAFm());
        fragments.add(new TabBFm_web());
        fragments.add(new TabCFm_web());
        fragments.add(new TabDFm_web());
        fragments.add(new MyInfo());

        rgs = (RadioGroup) findViewById(R.id.tabs_rg);
        rb1=(RadioButton)findViewById(R.id.tab_rb_a);
        rb2=(RadioButton)findViewById(R.id.tab_rb_b);
        rb3=(RadioButton)findViewById(R.id.tab_rb_c);
        rb4=(RadioButton)findViewById(R.id.tab_rb_d);
        rb5=(RadioButton)findViewById(R.id.tab_rb_e);

        FragmentTabAdapter tabAdapter = new FragmentTabAdapter(this, fragments, R.id.tab_content, rgs);

    }

    private void bindXiaoMi() {
        mainActivity = MainActivity.this;
        DataHelper dbHelper = new DataHelper(this);
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from CommunitiesInfo where Id = 1",null);
        while(cursor.moveToNext()){
            MiPushClient.subscribe(MainActivity.this, cursor.getString(cursor.getColumnIndex("CmtID")), null);
            Log.e("****","绑定的小米小区Id"+cursor.getString(cursor.getColumnIndex("CmtID")));
//            String s1 = cursor.getString(cursor.getColumnIndex("UnitNo"));
//            String s2 = cursor.getString(cursor.getColumnIndex("RoomNo"));
//            String s3 = cursor.getString(cursor.getColumnIndex("UnitName"));
        }
        //关闭数据库
        db.close();
//        MiPushClient.subscribe(MainActivity.this, "10003", null);
    }


    public static boolean isServiceWorked(Context context) {
        try {
            ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
                    .getRunningServices(30);
            for (int i = 0; i < runningService.size(); i++) {
                if (runningService.get(i).service.getClassName().toString().equals("com.etouchsky.gs.ETService")) {
                    System.out.println("MainActivity true");
                    return true;
                }
            }
            System.out.println("MainActivity false");
            return false;
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        DongSDKProxy.registerAccountCallback(mDongAccountProxy);
        DongSDKProxy.registerAccountCallback(mAccountProxy);
        LogUtils.i("MainActivity.clazz--->>>onResume.....deviceList:");


    }
    @Override
    protected void onPause() {
        super.onPause();
        callFlag=false;
        LogUtils.d("MainActivity onPause");
        //DongSDKProxy.unRegisterAccountCallback(mAccountProxy);
    }

    private class ListActivityDongAccountProxy extends AbstractDongCallbackProxy.DongAccountCallbackImp {
        @Override
        public int onAuthenticate(InfoUser tInfo) {
            LogUtils.i("MainActivity.clazz--->>>onAuthenticate........tInfo:ListActivityDongAccountProxy"
                    + tInfo+"callFlag=="+callFlag);
            //一般情况是消息推送执才会执行此处
            if (callFlag) {
                try {
                    ArrayList<DeviceInfo> deviceList = DongSDKProxy.requestGetDeviceListFromCache();
                    LogUtils.d("guosong=" + deviceList.size());
                    if(deviceList.size()==0){
                        Thread.sleep(300);
                        deviceList = DongSDKProxy.requestGetDeviceListFromCache();
                    }
                    for (int i = 0; i < deviceList.size(); i++) {
                        if (deviceList.get(i).dwDeviceID == Integer.parseInt(dwDeviceID)) {
                            DongConfiguration.mDeviceInfo = deviceList.get(i);
                            call(deviceList.get(i));
                            //MainActivity.this.finish();
                            break;
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            //DongSDKProxy.requestGetDeviceListFromPlatform();
            return 0;
        }

        @Override
        public int onUserError(int nErrNo) {
            LogUtils.i("MainActivity.clazz--->>>onUserError........nErrNo:"
                    + nErrNo);
            return 0;
        }

        /**
         * 平台在线推送时回调该方法
         */
        @Override
        public int onCall(ArrayList<DeviceInfo> list) {
            LogUtils.i("MainActivity.clazz--->>>onCall........list.size():" + list.size());
            //Toast.makeText(ETService.this, "ETService平台推送到达!!!", Toast.LENGTH_SHORT).show();
            int size = list.size();
            if (size > 0) {
                /*DeviceInfo deviceInfo = list.get(0);
                String message = deviceInfo.deviceName + deviceInfo.dwDeviceID
                        + deviceInfo.msg;
                DongConfiguration.mDeviceInfo = deviceInfo;
                //getApplicationContext().startActivity(new Intent(ETService.this,
                //       VideoViewActivity.class));
                Intent mintent = new Intent(MainActivity.this, VideoViewActivity.class);
                mintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                //mintent.setClassName(ETService.this, VideoViewActivity.class);
                mintent.putExtra("call","");
                getApplicationContext().startActivity(mintent);
                if(!callFlag | !Config.getAppIsRun(MainActivity.this)){
                    MainActivity.this.finish();
                }*/
                call(list.get(0));
            }
            return 0;
        }
    }
    private class LoginActivityDongAccountProxy extends DongAccountCallbackImp {

        @Override
        public int onAuthenticate(InfoUser tInfo) {
            DongConfiguration.mUserInfo = tInfo;
            LogUtils.i("MainActivity.clazz--->>>onAuthenticate........tInfo:"
                    + tInfo+"callFlag=="+callFlag);
            DongSDKProxy.requestSetPushInfo(PushInfo.PUSHTYPE_FORCE_ADD);
            DongSDKProxy.requestGetDeviceListFromPlatform();

            return 0;
        }

        @Override
        public int onUserError(int nErrNo) {
            LogUtils.i("LoginActivity.clazz--->>>onUserError........nErrNo:"
                    + nErrNo);
            DongSDK.reInitDongSDK();
            return 0;
        }

    }
    public void call(DeviceInfo deviceInfo){
        String message = deviceInfo.deviceName + deviceInfo.dwDeviceID
                + deviceInfo.msg;
        DongConfiguration.mDeviceInfo = deviceInfo;
        //getApplicationContext().startActivity(new Intent(ETService.this,
        //       VideoViewActivity.class));
        Intent mintent = new Intent(MainActivity.this, VideoViewActivity.class);
        mintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //mintent.setClassName(ETService.this, VideoViewActivity.class);
        mintent.putExtra("call","");
        //startActivityForResult(mintent,1);
        startActivity(mintent);
        if(!Config.getAppIsRun(MainActivity.this) | !callFlag ){
            MainActivity.this.finish();
        }
    }
}

