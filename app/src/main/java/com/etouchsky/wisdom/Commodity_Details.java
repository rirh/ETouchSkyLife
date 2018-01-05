package com.etouchsky.wisdom;

import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.etouchsky.adapter.AsyncImageLoader;
import com.etouchsky.adapter.AsyncImageLoader.ImageCallback;
import com.etouchsky.adapter.GallaryAdapter;
import com.etouchsky.bean.Business;
import com.etouchsky.bean.GoodsListInfo;
import com.etouchsky.util.Config;
import com.etouchsky.util.HttpUtil;
import com.gViewerX.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Commodity_Details extends Activity implements OnClickListener {
	GoodsListInfo goodsListInfo;
	//AllBusiInfo allBusiInfo;
	public String typeName;

	private AsyncImageLoader asyncImageLoader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goods_details);
		Config.setNoTheme(this);
		asyncImageLoader = new AsyncImageLoader();
		Intent intent = getIntent();
		goodsListInfo = (GoodsListInfo) intent
				.getSerializableExtra("goodsListInfo");
		typeName=intent.getStringExtra("typeName");
		//allBusiInfo = (AllBusiInfo) intent
		//.getSerializableExtra("allBusiInfo");

		init();
	}

	ImageView c_icon;
	TextView c_title;
	TextView c_sort;
	TextView c_price;
	TextView c_sore;
	TextView c_count;
	WebView goods_desc;
	TextView buy;
	Button back;
	Button addcar;
	TextView content;
	Gallery c_gallery;
	String	imageName;
	Bitmap bitmap;
	EditText c_count_t;
	TextView c_totol2;
	float price;
	GallaryAdapter gallaryAdapter;
	private void init() {
		c_gallery = (Gallery) findViewById(R.id.c_gallery);
		gallaryAdapter=new GallaryAdapter(
				Commodity_Details.this);
		c_totol2=(TextView)findViewById(R.id.c_totol2);// 小计总额
		c_title = (TextView) findViewById(R.id.c_title);
		c_sort = (TextView) findViewById(R.id.c_sort);
		c_count = (TextView) findViewById(R.id.c_count);
		c_price = (TextView) findViewById(R.id.c_price);
		c_sore = (TextView) findViewById(R.id.c_sore);
		goods_desc= (WebView) findViewById(R.id.goods_desc);
		c_title.setText(goodsListInfo.getName());
		c_sort.setText("类别:"+goodsListInfo.getSortName());
		price=Float.parseFloat(goodsListInfo.getPrice());
		c_price.setText("单价:"+price+"元");
		c_totol2.setText("合计：￥"+price+"");
		c_sore.setText("状态:上架");
		if(Integer.parseInt(goodsListInfo.getStock())==0){
			c_count.setText("库存:"+"无");
		}else{
			c_count.setText("库存:"+"有");
		}
		c_count_t=(EditText)findViewById(R.id.c_count_t);
		c_count_t.setText("1");
		c_count_t.setSelection(c_count_t.getText().length());
		c_count_t
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2) });// 限制填入数的位数
		c_count_t.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (c_count_t.getText().length() == 0) {
					c_count_t.setText("1");
					c_count_t.setSelection(c_count_t.getText().length());
//					JrShopActivity.fileService.updatekey("amount", "1",
//							goodsListInfo.getId());

				}
				//updateTotlePassService(GoodsManage.UPDATETOTLE,Integer.parseInt(manage_totol2.getText().toString()));
			}
		});
		c_count_t.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				// TODO Auto-generated method stub

				if(c_count_t.getText().length() != 0
						&& s.toString().length() != 0&&s.toString().equals("0")){
					c_count_t.setText("1");
					c_count_t.setSelection(c_count_t.getText().length());
				}


				if (c_count_t.getText().length() != 0
						&& s.toString().length() != 0) {

					// manage_count_t.setText(s.toString());

					float tool1 = price
							* Float.parseFloat(c_count_t.getText()
							.toString());
					System.out.println("String.valueOf(tool1):"+String.valueOf(tool1));
					c_totol2.setText("合计：￥"+String.valueOf(tool1));
				}


				if (c_count_t.getText().length() != 0) {
//						JrShopActivity.fileService.updatekey("amount",
//								c_count_t.getText().toString(),
//								goodsListInfo.getId());
				}
				//updateTotlePassService(GoodsManage.UPDATETOTLE,Integer.parseInt(manage_totol2.getText().toString()));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				//updateTotlePassService(GoodsManage.UPDATETOTLE,Float.parseFloat(manage_totol2.getText().toString()),goodsListInfos.getId(),"0");
			}
		});
		buy = (TextView) findViewById(R.id.buy);
		buy.setOnClickListener(this);
		/*
		// TODO Auto-generated method stub
		c_icon = (ImageView) findViewById(R.id.c_icon);
		c_update1 = (TextView) findViewById(R.id.c_update1);
		addcar = (Button) findViewById(R.id.addcar);
		addcar.setOnClickListener(this);
//		back = (Button) findViewById(R.id.back);
//		back.setOnClickListener(this);

		Button entycar = (Button) findViewById(R.id.entycar);
		entycar.setOnClickListener(this);
		content = (TextView) findViewById(R.id.content);


		//c_update1.setText("上架时间:"+goodsListInfo.getUpdata());
		//content.setText(goodsListInfo.getDescription());



		*/
		//loadpicture();
		ImageView login_back_btn = (ImageView) findViewById(R.id.login_back_btn);
		login_back_btn.setOnClickListener(this);
		//galleryinit();
		getDescribe();
	}
	public void getDescribe(){
		String url="";
		if("business".equals(typeName)){
			url=HttpUtil.business_details+"goods_id/"+goodsListInfo.getId();
		}else if("commodity".equals(typeName)){
			url=HttpUtil.commodity_details+"goods_id="+goodsListInfo.getId();
		}
		System.out.println(typeName+"getDescribeurl==="+url+"goodsListInfo.getid="+goodsListInfo.getBusiId());
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
							//String result=URLDecoder.decode(responseInfo.result,"UTF-8");
							JSONObject jb=new JSONObject(responseInfo.result);
							System.out.println("getDescribe=="+responseInfo.result);
							String data=jb.getString("data");
							String status=jb.getString("status");
							if("1".equals(status) && !"".equals(data)) {
								JSONObject jb1 = new JSONObject(data);
								List<String> list=new ArrayList<String>();
								if ("business".equals(typeName)) {
									//goods_desc.setText(Html.fromHtml(jb1.getString("detail")));
									goods_desc.loadDataWithBaseURL(null, jb1.getString("detail"), "text/html", "utf-8", null);
									String image=jb1.getString("image");
									String image1=jb1.getString("image1");
									String image2=jb1.getString("image2");
									if (!image.isEmpty())
									list.add(image);
									if (!image1.isEmpty())
									list.add(image1);
									if (!image2.isEmpty())
									list.add(image2);
								} else if ("commodity".equals(typeName)) {
									//goods_desc.setText(Html.fromHtml(jb1.getString("goods_desc")));
									goods_desc.loadDataWithBaseURL(null, jb1.getString("goods_desc"), "text/html", "utf-8", null);
									String gallerys=jb.getString("gallerys");
									LogUtils.d("guosong==","gallerys="+gallerys);
									JSONArray array=new JSONArray(gallerys);
									for(int i=0;i<array.length();i++){
										JSONObject oj = array.getJSONObject(i);
										list.add(oj.getString("img_original"));
										//LogUtils.d("guosong==","img_url"+oj.getString("img_url"));
									}
									LogUtils.d("guosong==","listsize="+list.size());
								}
								galleryinit(list);
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
			case R.id.login_back_btn:
				Commodity_Details.this.finish();
				break;
			case R.id.buy:
				Toast.makeText(getApplicationContext(), "购买成功，等待买家确认", 5000).show();

				break;
			//case R.id.addcar:
			// addcar();
			//break;
//		case R.id.back:
//
//			break;
			//case R.id.entycar:
			//Intent intent=new Intent(Commodity_Details.this,GoodsManage.class);

			//startActivity(intent);
			//break;
			default:
				break;
		}
	}
	private void addcar() {
	/*String productID=goodsListInfo.getId();
		// TODO Auto-generated method stub
		//判断购物车是否存在此数据，如果存在，则+1，不存在则插入新产品信息
	int result=JrShopMainActivity.fileService.queryCar(productID);

		if(result==0){//表示存在此记录

			int count=JrShopMainActivity.fileService.queryCarAmount(productID);
			JrShopMainActivity.fileService.updatekey("amount", String.valueOf(count+Integer.parseInt(c_count_t.getText().toString())), productID);

			JRAlert.showMessage(Commodity_Details.this,"添加成功",R.drawable.success,Toast.LENGTH_LONG);

		}else{

			if(JrShopMainActivity.fileService.insertCar(productID, "1")!=-1){
				JrShopMainActivity.fileService.insertCar(productID, c_count_t.getText().toString());

				JRAlert.showMessage(Commodity_Details.this,"添加成功",R.drawable.success,Toast.LENGTH_LONG);

			}else{
				JRAlert.showMessage(Commodity_Details.this,"添加失败",R.drawable.warning,Toast.LENGTH_LONG);
				
			}
		
		}*/


	}

	private void loadpicture(){
		String imageUrl = goodsListInfo.getIcon();





		Drawable cachedImage = asyncImageLoader.loadDrawable(imageUrl,
				new ImageCallback() {

					public void imageLoaded(Drawable imageDrawable,
											String imageUrl) {
//							ImageView imageViewByTag = (ImageView) mgridView
//							.findViewWithTag(imageUrl);


						c_icon.setImageDrawable(imageDrawable);
					}

				});
		if (cachedImage == null) {

			c_icon.setImageResource(R.mipmap.photo_loading);


		} else {

			c_icon.setImageDrawable(cachedImage);

		}

	}
	public static long mDownStartTime = 0;

	private void galleryinit(List<String> list) {
		// TODO Auto-generated method stub
		final List<String> gallaryurl =list;//goodsListInfo.getPicture();

		if (gallaryurl != null && gallaryurl.size() > 0) {

			gallaryAdapter.setmAppList(gallaryurl);
			c_gallery.setAdapter(gallaryAdapter);

			if(gallaryurl.size() > 3){
				c_gallery.setSelection(2);
			}

			c_gallery.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v,
										int position, long id) {
					if (System.currentTimeMillis() - mDownStartTime < 250) {

					} else {
						//ImageDialog dialog = new ImageDialog(
						//		Commodity_Details.this, gallaryurl.get(position));
						//dialog.show();
					}

				}
			});

		}
	}

}
