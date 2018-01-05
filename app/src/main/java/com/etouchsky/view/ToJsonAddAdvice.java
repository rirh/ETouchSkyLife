package com.etouchsky.view;

import java.io.File;
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

    Integer id;
    String advice_title;
    String remark;
    String account;
    String trouble_title;

    public ToJsonAddAdvice(Integer id, String remark, String account, String trouble_title) {
        this.id = id;
        this.remark = remark;
        this.account = account;
        this.trouble_title = trouble_title;
    }

    public ToJsonAddAdvice(String account, Integer id, String advice_title, String remark) {
        this.id = id;
        this.advice_title = advice_title;
        this.remark = remark;
        this.account = account;
    }

}
