package com.etouchsky.bean;

public class Commodity {
	public int getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(int goods_id) {
		this.goods_id = goods_id;
	}

	//商品ID
	private int goods_id;
	//商品分类
	private int cat_id;
	//商品名称
	private String goods_name;
	//库存
	private int goods_number;
	//价格
	private float shop_price;
	//产品缩略图
	private String goods_thumb;
	//商品图片
	private String goods_img;
	public int getCat_id() {
		return cat_id;
	}
	public void setCat_id(int cat_id) {
		this.cat_id = cat_id;
	}
	public String getGoods_name() {
		return goods_name;
	}
	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}
	public int getGoods_number() {
		return goods_number;
	}
	public void setGoods_number(int goods_number) {
		this.goods_number = goods_number;
	}
	public float getShop_price() {
		return shop_price;
	}
	public void setShop_price(float shop_price) {
		this.shop_price = shop_price;
	}
	public String getGoods_thumb() {
		return goods_thumb;
	}
	public void setGoods_thumb(String goods_thumb) {
		this.goods_thumb = goods_thumb;
	}
	public String getGoods_img() {
		return goods_img;
	}
	public void setGoods_img(String goods_img) {
		this.goods_img = goods_img;
	}

}
