package com.etouchsky.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.etouchsky.o2o.InsertAddressActivity;
import com.etouchsky.pojo.SameCod;
import com.etouchsky.pojo.UserAddressInfo;
import com.etouchsky.pojo.UserAddressInfoList;
import com.etouchsky.util.CacheUtils;
import com.etouchsky.util.HttpUtil;
import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.wisdom.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/9/14 0014.
 * 收货地址适配器暂用
 */

public class O2OUserAddressAdapter extends BaseAdapter {

    private ViewHodler viewHodler;
    private UserAddressInfoList list;
    private RequestBody body;
    private Request builder;
    private OkHttpClient client = new OkHttpClient();
    private Context context;
    private UserAddressInfo info;   //用户信息列表
    private Intent intent;
    private SameCod userMessageSameCod;
    private Activity activity;

    public O2OUserAddressAdapter(Context context, UserAddressInfo info, Activity activity) {
        this.context = context;
        this.info = info;
        this.activity = activity;
        intent = new Intent();
    }

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
        if (convertView == null) {
            viewHodler = new ViewHodler();
            convertView = LayoutInflater.from(context).inflate(R.layout.o2o_user_address_list, null);
            viewHodler.addressName = (TextView) convertView.findViewById(R.id.o2o_user_address_name);
            viewHodler.addressNum = (TextView) convertView.findViewById(R.id.o2o_user_address_phone_num);
            viewHodler.address = (TextView) convertView.findViewById(R.id.o2o_user_address);
            viewHodler.defaultIv = (ImageView) convertView.findViewById(R.id.o2o_user_address_default_iv);
            viewHodler.editList = (TextView) convertView.findViewById(R.id.o2o_address_edit_list);
            viewHodler.delectList = (TextView) convertView.findViewById(R.id.o2o_address_delete_list);
            viewHodler.editList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int) v.getTag();
                    Log.e("TAG***********", index + "");
                    intent.putExtra("addressID", Integer.valueOf(info.getAddress().get(index).getAddressId()).intValue());
                    Toast.makeText(context, "请输入您要修改的信息", Toast.LENGTH_SHORT).show();
                    intent.setClass(context, InsertAddressActivity.class);
                    context.startActivity(intent);
                }
            });
            viewHodler.delectList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doPost((Integer) viewHodler.delectList.getTag());
                }
            });
            convertView.setTag(viewHodler);
        } else {
            viewHodler = (ViewHodler) convertView.getTag();
        }
        viewHodler.editList.setTag(position);
        viewHodler.delectList.setTag(position);
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

    private void doPost(int index) {
        body = new FormBody.Builder().add("user_id", "" + CacheUtils.getUserId(context, CacheUtils.USER_ID, 0)).add("address_id", info.getAddress().get(index).getAddressId()).build();
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
                    Toast.makeText(context, userMessageSameCod.msg, Toast.LENGTH_SHORT).show();
                    OkAndGsonUtil.refreshActivity(activity); // 重启activity
                } else {
                    Toast.makeText(context, userMessageSameCod.msg, Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

}

