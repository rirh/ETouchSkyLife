package com.etouchsky.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/8/29 0029.
 * 该类为POJO基本类
 */

public class SameCod<T> {
    public String result;
    public int error;
    public String msg;
    public T info;

}
