package com.etouchsky.o2o;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.etouchsky.wisdom.R;

/**
 * Created by Administrator on 2017/9/1 0001.
 */

public class WalletActivity extends Activity implements View.OnClickListener {

    private LinearLayout recharge;
    private Intent intent = new Intent();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.o2o_my_wallet);
        initView();
    }

    private void initView() {
        recharge = (LinearLayout) findViewById(R.id.o2o_wallet_recharge_ll);

        recharge.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.o2o_wallet_recharge_ll:
                intent.setClass(this,RechargeActivity.class);
                startActivity(intent);
                break;
        }
    }
}
