package com.yugy.qingbo.sdk;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yugy.qingbo.func.Func;
import com.yugy.qingbo.sql.Account;
import com.yugy.qingbo.sql.AccountsDataSource;
import com.yugy.qingbo.sql.UsersDataSource;
import com.yugy.qingbo.storage.Conf;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yugy on 13-9-9.
 */
public class Weibo {

    private static AsyncHttpClient mClient = new AsyncHttpClient();

    public static String getAuthUrl(){
        return WeiboApiUrl.OAUTH2_AUTHORIZE + "?client_id="+ Conf.WEIBO_APP_KEY
                +"&response_type=code&display=mobile&redirect_uri=" + Conf.WEIBO_CALLBACK_URL;
    }

    /**
     * 根据code获取accessToken，若获取成功则存入数据库，并在onSuccess(String)中返回userId
     * @param context
     * @param code
     * @param responseHandler
     */
    public static void getAccessToken(Context context, String code, final AsyncHttpResponseHandler responseHandler){
        final AccountsDataSource dataSource = new AccountsDataSource(context);
        RequestParams params = new RequestParams();
        params.put("client_id", Conf.WEIBO_APP_KEY);
        params.put("client_secret", Conf.WEIBO_APP_SECRET);
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("redirect_uri", Conf.WEIBO_CALLBACK_URL);
        mClient.post(context, WeiboApiUrl.OAUTH2_ACCESS_TOKEN, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONObject response) {
                Func.log("授权成功: " + response.toString());
                try {
                    dataSource.open();
                    dataSource.createAccount(response);
                    dataSource.close();
                    responseHandler.onSuccess(response.getString("uid"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    responseHandler.onFailure(e, "Json解析失败");
                }
                super.onSuccess(response);
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                Func.log("授权失败: " + errorResponse.toString());
                responseHandler.onFailure(e, "授权失败");
                super.onFailure(e, errorResponse);
            }
        });
    }

    public static void getUserInfo(Context context, String userId, final JsonHttpResponseHandler responseHandler){
        final UsersDataSource dataSource = new UsersDataSource(context);
        RequestParams params = getParamsWithAccessToken(context);
        params.put("uid", userId);
        mClient.get(context, WeiboApiUrl.USER_SHOW, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONObject response) {
                dataSource.open();
                try {
                    dataSource.createUser(response);
                    responseHandler.onSuccess(response);
                } catch (JSONException e) {
                    responseHandler.onFailure(e, "Json解析失败");
                    e.printStackTrace();
                }
                super.onSuccess(response);
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                Func.log("获取用户信息失败: " + errorResponse.toString());

                responseHandler.onFailure(e, "获取用户信息失败");
                super.onFailure(e, errorResponse);
            }
        });

    }

    public static void getNewTimeline(Context context, String firstStatusId, final JsonHttpResponseHandler responseHandler){
        RequestParams params = getParamsWithAccessToken(context);
        params.put("since_id", firstStatusId);
        mClient.get(context, WeiboApiUrl.STATUS_HOME_TIMELINE, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONObject response) {
                Func.log(response.toString());
                try {
                    responseHandler.onSuccess(response.getJSONArray("statuses"));
                } catch (JSONException e) {
                    responseHandler.onFailure(e, "获取用户信息失败");
                    e.printStackTrace();
                }
                super.onSuccess(response);
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                Func.log(errorResponse.toString());
                super.onFailure(e, errorResponse);
            }
        });
    }

    public static void getOldTimeline(Context context, String lastStatusId, final JsonHttpResponseHandler responseHandler){
        RequestParams params = getParamsWithAccessToken(context);
        params.put("max_id", lastStatusId);
        params.put("count", 21 + "");
        mClient.get(context, WeiboApiUrl.STATUS_HOME_TIMELINE, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONObject response) {
                Func.log(response.toString());
                try {
                    responseHandler.onSuccess(response.getJSONArray("statuses"));
                } catch (JSONException e) {
                    responseHandler.onFailure(e, "获取用户信息失败");
                    e.printStackTrace();
                }
                super.onSuccess(response);
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                Func.log(errorResponse.toString());
                super.onFailure(e, errorResponse);
            }
        });
    }

    private static String mAccessToken = null;

    private static RequestParams getParamsWithAccessToken(Context context){
        if(mAccessToken == null){
            AccountsDataSource dataSource = new AccountsDataSource(context);
            dataSource.open();
            Account account = dataSource.getUsingAccount();
            mAccessToken = account.getAccessToken();
        }
        RequestParams params = new RequestParams();
        params.put("access_token", mAccessToken);
        return params;
    }

    public static class WeiboApiUrl{
        public static final String STATUS_HOME_TIMELINE = "https://api.weibo.com/2/statuses/home_timeline.json";
        public static final String USER_SHOW = "https://api.weibo.com/2/users/show.json";
        public static final String OAUTH2_ACCESS_TOKEN = "https://api.weibo.com/oauth2/access_token";
        public static final String OAUTH2_AUTHORIZE = "https://api.weibo.com/oauth2/authorize";
    }
}
