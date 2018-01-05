package com.etouchsky.pojo;

/**
 * Created by Administrator on 2017/9/29 0029.
 */

public class UserUpdateType {
/*
    account 登录账号
    osType 系统类型
    devToken 设备号
    isOnline 是否在线*/

    String account;
    Integer osType;
    String devToken;
    Integer isOnline;

    public UserUpdateType(String account, Integer osType, String devToken, Integer isOnline) {
        this.account = account;
        this.osType = osType;
        this.devToken = devToken;
        this.isOnline = isOnline;
    }
}
