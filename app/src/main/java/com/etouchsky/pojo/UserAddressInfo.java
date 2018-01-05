package com.etouchsky.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/9/7 0007.
 */

public class UserAddressInfo {
    @SerializedName("address_id")
    private String addressId;
    private List<UserAddressInfoList> address;
    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }
    public String getAddressId() {
        return addressId;
    }

    public void setAddress(List<UserAddressInfoList> address) {
        this.address = address;
    }
    public List<UserAddressInfoList> getAddress() {
        return address;
    }
}
