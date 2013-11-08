package com.yugy.qingbo.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by yugy on 13-11-7.
 */
public class SelectorImageView extends ImageView{
    public SelectorImageView(Context context) {
        super(context);
    }

    public SelectorImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectorImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN && isEnabled()){
            setColorFilter(Color.parseColor("#b8e6ff"), PorterDuff.Mode.MULTIPLY);
        }else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
            setColorFilter(null);
        }
        return super.onTouchEvent(event);
    }
}
