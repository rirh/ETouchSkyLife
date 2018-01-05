package com.etouchsky.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/9/6 0006.
 */

public class CollectStoreInfoList {


    @SerializedName("collect_number")
    private String collectNumber;
    @SerializedName("rec_id")
    private String recId;
    @SerializedName("shop_id")
    private String shopId;
    @SerializedName("store_name")
    private String storeName;
    @SerializedName("shop_logo")
    private String shopLogo;
    @SerializedName("add_time")
    private String addTime;
    @SerializedName("kf_type")
    private String kfType;
    @SerializedName("kf_ww")
    private String kfWw;
    @SerializedName("kf_qq")
    private String kfQq;
    @SerializedName("ru_id")
    private String ruId;
    @SerializedName("brand_thumb")
    private String brandThumb;
    @SerializedName("rankgoodReview")
    private String rankgoodreview;
    @SerializedName("ServergoodReview")
    private String servergoodreview;
    @SerializedName("deliverygoodReview")
    private String deliverygoodreview;
    public void setCollectNumber(String collectNumber) {
        this.collectNumber = collectNumber;
    }
    public String getCollectNumber() {
        return collectNumber;
    }

    public void setRecId(String recId) {
        this.recId = recId;
    }
    public String getRecId() {
        return recId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
    public String getShopId() {
        return shopId;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
    public String getStoreName() {
        return storeName;
    }

    public void setShopLogo(String shopLogo) {
        this.shopLogo = shopLogo;
    }
    public String getShopLogo() {
        return shopLogo;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }
    public String getAddTime() {
        return addTime;
    }

    public void setKfType(String kfType) {
        this.kfType = kfType;
    }
    public String getKfType() {
        return kfType;
    }

    public void setKfWw(String kfWw) {
        this.kfWw = kfWw;
    }
    public String getKfWw() {
        return kfWw;
    }

    public void setKfQq(String kfQq) {
        this.kfQq = kfQq;
    }
    public String getKfQq() {
        return kfQq;
    }

    public void setRuId(String ruId) {
        this.ruId = ruId;
    }
    public String getRuId() {
        return ruId;
    }

    public void setBrandThumb(String brandThumb) {
        this.brandThumb = brandThumb;
    }
    public String getBrandThumb() {
        return brandThumb;
    }

    public void setRankgoodreview(String rankgoodreview) {
        this.rankgoodreview = rankgoodreview;
    }
    public String getRankgoodreview() {
        return rankgoodreview;
    }

    public void setServergoodreview(String servergoodreview) {
        this.servergoodreview = servergoodreview;
    }
    public String getServergoodreview() {
        return servergoodreview;
    }

    public void setDeliverygoodreview(String deliverygoodreview) {
        this.deliverygoodreview = deliverygoodreview;
    }
    public String getDeliverygoodreview() {
        return deliverygoodreview;
    }

}
