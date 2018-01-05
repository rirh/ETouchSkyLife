package com.etouchsky.pojo;

/**
 * Created by Administrator on 2017/9/19 0019.
 */

public class AFragmentIndexPostInfo {
    String account; //电话号码
    int id;
    public AFragmentIndexPostInfo(String account) {
        this.account = account;
    }
    public AFragmentIndexPostInfo(String account, int id) {
        this.account = account;
        this.id = id;
    }
}
