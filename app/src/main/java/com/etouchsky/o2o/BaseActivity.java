package com.etouchsky.o2o;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;

import com.etouchsky.wisdom.R;

/**
 * Created by Administrator on 2017/9/13 0013.
 */

public class BaseActivity extends Activity {

    private RelativeLayout mRelativeLayout;
    private Activity context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.o2o_title_layout);
        initView();
    }


    public BaseActivity(Activity context) {
        this.context = context;
    }

    private void initView() {
        mRelativeLayout = (RelativeLayout) findViewById(R.id.o2o_title_bar_re);
        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destroyActivity(context);
            }
        });
    }

    private void destroyActivity(Activity context){
        context.finish();
    }
}
