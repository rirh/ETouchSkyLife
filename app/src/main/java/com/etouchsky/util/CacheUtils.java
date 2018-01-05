package com.etouchsky.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/7/19 0019.
 * 数据存储工具类
 */

public class CacheUtils {

    private static final String CACHE_FILE_NAME = "O2OLogin";
    private static SharedPreferences mSharedPreferences;

    public static final String IS_OPEN_LOGIN_PAGE = "IS_OPEN_LOGIN_PAGE";
    public static final String USER_ID = "USER_ID";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_ACCOUNT = "USER_ACCOUNT";
    public static final String HOUSE_COD = "HOUSE_COD";

    /**
     * @param context 上下文对象
     * @param key     要取数据的键
     * @param deValue 缺省值
     * @return
     */
    public static boolean getBoolean(Context context, String key, boolean deValue) {
        if (mSharedPreferences == null)
            mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(key, deValue);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        if (mSharedPreferences == null)
            mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
        mSharedPreferences.edit().putBoolean(key, value).commit();
    }

    public static int getUserId(Context context, String key, int deValue) {
        if (mSharedPreferences == null)
            mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
        return mSharedPreferences.getInt(key, deValue);
    }

    public static void putUserId(Context context, String key, int value) {
        if (mSharedPreferences == null)
            mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
        mSharedPreferences.edit().putInt(key, value).commit();
    }

    public static String getName(Context context, String key, String deValue) {
        if (mSharedPreferences == null)
            mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(key, deValue);
    }

    public static void putName(Context context, String key, String value) {
        if (mSharedPreferences == null)
            mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
        mSharedPreferences.edit().putString(key, value).commit();
    }


    public static boolean getHouseCod(Context context, String key, boolean deValue) {
        if (mSharedPreferences == null)
            mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(key, deValue);
    }

    public static void putHouseCod(Context context, String key, boolean value) {
        if (mSharedPreferences == null)
            mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
        mSharedPreferences.edit().putBoolean(key, value).commit();
    }
    public static String getAccount(Context context, String key, String deValue) {
        if (mSharedPreferences == null)
            mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(key, deValue);
    }

    public static void putAccount(Context context, String key, String value) {
        if (mSharedPreferences == null)
            mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
        mSharedPreferences.edit().putString(key, value).commit();
    }

}
