package com.etouchsky.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ddclient.jnisdk.InfoUser;
import com.etouchsky.wisdom.R;

import java.util.ArrayList;

public class AuthorizedAccountListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<InfoUser> mInfoUserList = new ArrayList<>();

    public AuthorizedAccountListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<InfoUser> userList) {
        mInfoUserList.clear();
        for (InfoUser infoUser : userList) {
            if (infoUser != null) mInfoUserList.add(infoUser);
        }
    }

    public ArrayList<InfoUser> getData() {
        return mInfoUserList;
    }

    @Override
    public int getCount() {
        return getData().size();
    }

    @Override
    public InfoUser getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.authorized_account_adapter_item,
                    null);
            convertView.setTag(hold);
        } else {
            hold = (Holder) convertView.getTag();
        }
        hold.userName = (TextView) convertView.findViewById(R.id.deviceName);
        hold.userName.setText(getData().get(position).userName);
        return convertView;
    }

    private static class Holder {
        TextView userName;
    }
}
