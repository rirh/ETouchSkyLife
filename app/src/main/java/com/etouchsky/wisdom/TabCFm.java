package com.etouchsky.wisdom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.etouchsky.bean.Business;
import com.etouchsky.util.Config;
import com.etouchsky.util.FileUtils;
import com.etouchsky.util.HttpUtil;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.List;

public class TabCFm extends Fragment{
    List<Business> business;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        System.out.println("CCCCCCCCCC____onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("CCCCCCCCCC____onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("CCCCCCCCCC____onCreateView");
        return inflater.inflate(R.layout.tab_c, container, false);
    }
    public void getPeriphery_business(){
        final String url=HttpUtil.periphery_business;
        Config.http.send(HttpMethod.GET, url,  null,
                new RequestCallBack<String>() {
                    @Override
                    public void onStart() {
                    }
                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        try{
                            String result=URLDecoder.decode(responseInfo.result,"UTF-8");
                            JSONObject jb=new JSONObject(result);
                            System.out.println("restult"+result);
                            Type listType = (Type) new TypeToken<List<Business>>(){}.getType();
                            business = Config.gsong.fromJson(jb.getString("data").toString(), listType);
                            for(int i=0;i<business.size();i++){
                                System.out.println("==="+business.get(i).getImage());
                                String titile=business.get(i).getTitle();
                                if (titile.length()>6) titile=titile.substring(0,6)+"...";
                                txts[i].setText(titile);
                                imgs[i].setTag(business.get(i).getImage());
                                utils.display(imgs[i],business.get(i).getImage(),displayConfig);
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(
                            com.lidroid.xutils.exception.HttpException arg0,
                            String msg) {
                        System.out.println("http---onFailure"+msg);
                    }
                });
    }
    ImageView img1,img2,img3,img4,img5;
    private final int[] img_arr = { R.id.img1, R.id.img2, R.id.img3, R.id.img4, R.id.img5 };

    private final int[] txt_arr = { R.id.titele1, R.id.titele2, R.id.titele3, R.id.titele4, R.id.titele5 };
    ImageView [] imgs=new ImageView[img_arr.length];;
    TextView [] txts=new TextView[txt_arr.length];
    private BitmapUtils utils;
    private BitmapDisplayConfig displayConfig;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("CCCCCCCCCC____onActivityCreated");
        for(int i=0;i<imgs.length;i++){
            imgs[i]=(ImageView)this.getView().findViewById(img_arr[i]);
            txts[i]=(TextView)this.getView().findViewById(txt_arr[i]);
            imgs[i].setOnClickListener(new ImgListener());
        }
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        FileUtils fileUtils=new FileUtils(this.getActivity(), "skylife");
        utils=new BitmapUtils(this.getActivity(),fileUtils.getCacheDir(),cacheSize);
        displayConfig=new BitmapDisplayConfig();
        //displayConfig.setShowOriginal(true); // 显示原始图片,不压缩, 尽量不要使用, 图片太大时容易OOM。
        //utils.configDefaultBitmapMaxSize(BitmapCommonUtils.getScreenSize(mActivity));
        displayConfig.setBitmapMaxSize(BitmapCommonUtils.getScreenSize(this.getActivity()));
        AlphaAnimation animation=new AlphaAnimation(0.1f,1.0f);
        animation.setDuration(500);
        displayConfig.setAnimation(animation);
        getPeriphery_business();
    }
    public class ImgListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            ImageView img = (ImageView) v;
            for (int i = 0; i < imgs.length; i++) {
                if (imgs[i].equals(img)) {
                    Intent intent=new Intent(TabCFm.this.getActivity(),ShopStore.class);
                    intent.putExtra("business", business.get(i));
                    startActivity(intent);
                }
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        System.out.println("CCCCCCCCCC____onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("CCCCCCCCCC____onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("CCCCCCCCCC____onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("CCCCCCCCCC____onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("CCCCCCCCCC____onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("CCCCCCCCCC____onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("CCCCCCCCCC____onDetach");
    }




}
