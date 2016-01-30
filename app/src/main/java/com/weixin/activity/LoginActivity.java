package com.weixin.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.weixin.MyApplication;
import com.weixin.R;
import com.weixin.bean.User;
import com.weixin.util.Config;
import com.weixin.util.JsonResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2016/1/25.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private String username;
    private String password;
    private String loginIconPath;
    private ImageView ivLoginIcon;
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegist;
    private User user;

    private MyHandler handler = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        initView();
    }

    private void initView() {
        etPassword = (EditText) findViewById(R.id.et_login_page_password);
        etUsername = (EditText) findViewById(R.id.et_login_page_username);
        ivLoginIcon = (ImageView) findViewById(R.id.iv_self_icon_login);
        btnLogin = (Button) findViewById(R.id.btn_login_page_login);
        btnRegist = (Button) findViewById(R.id.btn_login_page_regist);
        btnLogin.setOnClickListener(this);
        btnRegist.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_page_login:
                username = etUsername.getText().toString();
                password = etPassword.getText().toString();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "请完成登录信息", Toast.LENGTH_LONG).show();
                } else {
                    RequestBody formBody = new FormEncodingBuilder()
                            .add("login_username", username)
                            .add("login_password", password)
                            .build();
                    Request request = new Request.Builder().url(Config.LOGIN_USER_URL).post(formBody).build();
                    Callback callback = new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            Message msg = Message.obtain();
                            msg.what = Config.NETWORK_WRONG;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            String str = response.body().string();
                            Log.e("login_result====>", str);
                            user = new User();
                            Message msg = Message.obtain();
                            int resultCode = JsonResolver.getResultCode(str);
                            if (resultCode == Config.LOGIN_SUCCESS) {
                                user = JsonResolver.getUser(str);
                                Log.e("json_result_user====>", user.toString());
                            }
                            msg.what = resultCode;
                            handler.sendMessage(msg);
                        }
                    };
                    MyApplication.application.getHttpHelper().enqueue(request, callback);
                }
                break;
            case R.id.btn_login_page_regist:
                startActivity(new Intent(LoginActivity.this, RegistActivity.class));
                finish();
                break;
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //请求返回失败
                case 2001:
                    Toast.makeText(LoginActivity.this, "服务器奔溃，注册失败", Toast.LENGTH_LONG).show();
                    break;
                case 2002:
                    Toast.makeText(LoginActivity.this, "网络连接失败", Toast.LENGTH_LONG).show();
                    break;
                //用户不存在
                case 2004:
                    Toast.makeText(LoginActivity.this, "用户名不存在", Toast.LENGTH_LONG).show();
                    break;
                //登录成功
                case 2005:
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                    Log.e("login_success_user==>", user.toString());
                    SharedPreferences sp = getSharedPreferences("YOM", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("hadLogin", true);
                    editor.putString("username", user.getUsername());
                    editor.putInt("userId", user.getId());
                    editor.putInt("connectId", user.getConnectId());
                    editor.putString("nickname", user.getNickName());
                    editor.putString("headImgPath", user.getHeadImgPath());
                    editor.putString("email", user.getEmail());
                    editor.commit();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    break;
                //登录失败
                case 2006:
                    Toast.makeText(LoginActivity.this, "登录失败，密码错误", Toast.LENGTH_LONG).show();
                    break;
            }


            super.handleMessage(msg);
        }
    }

}
