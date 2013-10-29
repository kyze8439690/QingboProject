package com.yugy.qingbo.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by yugy on 13-9-10.
 */
public class MySQLiteHelper extends SQLiteOpenHelper{

    public static final String TABLE_ACCOUNTS = "accounts";
    public static final String TABLE_USERS = "users";

    public static final String COLUMN_ID = "_id";

    public static final String COLUMN_ACCESS_TOKEN = "access_token";
    public static final String COLUMN_EXPIRES_IN = "expires_in";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USING = "_using";

    public static final String COLUMN_SCREEN_NAME = "screen_name";
    public static final String COLUMN_AVATAR_URL = "avatar_url";
    public static final String COLUMN_JSON = "_json";


    private static final String DATABASE_NAME = "qingbo.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_ACCOUNT_CREATE = "create table "
            + TABLE_ACCOUNTS + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_ACCESS_TOKEN + " text not null, " +
            COLUMN_EXPIRES_IN + " text not null, " +
            COLUMN_USER_ID + " text not null, " +
            COLUMN_USING + " integer not null)";

    private static final String DATABASE_USER_CREATE = "create table "
            + TABLE_USERS + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_USER_ID + " text not null, " +
            COLUMN_SCREEN_NAME + " text not null, " +
            COLUMN_AVATAR_URL + " text not null, " +
            COLUMN_JSON + " text not null)";

    private static final String DATABASE_ACCOUNT_DROP = "DROP TABLE IF EXISTS "
            + TABLE_ACCOUNTS;

    private static final String DATABASE_USER_DROP = "DROP TABLE IF EXISTS "
            + TABLE_USERS;


    public MySQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_ACCOUNT_CREATE);
        db.execSQL(DATABASE_USER_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(), "升级数据库，从版本" + oldVersion + "到版本" + newVersion);
        db.execSQL(DATABASE_ACCOUNT_DROP);
        db.execSQL(DATABASE_USER_DROP);
        onCreate(db);
    }
}
