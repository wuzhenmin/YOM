package com.weixin;

import android.app.Application;

import com.weixin.util.OkHttpHelper;

/**
 * Created by zhenmin on 2016/1/21.
 */
public class MyApplication extends Application {

    public static MyApplication application;

    private OkHttpHelper httpHelper = new OkHttpHelper();

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public OkHttpHelper getHttpHelper() {
        return httpHelper;
    }

    public void setHttpHelper(OkHttpHelper httpHelper) {
        this.httpHelper = httpHelper;
    }

}
