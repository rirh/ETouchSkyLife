package com.etouchsky.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/9/11 0011.
 */

public class UserFromGoodsInfo {


    @SerializedName("order_id")
    private String orderId;
    @SerializedName("order_sn")
    private String orderSn;
    @SerializedName("shipping_fee")
    private String shippingFee;
    private String mobile;
    @SerializedName("order_time")
    private String orderTime;
    @SerializedName("order_status")
    private String orderStatus;
    @SerializedName("order_del")
    private String orderDel;
    private String status;
    @SerializedName("status_number")
    private String statusNumber;
    private String consignee;
    @SerializedName("main_order_id")
    private String mainOrderId;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("order_goods")
    private List<UserOrderGoods> orderGoods;
    @SerializedName("order_goods_num")
    private int orderGoodsNum;
    @SerializedName("order_child")
    private int orderChild;
    @SerializedName("no_picture")
    private String noPicture;
    @SerializedName("delete_yes")
    private int deleteYes;
    @SerializedName("invoice_no")
    private String invoiceNo;
    @SerializedName("shipping_name")
    private String shippingName;
    private String email;
    @SerializedName("address_detail")
    private String addressDetail;
    private String address;
    private String tel;
    @SerializedName("delivery_time")
    private String deliveryTime;
    @SerializedName("order_count")
    private int orderCount;
    @SerializedName("kf_type")
    private String kfType;
    @SerializedName("kf_ww")
    private String kfWw;
    @SerializedName("kf_qq")
    private String kfQq;
    @SerializedName("total_fee")
    private String totalFee;
    @SerializedName("pay_status")
    private String payStatus;
    @SerializedName("team_id")
    private String teamId;
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getOrderId() {
        return orderId;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }
    public String getOrderSn() {
        return orderSn;
    }

    public void setShippingFee(String shippingFee) {
        this.shippingFee = shippingFee;
    }
    public String getShippingFee() {
        return shippingFee;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getMobile() {
        return mobile;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }
    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderDel(String orderDel) {
        this.orderDel = orderDel;
    }
    public String getOrderDel() {
        return orderDel;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

    public void setStatusNumber(String statusNumber) {
        this.statusNumber = statusNumber;
    }
    public String getStatusNumber() {
        return statusNumber;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }
    public String getConsignee() {
        return consignee;
    }

    public void setMainOrderId(String mainOrderId) {
        this.mainOrderId = mainOrderId;
    }
    public String getMainOrderId() {
        return mainOrderId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserName() {
        return userName;
    }

    public void setOrderGoods(List<UserOrderGoods> orderGoods) {
        this.orderGoods = orderGoods;
    }
    public List<UserOrderGoods> getOrderGoods() {
        return orderGoods;
    }

    public void setOrderGoodsNum(int orderGoodsNum) {
        this.orderGoodsNum = orderGoodsNum;
    }
    public int getOrderGoodsNum() {
        return orderGoodsNum;
    }

    public void setOrderChild(int orderChild) {
        this.orderChild = orderChild;
    }
    public int getOrderChild() {
        return orderChild;
    }

    public void setNoPicture(String noPicture) {
        this.noPicture = noPicture;
    }
    public String getNoPicture() {
        return noPicture;
    }

    public void setDeleteYes(int deleteYes) {
        this.deleteYes = deleteYes;
    }
    public int getDeleteYes() {
        return deleteYes;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }
    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }
    public String getShippingName() {
        return shippingName;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }
    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getAddress() {
        return address;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
    public String getTel() {
        return tel;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }
    public int getOrderCount() {
        return orderCount;
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

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }
    public String getTotalFee() {
        return totalFee;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }
    public String getPayStatus() {
        return payStatus;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }
    public String getTeamId() {
        return teamId;
    }

}
