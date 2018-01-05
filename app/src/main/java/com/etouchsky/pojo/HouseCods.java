package com.etouchsky.pojo;

import java.util.List;

/**
 * Created by Administrator on 2017/9/30 0030.
 */

public class HouseCods<T> {


    private int status;
    private String desc;
    private List<T> communities;
    public void setStatus(int status) {
        this.status = status;
    }
    public int getStatus() {
        return status;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getDesc() {
        return desc;
    }

    public void setCommunities(List<T> communities) {
        this.communities = communities;
    }
    public List<T> getCommunities() {
        return communities;
    }

}
