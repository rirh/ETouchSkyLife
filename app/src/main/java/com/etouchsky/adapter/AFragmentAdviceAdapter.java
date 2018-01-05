package com.etouchsky.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.etouchsky.pojo.AFragmentAdviceData;
import com.etouchsky.pojo.AFragmentAdviceIndexInfo;
import com.etouchsky.wisdom.R;

/**
 * Created by Administrator on 2017/9/19 0019.
 */

public class AFragmentAdviceAdapter extends BaseAdapter {

    private AFragmentAdviceData data;
    private Context context;
    private AFragmentAdviceIndexInfo info;
    private ViewHolder holder;

    public AFragmentAdviceAdapter(Context context, AFragmentAdviceIndexInfo info) {
        this.context = context;
        this.info = info;

    }

    @Override
    public int getCount() {
        return info.getData().size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        data = (AFragmentAdviceData) info.getData().get(position);
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.a_fragment_list_view_layout,null);
            holder.title = (TextView) convertView.findViewById(R.id.a_fragment_advice_title);
            holder.statue = (TextView) convertView.findViewById(R.id.a_fragment_advice_statue);
            holder.createTime = (TextView) convertView.findViewById(R.id.a_fragment_advice_create_time);
            holder.communityName = (TextView) convertView.findViewById(R.id.a_fragment_advice_community_name);
            convertView.setTag(holder);
        }else
            holder = (ViewHolder) convertView.getTag();
        holder.title.setText(data.getAdviceTitle());
        holder.statue.setText(data.getState());
        holder.createTime.setText(data.getCreateTime());
        holder.communityName.setText(data.getCommunityName());
        return convertView;
    }

    private class ViewHolder{
        private TextView title,statue,createTime,communityName;
    }
}
