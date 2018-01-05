
package com.etouchsky.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.etouchsky.bean.Notice;
import com.etouchsky.data.DataHelper;
import com.etouchsky.pojo.HouseMoreLineInfo;
import com.etouchsky.util.FileUtils;
import com.etouchsky.util.OkAndGsonUtil;
import com.etouchsky.wisdom.R;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

@SuppressLint("NewApi")
public class NoticeListAdapter extends BaseAdapter {
    private Context context;                        //运行上下文
    private List<Notice> listItems;    //商品信息集合
    private LayoutInflater listContainer;           //视图容器
    private boolean[] hasChecked;                   //记录商品选中状态

    public final class ListItemView {                //自定义控件集合
        public ImageView notice_img;
        public TextView notice_name;
        public TextView notice_context;
        public TextView notice_time;
        public TextView name;
    }

    private BitmapUtils utils;
    private BitmapDisplayConfig displayConfig;

    public NoticeListAdapter(Context context, List<Notice> listItems) {
        this.context = context;
        listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文
        this.listItems = listItems;
        hasChecked = new boolean[getCount()];
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        FileUtils fileUtils = new FileUtils(context, "skylife");
        utils = new BitmapUtils(context, fileUtils.getCacheDir(), cacheSize);
        displayConfig = new BitmapDisplayConfig();
        //displayConfig.setShowOriginal(true); // 显示原始图片,不压缩, 尽量不要使用, 图片太大时容易OOM。
        //utils.configDefaultBitmapMaxSize(BitmapCommonUtils.getScreenSize(mActivity));
        displayConfig.setBitmapMaxSize(BitmapCommonUtils.getScreenSize(context));
        AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
        animation.setDuration(500);
        displayConfig.setAnimation(animation);
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return 1;//listItems.size();
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean hasChecked(int checkedID) {
        return hasChecked[checkedID];
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            ListItemView listItemView = null;
            // TODO Auto-generated method stub
            System.out.println("getview" + position);
            //自定义视图
            if (convertView == null) {
                listItemView = new ListItemView();
                convertView = listContainer.inflate(R.layout.notice_item, null);
                listItemView.notice_img = (ImageView) convertView.findViewById(R.id.more_line_info_ico);
                listItemView.notice_name = (TextView) convertView.findViewById(R.id.more_line_info_title);
                listItemView.notice_context = (TextView) convertView.findViewById(R.id.more_line_info_message);
                listItemView.notice_time = (TextView) convertView.findViewById(R.id.more_line_info_time);
                listItemView.name = (TextView) convertView.findViewById(R.id.more_line_info_name);
                //设置控件集到convertView
                convertView.setTag(listItemView);
            } else {
                listItemView = (ListItemView) convertView.getTag();
            }
            //得到一个可写的数据库
            DataHelper dbHelper = new DataHelper(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //select top 1 * from CommunitiesMoreLine order by id desc
            Cursor cursor = db.rawQuery("select * from CommunitiesMoreLine order by Id desc", null);
            cursor.moveToNext();
            String s = cursor.getString(cursor.getColumnIndex("Content"));
            //关闭数据库
            db.close();
            HouseMoreLineInfo info = OkAndGsonUtil.gson.fromJson(s,HouseMoreLineInfo.class);
            listItemView.notice_name.setText(info.getNoticetitle());
            listItemView.notice_time.setText(OkAndGsonUtil.timeStamp2Date(info.getNoticetime(), null).substring(0,11));
            listItemView.notice_context.setText(info.getNoticesummary());
            listItemView.name.setText(info.getCommunityName());
            if (info.getNoticeicon().equals("http://www.etouchme.com:83/pulic/images/etouchsky.jpg")) {
                listItemView.notice_img.setVisibility(View.GONE);
            } else {
                ImageLoader.getInstance().displayImage(info.getNoticeicon(),listItemView.notice_img );
            }

            //listItemView.notice_name.setText((CharSequence)listItems.get(position).getGoods_name());
            // listItemView.notice_context.setText(listItems.get(position).getShop_price()+"");
            //listItemView.notice_img.setTag(listItems.get(position).getGoods_img());
            // utils.display(listItemView.notice_img,listItems.get(position).getGoods_img(),displayConfig);
            //listItemView.sv.setImageBitmap(BitmapUtilities.getBitmapThumbnail((String)listItems.get(position).getGoods_img(),getWidth(context)));
//            listItemView.notice_context.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    // TODO Auto-generated method stub
//                    //startActivity(position);
//                }
//            });
//            listItemView.notice_img.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    // TODO Auto-generated method stub
//                    // startActivity(position);
//                }
//            });
//            listItemView.notice_name.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    // TODO Auto-generated method stub
//                    // Toast.makeText(context, "加入成功！", 5000).show();
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

}  