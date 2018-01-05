package com.etouchsky.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.etouchsky.activity.SharePasswordActivity;
import com.etouchsky.data.DataHelper;

/**
 * Created by Administrator on 2017/10/11 0011.
 */

public class DataUtil {

    public static SQLiteDatabase getSQLiteDb(Context context){

        DataHelper dbHelper = new DataHelper(context);
        return dbHelper.getReadableDatabase();
    }
}
