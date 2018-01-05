package com.etouchsky.o2o;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.etouchsky.wisdom.R;
import com.etouchsky.wisdom.TabBFm_web;

/**
 * Created by Administrator on 2017/9/6 0006.
 */

public class MyShareActivity extends Activity {
    private WebView wb;
    private ProgressDialog dialog;
    private int choice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_b_web);
//        setContentView(R.layout.o2o_my_share_layout);
        choice = getIntent().getIntExtra("MyShare",0);
        initView();
    }

    private void initView() {
        wb = (WebView) this.findViewById(R.id.web_b);
        WebSettings webSettings = wb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //启用数据库
        webSettings.setDatabaseEnabled(true);
        //设置定位的数据库路径
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setGeolocationDatabasePath(dir);
        //启用地理定位

        //开启DomStorage缓存
        webSettings.setDomStorageEnabled(true);
        //配置权限
        wb.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);

            }

        });
        switch (choice){
            case 0:
                wb.loadUrl("http://www.etouchme.com:89/mobile/index.php?m=user&a=affiliate");
                break;
            case 1:
                wb.loadUrl("http://www.etouchme.com:89/mobile/index.php?m=user&a=userhelp");
                break;

        }

        wb.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showProgressDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                closeProgressDialog();
            }
        });
        wb.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && wb.canGoBack()) {
                    wb.goBack();
                    return true;
                }
                return false;
            }
        });


    }

    private void showProgressDialog() {
        if (dialog == null)
            dialog = ProgressDialog.show(this, "加载中...", "正在加载数据。。。。，请稍后！", false, true);
    }

    private void closeProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
