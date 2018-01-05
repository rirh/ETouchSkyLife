
package com.etouchsky.adapter;

import java.util.ArrayList;
import java.util.List;

import com.etouchsky.bean.Business_commodity;
import com.etouchsky.bean.GoodsListInfo;
import com.etouchsky.util.FileUtils;
import com.etouchsky.wisdom.Commodity_Details;
import com.etouchsky.wisdom.R;
import com.etouchsky.wisdom.TabBFm;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class BusinessListAdapter extends BaseAdapter {
    private Context context;                        //运行上下文
    private List<Business_commodity> listItems;    //商品信息集合
    private LayoutInflater listContainer;           //视图容器
    private boolean[] hasChecked;                   //记录商品选中状态
    public final class ListItemView{                //自定义控件集合
        public ImageView goods_img;
        public ImageView shopping_cart;
        public TextView goods_name;
        public TextView shop_price;
    }
    private BitmapUtils utils;
    private BitmapDisplayConfig displayConfig;

    public BusinessListAdapter(Context context, List<Business_commodity> listItems) {
        this.context = context;
        listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文
        this.listItems = listItems;
        hasChecked = new boolean[getCount()];
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        FileUtils fileUtils=new FileUtils(context, "skylife");
        utils=new BitmapUtils(context,fileUtils.getCacheDir(),cacheSize);
        displayConfig=new BitmapDisplayConfig();
        //displayConfig.setShowOriginal(true); // 显示原始图片,不压缩, 尽量不要使用, 图片太大时容易OOM。
        //utils.configDefaultBitmapMaxSize(BitmapCommonUtils.getScreenSize(mActivity));
        displayConfig.setBitmapMaxSize(BitmapCommonUtils.getScreenSize(context));
        AlphaAnimation animation=new AlphaAnimation(0.1f,1.0f);
        animation.setDuration(500);
        displayConfig.setAnimation(animation);
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return listItems.size();
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        try{
            ListItemView  listItemView = null;
            // TODO Auto-generated method stub
            System.out.println("getview"+position);
            //自定义视图
            if (convertView == null) {
                listItemView = new ListItemView();
                convertView = listContainer.inflate(R.layout.commodity_item, null);
                listItemView.goods_img =(ImageView)convertView.findViewById(R.id.goods_img);
                listItemView.shopping_cart =(ImageView)convertView.findViewById(R.id.shopping_cart);
                listItemView.goods_name=(TextView)convertView.findViewById(R.id.goods_name);
                listItemView.shop_price=(TextView)convertView.findViewById(R.id.shop_price);
                //设置控件集到convertView
                convertView.setTag(listItemView);
            }else {
                listItemView = (ListItemView)convertView.getTag();
            }
            listItemView.goods_name.setText((CharSequence)listItems.get(position).getTitle());
            listItemView.shop_price.setText(listItems.get(position).getTeam_price()+"");
            System.out.println("==="+listItems.get(position).getImage());
            listItemView.goods_img.setTag(listItems.get(position).getImage());
            utils.display(listItemView.goods_img,listItems.get(position).getImage(),displayConfig);
            //listItemView.sv.setImageBitmap(BitmapUtilities.getBitmapThumbnail((String)listItems.get(position).getGoods_img(),getWidth(context)));
            listItemView.goods_name.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    startActivity(position);
                }
            });
            listItemView.goods_img.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    startActivity(position);
                }
            });
            listItemView.shopping_cart.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Toast.makeText(context, "加入成功！", 5000).show();
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
        return convertView;
    }
    public void startActivity(int position){
        GoodsListInfo info=new GoodsListInfo();
        info.setId(listItems.get(position).getGoods_id()+"");
        info.setIcon(listItems.get(position).getImage());
        info.setName(listItems.get(position).getTitle());
        info.setPrice(listItems.get(position).getTeam_price()+"");
        info.setStock("20");
        info.setSortName(listItems.get(position).getGroup_id()+"");
        List<String> list=new ArrayList<String>();
        list.add(listItems.get(position).getImage());
        info.setPicture(list);
        Intent goodsortIntent = new Intent(context,
                Commodity_Details.class);
        goodsortIntent.putExtra("goodsListInfo",info);
        goodsortIntent.putExtra("typeName", "business");
        context.startActivity(goodsortIntent);
    }
}  