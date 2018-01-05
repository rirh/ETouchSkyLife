package com.etouchsky.wisdom;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class TabDFm extends Fragment{
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
        return inflater.inflate(R.layout.tab_d, container, false);
    }
    public Context mContext;
    LinearLayout cleaning_layout,clean_layout,hourly_worker_layout,
            nanny_layout,sister_in_law_layout,elderly_care_layout,package_layout,maternity_matron_layout;
    TextView more_txet;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext=this.getActivity();
//        临时保姆
        cleaning_layout=(LinearLayout)this.getActivity().findViewById(R.id.cleaning_layout);
//        宅速洁
        clean_layout=(LinearLayout)this.getActivity().findViewById(R.id.clean_layout);
//        长期钟点工
        hourly_worker_layout=(LinearLayout)this.getActivity().findViewById(R.id.hourly_worker_layout);
//        住家保姆
        nanny_layout=(LinearLayout)this.getActivity().findViewById(R.id.nanny_layout);
//        育儿嫂
        sister_in_law_layout=(LinearLayout)this.getActivity().findViewById(R.id.sister_in_law_layout);
//        看护老人
        elderly_care_layout=(LinearLayout)this.getActivity().findViewById(R.id.elderly_care_layout);
//        套餐
        package_layout=(LinearLayout)this.getActivity().findViewById(R.id.package_layout);
//        月嫂
        maternity_matron_layout=(LinearLayout)this.getActivity().findViewById(R.id.maternity_matron_layout);
//        更多
        more_txet=(TextView)this.getActivity().findViewById(R.id.more_text);

        cleaning_layout.setOnClickListener(layout_OnClick);
        clean_layout.setOnClickListener(layout_OnClick);
        hourly_worker_layout.setOnClickListener(layout_OnClick);
        nanny_layout.setOnClickListener(layout_OnClick);
        sister_in_law_layout.setOnClickListener(layout_OnClick);
        elderly_care_layout.setOnClickListener(layout_OnClick);
        package_layout.setOnClickListener(layout_OnClick);
        maternity_matron_layout.setOnClickListener(layout_OnClick);
        more_txet.setOnClickListener(layout_OnClick);
    }
    View.OnClickListener layout_OnClick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.cleaning_layout:
                case R.id.clean_layout:
                case R.id.hourly_worker_layout:
                case R.id.nanny_layout:
                case R.id.sister_in_law_layout:
                case R.id.elderly_care_layout:
                case R.id.package_layout:
                case R.id.maternity_matron_layout:
                    showMsg("该小区暂未开通此功能");
                    break;
                case R.id.more_text:
                    showMsg("暂无更多信息");
                    break;
            }
        }
    };
    public void showMsg(String msg){
        Toast.makeText(mContext,msg,2000).show();
    }
    @Override
    public void onStart() {
        super.onStart();
        System.out.println("DDDDDDDDD____onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("DDDDDDDDD____onResume");
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




}
