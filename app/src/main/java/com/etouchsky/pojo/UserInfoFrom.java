package com.etouchsky.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/4 0004.
 */

public class UserInfoFrom {

    private List<UserFormList> list;
    private UserFromFilter filter;
    @SerializedName("page_count")
    private int pageCount;
    @SerializedName("record_count")
    private int recordCount;
    public void setList(List<UserFormList> list) {
        this.list = list;
    }
    public List<UserFormList> getList() {
        return list;
    }

    public void setFilter(UserFromFilter filter) {
        this.filter = filter;
    }
    public UserFromFilter getFilter() {
        return filter;
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
