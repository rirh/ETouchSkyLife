package com.etouchsky.o2o;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.wisdom.R;

/**
 * Created by Administrator on 2017/9/1 0001.
 */

public class GroupBuyActivity extends Activity {

    private TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.o2o_my_group_buy);
        initView();
        OkAndGsonUtil.setTitleBar(this,"拼团管理");
    }

    private void initView() {
    }
}
