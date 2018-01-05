package com.etouchsky.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/9/7 0007.
 */

public class UserAddressInfoList {

    @SerializedName("address_id")
    private String addressId;
    @SerializedName("address_name")
    private String addressName;
    @SerializedName("user_id")
    private String userId;
    private String consignee;
    private String email;
    private String country;
    private String province;
    private String city;
    private String district;
    private String street;
    private String address;
    private String zipcode;
    private String tel;
    private String mobile;
    @SerializedName("sign_building")
    private String signBuilding;
    @SerializedName("best_time")
    private String bestTime;
    private String audit;
    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }
    public String getAddressId() {
        return addressId;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }
    public String getAddressName() {
        return addressName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }
    public String getConsignee() {
        return consignee;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    public String getCountry() {
        return country;
    }

    public void setProvince(String province) {
        this.province = province;
    }
    public String getProvince() {
        return province;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public String getCity() {
        return city;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
    public String getDistrict() {
        return district;
    }

    public void setStreet(String street) {
        this.street = street;
    }
    public String getStreet() {
        return street;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getAddress() {
        return address;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
    public String getZipcode() {
        return zipcode;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
    public String getTel() {
        return tel;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getMobile() {
        return mobile;
    }

    public void setSignBuilding(String signBuilding) {
        this.signBuilding = signBuilding;
    }
    public String getSignBuilding() {
        return signBuilding;
    }

    public void setBestTime(String bestTime) {
        this.bestTime = bestTime;
    }
    public String getBestTime() {
        return bestTime;
    }

    public void setAudit(String audit) {
        this.audit = audit;
    }
    public String getAudit() {
        return audit;
    }

}
