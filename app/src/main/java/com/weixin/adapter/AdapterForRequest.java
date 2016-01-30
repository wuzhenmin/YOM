package com.weixin.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.weixin.R;
import com.weixin.bean.FriendRequest;
import com.weixin.bean.User;

import java.util.List;

/**
 * Created by hasee on 2016/1/28.
 */
public class AdapterForRequest extends BaseAdapter {

    private Context context ;
    private List<FriendRequest> users;
    //设置接口
    private View.OnClickListener onAdd;

    public void setOnAdd(View.OnClickListener onAdd){
        this.onAdd = onAdd;
    }

    public AdapterForRequest(Context context,List<FriendRequest> users ) {
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public FriendRequest getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.friend_request_ceil,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) view.getTag();
        int requestState = users.get(i).getRequestState();
        if (requestState == 1){
            viewHolder.ivNewFriendIcon.setImageBitmap(BitmapFactory.decodeFile(users.get(i).getReceiverImgUrl()));
            viewHolder.btnState.setVisibility(View.GONE);
            viewHolder.tvDescr.setText("hello");
            viewHolder.tvState.setVisibility(View.VISIBLE);
            viewHolder.tvState.setText("等待");
            viewHolder.tvNewFriendNickname.setText(users.get(i).getReceiveName());
        }else if (requestState == 2){
            viewHolder.ivNewFriendIcon.setImageBitmap(BitmapFactory.decodeFile(users.get(i).getSenderImgUrl()));
            viewHolder.tvNewFriendNickname.setText(users.get(i).getSendName());
            viewHolder.tvDescr.setText("对方请求添加你为好友");
            viewHolder.tvState.setVisibility(View.GONE);
            viewHolder.btnState.setVisibility(View.VISIBLE);
            viewHolder.btnState.setOnClickListener(onAdd);
            viewHolder.btnState.setTag(i);
            viewHolder.btnState.setText("添加");
        }else if (requestState == 3){
            viewHolder.btnState.setVisibility(View.GONE);
            viewHolder.tvState.setVisibility(View.VISIBLE);
            viewHolder.tvState.setText("已通过");
        }
        return view;
    }

    class ViewHolder {
        ImageView ivNewFriendIcon;
        TextView tvNewFriendNickname;
        TextView tvDescr;
        Button btnState;
        TextView tvState;
        ViewHolder(View view){
            ivNewFriendIcon = (ImageView) view.findViewById(R.id.iv_contact_add_friend_request);
            tvNewFriendNickname = (TextView) view.findViewById(R.id.tv_add_friend_request_nickname);
            tvDescr = (TextView) view.findViewById(R.id.tv_add_friend_request_desc);
            btnState = (Button) view.findViewById(R.id.btn_new_friend_add);
            tvState = (TextView) view.findViewById(R.id.tv_new_friend_state);
        }

        public TextView getTvState (){
            return tvState;
        }

        public Button getBtnState(){
            return btnState;
        }
    }


}
