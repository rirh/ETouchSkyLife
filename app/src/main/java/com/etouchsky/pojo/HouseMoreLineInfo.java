package com.etouchsky.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/9/30 0030.
 */

public class HouseMoreLineInfo {



    @SerializedName("noticeID")
    private String noticeid;
    @SerializedName("noticeTitle")
    private String noticetitle;
    @SerializedName("noticeSummary")
    private String noticesummary;
    @SerializedName("noticeIcon")
    private String noticeicon;
    @SerializedName("noticeTime")
    private String noticetime;
    @SerializedName("noticeExpiredTime")
    private String noticeexpiredtime;
    @SerializedName("communityID")
    private String communityid;

    private String communityName;

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public void setNoticeid(String noticeid) {
        this.noticeid = noticeid;
    }
    public String getNoticeid() {
        return noticeid;
    }

    public void setNoticetitle(String noticetitle) {
        this.noticetitle = noticetitle;
    }
    public String getNoticetitle() {
        return noticetitle;
    }

    public void setNoticesummary(String noticesummary) {
        this.noticesummary = noticesummary;
    }
    public String getNoticesummary() {
        return noticesummary;
    }

    public void setNoticeicon(String noticeicon) {
        this.noticeicon = noticeicon;
    }
    public String getNoticeicon() {
        return noticeicon;
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

    public void setCommunityid(String communityid) {
        this.communityid = communityid;
    }
    public String getCommunityid() {
        return communityid;
    }


}
