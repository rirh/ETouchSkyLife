package com.etouchsky.wisdom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etouchsky.GViewerXApplication;
import com.etouchsky.activity.AFragmentAdviceActivity;
import com.etouchsky.activity.AFragmentAdviceInfoActivity;
import com.etouchsky.activity.AFragmentMoreLineActivity;
import com.etouchsky.activity.AFragmentMoreLineInfoActivity;
import com.etouchsky.activity.DeviceListActivity;
import com.etouchsky.activity.SharePasswordActivity;
import com.etouchsky.adapter.NoticeListAdapter;
import com.etouchsky.bean.Notice;
import com.etouchsky.data.DataHelper;
import com.etouchsky.pojo.HouseMoreLineInfo;
import com.etouchsky.util.CacheUtils;
import com.etouchsky.util.DataUtil;
import com.etouchsky.util.OkAndGsonUtil;
import com.gViewerX.util.LogUtils;

import java.util.ArrayList;
import java.util.List;


public class TabAFm extends Fragment {
    private boolean fristInit = true, isRegister = true;
    private NoticeListAdapter listAdapter;
    private Intent intent = new Intent();
    private Context mContext;
    private View mView;
    private LinearLayout propert_jiafei_layout, expense_layout, compaint_layout, door_video_layout, liuyan_layout, key_lock_layout;
    private RelativeLayout more_linear;
    private ListView neticeList;
    private Cursor cursor;
    private SQLiteDatabase db;
    private TextView neticeTv;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        System.out.println("AAAAAAAAAA____onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("AAAAAAAAAA____onCreate");
        mContext = this.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("AAAAAAAAAA____onCreateView");
        if (CacheUtils.getHouseCod(mContext, CacheUtils.HOUSE_COD, false) && GViewerXApplication.preferences_userInfo.getBoolean("flagLogin", false)) {
            mView = inflater.inflate(R.layout.tab_a, container, false);
        } else {
            isRegister = false;
            mView = inflater.inflate(R.layout.tab_a, container, false);
        }
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
            initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("AAAAAAAAAA____onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
//        listAdapter.notifyDataSetChanged();
        System.out.println("AAAAAAAAAA____onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        //System.gc();
        fristInit = false;
        System.out.println("AAAAAAAAAA____onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("AAAAAAAAAA____onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("AAAAAAAAAA____onDestroyView");
    }

    @Override
    public void setRetainInstance(boolean retain) {
        // TODO Auto-generated method stub
        super.setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("AAAAAAAAAA____onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("AAAAAAAAAA____onDetach");
    }


    public void showMsg(String msg) {
        LogUtils.d("guosong", msg);
        Toast.makeText(TabAFm.this.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }


    //初始化界面与数据
    private void initView() {
        key_lock_layout = (LinearLayout) this.getActivity().findViewById(R.id.key_lock_layout);
        liuyan_layout = (LinearLayout) this.getActivity().findViewById(R.id.liuyan_layout);
        door_video_layout = (LinearLayout) this.getActivity().findViewById(R.id.door_video_layout);
        compaint_layout = (LinearLayout) this.getActivity().findViewById(R.id.compaint_layout);
        expense_layout = (LinearLayout) this.getActivity().findViewById(R.id.expense_layout);
        propert_jiafei_layout = (LinearLayout) this.getActivity().findViewById(R.id.propert_jiafei_layout);
        more_linear = (RelativeLayout) this.getActivity().findViewById(R.id.more_linear);
        neticeList = (ListView) this.getActivity().findViewById(R.id.neticeList);
        neticeTv = (TextView) this.getActivity().findViewById(R.id.neticeList_tv);
        propert_jiafei_layout.setOnClickListener(layout_OnClick);
        expense_layout.setOnClickListener(layout_OnClick);
        door_video_layout.setOnClickListener(layout_OnClick);
        compaint_layout.setOnClickListener(layout_OnClick);
        liuyan_layout.setOnClickListener(layout_OnClick);
        key_lock_layout.setOnClickListener(layout_OnClick);
        more_linear.setOnClickListener(layout_OnClick);
        List<Notice> NoticeList = new ArrayList<Notice>();
        Notice notice = new Notice();
        notice.setNotice_name("test");
        NoticeList.add(notice);
        db = DataUtil.getSQLiteDb(mContext);
        cursor = db.rawQuery("select * from CommunitiesMoreLine order by Id desc", null);
        if (cursor.getCount() == 0 || !isRegister) {
            neticeList.setVisibility(View.GONE);
            neticeTv.setVisibility(View.VISIBLE);
            more_linear.setEnabled(false);
        }
        listAdapter = new NoticeListAdapter(TabAFm.this.getActivity(), NoticeList);
        neticeList.setAdapter(listAdapter);
        neticeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //得到一个可写的数据库
                db = DataUtil.getSQLiteDb(mContext);
                cursor = db.rawQuery("select * from CommunitiesMoreLine order by Id desc", null);
                cursor.moveToNext();
                String s = cursor.getString(cursor.getColumnIndex("Content"));
                //关闭数据库
                db.close();
                intent.putExtra("noticeID", Integer.valueOf(OkAndGsonUtil.gson.fromJson(s, HouseMoreLineInfo.class).getNoticeid()));
                intent.setClass(mContext, AFragmentMoreLineInfoActivity.class);
                startActivity(intent);
            }
        });
    }


    View.OnClickListener layout_OnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                    int result=DongSDKProxy.requestUnlock(320265);
//                    if (result==0){
//                        showMsg("门开了");
//                    }
//                    break;
                case R.id.liuyan_layout:
                    if (isRegister) {
                        startActivity(new Intent(mContext, CallManager.class));
                    } else
                        Toast.makeText(mContext, "您暂无开通小区功能", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.more_linear:
                    if (isRegister) {
                        startActivity(new Intent(mContext, AFragmentMoreLineActivity.class));
                    } else
                        Toast.makeText(mContext, "您暂无开通小区功能", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.door_video_layout:
                    if (isRegister) {
                        startActivity(new Intent(mContext, DeviceListActivity.class));
                    } else
                        Toast.makeText(mContext, "您暂无开通小区功能", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.key_lock_layout:
                    //蓝牙开门按钮
                    if (isRegister) {
                        if (Build.VERSION.SDK_INT < 18) {
                            Toast.makeText(TabAFm.this.getActivity(), "您的当前系统版本过低，暂不支持蓝牙功能！", Toast.LENGTH_LONG);
                        } else {
                            startActivity(new Intent(mContext, DeviceScanActivity.class));
                        }
                    } else
                        Toast.makeText(mContext, "您暂无开通小区功能", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.compaint_layout:
                    //用户投诉按钮
                    if (isRegister) {
                        intent.setClass(mContext, AFragmentAdviceActivity.class);
                        intent.putExtra("AFragment", 0x01); //0x01为投诉按钮
                        startActivity(intent);
                    } else
                        Toast.makeText(mContext, "您暂无开通小区功能", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.expense_layout:
                    //用户报修按钮
                    if (isRegister) {
                        intent.setClass(mContext, AFragmentAdviceActivity.class);
                        intent.putExtra("AFragment", 0x02); //0x02为报修按钮
                        startActivity(intent);
                    } else
                        Toast.makeText(mContext, "您暂无开通小区功能", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.propert_jiafei_layout:
                    //访客开门按钮
                    if (isRegister) {
                        startActivity(new Intent(mContext, SharePasswordActivity.class));
                    } else
                        Toast.makeText(mContext, "您暂无开通小区功能", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
