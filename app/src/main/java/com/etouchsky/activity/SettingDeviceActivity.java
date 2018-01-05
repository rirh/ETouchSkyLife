package com.etouchsky.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongCallbackProxy.DongAccountCallbackImp;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.jnisdk.InfoDevice;
import com.ddclient.jnisdk.InfoUser;
import com.etouchsky.widget.WarnDialog;
import com.etouchsky.wisdom.R;
import com.gViewerX.util.LogUtils;

import java.util.ArrayList;

public class SettingDeviceActivity extends Activity implements OnClickListener {

    public static final String DEVICEINFO_KEY = "deviceinfo";

    private EditText mEtDeviceName;
    private Button mBtSave;
    private RelativeLayout mRlAuthorize;
    private RelativeLayout mRlDeleteDevice;

    private LinearLayout mLlBack;
    private TextView mTvTitle;

    private DeviceInfo mDeviceInfo;
    private ProgressDialog mProgress;
    private ArrayList<InfoDevice> mInfoDevices;

    private SettingDeviceActivityDongAccountProxy mDongAccountProxy = new SettingDeviceActivityDongAccountProxy();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_device);

        mBtSave = (Button) findViewById(R.id.bt_save);
        mRlAuthorize = (RelativeLayout) findViewById(R.id.rl_authorize_account);
        mRlDeleteDevice = (RelativeLayout) findViewById(R.id.rl_delete_device);
        mLlBack = (LinearLayout) findViewById(R.id.ll_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mEtDeviceName = (EditText) findViewById(R.id.et_device_name);

        Intent intent = getIntent();
        mDeviceInfo = (DeviceInfo) intent.getSerializableExtra(DEVICEINFO_KEY);

        mEtDeviceName.setText(mDeviceInfo.deviceName.toString());
        mTvTitle.setText(mDeviceInfo.deviceName.toString());

        mBtSave.setOnClickListener(this);
        mRlAuthorize.setOnClickListener(this);
        mRlDeleteDevice.setOnClickListener(this);
        mLlBack.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        DongSDKProxy.registerAccountCallback(mDongAccountProxy);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DongSDKProxy.unRegisterAccountCallback(mDongAccountProxy);
    }

    private class SettingDeviceActivityDongAccountProxy extends
            DongAccountCallbackImp {

        @Override
        public int onAuthenticate(InfoUser tInfo) {
            LogUtils.i("SettingDeviceActivity.clazz--->>>onAuthenticate........tInfo:"
                    + tInfo);
            return 0;
        }

        @Override
        public int onCall(ArrayList<DeviceInfo> list) {
            WarnDialog.applicationBroughtWarn(SettingDeviceActivity.this,
                    SettingDeviceActivity.this, mInfoDevices, true);
            LogUtils.i("SettingDeviceActivity.clazz--->>>onCall........list:"
                    + list);
            return 0;
        }

        @Override
        public int onDelDevice(int nReason) {
            if (nReason == 0) {
                if (mProgress != null) {
                    mProgress.dismiss();
                }
                Toast.makeText(SettingDeviceActivity.this,
                        SettingDeviceActivity.this.getString(R.string.suc),
                        Toast.LENGTH_SHORT).show();
            } else {
                if (mProgress != null) {
                    mProgress.dismiss();
                }
                Toast.makeText(SettingDeviceActivity.this,
                        SettingDeviceActivity.this.getString(R.string.fail),
                        Toast.LENGTH_SHORT).show();
            }
            LogUtils.i("SettingDeviceActivity.clazz--->>>onDelDevice........nReason:"
                    + nReason);
            return 0;
        }

        @Override
        public int onSetDeviceName(int nReason) {
            LogUtils.i("SettingDeviceActivity.clazz--->>>onSetDeviceName........nReason:"
                    + nReason);
            if (nReason == 0) {
                if (mProgress != null) {
                    mProgress.dismiss();
                }
                Toast.makeText(SettingDeviceActivity.this,
                        SettingDeviceActivity.this.getString(R.string.suc),
                        Toast.LENGTH_SHORT).show();
            } else {
                if (mProgress != null) {
                    mProgress.dismiss();
                }
                Toast.makeText(SettingDeviceActivity.this,
                        SettingDeviceActivity.this.getString(R.string.fail),
                        Toast.LENGTH_SHORT).show();
            }
            return 0;
        }

        @Override
        public int onUserError(int nErrNo) {
            LogUtils.i("SettingDeviceActivity.clazz--->>>onUserError........nErrNo:"
                    + nErrNo);
            return 0;
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_save:
                mProgress = ProgressDialog.show(this, "",
                        this.getString(R.string.wait), true, true);
                DongSDKProxy.requestSetDeviceName(mDeviceInfo.dwDeviceID,
                        mEtDeviceName.getText().toString());
                break;
            case R.id.rl_delete_device:
                AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
                mDialog.setTitle(this.getString(R.string.warn));
                mDialog.setMessage(this.getString(R.string.deldeteDevice));
                mDialog.setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mProgress = ProgressDialog.show(
                                        SettingDeviceActivity.this, "",
                                        SettingDeviceActivity.this
                                                .getString(R.string.wait), true,
                                        true);
                                DongSDKProxy.requestSetDeviceName(
                                        mDeviceInfo.dwDeviceID, mEtDeviceName
                                                .getText().toString());
                                DongSDKProxy.requestDeleteDevice(
                                        DongConfiguration.mUserInfo.userID,
                                        mDeviceInfo.dwDeviceID);
                            }
                        });
                mDialog.setNegativeButton(getString(R.string.cancel), null);
                mDialog.show();
                break;
            case R.id.ll_back:
                finish();
                break;
            case R.id.rl_authorize_account:
                if (DeviceInfo.isAuthDeviceType(mDeviceInfo, 23)) {
                    Toast.makeText(this, this.getString(R.string.permissions),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this, AuthorizedAccountActivity.class);
                intent.putExtra(DEVICEINFO_KEY, mDeviceInfo);
                startActivity(intent);
                break;
        }
    }
}
