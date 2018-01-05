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

public class RegistActivity extends Activity implements OnClickListener {

    private Button mBtGetCode;
    private EditText mEtPhoneNumber;
    private ProgressDialog mProgress;
    private LinearLayout mLlBack;
    private int mRandomCode;
    private RegisterActivityDongRegisterProxy mDongRegisterProxy
            = new RegisterActivityDongRegisterProxy();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        mEtPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        mLlBack = (LinearLayout) findViewById(R.id.ll_back);
        mBtGetCode = (Button) findViewById(R.id.bt_get_code);

        mLlBack.setOnClickListener(this);
        mBtGetCode.setOnClickListener(this);
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_get_code:
                if (TextUtils.isEmpty(mEtPhoneNumber.getText().toString())) {
                    Toast.makeText(this, this.getString(R.string.empty),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                mProgress = ProgressDialog
                        .show(this, "",
                                this.getString(R.string.getVerificationCodeing),
                                true, true);
                mRandomCode = (new Random().nextInt(999999) + 100000);
                String phoneNumber = mEtPhoneNumber.getText().toString();
                DongConfiguration.mPhoneNumber = phoneNumber;
                DongSDKProxy.requestQueryUser(phoneNumber);
                LogUtils.i("RegistActivity.clazz--->>>bt_get_code........mRandomCode:"
                        + mRandomCode + ",phoneNumber:" + phoneNumber);
                break;
            case R.id.ll_back:
                finish();
                break;
        }
    }

    private class RegisterActivityDongRegisterProxy extends
            DongRegisterCallbackImp {

        @Override
        public int onQueryUser(int nReason) {
            if (nReason == 0) {// 未注册过
                // 用来发短信，接收验证码
                DongSDKProxy.requestSmsAuth(mRandomCode + "",
                        DongConfiguration.mPhoneNumber);

            } else {
                Toast.makeText(
                        RegistActivity.this,
                        RegistActivity.this
                                .getString(R.string.phoneRegisteredYes),
                        Toast.LENGTH_SHORT).show();
                mProgress.dismiss();
            }
            LogUtils.i("RegistActivity.clazz--->>>onQueryUser........nReason:"
                    + nReason);
            return 0;
        }

        @Override
        public int onSmsAuth(int nReason) {
            LogUtils.i("RegistActivity.clazz--->>>onSmsAuth...1111.....nReason:"
                    + nReason);
            if (nReason == 0) {
                if (mProgress != null) {
                    mProgress.dismiss();
                }
                Intent intent = new Intent(RegistActivity.this, VerificationCodeActivity.class);
                intent.putExtra("randomCode", mRandomCode);
                startActivity(intent);
                RegistActivity.this.finish();
            } else {
                Toast.makeText(RegistActivity.this,
                        RegistActivity.this.getString(R.string.verFail),
                        Toast.LENGTH_SHORT).show();
                mProgress.dismiss();
            }
            LogUtils.i("RegistActivity.clazz--->>>onSmsAuth........nReason:"
                    + nReason);
            return 0;
        }

        @Override
        public int onRegisterError(int nErrNo) {
            WarnDialog.showDialog(RegistActivity.this, mProgress, nErrNo);
            LogUtils.i("RegistActivity.clazz--->>>onRegisterError........nReason:"
                    + nErrNo);
            return 0;
        }
    }

}
