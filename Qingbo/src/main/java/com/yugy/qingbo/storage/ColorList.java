package com.yugy.qingbo.storage;

import android.graphics.Color;

/**
 * Created by yugy on 13-11-7.
 */
public class ColorList {

    private static final int[] colorList = new int[]{
            Color.parseColor("#5c3292"),
            Color.parseColor("#2cbdbb"),
            Color.parseColor("#ffa800"),
            Color.parseColor("#0dab23"),
            Color.parseColor("#2d8dd1"),
            Color.parseColor("#d403c3"),
            Color.parseColor("#ec4848"),
    };

    private static int index = 0;

    public static int getColor(){
        return colorList[index++ % 7];
    }

}
