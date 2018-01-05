package com.etouchsky.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.etouchsky.pojo.UserCommunities;

import java.util.List;

/**
 * Created by Administrator on 2017/9/29 0029.
 */

public class CommunitiesInitData {

    private final SQLiteDatabase db;
    private ContentValues cv;
    private List<UserCommunities> userResultInfo;


    //创建用户ID表构造方法
    public CommunitiesInitData(Context context, List<UserCommunities> userResultInfo) {
        db = new DataHelper(context).getReadableDatabase();
        this.userResultInfo = userResultInfo;
        initData();
    }


    //接受物业通知的方法
    public CommunitiesInitData(Context context, String message) {
        db = new DataHelper(context).getReadableDatabase();
        initMoreLine(message);
    }

    public SQLiteDatabase getDb() {
        if (db == null)
            return null;
        return db;
    }


    private void initData() {
        //生成ContentValues对象 //key:列名，value:想插入的值
        cv = new ContentValues();
        //往ContentValues对象存放数据，键-值对模式
        for (int i = 0; i < userResultInfo.size(); i++) {
            cv.put("CmtID", userResultInfo.get(i).getCmtid());
            cv.put("CmtName", userResultInfo.get(i).getCmtname());
            cv.put("CmtAddress", userResultInfo.get(i).getCmtaddress());
            cv.put("Lat", userResultInfo.get(i).getLat());
            cv.put("Lng", userResultInfo.get(i).getLng());
            cv.put("UnitNo", userResultInfo.get(i).getUnitno());
            cv.put("RoomNo", userResultInfo.get(i).getRoomno());
            cv.put("UnitName", userResultInfo.get(i).getUnitname());
            //调用insert方法，将数据插入数据库
            db.insert("CommunitiesInfo", null, cv);
        }
        //关闭数据库
        db.close();
    }

    private void initMoreLine(String message) {
        //生成ContentValues对象 //key:列名，value:想插入的值
        cv = new ContentValues();
        //往ContentValues对象存放数据，键-值对模式
        cv.put("Content", message);
        //调用insert方法，将数据插入数据库
        db.insert("CommunitiesMoreLine", null, cv);
        //关闭数据库
        db.close();
    }


}
