package com.etouchsky.pojo;

/**
 * Created by Administrator on 2017/9/30 0030.
 */

public class AFragmentAdviceNew {

    String account; //电话号码
    int  community_id; //所属小区
    String unitno;    //楼栋
    String roomno; //房号

    public AFragmentAdviceNew(String account, int community_id, String unitno, String roomno) {
        this.account = account;
        this.community_id = community_id;
        this.unitno = unitno;
        this.roomno = roomno;
    }
}
