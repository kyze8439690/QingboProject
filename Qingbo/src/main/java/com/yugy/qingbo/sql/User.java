package com.yugy.qingbo.sql;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yugy on 13-9-15.
 */
public class User {

    private long id;
    private String userId;
    private String screenName;
    private String avatarUrl;
    private String json;

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public JSONObject getJson() throws JSONException {
        return new JSONObject(json);
    }

    public void setJson(String json) {
        this.json = json;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
