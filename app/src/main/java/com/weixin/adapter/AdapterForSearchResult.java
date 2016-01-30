package com.weixin.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weixin.R;
import com.weixin.bean.User;

import java.util.List;

/**
 * Created by hasee on 2016/1/28.
 */
public class AdapterForSearchResult extends BaseAdapter {

    private List<User> searchResult;
    private Context context;


    public AdapterForSearchResult(Context context,List<User> users) {
        this.context = context;
        this.searchResult = users;
    }

    @Override
    public int getCount() {
        return searchResult.size();
    }

    @Override
    public User getItem(int i) {
        return searchResult.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.search_result_ceil,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) view.getTag();
        User user = new User();
        user = getItem(i);
        viewHolder.ivIcon.setImageBitmap(BitmapFactory.decodeFile(user.getHeadImgPath()));
        viewHolder.tvNickname.setText(user.getNickName());
        viewHolder.tvUsername.setText(user.getUsername());
        return view;
    }

    class ViewHolder{
        private ImageView ivIcon;
        private TextView tvNickname;
        private TextView tvUsername;

        public ViewHolder(View view){
            ivIcon = (ImageView) view.findViewById(R.id.iv_search_result_icon);
            tvNickname = (TextView) view.findViewById(R.id.tv_search_result_nickname);
            tvUsername = (TextView) view.findViewById(R.id.tv_search_result_username);
        }
    }

}
