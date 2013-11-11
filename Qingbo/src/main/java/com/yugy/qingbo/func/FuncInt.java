package com.yugy.qingbo.func;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by yugy on 13-10-4.
 */
public class FuncInt {

    private static Resources resources;

    public static void setContext(Context context){
        resources = context.getResources();
    }

    public static int dp(float dp){
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return px;
    }

    public static double px(int px){
        float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, resources.getDisplayMetrics());
        return dp;
    }

}
