package com.etouchsky.wisdom;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.util.Log;

import com.etouchsky.activity.AFragmentMoreLineActivity;
import com.etouchsky.activity.AFragmentMoreLineInfoActivity;
import com.etouchsky.activity.O2OLoginActivity;
import com.etouchsky.data.CommunitiesInitData;
import com.etouchsky.pojo.HouseMoreLineInfo;
import com.etouchsky.util.OkAndGsonUtil;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/9/28 0028.
 */

public class MessageReceiver extends PushMessageReceiver {
    private String mRegId;
    private String mTopic;
    private String mAlias;
    private String mAccount;
    private String mStartTime;
    private String mEndTime;
    private  O2OLoginActivity o2OLoginActivity = new O2OLoginActivity();

    //    用来接收服务器发送的透传消息
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        Log.e("*****xiaomi", "onReceivePassThroughMessage :" + message.toString());
//        Log.v(DemoApplication.TAG,"onReceivePassThroughMessage is called. " + message.toString());
//        String log = context.getString(R.string.recv_passthrough_message, message.getContent());
//        MainActivity.logList.add(0, getSimpleDate() + " " + log);
//
//        if (!TextUtils.isEmpty(message.getTopic())) {
//            mTopic = message.getTopic();
//        } else if (!TextUtils.isEmpty(message.getAlias())) {
//            mAlias = message.getAlias();
//        }
//
//        Message msg = Message.obtain();
//        msg.obj = log;
//        DemoApplication.getHandler().sendMessage(msg);
    }

    //用来接收服务器发来的通知栏消息（用户点击通知栏时触发）
    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        Log.e("*****xiaomi", "onNotificationMessageClicked :" + message.toString()+"上下文数据"+message.getContent());
        Intent intent = new Intent(context, AFragmentMoreLineInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //添加新的TASK
        intent.putExtra("noticeID", Integer.valueOf(OkAndGsonUtil.gson.fromJson(message.getContent(),HouseMoreLineInfo.class).getNoticeid()));
        context.startActivity(intent);
//        Log.v(DemoApplication.TAG,"onNotificationMessageClicked is called. " + message.toString());
//        String log = context.getString(R.string.click_notification_message, message.getContent());
//        MainActivity.logList.add(0, getSimpleDate() + " " + log);
//
//        if (!TextUtils.isEmpty(message.getTopic())) {
//            mTopic = message.getTopic();
//        } else if (!TextUtils.isEmpty(message.getAlias())) {
//            mAlias = message.getAlias();
//        }
//
//        Message msg = Message.obtain();
//        if (message.isNotified()) {
//            msg.obj = log;
//        }
//        DemoApplication.getHandler().sendMessage(msg);
    }


    //    用来接收服务器发来的通知栏消息（消息到达客户端时触发，并且可以接收应用在前台时不弹出通知的通知消息）
    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Log.e("*****xiaomi", "onNotificationMessageArrived :" + message.toString() + message.getContent() + message.getTopic());
        new CommunitiesInitData(context,message.getContent());
        Log.e("*****xiaomi", "onNotificationMessageArrived :" + message.getTitle() + "数据内容:" + message.getContent() + "消息摘要" + message.getDescription() + "推送标题" + message.getTitle());
//        messageId={tlm10b16506583857310eJ},passThrough={0},alias={null},topic={**ALL**},userAccount={null},content={推送传输的数据内容},description={推送消息摘要},title={推送标题},isNotified={false},notifyId={0},
//        Log.v(DemoApplication.TAG,
//                "onNotificationMessageArrived is called. " + message.toString());
//        String log = context.getString(R.string.arrive_notification_message, message.getContent());
//        MainActivity.logList.add(0, getSimpleDate() + " " + log);
//
//        if (!TextUtils.isEmpty(message.getTopic())) {
//            mTopic = message.getTopic();
//        } else if (!TextUtils.isEmpty(message.getAlias())) {
//            mAlias = message.getAlias();
//        }
//
//        Message msg = Message.obtain();
//        msg.obj = log;
//        DemoApplication.getHandler().sendMessage(msg);
    }

    //    用来接收客户端向服务器发送命令消息后返回的响应     1
    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        Log.e("*****xiaomi", "onCommandResult :" + message.toString());
//        Log.v(DemoApplication.TAG,
//                "onCommandResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        String log;
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                log = context.getString(R.string.register_success);
            } else {
                log = context.getString(R.string.register_fail);
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
                log = context.getString(R.string.set_alias_success, mAlias);
            } else {
                log = context.getString(R.string.set_alias_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
                log = context.getString(R.string.unset_alias_success, mAlias);
            } else {
                log = context.getString(R.string.unset_alias_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
                log = context.getString(R.string.set_account_success, mAccount);
            } else {
                log = context.getString(R.string.set_account_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
                log = context.getString(R.string.unset_account_success, mAccount);
            } else {
                log = context.getString(R.string.unset_account_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
                log = context.getString(R.string.subscribe_topic_success, mTopic);
            } else {
                log = context.getString(R.string.subscribe_topic_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
                log = context.getString(R.string.unsubscribe_topic_success, mTopic);
            } else {
                log = context.getString(R.string.unsubscribe_topic_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
                log = context.getString(R.string.set_accept_time_success, mStartTime, mEndTime);
            } else {
                log = context.getString(R.string.set_accept_time_fail, message.getReason());
            }
        } else {
            log = message.getReason();
        }
//        MainActivity.logList.add(0, getSimpleDate() + "    " + log);
//
//        Message msg = Message.obtain();
//        msg.obj = log;
//        DemoApplication.getHandler().sendMessage(msg);
    }

    //    用来接受客户端向服务器发送注册命令消息后返回的响应
    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        Log.e("*****xiaomi", "onReceiveRegisterResult :" + message.toString());

//        Log.v(DemoApplication.TAG,
//                "onReceiveRegisterResult is called. " + message.toString());
//        String command = message.getCommand();
//        List<String> arguments = message.getCommandArguments();
//        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
//        String log;
//        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
//            if (message.getResultCode() == ErrorCode.SUCCESS) {
//                mRegId = cmdArg1;
//                log = context.getString(R.string.register_success);
//            } else {
//                log = context.getString(R.string.register_fail);
//            }
//        } else {
//            log = message.getReason();
//        }
//
//        Message msg = Message.obtain();
//        msg.obj = log;
//        DemoApplication.getHandler().sendMessage(msg);
    }

    @SuppressLint("SimpleDateFormat")
    private static String getSimpleDate() {
        return new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date());
    }


}
