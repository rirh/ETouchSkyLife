package com.etouchsky.pojo;

/**
 * Created by Administrator on 2017/9/8 0008.
 */

public class SaveAddress {

  /*  address_id	地址ID	Number	N
    	联系人	string	Y
    	手机	string	Y
    	省份ID	number	Y
    	城市ID	number	Y
    	地区ID	number	Y
    	地址	string	Y*/

    public int address_id;
    public  String consignee;
    public String mobile;
    public String address;
    public int city;
    public int district;
    public int province;

//    修改方法
    public SaveAddress(int address_id, String consignee, String mobile, String address) {
        this.address_id = address_id;
        this.consignee = consignee;
        this.mobile = mobile;
        this.address = address;
    }

//    新增方法
    public SaveAddress(String consignee, String mobile, String address) {;
        this.consignee = consignee;
        this.mobile = mobile;
        this.address = address;
    }
}
