package com.yugy.qingbo.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;

import com.yugy.qingbo.func.FuncStr;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by yugy on 13-10-4.
 */
public class TimeLineModel implements Parcelable{
    public SpannableString text;
    public String name;
    public String headUrl;
    public ArrayList<String> topics = new ArrayList<String>();
    public String time;
    public int commentCount;
    public int repostCount;
    public boolean hasPic;
    public String picUrl = "";
    public String picUrlMiddle = "";
    public String picUrlHD = "";
    public boolean hasRepost;
    public SpannableString repostName = new SpannableString("");
    public SpannableString repostText = new SpannableString("");
    public boolean hasRepostPic;
    public String repostPicUrl = "";
    public String repostPicUrlMiddle = "";
    public String repostPicUrlHD = "";

    private String unParseTime;

    public void parse(JSONObject json) throws JSONException, ParseException {
        text = FuncStr.parseStatusText(json.getString("text"));
        name = json.getJSONObject("user").getString("screen_name");
        headUrl = json.getJSONObject("user").getString("avatar_large");
//        headUrl = json.getJSONObject("user").getString("profile_image_url");
        topics.addAll(FuncStr.getTopic(json.getString("text")));
        unParseTime = json.getString("created_at");
        time = FuncStr.parseTime(unParseTime);
        commentCount = json.getInt("comments_count");
        repostCount = json.getInt("reposts_count");
        if (hasPic = json.has("thumbnail_pic")) {
            picUrl = json.getString("thumbnail_pic");
            picUrlMiddle = json.getString("bmiddle_pic");
            picUrlHD = json.getString("original_pic");
        }
        if (hasRepost = json.has("retweeted_status")) {
            JSONObject repostJson = json.getJSONObject("retweeted_status");
            repostName = FuncStr.parseStatusText("此微博最初是由@" + repostJson.getJSONObject("user").getString("screen_name") + " 分享的");
            repostText = FuncStr.parseStatusText(repostJson.getString("text"));
            topics.addAll(FuncStr.getTopic(repostJson.getString("text")));
            if (hasRepostPic = repostJson.has("thumbnail_pic")) {
                repostPicUrl = repostJson.getString("thumbnail_pic");
                repostPicUrlMiddle = repostJson.getString("bmiddle_pic");
                repostPicUrlHD = repostJson.getString("original_pic");
            }
        }
    }

    public void reParseTime() throws ParseException {
        time = FuncStr.parseTime(unParseTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(new int[]{
            commentCount,
            repostCount
        });
        dest.writeBooleanArray(new boolean[]{
                hasPic,
                hasRepost,
                hasRepostPic
        });
        dest.writeStringArray(new String[]{
                text.toString(),
                name,
                headUrl,
                unParseTime,
                picUrl,
                picUrlMiddle,
                picUrlHD,
                repostName.toString(),
                repostText.toString(),
                repostPicUrl,
                repostPicUrlMiddle,
                repostPicUrlHD
        });
        dest.writeStringList(topics);
    }

    public TimeLineModel(){}

    private TimeLineModel(Parcel in) throws ParseException {
        int[] intData = new int[2];
        in.readIntArray(intData);
        this.commentCount = intData[0];
        this.repostCount = intData[1];

        boolean[] booleanData = new boolean[3];
        in.readBooleanArray(booleanData);
        this.hasPic = booleanData[0];
        this.hasRepost = booleanData[1];
        this.hasRepostPic = booleanData[2];

        String[] stringData = new String[12];
        in.readStringArray(stringData);
        this.text = FuncStr.parseStatusText(stringData[0]);
        this.name = stringData[1];
        this.headUrl = stringData[2];
        this.unParseTime = stringData[3];
        this.time = FuncStr.parseTime(this.unParseTime);
        this.picUrl = stringData[4];
        this.picUrlMiddle = stringData[5];
        this.picUrlHD = stringData[6];
        this.repostName = FuncStr.parseStatusText(stringData[7]);
        this.repostText = FuncStr.parseStatusText(stringData[8]);
        this.repostPicUrl = stringData[9];
        this.repostPicUrlMiddle = stringData[10];
        this.repostPicUrlHD = stringData[11];

        this.topics = new ArrayList<String>();
        in.readStringList(this.topics);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            try {
                return new TimeLineModel(source);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public Object[] newArray(int size) {
            return new TimeLineModel[size];
        }
    };
}
