package com.etouchsky.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/9/5 0005.
 */

public class DiscountCouponInfo {
    private List<DiscountCouponInfoList> list;
    @SerializedName("page_count")
    private int pageCount;
    @SerializedName("record_count")
    private int recordCount;
    public void setList(List<DiscountCouponInfoList> list) {
        this.list = list;
    }
    public List<DiscountCouponInfoList> getList() {
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
