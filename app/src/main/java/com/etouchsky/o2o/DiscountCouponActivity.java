package com.etouchsky.o2o;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.etouchsky.pojo.DiscountCouponInfo;
import com.etouchsky.pojo.DiscountCouponInfoList;
import com.etouchsky.pojo.SameCod;
import com.etouchsky.util.CacheUtils;
import com.etouchsky.util.HttpUtil;
import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.wisdom.R;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/9/5 0005.
 */

public class DiscountCouponActivity extends Activity implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup mRadioGroup;
    private FrameLayout mFrameLayout;
    private TextView hint,title;
    private ListView discountListView;

    private Message message1;
    private Type type;
    private SameCod sameCod;

    private DiscountCouponInfo discountCouponInfo;
    private AbsListView.LayoutParams layoutParams;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.o2o_my_discount_coupon);
        initView();
        OkAndGsonUtil.setTitleBar(this,"我的卡券");

    }


    /*参考代码提示符
        */


    private void initView() {
        mRadioGroup = (RadioGroup) findViewById(R.id.o2o_my_discount_coupon_rg);
        mFrameLayout = (FrameLayout) findViewById(R.id.o2o_discount_coupon_fl);
        hint = (TextView) findViewById(R.id.o2o_discount_coupon_hint);
        title = (TextView) findViewById(R.id.private_message_title);
        title.setText("我的卡券");
        layoutParams = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.MATCH_PARENT);
        discountListView = new ListView(DiscountCouponActivity.this);
        discountListView.setPadding(20, 30, 10, 20);
        discountListView.setDivider(null);
        mRadioGroup.check(R.id.o2o_my_discount_coupon_unused);
        mRadioGroup.setOnCheckedChangeListener(this);
        initData("0");

    }

    private void initData(String status) {
        OkAndGsonUtil.doGet(HttpUtil.O2O_USER_COUPON + "&user_id=" + CacheUtils.getUserId(this, CacheUtils.USER_ID, 0) + "&coupon_status=" + status).enqueue(new Callback() {
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
        message1 = new Message();
        type = new TypeToken<SameCod<DiscountCouponInfo>>() {
        }.getType();
        sameCod = OkAndGsonUtil.gson.fromJson(jsonData, type);
        if (sameCod.result.equals("success")) {
            discountCouponInfo = (DiscountCouponInfo) sameCod.info;
            message1.what = 0x02;  //02代表有优惠券
            handler.sendMessage(message1);
        } else {
            message1.what = 0x03;  //03代表获取数据失败
            handler.sendMessage(message1);
        }
    }

    private Handler handler = new Handler() {

        private DiscountMessageAdapter discountMessageAdapter;
        private boolean isOpenListView = false;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x02:
                    hint.setVisibility(View.GONE);
                    discountListView.setVisibility(View.VISIBLE);
                    discountMessageAdapter = new DiscountMessageAdapter(DiscountCouponActivity.this);
                    discountListView.setAdapter(discountMessageAdapter);
                    if (isOpenListView) {
                        mFrameLayout.removeView(discountListView);
                        mFrameLayout.addView(discountListView, layoutParams);
                    } else {
                        mFrameLayout.addView(discountListView, layoutParams);
                        isOpenListView = true;
                    }
                    break;
                case 0x03:
                    if (discountListView != null)
                        discountListView.setVisibility(View.GONE);
                    hint.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId){
            case R.id.o2o_my_discount_coupon_unused:
                initData("0");
                break;
            case R.id.o2o_my_discount_coupon_used:
                initData("1");
                break;
            case R.id.o2o_my_discount_coupon_past:
                initData("2");
                break;
        }
    }


    class DiscountMessageAdapter extends BaseAdapter {
        private Context context;
        private MessageViewHolder holder;
        private DiscountCouponInfoList discountCouponInfoList;

        public DiscountMessageAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return discountCouponInfo.getList().size();
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
            discountCouponInfoList = discountCouponInfo.getList().get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.o2o_discount_coupon_list, null);
                holder = new MessageViewHolder();
                holder.couTitle = (TextView) convertView.findViewById(R.id.o2o_cou_title);
                holder.couMan = (TextView) convertView.findViewById(R.id.o2o_cou_man);
                holder.counponTime = (TextView) convertView.findViewById(R.id.o2o_discount_coupon_time);
                holder.couMoney = (TextView) convertView.findViewById(R.id.o2o_cou_money);
                convertView.setTag(holder);
            } else
                holder = (MessageViewHolder) convertView.getTag();
            holder.couMoney.setText("￥"+discountCouponInfoList.getCouMoney());
            holder.couTitle.setText(discountCouponInfoList.getCouTitle());
            holder.couMan.setText("满"+discountCouponInfoList.getCouMan()+"元可用");
            holder.counponTime.setText(discountCouponInfoList.getBegintime()+" 至 "+discountCouponInfoList.getEndtime());
            return convertView;
        }

        private class MessageViewHolder {
            private TextView couTitle, couMan, counponTime, couMoney;

        }
    }
}
