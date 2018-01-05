package com.etouchsky.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/9/4 0004.
 */

public class UserFromFilter {

    private int page;
    @SerializedName("page_size")
    private int pageSize;
    @SerializedName("record_count")
    private int recordCount;
    @SerializedName("page_count")
    private int pageCount;
    public void setPage(int page) {
        this.page = page;
    }
    public int getPage() {
        return page;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    public int getPageSize() {
        return pageSize;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }
    public int getRecordCount() {
        return recordCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
    public int getPageCount() {
        return pageCount;
    }


}
