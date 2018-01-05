package com.etouchsky.pojo;

import java.util.List;

/**
 * Created by Administrator on 2017/9/19 0019.
 */

public class AFragmentAdviceIndexInfo<DataType> {

    private int status;
    private List<DataType> data;

    public void setStatus(int status) {
        this.status = status;
    }
    public int getStatus() {
        return status;
    }

    public void setData(List<DataType> data) {
        this.data = data;
    }
    public List<DataType> getData() {
        return data;
    }


}
