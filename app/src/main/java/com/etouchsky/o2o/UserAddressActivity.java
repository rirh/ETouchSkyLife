package com.etouchsky.o2o;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.etouchsky.adapter.O2OUserAddressAdapter;
import com.etouchsky.pojo.SameCod;
import com.etouchsky.pojo.UserAddressInfo;
import com.etouchsky.pojo.UserAddressInfoList;
import com.etouchsky.util.CacheUtils;
import com.etouchsky.util.HttpUtil;
import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.wisdom.R;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Administrator on 2017/9/7 0007.
 * 用户收货地址管理
 */

public class UserAddressActivity extends Activity implements View.OnClickListener {

    private TextView hint, insertTv;
    private Button insertBt;
    private FrameLayout mFrameLayout;
    private Type type;
    private SameCod userMessageSameCod;
    private UserAddressInfo info;
    private ListView messageListView;
    private AbsListView.LayoutParams layoutParams;
    private boolean isOpenListView = false;
    private Message message = new Message();
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.o2o_user_adress);
        initView();
        OkAndGsonUtil.setTitleBar(this, "收货地址");
    }

    private void initView() {
        intent = new Intent();
        hint = (TextView) findViewById(R.id.o2o_user_address_hint);
        insertTv = (TextView) findViewById(R.id.o2o_user_address_insert_tv);
        insertBt = (Button) findViewById(R.id.o2o_user_address_insert_bt);
        insertBt.setOnClickListener(this);
        insertTv.setOnClickListener(this);
        messageListView = new ListView(UserAddressActivity.this);
        messageListView.setDivider(null);
        mFrameLayout = (FrameLayout) findViewById(R.id.o2o_user_address_fl);
        layoutParams = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT);
        OkAndGsonUtil.doGet(HttpUtil.O2O_USER_ADDRESS + "&user_id=" + CacheUtils.getUserId(this, CacheUtils.USER_ID, 0)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("++++++++", response.toString());
                initData(response.body().string());
            }
        });

    }

    private void initData(String jsonData) {
        Log.e("+++++++++++", jsonData);
        type = new TypeToken<SameCod<UserAddressInfo>>() {
        }.getType();
        userMessageSameCod = OkAndGsonUtil.gson.fromJson(jsonData, type);
        info = (UserAddressInfo) userMessageSameCod.info;
        if (info.getAddress().size() != 0) {
            message.what = 0x02;  //02代表订单
            handler.sendMessage(message);
        } else {
            message.what = 0x03;  //03代表获取数据失败
            handler.sendMessage(message);
        }
    }


    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x02:
                    hint.setVisibility(View.GONE);
                    insertTv.setVisibility(View.GONE);
                    insertBt.setVisibility(View.VISIBLE);
                    insertBt.setOnClickListener(UserAddressActivity.this);
                    messageListView.setVisibility(View.VISIBLE);
                    O2OUserAddressAdapter messageAdapter = new O2OUserAddressAdapter(UserAddressActivity.this,info,UserAddressActivity.this);
                    messageListView.setAdapter(messageAdapter);
                    if (isOpenListView) {
                        mFrameLayout.removeView(messageListView);
                        mFrameLayout.addView(messageListView, layoutParams);
                    } else {
                        mFrameLayout.addView(messageListView, layoutParams);
                        isOpenListView = true;
                    }
                    break;
                case 0x03:
                    if (messageListView != null)
                        messageListView.setVisibility(View.GONE);
                    hint.setVisibility(View.VISIBLE);
                    insertTv.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.o2o_user_address_insert_tv:
//                插入用户信息
                intent.setClass(UserAddressActivity.this, InsertAddressActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.o2o_user_address_insert_bt:
                intent.setClass(UserAddressActivity.this, InsertAddressActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

/*
    private class UserAddressAdapter extends BaseAdapter {

        private ViewHodler viewHodler;
        private UserAddressInfoList list;
        private int index;
        private RequestBody body;
        private Request builder;
        private OkHttpClient client = new OkHttpClient();

        @Override
        public int getCount() {
            return info.getAddress().size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            list = info.getAddress().get(position);
            Log.e("PPPPPPPPPPPPPP", position + "");
            index = position;
            if (convertView == null) {
                viewHodler = new ViewHodler();
                convertView = LayoutInflater.from(UserAddressActivity.this).inflate(R.layout.o2o_user_address_list, null);
                viewHodler.addressName = (TextView) convertView.findViewById(R.id.o2o_user_address_name);
                viewHodler.addressNum = (TextView) convertView.findViewById(R.id.o2o_user_address_phone_num);
                viewHodler.address = (TextView) convertView.findViewById(R.id.o2o_user_address);
                viewHodler.defaultIv = (ImageView) convertView.findViewById(R.id.o2o_user_address_default_iv);
                viewHodler.editList = (TextView) convertView.findViewById(R.id.o2o_address_edit_list);
                viewHodler.delectList = (TextView) convertView.findViewById(R.id.o2o_address_delete_list);
                viewHodler.editList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.putExtra("addressID", Integer.valueOf(info.getAddress().get(index).getAddressId()).intValue());
                        Toast.makeText(UserAddressActivity.this, "请输入您要修改的信息", Toast.LENGTH_SHORT).show();
                        intent.setClass(UserAddressActivity.this, InsertAddressActivity.class);
                        startActivity(intent);
                    }
                });
                viewHodler.delectList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doPost();
                    }
                });
                convertView.setTag(viewHodler);
            } else
                viewHodler = (ViewHodler) convertView.getTag();
            viewHodler.addressName.setText(list.getConsignee());
            viewHodler.addressNum.setText(list.getMobile());
            viewHodler.address.setText(list.getAddress());
            if (info.getAddressId().equals(list.getAddressId()))
                viewHodler.defaultIv.setImageResource(R.mipmap.o2o_user_address_default_red);


            return convertView;
        }

        private class ViewHodler {
            TextView addressName, addressNum, address, editList, delectList;
            ImageView defaultIv;
        }

        private void doPost() {
            body = new FormBody.Builder().add("user_id", "" + CacheUtils.getUserId(UserAddressActivity.this, CacheUtils.USER_ID, 0)).add("address_id", info.getAddress().get(index).getAddressId()).build();
            builder = new Request.Builder().url(HttpUtil.O2O_USER_DELETE_ADDRESS).post(body).build();
            Call call = client.newCall(builder);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
//                Log.e("======", );
                    initData(response.body().string());

                }
            });
        }

        private void initData(String jsonData) {
            Log.e("============", jsonData);
            userMessageSameCod = OkAndGsonUtil.gson.fromJson(jsonData, SameCod.class);
            Message message = new Message();
            message.what = 0x00; //表示成功获取数据
            handler.sendMessage(message);

        }

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x00) {
                    if (userMessageSameCod.result.equals("success")) {
                        Toast.makeText(UserAddressActivity.this, userMessageSameCod.msg, Toast.LENGTH_SHORT).show();
                        OkAndGsonUtil.refreshActivity(UserAddressActivity.this);
                    } else {
                        Toast.makeText(UserAddressActivity.this, userMessageSameCod.msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

    }
*/

}
