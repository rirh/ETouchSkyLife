package com.etouchsky.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongCallbackProxy;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.jnisdk.InfoDevice;
import com.ddclient.jnisdk.InfoUser;
import com.gViewerX.util.LogUtils;

import static com.etouchsky.wisdom.R.id;
import static com.etouchsky.wisdom.R.layout;
import static com.etouchsky.wisdom.R.string;

public class IPLoginActivity extends Activity {

    private ProgressDialog mProgress;

    private IPLoginActivityDongAccountProxy mDongAccountProxy = new IPLoginActivityDongAccountProxy();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_ip_login);
        LogUtils.i("IPLoginActivity.clazz--->>>onCreate--->>>");
        Button btLogin = (Button) findViewById(id.bt_ip_login);
        final EditText etIP = (EditText) findViewById(id.et_ip);
        final EditText etPort = (EditText) findViewById(id.et_port);

        btLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mProgress = ProgressDialog.show(
                        IPLoginActivity.this, "",
                        IPLoginActivity.this
                                .getString(string.waitingForConnection),
                        true, true);
                DongSDKProxy.initDongAccount(mDongAccountProxy);
                String ip = etIP.getText().toString();
                short port = Short.parseShort(etPort.getText().toString());
                LogUtils.i("IPLoginActivity.clazz--->>>ip =:"
                        + ip + ",port:" + port);
                DongSDKProxy.directLogin(ip, port);
            }
        });
    }

    private class IPLoginActivityDongAccountProxy extends
            AbstractDongCallbackProxy.DongAccountCallbackImp {

        @Override
        public int onAuthenticate(InfoUser tInfo) {
            if (mProgress != null)
                mProgress.dismiss();
            DongConfiguration.mDeviceInfo = new DeviceInfo(new InfoDevice(320265));
            startActivity(new Intent(IPLoginActivity.this, VideoViewActivity.class));
            LogUtils.i("IPLoginActivityDongAccountProxy.clazz--->>>onAuthenticate...441.....tInfo:"
                    + tInfo);
            return 0;
        }

        @Override
        public int onNewListInfo() {
            LogUtils.i("IPLoginActivityDongAccountProxy.clazz--->>>onNewListInfo");
            return 0;
        }

        @Override
        public int onUserError(int nErrNo) {
            if (mProgress != null) {
                mProgress.dismiss();
            }
            LogUtils.i("IPLoginActivityDongAccountProxy.clazz--->>>onUserError........nErrNo:"
                    + nErrNo);
            return 0;
        }
    }
}
