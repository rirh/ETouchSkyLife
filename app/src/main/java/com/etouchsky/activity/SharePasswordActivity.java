package com.etouchsky.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.etouchsky.data.DataHelper;
import com.etouchsky.pojo.AFragmentAdviceNew;
import com.etouchsky.pojo.RequestUserUnLock;
import com.etouchsky.pojo.SimpleHouseInfo;
import com.etouchsky.util.CacheUtils;
import com.etouchsky.util.HttpUtil;
import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.view.PickerView;
import com.etouchsky.wisdom.R;
import com.nostra13.universalimageloader.utils.L;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/9/19 0019.
 */

public class SharePasswordActivity extends Activity {
    private Calendar c = Calendar.getInstance();
    private PickerView minute_pv;
    private PickerView second_pv;
    private RadioGroup mRadioGroup;
    private Button produceButton;
    private int addEndMinute = 10, addBeginHour = c.get(Calendar.HOUR_OF_DAY), addBeginMinute = c.get(Calendar.MINUTE) + 1;
    private Calendar calendar = Calendar.getInstance();
    private String beginTime, beginDate, endTime, userBeginTime, userEndTime, jsonObject, passwordCode;
    private TextView beginTv, endTv, beginDateTv;
    private long seleteTime = 0, postBeginTime, postEndTime;
    private Intent intent = new Intent();
    private Bundle bundle = new Bundle();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d HH:mm");
    private Date date = null;
    private RequestUserUnLock mRequestUserUnLock;
    private SimpleHouseInfo mSimpleHouseInfo;
    private Message message;
    private ProgressDialog dialog;
    private int nowTime = c.get(Calendar.MINUTE);
//    private int time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_fragment_share_password);
        initView();


    }

    private void initView() {
//        time = c.get(Calendar.HOUR_OF_DAY);
        initPickerView();
        OkAndGsonUtil.setHouseTitleBar(SharePasswordActivity.this, "动态密码");
        mRadioGroup = (RadioGroup) findViewById(R.id.house_choice_time_rg);
        produceButton = (Button) findViewById(R.id.house_produce_password);
        beginTv = (TextView) findViewById(R.id.my_share_password_begin_time_tv);
        endTv = (TextView) findViewById(R.id.my_share_password_end_time_tv);
        beginDateTv = (TextView) findViewById(R.id.my_share_password_begin_date_tv);
        mRadioGroup.check(R.id.house_choice_time_ten_rb);
        beginTime = "" + addBeginHour + ":" + addBeginMinute;
        if (nowTime == 59) {
            addBeginHour = c.get(Calendar.HOUR_OF_DAY) + 1;
            addBeginMinute = 01;
            beginTime = "" + addBeginHour + ":" + addBeginMinute;
        }
        beginDate = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        beginDateTv.setText("生效日期为:" + beginDate);
        beginTv.setText("生效时间为:" + beginTime);
        setDateAndTime();
        endTv.setText("失效时间为:" + userEndTime);
        //配置有效时长按钮
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.house_choice_time_ten_rb:
                        addEndMinute = 10;
                        setDateAndTime();
                        endTv.setText("失效时间为:" + userEndTime);
                        break;
                    case R.id.house_choice_time_thirty_rb:
                        addEndMinute = 30;
                        setDateAndTime();
                        endTv.setText("失效时间为:" + userEndTime);
                        break;
                }
            }
        });
        //生成动态密码
        produceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (beginDate != null) {
                    setDateAndTime();
                    if (postBeginTime < Calendar.getInstance().getTimeInMillis())
                        Toast.makeText(SharePasswordActivity.this, "生效时间不得早于当前时间", Toast.LENGTH_SHORT).show();
                    else {
                        dialog = OkAndGsonUtil.showProgressDialog(SharePasswordActivity.this, "请稍等...", "密码生成中");
                        message = new Message();
                        passwordCode = Integer.toString(1 + (int) (Math.random() * ((999999 - 1) + 1)));
                        produceButton.setEnabled(false);
                        Log.e("网络请求", postBeginTime + "分割线" + postEndTime);
                        //int 小区id communityID, String fromTime, String validTime, 楼栋号 String unitno, 房号 String roomno,动态密码 String code
                        DataHelper dbHelper = new DataHelper(SharePasswordActivity.this);
                        SQLiteDatabase db = dbHelper.getReadableDatabase();
                        Cursor cursor = db.query("CommunitiesInfo", new String[]{"CmtID", "UnitNo", "RoomNo", "UnitName"}, null, null, null, null, null);
                        cursor.moveToNext();
                        mRequestUserUnLock = new RequestUserUnLock(cursor.getInt(cursor.getColumnIndex("CmtID")), postBeginTime + "", postEndTime + "", cursor.getString(cursor.getColumnIndex("UnitNo")), cursor.getString(cursor.getColumnIndex("RoomNo")), passwordCode);
                        String s1 = cursor.getString(cursor.getColumnIndex("UnitNo"));
                        String s2 = cursor.getString(cursor.getColumnIndex("RoomNo"));
                        String s3 = cursor.getString(cursor.getColumnIndex("UnitName"));
                        Log.e("****", s1 + "*" + s2 + "*" + s3 + "*");
                        //关闭数据库
                        db.close();
                        jsonObject = OkAndGsonUtil.gson.toJson(mRequestUserUnLock);
                        Log.e("传入的数据", jsonObject);
                        OkAndGsonUtil.doHousePost(jsonObject, HttpUtil.HOUSE_SHARE_PASSWORD).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                message.what = 0x02;//失败
                                handler.sendMessage(message);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                initData(response.body().string());

                            }
                        });

                    }
                } else
                    Toast.makeText(SharePasswordActivity.this, "请选择日期", Toast.LENGTH_SHORT).show();

            }
        });

        //配置日期控件
        beginDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putLong("selete_time", seleteTime);
                intent.putExtras(bundle);
                intent.setClass(SharePasswordActivity.this, CaldroidActivity.class);
                startActivityForResult(intent, 5);

            }
        });
    }

    //初始化json数据
    private void initData(String jsonMessage) {
        Log.e("*****", jsonMessage);
        mSimpleHouseInfo = OkAndGsonUtil.gson.fromJson(jsonMessage, SimpleHouseInfo.class);
        if (mSimpleHouseInfo.getStatus() == 0) {
            message.what = 0x01; //成功生成密码
            handler.sendMessage(message);

        } else {
            message.what = 0x02;//失败
            handler.sendMessage(message);
        }
    }


    //设置时间日期
    private void setDateAndTime() {
        Calendar.getInstance().getTimeInMillis();
        try {
            date = dateFormat.parse(beginDate + " " + beginTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);  // 对 calendar 设置为 date 所定的日期
        postBeginTime = calendar.getTimeInMillis();// 传入的生效时间
        userBeginTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(calendar.getTime());
        calendar.add(Calendar.MINUTE, addEndMinute);
        postEndTime = calendar.getTimeInMillis();
        userEndTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(calendar.getTime());
    }

    //返回选择日期
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 5) {
            if (resultCode == 2) {
                seleteTime = data.getLongExtra("SELETE_DATA_TIME", 0);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date d1 = new Date(seleteTime);
                beginDate = format.format(d1);
                if (seleteTime > 0) {
                    beginDateTv.setText("生效日期为:" + beginDate);
//                    Calendar c = Calendar.getInstance();
//                    if( beginDate.substring(8).equals(""+c.get(Calendar.DAY_OF_MONTH)))
//                        time = c.get(Calendar.HOUR_OF_DAY);
//                    else time = 0;
                    setDateAndTime();
                    endTv.setText("失效时间为:" + userEndTime);
                } else {
                    return;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //配置自定义控件
    private void initPickerView() {
        minute_pv = (PickerView) findViewById(R.id.minute_pv);
        second_pv = (PickerView) findViewById(R.id.second_pv);
        List<String> data = new ArrayList<String>();
        List<String> seconds = new ArrayList<String>();
        for (int time = 0;time < 24; time++) {
            if (time < 10) {
                data.add("0" + time);
            } else data.add("" + time);
        }
        for (int i = 0; i < 60; i++) {
            seconds.add(i < 10 ? "0" + i : "" + i);
        }
        minute_pv.setData(data);
        minute_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                addBeginHour = Integer.valueOf(text).intValue();
                beginTime = addBeginHour + ":" + addBeginMinute;
                beginTv.setText("生效时间为:" + beginTime);
                setDateAndTime();
                endTv.setText("失效时间为:" + userEndTime);
            }
        });
        second_pv.setData(seconds);
        second_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
//                addBeginMinute = Integer.valueOf(text).intValue();
                beginTime = addBeginHour + ":" + text;
                beginTv.setText("生效时间为:" + beginTime);
                setDateAndTime();
                endTv.setText("失效时间为:" + userEndTime);
            }
        });

        if (nowTime == 59) {
            minute_pv.setSelected(c.get(Calendar.HOUR_OF_DAY) + 1);
            second_pv.setSelected(01);
        } else {
            minute_pv.setSelected(c.get(Calendar.HOUR_OF_DAY));
            second_pv.setSelected(c.get(Calendar.MINUTE) + 1);
        }
    }


    //分享功能
    private void showCustomizeDialog(final String password) {
        AlertDialog.Builder customizeDialog = new AlertDialog.Builder(SharePasswordActivity.this);
        View dialogView = LayoutInflater.from(SharePasswordActivity.this).inflate(R.layout.share_password_dialog_layout, null);
        customizeDialog.setView(dialogView);
        LinearLayout qq = (LinearLayout) dialogView.findViewById(R.id.my_password_share_qq); //
        LinearLayout weChat = (LinearLayout) dialogView.findViewById(R.id.my_password_share_we_chat); //
        LinearLayout shortMessage = (LinearLayout) dialogView.findViewById(R.id.my_password_share_short_message); //
        shortMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri smsToUri = Uri.parse("smsto:");
                Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                intent.putExtra("sms_body", password);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else
                    Toast.makeText(SharePasswordActivity.this, "请先安装短信工具", Toast.LENGTH_SHORT).show();
            }
        });
        qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent qqIntent = new Intent(Intent.ACTION_SEND);
                qqIntent.setPackage("com.tencent.mobileqq");
                qqIntent.setType("text/plain");
                qqIntent.putExtra(Intent.EXTRA_TEXT, password);
                if (qqIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(qqIntent);
                } else
                    Toast.makeText(SharePasswordActivity.this, "请先安装QQ", Toast.LENGTH_SHORT).show();
            }
        });
        weChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent wechatIntent = new Intent(Intent.ACTION_SEND);
                wechatIntent.setPackage("com.tencent.mm");
                wechatIntent.setType("text/plain");
                wechatIntent.putExtra(Intent.EXTRA_TEXT, password);
                if (wechatIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(wechatIntent);
                } else
                    Toast.makeText(SharePasswordActivity.this, "请先安装微信", Toast.LENGTH_SHORT).show();
            }
        });
        customizeDialog.show();
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            produceButton.setEnabled(true);
            switch (msg.what) {
                case 0x01:
                    OkAndGsonUtil.closeProgressDialog(dialog);
                    showCustomizeDialog("动态密码:" + passwordCode + ",生效时间:" + userBeginTime + ",失效时间:" + userEndTime);
                    break;
                case 0x02:
                    OkAndGsonUtil.closeProgressDialog(dialog);
                    if (mSimpleHouseInfo != null && mSimpleHouseInfo.getDesc() != null)
                        Toast.makeText(SharePasswordActivity.this, mSimpleHouseInfo.getDesc(), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(SharePasswordActivity.this, "与服务器连接未响应", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


}
