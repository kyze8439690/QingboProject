package com.yugy.qingbo.widget;

import android.content.Context;
import android.content.Intent;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yugy.qingbo.R;
import com.yugy.qingbo.activity.DetailActivity;
import com.yugy.qingbo.func.FuncInt;
import com.yugy.qingbo.func.FuncNet;
import com.yugy.qingbo.model.TimeLineModel;

import java.util.ArrayList;

/**
 * Created by yugy on 13-10-4.
 */
public class TimeLineListItem extends RelativeLayout implements View.OnClickListener, AdapterView.OnItemClickListener{
    public TimeLineListItem(Context context) {
        super(context);
        init();
    }

    private ImageView head;
    private TextView name;
    private TextView text;
    private TextView topic;
    private TextView time;
    private NoScrollGridView gridView;
    private SelectorImageView pic;
    private TextView repostName;
    private TextView repostText;
    private TextView commentCount;
    private TextView repostCount;

    private LayoutInflater layoutInflater;
    private TimeLineModel data;

    private void init(){
        layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.widget_timeline_listitem, this);
        head = (ImageView) findViewById(R.id.timeline_listitem_head);
        name = (TextView) findViewById(R.id.timeline_listitem_name);
        text = (TextView) findViewById(R.id.timeline_listitem_text);
        text.setMovementMethod(LinkMovementMethod.getInstance());
        topic = (TextView) findViewById(R.id.timeline_listitem_topic);
        time = (TextView) findViewById(R.id.timeline_listitem_time);
        gridView = (NoScrollGridView) findViewById(R.id.timeline_listitem_picgrid);
        pic = (SelectorImageView) findViewById(R.id.timeline_listitem_pic);
        repostName = (TextView) findViewById(R.id.timeline_listitem_repost_name);
        repostName.setMovementMethod(LinkMovementMethod.getInstance());
        repostText = (TextView) findViewById(R.id.timeline_listitem_repost_text);
        repostText.setMovementMethod(LinkMovementMethod.getInstance());
        commentCount = (TextView) findViewById(R.id.timeline_listitem_commentcount);
        repostCount = (TextView) findViewById(R.id.timeline_listitem_repostcount);

        gridView.setOnItemClickListener(this);
        pic.setOnClickListener(this);
        commentCount.setOnClickListener(this);
        repostCount.setOnClickListener(this);
    }

    public void parse(TimeLineModel data){
        this.data = data;
        if(data.hasPics || data.hasRepostPics){
            pic.setVisibility(GONE);
            gridView.setVisibility(VISIBLE);
            gridView.setAdapter(new PicsAdapter(data.pics));
            LayoutParams lp = (LayoutParams) commentCount.getLayoutParams();
            lp.addRule(BELOW, R.id.timeline_listitem_picgrid);
            commentCount.setLayoutParams(lp);
        }else{
            gridView.setVisibility(GONE);
            LayoutParams lp = (LayoutParams) commentCount.getLayoutParams();
            lp.addRule(BELOW, R.id.timeline_listitem_pic);
            commentCount.setLayoutParams(lp);
            if(data.hasPic || data.hasRepostPic){
                pic.setVisibility(VISIBLE);
                if(FuncNet.isWifi()){
                    ImageLoader.getInstance().displayImage(data.pics.get(0).replace("thumbnail", "bmiddle"), pic);
                }else{
                    ImageLoader.getInstance().displayImage(data.pics.get(0), pic);
                }
            }else{
                pic.setVisibility(GONE);
            }
        }
        if(data.topics.size() == 0){
            topic.setVisibility(INVISIBLE);
        }else{
            topic.setVisibility(VISIBLE);
            topic.setText(data.topics.get(0));
        }
        if(data.hasRepost){
            repostName.setVisibility(VISIBLE);
            repostText.setVisibility(VISIBLE);
            repostName.setText(data.repostName);
            repostText.setText(data.repostText);
        }else{
            repostName.setVisibility(GONE);
            repostText.setVisibility(GONE);
        }
        if(data.commentCount != 0){
            commentCount.setText(data.commentCount + "");
        }else{
            commentCount.setText("评论");
        }
        if(data.repostCount != 0){
            repostCount.setText(data.repostCount + "");
        }else{
            repostCount.setText("转发");
        }

        text.setText(data.text);
        name.setText(data.name);
        time.setText(data.time);
        ImageLoader.getInstance().displayImage(data.headUrl, head, new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.default_head)
                .showImageForEmptyUri(R.drawable.default_head)
                .showImageForEmptyUri(R.drawable.default_head)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .displayer(new RoundedBitmapDisplayer(FuncInt.dp(25)))
                .build());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra(DetailActivity.DATA, data);
        intent.putExtra(DetailActivity.VIEW_TYPE, DetailActivity.VIEW_TYPE_PIC);
        intent.putExtra(DetailActivity.VIEW_PICS_ITEM_ID, position);
        getContext().startActivity(intent);
    }

    private class PicsAdapter extends BaseAdapter{

        private ArrayList<String> data;
        private int imageWidth = FuncInt.dp(80);

        public PicsAdapter(ArrayList<String> model){
            data = model;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView image = (ImageView) convertView;
            if(image == null){
                image = new ImageView(getContext());
                image.setLayoutParams(new AbsListView.LayoutParams(imageWidth, imageWidth));
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            ImageLoader.getInstance().displayImage(data.get(position), image);
            return image;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.timeline_listitem_pic:
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra(DetailActivity.DATA, data);
                intent.putExtra(DetailActivity.VIEW_TYPE, DetailActivity.VIEW_TYPE_PIC);
                intent.putExtra(DetailActivity.VIEW_PICS_ITEM_ID, 0);
                getContext().startActivity(intent);
                break;
        }
    }
}
