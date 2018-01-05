package com.etouchsky.wisdom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.etouchsky.activity.O2OLoginActivity;
import com.etouchsky.bean.Business;
import com.etouchsky.util.CacheUtils;

import java.util.List;

public class TabCFm_web extends Fragment {
    List<Business> business;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        System.out.println("CCCCCCCCCC____onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("CCCCCCCCCC____onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("CCCCCCCCCC____onCreateView");
        return inflater.inflate(R.layout.tab_c_web, container, false);
    }

    private ProgressDialog dialog;

    public void showProgressDialog() {
        if (dialog == null)
            dialog = ProgressDialog.show(TabCFm_web.this.getActivity(), "加载中...", "正在加载数据。。。。，请稍后！", false, true);
    }

    public void closeProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        O2OLoginActivity.wb = (WebView) this.getActivity().findViewById(R.id.web_c);
        WebSettings webSettings = O2OLoginActivity.wb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //启用数据库
        webSettings.setDatabaseEnabled(true);
//设置定位的数据库路径
        String dir = this.getActivity().getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setGeolocationDatabasePath(dir);
//启用地理定位
        webSettings.setGeolocationEnabled(true);
        /**
         * 此处很重要，必须要
         */
//开启DomStorage缓存
        webSettings.setDomStorageEnabled(true);
//配置权限
        // 设置 缓存模式
        // 开启 Application Caches 功能
        webSettings.setAppCacheEnabled(true);
        //if (NetUtils.isNetworkAvailable(TabCFm_web.this)) {
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // } else {
        // webSettings.setCacheMode(
        //        WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //  }
        //webSettings.setBlockNetworkImage(true);// 把图片加载放在最后来加载渲染
        O2OLoginActivity.wb.setWebChromeClient(new WebChromeClient() {
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


        O2OLoginActivity.wb.loadUrl("http://www.etouchme.com:89/mobile");
        O2OLoginActivity.wb.setWebViewClient(new WebViewClient() {
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
        O2OLoginActivity.wb.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && O2OLoginActivity.wb.canGoBack()) {
                    O2OLoginActivity.wb.goBack();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("CCCCCCCCCC____onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("CCCCCCCCCC____onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("CCCCCCCCCC____onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("CCCCCCCCCC____onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        O2OLoginActivity.wb.removeAllViews();
        O2OLoginActivity.wb.destroy();
        System.out.println("CCCCCCCCCC____onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("CCCCCCCCCC____onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("CCCCCCCCCC____onDetach");
    }


}
