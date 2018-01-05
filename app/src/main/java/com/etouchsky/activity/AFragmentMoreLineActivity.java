package com.etouchsky.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.etouchsky.data.DataHelper;
import com.etouchsky.pojo.HouseMoreLineInfo;
import com.etouchsky.util.HttpUtil;
import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.wisdom.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/9/29 0029.
 */

public class AFragmentMoreLineActivity extends Activity {


    private ListView mListView;
    private HouseMoreLineInfo houseMoreLineInfo;
    private List<HouseMoreLineInfo> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_fragment_more_layout);
        initView();
        OkAndGsonUtil.setTitleBar(this, "物业通知");
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.a_fragment_more_lv);
        DataHelper dbHelper = new DataHelper(this);
        list = new ArrayList<HouseMoreLineInfo>();
        //得到一个可写的数据库
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM CommunitiesMoreLine ORDER BY Id DESC", null);
        while (cursor.moveToNext()) {
            String s = cursor.getString(cursor.getColumnIndex("Content"));
            Log.e("*****", "通知列表:" + s);
            list.add(OkAndGsonUtil.gson.fromJson(s, HouseMoreLineInfo.class));
        }
        //关闭数据库
        db.close();
        MoreLineAdapter adapter = new MoreLineAdapter();
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(AFragmentMoreLineActivity.this, AFragmentMoreLineInfoActivity.class);
                intent.putExtra("noticeID", Integer.valueOf(list.get(position).getNoticeid()));
                startActivity(intent);
            }
        });

    }

    class MoreLineAdapter extends BaseAdapter {

        private ViewHolder holder;

        @Override
        public int getCount() {
            return list.size();
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

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(AFragmentMoreLineActivity.this).inflate(R.layout.house_more_line_info_activity_b, null);
                holder.title = (TextView) convertView.findViewById(R.id.more_line_info_title);
                holder.time = (TextView) convertView.findViewById(R.id.more_line_info_time);
                holder.message = (TextView) convertView.findViewById(R.id.more_line_info_message);
                holder.name = (TextView) convertView.findViewById(R.id.more_line_info_name);
                holder.ico = (ImageView) convertView.findViewById(R.id.more_line_info_ico);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();

            ImageLoader.getInstance().displayImage(list.get(position).getNoticeicon(), holder.ico);
            holder.name.setText(list.get(position).getCommunityName());
            holder.title.setText(list.get(position).getNoticetitle());
            holder.time.setText(OkAndGsonUtil.timeStamp2Date(list.get(position).getNoticetime(), null).substring(0, 11));
            holder.message.setText(list.get(position).getNoticesummary());
            return convertView;
        }

        private class ViewHolder {
            TextView title, time, message,name;
            ImageView ico;
        }
    }
}
