package com.etouchsky.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongCallbackProxy.DongAccountCallbackImp;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.jnisdk.InfoUser;
import com.etouchsky.adapter.DeviceListAdapter;
import com.etouchsky.wisdom.R;
import com.gViewerX.util.LogUtils;

import java.util.ArrayList;

public class DeviceListActivity extends Activity implements OnClickListener {

    private DeviceListAdapter mDeviceListAdapter;
    private ListView mListView;
    private LinearLayout mBack;
    private TextView mAdd;

    private ListActivityDongAccountProxy mAccountProxy = new ListActivityDongAccountProxy();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        mListView = (ListView) findViewById(R.id.lv_biglist);
        mBack = (LinearLayout) findViewById(R.id.bt_back);
        mAdd = (TextView) findViewById(R.id.tv_add);
        mDeviceListAdapter = new DeviceListAdapter(DeviceListActivity.this);
        mListView.setAdapter(mDeviceListAdapter);
        mAdd.setVisibility(View.INVISIBLE);
        mBack.setOnClickListener(this);
        mAdd.setOnClickListener(this);

        boolean initDongAccount = DongSDKProxy.initCompleteDongAccount();
        LogUtils.d("initDongAccount"+initDongAccount);
        if (!initDongAccount) {
            DongSDKProxy.initDongAccount(mAccountProxy);
        }
        mListView.setOnItemClickListener(mListItemClick);
    }

    private  int i = 0;
    @Override
    protected void onResume() {
        super.onResume();
        DongSDKProxy.registerAccountCallback(mAccountProxy);
        ArrayList<DeviceInfo> deviceList = DongSDKProxy.requestGetDeviceListFromCache();
        mDeviceListAdapter.setData(deviceList);
        mDeviceListAdapter.notifyDataSetChanged();
        LogUtils.i("ListActivity.clazz--->>>onResume" +
                ".....deviceList:" + deviceList);
//        if (LogUtils.isDebug) {
//            mBack.setVisibility(View.GONE);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        DongSDKProxy.unRegisterAccountCallback(mAccountProxy);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // DongSDK.finishDongSDK();
       // System.exit(0);
    }

    private OnItemClickListener mListItemClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            DeviceInfo deviceInfo = (DeviceInfo) mDeviceListAdapter.getItem(arg2);
            if (!deviceInfo.isOnline) {
                Toast.makeText(
                        DeviceListActivity.this,
                        DeviceListActivity.this
                                .getString(R.string.PLEASE_SELECT_ONLINE_CAMERA),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            DongConfiguration.mDeviceInfo = deviceInfo;
            LogUtils.i("ListActivity.clazz--->>>onItemClick.....2222...deviceInfo:"
                    + deviceInfo);
            startActivity(new Intent(DeviceListActivity.this,
                    VideoViewActivity.class));
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_back:
                finish();
                break;
            case R.id.tv_add:
                startActivity(new Intent(this, AddDeviceActivity.class));
                break;
        }
    }

    private class ListActivityDongAccountProxy extends DongAccountCallbackImp {

        @Override
        public int onAuthenticate(InfoUser tInfo) {
            LogUtils.i("ListActivity.clazz--->>>onAuthenticate........tInfo:"
                    + tInfo);
            return 0;
        }

        @Override
        public int onUserError(int nErrNo) {
            LogUtils.i("ListActivity.clazz--->>>onUserError........nErrNo:"
                    + nErrNo);
            return 0;
        }

        @Override
        public int onNewListInfo() {
            ArrayList<DeviceInfo> deviceInfoList = DongSDKProxy.requestGetDeviceListFromCache();
            mDeviceListAdapter.setData(deviceInfoList);
            mDeviceListAdapter.notifyDataSetChanged();
            LogUtils.i("ListActivity.clazz--->>>onNewListInfo........deviceInfoList.size:"
                    + deviceInfoList.size());
            return 0;
        }


        @Override
        public int onCall(ArrayList<DeviceInfo> list) {
            LogUtils.i("ListActivity.clazz--->>>onCall........list.size():" + list.size());
            int size = list.size();
            if (size > 0) {
                DeviceInfo deviceInfo = list.get(0);
                String message = deviceInfo.deviceName + deviceInfo.dwDeviceID
                        + deviceInfo.msg;
                DongConfiguration.mDeviceInfo = deviceInfo;
                //getApplicationContext().startActivity(new Intent(ETService.this,
                //       VideoViewActivity.class));
                Intent mintent = new Intent(DeviceListActivity.this, VideoViewActivity.class);
                mintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                //mintent.setClassName(ETService.this, VideoViewActivity.class);
                mintent.putExtra("call","");
                getApplicationContext().startActivity(mintent);
            }
            return 0;
        }
    }

}
