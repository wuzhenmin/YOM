package com.weixin.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.weixin.activity.AllResquestActivity;
import com.weixin.adapter.AdapterForContacts;
import com.weixin.bean.User;
import com.weixin.util.Config;
import com.weixin.util.JsonResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {

    private ListView lvContacts;
    private AdapterForContacts adapterForContacts;
    private List<User> contacts;
    private Button btnNewFriend;
    private TextView tvNotContacts;
    private MyHandler myHandler = new MyHandler();
    private User loginUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_page, container, false);
        btnNewFriend = (Button) view.findViewById(R.id.btn_contact_page_new_friend);
        lvContacts = (ListView) view.findViewById(R.id.lv_contacts);
        tvNotContacts = (TextView) view.findViewById(R.id.tv_not_contact);
        initData();
        btnNewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AllResquestActivity.class));
            }
        });
        return view;
    }


    private void initData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("YOM",Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", 0);
        String username = sharedPreferences.getString("username", null);
        String nickname = sharedPreferences.getString("nickname",null);
        loginUser = new User();
        loginUser.setId(userId);
        loginUser.setUsername(username);
        loginUser.setNickName(nickname);
        contacts = new ArrayList<User>();
        RequestBody formBody = new FormEncodingBuilder()
                .add("userId", loginUser.getId() + "")
                .build();
        Request request = new Request.Builder().url(Config.GET_CONNECT_CONTACTS_URL).post(formBody).build();
        Callback callback = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                myHandler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String str = response.body().string();
                int resultCode = JsonResolver.getResultCode(str);
                Log.e("contacts_resultCode===>", resultCode + "");
                Message msg = Message.obtain();
                msg.what = resultCode;
                if (resultCode == Config.GET_CONTACTS_SUCCESS) {
                    contacts = JsonResolver.getConnectContacts(str);
                    Log.e("requestUsers===========",contacts.toString());
                }
                myHandler.sendMessage(msg);
            }
        };
        MyApplication.application.getHttpHelper().enqueue(request, callback);
        adapterForContacts = new AdapterForContacts(contacts, getActivity());
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getActivity(), "网络连接异常", Toast.LENGTH_LONG).show();
                    tvNotContacts.setVisibility(View.VISIBLE);
                    lvContacts.setVisibility(View.GONE);
                    break;
                //没有联系人
                case 3000:
                    tvNotContacts.setVisibility(View.VISIBLE);
                    lvContacts.setVisibility(View.GONE);
                    break;
                case 3001:
                    //有联系人时的处理
                    lvContacts.setVisibility(View.VISIBLE);
                    tvNotContacts.setVisibility(View.GONE);
                    adapterForContacts = new AdapterForContacts(contacts,getActivity());
                    lvContacts.setAdapter(adapterForContacts);
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
