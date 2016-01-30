package com.weixin.activity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.weixin.adapter.AdapterForSearchResult;
import com.weixin.bean.User;
import com.weixin.fragment.ContactsFragment;
import com.weixin.fragment.SelfPageFragment;
import com.weixin.fragment.SelfPageFragment3;
import com.weixin.fragment.SelfPageFragment4;
import com.weixin.util.ChangeColorIconWithText;
import com.weixin.util.Config;
import com.weixin.util.JsonResolver;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements OnClickListener,
        OnPageChangeListener, MenuItem.OnMenuItemClickListener, AdapterView.OnItemClickListener {

    private ViewPager mViewPager;
    private List<Fragment> mTabs = new ArrayList<Fragment>();
    private FragmentPagerAdapter mAdapter;

    private EditText etAddFriendSearch;
    private Button btnDoSearch;
    private ListView lvSearchResult;
    private AdapterForSearchResult adapter;
    private List<User> searchResult;
    private TextView tvNotResult;
    private AlertDialog alertDialog;
    private AlertDialog addFriendDetailDialog;
    private ImageView ivDetailIcon;
    private TextView tvNickNameBesideIv;
    private TextView tvUsernameDetail;
    private TextView tvNickNameDetail;
    private TextView tvEmailDetail;
    private Button btnAddFriend;
    private User userForDettailDialog;
    private User loginUser;
    private MyHandler handler = new MyHandler();

    private List<ChangeColorIconWithText> mTabIndicators = new ArrayList<ChangeColorIconWithText>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setOverflowButtonAlways();

        initView();
        initDatas();
        mViewPager.setAdapter(mAdapter);
        initEvent();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

        ChangeColorIconWithText one = (ChangeColorIconWithText) findViewById(R.id.id_indicator_one);
        mTabIndicators.add(one);
        ChangeColorIconWithText two = (ChangeColorIconWithText) findViewById(R.id.id_indicator_two);
        mTabIndicators.add(two);
        ChangeColorIconWithText three = (ChangeColorIconWithText) findViewById(R.id.id_indicator_three);
        mTabIndicators.add(three);
        ChangeColorIconWithText four = (ChangeColorIconWithText) findViewById(R.id.id_indicator_four);
        mTabIndicators.add(four);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);

        one.setIconAlpha(1.0f);
    }

    private void initDatas() {
        SharedPreferences sharedPreferences = getSharedPreferences("YOM",MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", 0);
        String username = sharedPreferences.getString("username", null);
        String nickname = sharedPreferences.getString("nickname",null);
        String loginImg = sharedPreferences.getString("headImgPath",null);
        loginUser = new User();
        loginUser.setId(userId);
        loginUser.setUsername(username);
        loginUser.setNickName(nickname);
        loginUser.setHeadImgPath(loginImg);

        SelfPageFragment4 tabFragment = new SelfPageFragment4();
        mTabs.add(tabFragment);
        ContactsFragment contactsFragment = new ContactsFragment();
        mTabs.add(contactsFragment);
        SelfPageFragment3 tabFragment3 = new SelfPageFragment3();
        mTabs.add(tabFragment3);
        SelfPageFragment selfPageFragment = new SelfPageFragment();
        mTabs.add(selfPageFragment);


        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }
        };
    }


    private void initEvent() {
        mViewPager.setOnPageChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem action_search = menu.findItem(R.id.action_search); //获取MenuItem的实例,注意,这里获取的不是SearchView的实例.
        MenuItem addFriend = menu.findItem(R.id.action_add_friend);
        addFriend.setOnMenuItemClickListener(this);
        action_search.setVisible(true); //设置Item是否可见,这里与View的设置不太一样,接受的是boolean值,只有两种状态

        return true;
    }

    /**
     * 利用反射，设置sHasPermanentMenuKey 为false
     * 作用是actionBar显示overflow menu
     */
    private void setOverflowButtonAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKey = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKey.setAccessible(true);
            menuKey.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置menu显示icon
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {

        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset > 0)//手指在移动使当前的页面更改，所以左边的page才是position的页面，右边的页面就是position加1的页面
        {
            ChangeColorIconWithText left = mTabIndicators.get(position);
            ChangeColorIconWithText right = mTabIndicators.get(position + 1);
            left.setIconAlpha(1 - positionOffset);//颜色动画渐变（positionOffset的变化而变化）
            right.setIconAlpha(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onClick(View v) {
        clickTab(v);
    }

    /**
     * 点击Tab按钮
     *
     * @param v
     */
    private void clickTab(View v) {
        resetOtherTabs();

        switch (v.getId()) {
            case R.id.id_indicator_one:
                mTabIndicators.get(0).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.id_indicator_two:
                mTabIndicators.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.id_indicator_three:
                mTabIndicators.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                break;
            case R.id.id_indicator_four:
                mTabIndicators.get(3).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(3, false);
                break;
        }
    }

    /**
     * 重置其他的TabIndicator的颜色
     */
    private void resetOtherTabs() {
        for (int i = 0; i < mTabIndicators.size(); i++) {
            mTabIndicators.get(i).setIconAlpha(0);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_add_friend:
                showSearchDialog();
                break;
        }

        return false;
    }

    //显示点击搜索结果后的用户具体信息对话框
    private void showDetailFriendDialog() {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.user_detail_dialog_for_add, null);
        ivDetailIcon = (ImageView) view.findViewById(R.id.iv_detail_dialog_self_icon);
        tvNickNameBesideIv = (TextView) view.findViewById(R.id.tv_detail_dialog_nickname);
        tvNickNameDetail = (TextView) view.findViewById(R.id.tv_detail_dialog_nickname_detail);
        tvUsernameDetail = (TextView) view.findViewById(R.id.tv_detail_dialog_username);
        tvEmailDetail = (TextView) view.findViewById(R.id.tv_detail_dialog_email);
        btnAddFriend = (Button) view.findViewById(R.id.btn_detail_dialog_add_friend);
        btnAddFriend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("userIdSend",loginUser.getId()+"")
                        .add("userIdReceive",userForDettailDialog.getId()+"")
                        .add("sendName",loginUser.getNickName())
                        .add("receiveName",userForDettailDialog.getNickName())
                        .add("senderImgUrl",loginUser.getHeadImgPath())
                        .add("receiverImgUrl",userForDettailDialog.getHeadImgPath())
                        .build();
                final Request request = new Request.Builder().url(Config.SEND_ADD_FRIEND_URL).post(requestBody).build();
                Callback callback = new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        handler.sendEmptyMessage(Config.NETWORK_WRONG);
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        String str = response.body().string();
                        int resultCode = JsonResolver.getResultCode(str);
                        handler.sendEmptyMessage(resultCode);
                    }
                };
                MyApplication.application.getHttpHelper().enqueue(request,callback);
            }
        });
        addFriendDetailDialog = new AlertDialog.Builder(MainActivity.this).create();
        addFriendDetailDialog.setView(view);
        Log.e("add_detail===>",userForDettailDialog.toString());
        ivDetailIcon.setImageBitmap(BitmapFactory.decodeFile(userForDettailDialog.getHeadImgPath()));
        tvNickNameBesideIv.setText(userForDettailDialog.getNickName());
        tvEmailDetail.setText(userForDettailDialog.getEmail());
        tvNickNameDetail.setText(userForDettailDialog.getNickName());
        tvUsernameDetail.setText(userForDettailDialog.getUsername());
        addFriendDetailDialog.show();
    }

    //显示添加好友的对话框
    private void showSearchDialog() {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_friend_dialog, null);
        etAddFriendSearch = (EditText) view.findViewById(R.id.et_add_friend_dialog_search);
        btnDoSearch = (Button) view.findViewById(R.id.btn_do_search_friend);
        lvSearchResult = (ListView) view.findViewById(R.id.lv_search_friend_result);
        tvNotResult = (TextView) view.findViewById(R.id.tv_search_not_result);
        alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setView(view);
        alertDialog.show();
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final String searchContent = etAddFriendSearch.getText().toString();
                btnDoSearch.setVisibility(View.VISIBLE);
                btnDoSearch.setText("搜索：" + searchContent);
                if (TextUtils.isEmpty(searchContent)) {
                    btnDoSearch.setVisibility(View.GONE);
                    lvSearchResult.setVisibility(View.GONE);
                    tvNotResult.setVisibility(View.GONE);
                }
                btnDoSearch.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences sharedPreferences = getSharedPreferences("YOM", MODE_PRIVATE);
                        int userId = sharedPreferences.getInt("userId", 0);
                        int connectId = sharedPreferences.getInt("connectId", 0);
                        RequestBody formBody = new FormEncodingBuilder()
                                .add("userId", userId + "")
                                .add("connectId", connectId + "")
                                .add("searchContent", searchContent).build();

                        Request request = new Request.Builder().url(Config.SEARCH_USER_URL).post(formBody).build();
                        Callback callback = new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                                handler.sendEmptyMessage(Config.NETWORK_WRONG);
                            }

                            @Override
                            public void onResponse(Response response) throws IOException {
                                String str = response.body().string();
                                int resultCode = JsonResolver.getResultCode(str);
                                Log.e("search_resultCode===>", resultCode + "");
                                searchResult = new ArrayList<User>();
                                if (resultCode == Config.GET_CONTACTS_SUCCESS) {
                                    searchResult = JsonResolver.getSearchResult(str);
                                    Log.e("searchResult==>", searchResult.toString());
                                }
                                handler.sendEmptyMessage(resultCode);
                            }
                        };
                        MyApplication.application.getHttpHelper().enqueue(request, callback);
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        etAddFriendSearch.addTextChangedListener(watcher);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        RequestBody formBody = new FormEncodingBuilder().add("username", searchResult.get(i).getUsername()).build();
        Request request = new Request.Builder().url(Config.GET_USER_DETAIL_URL).post(formBody).build();
        Callback callback = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                handler.sendEmptyMessage(Config.NETWORK_WRONG);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String str = response.body().string();
                int resultCode = JsonResolver.getResultCode(str);
                if (resultCode == 1) {
                    userForDettailDialog = new User();
                    userForDettailDialog = JsonResolver.getUser(str);
                }
                handler.sendEmptyMessage(resultCode);
            }
        };
        MyApplication.application.getHttpHelper().enqueue(request,callback);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(MainActivity.this, "获取失败，服务器奔溃", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    showDetailFriendDialog();
                    break;
                case 2002:
                    lvSearchResult.setVisibility(View.GONE);
                    tvNotResult.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
                    break;
                case 3000:
                    tvNotResult.setVisibility(View.VISIBLE);
                    btnDoSearch.setVisibility(View.GONE);
                    lvSearchResult.setVisibility(View.GONE);
                    break;
                case 3001:
                    adapter = new AdapterForSearchResult(MainActivity.this, searchResult);
                    lvSearchResult.setAdapter(adapter);
                    lvSearchResult.setVisibility(View.VISIBLE);
                    btnDoSearch.setVisibility(View.GONE);
                    tvNotResult.setVisibility(View.GONE);
                    lvSearchResult.setOnItemClickListener(MainActivity.this);
                    break;
                case 3002:
                    Toast.makeText(MainActivity.this, "请求已发送", Toast.LENGTH_LONG).show();
                    addFriendDetailDialog.dismiss();
                    alertDialog.dismiss();
                    break;
                case 3003:
                    Toast.makeText(MainActivity.this, "发送失败，服务器奔溃", Toast.LENGTH_LONG).show();
                    break;
            }
            super.handleMessage(msg);
        }
    }


}
