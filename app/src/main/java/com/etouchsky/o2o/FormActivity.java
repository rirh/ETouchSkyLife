package com.etouchsky.o2o;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.etouchsky.pojo.SameCod;
import com.etouchsky.pojo.UserFormList;
import com.etouchsky.pojo.UserInfoFrom;
import com.etouchsky.pojo.UserOrderGoods;
import com.etouchsky.util.CacheUtils;
import com.etouchsky.util.HttpUtil;
import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.wisdom.R;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/9/1 0001.
 */

public class FormActivity extends Activity implements RadioGroup.OnCheckedChangeListener {

    private FrameLayout mFromMessage;
    private RadioGroup mRadioGroup;
    private MessageAdapter messageAdapter;
    private TextView hintText;
    private Type type;
    private SameCod sameCod;
    private UserInfoFrom userInfoFrom;
    private Message message;
    private boolean isOpenListView = false;
    private ListView messageListView;
    private AbsListView.LayoutParams layoutParams;
    private Intent intent = new Intent();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x02:
                    hintText.setVisibility(View.GONE);
                    messageListView.setVisibility(View.VISIBLE);
                    messageAdapter = new MessageAdapter(FormActivity.this);
                    messageListView.setAdapter(messageAdapter);
                    if (isOpenListView) {
                        mFromMessage.removeView(messageListView);
                        mFromMessage.addView(messageListView, layoutParams);
                    } else {
                        mFromMessage.addView(messageListView, layoutParams);
                        isOpenListView = true;
                    }
                    break;
                case 0x03:
                    if (messageListView != null)
                        messageListView.setVisibility(View.GONE);
                    hintText.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.o2o_my_form);
        initView();
        OkAndGsonUtil.setTitleBar(this,"订单管理");

    }
    //    初始化列表和单选框
    private void initView() {
        mFromMessage = (FrameLayout) findViewById(R.id.o2o_my_form_fl_from_message);
        mRadioGroup = (RadioGroup) findViewById(R.id.o2o_my_form_rg_title_bar);
        hintText = (TextView) findViewById(R.id.o2o_my_form_tv_hint);
        mRadioGroup = (RadioGroup) findViewById(R.id.o2o_my_form_rg_title_bar);
        messageListView = new ListView(FormActivity.this);
        layoutParams = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.MATCH_PARENT);
        switch (getIntent().getIntExtra("From", 4)) {
            case 0:
                mRadioGroup.check(R.id.o2o_my_form_rb_all_from);
                initData("0");
                break;
            case 1:
                mRadioGroup.check(R.id.o2o_my_form_rb_wait_pay);
                initData("1");
                break;
            case 2:
                mRadioGroup.check(R.id.o2o_my_form_rb_wait_take);
                initData("2");
                break;
            case 3:
                mRadioGroup.check(R.id.o2o_my_form_rb_wait_appraise);
                if (messageListView != null)
                    messageListView.setVisibility(View.GONE);
                hintText.setVisibility(View.VISIBLE);
                break;
        }
        mRadioGroup.setOnCheckedChangeListener(this);
    }

    private void initData(String status) {
        OkAndGsonUtil.doGet(HttpUtil.O2O_USER_ORDER_LIST + "&user_id=" + CacheUtils.getUserId(this, CacheUtils.USER_ID, 0) + "&status=" + status).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                initGson(response.body().string().toString());
            }

        });

    }

    private void initGson(String jsonData) {
        Log.e("*****","订单信息"+jsonData);
        message = new Message();
        type = new TypeToken<SameCod<UserInfoFrom>>() {
        }.getType();
        sameCod = OkAndGsonUtil.gson.fromJson(jsonData, type);
        if (sameCod.result.equals("success")) {
            userInfoFrom = (UserInfoFrom) sameCod.info;
            message.what = 0x02;  //02代表订单
            handler.sendMessage(message);
        } else {//
            message.what = 0x03;  //03代表获取数据失败
            handler.sendMessage(message);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.o2o_my_form_rb_all_from:
                initData("0");
                break;
            case R.id.o2o_my_form_rb_wait_pay:
                initData("1");
                break;
            case R.id.o2o_my_form_rb_wait_take:
                initData("2");
                break;
            case R.id.o2o_my_form_rb_wait_appraise:
                if (messageListView != null)
                    messageListView.setVisibility(View.GONE);
                hintText.setVisibility(View.VISIBLE);
                break;
        }

    }

    //   订单列表适配器
    class MessageAdapter extends BaseAdapter {
        private Context context;
        private MessageViewHolder holder;
        private List<UserFormList> userFormLists = userInfoFrom.getList();
        private UserFormList userFormList;
        private GoodsListAdapter goodsListAdapter;

        public MessageAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {

            return userInfoFrom.getList().size();
        }

        @Override
        public Object getItem(int position) {
            return userFormList;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            userFormList = userFormLists.get(position);
            if (convertView == null) {
                holder = new MessageViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.o2o_my_from_list_layout, null);
                holder.storeName = (TextView) convertView.findViewById(R.id.my_form_list_store);
                holder.goodsStatue = (TextView) convertView.findViewById(R.id.my_form_list_statue);
                holder.goodsNum = (TextView) convertView.findViewById(R.id.goods_num);
                holder.goodsTime = (TextView) convertView.findViewById(R.id.goods_time);
                holder.sumMoney = (TextView) convertView.findViewById(R.id.mu_form_tv_goods_num_money);
                holder.sumGoods = (TextView) convertView.findViewById(R.id.mu_form_tv_goods_num);
                holder.cancelForm = (Button) convertView.findViewById(R.id.mu_form_bt_cancel_form);
                holder.goodsList = (ListView) convertView.findViewById(R.id.my_form_list_goods_list_fl);
//                holder.cancelForm.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    }
//                });
                holder.goodsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    private int i;
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        i = (int) parent.getTag(R.id.tag_first);
                        intent.setClass(FormActivity.this, MyFormGoodsInfo.class);
                        intent.putExtra("ORDER_ID", Integer.valueOf(userFormLists.get(i).getOrderId()).intValue());
                        startActivity(intent);
                    }
                });
                convertView.setTag(holder);
            } else {
                holder = (MessageViewHolder) convertView.getTag();
            }
            holder.storeName.setText(userFormList.getUserName());
            holder.goodsStatue.setText(userFormList.getOrderStatus());
            holder.goodsNum.setText("订单号:" + userFormList.getOrderSn());
            holder.goodsTime.setText(userFormList.getOrderTime() + "");
            holder.sumMoney.setText("合计:￥" + userFormList.getTotalFee().substring(10));
            holder.sumGoods.setText("共" + userFormList.getOrderGoodsNum() + "件商品");
            goodsListAdapter = new GoodsListAdapter(context, userFormList);
            holder.goodsList.setAdapter(goodsListAdapter);
            holder.goodsList.setTag(R.id.tag_first,position);
            goodsListAdapter.setListViewHeightBasedOnChildren(holder.goodsList);
            return convertView;
        }
        private class MessageViewHolder {
            private TextView storeName, goodsStatue, goodsNum, goodsTime, sumMoney, sumGoods;
            private Button cancelForm;
            private ListView goodsList;

        }
    }
    //商品列表适配器
    class GoodsListAdapter extends BaseAdapter {
        private Context context;
        private GoodsViewHolder viewHolder;
        private UserFormList userFormList;
        private UserOrderGoods userOrderGoods;

        public GoodsListAdapter(Context context, UserFormList userFormList) {
            this.context = context;
            this.userFormList = userFormList;

        }

        @Override
        public int getCount() {
            return userFormList.getOrderGoods().size();
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
            userOrderGoods = userFormList.getOrderGoods().get(position);
            if (convertView == null) {
                viewHolder = new GoodsViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.o2o_goods_list_layout, null);
                viewHolder.listGoodsName = (TextView) convertView.findViewById(R.id.goods_list_goods_name_tv);
                viewHolder.listGoodsMoney = (TextView) convertView.findViewById(R.id.goods_list_goods_money_tv);
                viewHolder.listGoodsNum = (TextView) convertView.findViewById(R.id.goods_list_goods_num_tv);
                viewHolder.listGoodsPicture = (ImageView) convertView.findViewById(R.id.goods_list_goods_picture_iv);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (GoodsViewHolder) convertView.getTag();
            }
            viewHolder.listGoodsName.setText(userOrderGoods.getGoodsName());
            viewHolder.listGoodsMoney.setText("￥" + userOrderGoods.getGoodsPrice().substring(10));
            viewHolder.listGoodsNum.setText("x" + userOrderGoods.getGoodsNumber());
            ImageLoader.getInstance().displayImage(userOrderGoods.getGoodsThumb(),
                    viewHolder.listGoodsPicture);
            return convertView;
        }

        private class GoodsViewHolder {
            private TextView listGoodsName, listGoodsMoney, listGoodsNum;
            private ImageView listGoodsPicture;

        }

        //        动态设置ListView的高度
        public void setListViewHeightBasedOnChildren(ListView listView) {
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
