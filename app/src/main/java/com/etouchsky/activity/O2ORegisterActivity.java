package com.etouchsky.activity;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etouchsky.pojo.SameCod;
import com.etouchsky.util.HttpUtil;
import com.etouchsky.wisdom.MainActivity;
import com.etouchsky.wisdom.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

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
 * Created by Administrator on 2017/8/31 0031.
 */

public class O2ORegisterActivity extends Activity {

    @ViewInject(R.id.o2o_et_user_name_register)
    private EditText userNameRegister;
    @ViewInject(R.id.o2o_et_email_register)
    private EditText emailRegister;
    @ViewInject(R.id.o2o_et_confirm_password_register)
    private EditText confirmPasswordRegister;
    @ViewInject(R.id.o2o_et_password_register)
    private EditText passwordRegister;
    @ViewInject(R.id.o2o_bt_registerbt_register)
    private Button register;
    @ViewInject(R.id.o2o_bt_back_register)
    private TextView back;
    @ViewInject(R.id.o2o_activity_register_rl)
    private RelativeLayout titleBack;
    private Intent intent = new Intent();
    private String userName,password,email,confirmPassword;
    private SameCod userMessageSameCod;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.o2o_activity_register);
        initView();
    }

    private void initView() {
        ViewUtils.inject(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.setClass(O2ORegisterActivity.this, O2OLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = userNameRegister.getText().toString();
                email = emailRegister.getText().toString();
                password = passwordRegister.getText().toString();
                confirmPassword = confirmPasswordRegister.getText().toString();
                doPost();
            }
        });
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.setClass(O2ORegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        intent.setClass(O2ORegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
    public void doPost() {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("username",userName).add("email", email).add("password",password).add("confirm_password",confirmPassword).build();
        Request builder = new Request.Builder().url(HttpUtil.O2O_USER_REGISTER).post(body).build();
        Log.e("+++++",userName+"*****"+password+"**********"+email+"*********"+confirmPassword);
        Call call = okHttpClient.newCall(builder);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("+++++",response.toString());
                String s = response.body().string();
                Log.e("+++++",s);
                initData(s);
            }
        });
    }


    private void initData(String jsonData) {
        Gson gson = new Gson();
        Type type = new TypeToken<SameCod<String>>() {
        }.getType();
        userMessageSameCod = gson.fromJson(jsonData,type);
//        Log.e("+++++",userMessageSameCod.result);
        if (userMessageSameCod.result.equals("success")){
            Message message = new Message();
            message.what = 0;
            handler.sendMessage(message);
        }
        else {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    }
// Toast.makeText(O2ORegisterActivity.this,"对不起请输入正确的信息",Toast.LENGTH_SHORT).show();
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
           switch (msg.what){
               case 0:{

                   Toast.makeText(O2ORegisterActivity.this,"注册成功自动返回登录界面",Toast.LENGTH_SHORT).show();
                   finish();

                   break;
               }
               case 1:{
                   Toast.makeText(O2ORegisterActivity.this,"对不起请输入正确的信息",Toast.LENGTH_SHORT).show();
                   break;
               }
           }
        }
    };

}
