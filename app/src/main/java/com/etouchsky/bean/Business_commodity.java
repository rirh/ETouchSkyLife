package com.etouchsky.bean;

public class Business_commodity {
	//	商品编号
	private int goods_id;
	//	商品名称
	private String title;
	//	价格
	private String team_price;
	//	市场价格
	private String market_price;
	//商品封面
	private String image;
	//购买数据
	private String now_number;
	//所属分类id
	private int group_id;
	public int getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(int goods_id) {
		this.goods_id = goods_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getNow_number() {
		return now_number;
	}
	public String getTeam_price() {
		return team_price;
	}
	public void setTeam_price(String team_price) {
		this.team_price = team_price;
	}
	public String getMarket_price() {
		return market_price;
	}
	public void setMarket_price(String market_price) {
		this.market_price = market_price;
	}
	public void setNow_number(String now_number) {
		this.now_number = now_number;
	}
	public int getGroup_id() {
		return group_id;
	}
	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}

}
