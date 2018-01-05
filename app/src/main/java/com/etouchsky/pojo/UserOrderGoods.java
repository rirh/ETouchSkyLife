package com.etouchsky.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/9/4 0004.
 */

public class UserOrderGoods {

    @SerializedName("goods_id")
    private String goodsId;
    @SerializedName("goods_name")
    private String goodsName;
    @SerializedName("goods_number")
    private String goodsNumber;
    @SerializedName("extension_code")
    private String extensionCode;
    @SerializedName("goods_price")
    private String goodsPrice;
    @SerializedName("goods_thumb")
    private String goodsThumb;
    private String url;
    @SerializedName("trade_id")
    private String tradeId;
    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }
    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsNumber(String goodsNumber) {
        this.goodsNumber = goodsNumber;
    }
    public String getGoodsNumber() {
        return goodsNumber;
    }

    public void setExtensionCode(String extensionCode) {
        this.extensionCode = extensionCode;
    }
    public String getExtensionCode() {
        return extensionCode;
    }

    public void setGoodsPrice(String goodsPrice) {
        this.goodsPrice = goodsPrice;
    }
    public String getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsThumb(String goodsThumb) {
        this.goodsThumb = goodsThumb;
    }
    public String getGoodsThumb() {
        return goodsThumb;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }
    public String getTradeId() {
        return tradeId;
    }
    
}
