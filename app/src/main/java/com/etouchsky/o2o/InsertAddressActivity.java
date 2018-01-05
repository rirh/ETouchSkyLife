package com.etouchsky.o2o;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.etouchsky.pojo.SameCod;
import com.etouchsky.pojo.SaveAddress;
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
 * Created by Administrator on 2017/9/7 0007.
 */

public class InsertAddressActivity extends Activity {
    private RequestBody body;
    private Button saveUserAddress;
    private Request builder;
    private OkHttpClient client = new OkHttpClient();
    private EditText user, userNum, userAddress;
    private String dataJson;
    private SameCod sameCod;
    int addressID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.o2o_insert_adress);
        initView();

    }

    private void initView() {
        saveUserAddress = (Button) findViewById(R.id.o2o_insert_address_save_bt);
        user = (EditText) findViewById(R.id.o2o_insert_address_user);
        userNum = (EditText) findViewById(R.id.o2o_insert_address_user_num);
        userAddress = (EditText) findViewById(R.id.o2o_insert_address_user_address);
        addressID = getIntent().getIntExtra("addressID",0);
        if (addressID != 0) {
            OkAndGsonUtil.setTitleBar(InsertAddressActivity.this,"修改地址");

        } else {
            OkAndGsonUtil.setTitleBar(InsertAddressActivity.this,"添加地址");
        }
        saveUserAddress.setOnClickListener(new View.OnClickListener() {
            private SaveAddress saveAddress;
            @Override
            public void onClick(View v) {
                if (addressID != 0) {
                    saveAddress = new SaveAddress(addressID, user.getText().toString(), userNum.getText().toString(), userAddress.getText().toString());
                } else {
                    saveAddress = new SaveAddress(user.getText().toString(), userNum.getText().toString(), userAddress.getText().toString());
                }
                dataJson = OkAndGsonUtil.gson.toJson(saveAddress);
                Log.e("============", dataJson);
                doPost();
            }
        });
    }

    private void doPost() {
        body = new FormBody.Builder().add("user_id", "" + CacheUtils.getUserId(this, CacheUtils.USER_ID, 0)).add("data", dataJson).build();
        builder = new Request.Builder().url(HttpUtil.O2O_USER_INSERT_ADDRESS).post(body).build();
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
        sameCod = OkAndGsonUtil.gson.fromJson(jsonData, SameCod.class);
        Message message = new Message();
        message.what = 0x00; //表示成功获取数据
        handler.sendMessage(message);

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x00) {
                if (sameCod.result.equals("success")) {
                    Toast.makeText(InsertAddressActivity.this, sameCod.msg, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(InsertAddressActivity.this,UserAddressActivity.class));
                    finish();
                } else {
                    Toast.makeText(InsertAddressActivity.this, sameCod.msg, Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


}
