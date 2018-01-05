package com.etouchsky.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/9/19 0019.
 */

public class AFragmentAdviceData {

    private String rid;
    @SerializedName(value = "adviceTitle", alternate = {"advice_title", "trouble_title"})
    private String adviceTitle;
    private String remark;
    @SerializedName("unit_id")
    private String unitId;
    @SerializedName("unit_name")
    private String unitName;
    @SerializedName("room_no")
    private String roomNo;
    @SerializedName("community_id")
    private String communityId;
    @SerializedName("community_name")
    private String communityName;
    private String state;
    @SerializedName("complete_date")
    private String completeDate;
    @SerializedName("complete_content")
    private String completeContent;
    @SerializedName("create_time")
    private String createTime;
    public void setRid(String rid) {
        this.rid = rid;
    }
    public String getRid() {
        return rid;
    }

    public void setAdviceTitle(String adviceTitle) {
        this.adviceTitle = adviceTitle;
    }
    public String getAdviceTitle() {
        return adviceTitle;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getRemark() {
        return remark;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }
    public String getUnitId() {
        return unitId;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    public String getUnitName() {
        return unitName;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }
    public String getRoomNo() {
        return roomNo;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }
    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }
    public String getCommunityName() {
        return communityName;
    }

    public void setState(String state) {
        this.state = state;
    }
    public String getState() {
        return state;
    }

    public void setCompleteDate(String completeDate) {
        this.completeDate = completeDate;
    }
    public String getCompleteDate() {
        return completeDate;
    }

    public void setCompleteContent(String completeContent) {
        this.completeContent = completeContent;
    }
    public String getCompleteContent() {
        return completeContent;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    public String getCreateTime() {
        return createTime;
    }

}
