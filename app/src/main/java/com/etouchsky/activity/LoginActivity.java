package com.etouchsky.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongCallbackProxy.DongAccountCallbackImp;
import com.ddclient.dongsdk.DongSDK;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.dongsdk.PushInfo;
import com.ddclient.jnisdk.InfoUser;
import com.ddclient.push.DongPushMsgManager;
import com.etouchsky.widget.WarnDialog;
import com.etouchsky.wisdom.R;
import com.gViewerX.util.LogUtils;

public class LoginActivity extends Activity implements OnClickListener {

    private TextView mTvForgetPwd;
    private TextView mTvRegister;
    private TextView mNativeDevice;
    private Button mBtLogin;
    private ProgressDialog mProgress;
    private EditText mUsername, mPwd;

    LoginActivityDongAccountProxy mDongAccountProxy = new LoginActivityDongAccountProxy();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化离线推送
        DongSDK.initializePush(this, DongPushMsgManager.PUSH_TYPE_ALL);

        mTvForgetPwd = (TextView) findViewById(R.id.tv_forgetpwd);
        mTvRegister = (TextView) findViewById(R.id.tv_registaccount);
        mNativeDevice = (TextView) findViewById(R.id.tv_nativedevice);
        mBtLogin = (Button) findViewById(R.id.tv_login);
        mUsername = (EditText) findViewById(R.id.et_username);
        mPwd = (EditText) findViewById(R.id.et_password);

        mTvForgetPwd.setOnClickListener(this);
        mTvRegister.setOnClickListener(this);
        mNativeDevice.setOnClickListener(this);
        mBtLogin.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        DongSDKProxy.registerAccountCallback(mDongAccountProxy);
        mUsername.setText("13971703884");
        mPwd.setText("123456");
//        if (LogUtils.isDebug) {
//            mTvForgetPwd.setVisibility(View.GONE);
//            mTvRegister.setVisibility(View.GONE);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        DongSDKProxy.unRegisterAccountCallback(mDongAccountProxy);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void ipLogin(View v) {
        LogUtils.i("LoginActivity.clazz--->>>ipLogin........ 111111:" );
        startActivity(new Intent(this, IPLoginActivity.class));
        LogUtils.i("LoginActivity.clazz--->>>ipLogin........ 2222222222:" );
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_forgetpwd:
                startActivity(new Intent(this, ForgetPwdActivity.class));
                break;
            case R.id.tv_registaccount:
                startActivity(new Intent(this, RegistActivity.class));
                break;
            case R.id.tv_nativedevice:
                startActivity(new Intent(this, SerchLocalDeviceSetting.class));
                break;
            case R.id.tv_login:
                String name = mUsername.getText().toString();
                String pwd = mPwd.getText().toString();
                mProgress = ProgressDialog.show(this, "",
                        getString(R.string.logging), true, true);
                boolean initDongAccount = DongSDKProxy.initCompleteDongAccount();
                DongSDKProxy.initDongAccount(mDongAccountProxy);
                DongSDKProxy.login(name, pwd);
                LogUtils.i("LoginActivity.clazz--->>>tv_login........ initDongAccount:" + initDongAccount);
                break;
        }
    }

    private class LoginActivityDongAccountProxy extends DongAccountCallbackImp {

        @Override
        public int onAuthenticate(InfoUser tInfo) {
            DongConfiguration.mUserInfo = tInfo;
            LogUtils.i("LoginActivity.clazz--->>>onAuthenticate........tInfo:"
                    + tInfo);
            mProgress.dismiss();
            DongSDKProxy.requestSetPushInfo(PushInfo.PUSHTYPE_FORCE_ADD);
            DongSDKProxy.requestGetDeviceListFromPlatform();
            startActivity(new Intent(LoginActivity.this, DeviceListActivity.class));
            LoginActivity.this.finish();
            return 0;
        }

        @Override
        public int onUserError(int nErrNo) {
            LogUtils.i("LoginActivity.clazz--->>>onUserError........nErrNo:"
                    + nErrNo);
            WarnDialog.showDialog(LoginActivity.this, mProgress, nErrNo);
            DongSDK.reInitDongSDK();
            return 0;
        }
    }
}
