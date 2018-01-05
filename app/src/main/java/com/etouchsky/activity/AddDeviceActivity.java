package com.etouchsky.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.etouchsky.wisdom.R;
import com.ddclient.dongsdk.AbstractDongCallbackProxy.DongAccountCallbackImp;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.jnisdk.InfoUser;
import com.etouchsky.adapter.LanAddDeviceListAdapter;
import com.etouchsky.widget.WarnDialog;
import com.gViewerX.util.LogUtils;

import java.util.ArrayList;

public class AddDeviceActivity extends Activity implements OnClickListener {
    private EditText mEtDeviceName;
    private EditText mEtDeviceSer;

    private ListView mListView;
    private LanAddDeviceListAdapter mListAdapter;
    public ArrayList<DeviceInfo> mDeviceInfoList;
    private LinearLayout mLlBack;
    private ProgressDialog mProgress;
    private TextView mTvDone;

    private AddDeviceActivityDongAccountLanProxy mAccountLanProxy
            = new AddDeviceActivityDongAccountLanProxy();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        mEtDeviceName = (EditText) findViewById(R.id.et_device_name);
        mEtDeviceSer = (EditText) findViewById(R.id.et_device_serial);
        mTvDone = (TextView) findViewById(R.id.tv_done);
        mLlBack = (LinearLayout) findViewById(R.id.ll_back);
        mListView = (ListView) findViewById(R.id.listview);
        mListAdapter = new LanAddDeviceListAdapter(this);
        mListView.setAdapter(mListAdapter);

        DongSDKProxy
                .initDongAccountLan(mAccountLanProxy);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final DeviceInfo deviceInfo = mListAdapter
                        .getItem(position);
                mEtDeviceName.setText(deviceInfo.deviceName);
                mEtDeviceSer.setText(deviceInfo.deviceSerialNO);
            }
        });
        mTvDone.setOnClickListener(this);
        mLlBack.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DongSDKProxy.requestLanStartScan();
        DongSDKProxy.registerAccountLanCallback(mAccountLanProxy);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DongSDKProxy.unRegisterAccountLanCallback(mAccountLanProxy);
        DongSDKProxy.requestLanStopScan();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mLlBack.performClick();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DongSDKProxy.requestLanLoginOut();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.tv_done:
                String deviceName = AddDeviceActivity.this.mEtDeviceName.getText()
                        .toString();
                String deviceSeri = AddDeviceActivity.this.mEtDeviceSer.getText()
                        .toString();

                if (deviceName.equals("") || deviceSeri.equals("")) {
                    Toast.makeText(AddDeviceActivity.this,
                            AddDeviceActivity.this.getText(R.string.empty),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                mProgress = ProgressDialog.show(AddDeviceActivity.this, "",
                        AddDeviceActivity.this.getText(R.string.wait), true, true);
                DongSDKProxy.requestAddDevice(deviceName, deviceSeri);
                break;
            default:
                break;
        }
    }

    private class AddDeviceActivityDongAccountLanProxy extends
            DongAccountCallbackImp {

        @Override
        public int onAuthenticate(InfoUser tInfo) {
            LogUtils.i("AddDeviceActivity.clazz--->>>onAuthenticate........tInfo:"
                    + tInfo);
            return 0;
        }

        @Override
        public int onNewListInfo() {
            mDeviceInfoList = DongSDKProxy.requestLanGetDeviceListFromCache();
            mListAdapter.setData(mDeviceInfoList);
            mListAdapter.notifyDataSetChanged();
            LogUtils.i("AddDeviceActivity.clazz--->>>onNewListInfo........mDeviceInfoList:"
                    + mDeviceInfoList);
            return 0;
        }

        @Override
        public int onAddDevice(int nReason, String username) {
            LogUtils.i("AddDeviceActivity.clazz--->>>onAddDevice........nReason:"
                    + nReason + ";username:" + username);
            if (nReason == 0) {
                Toast.makeText(AddDeviceActivity.this,
                        AddDeviceActivity.this.getText(R.string.suc),
                        Toast.LENGTH_SHORT).show();
                mProgress.dismiss();
            } else if (nReason == 1) {
                Toast.makeText(AddDeviceActivity.this,
                        AddDeviceActivity.this.getString(R.string.serError),
                        Toast.LENGTH_SHORT).show();
                mProgress.dismiss();
            } else if (nReason == 2) {
                Toast.makeText(AddDeviceActivity.this,
                        AddDeviceActivity.this.getString(R.string.alreadyAdd),
                        Toast.LENGTH_SHORT).show();
                mProgress.dismiss();
            } else if (nReason == 3) {
                mProgress.dismiss();
                new AlertDialog.Builder(AddDeviceActivity.this)
                        .setTitle(
                                AddDeviceActivity.this.getString(R.string.warn))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage(
                                AddDeviceActivity.this
                                        .getString(R.string.alreadyAdd)
                                        + "("
                                        + username + ")")
                        .setCancelable(false)
                        .setPositiveButton(
                                AddDeviceActivity.this.getString(R.string.ok),
                                null).show();
            }
            return 0;
        }

        @Override
        public int onUserError(int nErrNo) {
            LogUtils.i("AddDeviceActivity.clazz--->>>onUserError........nErrNo:"
                    + nErrNo);
            WarnDialog.showDialog(AddDeviceActivity.this, mProgress, nErrNo);
            return 0;
        }

    }
}
