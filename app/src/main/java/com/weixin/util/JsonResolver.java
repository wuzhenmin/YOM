package com.weixin.util;

import android.util.Log;

import com.weixin.bean.FriendRequest;
import com.weixin.bean.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2016/1/25.
 */
public class JsonResolver {

    public static User getUser(String json) {
        Log.e("json:", json);
        User user = null;
        try {
            JSONObject object = new JSONObject(json);
            int resultCode = object.getInt("resultCode");
            if (resultCode == Config.INSERT_SUCCESS || resultCode == Config.LOGIN_SUCCESS) {
                JSONObject item = object.getJSONObject("user");
                user = new User();
                user.setId(item.getInt("userId"));
                user.setConnectId(item.getInt("connectId"));
                user.setUsername(item.getString("username"));
                user.setNickName(item.getString("nickName"));
                user.setEmail(item.getString("email"));
                user.setHeadImgPath(item.getString("headImgPath"));
                return user;
            }
            if (resultCode == 1){
                JSONObject item = object.getJSONObject("user");
                user = new User();
                user.setId(item.getInt("userId"));
                user.setUsername(item.getString("username"));
                user.setEmail(item.getString("email"));
                user.setNickName(item.getString("nickName"));
                user.setHeadImgPath(item.getString("headImgPath"));
                return user;
            }

            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static int getResultCode(String json) {
        int resultCode = 0;
        try {
            JSONObject object = new JSONObject(json);
            resultCode = object.getInt("resultCode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultCode;
    }

    public static List<User> getConnectContacts(String json) {
        List<User> contacts = new ArrayList<User>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("users");
            if (jsonArray.length()<1){
                return null;
            }else{
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    User user = new User();
                    user.setConnectId(item.getInt("connectId"));
                    user.setNickName(item.getString("nickName"));
                    user.setId(item.getInt("userId"));
                    user.setUsername(item.getString("username"));
                    user.setHeadImgPath(item.getString("headImgPath"));
                    user.setState(item.getInt("state"));
                    contacts.add(user);
                }
                return contacts;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    public static List<User> getSearchResult(String json) {
        List<User> searchResult = new ArrayList<User>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("searchResult");
            Log.e("jsonarray_in_search===>",jsonArray.toString());
            if (jsonArray.length()<1){
                return null;
            }else{
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    User user = new User();
                    user.setConnectId(item.getInt("connectId"));
                    user.setNickName(item.getString("nickName"));
                    user.setId(item.getInt("userId"));
                    user.setUsername(item.getString("username"));
                    user.setHeadImgPath(item.getString("headImgPath"));
                    searchResult.add(user);
                }
                return searchResult;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return searchResult;
    }

    public static List<FriendRequest> getAllRequstFriends(String json) {
        List<FriendRequest> allRequestFriends = new ArrayList<FriendRequest>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("allRequestFriends");
            Log.e("jsonarray_in_search===>",jsonArray.toString());
            if (jsonArray.length()<1){
                return null;
            }else{
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    FriendRequest friendRequest = new FriendRequest();
                    friendRequest.setFriendId(item.getInt("friendId"));
                    friendRequest.setUserId(item.getInt("userId"));
                    friendRequest.setSendName(item.getString("sendName"));
                    friendRequest.setReceiveName(item.getString("receiveName"));
                    friendRequest.setRequestState(item.getInt("requestState"));
                    friendRequest.setSenderImgUrl(item.getString("senderImgUrl"));
                    friendRequest.setReceiverImgUrl(item.getString("receiverImgUrl"));
                    allRequestFriends.add(friendRequest);
                }
                return allRequestFriends;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allRequestFriends;
    }


}
