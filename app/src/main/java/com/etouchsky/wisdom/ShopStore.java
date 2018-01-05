package com.etouchsky.wisdom;

import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.etouchsky.adapter.AsyncImageLoader;
import com.etouchsky.adapter.AsyncImageLoader.ImageCallback;
import com.etouchsky.adapter.BusinessListAdapter;
import com.etouchsky.bean.Business;
import com.etouchsky.bean.Business_commodity;
import com.etouchsky.bean.Commodity;
import com.etouchsky.util.Config;
import com.etouchsky.util.HttpUtil;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShopStore extends Activity implements OnClickListener {
	Business business;
	ListView listV;
	private int page = 1;
	BusinessListAdapter listadapter;
	private ProgressDialog dialog;
	private int currPage = 5;
	private int visibleLastIndex = 0; // 最后的可视项索引
	private int visibleItemCounts; // 当前窗口可见项总数
	List<Business_commodity> commodity;
	List<Business_commodity> commoditys;
	// AllBusiInfo allBusiInfo;
	boolean iSscroll = false;
	private AsyncImageLoader asyncImageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_store);
		Config.setNoTheme(this);
		asyncImageLoader = new AsyncImageLoader();
		Intent intent = getIntent();
		business = (Business) intent.getSerializableExtra("business");
		// allBusiInfo = (AllBusiInfo) intent
		// .getSerializableExtra("allBusiInfo");

		init();
	}

	ImageView c_icon;

	private void init() {
		c_icon = (ImageView) findViewById(R.id.shop_img);
		TextView title = (TextView) findViewById(R.id.shop_names);
		listV = (ListView) findViewById(R.id.business_list);
		commoditys = new ArrayList<Business_commodity>();
		listV.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				int itemsLastIndex = listadapter.getCount() - 1; // 数据集最后一项的索引
				int lastIndex = itemsLastIndex + 1; // 加上底部的loadMoreView项
				System.out.println(scrollState + "lastIndex=" + lastIndex + "===" + visibleLastIndex + "=="
						+ visibleLastIndex % currPage + "visibleItemCounts" + visibleItemCounts);
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
					// 如果是自动加载,可以在这里放置异步加载数据的代码
					Log.i("LOADMORE", "loading...");
					page += 1;
					getCategoryList(-1);
					listadapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				visibleItemCounts = visibleItemCount;
				visibleLastIndex = firstVisibleItem + visibleItemCount;
			}
		});
		title.setText(business.getTitle());
		ImageView login_back_btn = (ImageView) findViewById(R.id.login_back_btn);
		login_back_btn.setOnClickListener(loginClick);
		ImageView more_info_id = (ImageView) findViewById(R.id.more_info_id);
		more_info_id.setOnClickListener(loginClick);
		loadpicture();
		getCategoryList(-1);
	}

	OnClickListener loginClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.login_back_btn:
					ShopStore.this.finish();
					break;
				case R.id.more_info_id:
					Toast.makeText(ShopStore.this, "正在建设中。。。。", 2000).show();
					break;

			}
		}
	};

	public void showProgressDialog() {
		dialog = ProgressDialog.show(this, "加载中...", "正在加载数据。。。。，请稍后！", false, true);
	}

	public void closeProgressDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

	public void getCategoryList(final int cat_id) {
		// http://192.168.1.88:85/app/api.php/Goods/busiGoods/partner_id/697/cate_id/104/page/1/page_num/3
		String param = "";
		if (cat_id != -1) {
			param = "/cate_id/" + cat_id;
		}
		final String url = HttpUtil.business_commodity + "/partner_id/" + business.getPartner_id() + "/page/" + page
				+ param;
		System.out.println("============" + url);
		Config.http.send(HttpMethod.GET, url, null, new RequestCallBack<String>() {
			@Override
			public void onStart() {
				if (dialog == null) {
					showProgressDialog();
				}
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					String result = URLDecoder.decode(responseInfo.result, "UTF-8");
					JSONObject jb = new JSONObject(result);
					Type listType = (Type) new TypeToken<List<Business_commodity>>() {
					}.getType();
					commodity = Config.gsong.fromJson(jb.getString("data").toString(), listType);

					System.out.println(cat_id + "page=" + 5 + "result" + result);
					if (commodity.size() <= 0 && iSscroll) {
						Toast.makeText(ShopStore.this, "您已经拉到底啦！", 5000).show();
					} else {
						commoditys.addAll(commodity);
						if (!iSscroll) {
							listadapter = new BusinessListAdapter(ShopStore.this, commoditys);
							listV.setAdapter(listadapter);
						} else {
							listadapter.notifyDataSetChanged();
						}
					}
					iSscroll = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
				closeProgressDialog();
			}

			@Override
			public void onFailure(com.lidroid.xutils.exception.HttpException arg0, String msg) {
				closeProgressDialog();
				System.out.println("http---onFailure" + msg);
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.buy:
				Toast.makeText(getApplicationContext(), "购买成功，等待买家确认", 5000).show();

				break;
			// case R.id.addcar:
			// addcar();
			// break;
			// case R.id.back:
			//
			// break;
			// case R.id.entycar:
			// Intent intent=new Intent(Commodity_Details.this,GoodsManage.class);

			// startActivity(intent);
			// break;
			default:
				break;
		}
	}

	private void addcar() {
		/*
		 * String productID=goodsListInfo.getId(); // TODO Auto-generated method
		 * stub //判断购物车是否存在此数据，如果存在，则+1，不存在则插入新产品信息 int
		 * result=JrShopMainActivity.fileService.queryCar(productID);
		 *
		 * if(result==0){//表示存在此记录
		 *
		 * int count=JrShopMainActivity.fileService.queryCarAmount(productID);
		 * JrShopMainActivity.fileService.updatekey("amount",
		 * String.valueOf(count+Integer.parseInt(c_count_t.getText().toString())
		 * ), productID);
		 *
		 * JRAlert.showMessage(Commodity_Details.this,"添加成功",R.drawable.success,
		 * Toast.LENGTH_LONG);
		 *
		 * }else{
		 *
		 * if(JrShopMainActivity.fileService.insertCar(productID, "1")!=-1){
		 * JrShopMainActivity.fileService.insertCar(productID,
		 * c_count_t.getText().toString());
		 *
		 * JRAlert.showMessage(Commodity_Details.this,"添加成功",R.drawable.success,
		 * Toast.LENGTH_LONG);
		 *
		 * }else{
		 * JRAlert.showMessage(Commodity_Details.this,"添加失败",R.drawable.warning,
		 * Toast.LENGTH_LONG);
		 * 
		 * }
		 * 
		 * }
		 */

	}

	private void loadpicture() {
		String imageUrl = business.getImage();

		Drawable cachedImage = asyncImageLoader.loadDrawable(imageUrl, new ImageCallback() {

			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				// ImageView imageViewByTag = (ImageView) mgridView
				// .findViewWithTag(imageUrl);

				c_icon.setImageDrawable(imageDrawable);
			}

		});
		if (cachedImage == null) {

			c_icon.setImageResource(R.mipmap.photo_loading);

		} else {

			c_icon.setImageDrawable(cachedImage);

		}

	}
}
