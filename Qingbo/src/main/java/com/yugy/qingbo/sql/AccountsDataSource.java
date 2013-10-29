package com.yugy.qingbo.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.yugy.qingbo.func.Func;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yugy on 13-9-15.
 */
public class AccountsDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_ACCESS_TOKEN,
            MySQLiteHelper.COLUMN_EXPIRES_IN,
            MySQLiteHelper.COLUMN_USER_ID,
            MySQLiteHelper.COLUMN_USING
    };

    public AccountsDataSource(Context context){
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    /**
     * 在数据库中创建account   ， 若存在相同userId则更新数据库
     * @param object
     * @return account对象
     * @throws JSONException
     */
    public void createAccount(JSONObject object) throws JSONException {
        Account account = getAccount(object.getString("uid"));
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ACCESS_TOKEN, object.getString("access_token"));
        values.put(MySQLiteHelper.COLUMN_EXPIRES_IN, object.getString("expires_in"));
        values.put(MySQLiteHelper.COLUMN_USER_ID, object.getString("uid"));
        values.put(MySQLiteHelper.COLUMN_USING, 1);
        setAllAccountsUnusing();
        if(account != null){
            database.update(MySQLiteHelper.TABLE_ACCOUNTS, values,
                    MySQLiteHelper.COLUMN_USER_ID + "=" + account.getUserId(), null);
        }else{
            database.insert(MySQLiteHelper.TABLE_ACCOUNTS, null, values);
        }
    }

    public void setAllAccountsUnusing(){
        List<Account> accounts = getAllAccounts();
        for(Account account : accounts){
            account.setUsing(false);
            updateAccount(account);
        }
    }

    public void updateAccount(Account account){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ACCESS_TOKEN, account.getAccessToken());
        values.put(MySQLiteHelper.COLUMN_EXPIRES_IN, account.getExpiresIn());
        values.put(MySQLiteHelper.COLUMN_USER_ID, account.getUserId());
        values.put(MySQLiteHelper.COLUMN_USING, account.getUsingNum());
        database.update(MySQLiteHelper.TABLE_ACCOUNTS, values,
                MySQLiteHelper.COLUMN_USER_ID + "=" + account.getUserId(), null);
    }

    /**
     * 根据userId读取数据库获取account对象
     * @param userId
     * @return
     */
    public Account getAccount(String userId){
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ACCOUNTS, allColumns,
                MySQLiteHelper.COLUMN_USER_ID + "=" + userId, null, null, null, null);
        if(cursor.moveToFirst()){
            Account account = cursorToAccount(cursor);
            cursor.close();
            return account;
        }else{
            return null;
        }
    }

    /**
     * 获取using项为true(1)的account
     * @return
     */
    public Account getUsingAccount(){
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ACCOUNTS, allColumns,
                MySQLiteHelper.COLUMN_USING + "=" + "1", null, null, null, null);
        if(cursor.moveToFirst()){
            Account account = cursorToAccount(cursor);
            cursor.close();
            return account;
        }else{
            return null;
        }

    }

    /**
     * 检查是否存在用户
     * @return
     */
    public boolean hasAccount(){
        Account account = getUsingAccount();
        if(account == null){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 删除account
     * @param account
     */
    public void deleteAccount(Account account){
        long id = account.getId();
        Func.log("删除ID为" + id + "的account");
        database.delete(MySQLiteHelper.TABLE_ACCOUNTS, MySQLiteHelper.COLUMN_ID + "=" + id, null);
        Func.log("设置默认account");
        List<Account> accounts = getAllAccounts();
        accounts.get(0).setUsing(true);
        updateAccount(accounts.get(0));
    }

    /**
     * 获取所有account
     * @return
     */
    public List<Account> getAllAccounts(){
        List<Account> accounts = new ArrayList<Account>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ACCOUNTS, allColumns,
                null, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Account account = cursorToAccount(cursor);
            accounts.add(account);
            cursor.moveToNext();
        }
        cursor.close();
        return accounts;
    }

    private Account cursorToAccount(Cursor cursor){
        Account account = new Account();
        account.setId(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
        account.setAccessToken(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ACCESS_TOKEN)));
        account.setExpiresIn(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_EXPIRES_IN)));
        account.setUserId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_USER_ID)));
        if(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_USING)) == 1){
            account.setUsing(true);
        }else{
            account.setUsing(false);
        }
        return account;
    }

}
