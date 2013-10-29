package com.yugy.qingbo.widget;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yugy.qingbo.R;
import com.yugy.qingbo.model.TimeLineModel;

/**
 * Created by yugy on 13-10-4.
 */
public class TimeLineListItem extends RelativeLayout implements View.OnClickListener{
    public TimeLineListItem(Context context) {
        super(context);
        init();
    }

    private RoundedImageView head;
    private TextView name;
    private TextView text;
    private TextView topic;
    private TextView time;
    private ImageView pic;
    private View repostDivider;
    private TextView repostName;
    private TextView repostText;
    private TextView commentCount;
    private TextView repostCount;

    private void init(){
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.widget_timeline_listitem, this);
        head = (RoundedImageView) findViewById(R.id.timeline_listitem_head);
        name = (TextView) findViewById(R.id.timeline_listitem_name);
        text = (TextView) findViewById(R.id.timeline_listitem_text);
        text.setMovementMethod(LinkMovementMethod.getInstance());
        topic = (TextView) findViewById(R.id.timeline_listitem_topic);
        time = (TextView) findViewById(R.id.timeline_listitem_time);
        pic = (ImageView) findViewById(R.id.timeline_listitem_pic);
        repostDivider = findViewById(R.id.timeline_listitem_repost_divider);
        repostName = (TextView) findViewById(R.id.timeline_listitem_repost_name);
        repostName.setMovementMethod(LinkMovementMethod.getInstance());
        repostText = (TextView) findViewById(R.id.timeline_listitem_repost_text);
        repostText.setMovementMethod(LinkMovementMethod.getInstance());
        commentCount = (TextView) findViewById(R.id.timeline_listitem_commentcount);
        repostCount = (TextView) findViewById(R.id.timeline_listitem_repostcount);

        commentCount.setOnClickListener(this);
        repostCount.setOnClickListener(this);
    }

    public void parse(TimeLineModel data){
        if(data.hasPic){
            pic.setVisibility(VISIBLE);
            ImageLoader.getInstance().displayImage(data.picUrl, pic);
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
                ImageLoader.getInstance().displayImage(data.repostPicUrl, pic);
            }else{
                pic.setVisibility(GONE);
            }
            repostDivider.setVisibility(VISIBLE);
            repostName.setVisibility(VISIBLE);
            repostText.setVisibility(VISIBLE);
            repostName.setText(data.repostName);
            repostText.setText(data.repostText);
        }else{
            repostDivider.setVisibility(GONE);
            repostName.setVisibility(GONE);
            repostText.setVisibility(GONE);
        }
        if(data.commentCount != 0){
            commentCount.setText(data.commentCount + "");
        }else{
            commentCount.setText("");
        }
        if(data.repostCount != 0){
            repostCount.setText(data.repostCount + "");
        }else{
            repostCount.setText("");
        }

        text.setText(data.text);
        name.setText(data.name);
        time.setText(data.time);
        ImageLoader.getInstance().displayImage(data.headUrl, head);
    }

    @Override
    public void onClick(View v) {

    }
}
