package com.etouchsky.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/9/14 0014.
 */

public class UserInfos {


    @SerializedName("user_id")
    private String userId;
    private String birthday;
    private String email;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("nick_name")
    private String nickName;
    private String sex;
    @SerializedName("user_money")
    private String userMoney;
    @SerializedName("frozen_money")
    private String frozenMoney;
    @SerializedName("pay_points")
    private String payPoints;
    @SerializedName("rank_points")
    private String rankPoints;
    @SerializedName("address_id")
    private String addressId;
    @SerializedName("user_rank")
    private String userRank;
    @SerializedName("parent_id")
    private String parentId;
    private String qq;
    @SerializedName("mobile_phone")
    private String mobilePhone;
    @SerializedName("user_picture")
    private String userPicture;
    private String bonus;
    private String couponses;
    @SerializedName("team_num")
    private int teamNum;
    @SerializedName("pay_count")
    private int payCount;
    @SerializedName("confirmed_count")
    private int confirmedCount;
    @SerializedName("not_comment")
    private int notComment;
    @SerializedName("return_count")
    private String returnCount;
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public String getBirthday() {
        return birthday;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserName() {
        return userName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getNickName() {
        return nickName;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getSex() {
        return sex;
    }

    public void setUserMoney(String userMoney) {
        this.userMoney = userMoney;
    }
    public String getUserMoney() {
        return userMoney;
    }

    public void setFrozenMoney(String frozenMoney) {
        this.frozenMoney = frozenMoney;
    }
    public String getFrozenMoney() {
        return frozenMoney;
    }

    public void setPayPoints(String payPoints) {
        this.payPoints = payPoints;
    }
    public String getPayPoints() {
        return payPoints;
    }

    public void setRankPoints(String rankPoints) {
        this.rankPoints = rankPoints;
    }
    public String getRankPoints() {
        return rankPoints;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }
    public String getAddressId() {
        return addressId;
    }

    public void setUserRank(String userRank) {
        this.userRank = userRank;
    }
    public String getUserRank() {
        return userRank;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    public String getParentId() {
        return parentId;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }
    public String getQq() {
        return qq;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }
    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }
    public String getUserPicture() {
        return userPicture;
    }

    public void setBonus(String bonus) {
        this.bonus = bonus;
    }
    public String getBonus() {
        return bonus;
    }

    public void setCouponses(String couponses) {
        this.couponses = couponses;
    }
    public String getCouponses() {
        return couponses;
    }

    public void setTeamNum(int teamNum) {
        this.teamNum = teamNum;
    }
    public int getTeamNum() {
        return teamNum;
    }

    public void setPayCount(int payCount) {
        this.payCount = payCount;
    }
    public int getPayCount() {
        return payCount;
    }

    public void setConfirmedCount(int confirmedCount) {
        this.confirmedCount = confirmedCount;
    }
    public int getConfirmedCount() {
        return confirmedCount;
    }

    public void setNotComment(int notComment) {
        this.notComment = notComment;
    }
    public int getNotComment() {
        return notComment;
    }

    public void setReturnCount(String returnCount) {
        this.returnCount = returnCount;
    }
    public String getReturnCount() {
        return returnCount;
    }

}
