package com.etouchsky.wisdom;

import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.etouchsky.adapter.CommodityListAdapter;
import com.etouchsky.bean.Category;
import com.etouchsky.bean.Commodity;
import com.etouchsky.util.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


public class TabBFm extends Fragment{
	ListView listV;
	boolean fristInit=true;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.tab_b, container, false);
	}
	public static List<String> typeName;
	public static List<HashMap<String, Object>> dataSourceList = new ArrayList<HashMap<String, Object>>();
	RadioGroup radioCamType;
	String checktitileTypeID;
	boolean flag=true;
	boolean isTypetitileIntent=false;
	private HttpUtils http = new HttpUtils();
	Gson gsons=new Gson();
	List<Category> users;
	List<Commodity> commodity;
	List<Commodity> commoditys;
	private ProgressDialog dialog;
	Long historyTime=0l;
	CommodityListAdapter listadapter;
	public static String commodity_category_name;
	private int visibleLastIndex = 0;   //最后的可视项索引
	private int visibleItemCounts;       // 当前窗口可见项总数
	private int currPage=5;
	private int page=1;
	public int currCat_id;
	boolean iSscroll=false;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		listV=(ListView)this.getView().findViewById(R.id.commodityList);
		radioCamType=(RadioGroup)this.getView().findViewById(R.id.radioCamType);
		commoditys=new ArrayList<Commodity>();
		http.configCurrentHttpCacheExpiry(1000 * 10);
		http.configTimeout(10 * 1000);
		radioCamType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				RadioButton tempButton = (RadioButton)TabBFm.this.getActivity().findViewById(checkedId);
				HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
				Long getcurrTime=System.currentTimeMillis();
				if(getcurrTime-historyTime>500){//第一次加载数据会调用两次，时间间隔为0.02s左右
					page=1;
					historyTime=getcurrTime;
					getCategoryList(checkedId);
					commoditys.clear();
					currCat_id=checkedId;
					commodity_category_name=tempButton.getText().toString();
				}
			}
		});
		listV.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				int itemsLastIndex = listadapter.getCount() - 1;    //数据集最后一项的索引
				int lastIndex = itemsLastIndex + 1;             //加上底部的loadMoreView项
				System.out.println(scrollState+"lastIndex="+lastIndex+"==="+visibleLastIndex+"=="+visibleLastIndex%currPage+"visibleItemCounts"+visibleItemCounts);
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
					//如果是自动加载,可以在这里放置异步加载数据的代码
					Log.i("LOADMORE", "loading...");
					page+=1;
					getCategoryList(currCat_id);
					listadapter.notifyDataSetChanged();
					iSscroll=true;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				visibleItemCounts = visibleItemCount;
				visibleLastIndex = firstVisibleItem + visibleItemCount;
			}
		});
		//初始化商品分类
		getCategory();
	}
	public void getCategory(){
		RequestParams params = new RequestParams();
		params.addBodyParameter("auth", "etc");
		params.addBodyParameter("verify", "cdf280b1acb418344a0343cc1a655ddc");
		params.addBodyParameter("pid", "");
		http.send(HttpMethod.GET, HttpUtil.commodity_category,  params,
				new RequestCallBack<String>() {
					@Override
					public void onStart() {
						showProgressDialog();
						System.out.println("http---onStart");
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
							Type listType = (Type) new TypeToken<List<Category>>(){}.getType();
							users = gsons.fromJson(jb.getString("data").toString(), listType);
							for (int i=0;i<users.size();i++) {
								RadioButton radio = new RadioButton(TabBFm.this.getActivity());
								radio.setText(users.get(i).getCat_name());
								radio.setButtonDrawable(android.R.color.transparent);
								radio.setPadding(15, 0, 0, 0);
								radio.setTextSize(18);
								radio.setId(Integer.parseInt(users.get(i).getCat_id()));
								ColorStateList colorStateList=getResources().getColorStateList(R.color.txt_type);
								radio.setTextColor(colorStateList);
								radioCamType.addView(radio);
								if(i==0){
									radioCamType.check(radio.getId());
								}
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						if(users.size()<=0)closeProgressDialog();
					}
					@Override
					public void onFailure(
							com.lidroid.xutils.exception.HttpException arg0,
							String msg) {
						closeProgressDialog();
						Toast.makeText(TabBFm.this.getActivity(), msg, 5000).show();
					}
				});
	}
	public void showProgressDialog(){
		dialog = ProgressDialog.show(TabBFm.this.getActivity(), "加载中...", "正在加载数据。。。。，请稍后！",false, true);
	}
	public void closeProgressDialog(){
		if (dialog!=null) {
			dialog.dismiss();
			dialog=null;
		}
	}
	public void getCategoryList(final int cat_id){
		final String url=HttpUtil.commodity_List+"&cat_id="+cat_id+"&page="+page;
		http.send(HttpMethod.GET, url,  null,
				new RequestCallBack<String>() {
					@Override
					public void onStart() {
						if (dialog==null) {
							showProgressDialog();
						}
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
							Type listType = (Type) new TypeToken<List<Commodity>>(){}.getType();
							commodity = gsons.fromJson(jb.getString("data").toString(), listType);

							System.out.println(cat_id+"page="+page+"result"+result);
							if(commodity.size()<=0&&iSscroll){
								Toast.makeText(getActivity(), "您已经拉到底啦！", 5000).show();
							}else{
								commoditys.addAll(commodity);
								if(!iSscroll){
									listadapter =new CommodityListAdapter(TabBFm.this.getActivity(), commoditys);
									listV.setAdapter(listadapter);
								}else{
									listadapter.notifyDataSetChanged();
								}
							}
							iSscroll=false;
						}catch(Exception e){
							e.printStackTrace();
						}
						closeProgressDialog();
					}

					@Override
					public void onFailure(
							com.lidroid.xutils.exception.HttpException arg0,
							String msg) {
						closeProgressDialog();
						System.out.println("http---onFailure"+msg);
					}
				});
	}
	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void setRetainInstance(boolean retain) {
		// TODO Auto-generated method stub
		super.setRetainInstance(true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}
}
