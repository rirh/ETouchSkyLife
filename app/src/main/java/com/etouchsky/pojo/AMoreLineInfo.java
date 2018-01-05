package com.etouchsky.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/9/30 0030.
 */

public class AMoreLineInfo {

    @SerializedName("noticeID")
    private String noticeid;
    @SerializedName("resCode")
    private int rescode;
    private String desc;
    @SerializedName("noticeTitle")
    private String noticetitle;
    @SerializedName("noticeTime")
    private String noticetime;
    @SerializedName("noticeExpiredTime")
    private String noticeexpiredtime;
    @SerializedName("noticeContent")
    private String noticecontent;
    @SerializedName("communityName")
    private String communityname;
    public void setNoticeid(String noticeid) {
        this.noticeid = noticeid;
    }
    public String getNoticeid() {
        return noticeid;
    }

    public void setRescode(int rescode) {
        this.rescode = rescode;
    }
    public int getRescode() {
        return rescode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getDesc() {
        return desc;
    }

    public void setNoticetitle(String noticetitle) {
        this.noticetitle = noticetitle;
    }
    public String getNoticetitle() {
        return noticetitle;
    }

    public void setNoticetime(String noticetime) {
        this.noticetime = noticetime;
    }
    public String getNoticetime() {
        return noticetime;
    }

    public void setNoticeexpiredtime(String noticeexpiredtime) {
        this.noticeexpiredtime = noticeexpiredtime;
    }
    public String getNoticeexpiredtime() {
        return noticeexpiredtime;
    }

    public void setNoticecontent(String noticecontent) {
        this.noticecontent = noticecontent;
    }
    public String getNoticecontent() {
        return noticecontent;
    }

    public void setCommunityname(String communityname) {
        this.communityname = communityname;
    }
    public String getCommunityname() {
        return communityname;
    }


}
