package com.yugy.qingbo.func;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yugy on 13-10-4.
 */
public class FuncStr {

    /*
    time sample: Fri Oct 04 18:20:31 +0800 2013
     */
    private static SimpleDateFormat decodeDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);

    public static SpannableString parseStatusText(String text){
        SpannableString parseString = new SpannableString(text);
        Pattern urlPattern = Pattern.compile("http://t.cn/.{7}");
        Pattern atPattern = Pattern.compile("@([0-9a-zA-Z\\u4e00-\\u9fa5_-]+)");
        Matcher urlMatcher = urlPattern.matcher(text);
        Matcher atMatcher = atPattern.matcher(text);
        while(urlMatcher.find()){
            String url = urlMatcher.group();
            int start = urlMatcher.start();
            int end = urlMatcher.end();
            parseString.setSpan(new URLSpan(url), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            parseString.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        while(atMatcher.find()){
            int start = atMatcher.start();
            int end = atMatcher.end();
            final String name = atMatcher.group();
            parseString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Func.toast(name);
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            parseString.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return parseString;
    }

    public static ArrayList<String> getTopic(String text){
        ArrayList<String> topics = new ArrayList<String>();
        Pattern topicPattern = Pattern.compile("#[^#]+#");
        Matcher topicMatcher = topicPattern.matcher(text);
        while(topicMatcher.find()){
            topics.add(topicMatcher.group());
        }
        return topics;
    }

    public static String parseTime(String time) throws ParseException {
        Date now = new Date();
        Date date = decodeDateFormat.parse(time);

        //检测年份
        int year;
        if((year = now.getYear() - date.getYear()) > 0){
            return year + "年前";
        }
        //检测月份
        int month;
        if((month = now.getMonth() - date.getMonth()) > 0){
            return month + "个月前";
        }
        //检查天数
        int day = now.getDay() - date.getDay();
        if(day > 0 && day < 7){
            return day + "天前";
        }else if(day >= 7){
            return day / 7 + "个星期前";
        }
        //检查小时
        int hour;
        if((hour = now.getHours() - date.getHours()) > 0){
            return hour + "小时前";
        }
        //检查分钟
        int minute;
        if((minute = now.getMinutes() - date.getMinutes()) > 0){
            return minute + "分钟前";
        }
        //检查秒数
        int second;
        if((second = now.getSeconds() - date.getSeconds()) > 0){
            return second + "秒前";
        }
        return date.getHours() + ":" + date.getMinutes();
    }

}
