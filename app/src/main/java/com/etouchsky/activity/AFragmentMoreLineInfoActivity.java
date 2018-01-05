package com.etouchsky.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etouchsky.pojo.AFragmentAdviceInfo;
import com.etouchsky.pojo.AMoreLineInfo;
import com.etouchsky.util.HttpUtil;
import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.wisdom.R;

import java.io.IOException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/9/30 0030.
 */

public class AFragmentMoreLineInfoActivity extends Activity {

    private WebView message;
    private TextView title,beginTime,endTime,name;
    private ProgressDialog dialog;
    private AMoreLineInfo info;
    private LinearLayout llContent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_fragment_more_line_info);
        initView();
    }

    private void initView() {
        dialog = OkAndGsonUtil.showProgressDialog(this);
        OkAndGsonUtil.setTitleBar(this,"通知详情");
        title = (TextView) findViewById(R.id.more_line_content_title);
        message = (WebView) findViewById(R.id.more_line_content_message);
        beginTime = (TextView) findViewById(R.id.more_line_content_begin_time);
        endTime = (TextView) findViewById(R.id.more_line_content_end_time);
        llContent = (LinearLayout) findViewById(R.id.more_line_content_ll);
        name    = (TextView) findViewById(R.id.more_line_content_name);
        OkAndGsonUtil.doHousePost("{\"noticeID\": \""+getIntent().getIntExtra("noticeID",0)+"\"}", HttpUtil.HOUSE_MORE_LINE).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = Message.obtain();
                message.what = 0x02;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                initData(response.body().string());
            }
        });
    }

    private void initData(String jsonMessage) {
        Log.e("****",jsonMessage);
        info = OkAndGsonUtil.gson.fromJson(jsonMessage,AMoreLineInfo.class);
        Message message = Message.obtain();
        message.what = 0x01;
        handler.sendMessage(message);

    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x01){
                OkAndGsonUtil.closeProgressDialog(dialog);
                title.setText(info.getNoticetitle());
                beginTime.setText("起始日期:"+OkAndGsonUtil.timeStamp2Date(info.getNoticetime(),null).substring(0,11));
                endTime.setText("结束日期:"+OkAndGsonUtil.timeStamp2Date(info.getNoticeexpiredtime(),null).substring(0,11));
               name.setText(info.getCommunityname());
                String html = info.getNoticecontent();
//                message.setMovementMethod(ScrollingMovementMethod.getInstance());// 设置可滚动
//                message.setMovementMethod(LinkMovementMethod.getInstance());//设置超链接可以打开网页
//                message.setText(Html.fromHtml(html,imgGetter,null));
                message.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
                message.getSettings().setJavaScriptEnabled(true);
                message.setWebChromeClient(new WebChromeClient());
            }else {

            }
        }
    };



}
