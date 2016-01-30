package com.weixin.bean;

/**
 * Created by hasee on 2016/1/29.
 */
public class FriendRequest {

    private String sendName;
    private String receiveName;
    private int id;
    private int userId;
    private int friendId;
    private int requestState;
    private String senderImgUrl;
    private String receiverImgUrl;
    private int addVisible;

    public int getVisible() {
        return addVisible;
    }

    public void setVisible(int visible) {
        this.addVisible = visible;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public int getRequestState() {
        return requestState;
    }

    public void setRequestState(int requestState) {
        this.requestState = requestState;
    }

    public String getSenderImgUrl() {
        return senderImgUrl;
    }

    public void setSenderImgUrl(String senderImgUrl) {
        this.senderImgUrl = senderImgUrl;
    }

    public String getReceiverImgUrl() {
        return receiverImgUrl;
    }

    public void setReceiverImgUrl(String receiverImgUrl) {
        this.receiverImgUrl = receiverImgUrl;
    }

    @Override
    public String toString() {
        return "FriendRequst [sendName=" + sendName + ", receiveName="
                + receiveName + ", id=" + id + ", userId=" + userId
                + ", friendId=" + friendId + ", requestState=" + requestState
                + ", senderImgUrl=" + senderImgUrl + ", receiverImgUrl="
                + receiverImgUrl + "]";
    }
}
