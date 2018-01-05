package com.etouchsky.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/9/6 0006.
 */

public class SameCodInfo<A> {


    private List<A> list;
    @SerializedName("page_count")
    private int pageCount;
    @SerializedName("record_count")
    private int recordCount;
    private UserFromFilter filter;
    public void setList(List<A> list) {
        this.list = list;
    }
    public List<A> getList() {
        return list;
    }


    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
    public int getPageCount() {
        return pageCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }
    public int getRecordCount() {
        return recordCount;
    }

}
