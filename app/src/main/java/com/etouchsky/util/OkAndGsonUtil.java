package com.etouchsky.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.etouchsky.wisdom.R;
import com.etouchsky.wisdom.TabBFm_web;
import com.google.gson.Gson;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/8/30 0030.
 * 加入了常用的工具方法
 */

public class OkAndGsonUtil {
    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static Request request;  //请求对象
    private static okhttp3.Call call; //呼叫对象
    public static Gson gson = new Gson();
    private static MediaType json = MediaType.parse("application/json; charset=utf-8");    //设置json字符的编码格式
    private static RequestBody body;


//    o2o商城get方法
    public static okhttp3.Call doGet(String httpUrl) {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        request = new Request.Builder().get().url(httpUrl).build();
        call = okHttpClient.newCall(request);
        return call;
    }

    //设置标题功能
    public static void setTitleBar(final Activity activity, String titleName){
        ImageView imageView = (ImageView) activity.findViewById(R.id.o2o_title_left);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        TextView textView = (TextView)  activity.findViewById(R.id.private_message_title);
        textView.setText(titleName);
    }
    //设置小区标题功能
    public static void setHouseTitleBar(final Activity activity, String titleName){
        ImageView imageView = (ImageView) activity.findViewById(R.id.o2o_title_left);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        TextView textView = (TextView)  activity.findViewById(R.id.private_message_title);
        textView.setText(titleName);
    }

    //小区数据传输方法
    public static Call doHousePost(String jsonObject, String url) {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        body = RequestBody.create(json,jsonObject);     //数据体
        Request builder = new Request.Builder().url(url).post(body).build();
        call = okHttpClient.newCall(builder);
        return call;
    }


    //    重启activity
    public static void refreshActivity(Activity activity) {
        Intent intent = activity.getIntent();
        activity.overridePendingTransition(0, 0);
        activity.finish();
        activity.overridePendingTransition(0, 0);
        activity.startActivity(intent);
    }


    public static String timeStamp2Date(String seconds,String format) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
            return "";
        }
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds+"000")));
    }


    public static ProgressDialog showProgressDialog(Context context) {
        ProgressDialog dialog = null;
        if (dialog == null)
            dialog = ProgressDialog.show(context, "加载中...", "正在加载数据。。。。，请稍后！", false, true);
        return dialog;
    }
    public static ProgressDialog showProgressDialog(Context context,String title,String message) {
        ProgressDialog dialog = null;
        if (dialog == null)
            dialog = ProgressDialog.show(context, title, message, false, true);
        return dialog;
    }
    public static void closeProgressDialog( ProgressDialog dialog) {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

}
