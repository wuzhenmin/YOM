package com.weixin.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.weixin.MyApplication;
import com.weixin.R;
import com.weixin.adapter.AdapterForRequest;
import com.weixin.bean.FriendRequest;
import com.weixin.bean.User;
import com.weixin.util.Config;
import com.weixin.util.JsonResolver;

import java.io.IOException;
import java.util.List;

/**
 * Created by hasee on 2016/1/28.
 */
public class AllResquestActivity extends ActionBarActivity {

    private MyHandler myHandler = new MyHandler();
    private List<FriendRequest> allRequestUsers;
    private ListView lvRequest;
    private TextView tvNotRequest;
    private User loginUser;
    private Button btnAdd;
    private TextView tvAdded;
    private AdapterForRequest adapterForRequest;
    int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_friend_request);
        initView();
        initData();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("新的朋友");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        lvRequest = (ListView) findViewById(R.id.lv_friend_request_dialog);
        tvNotRequest = (TextView) findViewById(R.id.tv_not_request);

    }

    private void initData() {
        SharedPreferences sharedPreferences = getSharedPreferences("YOM", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", 0);
        String username = sharedPreferences.getString("username", null);
        String nickname = sharedPreferences.getString("nickname", null);
        loginUser = new User();
        loginUser.setId(userId);
        loginUser.setUsername(username);
        loginUser.setNickName(nickname);
        RequestBody requestBody = new FormEncodingBuilder().add("userId", loginUser.getId() + "").build();
        Request request = new Request.Builder().url(Config.GET_ALL_REQUEST_URL).post(requestBody).build();
        Callback callback = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                myHandler.sendEmptyMessage(Config.NETWORK_WRONG);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String str = response.body().string();
                int resultCode = JsonResolver.getResultCode(str);
                if (resultCode == Config.SUCCESS) {
                    allRequestUsers = JsonResolver.getAllRequstFriends(str);
                    Log.e("ALL REQUEST===>", allRequestUsers.toString());
                }
                myHandler.sendEmptyMessage(resultCode);
            }
        };
        MyApplication.application.getHttpHelper().enqueue(request, callback);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 6666:
                    tvNotRequest.setVisibility(View.GONE);
                    adapterForRequest = new AdapterForRequest(AllResquestActivity.this, allRequestUsers);
                    adapterForRequest.setOnAdd(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Object tag = view.getTag();
                            position = (Integer) tag;
                            Log.e("estUsers.get(position)",allRequestUsers.get(position).toString());
                            Request request = new Request.Builder().url(Config.SEND_ADD_FRIEND_URL+"?sendUserId="+allRequestUsers.get(position).getUserId()).get().build();
                            com.squareup.okhttp.Callback callback = new com.squareup.okhttp.Callback() {
                                @Override
                                public void onFailure(Request request, IOException e) {
                                    myHandler.sendEmptyMessage(Config.NETWORK_WRONG);
                                }

                                @Override
                                public void onResponse(Response response) throws IOException {
                                    String str = response.body().string();
                                    int resultCode = JsonResolver.getResultCode(str);
                                    myHandler.sendEmptyMessage(resultCode);
                                }
                            };
                            MyApplication.application.getHttpHelper().enqueue(request,callback);
                        }
                    });
                    lvRequest.setAdapter(adapterForRequest);
                    lvRequest.setVisibility(View.VISIBLE);
                    break;
                case 4444:
                    tvNotRequest.setVisibility(View.VISIBLE);
                    lvRequest.setVisibility(View.GONE);
                    break;
                case 3002:
                    allRequestUsers.get(position).setRequestState(3);
                    adapterForRequest.notifyDataSetChanged();
                    Toast.makeText(AllResquestActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    }
}
