package com.yugy.qingbo.func;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.yugy.qingbo.R;
import com.yugy.qingbo.storage.Conf;

/**
 * Created by yugy on 13-9-9.
 */
public class Func {

    private static Context context;

    public static void setContext(Context context){
        Func.context = context;
    }

    public static Context getContext(){
        return context;
    }

    public static void log(String str){
        if(Conf.DEBUG){
            Log.d(Conf.TAG, str);
        }
    }

    public static void toast(String str){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static void myToast(String str){
        Toast myToast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        myToast.setGravity(Gravity.TOP, 0, FuncInt.dp(80));
        TextView text = new TextView(context);
        text.setText(str);
        text.setTextColor(Color.WHITE);
        text.setBackgroundResource(R.drawable.bg_toast);
        int padding = FuncInt.dp(14);
        text.setPadding(padding, padding, padding, padding);
        myToast.setView(text);
        myToast.show();
    }

}
