package com.yugy.qingbo.activity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.umeng.analytics.MobclickAgent;
import com.yugy.qingbo.R;
import com.yugy.qingbo.sql.Account;
import com.yugy.qingbo.sql.AccountsDataSource;
import com.yugy.qingbo.sql.User;
import com.yugy.qingbo.sql.UsersDataSource;
import com.yugy.qingbo.widget.AccountItem;

import java.util.ArrayList;
import java.util.List;

public class AccountActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        initViews();
    }

    private AccountsDataSource accountsDataSource;
    private UsersDataSource usersDataSource;
    private List<Account> accounts;
    private BaseAdapter adapter;

    @Override
    protected void onResume() {
        accounts = accountsDataSource.getAllAccounts();
        adapter.notifyDataSetChanged();
        MobclickAgent.onResume(this);
        super.onResume();
    }

    private void initViews(){
        accounts = new ArrayList<Account>();
        accountsDataSource = new AccountsDataSource(this);
        usersDataSource = new UsersDataSource(this);
        accountsDataSource.open();
        usersDataSource.open();
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return accounts.size();
            }

            @Override
            public Object getItem(int position) {
                Account account = accounts.get(position);
                User user = usersDataSource.getUser(account.getUserId());
                AccountItem accountItem = new AccountItem(AccountActivity.this);
                if(user != null){
                    accountItem.getName().setText(user.getScreenName());
                }else{
                    accountItem.getName().setText("获取用户信息失败");
                }
                return accountItem;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Account account = accounts.get(position);
                User user = usersDataSource.getUser(account.getUserId());
                AccountItem accountItem;
                if(convertView != null){
                    accountItem = (AccountItem) convertView;
                }else{
                    accountItem = new AccountItem(AccountActivity.this);
                }
                if(user != null){
                    accountItem.getName().setText(user.getScreenName());
                }else{
                    accountItem.getName().setText("获取用户信息失败");
                }

                return accountItem;
            }
        };
        setListAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        accountsDataSource.close();
        usersDataSource.close();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.account_manage_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.account_manage_action_add:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("选择平台").setItems(new String[]{
                    "新浪微博"
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                startActivity(new Intent(AccountActivity.this, WeiboLoginActivity.class));
                                break;
                        }
                    }
                }).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
