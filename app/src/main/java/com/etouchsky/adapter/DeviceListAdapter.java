package com.etouchsky.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddclient.dongsdk.DeviceInfo;
import com.etouchsky.activity.SettingDeviceActivity;
import com.etouchsky.wisdom.R;

import java.util.ArrayList;

public class DeviceListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<DeviceInfo> mDeviceInfoList = new ArrayList<>();

    public DeviceListAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<DeviceInfo> deviceList) {
        mDeviceInfoList.clear();
        for (DeviceInfo deviceInfo : deviceList) {
            if (deviceInfo != null) {
                mDeviceInfoList.add(deviceInfo);
            }else{
                Toast.makeText(mContext,"无法获取到设备信息",Toast.LENGTH_SHORT).show();
            }
        }
//        Toast.makeText(mContext,"共有"+mDeviceInfoList.size()+"台设备",Toast.LENGTH_SHORT).show();
    }

    public ArrayList<DeviceInfo> getData() {
        return mDeviceInfoList;
    }

    @Override
    public int getCount() {
        return mDeviceInfoList.size();
    }

    @Override
    public DeviceInfo getItem(int position) {
        return mDeviceInfoList.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder hold;
        if (convertView == null) {
            hold = new Holder();
            convertView = mInflater
                    .inflate(R.layout.device_list_adapter_item, null);
            convertView.setTag(hold);
            hold.deviceName = (TextView) convertView
                    .findViewById(R.id.deviceName);
            hold.mess = (TextView) convertView.findViewById(R.id.mess);
            hold.setting = (ImageView) convertView.findViewById(R.id.setting);
        } else {
            hold = (Holder) convertView.getTag();
        }

        hold.deviceName.setText(getData().get(position).deviceName);
        DeviceInfo infoDevice = getData().get(position);
        if (infoDevice.isOnline) {
            if (DeviceInfo.isAuthDeviceType(infoDevice, 23)) {
                hold.mess.setText(mContext.getString(R.string.authDeviceOn));
            } else {
                hold.mess.setText(mContext.getString(R.string.myDeviceOn));
            }
        } else {
            if (DeviceInfo.isAuthDeviceType(infoDevice, 23)) {
                hold.mess.setText(mContext.getString(R.string.authDeviceOff));
            } else {
                hold.mess.setText(mContext.getString(R.string.myDeviceOff));
            }
        }
        onClickListener(hold, getData().get(position));
        return convertView;
    }

    private void onClickListener(Holder hold, final DeviceInfo device) {
        hold.setting.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(mContext,
                        SettingDeviceActivity.class);
                intent.putExtra(SettingDeviceActivity.DEVICEINFO_KEY, device);
                mContext.startActivity(intent);
            }
        });
    }

    private static class Holder {
        TextView deviceName;
        TextView mess;
        ImageView setting;
    }
}
