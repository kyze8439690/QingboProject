package com.yugy.qingbo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yugy.qingbo.R;

/**
 * Created by yugy on 13-9-15.
 */
public class AccountItem extends RelativeLayout {
    public AccountItem(Context context) {
        super(context);
        init();
    }

    public AccountItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AccountItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private ImageView headIcon;
    private TextView name;

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.widget_account_item, this);
        headIcon = (ImageView) findViewById(R.id.account_item_head);
        name = (TextView) findViewById(R.id.account_item_name);
    }

    public ImageView getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(ImageView headIcon) {
        this.headIcon = headIcon;
    }

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }
}
