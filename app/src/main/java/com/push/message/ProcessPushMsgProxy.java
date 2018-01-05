package com.push.message;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.ddclient.dongsdk.PushMsgBean;
import com.etouchsky.wisdom.MainActivity;
import com.gViewerX.util.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * 必须拷贝这个类到应用中去，此类用于离线推送处理的中转站
 */
public class ProcessPushMsgProxy {
    public static String TAG="ProcessPushMsgProxy.clazz";
    /**
     * 处理离线推送
     *
     * @param context     上下文
     * @param dongMessage 消息载体
     */
    public static void processPushMsg(Context context, PushMsgBean dongMessage) {
        LogUtils.i("ProcessPushMsgProxy.clazz---------->>>pushMessageReceiver PushMsgBean:"
                + dongMessage.getDeviceId());
        if (getcurData(dongMessage.getPushTime())){
            return;
        }
        /*DeviceInfo deviceInfo=new DeviceInfo(null);
        deviceInfo.deviceName=dongMessage.getMessage();
        deviceInfo.msg=dongMessage.getMessage();
        deviceInfo.dwDeviceID=Integer.parseInt(dongMessage.getDeviceId());
       DongConfiguration.mDeviceInfo = deviceInfo;*/
      //getApplicationContext().startActivity(new Intent(ETService.this,
        //       VideoViewActivity.class));
        if(!getCurrentAct(context)){

            //if(flag) {
                Intent mintent = new Intent(context, MainActivity.class);
                mintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                //mintent.setClassName(ETService.this, VideoViewActivity.class);
                mintent.putExtra("call", "");
                mintent.putExtra("dwDeviceID",dongMessage.getDeviceId());
                context.startActivity(mintent);
            //}
        }
    }
    public static boolean getCurrentAct(Context context) {
        final ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        String name = am.getRunningTasks(1).get(0).topActivity.getClassName();
        for(int i=0;i<am.getRunningTasks(1).size();i++){
            LogUtils.d("ProcessPushMsgProxy="+am.getRunningTasks(1).get(i).getClass());
        }
        System.out.println("ProcessPushMsgProxy name=" + name);
        if ("com.etouchsky.wisdom.login".equals(name)) {
            return true;
        } else if ("com.etouchsky.wisdom.MainActivity".equals(name)) {
            return true;
        } else if ("com.etouchsky.activity.VideoViewActivity".equals(name)) {
            return true;
        }  else {
            return false;
        }

    }
    public static Boolean getcurData(String s2){
        SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date=sDateFormat.format(new java.util.Date());
        LogUtils.d(TAG,s2+"data=="+date);
        try {
            Date d1 = sDateFormat.parse(date);
            Date d2 = sDateFormat.parse(s2);
            //比较
            Long result=Math.abs(((d1.getTime() - d2.getTime())/(60*1000)));
            System.out.println("ProcessPushMsgProxyresult==="+result);
            if(result >=2) {
               // System.out.println("ProcessPushMsgProxy大于三天");
                return true;
            }else{
                //System.out.println("ProcessPushMsgProxy小于三天");
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;

    }
    /**
     * 自定义的消息格式
     *
     * @param context 上下文对象
     * @param msg     自定义推送信息
     */
    public static void processPushMsg(Context context, String msg) {
        LogUtils.i("ProcessPushMsgProxy.clazz----------->>>message define message:" + msg);
    }
}
