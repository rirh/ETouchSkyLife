package com.etouchsky.bean;

import java.io.Serializable;
import java.util.List;

public class GoodsListInfo implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String price;
	private String name;
	private String icon;
	private String description;
	private String id;
	private String stock;//库存
	private List<String> picture;//载图
	private String updata;//上架日期
	private String busiId;//
	private String sortName;
	private String amount;//购买的数量

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public String getBusiId() {
		return busiId;
	}

	public void setBusiId(String busiId) {
		this.busiId = busiId;
	}

	public String getUpdata() {
		return updata;
	}

	public void setUpdata(String updata) {
		this.updata = updata;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}



	public List<String> getPicture() {
		return picture;
	}

	public void setPicture(List<String> picture) {
		this.picture = picture;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
