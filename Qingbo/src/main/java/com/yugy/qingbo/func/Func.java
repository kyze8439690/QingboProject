package com.yugy.qingbo.func;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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

}
