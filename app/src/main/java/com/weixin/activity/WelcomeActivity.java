package com.weixin.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.weixin.R;

/**
 * Created by hasee on 2016/1/25.
 */
public class WelcomeActivity extends Activity {

    private static final int TIME = 2000;
    private static final int GO_MAIN = 1000;
    private static final int GO_LOGIN = 1001;

    private Boolean hadLogin;
    private Boolean isFirstInstall;
    private MyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);
        init();
    }

    private void init() {
        SharedPreferences sp = getSharedPreferences("YOM",MODE_PRIVATE);
        isFirstInstall = sp.getBoolean("isFirstInstall",true);
        if (isFirstInstall){
            goGuidPages();
        }else{
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isFirstInstall", false);
            editor.commit();
            hadLogin = sp.getBoolean("hadLogin",false);
            if (hadLogin){
                handler.sendEmptyMessageDelayed(GO_MAIN, TIME);
            }else{
                handler.sendEmptyMessageDelayed(GO_LOGIN, TIME);
            }
        }
    }

    private void goGuidPages() {

    }

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GO_MAIN:
                    startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                    finish();
                    break;
                case GO_LOGIN:
                    startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
