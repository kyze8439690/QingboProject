package com.yugy.qingbo.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yugy on 13-9-15.
 */
public class UsersDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_USER_ID,
            MySQLiteHelper.COLUMN_SCREEN_NAME,
            MySQLiteHelper.COLUMN_AVATAR_URL,
            MySQLiteHelper.COLUMN_JSON
    };

    public UsersDataSource(Context context){
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public void createUser(JSONObject json) throws JSONException{
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_USER_ID, json.getString("id"));
        values.put(MySQLiteHelper.COLUMN_SCREEN_NAME, json.getString("screen_name"));
        values.put(MySQLiteHelper.COLUMN_AVATAR_URL, json.getString("profile_image_url"));
        values.put(MySQLiteHelper.COLUMN_JSON, json.toString());
        if(hasUser(json.getString("id"))){
            database.update(MySQLiteHelper.TABLE_USERS, values,
                    MySQLiteHelper.COLUMN_USER_ID + "=" + json.getString("id"), null);
        }else{
            database.insert(MySQLiteHelper.TABLE_USERS, null, values);
        }
    }

    public void updateUser(User user) throws JSONException {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_USER_ID, user.getUserId());
        values.put(MySQLiteHelper.COLUMN_SCREEN_NAME, user.getScreenName());
        values.put(MySQLiteHelper.COLUMN_AVATAR_URL, user.getAvatarUrl());
        values.put(MySQLiteHelper.COLUMN_JSON, user.getJson().toString());
        database.update(MySQLiteHelper.TABLE_USERS, values,
                MySQLiteHelper.COLUMN_USER_ID + "=" + user.getUserId(), null);
    }

    public boolean hasUser(String userId){
        User user = getUser(userId);
        if(user != null){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 根据userId从数据库中获取user信息
     * @param userId
     * @return
     */
    public User getUser(String userId){
        Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS, allColumns,
                MySQLiteHelper.COLUMN_USER_ID + "=" + userId, null, null, null, null);
        if(cursor.moveToFirst()){
            User user = cursorToUser(cursor);
            cursor.close();
            return user;
        }else{
            return null;
        }
    }

    private User cursorToUser(Cursor cursor){
        User user = new User();
        user.setId(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
        user.setUserId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_USER_ID)));
        user.setScreenName(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_SCREEN_NAME)));
        user.setAvatarUrl(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_AVATAR_URL)));
        user.setJson(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JSON)));
        return user;
    }
}
