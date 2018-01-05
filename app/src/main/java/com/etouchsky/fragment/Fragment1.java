package com.etouchsky.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etouchsky.activity.O2OLoginActivity;
import com.etouchsky.wisdom.R;


/**
 * Created by zq on 2016/11/12.
 */

public class Fragment1 extends Fragment {
    private View view;
    private RelativeLayout mRelativeLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view=getView();
        initView();
    }
    private void initView() {
        mRelativeLayout= (RelativeLayout) view.findViewById(R.id.fragment_background);
        TextView fragment_skip = (TextView) view.findViewById(R.id.fragment_skip);
        fragment_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),O2OLoginActivity.class);
                startActivityForResult(intent,1);
                getActivity().finish();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1)
        {
            if (resultCode==1)
            {
                getActivity().setResult(1);
                getActivity().finish();
            }
        }
    }
}
