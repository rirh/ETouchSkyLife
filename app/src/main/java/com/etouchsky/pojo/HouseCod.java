package com.etouchsky.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/9/27 0027.
 */

public class HouseCod<T> {

    private int resCode;
    private String desc;
    @SerializedName(value = "info", alternate = {"communities"})
    private List<T> info;

    public List<T> getInfo() {
        return info;
    }

    public void setInfo(List<T> info) {
        this.info = info;
    }

    public void setRescode(int rescode) {
        this.resCode = resCode;
    }
    public int getRescode() {
        return resCode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getDesc() {
        return desc;
    }




}
