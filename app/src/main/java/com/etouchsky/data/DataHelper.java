package com.etouchsky.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2017/9/29 0029.
 */

public class DataHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;            //版本号
    private static final String DB_NAME = "UserInfo.db";   //数据库名称
    public static final String TABLE_NAME = "CommunitiesInfo";    //用户小区ID表名
    public static final String TABLE_NAME_MORE_LINE = "CommunitiesMoreLine";    //用户小区物业管理表名
    private String sql;


    public DataHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // 当第一次创建数据库的时候，调用该方法
    @Override
    public void onCreate(SQLiteDatabase db) {
        //        创建表
        sql = "create table if not exists " + TABLE_NAME +
                " (Id integer primary key, CmtID integer, CmtName text, CmtAddress text, Lat REAL, Lng REAL, UnitNo text, RoomNo text, UnitName text)";
        db.execSQL(sql);
        sql = "create table if not exists " + TABLE_NAME_MORE_LINE +
                " (Id integer primary key, Content text)";
        db.execSQL(sql);
    }

    //当更新数据库的时候执行该方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //输出更新数据库的日志信息
        Log.i("****", "update Database------------->");
    }
}
