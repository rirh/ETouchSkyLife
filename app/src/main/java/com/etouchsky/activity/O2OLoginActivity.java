package com.etouchsky.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongCallbackProxy;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDK;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.dongsdk.PushInfo;
import com.ddclient.jnisdk.InfoUser;
import com.ddclient.push.DongPushMsgManager;
import com.etouchsky.GViewerXApplication;
import com.etouchsky.data.CommunitiesInitData;
import com.etouchsky.pojo.AFragmentIndexPostInfos;
import com.etouchsky.pojo.HouseCod;
import com.etouchsky.pojo.HouseCods;
import com.etouchsky.pojo.SameCod;
import com.etouchsky.pojo.UserCommunities;
import com.etouchsky.pojo.UserInfo;
import com.etouchsky.pojo.UserUpdateType;
import com.etouchsky.util.CacheUtils;
import com.etouchsky.util.HttpUtil;
import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.widget.WarnDialog;
import com.etouchsky.wisdom.MainActivity;
import com.etouchsky.wisdom.R;
import com.gViewerX.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/31 0031.
 */

public class O2OLoginActivity extends Activity {

    @ViewInject(R.id.o2o_bt_login)
    private Button btLogin;
    @ViewInject(R.id.o2o_et_user_name)
    private EditText etUserName;
    @ViewInject(R.id.o2o_et_password)
    private EditText etPassword;
    @ViewInject(R.id.o2o_bt_register)
    private TextView btRegister;
    @ViewInject(R.id.o2o_user_login_title)
    private RelativeLayout back;

    public static boolean callFlag = false;
    private ProgressDialog mProgress;
    private String userName, password;
    private SameCod userMessageSameCod;
    private Intent intent = new Intent();
    private Gson gson = new Gson();
    private Type type;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private UserInfo userInfo;
    private String tgt = "";
    private String ticket = "";
    private Message message;
    private Matcher matcher;
    public static WebView wb;
    private AFragmentIndexPostInfos phone;
    private HouseCod cod;
    private LoginActivityDongAccountProxy mDongAccountProxy = new LoginActivityDongAccountProxy();
    private HouseCods<UserCommunities> userResult;
    //记录用户首次点击返回键的时间
    private long firstTime = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.o2o_user_login);
        DongSDK.initializePush(O2OLoginActivity.this, DongPushMsgManager.PUSH_TYPE_ALL);  //初始化所有推送

        initView();
    }



    @Override
    protected void onResume() {
        super.onResume();
        DongSDKProxy.registerAccountCallback(mDongAccountProxy);
        LogUtils.i("MainActivity.clazz--->>>onResume.....deviceList:");


    }


    void initView() {
        ViewUtils.inject(this);
        wb = new WebView(O2OLoginActivity.this);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = etUserName.getText().toString();
                password = etPassword.getText().toString();
                phone = new AFragmentIndexPostInfos(etUserName.getText().toString());
                mProgress = ProgressDialog.show(O2OLoginActivity.this, "",
                        getString(R.string.logging), true, true);

                getTGT();
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.setClass(O2OLoginActivity.this, O2ORegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                intent.setClass(O2OLoginActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            firstTime = secondTime;
        } else {

            finish();

        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x01) {
                mProgress.dismiss();
                Toast.makeText(O2OLoginActivity.this, "对不起您的账号密码错误", Toast.LENGTH_SHORT).show();
            }
            if (msg.what == 0x02) {
//                获取用户信息后加载o2o用户信息
                removewWebViewCookie();
                wb.loadUrl("http://www.etouchme.com:89/mobile/index.php?m=user&ticket=" + ticket);
                wb.setWebViewClient(new WebViewClient());  //目的是在当前web view内跳转不要跳转至网页
                UserUpdateType type = new UserUpdateType(userName, 1, null, 1);
                CacheUtils.putBoolean(O2OLoginActivity.this, CacheUtils.IS_OPEN_LOGIN_PAGE, true); //是否登录的信息
                CacheUtils.putName(O2OLoginActivity.this, CacheUtils.USER_NAME, userInfo.userName); //用户的名称
                CacheUtils.putUserId(O2OLoginActivity.this, CacheUtils.USER_ID, userInfo.userId);  //O2o的ID
                CacheUtils.putAccount(O2OLoginActivity.this, CacheUtils.USER_ACCOUNT, userName);   //此处为获取用户的物业平台手机号码  "13750509674"
                //移动端类型
                OkAndGsonUtil.doHousePost(OkAndGsonUtil.gson.toJson(type), HttpUtil.UPDATE_TYPE).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                    }
                });
                //获取小区信息
                OkAndGsonUtil.doHousePost(OkAndGsonUtil.gson.toJson(phone), HttpUtil.GET_COMMUNITIES).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        setCommunities(response.body().string());
                    }
                });
            }
            if (msg.what == 0x03) {
                Log.e("****", "0x03");
                DongSDKProxy.initDongAccount(mDongAccountProxy);   //初始化电话呼叫
                DongSDKProxy.login(userName, "123456");
//                Log.e("******", "云对讲号码" + CacheUtils.getAccount(O2OLoginActivity.this, CacheUtils.USER_ACCOUNT, "000"));
            }
        }
    };


    //保存小区信息
    private void setCommunities(String jsonMessage) {
        Log.e("****", "小区数据:" + jsonMessage);
        type = new TypeToken<HouseCods<UserCommunities>>() {
        }.getType();
        userResult = gson.fromJson(jsonMessage, type);
        List<UserCommunities> userResultInfo = userResult.getCommunities();
        new CommunitiesInitData(this, userResultInfo);
        OkAndGsonUtil.doHousePost(OkAndGsonUtil.gson.toJson(phone), HttpUtil.IS_CHECKUSER).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                        String sd = ;
                cod = OkAndGsonUtil.gson.fromJson(response.body().string(), HouseCod.class);
                Log.e("*****云对讲判断码", "COD:" + cod.getRescode() + cod.getDesc() + CacheUtils.getAccount(O2OLoginActivity.this, CacheUtils.USER_ACCOUNT, "000"));
                if (cod.getRescode() == 0) {
//                    //给二次登录设值
//                    GViewerXApplication.preferences_editor.putString("account", CacheUtils.getAccount(O2OLoginActivity.this, CacheUtils.USER_ACCOUNT, "000")).putBoolean("flagLogin", true).putString("pwd", "123456").commit();
                    Message message = Message.obtain();
                    message.what = 0x03;
                    handler.sendMessage(message);
                } else {
                    CacheUtils.putHouseCod(O2OLoginActivity.this, CacheUtils.HOUSE_COD, false);
                    GViewerXApplication.preferences_editor.putBoolean("flagLogin", false).commit();
                    intent.setClass(O2OLoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }


            }
        });
    }


    private void getUserInfo() {
        OkAndGsonUtil.doGet(HttpUtil.O2O_USER_INFO + "&user_name=" + userName).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.what = 0x01;
                handler.sendMessage(message);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                initData(response.body().string());

            }
        });
    }

    private void initData(String jsonData) {
        Log.e("+++++++++++", jsonData);
        type = new TypeToken<SameCod<UserInfo>>() {
        }.getType();
        userMessageSameCod = gson.fromJson(jsonData, type);
        userInfo = (UserInfo) userMessageSameCod.info;
//        Log.e("+++++",userMessageSameCod.result);
        if (userMessageSameCod.result.equals("success")) {
            message = handler.obtainMessage();
            message.what = 0x02;//执行webview的loadURL指令
            handler.sendMessage(message);
        } else {
            message = handler.obtainMessage();
            message.what = 0x01;
            handler.sendMessage(message);
        }
    }

    //    请求TGT
    private void getTGT() {
        FormBody formBody = new FormBody.Builder()
                .add("password", password)
                .add("username", userName)
                .build();
        Request request = new Request.Builder().url("http://www.etouchme.com:6060/usercas/v1/tickets").post(formBody).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                message = handler.obtainMessage();
                message.what = 0x01;
                handler.sendMessage(message);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (201 == response.code()) {
                    String bodyContent = response.body().string().toString();
                    matcher = Pattern.compile(".*action=\".*/(.*?)\".*").matcher(bodyContent);
                    if (matcher.matches()) {
                        tgt = matcher.group(1);
                        Log.e("*******你的TGT", "Your TGT is :" + tgt);
                        getTicket();
                    }
                } else {
                    message = handler.obtainMessage();
                    message.what = 0x01;
                    handler.sendMessage(message);
                }
            }
        });
    }


    //        请求ticket
    private void getTicket() {
        RequestBody formBody = new FormBody.Builder().add("service", "http://www.etouchme.com:89/mobile/index.php?m=user").build();
        Request request = new Request.Builder().post(formBody).url("http://www.etouchme.com:6060/usercas/v1/tickets" + "/" + tgt).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ticket = response.body().string();
                if (ticket != null) {
                    Log.e("*******ticket", "Your ticket is :" + ticket);
                    getUserInfo();
                }
            }
        });
    }


    private void removewWebViewCookie() {
        CookieSyncManager.createInstance(getApplication());  //Create a singleton CookieSyncManager within a context
        CookieManager cookieManager = CookieManager.getInstance(); // the singleton CookieManager instance
        cookieManager.removeAllCookie();// Removes all cookies.
        CookieSyncManager.getInstance().sync(); // forces sync manager to sync now
//
//        O2OLoginActivity.wb.setWebChromeClient(null);
//        O2OLoginActivity.wb.setWebViewClient(null);
//        O2OLoginActivity.wb.getSettings().setJavaScriptEnabled(false);
//        O2OLoginActivity.wb.clearCache(true);
    }


    private class LoginActivityDongAccountProxy extends AbstractDongCallbackProxy.DongAccountCallbackImp {
        @Override
        public int onAuthenticate(InfoUser tInfo) {
            Log.e("****", "tInfo" + tInfo);
            DongConfiguration.mUserInfo = tInfo;
            LogUtils.i("Login.clazz--->>>onAuthenticate........tInfo:"
                    + tInfo);
            if (mProgress != null)
                mProgress.dismiss();
            DongSDKProxy.requestSetPushInfo(PushInfo.PUSHTYPE_FORCE_ADD);
            DongSDKProxy.requestGetDeviceListFromPlatform();
            GViewerXApplication.preferences_editor.putString("account", userName).putBoolean("flagLogin", true).putString("pwd", "123456").commit();
            CacheUtils.putHouseCod(O2OLoginActivity.this, CacheUtils.HOUSE_COD, true);
            Intent intent = new Intent(O2OLoginActivity.this, MainActivity.class);
            startActivity(intent);
            O2OLoginActivity.this.finish();
            return 0;
        }

        @Override
        public int onUserError(int nErrNo) {
            LogUtils.i("LoginActivity.clazz--->>>onUserError........nErrNo:"
                    + nErrNo);
            Log.e("****", "nErrNo" + nErrNo);
            WarnDialog.showDialog(O2OLoginActivity.this, mProgress, nErrNo);
            DongSDK.reInitDongSDK();
            return 0;
        }

        /**
         * 平台在线推送时回调该方法
         */
        @Override
        public int onCall(ArrayList<DeviceInfo> list) {
            LogUtils.i("ETService.clazz--->>>onCall........list.size():" + list.size());
            //Toast.makeText(ETService.this, "ETService平台推送到达!!!", Toast.LENGTH_SHORT).show();
            int size = list.size();
            if (size > 0) {
                DeviceInfo deviceInfo = list.get(0);
                String message = deviceInfo.deviceName + deviceInfo.dwDeviceID
                        + deviceInfo.msg;
                DongPushMsgManager.pushMessageChange(O2OLoginActivity.this, deviceInfo.toString());
                DongConfiguration.mDeviceInfo = deviceInfo;
                //getApplicationContext().startActivity(new Intent(ETService.this,
                //       VideoViewActivity.class));
                Intent mintent = new Intent(O2OLoginActivity.this, VideoViewActivity.class);
                mintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                //mintent.setClassName(ETService.this, VideoViewActivity.class);
                mintent.putExtra("call", "");
                getApplicationContext().startActivity(mintent);
                O2OLoginActivity.this.finish();
            }
            return 0;
        }
    }

}
