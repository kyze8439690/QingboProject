package com.yugy.qingbo.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.yugy.qingbo.R;
import com.yugy.qingbo.model.TimeLineModel;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by yugy on 13-11-7.
 */
public class DetailActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initViews();
        getData();
    }

    private ActionBar actionBar;
    private ImageView image;
    private ProgressBar progress;

    private PhotoViewAttacher photoViewAttacher;
    private TimeLineModel data;

    public final static int VIEW_TYPE_PIC = 0;
    public final static int VIEW_TYPE_REPOST_PIC = 1;

    private void getData(){
        data = getIntent().getParcelableExtra("data");
        switch (getIntent().getIntExtra("viewType", -1)){
            case VIEW_TYPE_PIC:
                displayImage(data.picUrlHD);
                break;
            case VIEW_TYPE_REPOST_PIC:
                displayImage(data.repostPicUrlHD);
                break;
        }
    }

    private void initViews(){
        actionBar = getActionBar();
        image = (ImageView) findViewById(R.id.detail_pic);
        photoViewAttacher = new PhotoViewAttacher(image);
        photoViewAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v2) {
                if(actionBar.isShowing()){
                    actionBar.hide();
                }else{
                    actionBar.show();
                }
            }
        });
        progress = (ProgressBar) findViewById(R.id.detail_progress);
    }

    private void displayImage(String url){
        ImageLoader.getInstance().displayImage(url, image, new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisc(true)
                .displayer(new FadeInBitmapDisplayer(600))
                .build(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {}

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {}

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                progress.setVisibility(View.GONE);
                photoViewAttacher.update();
            }

            @Override
            public void onLoadingCancelled(String s, View view) {}
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if(NavUtils.shouldUpRecreateTask(this, upIntent)){
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
                }else{
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
