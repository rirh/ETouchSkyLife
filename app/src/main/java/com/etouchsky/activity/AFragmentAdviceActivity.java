package com.etouchsky.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.etouchsky.adapter.AFragmentAdviceAdapter;
import com.etouchsky.data.DataHelper;
import com.etouchsky.pojo.AFragmentAdviceData;
import com.etouchsky.pojo.AFragmentAdviceIndexInfo;
import com.etouchsky.pojo.AFragmentAdviceNew;
import com.etouchsky.pojo.AFragmentIndexPostInfo;
import com.etouchsky.pojo.AFragmentIndexPostInfos;
import com.etouchsky.pojo.SameCod;
import com.etouchsky.pojo.UserAddressInfo;
import com.etouchsky.util.CacheUtils;
import com.etouchsky.util.HttpUtil;
import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.view.PublishedActivity;
import com.etouchsky.wisdom.MainActivity;
import com.etouchsky.wisdom.R;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/9/19 0019.
 */

public class AFragmentAdviceActivity extends Activity implements View.OnClickListener {


    private TextView hint, insertTv;
    private Button insertBt;
    private FrameLayout mFrameLayout;
    private Type type;
    private ListView messageListView;
    private AbsListView.LayoutParams layoutParams;
    private boolean isOpenListView = false;
    private Message message = new Message();
    private Intent intent = new Intent();
    private String jsonObject;
    private AFragmentAdviceIndexInfo aFragmentAdviceIndexInfo;
    private int choicePage;
    private String Url;
    private AFragmentAdviceNew mAFragmentIndexPostInfo;
    private ProgressDialog dialog;
    private AFragmentAdviceAdapter messageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_fragment_advice_list);
        choicePage = getIntent().getIntExtra("AFragment",0x00);
        initView();

    }

    //初始化控件并获取网络数据
    private void initView() {
        dialog = OkAndGsonUtil.showProgressDialog(AFragmentAdviceActivity.this);
        switch (choicePage){
            case 0x01:   //0x01为投诉按钮
                OkAndGsonUtil.setHouseTitleBar(AFragmentAdviceActivity.this, "投诉");
                Url = HttpUtil.HOUSE_ADVICE_INDEX;
                intent.putExtra("INDEX",0x01);
                break;
            case 0x02:  //0x02为报修按钮
                OkAndGsonUtil.setHouseTitleBar(AFragmentAdviceActivity.this, "报修");
                Url = HttpUtil.HOUSE_TROUBLE_INDEX;
                intent.putExtra("INDEX",0x02);
                break;
        }

        hint = (TextView) findViewById(R.id.a_fragment_advice_hint);
        insertTv = (TextView) findViewById(R.id.a_fragment_insert_advice_bt);
        insertBt = (Button) findViewById(R.id.a_fragment_again_insert_advice_bt);
        insertBt.setOnClickListener(this);
        insertTv.setOnClickListener(this);
        messageListView = new ListView(AFragmentAdviceActivity.this);
        messageListView.setDivider(null);
        mFrameLayout = (FrameLayout) findViewById(R.id.a_fragment_advice_fl);
        layoutParams = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT);
        DataHelper dbHelper = new DataHelper(this);
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from CommunitiesInfo where Id = 1",null);
        while(cursor.moveToNext()){
            mAFragmentIndexPostInfo = new AFragmentAdviceNew( CacheUtils.getAccount(AFragmentAdviceActivity.this, CacheUtils.USER_ACCOUNT, "000"),cursor.getInt(cursor.getColumnIndex("CmtID")),cursor.getString(cursor.getColumnIndex("UnitNo")),cursor.getString(cursor.getColumnIndex("RoomNo"))); //13750509674
//            String s1 = cursor.getString(cursor.getColumnIndex("UnitNo"));
//            String s2 = cursor.getString(cursor.getColumnIndex("RoomNo"));
//            String s3 = cursor.getString(cursor.getColumnIndex("UnitName"));
        }
        //关闭数据库
        db.close();
        jsonObject = OkAndGsonUtil.gson.toJson(mAFragmentIndexPostInfo);
        Log.e("*****传入的数据","jsonObject:"+jsonObject);
//        initData("{\"status\":0,\"data\":[{\"rid\":\"1\",\"advice_title\":\"\\u6295\\u8bc9\\u6807\\u9898\",\"remark\":\"\\u6295\\u8bc9\\u5185\\u5bb9\",\"unit_id\":\"20\",\"unit_name\":\" 01\\u680b 0603\",\"room_no\":\"0603\",\"community_id\":\"23\",\"community_name\":\"\\u674e\\u6717\\u56fd\\u9645\\u73e0\\u5b9d\\u56ed\",\"state\":\"\\u5df2\\u89e3\\u51b3\",\"complete_date\":\"2017-07-23 17:02:03\",\"complete_content\":\"sdafsdafsdf\",\"create_time\":\"2017-07-23 17:02:03\"}]}");
        OkAndGsonUtil.doHousePost(jsonObject, Url).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                message.what = 0x03;  //03代表获取数据失败
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                initData(response.body().string());
            }
        });
        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                intent.setClass(AFragmentAdviceActivity.this,AFragmentAdviceInfoActivity.class);

                AFragmentAdviceData data =  (AFragmentAdviceData) aFragmentAdviceIndexInfo.getData().get(position);

                intent.putExtra("RID",Integer.valueOf(data.getRid()).intValue());
                startActivity(intent);
            }
        });
    }

    private void initData(String jsonData) {
        Log.e("****", jsonData);
        type = new TypeToken<AFragmentAdviceIndexInfo<AFragmentAdviceData>>() {
        }.getType();
        aFragmentAdviceIndexInfo = OkAndGsonUtil.gson.fromJson(jsonData, type);
        if (aFragmentAdviceIndexInfo.getStatus() == 0 && aFragmentAdviceIndexInfo.getData().size()>0) {
            message.what = 0x02;  //代表获取数据成功
            handler.sendMessage(message);
        } else {
            message.what = 0x03;  //03代表获取数据失败
            handler.sendMessage(message);
        }
    }


   private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x02:
                    hint.setVisibility(View.GONE);
                    insertTv.setVisibility(View.GONE);
                    insertBt.setVisibility(View.VISIBLE);
                    insertBt.setOnClickListener(AFragmentAdviceActivity.this);
                    messageListView.setVisibility(View.VISIBLE);
                    messageAdapter = new AFragmentAdviceAdapter(AFragmentAdviceActivity.this, aFragmentAdviceIndexInfo);
                    messageListView.setAdapter(messageAdapter);
                    if (isOpenListView) {
                        mFrameLayout.removeView(messageListView);
                        mFrameLayout.addView(messageListView, layoutParams);
                        OkAndGsonUtil.closeProgressDialog(dialog);
                    } else {
                        mFrameLayout.addView(messageListView, layoutParams);
                        isOpenListView = true;
                        OkAndGsonUtil.closeProgressDialog(dialog);
                    }
                    break;
                case 0x03:
                    if (messageListView != null)
                        messageListView.setVisibility(View.GONE);
                    hint.setVisibility(View.VISIBLE);
                    insertTv.setVisibility(View.VISIBLE);
                    OkAndGsonUtil.closeProgressDialog(dialog);
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.a_fragment_insert_advice_bt:
//                插入用户建议 或插入投诉信息
                intent.setClass(AFragmentAdviceActivity.this, PublishedActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.a_fragment_again_insert_advice_bt:
//               插入的用户建议 或插入投诉信息
                intent.setClass(AFragmentAdviceActivity.this, PublishedActivity.class);
                startActivity(intent);

                finish();
                break;
        }
    }
}
