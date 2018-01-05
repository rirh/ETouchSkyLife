package com.etouchsky.o2o;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.etouchsky.activity.O2OLoginActivity;
import com.etouchsky.pojo.SameCod;
import com.etouchsky.pojo.UpDateUserInfo;
import com.etouchsky.pojo.UserInfo;
import com.etouchsky.util.CacheUtils;
import com.etouchsky.util.HttpUtil;
import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.wisdom.MainActivity;
import com.etouchsky.wisdom.R;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.utils.L;

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

public class UpDataUserInfo extends Activity implements View.OnClickListener {

    private RequestBody body;
    private Request builder;
    private EditText mEditText;
    private String dateJson;
    private OkHttpClient client = new OkHttpClient();
    private SameCod sameCod;
    private Button mButton;
    private UpDateUserInfo userInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.o2o_up_data_user_info_layout);
        initView();

    }

    private void initView() {
        mButton = (Button) findViewById(R.id.o2o_bt_save_update);
        mEditText = (EditText) findViewById(R.id.o2o_up_data_user_name);
        mButton.setOnClickListener(this);
        OkAndGsonUtil.setTitleBar(this,getResources().getString(R.string.private_message_name));
    }

    private void doPost() {
        body = new FormBody.Builder().add("user_id", "" + CacheUtils.getUserId(this, CacheUtils.USER_ID, 0)).add("data", dateJson).build();
        builder = new Request.Builder().url(HttpUtil.O2O_USER_UPDATE).post(body).build();
        Call call = client.newCall(builder);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
               Log.e("======",response.toString());
                initData(response.body().string());
            }
        });
    }

    private void initData(String jsonData) {
//        Log.e("============", jsonData);
        sameCod = OkAndGsonUtil.gson.fromJson(jsonData, SameCod.class);
        if (sameCod.result.equals("success")) {
            Toast.makeText(this, "昵称更新成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, sameCod.msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.o2o_bt_save_update:
                userInfo = new UpDateUserInfo(mEditText.getText().toString());
                dateJson = OkAndGsonUtil.gson.toJson(userInfo);
//                Log.e("===========",dateJson);
                doPost();
                break;
        }
    }
}
