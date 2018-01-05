package com.etouchsky.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongCallbackProxy.DongRegisterCallbackImp;
import com.ddclient.dongsdk.DongSDKProxy;
import com.etouchsky.widget.WarnDialog;
import com.etouchsky.wisdom.R;
import com.gViewerX.util.LogUtils;

import java.util.Random;

public class ForgetPwdActivity extends Activity implements OnClickListener {

    private Button mBtGetCode;
    private ProgressDialog mProgress;
    private int mRandomCode;
    private LinearLayout mLlBack;
    private EditText mEtPhoneNumber;
    private ForgetPwdActivityDongRegisterProxy
            mDongRegisterProxy = new ForgetPwdActivityDongRegisterProxy();
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);

        mBtGetCode = (Button) findViewById(R.id.bt_get_code);
        mEtPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        mLlBack = (LinearLayout) findViewById(R.id.ll_back);

        mBtGetCode.setOnClickListener(this);
        mLlBack.setOnClickListener(this);
        boolean initedDongRegister = DongSDKProxy.initCompleteDongRegister();
        if (!initedDongRegister) {
            DongSDKProxy.intDongRegister(mDongRegisterProxy);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DongSDKProxy.registerDongRegisterCallback(mDongRegisterProxy);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DongSDKProxy.unRegisterDongRegisterCallback(mDongRegisterProxy);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.bt_get_code:
                String phoneNum = mEtPhoneNumber.getText().toString();
                if (TextUtils.isEmpty(phoneNum)) {
                    Toast.makeText(this, getString(R.string.empty),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                mProgress = ProgressDialog.show(this, "",
                        getString(R.string.getVerificationCodeing), true, true);
                mRandomCode = (new Random().nextInt(999999) + 100000);
                DongConfiguration.mPhoneNumber = phoneNum;
                DongSDKProxy.requestQueryUser(phoneNum);
                break;
        }
    }

    private class ForgetPwdActivityDongRegisterProxy extends
            DongRegisterCallbackImp {
        @Override
        public int onQueryUser(int nReason) {
            if (nReason == 0) {// 已经被注册过
                Toast.makeText(
                        ForgetPwdActivity.this,
                        ForgetPwdActivity.this
                                .getString(R.string.phoneRegisteredNO),
                        Toast.LENGTH_SHORT).show();
                mProgress.dismiss();
            } else {
                // 发短信接收验证码
                DongSDKProxy.requestSmsAuth(mRandomCode + "",
                        DongConfiguration.mPhoneNumber);
            }
            LogUtils.i("ForgetPwdActivity.clazz--->>>onQueryUser........nReason:"
                    + nReason);
            return 0;
        }

        @Override
        public int onSmsAuth(int nReason) {
            if (nReason == 0) {
                mProgress.dismiss();
                Intent intent = new Intent(ForgetPwdActivity.this,
                        VerificationCodeActivity.class);
                intent.putExtra("randomCode", mRandomCode);
                startActivity(intent);
                ForgetPwdActivity.this.finish();
            } else {
                Toast.makeText(
                        ForgetPwdActivity.this,
                        ForgetPwdActivity.this
                                .getString(R.string.againReseartVerificationCode),
                        Toast.LENGTH_SHORT).show();
                mProgress.dismiss();
            }
            LogUtils.i("ForgetPwdActivity.clazz--->>>onSmsAuth........nReason:"
                    + nReason);
            return 0;
        }

        @Override
        public int onRegisterError(int nErrNo) {
            LogUtils.i("ForgetPwdActivity.clazz--->>>onRegisterError........nErrNo:"
                    + nErrNo);
            WarnDialog.showDialog(ForgetPwdActivity.this, mProgress, nErrNo);
            return 0;
        }
    }

}
