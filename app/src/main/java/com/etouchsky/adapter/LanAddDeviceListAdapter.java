package com.etouchsky.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ddclient.dongsdk.DeviceInfo;
import com.etouchsky.wisdom.R;

import java.util.ArrayList;

public class LanAddDeviceListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<DeviceInfo> mDeviceInfoList = new ArrayList<>();

    public LanAddDeviceListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<DeviceInfo> deviceList) {
        mDeviceInfoList.clear();
        for (DeviceInfo deviceInfo : deviceList) {
            if (deviceInfo != null) mDeviceInfoList.add(deviceInfo);
        }
    }

    public ArrayList<DeviceInfo> getData() {
        return mDeviceInfoList;
    }

    @Override
    public int getCount() {
        return getData().size();
    }

    @Override
    public DeviceInfo getItem(int position) {
        return getData().get(position);
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
            convertView = mInflater.inflate(R.layout.lan_list_adapter_item, null);
            convertView.setTag(hold);
        } else {
            hold = (Holder) convertView.getTag();
        }
        hold.deviceName = (TextView) convertView.findViewById(R.id.deviceName);
        hold.deviceName.setText(mDeviceInfoList.get(position).deviceName);
        return convertView;
    }

    private static class Holder {
        TextView deviceName;
    }
}
