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
 * Created by hasee on 2016/1/27.
 */
public class AdapterForContacts extends BaseAdapter {

    private List<User> contacts;
    private Context context;

    public AdapterForContacts(List<User> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public User getItem(int i) {
        return contacts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MyViewHolder myViewHolder = null;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.contact_ceil,null);
            myViewHolder = new MyViewHolder(view);
            view.setTag(myViewHolder);
        }
        myViewHolder = (MyViewHolder) view.getTag();
        myViewHolder.tvRename.setText(contacts.get(i).getNickName());
        myViewHolder.ivContactIcon.setImageBitmap(BitmapFactory.decodeFile(contacts.get(i).getHeadImgPath()));
        return view;
    }

    class MyViewHolder{
        private ImageView ivContactIcon;
        private TextView tvRename;
        MyViewHolder(View view){
            ivContactIcon = (ImageView) view.findViewById(R.id.iv_contact_page_icon);
            tvRename = (TextView) view.findViewById(R.id.tv_contact_page_rename);
        }
    }
}
