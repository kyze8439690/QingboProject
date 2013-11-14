package com.yugy.qingbo.helper;

import android.widget.AbsListView;

/**
 * Created by yugy on 13-11-14.
 */
public class SpeedScrollListener implements AbsListView.OnScrollListener {

    private int previousFirstVisibleItem = 0;
    private long previousEventTime = 0, currTime, timeToScrollOneElement;
    private double speed = 0;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(previousFirstVisibleItem != firstVisibleItem){
            currTime = System.currentTimeMillis();
            timeToScrollOneElement = currTime - previousEventTime;
            speed = ((double) 1 / timeToScrollOneElement) * 1000;

            previousFirstVisibleItem = firstVisibleItem;
            previousEventTime = currTime;
        }
    }

    public double getSpeed(){
        return speed;
    }
}
