package com.etouchsky.o2o;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.wisdom.R;

/**
 * Created by Administrator on 2017/9/8 0008.
 */

public class AttestationActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.o2o_attestation_layout);
        OkAndGsonUtil.setTitleBar(this,"实名认证");

    }
}
