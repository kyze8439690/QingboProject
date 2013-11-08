package com.yugy.qingbo.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.yugy.qingbo.R;
import com.yugy.qingbo.func.Func;
import com.yugy.qingbo.func.FuncInt;
import com.yugy.qingbo.func.FuncNet;
import com.yugy.qingbo.model.TimeLineModel;
import com.yugy.qingbo.sdk.Weibo;
import com.yugy.qingbo.sql.AccountsDataSource;
import com.yugy.qingbo.storage.ColorList;
import com.yugy.qingbo.widget.TimeLineListItem;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

public class MainActivity extends Activity implements ListView.OnItemClickListener, PullToRefreshAttacher.OnRefreshListener{

    private DrawerLayout mDrawerLayout;
    private RelativeLayout mDrawerLeftLayout;
    private RelativeLayout mDrawerRightLayout;
    private ListView mDrawerLeftList;
    private ListView mDrawerRightList;
    private ListView mTimeLineList;
    private ActionBar mActionbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView mEmptyView;

    private PullToRefreshAttacher mPullToRefreshAttacher;
    private PullToRefreshLayout mPullToRefreshLayout;
    private DefaultHeaderTransformer headerTransformer;
    private AccountsDataSource accountsDataSource;
    private String[] drawerListViewString;
    private ArrayList<TimeLineModel> timeLineData;
    private BaseAdapter timeLineListAdapter;
    private AnimationDrawable mJingleDrawable;

    private long firstStatusId = 0;
    private long lastStatusId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Debug.startMethodTracing();
        MobclickAgent.setDebugMode(true);
        MobclickAgent.onError(this);
        UmengUpdateAgent.update(this);
        appInit();
        initViews();
        initComponents();
        if(hasAccount()){
            getNewData();
        }
    }

    @Override
    protected void onDestroy() {
//        Debug.stopMethodTracing();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void appInit(){
        Func.setContext(getApplicationContext());
        FuncNet.setContext(getApplicationContext());
    }

    private boolean hasAccount(){
        accountsDataSource = new AccountsDataSource(this);
        accountsDataSource.open();
        return accountsDataSource.hasAccount();
    }

    private void initViews(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawerlayout);
        mActionbar = getActionBar();
        mActionbar.setDisplayHomeAsUpEnabled(true);
        mActionbar.setHomeButtonEnabled(true);
        mDrawerLeftLayout = (RelativeLayout) findViewById(R.id.main_left_drawer);
        mDrawerLeftList = (ListView) findViewById(R.id.main_left_drawer_list);
        mDrawerRightLayout = (RelativeLayout) findViewById(R.id.main_right_drawer);
        mDrawerRightList = (ListView) findViewById(R.id.main_right_drawer_list);
        mEmptyView = (TextView) findViewById(R.id.main_right_drawer_emptyview);
        mJingleDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.jingles);
        mEmptyView.setCompoundDrawablesWithIntrinsicBounds(null, mJingleDrawable, null, null);
        mEmptyView.setCompoundDrawablePadding(FuncInt.dp(10));
        mDrawerRightList.setEmptyView(mEmptyView);
        mTimeLineList = (ListView) findViewById(R.id.main_timeline_list);
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        mTimeLineList.addFooterView(progressBar);
        PauseOnScrollListener pauseOnScrollListener = new PauseOnScrollListener(ImageLoader.getInstance(), false, true);
        mTimeLineList.setOnScrollListener(pauseOnScrollListener);
        mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.main_refreshlayout);

        drawerListViewString = new String[]{
            "账号",
            "设置"
        };
        mDrawerLeftList.setAdapter(new ArrayAdapter<String>(
                this,
                R.layout.widget_drawer_menu_item,
                drawerListViewString
        ));
        mDrawerLeftList.setOnItemClickListener(this);
        mDrawerRightList.setOnItemClickListener(this);
        mTimeLineList.setOnItemClickListener(this);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer_toggle,
                R.string.app_name,
                R.string.app_name){

                @Override
                public void onDrawerOpened(View drawerView) {
                    if(drawerView.equals(mDrawerRightLayout)){
                        if(mJingleDrawable.isVisible()){
                            if(mJingleDrawable.isRunning()){
                                mJingleDrawable.stop();
                            }
                            mJingleDrawable.start();

                        }
                    }
                    if(mDrawerLayout.isDrawerOpen(Gravity.END) && drawerView.equals(mDrawerLeftLayout)){
                        mDrawerLayout.closeDrawer(Gravity.END);
                    }else if(mDrawerLayout.isDrawerOpen(Gravity.START) && drawerView.equals(mDrawerRightLayout)){
                        mDrawerLayout.closeDrawer(Gravity.START);
                    }
                    super.onDrawerOpened(drawerView);
                }
        };

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

            private int adapterLastPosition = -1;

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
                if (position > adapterLastPosition) {
                    item.setTranslationX(0.0f);
                    item.setTranslationY(200);
                    item.setRotationX(45.0f);
                    item.setScaleX(0.7f);
                    item.setScaleY(0.55f);
                    ViewPropertyAnimator localViewPropertyAnimator = item.animate().rotationX(0.0f).rotationY(0.0f)
                            .translationX(0).translationY(0).setDuration(600)
                            .scaleX(1.0f).scaleY(1.0f).setInterpolator(new DecelerateInterpolator());
                    localViewPropertyAnimator.setStartDelay(0).start();
                    adapterLastPosition = position;
                }
                if(position == getCount() - 1){
                    getOldData();
                }
                return item;
            }
        };
        mTimeLineList.setAdapter(timeLineListAdapter);
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
        headerTransformer = (DefaultHeaderTransformer) mPullToRefreshAttacher.getHeaderTransformer();
        headerTransformer.setProgressBarColor(ColorList.getColor());
        headerTransformer.setPullText("向下滑动即可刷新");
        headerTransformer.setRefreshingText("正在刷新...");
        mPullToRefreshAttacher.setHeaderViewListener(new PullToRefreshAttacher.HeaderViewListener() {
            @Override
            public void onStateChanged(View view, int i) {
                if(i == PullToRefreshAttacher.HeaderViewListener.STATE_VISIBLE){
                    headerTransformer.setProgressBarColor(ColorList.getColor());
                }
            }
        });
        mPullToRefreshLayout.setPullToRefreshAttacher(mPullToRefreshAttacher, this);
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
            case R.id.main_action_notify:
                if(mDrawerLayout.isDrawerVisible(Gravity.END)){
                    mDrawerLayout.closeDrawer(mDrawerRightLayout);
                }else{
                    mDrawerLayout.openDrawer(mDrawerRightLayout);
                }
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
        if(parent.equals(mDrawerLeftList)){
            switch(position){
                case 0:
                    startActivity(new Intent(MainActivity.this, AccountActivity.class));
                    break;
            }
        }else if(parent.equals(mTimeLineList)){

        }
    }

    private void getNewData(){
        mPullToRefreshAttacher.setRefreshing(true);
        Weibo.getNewTimeline(this, firstStatusId + "", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    TimeLineModel data = new TimeLineModel();
                    try {
                        if (i == 0) {
                            firstStatusId = response.getJSONObject(i).getLong("id");
                        }else if(i == response.length() - 1){
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
                Func.myToast("更新了" + response.length() + "条新微薄");
                mPullToRefreshAttacher.setRefreshing(false);
                timeLineListAdapter.notifyDataSetChanged();
                super.onSuccess(response);
            }
        });
    }

    private void getOldData(){
        mPullToRefreshAttacher.setRefreshing(true);
        Weibo.getOldTimeline(this, lastStatusId + "", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONArray response) {
                for (int i = 1; i < response.length(); i++) {
                    TimeLineModel data = new TimeLineModel();
                    try {
                        if(i == response.length() - 1){
                            lastStatusId = response.getJSONObject(i).getLong("id");
                        }
                        data.parse(response.getJSONObject(i));
                        timeLineData.add(timeLineData.size(), data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                Func.myToast("加载了" + (response.length() - 1) + "条微薄");
                timeLineListAdapter.notifyDataSetChanged();
                mPullToRefreshAttacher.setRefreshComplete();
                super.onSuccess(response);
            }
        });
    }

    @Override
    public void onRefreshStarted(View view) {
        Weibo.getNewTimeline(this, firstStatusId + "", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray response) {
                for (int i = 0; i < timeLineData.size(); i++) {
                    try {
                        timeLineData.get(i).reParseTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                for (int i = response.length() - 1; i >= 0; i--) {
                    TimeLineModel data = new TimeLineModel();
                    try {
                        if (i == 0) {
                            firstStatusId = response.getJSONObject(i).getLong("id");
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
                if (response.length() == 0) {
                    Func.myToast("没有新微博");
                } else {
                    Func.myToast("更新了" + response.length() + "条新微薄");
                }
                mPullToRefreshAttacher.setRefreshComplete();
                super.onSuccess(response);
            }
        });
    }
}
