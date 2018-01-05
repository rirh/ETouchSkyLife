package com.etouchsky.wisdom;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongCallbackProxy.DongAccountCallbackImp;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDK;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.dongsdk.PushInfo;
import com.ddclient.jnisdk.InfoUser;
import com.etouchsky.activity.VideoViewActivity;
import com.etouchsky.util.Config;
import com.etouchsky.util.DataUtil;
import com.etouchsky.util.HttpUtil;
import com.etouchsky.util.MediaMusicOfCall;
import com.etouchsky.view.RippleView;
import com.gViewerX.util.LogUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class CallManager extends Activity {
    /**
     * Called when the activity is first created.
     */
    // 正在播放的设备
    private DeviceInfo mDeviceInfo;
    public static boolean callFlag = false, isTimeOut = false;
    public static String dwDeviceID = null;
    private ListActivityDongAccountProxy mAccountProxy = new ListActivityDongAccountProxy();
    LoginActivityDongAccountProxy mDongAccountProxy = new LoginActivityDongAccountProxy();
    private RippleView mRippleLayout;
    SharedPreferences sp;
    private Timer timer = new Timer();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x01){
                Toast.makeText(CallManager.this,"呼叫超时",Toast.LENGTH_SHORT).show();
                sendOutdoorHandup();
//                stopTimer();
                CallManager.this.finish();
            }
        }
    };
    private String devCode;
    private String cameraID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.setNoTheme(this);
        MediaMusicOfCall.intPlayData(CallManager.this);
        MediaMusicOfCall.playMusic();
        setContentView(R.layout.call_layout);

        ImageView iv_hangup = (ImageView) findViewById(R.id.iv_hangup);
        iv_hangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                sendOutdoorHandup();
                CallManager.this.finish();
            }
        });
        mRippleLayout = (RippleView) findViewById(R.id.ripple_view);
        if (mRippleLayout.isStarting()) {
            mRippleLayout.stop();
        }
        mRippleLayout.start();
        sendMangercall();
        timer.schedule(new TimerTask() {

            public void run() {
                // TODO Auto-generated method stub
                Message message = Message.obtain();
                message.what = 0x01;
                handler.sendMessage(message);
            }
        }, 30000);
    }

    public void sendMangercall() {
        final String url = HttpUtil.callManager;
        RequestParams params = new RequestParams();
        try {
            JSONObject jb = new JSONObject();
            Cursor cursor = DataUtil.getSQLiteDb(this).query("CommunitiesInfo", new String[]{"CmtID", "UnitNo", "RoomNo", "UnitName"}, null, null, null, null, null);
            cursor.moveToNext();
            String s1 = cursor.getString(cursor.getColumnIndex("UnitNo"));
            String s2 = cursor.getString(cursor.getColumnIndex("RoomNo"));
            String s3 = cursor.getString(cursor.getColumnIndex("CmtID"));
            Log.e("****", s1 + "*" + s2 + "*" + s3 + "*");
            jb.put("unitNo", cursor.getString(cursor.getColumnIndex("UnitNo")));
            jb.put("roomNo", cursor.getString(cursor.getColumnIndex("RoomNo")));
            jb.put("communityId", cursor.getString(cursor.getColumnIndex("CmtID")));
            params.setBodyEntity(new StringEntity(jb.toString(), "utf-8"));
            params.setContentType("applicatin/json");
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.addHeader("Content-Type", "application/json");
        Config.http.send(HttpRequest.HttpMethod.POST, url, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        try {
                            System.out.println("http---onSuccess");
                            Log.e("*****成功", responseInfo.result);
                            JSONObject object = new JSONObject(responseInfo.result);
                            devCode = object.getString("devCode");
                            cameraID = object.getString("cameraID");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(
                            com.lidroid.xutils.exception.HttpException arg0,
                            String msg) {
                        System.out.println("http---onFailure" + msg);
                        Log.e("*****失败", msg);
                    }
                });
    }

    public void sendOutdoorHandup() {
        final String url = HttpUtil.handup;
        Log.e("****", "挂断地址:" + devCode);
        RequestParams params = new RequestParams("UTF-8");
        try {
            params.setBodyEntity(new StringEntity("{\"cameraID\":\"" + cameraID + "\"}", "UTF-8"));
            params.setContentType("applicatin/json");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//        params.setAsJsonContent(true);
//        params.setBodyContent("{\"username\":\"test\",\"password\":\"test\"}");
//        params.addHeader("Content-Type", "application/json");
        Config.http.send(HttpRequest.HttpMethod.POST, url, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        try {
//                            System.out.println("http---onSuccess");
                            Log.e("****", "http---onFailure");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(
                            com.lidroid.xutils.exception.HttpException arg0,
                            String msg) {
//                        System.out.println("http---onFailure"+msg);
                        Log.e("****", "http---onFailure" + "msg:" + msg);
                    }
                });
        MediaMusicOfCall.stopMusic();
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
        callFlag = false;
        //sendOutdoorHandup();
        LogUtils.d("MainActivity onPause");
        //DongSDKProxy.unRegisterAccountCallback(mAccountProxy);
    }

    // 停止定时器
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            // 一定设置为null，否则定时器不会被回收
            timer = null;
        }
    }


    private class ListActivityDongAccountProxy extends DongAccountCallbackImp {
        @Override
        public int onAuthenticate(InfoUser tInfo) {
            LogUtils.i("MainActivity.clazz--->>>onAuthenticate........tInfo:ListActivityDongAccountProxy"
                    + tInfo + "callFlag==" + callFlag);
            //一般情况是消息推送执才会执行此处
            if (callFlag) {
                try {
                    ArrayList<DeviceInfo> deviceList = DongSDKProxy.requestGetDeviceListFromCache();
                    LogUtils.d("guosong=" + deviceList.size());
                    if (deviceList.size() == 0) {
                        Thread.sleep(300);
                        deviceList = DongSDKProxy.requestGetDeviceListFromCache();
                    }
                    for (int i = 0; i < deviceList.size(); i++) {
                        if (deviceList.get(i).dwDeviceID == Integer.parseInt(dwDeviceID)) {
                            DongConfiguration.mDeviceInfo = deviceList.get(i);
                            call(deviceList.get(i));
                            break;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
                    + tInfo + "callFlag==" + callFlag);
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

    public void call(DeviceInfo deviceInfo) {
        String message = deviceInfo.deviceName + deviceInfo.dwDeviceID
                + deviceInfo.msg;
        DongConfiguration.mDeviceInfo = deviceInfo;
        mDeviceInfo = deviceInfo;
        Log.e("****挂断地址:",mDeviceInfo.deviceSerialNO);
//        if (isTimeOut) {
//            sendOutdoorHandup();
//        } else {
            //getApplicationContext().startActivity(new Intent(ETService.this,
            //       VideoViewActivity.class));
            Intent mintent = new Intent(CallManager.this, VideoViewActivity.class);
            mintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            //mintent.setClassName(ETService.this, VideoViewActivity.class);
            MediaMusicOfCall.stopMusic();
            mintent.putExtra("managercall", "");
            //startActivityForResult(mintent,1);
            stopTimer();
            startActivity(mintent);
            if (!Config.getAppIsRun(CallManager.this) | !callFlag) {
                CallManager.this.finish();
            }
//        }
    }

    @Override
    public void onBackPressed() {
        stopTimer();
        sendOutdoorHandup();
        CallManager.this.finish();
    }

}

