package com.yugy.qingbo.sql;

/**
 * Created by yugy on 13-9-10.
 */
public class Account {

    private long id;
    private String accessToken;
    private String expiresIn;
    private String userId;
    private boolean using;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isUsing() {
        return using;
    }

    public int getUsingNum(){
        if(using){
            return 1;
        }else{
            return 0;
        }
    }

    public void setUsing(boolean using) {
        this.using = using;
    }
}
