package com.yugy.qingbo.storage;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import com.yugy.qingbo.R;
import com.yugy.qingbo.func.Func;

/**
 * Created by yugy on 13-10-4.
 */
public class Anim {

    public static Animation pushFromBottomIn = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.0f){{
        setDuration(600);
    }};

}
