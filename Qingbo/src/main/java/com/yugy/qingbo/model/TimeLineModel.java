package com.yugy.qingbo.model;

import android.text.SpannableString;

import com.yugy.qingbo.func.FuncStr;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by yugy on 13-10-4.
 */
public class TimeLineModel {
    public SpannableString text;
    public String name;
    public String headUrl;
    public ArrayList<String> topics;
    public String time;
    public int commentCount;
    public int repostCount;
    public boolean hasPic;
    public String picUrl;
    public boolean hasRepost;
    public SpannableString repostName;
    public SpannableString repostText;
    public boolean hasRepostPic;
    public String repostPicUrl;

    private String unParseTime;

    public void parse(JSONObject json) throws JSONException, ParseException {
        text = FuncStr.parseStatusText(json.getString("text"));
        name = json.getJSONObject("user").getString("screen_name");
        headUrl = json.getJSONObject("user").getString("avatar_large");
//        headUrl = json.getJSONObject("user").getString("profile_image_url");
        topics = new ArrayList<String>();
        topics.addAll(FuncStr.getTopic(json.getString("text")));
        unParseTime = json.getString("created_at");
        time = FuncStr.parseTime(unParseTime);
        commentCount = json.getInt("comments_count");
        repostCount = json.getInt("reposts_count");
        if (json.has("thumbnail_pic")) {
            hasPic = true;
            picUrl = json.getString("thumbnail_pic");
        } else {
            hasPic = false;
        }
        if (json.has("retweeted_status")) {
            hasRepost = true;
            JSONObject repostJson = json.getJSONObject("retweeted_status");
            repostName = FuncStr.parseStatusText("此微博最初是由@" + repostJson.getJSONObject("user").getString("screen_name") + " 分享的");
            repostText = FuncStr.parseStatusText(repostJson.getString("text"));
            topics.addAll(FuncStr.getTopic(repostJson.getString("text")));
            if (repostJson.has("thumbnail_pic")) {
                hasRepostPic = true;
                repostPicUrl = repostJson.getString("thumbnail_pic");
            } else {
                hasRepostPic = false;
            }
        } else {
            hasRepost = false;
        }
    }

    public void reParseTime() throws ParseException {
        time = FuncStr.parseTime(unParseTime);
    }
}
