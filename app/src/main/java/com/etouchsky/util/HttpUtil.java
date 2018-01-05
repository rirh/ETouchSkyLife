package com.etouchsky.util;

public class HttpUtil {
    public static String public_url="http://112.74.164.111:84/api/goods.php?";
    //商品分类
    public static String commodity_category=public_url+"action=get_category&auth=etc&verify=cdf280b1acb418344a0343cc1a655ddc&pid=";
    public static String commodity_List=public_url+"action=get_goods_info&auth=etc&verify=dc2100f372fa3d10c882004667d80e22&page_num=5";
    //商品详情
    public static String commodity_details=public_url+"action=get_goods&auth=etc&verify=840c0f165ea8e5f9c0b037dd0899c91e&";

    //周边接口
    //人气商家
    public static String public_periphery="http://112.74.164.111:85/app/api.php/";
    public static String periphery_business=public_periphery+"Business/busiTop/lat/22.6493/lng/114.133532/page/1/page_num/5";
    //商家商品
    public static String business_commodity=public_periphery+"Goods/busiGoods/page_num/5";
    //商品详情
    public static String business_details=public_periphery+"Goods/goodsDetail/";

    //	物业平台
//	获取验证码
    public static String public_wuye="http://112.74.164.111:83/api.php/";
    public static String getVerification_Code=public_wuye+"User/loginmess";
    public static String getLogin=public_wuye+"User/logincheck";
    //手机挂断
    public static String handup="http://www.etouchme.com:83/api.php/Device/phonehangup";


    //请求物业消息详细内容
    public static String HOUSE_MORE_LINE = "http://www.etouchme.com:83/api.php/Notice/detail";

    //手机呼管理中心
    public static String callManager="http://www.etouchme.com:83/api.php/Device/phonecall";
    /**
     * O2O商城接口
     */
    //  公共地址
    public static String O2O_SAME_CODE = "http://www.etouchme.com:89/api.php?app_key=D9EFB34E-F0C9-4CD0-8415-287E220221B8&method=";
    //    用户登录接口
    public static String O2O_USER_LOGIN = O2O_SAME_CODE + "dsc.user.login.post";
    //   获取用户信息接口
    public static String O2O_USER_INFO = O2O_SAME_CODE + "dsc.user.info.get";
    //   获取用户订单接口
    public static String O2O_USER_ORDER_LIST = O2O_SAME_CODE + "dsc.order.list.get";
    //   获取订单详情接口
    public static String O2O_USER_ORDER_INFO = O2O_SAME_CODE + "dsc.order.info.get";
    //   获取用户优惠券
    public static String O2O_USER_COUPON = O2O_SAME_CODE + "dsc.user.coupon.get";
    //   我的收藏接口
    public static String O2O_USER_COLLECT = O2O_SAME_CODE + "dsc.user.collect.get";
    //   关注店铺接口
    public static String O2O_USER_STORE = O2O_SAME_CODE + "dsc.user.store.get";
    //   用户注册接口
    public static String O2O_USER_REGISTER = O2O_SAME_CODE + "dsc.user.register.post";
    //   修改用户信息接口
    public static String O2O_USER_UPDATE = O2O_SAME_CODE + "dsc.user.update.post";
    //   获取收货地址接口
    public static String O2O_USER_ADDRESS = O2O_SAME_CODE + "dsc.user.address.list.get";
    //    新增/修改收货地址
    public static String O2O_USER_INSERT_ADDRESS = O2O_SAME_CODE + "dsc.user.address.save.post";
    //    删除收货地址
    public static String O2O_USER_DELETE_ADDRESS = O2O_SAME_CODE + "dsc.user.address.del.post";

    /**
     * 物业平台接口
     */
    //公共地址
    public static String HOUSE_SAME_COD ="http://112.74.164.111:83/api.php/";
    // 动态密码
    public static String HOUSE_SHARE_PASSWORD = HOUSE_SAME_COD + "device/setdynamicpwd";
    //投诉列表
    public static String HOUSE_ADVICE_INDEX = HOUSE_SAME_COD + "advice/index";
    //投诉详情
    public static String HOUSE_ADVICE_DETAIL = HOUSE_SAME_COD + "advice/detail";
    //新增编辑投诉
    public static String HOUSE_ADVICE_SAVE = HOUSE_SAME_COD + "advice/save";
    //报修列表
    public static String HOUSE_TROUBLE_INDEX = HOUSE_SAME_COD + "trouble/index";
    //报修详情
    public static String HOUSE_TROUBLE_DETAIL = HOUSE_SAME_COD + "trouble/detail/";
    //新增编辑报修
    public static String HOUSE_TROUBLE_SAVE = HOUSE_SAME_COD + "trouble/save";
    //移动端类型
    public static String UPDATE_TYPE = HOUSE_SAME_COD + "Device/updatetype";
    //获取用户的房屋信息
    public static String GET_COMMUNITIES =  HOUSE_SAME_COD + "User/getcommunities";
    //检测账号是否开启云对讲
    public static String IS_CHECKUSER =  "http://www.etouchme.com:83/api.php/User/checkuser";

}
