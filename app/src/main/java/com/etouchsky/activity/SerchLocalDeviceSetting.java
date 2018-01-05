package com.etouchsky.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongCallbackProxy.DongAccountCallbackImp;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.jnisdk.InfoUser;
import com.etouchsky.adapter.LanAddDeviceListAdapter;
import com.etouchsky.widget.WarnDialog;
import com.etouchsky.wisdom.R;
import com.gViewerX.util.LogUtils;

import java.util.ArrayList;

public class SerchLocalDeviceSetting extends Activity implements
        OnClickListener {
    private ListView mListView;
    private LanAddDeviceListAdapter mListAdapter;
    private TextView mTvRefresh;
    private ProgressDialog mProgress;

    public ArrayList<DeviceInfo> mDeviceInfoList;
    private DeviceInfo mDeviceInfo;
    private LinearLayout mLlBack;

    private SerchLocalDeviceSettingDongAccountProxy mDongAccountProxy
            = new SerchLocalDeviceSettingDongAccountProxy();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_local_device);

        mLlBack = (LinearLayout) findViewById(R.id.ll_back);
        mTvRefresh = (TextView) findViewById(R.id.tv_refresh);
        mListView = (ListView) findViewById(R.id.listview);

        mListAdapter = new LanAddDeviceListAdapter(this);
        mListView.setAdapter(mListAdapter);

        mLlBack.setOnClickListener(this);
        mTvRefresh.setOnClickListener(this);
        mListView.setOnItemClickListener(mListItemOnclick);

        DongSDKProxy.initDongAccountLan(mDongAccountProxy);
        DongSDKProxy.requestLanDeviceListFromPlatform();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DongSDKProxy.requestLanStartScan();
        DongSDKProxy.registerAccountLanCallback(mDongAccountProxy);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DongSDKProxy.unRegisterAccountLanCallback(mDongAccountProxy);
        DongSDKProxy.requestLanStopScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DongSDKProxy.requestLanLoginOut();
        LogUtils.i("SerchLocalDeviceSetting.clazz--->>>onDestroy........requestLanLoginOut");
    }

    private OnItemClickListener mListItemOnclick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            /*final DeviceInfo deviceInfo = mListAdapter.getItem(position);

            View diaView = View.inflate(SerchLocalDeviceSetting.this,
                    R.layout.login_dialog, null);
            final Dialog dialog = new Dialog(SerchLocalDeviceSetting.this,
                    nulll);
            dialog.setContentView(diaView);
            dialog.show();

            TextView tvLogin = (TextView) diaView.findViewById(R.id.tv_login);
            TextView tvCancel = (TextView) diaView.findViewById(R.id.tv_cancel);
            final EditText etPwd = (EditText) diaView.findViewById(R.id.et_pwd);

            tvLogin.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    String pwd = etPwd.getText().toString();
                    mProgress = ProgressDialog.show(
                            SerchLocalDeviceSetting.this, "",
                            SerchLocalDeviceSetting.this
                                    .getString(R.string.waitingForConnection),
                            true, true);
                    mDeviceInfo = deviceInfo;
                    DongSDKProxy.initDongAccountLan(mDongAccountProxy);
                    DongSDKProxy.requestLanExploreLogin(mDeviceInfo.dwDeviceID,
                            mDeviceInfo.deviceName, pwd);
                    LogUtils.i("SerchLocalDeviceSetting.clazz--->>>tvLogin....re....mDeviceInfo:"
                            + mDeviceInfo);
                    dialog.dismiss();
                }
            });
            tvCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });*/
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.tv_refresh:
                DongSDKProxy.requestLanFlush();
                break;
        }
    }

    private class SerchLocalDeviceSettingDongAccountProxy extends
            DongAccountCallbackImp {

        @Override
        public int onAuthenticate(InfoUser tInfo) {
            if (mProgress != null)
                mProgress.dismiss();
            DongConfiguration.mDeviceInfo = mDeviceInfo;
            startActivity(new Intent(SerchLocalDeviceSetting.this,
                    VideoViewActivity.class));
            LogUtils.i("SerchLocalDeviceSetting.clazz--->>>onAuthenticate........tInfo:"
                    + tInfo);
            return 0;
        }

        @Override
        public int onNewListInfo() {
            mDeviceInfoList = DongSDKProxy.requestLanGetDeviceListFromCache();
            mListAdapter.setData(mDeviceInfoList);
            mListAdapter.notifyDataSetChanged();
            LogUtils.i("SerchLocalDeviceSetting.clazz--->>>onNewListInfo........mDeviceInfoList.size():"
                    + mDeviceInfoList.size());
            return 0;
        }

        @Override
        public int onUserError(int nErrNo) {
            if (mProgress != null) {
                mProgress.dismiss();
            }
            WarnDialog.showDialog(SerchLocalDeviceSetting.this, mProgress,
                    nErrNo);
            LogUtils.i("SerchLocalDeviceSetting.clazz--->>>onUserError........nErrNo:"
                    + nErrNo);
            return 0;
        }
    }
}
