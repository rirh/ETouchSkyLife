package com.etouchsky.o2o;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.etouchsky.pojo.SameCod;
import com.etouchsky.pojo.UserFromGoodsInfo;
import com.etouchsky.pojo.UserOrderGoods;
import com.etouchsky.util.CacheUtils;
import com.etouchsky.util.HttpUtil;
import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.wisdom.R;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/9/9 0009.
 */

public class MyFormGoodsInfo extends Activity {

    private int orderID;

    @ViewInject(R.id.o2o_my_from_goods_info_address_name)
    private TextView addressName;//收货人名称

    @ViewInject(R.id.o2o_my_from_goods_info_address_num)
    private TextView addressNum;//收货人号码

    @ViewInject(R.id.o2o_my_from_goods_info_address)
    private TextView address;//收货人地址

    @ViewInject(R.id.o2o_my_from_goods_info_shop)
    private TextView shop; //商家

    @ViewInject(R.id.o2o_my_from_goods_info_num)
    private TextView goodsNum;//订单号

    @ViewInject(R.id.o2o_my_from_goods_info_time)
    private TextView goodsTime;//订单时间

    @ViewInject(R.id.o2o_my_from_goods_info_list)
    private ListView goodsList;//订单列表

    @ViewInject(R.id.o2o_my_from_goods_info_shipping_name)
    private TextView shippingName;//配送方式

    @ViewInject(R.id.o2o_my_from_goods_info_shipping_fee)
    private TextView shippingFee; //配送金额

    @ViewInject(R.id.o2o_my_from_goods_info_goods_price)
    private TextView goodsPrice;//商品金额

    @ViewInject(R.id.o2o_my_from_goods_info_fee)
    private TextView Fee;//运费

    private Message message = new Message();
    private Type type;
    private SameCod sameCod;
    private UserFromGoodsInfo info;
    private GoodsInfoListAdapter goodsInfoListAdapter = new GoodsInfoListAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.o2o_my_from_goods_info);
        initView();
    }

    private void initView() {
        orderID = getIntent().getIntExtra("ORDER_ID", 0);
        ViewUtils.inject(this);
        OkAndGsonUtil.doGet(HttpUtil.O2O_USER_ORDER_INFO + "&user_id=" + CacheUtils.getUserId(this, CacheUtils.USER_ID, 0) + "&order_id=" + orderID).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                initGson(response.body().string());
            }

        });
    }

    private void initGson(String jsonData) {
        Log.e("订单详情",jsonData);
        type = new TypeToken<SameCod<UserFromGoodsInfo>>() {
        }.getType();
        sameCod = OkAndGsonUtil.gson.fromJson(jsonData, type);
        if (sameCod.result.equals("success")) {
            info = (UserFromGoodsInfo) sameCod.info;
            message.what = 0x02;  //02代表成功获取数据
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
                    addressName.setText(info.getConsignee().toString());
                    addressNum.setText(info.getMobile().toString());
                    address.setText(info.getAddress().toString());
                    shop.setText(info.getUserName().toString());
                    goodsNum.setText("订单号:" + info.getOrderSn().toString());
                    goodsTime.setText(info.getOrderTime().toString());
                    shippingName.setText(info.getShippingName().toString());
                    shippingFee.setText(info.getShippingFee().toString());
                    goodsPrice.setText(info.getTotalFee().toString().substring(10));
                    goodsList.setAdapter(goodsInfoListAdapter);
                    goodsInfoListAdapter.setListViewHeightBasedOnChildren(goodsList);
                    break;
                case 0x03:
                    break;
            }
        }
    };

    private class GoodsInfoListAdapter extends BaseAdapter {
        private GoodsViewHolder viewHolder;

        @Override
        public int getCount() {
            return info.getOrderGoods().size();
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
                viewHolder = new GoodsViewHolder();
                convertView = LayoutInflater.from(MyFormGoodsInfo.this).inflate(R.layout.o2o_goods_list_layout, null);
                viewHolder.listGoodsName = (TextView) convertView.findViewById(R.id.goods_list_goods_name_tv);
                viewHolder.listGoodsMoney = (TextView) convertView.findViewById(R.id.goods_list_goods_money_tv);
                viewHolder.listGoodsNum = (TextView) convertView.findViewById(R.id.goods_list_goods_num_tv);
                viewHolder.listGoodsPicture = (ImageView) convertView.findViewById(R.id.goods_list_goods_picture_iv);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (GoodsViewHolder) convertView.getTag();
            }
            viewHolder.listGoodsName.setText(info.getOrderGoods().get(position).getGoodsName());
            viewHolder.listGoodsMoney.setText("￥" + info.getOrderGoods().get(position).getGoodsPrice().substring(10));
            viewHolder.listGoodsNum.setText("x" + info.getOrderGoods().get(position).getGoodsNumber());
            ImageLoader.getInstance().displayImage(info.getOrderGoods().get(position).getGoodsThumb(),
                    viewHolder.listGoodsPicture);
            return convertView;
        }

        private class GoodsViewHolder {
            private TextView listGoodsName, listGoodsMoney, listGoodsNum;
            private ImageView listGoodsPicture;

        }

        //        动态设置ListView的高度
        private void setListViewHeightBasedOnChildren(ListView listView) {
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                return;
            }
            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();

            params.height = totalHeight
                    + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
        }
    }
}



