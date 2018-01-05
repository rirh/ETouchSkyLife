package com.etouchsky.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/8/29 0029.
 * 用户信息POJO类
 */

public class UserInfo {

    @SerializedName("user_id")
    public int userId;
    @SerializedName("email")
    public String email;
    @SerializedName("user_name")
    public String userName = "暂无数据";
    @SerializedName("nick_name")
    public String nickName;
    @SerializedName("sex")
    public String sex;
    @SerializedName("user_money")
    public double userMoney;
    @SerializedName("frozen_money")
    public double frozenMoney;
    @SerializedName("pay_points")
    public double payPoints;
    @SerializedName("rank_points")
    public double rankPoints;
    @SerializedName("address_id")
    public double addressId;
    @SerializedName("user_rank")
    public String userRank;
    @SerializedName("parent_id")
    public int parentId;
    @SerializedName("qq")
    public String qq;
    @SerializedName(value = "mobilePhone" , alternate = {"mobile_phone","Mobile"})
    public String mobilePhone;
    @SerializedName("user_picture")
    public String userPicture;
    @SerializedName("birthday")
    public String birthday;


}
