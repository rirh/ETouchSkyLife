package com.etouchsky.o2o;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.etouchsky.pojo.CollectShopInfoList;
import com.etouchsky.pojo.CollectStoreInfoList;
import com.etouchsky.pojo.SameCod;
import com.etouchsky.pojo.SameCodInfo;
import com.etouchsky.util.CacheUtils;
import com.etouchsky.util.HttpUtil;
import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.wisdom.R;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/9/6 0006.
 */

public class CollectShopActivity extends Activity {


    private ListView listView;
    private Message message = new Message();
    private Type type;
    private SameCod sameCod;
    private CollectShopAdapter collectShopAdapter;
    private int countSize,choice;
    private boolean isOpenListView = false;
    private AbsListView.LayoutParams layoutParams;
    private FrameLayout mFromMessage;
    private TextView hintText,title;
    private RadioGroup radioGroup;
    private SameCodInfo<CollectShopInfoList> sameCodInfo;
    private SameCodInfo<CollectStoreInfoList> storeInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.o2o_my_form);
        collectShopAdapter = new CollectShopAdapter(CollectShopActivity.this);
        choice = getIntent().getIntExtra("CollectShop", 2);
        initView();
        switch (choice) {
            case 0:
                initData0();//初始化商品收藏
                break;
            case 1:
                initData1();//初始化店铺收藏
                break;
        }
        OkAndGsonUtil.setTitleBar(this,"收藏管理");

    }

    private void initView(){
        mFromMessage = (FrameLayout) findViewById(R.id.o2o_my_form_fl_from_message);
        hintText = (TextView) findViewById(R.id.o2o_my_form_tv_hint);
        radioGroup = (RadioGroup) findViewById(R.id.o2o_my_form_rg_title_bar);
        title = (TextView) findViewById(R.id.private_message_title);
        title.setText("我的收藏");
        radioGroup.setVisibility(View.GONE);
        listView = new ListView(CollectShopActivity.this);
        layoutParams = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.MATCH_PARENT);
    }


    private void initData0() {
        OkAndGsonUtil.doGet(HttpUtil.O2O_USER_COLLECT + "&user_id=" + CacheUtils.getUserId(this, CacheUtils.USER_ID, 0)).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                type = new TypeToken<SameCod<SameCodInfo<CollectShopInfoList>>>() {
                }.getType();
                sameCod = OkAndGsonUtil.gson.fromJson(response.body().string(), type);
                if (sameCod.result.equals("success")) {
                    sameCodInfo = (SameCodInfo) sameCod.info;
                    countSize = sameCodInfo.getList().size();
                    message.what = 0x02;  //02代表获取数据成功
                    handler.sendMessage(message);
                } else {
                    message.what = 0x03;  //03代表获取数据失败
                    handler.sendMessage(message);
                }
            }
        });
    }


    private void initData1() {
        OkAndGsonUtil.doGet(HttpUtil.O2O_USER_STORE + "&user_id=" + CacheUtils.getUserId(this, CacheUtils.USER_ID, 0)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                type = new TypeToken<SameCod<SameCodInfo<CollectStoreInfoList>>>() {
                }.getType();
                sameCod = OkAndGsonUtil.gson.fromJson(response.body().string(), type);
                if (sameCod.result.equals("success")) {
                    storeInfo = (SameCodInfo<CollectStoreInfoList>) sameCod.info;
                    countSize = storeInfo.getList().size();
                    message.what = 0x02;  //02代表获取数据成功
                    handler.sendMessage(message);
                } else {
                    message.what = 0x03;  //03代表获取数据失败
                    handler.sendMessage(message);
                }
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what) {
                case 0x02:
                    hintText.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    collectShopAdapter = new CollectShopAdapter(CollectShopActivity.this);
                    listView.setAdapter(collectShopAdapter);
                    if (isOpenListView) {
                        mFromMessage.removeView(listView);
                        mFromMessage.addView(listView, layoutParams);
                    } else {
                        mFromMessage.addView(listView, layoutParams);
                        isOpenListView = true;
                    }
                    break;
                case 0x03:
                    if (listView != null)
                        listView.setVisibility(View.GONE);
                    hintText.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    class CollectShopAdapter extends BaseAdapter {

        private Context context;
        private ViewHolder viewHolder;

        public CollectShopAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return countSize;
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
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.o2o_cpllect_shop_listview_layout,null);
                viewHolder.shopIv = (ImageView) convertView.findViewById(R.id.o2o_collect_shop_lv_iv);
                viewHolder.shopNameTv = (TextView) convertView.findViewById(R.id.o2o_collect_shop_name_ll_tv);
                viewHolder.shopNumTv = (TextView) convertView.findViewById(R.id.o2o_collect_shop_num_ll_tv);
                viewHolder.shopPictureTv = (TextView) convertView.findViewById(R.id.o2o_collect_shop_picture_ll_tv);
                convertView.setTag(convertView);
            }else
                viewHolder = (ViewHolder) convertView.getTag();
            //关注商店
            switch (choice) {
                case 0:
                    //店铺收藏
                    ImageLoader.getInstance().displayImage(sameCodInfo.getList().get(position).getGoodsThumb(),
                            viewHolder.shopIv);
                    viewHolder.shopNameTv.setText(sameCodInfo.getList().get(position).getGoodsName());
                    viewHolder.shopNumTv.setText("￥"+sameCodInfo.getList().get(position).getGoodsPrice().substring(10));
                    break;
                case 1:
                    ImageLoader.getInstance().displayImage(storeInfo.getList().get(position).getBrandThumb(),
                            viewHolder.shopIv);
                    viewHolder.shopNameTv.setText(storeInfo.getList().get(position).getStoreName());
                    viewHolder.shopNumTv.setTextColor(0xFFAAAAAA);
                    viewHolder.shopNumTv.setText("已经有"+storeInfo.getList().get(position).getCollectNumber()+"人关注");
                    break;}
            return convertView;
        }

        private class ViewHolder {
            private ImageView shopIv;
            private TextView shopNameTv, shopNumTv, shopPictureTv;
        }
    }
}
