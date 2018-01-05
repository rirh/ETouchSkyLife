package com.etouchsky.pojo;

/**
 * Created by Administrator on 2017/9/14 0014.
 */

public class RequestUserUnLock {


 /*   communityID	小区ID	Integer	Y
    fromTime	生效时间  	string	Y
    validTime	失效时间	string	Y
    unitno	楼栋号	String	N
    roomno	房号	String	Y
    code	动态密码 	String	Y*/

    private int communityID;
    private String fromTime;
    private String validTime;
    private String unitno;
    private String roomno;
    private String code;

    public RequestUserUnLock(int communityID, String fromTime, String validTime, String unitno, String roomno, String code) {
        this.communityID = communityID;
        this.fromTime = fromTime;
        this.validTime = validTime;
        this.unitno = unitno;
        this.roomno = roomno;
        this.code = code;
    }

}
