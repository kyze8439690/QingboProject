package com.yugy.qingbo.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.yugy.qingbo.R;
import com.yugy.qingbo.func.Func;
import com.yugy.qingbo.model.TimeLineModel;
import com.yugy.qingbo.sdk.Weibo;
import com.yugy.qingbo.sql.AccountsDataSource;
import com.yugy.qingbo.widget.TimeLineListItem;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

public class MainActivity extends Activity implements ListView.OnItemClickListener, PullToRefreshAttacher.OnRefreshListener{

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private ListView mTimeLineList;
    private ActionBar mActionbar;
    private ActionBarDrawerToggle mDrawerToggle;

    private PullToRefreshAttacher mPullToRefreshAttacher;
    private PullToRefreshLayout mPullToRefreshLayout;
    private AccountsDataSource accountsDataSource;
    private String[] drawerListViewString;
    private ArrayList<TimeLineModel> timeLineData;
    private BaseAdapter timeLineListAdapter;

    private long lastStatusId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appInit();
        initViews();
        initComponents();
        if(hasAccount()){
            mPullToRefreshAttacher.setRefreshing(true);
            Weibo.getTimeline(this, lastStatusId + "", new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(JSONArray response) {
                    for(int i = 0; i < response.length(); i++){
                        TimeLineModel data = new TimeLineModel();
                        try {
                            if(i == 0){
                                lastStatusId = response.getJSONObject(i).getLong("id");
                            }
                            data.parse(response.getJSONObject(i));
                            timeLineData.add(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    mPullToRefreshAttacher.setRefreshing(false);
                    timeLineListAdapter.notifyDataSetChanged();
                    super.onSuccess(response);
                }
            });
        }
    }

    private void appInit(){
        Func.setContext(getApplicationContext());
    }

    private boolean hasAccount(){
        accountsDataSource = new AccountsDataSource(this);
        accountsDataSource.open();
        return accountsDataSource.hasAccount();
    }

    private void initViews(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawerlayout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);
        mActionbar = getActionBar();
        mActionbar.setDisplayHomeAsUpEnabled(true);
        mActionbar.setHomeButtonEnabled(true);
        mDrawerListView = (ListView) findViewById(R.id.main_left_drawer);
        mTimeLineList = (ListView) findViewById(R.id.main_timeline_list);
        mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.main_refreshlayout);
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
        mPullToRefreshLayout.setPullToRefreshAttacher(mPullToRefreshAttacher, this);

        drawerListViewString = new String[]{
            "账号",
            "设置"
        };
        mDrawerListView.setAdapter(new ArrayAdapter<String>(
                this,
                R.layout.widget_drawer_menu_item,
                drawerListViewString
        ));
        mDrawerListView.setOnItemClickListener(this);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer_toggle,
                R.string.app_name,
                R.string.app_name
        );

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void initComponents(){
        timeLineData = new ArrayList<TimeLineModel>();
        timeLineListAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return timeLineData.size();
            }

            @Override
            public Object getItem(int position) {
                TimeLineListItem item = new TimeLineListItem(MainActivity.this);
                item.parse(timeLineData.get(position));
                return item;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TimeLineListItem item;
                if(convertView != null){
                    item = (TimeLineListItem) convertView;
                }else{
                    item = new TimeLineListItem(MainActivity.this);
                }
                item.parse(timeLineData.get(position));
                return item;
            }
        };
        mTimeLineList.setAdapter(timeLineListAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch (item.getItemId()){
            case R.id.main_action_refresh:
                item.setVisible(false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
            case 0:
                startActivity(new Intent(MainActivity.this, AccountActivity.class));
                break;
        }
    }

    @Override
    public void onRefreshStarted(View view) {
        Weibo.getTimeline(this, lastStatusId + "", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONArray response) {
                for(int i = 0; i < timeLineData.size(); i++){
                    try{
                        timeLineData.get(i).reParseTime();
                    }catch (ParseException e){
                        e.printStackTrace();
                    }
                }
                for(int i = response.length() - 1; i >= 0; i--){
                    TimeLineModel data = new TimeLineModel();
                    try {
                        if(i == 0){
                            lastStatusId = response.getJSONObject(i).getLong("id");
                        }
                        data.parse(response.getJSONObject(i));
                        timeLineData.add(0, data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                timeLineListAdapter.notifyDataSetChanged();
                if(response.length() == 0){
                    Func.toast("没有新微博");
                }else{
                    Func.toast("更新了" + response.length() +"条新微薄");
                }
                mPullToRefreshAttacher.setRefreshComplete();
                super.onSuccess(response);
            }
        });
    }
}
