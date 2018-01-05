package com.etouchsky.pojo;

import java.util.List;

/**
 * Created by Administrator on 2017/9/20 0020.
 */

public class ToJsonAddAdvice {

  /*  id	编号	Integer
    advice_title	投诉标题	string
    remark	投诉内容	string
    File	图片	Array
*/


    Integer id	;
    String advice_title;
    String remark;
    List<java.io.File> File;
    String account;

    public ToJsonAddAdvice(String account, Integer id, String advice_title, String remark) {
        this.id = id;
        this.advice_title = advice_title;
        this.remark = remark;
//        this.File = files;
        this.account = account;
    }


//    , List<File> files
}
