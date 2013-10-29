package com.yugy.qingbo.func;

/**
 * Created by yugy on 13-10-4.
 */
public class FuncInt {

    private static float densityDpi = 0;

    public static int dp(double dp){
        if(densityDpi == 0){
            densityDpi = Func.getContext().getResources().getDisplayMetrics().densityDpi;
        }
        return (int) (dp * (densityDpi / 160));
    }

    public static double px(int px){
        if(densityDpi == 0){
            densityDpi = Func.getContext().getResources().getDisplayMetrics().densityDpi;
        }
        return px / (densityDpi / 160);
    }

}
