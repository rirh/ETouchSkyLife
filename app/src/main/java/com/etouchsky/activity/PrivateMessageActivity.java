package com.etouchsky.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ddclient.dongsdk.DongSDK;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.dongsdk.PushInfo;
import com.etouchsky.GViewerXApplication;
import com.etouchsky.o2o.AttestationActivity;
import com.etouchsky.o2o.UpDataUserInfo;
import com.etouchsky.o2o.UserAddressActivity;
import com.etouchsky.pojo.SameCod;
import com.etouchsky.pojo.UserInfos;
import com.etouchsky.util.CacheUtils;
import com.etouchsky.util.DataUtil;
import com.etouchsky.util.HttpUtil;
import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.wisdom.MainActivity;
import com.etouchsky.wisdom.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/28 0028.
 * 个人信息界面
 */

public class PrivateMessageActivity extends Activity implements View.OnClickListener {

    @ViewInject(R.id.private_message_re_user_name)
    private TextView userName;
    @ViewInject(R.id.private_message_tv_name)
    private TextView reName;
    @ViewInject(R.id.private_message_tv_sex)
    private TextView tvSex;
    @ViewInject(R.id.private_message_tv_data)
    private TextView tvData;
    @ViewInject(R.id.private_message_tv_attestation)
    private TextView tvAttestation;
    @ViewInject(R.id.private_message_back)
    private ImageView back;
    @ViewInject(R.id.private_message_tv_quit)
    private TextView quit;
    @ViewInject(R.id.private_message_iv_head_portrait)
    private ImageView mPicture;
    @ViewInject(R.id.private_message_re_name)
    private RelativeLayout nickName;
    @ViewInject(R.id.private_message_re_head_portrait)
    private RelativeLayout userNameRe;
    @ViewInject(R.id.private_message_re_adress)
    private RelativeLayout userAdress;
    @ViewInject(R.id.private_message_re_attestation)
    private RelativeLayout attestation;
    private UserInfos userInfo;
    private SameCod userMessageSameCod;
    private Gson gson = new Gson();
    private Type type;
    private Message message = new Message();
    private Intent intent = new Intent();
    private SQLiteDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_message_activity_layout);
        initView();
    }


    private void initView() {
        ViewUtils.inject(this);
        back.setOnClickListener(this);
        quit.setOnClickListener(this);
        nickName.setOnClickListener(this);
        userNameRe.setOnClickListener(this);
        userAdress.setOnClickListener(this);
        attestation.setOnClickListener(this);
        OkAndGsonUtil.doGet(HttpUtil.O2O_USER_INFO + "&user_id=" + CacheUtils.getUserId(this, CacheUtils.USER_ID, 0)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                Log.e("++++++++", response.toString());
                initData(response.body().string());

            }
        });
    }

    private void initData(String jsonData) {
//        Log.e("+++++++++++", jsonData);
        type = new TypeToken<SameCod<UserInfos>>() {
        }.getType();
        userMessageSameCod = gson.fromJson(jsonData, type);
        userInfo = (UserInfos) userMessageSameCod.info;
        message.what = 0x01;
        handler.sendMessage(message);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x01) {
                userName.setText(userInfo.getUserName().toString());
                reName.setText(userInfo.getNickName().toString());
                tvSex.setText(userInfo.getSex().toString());
                tvData.setText(userInfo.getBirthday().toString());
                ImageLoader.getInstance().displayImage(userInfo.getUserPicture(), mPicture);
            }
        }
    };

    private void removewWebViewCookie() {
        CookieSyncManager.createInstance(getApplication());  //Create a singleton CookieSyncManager within a context
        CookieManager cookieManager = CookieManager.getInstance(); // the singleton CookieManager instance
        cookieManager.removeAllCookie();// Removes all cookies.
        CookieSyncManager.getInstance().sync(); // forces sync manager to sync now

//        O2OLoginActivity.wb.setWebChromeClient(null);
//        O2OLoginActivity.wb.setWebViewClient(null);
//        O2OLoginActivity.wb.getSettings().setJavaScriptEnabled(false);
//        O2OLoginActivity.wb.clearCache(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.private_message_back:
                finish();
                break;
            case R.id.private_message_tv_quit:
                //退出按钮
                if (finishSDK()) {
                    CacheUtils.putBoolean(this, CacheUtils.IS_OPEN_LOGIN_PAGE, false);
                    removewWebViewCookie();
                    db = DataUtil.getSQLiteDb(PrivateMessageActivity.this);
                    Cursor cursor = db.rawQuery("select * from CommunitiesInfo where Id = 1", null);
                    cursor.moveToNext();
                    MiPushClient.unsubscribe(PrivateMessageActivity.this, cursor.getString(cursor.getColumnIndex("CmtID")), null);
                    db.execSQL("DELETE FROM CommunitiesInfo ");
                    db.close();
                    if (MainActivity.mainActivity != null)
                        MainActivity.mainActivity.finish();
                    intent.setClass(PrivateMessageActivity.this, O2OLoginActivity.class);
                    startActivity(intent);
                    PrivateMessageActivity.this.finish();
                    System.exit(0);
                }
                break;
            case R.id.private_message_re_name:
//                更新用户个人信息
//                intent.setClass(this, UpDataUserInfo.class);
//                startActivity(intent);
                break;
            case R.id.private_message_re_head_portrait:
                Toast.makeText(PrivateMessageActivity.this, "慧e家会员名作为登录名不可修改~", Toast.LENGTH_SHORT).show();
                break;
            case R.id.private_message_re_adress:
                //收货地址管理
                intent.setClass(this, UserAddressActivity.class);
                startActivity(intent);
                break;
            case R.id.private_message_re_attestation:
                //用户认证配置
                intent.setClass(this, AttestationActivity.class);
                startActivity(intent);
                break;

        }
    }

    private boolean finishSDK() {
        GViewerXApplication.preferences_editor.putBoolean("flagLogin", false).commit();
        DongSDKProxy.requestSetPushInfo(PushInfo.PUSHTYPE_DEL);
        DongSDKProxy.loginOut();
        DongSDK.finishDongSDK();
        return true;
    }
}
