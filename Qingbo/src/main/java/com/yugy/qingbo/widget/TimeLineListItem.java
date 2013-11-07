package com.yugy.qingbo.widget;

import android.content.Context;
import android.content.Intent;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
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

/**
 * Created by yugy on 13-10-4.
 */
public class TimeLineListItem extends RelativeLayout implements View.OnClickListener{
    public TimeLineListItem(Context context) {
        super(context);
        init();
    }

    private ImageView head;
    private TextView name;
    private TextView text;
    private TextView topic;
    private TextView time;
    private SeletorImageView pic;
    private TextView repostName;
    private TextView repostText;
    private TextView commentCount;
    private TextView repostCount;

    private TimeLineModel data;

    private void init(){
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.widget_timeline_listitem, this);
        head = (ImageView) findViewById(R.id.timeline_listitem_head);
        name = (TextView) findViewById(R.id.timeline_listitem_name);
        text = (TextView) findViewById(R.id.timeline_listitem_text);
        text.setMovementMethod(LinkMovementMethod.getInstance());
        topic = (TextView) findViewById(R.id.timeline_listitem_topic);
        time = (TextView) findViewById(R.id.timeline_listitem_time);
        pic = (SeletorImageView) findViewById(R.id.timeline_listitem_pic);
        repostName = (TextView) findViewById(R.id.timeline_listitem_repost_name);
        repostName.setMovementMethod(LinkMovementMethod.getInstance());
        repostText = (TextView) findViewById(R.id.timeline_listitem_repost_text);
        repostText.setMovementMethod(LinkMovementMethod.getInstance());
        commentCount = (TextView) findViewById(R.id.timeline_listitem_commentcount);
        repostCount = (TextView) findViewById(R.id.timeline_listitem_repostcount);

        pic.setOnClickListener(this);
        commentCount.setOnClickListener(this);
        repostCount.setOnClickListener(this);
    }

    public void parse(TimeLineModel data){
        this.data = data;
        if(data.hasPic){
            pic.setVisibility(VISIBLE);
            if(FuncNet.isWifi()){
                ImageLoader.getInstance().displayImage(data.picUrlMiddle, pic);
            }else{
                ImageLoader.getInstance().displayImage(data.picUrl, pic);
            }
        }else{
            pic.setVisibility(GONE);
        }
        if(data.topics.size() == 0){
            topic.setVisibility(INVISIBLE);
        }else{
            topic.setVisibility(VISIBLE);
            topic.setText(data.topics.get(0));
        }
        if(data.hasRepost){
            if(data.hasRepostPic){
                pic.setVisibility(VISIBLE);
                if(FuncNet.isWifi()){
                    ImageLoader.getInstance().displayImage(data.repostPicUrlMiddle, pic);
                }else{
                    ImageLoader.getInstance().displayImage(data.repostPicUrl, pic);
                }
            }else{
                pic.setVisibility(GONE);
            }
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.timeline_listitem_pic:
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("data", data);
                if(data.hasRepostPic){
                    intent.putExtra("viewType", DetailActivity.VIEW_TYPE_REPOST_PIC);
                }else{
                    intent.putExtra("viewType", DetailActivity.VIEW_TYPE_PIC);
                }
                getContext().startActivity(intent);
                break;
        }
    }
}
