package com.weixin.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.weixin.MyApplication;
import com.weixin.R;
import com.weixin.activity.LoginActivity;
import com.weixin.bean.User;
import com.weixin.util.Config;
import com.weixin.util.JsonResolver;
import com.weixin.util.Tools;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class SelfPageFragment extends Fragment implements View.OnClickListener {
    private ImageView ivSelfIcon;
    private File file;
    private TextView tvNickName;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView getTvNickNameDetail;
    private Button btnEditData;
    private Button btnLogOut;
    private LinearLayout selfItem;

    private EditText etUsername;
    private EditText etNickname;
    private EditText etEmail;
    private Button btnSaveEditData;
    private Button btnCancel;
    private AlertDialog dialog;
    private MyHandler handler = new MyHandler();


    /* 请求码 */
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    private String filename;
    private String nickname;
    private String username;
    private String email;
    private Integer userId;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.self_page, container, false);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        selfItem = (LinearLayout) view.findViewById(R.id.self_detail_item);
        ivSelfIcon = (ImageView) view.findViewById(R.id.iv_self_icon);
        tvNickName = (TextView) view.findViewById(R.id.tv_self_page_nickname);
        tvEmail = (TextView) view.findViewById(R.id.tv_self_page_email);
        tvUsername = (TextView) view.findViewById(R.id.tv_self_page_username);
        getTvNickNameDetail = (TextView) view.findViewById(R.id.tv_self_page_nickname_detail);
        btnEditData = (Button) view.findViewById(R.id.btn_self_page_edit_data);
        btnLogOut = (Button) view.findViewById(R.id.btn_self_page_log_out);
        selfItem.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
        btnEditData.setOnClickListener(this);
    }

    private void initData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("YOM", Context.MODE_PRIVATE);
        filename = sharedPreferences.getString("headImgPath", null);
        nickname = sharedPreferences.getString("nickname", null);
        username = sharedPreferences.getString("username", null);
        email = sharedPreferences.getString("email", null);
        userId = sharedPreferences.getInt("userId", 0);
        ivSelfIcon.setImageBitmap(BitmapFactory.decodeFile(filename));
        tvUsername.setText(username);
        tvNickName.setText(nickname);
        tvEmail.setText(email);
        getTvNickNameDetail.setText(nickname);
    }

    private String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String str = format.format(date);
        return str;
    }

    /**
     * 显示选择对话框
     */
    private void showDialog() {
        String[] items = new String[]{"选择本地图片", "拍照"};
        new AlertDialog.Builder(getActivity())
                .setTitle("设置头像")
                .setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intentFromGallery = new Intent();
                                intentFromGallery.setType("image/*"); // 设置文件类型
                                intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
                                break;
                            case 1:

                                Intent intentFromCapture = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);
                                filename = getTime() + ".jpg";
                                // 判断存储卡是否可以用，可用进行存储
                                if (Tools.hasSdcard()) {
                                    intentFromCapture.putExtra(
                                            MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(new File(Environment
                                                    .getExternalStorageDirectory(),
                                                    filename)));
                                }

                                startActivityForResult(intentFromCapture,
                                        CAMERA_REQUEST_CODE);
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != 0) {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    if (Tools.hasSdcard()) {
                        File tempFile = new File(
                                Environment.getExternalStorageDirectory()
                                        + "/" + filename);
                        startPhotoZoom(Uri.fromFile(tempFile));
                    } else {
                        Toast.makeText(getActivity(), "未找到存储卡，无法存储照片！",
                                Toast.LENGTH_LONG).show();
                    }

                    break;
                case RESULT_REQUEST_CODE:
                    if (data != null) {
                        getImageToView(data);
                    }
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }

    /**
     * 保存裁剪之后的图片数据
     */
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            ivSelfIcon.setImageDrawable(drawable);
        }
    }

    private void showEditDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_detail_dialog, null);
        etEmail = (EditText) view.findViewById(R.id.et_edit_dialog_email);
        etUsername = (EditText) view.findViewById(R.id.et_edit_dialog_username);
        etNickname = (EditText) view.findViewById(R.id.et_edit_dialog_nickname);
        btnSaveEditData = (Button) view.findViewById(R.id.btn_save_edit_data);
        btnCancel = (Button) view.findViewById(R.id.btn_edit_cancel);
        btnCancel.setOnClickListener(this);
        btnSaveEditData.setOnClickListener(this);
        etUsername.setText(username);
        etNickname.setText(nickname);
        etEmail.setText(email);
        dialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.self_detail_item:
                showDialog();
                break;
            case R.id.btn_self_page_log_out:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("确认退出吗？");
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("YOM", Context.MODE_PRIVATE);
                        sharedPreferences.edit().clear();
                        dialog.dismiss();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case R.id.btn_self_page_edit_data:
                showEditDialog();
                break;
            case R.id.btn_save_edit_data:
                username = etUsername.getText().toString();
                nickname = etNickname.getText().toString();
                email = etEmail.getText().toString();
                RequestBody formBody = new FormEncodingBuilder()
                        .add("edit_userId", userId + "")
                        .add("edit_username", username)
                        .add("edit_email", email)
                        .add("edit_nickname", nickname)
                        .build();
                final Request request = new Request.Builder().url(Config.UPDATE_USER_DATA_URL).post(formBody).build();
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
                            user = new User();
                            user = JsonResolver.getUser(str);
                            Log.e("update_success:",user.toString());
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("YOM", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", user.getUsername());
                            editor.putString("nickname", user.getNickName());
                            editor.putString("email", user.getEmail());
                            editor.commit();
                        }
                        handler.sendEmptyMessage(resultCode);
                    }
                };
                MyApplication.application.getHttpHelper().enqueue(request, callback);

                dialog.dismiss();
                break;

            case R.id.btn_edit_cancel:
                dialog.dismiss();
                initData();
                break;
        }

    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2002:
                    Toast.makeText(getActivity(), "网络连接异常", Toast.LENGTH_LONG).show();
                    break;
                case 0:
                    Toast.makeText(getActivity(), "修改失败，服务器奔溃", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_LONG).show();
                    initData();
                    break;
                case 2003:
                    Toast.makeText(getActivity(), "修改失败，账号已被使用", Toast.LENGTH_LONG).show();
                    break;
            }
            super.handleMessage(msg);
        }
    }


}
