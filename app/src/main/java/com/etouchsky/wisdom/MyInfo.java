package com.etouchsky.wisdom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ddclient.dongsdk.AbstractDongCallbackProxy.DongAccountCallbackImp;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.jnisdk.InfoUser;
import com.etouchsky.activity.PrivateMessageActivity;
import com.etouchsky.o2o.CollectShopActivity;
import com.etouchsky.o2o.DiscountCouponActivity;
import com.etouchsky.o2o.ExchangeGoodActivity;
import com.etouchsky.o2o.FormActivity;
import com.etouchsky.o2o.GroupBuyActivity;
import com.etouchsky.o2o.MyShareActivity;
import com.etouchsky.o2o.NoPageActivity;
import com.etouchsky.o2o.RedPacketActivity;
import com.etouchsky.o2o.WalletActivity;
import com.etouchsky.pojo.SameCod;
import com.etouchsky.pojo.UserInfo;
import com.etouchsky.pojo.UserInfos;
import com.etouchsky.util.CacheUtils;
import com.etouchsky.util.HttpUtil;
import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.util.PictureScanner;
import com.gViewerX.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyInfo extends Fragment implements View.OnClickListener {
    private View infoView;

    @ViewInject(R.id.my_info_re_private_message)
    private RelativeLayout myInfoSetting;  //个人信息设置

    @ViewInject(R.id.my_info_re_from)
    private RelativeLayout myFrom;     //订单管理

    @ViewInject(R.id.my_info_ll_wait_pay)
    private LinearLayout myWaitPay; //代付款

    @ViewInject(R.id.my_info_ll_wait_groupon)
    private LinearLayout myWaitGroupon; //待拼团

    @ViewInject(R.id.my_info_ll_wait_take)
    private LinearLayout myWaitTake;  //待收货

    @ViewInject(R.id.my_info_ll_wait_appraise)
    private LinearLayout myWaitAppraise; //待评价

    @ViewInject(R.id.my_info_ll_exchange_good)
    private LinearLayout myExchangeGood; //退换货

    @ViewInject(R.id.my_info_re_my_wallet)
    private RelativeLayout myWallet;  //我的钱包

    @ViewInject(R.id.my_info_ll_remaining_sum)
    private LinearLayout myRemainingSum;     //余额

    @ViewInject(R.id.my_info_ll_red_packet)
    private LinearLayout myRedPacket; //红包

    @ViewInject(R.id.my_info_ll_integral)
    private LinearLayout myIntegral; //积分

    @ViewInject(R.id.my_info_ll_discount_coupon)
    private LinearLayout myDiscountCoupon;  //优惠卷

    @ViewInject(R.id.my_info_ll_shop_collect)
    private LinearLayout myShopCollect; //商品收藏

    @ViewInject(R.id.my_info_ll_attention_shop)
    private LinearLayout myAttentionShop; //关注商店

    @ViewInject(R.id.my_info_ll_my_share)
    private LinearLayout myShare; //我的分享

    @ViewInject(R.id.my_info_ll_help_center)
    private LinearLayout myHelpCentter;  //帮助中心

    @ViewInject(R.id.my_info_ll_recruitment)
    private LinearLayout myRecruitment; //商家入驻

    @ViewInject(R.id.my_info_ll_browsing_history)
    private LinearLayout myBrowsingHistory; //浏览记录

    @ViewInject(R.id.my_info_re_user_name)
    private TextView myInfoName;       //我的昵称

    @ViewInject(R.id.my_info_iv_head_portrait)
    private ImageView mPicture;

    @ViewInject(R.id.my_info_ll_photo)
    private LinearLayout myPhoto; //我的相册

    @ViewInject(R.id.my_info_tv_remaining_sum)
    private TextView remaining; //余额

    @ViewInject(R.id.my_info_tv_red_packet_number)
    private TextView rePacket; //红包

    @ViewInject(R.id.my_info_tv_integral_number)
    private TextView integral; //积分

    @ViewInject(R.id.my_info_tv_discount_coupon_number)
    private TextView discountCoupon; //积分

    @ViewInject(R.id.o2o_my_form_wait_pay_num)
    private TextView waitPay;  //待付款

    @ViewInject(R.id.o2o_my_form_wait_groupon_num)
    private TextView waitGroupon;

    private Intent intent = new Intent();
    private Message message = new Message();
    //图片路径
    private static final String IMAGE = Environment.getExternalStorageDirectory().toString() + "/Viewer/image/";
    private UserInfos userInfo;
    private SameCod userMessageSameCod;
    private Type type;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        System.out.println("DDDDDDDDD____onAttach");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("DDDDDDDDD____onCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("DDDDDDDDD____onCreateView");
        infoView = inflater.inflate(R.layout.my_info, container, false);
        initView();
        return infoView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("DDDDDDDDD____onActivityCreated");

//        initView();
//        sp = MyInfo.this.getActivity().getSharedPreferences("userInfo", Context.MODE_APPEND);
//        TextView user_name = (TextView) this.getActivity().findViewById(R.id.user_name);
//        user_name.setText(sp.getString("account", "请登录"));
//        TextView app_exit = (TextView) this.getActivity().findViewById(R.id.app_exit);
//        app_exit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FileUtils.alertText(MyInfo.this.getActivity(), "警告", "您确定要退出！", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog,
//                                        int which) {
//                        switch (which) {
//                            case DialogInterface.BUTTON_POSITIVE:
//                                dialog.dismiss();
//                                sp.edit().putBoolean("flagLogin", false).commit();
//                                DongSDKProxy.requestSetPushInfo(PushInfo.PUSHTYPE_DEL);
//                                DongSDKProxy.loginOut();
//                                DongSDK.finishDongSDK();
//                                System.exit(0);
//                                break;
//                            case DialogInterface.BUTTON_NEGATIVE:
//                                dialog.dismiss();
//                                break;
//                        }
//                    }
//                });
//            }
//        });
    }

    private void Photo() {
        final PictureScanner pictureScanner = new PictureScanner(getActivity());
        if (pictureScanner.fileIsExists(IMAGE) &&  pictureScanner.PictureStart(IMAGE)) {
        } else Toast.makeText(getActivity(), "抱歉您还未拍摄安防照片", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("DDDDDDDDD____onStart");

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("DDDDDDDDD____onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("DDDDDDDDD____onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("DDDDDDDDD____onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("DDDDDDDDD____onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("DDDDDDDDD____onDetach");
    }


    private void initView() {
        ViewUtils.inject(this, infoView);
        myPhoto.setOnClickListener(this);
        myInfoSetting.setOnClickListener(this);
        myFrom.setOnClickListener(this);
        myWaitPay.setOnClickListener(this);
        myWaitGroupon.setOnClickListener(this);
        myWaitTake.setOnClickListener(this);
        myWaitAppraise.setOnClickListener(this);
        myExchangeGood.setOnClickListener(this);
        myWallet.setOnClickListener(this);
        myRemainingSum.setOnClickListener(this);
        myRedPacket.setOnClickListener(this);
        myIntegral.setOnClickListener(this);
        myDiscountCoupon.setOnClickListener(this);
        myShopCollect.setOnClickListener(this);
        myAttentionShop.setOnClickListener(this);
        myShare.setOnClickListener(this);
        myHelpCentter.setOnClickListener(this);
        myRecruitment.setOnClickListener(this);
        myBrowsingHistory.setOnClickListener(this);
        myInfoName.setText(CacheUtils.getName(getActivity(), CacheUtils.USER_NAME, "无法获取"));
        OkAndGsonUtil.doGet(HttpUtil.O2O_USER_INFO + "&user_id=" + CacheUtils.getUserId(getActivity(), CacheUtils.USER_ID, 0)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                Log.e("++++++++", response.body().string());
                initData(response.body().string());

            }
        });
    }

    private void initData(String jsonData) {
        Log.e("+++++++++++", jsonData);
        type = new TypeToken<SameCod<UserInfos>>() {
        }.getType();
        userMessageSameCod = OkAndGsonUtil.gson.fromJson(jsonData, type);
        userInfo = (UserInfos) userMessageSameCod.info;
        message.what = 0x01;
        handler.sendMessage(message);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x01) {
                ImageLoader.getInstance().displayImage(userInfo.getUserPicture(), mPicture);
                if (userInfo.getPayCount() == 0)
                    waitPay.setVisibility(View.INVISIBLE);  //待付款
                else {
                    waitPay.setVisibility(View.VISIBLE);
                    waitPay.setText("" + userInfo.getPayCount());
                }
                if (userInfo.getTeamNum() == 0)
                    waitGroupon.setVisibility(View.INVISIBLE);  //待拼团
                else {
                    waitPay.setVisibility(View.VISIBLE);
                    waitGroupon.setText(userInfo.getTeamNum());
                }


                remaining.setText("" + userInfo.getUserMoney());
                rePacket.setText("" + userInfo.getBonus());
                integral.setText("" + userInfo.getPayPoints());
                discountCoupon.setText("" + userInfo.getCouponses());

            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_info_re_private_message:
                //个人信息设置
                intent.setClass(getActivity(), PrivateMessageActivity.class);
                startActivity(intent);
                break;
            case R.id.my_info_re_from:
                //订单管理
                intent.putExtra("From", 0);
                intent.setClass(getActivity(), FormActivity.class);
                startActivity(intent);
                break;
            case R.id.my_info_ll_wait_pay:
                //代付款
                intent.putExtra("From", 1);
                intent.setClass(getActivity(), FormActivity.class);
                startActivity(intent);
                break;
            case R.id.my_info_ll_wait_groupon:
                //待拼团
                intent.setClass(getActivity(), GroupBuyActivity.class);
                startActivity(intent);
                break;
            case R.id.my_info_ll_wait_take:
                //待收货
                intent.putExtra("From", 2);
                intent.setClass(getActivity(), FormActivity.class);
                startActivity(intent);
                break;
            case R.id.my_info_ll_wait_appraise:
                //待评价
                intent.putExtra("From", 3);
                intent.setClass(getActivity(), FormActivity.class);
                startActivity(intent);
                break;
            case R.id.my_info_ll_exchange_good:
                intent.setClass(getActivity(), ExchangeGoodActivity.class);
                startActivity(intent);
                //退换货
                break;
            case R.id.my_info_re_my_wallet:
                //我的钱包
                intent.setClass(getActivity(), WalletActivity.class);
                startActivity(intent);
                break;
            case R.id.my_info_ll_remaining_sum:
                //余额
                intent.setClass(getActivity(), WalletActivity.class);
                startActivity(intent);
                break;
            case R.id.my_info_ll_red_packet:
                intent.setClass(getActivity(), RedPacketActivity.class);
                startActivity(intent);
                //红包
                break;
            case R.id.my_info_ll_integral:
                //积分
                intent.setClass(getActivity(), NoPageActivity.class);
                startActivity(intent);
                break;
            case R.id.my_info_ll_discount_coupon:
                intent.setClass(getActivity(), DiscountCouponActivity.class);
                startActivity(intent);
                //折扣券
                break;
            case R.id.my_info_ll_shop_collect:
                intent.putExtra("CollectShop", 0);
                intent.setClass(getActivity(), CollectShopActivity.class);
                startActivity(intent);
                //商品收藏
                break;
            case R.id.my_info_ll_attention_shop:
                intent.putExtra("CollectShop", 1);
                intent.setClass(getActivity(), CollectShopActivity.class);
                startActivity(intent);
                //关注商店
                break;
            case R.id.my_info_ll_my_share:
                intent.putExtra("MyShare", 0);
                intent.setClass(getActivity(), MyShareActivity.class);
                startActivity(intent);
                //我的分享
                break;
            case R.id.my_info_ll_help_center:
                intent.putExtra("MyShare", 1);
                intent.setClass(getActivity(), MyShareActivity.class);
                startActivity(intent);
                //帮助中心
                break;
            case R.id.my_info_ll_recruitment:
                //商家入驻
                intent.setClass(getActivity(), NoPageActivity.class);
                startActivity(intent);
                break;
            case R.id.my_info_ll_browsing_history:
                //浏览记录
                intent.setClass(getActivity(), NoPageActivity.class);
                startActivity(intent);
                break;
            case R.id.my_info_ll_photo:
                //我的相册
                Photo();
                break;
        }
    }


    private class ListActivityDongAccountProxy extends DongAccountCallbackImp {

        @Override
        public int onAuthenticate(InfoUser tInfo) {
            LogUtils.i("ListActivity.clazz--->>>onAuthenticate........tInfo:"
                    + tInfo);
            return 0;
        }

        @Override
        public int onUserError(int nErrNo) {
            LogUtils.i("ListActivity.clazz--->>>onUserError........nErrNo:"
                    + nErrNo);
            return 0;
        }

        @Override
        public int onNewListInfo() {
            return 0;
        }

        /**
         * 平台在线推送时回调该方法
         */
        @Override
        public int onCall(ArrayList<DeviceInfo> list) {
            /*LogUtils.i("ListActivity.clazz--->>>onCall........list.size():" + list.size());
            Toast.makeText(DeviceListActivity.this, "平台推送到达!!!", Toast.LENGTH_SHORT).show();
            int size = list.size();
            if (size > 0) {
                DeviceInfo deviceInfo = list.get(0);
                String message = deviceInfo.deviceName + deviceInfo.dwDeviceID
                        + deviceInfo.msg;
                DongPushMsgManager.pushMessageChange(DeviceListActivity.this, message);
            }*/
            return 0;
        }
    }

}
