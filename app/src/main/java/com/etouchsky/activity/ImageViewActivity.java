package com.etouchsky.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.etouchsky.pojo.AFragmentAdviceDataInfo;
import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.wisdom.R;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2017/10/9 0009.
 */

public class ImageViewActivity extends Activity {

    private ViewPager view;
    private int postion;
    private AFragmentAdviceDataInfo info;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view_layout);
        postion = getIntent().getIntExtra("position",-1);
        info = (AFragmentAdviceDataInfo) getIntent().getSerializableExtra("DataInfo");
        initView();
    }

    private void initView() {
        view = (ViewPager) findViewById(R.id.image_view_iv);
        ViewAdapter adapter = new ViewAdapter();
        view.setAdapter(adapter);
        view.setCurrentItem(postion);//设置viewpager的下标
        OkAndGsonUtil.setTitleBar(this,"图片详情");
    }


    private class ViewAdapter extends PagerAdapter {
        private ImageView view ;
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
            view = new ImageView(ImageViewActivity.this);
            ImageLoader.getInstance().displayImage(info.getData().getImages().get(position),view);
//            Log.e("****",info.getData().getImages().get(position) + info.getData().getImages().size());
//            postion++;
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
//            container.removeView(mViewList.get(position));
        }

        @Override
        public int getCount() {
            return info.getData().getImages().size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }




    }
}
