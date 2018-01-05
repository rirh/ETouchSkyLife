package com.etouchsky.bean;

/**
 * Created by Administrator on 2017/7/5.
 */

public class Notice {
    private String notice_name;
    private String notice_context;
    private String notice_time;
    private String notice_img;

    public String getNotice_img() {
        return notice_img;
    }

    public void setNotice_img(String notice_img) {
        this.notice_img = notice_img;
    }

    public String getNotice_name() {
        return notice_name;
    }

    public void setNotice_name(String notice_name) {
        this.notice_name = notice_name;
    }

    public String getNotice_context() {
        return notice_context;
    }

    public void setNotice_context(String notice_context) {
        this.notice_context = notice_context;
    }

    public String getNotice_time() {
        return notice_time;
    }

    public void setNotice_time(String notice_time) {
        this.notice_time = notice_time;
    }
}
