package com.yugy.qingbo.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.PagerAdapter;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yugy.qingbo.R;
import com.yugy.qingbo.func.FuncInt;
import com.yugy.qingbo.model.TimeLineModel;
import com.yugy.qingbo.widget.HackyViewPager;
import com.yugy.qingbo.widget.SlidingUpPanelLayout;

import uk.co.senab.photoview.PhotoView;
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

    private SlidingUpPanelLayout slidingLayout;
    private RelativeLayout frontLayout;
    private RelativeLayout headLayout;
    private RelativeLayout contentLayout;
    private ActionBar actionBar;
    private HackyViewPager viewPager;
    private ProgressBar progress;
    private TextView actionbarTitle;
    private ImageView head;
    private TextView name;
    private TextView text;
    private TextView repostName;
    private TextView repostText;

    private TimeLineModel data;
    private Drawable actionBarBackgroundDrawable;
    private Drawable repostNameBackgroundDrawable;

    public final static String DATA = "data";
    public final static String VIEW_TYPE = "viewType";
    public final static String VIEW_PICS_ITEM_ID = "picId";

    public final static int VIEW_TYPE_CONTENT = 0;
    public final static int VIEW_TYPE_PIC = 1;

    private void getData(){
        data = getIntent().getParcelableExtra(DATA);
        switch (getIntent().getIntExtra(VIEW_TYPE, -1)){
            case VIEW_TYPE_PIC:
                displayImage(getIntent().getIntExtra(VIEW_PICS_ITEM_ID, -1));
                break;
            case VIEW_TYPE_CONTENT:
                text.setSingleLine(false);
                actionBarBackgroundDrawable.setAlpha(255);
                actionBarBackgroundDrawable.invalidateSelf();
                actionbarTitle.setTextColor(Color.BLACK);
                name.setTextColor(Color.BLACK);
                text.setTextColor(Color.BLACK);
                headLayout.setBackgroundColor(Color.WHITE);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                    contentLayout.setBackground(repostNameBackgroundDrawable);
                }else{
                    contentLayout.setBackgroundDrawable(repostNameBackgroundDrawable);
                }
                slidingLayout.expandPane();
                if(data.hasPic || data.hasRepostPic){
                    displayImage(0);
                }else{
                    slidingLayout.setSlidingEnabled(false);
                }
                break;
        }
        ImageLoader.getInstance().displayImage(data.headUrl, head, new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.default_head)
                .showImageForEmptyUri(R.drawable.default_head)
                .showImageForEmptyUri(R.drawable.default_head)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .displayer(new RoundedBitmapDisplayer(FuncInt.dp(40)))
                .build());
        name.setText(data.name);
        text.setText(data.text);
        if(data.hasRepost){
            repostName.setVisibility(View.VISIBLE);
            repostText.setVisibility(View.VISIBLE);
            repostName.setText(data.repostName);
            repostText.setText(data.repostText);
        }
    }

    private void initViews(){
        actionBar = getActionBar();
        actionBarBackgroundDrawable = getResources().getDrawable(R.drawable.ab_solid_light_holo);
        repostNameBackgroundDrawable = getResources().getDrawable(R.drawable.bg_detail_contentlayout);
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN){
            actionBarBackgroundDrawable.setCallback(mDrawableCallback);
            repostNameBackgroundDrawable.setCallback(mDrawableCallback);
        }
        actionBarBackgroundDrawable.setAlpha(0);
        actionBar.setBackgroundDrawable(actionBarBackgroundDrawable);
        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.detail_rootlayout);
        slidingLayout.setPanelOverlayable(true);
        slidingLayout.setPanelTransparent(true);
        slidingLayout.setCoveredFadeColor(Color.TRANSPARENT);
        slidingLayout.setPanelHeight(FuncInt.dp(82 + 48));
        slidingLayout.setEnableDragViewTouchEvents(true);
        slidingLayout.setPanelSlideListener(new SlidingUpPanelLayout.SimplePanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                int alpha = (int) (255 * (1 - slideOffset));
                if(alpha > 0 && !contentLayout.getBackground().equals(repostNameBackgroundDrawable)){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                        contentLayout.setBackground(repostNameBackgroundDrawable);
                    }else{
                        contentLayout.setBackgroundDrawable(repostNameBackgroundDrawable);
                    }
                }
                actionBarBackgroundDrawable.setAlpha(alpha);
                repostNameBackgroundDrawable.setAlpha(alpha);
                actionBarBackgroundDrawable.invalidateSelf();
                repostNameBackgroundDrawable.invalidateSelf();
                int fontColor;
                int backgroundColor = Color.argb(alpha, 255, 255, 255);
                if (alpha < 128) {
                    fontColor = Color.argb(255 - 2 * alpha, 255, 255, 255);
                } else {
                    fontColor = Color.argb(2 * (alpha - 128), 0, 0, 0);
                }
                actionbarTitle.setTextColor(fontColor);
                name.setTextColor(fontColor);
                text.setTextColor(fontColor);
                repostName.setTextColor(fontColor);
                repostText.setTextColor(fontColor);
                headLayout.setBackgroundColor(backgroundColor);
            }

            @Override
            public void onPanelExpanded(View panel) {
                if (!actionBar.isShowing()) {
                    actionBar.show();
                }
                text.setSingleLine(false);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                headLayout.setBackgroundResource(R.drawable.black_gradient);
                text.setSingleLine(true);
                if(slidingLayout.getPanelHeight() == FuncInt.dp(48)){
                    actionBar.hide();
                }
            }
        });
        frontLayout = (RelativeLayout) findViewById(R.id.detail_frontlayout);
        frontLayout.setOnClickListener(this);
        headLayout = (RelativeLayout) findViewById(R.id.detail_frontlayout_head_layout);
//        slidingLayout.setDragView(headLayout);
        contentLayout = (RelativeLayout)    findViewById(R.id.detail_frontlayout_content_layout);
        viewPager = (HackyViewPager) findViewById(R.id.detail_picpager);
        progress = (ProgressBar) findViewById(R.id.detail_progress);
        int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        actionbarTitle = (TextView) findViewById(titleId);
        actionbarTitle.setTextColor(Color.WHITE);
        head = (ImageView) findViewById(R.id.detail_head);
        name = (TextView) findViewById(R.id.detail_name);
        text = (TextView) findViewById(R.id.detail_text);
        text.setMovementMethod(LinkMovementMethod.getInstance());
        repostName = (TextView) findViewById(R.id.detail_repost_name);
        repostName.setMovementMethod(LinkMovementMethod.getInstance());
        repostText = (TextView) findViewById(R.id.detail_repost_text);
        repostText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private Drawable.Callback mDrawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            actionBar.setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {}

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {}
    };

    private DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(false)
            .cacheOnDisc(true)
            .displayer(new FadeInBitmapDisplayer(600))
            .build();

    private void displayImage(final int index){
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return data.pics.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;
            }

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {
                final PhotoView photoView = new PhotoView(DetailActivity.this);
                photoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                    @Override
                    public void onViewTap(View view, float v, float v2) {
                        if (actionBar.isShowing()) {
                            actionBar.hide();
                            slidingLayout.setPanelHeight(FuncInt.dp(48));
                        } else {
                            actionBar.show();
                            slidingLayout.setPanelHeight(FuncInt.dp(82 + 48));
                        }
                    }
                });
                ImageSize targetSize = new ImageSize(4096, 4096);
                ImageLoader.getInstance().loadImage(data.pics.get(position).replace("thumbnail", "large"), targetSize, displayImageOptions, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        photoView.setImageBitmap(loadedImage);
                        if(position == index){
                            progress.setVisibility(View.GONE);
                        }
                    }
                });
                container.addView(photoView);
                return photoView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
        viewPager.setCurrentItem(index);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.detail_frontlayout:
                if(slidingLayout.isExpanded() && (data.hasPic || data.hasRepostPic)){
                    slidingLayout.collapsePane();
                }else{
                    slidingLayout.expandPane();
                }
                break;
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
