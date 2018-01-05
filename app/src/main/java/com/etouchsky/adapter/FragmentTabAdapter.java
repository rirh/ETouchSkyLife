package com.etouchsky.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.etouchsky.GViewerXApplication;
import com.etouchsky.activity.O2OLoginActivity;
import com.etouchsky.util.CacheUtils;
import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.wisdom.R;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: wangjie  email:tiantian.china.2@gmail.com
 * Date: 13-10-10
 * Time: 上午9:25
 */
public class FragmentTabAdapter implements RadioGroup.OnCheckedChangeListener {
    private List<Fragment> fragments; // 一个tab页面对应一个Fragment
    private RadioGroup rgs; // 用于切换tab
    private FragmentActivity fragmentActivity; // Fragment所属的Activity
    private int fragmentContentId; // Activity中所要被替换的区域的id
    private int currentTab; // 当前Tab页面索引
    private boolean isLogin;
    private FragmentTransaction ft;
    //    private OnRgsExtraCheckedChangedListener onRgsExtraCheckedChangedListener; // 用于让调用者在切换tab时候增加新的功能

    public FragmentTabAdapter(FragmentActivity fragmentActivity, List<Fragment> fragments, int fragmentContentId, RadioGroup rgs) {
        this.fragments = fragments;
        this.rgs = rgs;
        this.fragmentActivity = fragmentActivity;
        this.fragmentContentId = fragmentContentId;
        // 默认显示第三页
        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
        ft.add(fragmentContentId, fragments.get(2));
        ft.commit();
        rgs.check(R.id.tab_rb_c);
        rgs.setOnCheckedChangeListener(this);


    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        for (int i = 0; i < rgs.getChildCount(); i++) {
            if (rgs.getChildAt(i).getId() == checkedId) {
                Fragment fragment = fragments.get(i);
                ft = obtainFragmentTransaction(i);
                getCurrentFragment().onPause(); // 暂停当前tab
                // getCurrentFragment().onStop(); // 暂停当前tab

//                用于第二次启动
                if (fragment.isAdded()) {
                    //  fragment.onStart(); // 启动目标tab的onStart()
                    fragment.onResume(); // 启动目标tab的onResume()
                } else {
                    ft.add(fragmentContentId, fragment);
                }
                if (i == 0 && CacheUtils.getHouseCod(fragmentActivity.getBaseContext(), CacheUtils.HOUSE_COD, false) && GViewerXApplication.preferences_userInfo.getBoolean("flagLogin", false)) {
                    showTab(i);
                    ft.commit();
                } else if (i== 0){
                     AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(fragmentActivity);
                    normalDialog.setTitle("提示");
                    normalDialog.setMessage("您暂无开通小区功能");
                    normalDialog.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showTab(0); // 显示目标tab
                                    ft.commit();
//                                    rgs.check(R.id.tab_rb_c);
                                }
                            });
//                    normalDialog.setNegativeButton("关闭",
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    showTab(0); // 显示目标tab
//                                    ft.commit();

//                                    rgs.check(R.id.tab_rb_c);
//                                    rgs.check(R.id.tab_rb_c);
//                                }
//                            });
                    // 显示
                    normalDialog.show();
                }else {
                    showTab(i); // 显示目标tab
                    ft.commit();
                }

//                showTab(i); // 显示目标tab
//                ft.commit();
                // 如果设置了切换tab额外功能功能接口
//                if(null != onRgsExtraCheckedChangedListener){
//                    onRgsExtraCheckedChangedListener.OnRgsExtraCheckedChanged(radioGroup, checkedId, i);
//                 }
            }
        }
    }

    /**
     * 切换tab
     *
     * @param idx
     */
    public void showTab(int idx) {
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            FragmentTransaction ft = obtainFragmentTransaction(idx);
            if (idx == i) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
            ft.commit();
        }
        currentTab = idx; // 更新目标tab为当前tab
    }

    /**
     * 获取一个带动画的FragmentTransaction
     *
     * @param index
     * @return
     */
    private FragmentTransaction obtainFragmentTransaction(int index) {
        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
        // 设置切换动画
        if (index > currentTab) {
            //ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
        } else {
            //ft.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
        }
        return ft;
    }

    public int getCurrentTab() {
        return currentTab;
    }

    public Fragment getCurrentFragment() {
        return fragments.get(currentTab);
    }


    private void showNormalDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */

    }

}
