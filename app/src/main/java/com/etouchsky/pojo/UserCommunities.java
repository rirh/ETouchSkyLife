package com.etouchsky.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/9/29 0029.
 */

public class UserCommunities {

    @SerializedName("cmtID")
    private String cmtid;
    @SerializedName("cmtName")
    private String cmtname;
    @SerializedName("cmtAddress")
    private String cmtaddress;
    private String lat;
    private String lng;
    @SerializedName("unitNo")
    private String unitno;
    @SerializedName("roomNo")
    private String roomno;
    @SerializedName("unitName")
    private String unitname;
    public void setCmtid(String cmtid) {
        this.cmtid = cmtid;
    }
    public String getCmtid() {
        return cmtid;
    }

    public void setCmtname(String cmtname) {
        this.cmtname = cmtname;
    }
    public String getCmtname() {
        return cmtname;
    }

    public void setCmtaddress(String cmtaddress) {
        this.cmtaddress = cmtaddress;
    }
    public String getCmtaddress() {
        return cmtaddress;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
    public String getLat() {
        return lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
    public String getLng() {
        return lng;
    }

    public void setUnitno(String unitno) {
        this.unitno = unitno;
    }
    public String getUnitno() {
        return unitno;
    }

    public void setRoomno(String roomno) {
        this.roomno = roomno;
    }
    public String getRoomno() {
        return roomno;
    }

    public void setUnitname(String unitname) {
        this.unitname = unitname;
    }
    public String getUnitname() {
        return unitname;
    }

}
