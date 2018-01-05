package com.etouchsky.pojo;


import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/19 0019.
 * 这里实现序列化是为了将这个对象进行intent传输
 */

public class AFragmentAdviceDataInfo  implements Serializable {
    private int status;
    private AFragmentAdviceInfo data;

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setData(AFragmentAdviceInfo data) {
        this.data = data;
    }

    public AFragmentAdviceInfo getData() {
        return data;
    }


}
