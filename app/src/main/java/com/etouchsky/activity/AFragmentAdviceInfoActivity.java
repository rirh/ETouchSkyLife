package com.etouchsky.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.etouchsky.pojo.AFragmentAdviceDataInfo;
import com.etouchsky.pojo.AFragmentAdviceInfo;
import com.etouchsky.pojo.AFragmentIndexPostInfo;
import com.etouchsky.util.CacheUtils;
import com.etouchsky.util.HttpUtil;
import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.view.Bimp;
import com.etouchsky.view.PhotoActivity;
import com.etouchsky.view.PublishedActivity;
import com.etouchsky.wisdom.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/9/20 0020.
 */

public class AFragmentAdviceInfoActivity extends Activity {

    private String jsonObject;
    private Message message = new Message();
    private TextView title, adviceMessage,beginTime,endTime;
    private AFragmentAdviceInfo info ;
    private int choicePage, rid;
    private String url;
    private GridView imageGrid;
    private GridAdviceAdapter adapter;
    private AFragmentAdviceDataInfo aFragmentAdviceIndexInfo;
    private ProgressDialog dialog;
    private Intent intent = new Intent();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_insert_advice_layout);
        rid = getIntent().getIntExtra("RID", 0x00);
        choicePage = getIntent().getIntExtra("INDEX", 0x00);
        initView();

    }

    private void initView() {

        dialog = OkAndGsonUtil.showProgressDialog(this);

        switch (choicePage) {
            case 0x01:   //0x01为投诉按钮
                OkAndGsonUtil.setHouseTitleBar(AFragmentAdviceInfoActivity.this, "投诉");
                url = HttpUtil.HOUSE_ADVICE_DETAIL;
                break;
            case 0x02:  //0x02为报修按钮
                OkAndGsonUtil.setHouseTitleBar(AFragmentAdviceInfoActivity.this, "报修");
                url = HttpUtil.HOUSE_TROUBLE_DETAIL;
                break;
        }
        imageGrid = (GridView) findViewById(R.id.a_insert_advice_image_grid);
        title = (TextView) findViewById(R.id.a_insert_advice_title);
        adviceMessage = (TextView) findViewById(R.id.a_insert_advice_message);
        beginTime = (TextView) findViewById(R.id.a_insert_advice_begin_time);
        endTime = (TextView) findViewById(R.id.a_insert_advice_end_time);
        adapter = new GridAdviceAdapter();
        AFragmentIndexPostInfo aFragmentIndexPostInfo = new AFragmentIndexPostInfo(CacheUtils.getAccount(AFragmentAdviceInfoActivity.this, CacheUtils.USER_ACCOUNT, "000"), rid);
        jsonObject = OkAndGsonUtil.gson.toJson(aFragmentIndexPostInfo);
//        initData("{\"status\":0,\"data\":{\"rid\":\"1\",\"trouble_title\":\"\\u6295\\u8bc9\\u6807\\u9898\",\"remark\":\"\\u6295\\u8bc9\\u5185\\u5bb9\",\"images\":[\"112.74.164.111:83\\/Uploads\\/Picture\\/2017-07-23\\/59746f3433446.jpg\",\"112.74.164.111:83\\/Uploads\\/Picture\\/2017-07-23\\/59746ff41c116.png\",\"112.74.164.111:83\\/Uploads\\/Picture\\/2017-07-23\\/59746ff424200.png\",\"112.74.164.111:83\\/Uploads\\/Picture\\/2017-07-23\\/59746ff4274c9.png\"],\"unit_id\":\"20\",\"unit_name\":\" 01\\u680b 0603\",\"room_no\":\"0603\",\"community_id\":\"23\",\"community_name\":\"\\u674e\\u6717\\u56fd\\u9645\\u73e0\\u5b9d\\u56ed\",\"state\":\"\\u5df2\\u89e3\\u51b3\",\"complete_date\":\"2017-07-23 17:02:03\",\"complete_content\":\"sdafsdafsdf\",\"create_time\":\"2017-07-23 17:02:03\"}}");
        OkAndGsonUtil.doHousePost(jsonObject, url).enqueue(new Callback() {
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


        imageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent.setClass(AFragmentAdviceInfoActivity.this,ImageViewActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("DataInfo",aFragmentAdviceIndexInfo);
                startActivity(intent);
            }
        });

    }

    private void initData(String jsonData) {
        Log.e("+++++++++++", jsonData);

        aFragmentAdviceIndexInfo = OkAndGsonUtil.gson.fromJson(jsonData, AFragmentAdviceDataInfo.class);
//        if(aFragmentAdviceIndexInfo.getData() == null){
//            Toast.makeText(AFragmentAdviceInfoActivity.this,"对象为空",Toast.LENGTH_SHORT).show();
//        }
//     Toast.makeText(AFragmentAdviceInfoActivity.this,aFragmentAdviceIndexInfo.getData().getAdviceTitle()+"",Toast.LENGTH_SHORT).show();
//        Log.e("+++++++++++",info.getAdviceTitle());
        if (aFragmentAdviceIndexInfo.getStatus() == 0) {
            message.what = 0x02;  //代表获取数据成功
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
                    title.setText(""+aFragmentAdviceIndexInfo.getData().getAdviceTitle());
                    adviceMessage.setText(""+ aFragmentAdviceIndexInfo.getData().getRemark());
                    beginTime.setText("提交时间:"+aFragmentAdviceIndexInfo.getData().getCreateTime().substring(0,11));
//                    endTime.setText("回复时间:"+aFragmentAdviceIndexInfo.getData().getCompleteDate().substring(0,11));
                    imageGrid.setAdapter(adapter);
                    OkAndGsonUtil.closeProgressDialog(dialog);
                    break;
                case 0x03:
                    Toast.makeText(AFragmentAdviceInfoActivity.this, "与服务器连接未响应", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    private class GridAdviceAdapter extends BaseAdapter{
        private ViewHolder holder;
        @Override
        public int getCount() {
            return aFragmentAdviceIndexInfo.getData().getImages().size();
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
            if (convertView == null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(AFragmentAdviceInfoActivity.this).inflate(R.layout.item_published_grida,null);
                holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ImageLoader.getInstance().displayImage( aFragmentAdviceIndexInfo.getData().getImages().get(position), holder.image);
//            http://112.74.164.111:83/Uploads/Picture/2017-07-23/59746f3433446.jpg
            return convertView;
        }

        private class ViewHolder {
            public ImageView image;
        }
    }
}
