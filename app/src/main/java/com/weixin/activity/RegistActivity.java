package com.weixin.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by hasee on 2016/1/25.
 */
public class RegistActivity extends Activity implements View.OnClickListener {

    private EditText etUsername;
    private EditText etPassword;
    private EditText etNickName;
    private EditText etEmail;
    private ImageView ivSelfIcon;
    private Button btnLogin;
    private Button btnRegist;
    private String username, password, nickName, email, iconPath;
    private MyHandler myHandler = new MyHandler();

    /* 请求码 */
    private Bitmap head;//头像Bitmap
    private static String path = "/sdcard/myHead/";//sd路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_page);
        initView();
    }

    private void initView() {
        ivSelfIcon = (ImageView) findViewById(R.id.iv_self_icon_regist);
        etEmail = (EditText) findViewById(R.id.et_regist_page_email);
        etNickName = (EditText) findViewById(R.id.et_regist_page_nickname);
        etUsername = (EditText) findViewById(R.id.et_regist_page_username);
        etPassword = (EditText) findViewById(R.id.et_regist_page_password);
        btnLogin = (Button) findViewById(R.id.btn_regist_page_login);
        btnRegist = (Button) findViewById(R.id.btn_regist_page_regist);
        btnLogin.setOnClickListener(this);
        btnRegist.setOnClickListener(this);
        ivSelfIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_regist_page_login:
                startActivity(new Intent(RegistActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.btn_regist_page_regist:
                username = etUsername.getText().toString();
                password = etPassword.getText().toString();
                nickName = etNickName.getText().toString();
                email = etEmail.getText().toString();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)
                        || TextUtils.isEmpty(iconPath) || TextUtils.isEmpty(nickName)
                        || TextUtils.isEmpty(email)){
                    Toast.makeText(RegistActivity.this,"请检查注册信息的完整",Toast.LENGTH_LONG).show();
                }else{
                    RequestBody formBody = new FormEncodingBuilder()
                            .add("regist_username", username)
                            .add("regist_password", password)
                            .add("regist_email", email)
                            .add("regist_nickname", nickName)
                            .add("regist_head_img", iconPath).build();
                    Request request = new Request.Builder().url(Config.REGIST_USER_URL).post(formBody).build();
                    Callback callback = new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            Message msg = Message.obtain();
                            msg.what = Config.NETWORK_WRONG;
                            myHandler.sendMessage(msg);
                            Toast.makeText(RegistActivity.this, "网络错误，注册失败！", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            String str = response.body().string();
                            Log.e("======regist result:", str);
                            User user;
                            int resultCode = JsonResolver.getResultCode(str);
                            Message msg = Message.obtain();
                            msg.what = resultCode;
                            if (resultCode == Config.INSERT_SUCCESS){
                                user = JsonResolver.getUser(str);
                                msg.obj = user;
                            }
                            myHandler.sendMessage(msg);
                        }
                    };
                    MyApplication.application.getHttpHelper().enqueue(request, callback);
                }



                break;
            case R.id.iv_self_icon_regist:
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, Config.IMAGE_REQUEST_CODE_CHANGE_SELF_ICON);
                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Config.IMAGE_REQUEST_CODE_CHANGE_SELF_ICON:
                if (resultCode == RESULT_OK) {
                    cropPhoto(data.getData());//裁剪图片
                }
                break;
            case 3:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    head = extras.getParcelable("data");
                    if (head != null) {
                        /**
                         * 上传服务器代码
                         */
                        setPicToView(head);//保存在SD卡中
                        ivSelfIcon.setImageBitmap(head);//用ImageView显示出来
                    }
                }
                break;
            default:
                break;

        }
    }

    /**
     * 调用系统的裁剪
     *
     * @param uri
     */
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    private void setPicToView(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        FileOutputStream b = null;
        File file = new File(path);
        file.mkdirs();// 创建文件夹
        String fileName = path + getTime() + ".jpg";//图片名字
        iconPath = fileName;
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String str = format.format(date);
        return str;
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                //请求返回成功
                case 2000:
                    User user = (User) msg.obj;
                    Toast.makeText(RegistActivity.this, "恭喜" + user.getUsername() + "注册成功", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegistActivity.this,LoginActivity.class));
                    RegistActivity.this.finish();
                    break;
                //请求返回失败
                case 2001:
                    Toast.makeText(RegistActivity.this, "服务器奔溃，注册失败", Toast.LENGTH_LONG).show();
                    break;

                case 2002:
                    Toast.makeText(RegistActivity.this, "网络连接失败", Toast.LENGTH_LONG).show();
                    break;
                case 2003:
                    Toast.makeText(RegistActivity.this, "账号已存在", Toast.LENGTH_LONG).show();
                    break;
            }


            super.handleMessage(msg);
        }
    }

}