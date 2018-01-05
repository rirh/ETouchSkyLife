package com.etouchsky.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/9/1 0001.
 */

public class UserLogin {


    @SerializedName("user_id")
    private String userId;
    private String email;
    @SerializedName("user_name")
    private String userName;
    private String mobile;
}
