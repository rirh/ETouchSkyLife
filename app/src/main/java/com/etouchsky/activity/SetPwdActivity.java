package com.etouchsky.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.etouchsky.wisdom.R;
import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongCallbackProxy.DongRegisterCallbackImp;
import com.ddclient.dongsdk.DongSDKProxy;
import com.gViewerX.util.LogUtils;

public class SetPwdActivity extends Activity implements OnClickListener {
    private Button mBtDone;
    private EditText mEtPwd;
    private ProgressDialog mProgress;
    private LinearLayout mLlBack;

    private SetPasswordActivityDongRegisterProxy mRegisterProxy
            = new SetPasswordActivityDongRegisterProxy();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pwd);

        mEtPwd = (EditText) findViewById(R.id.et_password);
        mLlBack = (LinearLayout) findViewById(R.id.ll_back);
        mBtDone = (Button) findViewById(R.id.bt_done);

        mBtDone.setOnClickListener(this);
        mLlBack.setOnClickListener(this);
        boolean initDongRegister = DongSDKProxy.initCompleteDongRegister();

        if (!initDongRegister) {
            DongSDKProxy.intDongRegister(mRegisterProxy);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DongSDKProxy.registerDongRegisterCallback(mRegisterProxy);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DongSDKProxy.unRegisterDongRegisterCallback(mRegisterProxy);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_done:
                mProgress = ProgressDialog.show(SetPwdActivity.this, "",
                        SetPwdActivity.this
                                .getString(R.string.setPasswording), true, true);
                DongSDKProxy.requestSetSecret(mEtPwd.getText().toString(),
                        DongConfiguration.mPhoneNumber);
                break;
            case R.id.ll_back:
                finish();
                break;
        }
    }

    private class SetPasswordActivityDongRegisterProxy extends
            DongRegisterCallbackImp {

        @Override
        public int onSetSecret(int nReason) {
            if (nReason == 0) {
                Toast.makeText(
                        SetPwdActivity.this,
                        SetPwdActivity.this
                                .getString(R.string.passwordsucc),
                        Toast.LENGTH_SHORT).show();
                mProgress.dismiss();
                SetPwdActivity.this.finish();
            } else {
                Toast.makeText(
                        SetPwdActivity.this,
                        SetPwdActivity.this
                                .getString(R.string.passwordfail),
                        Toast.LENGTH_SHORT).show();
                mProgress.dismiss();
            }
            LogUtils.i("SetPwdActivity.clazz--->>>onSetSecret........nReason:"
                    + nReason);
            return 0;
        }

        @Override
        public int onRegisterError(int nErrNo) {
            LogUtils.i("SetPwdActivity.clazz--->>>onRegisterError........nErrNo:" + nErrNo);
            return 0;
        }
    }

}
