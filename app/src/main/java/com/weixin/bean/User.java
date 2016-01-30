package com.weixin.bean;

/**
 * Created by hasee on 2016/1/25.
 */
public class User {
    private String username;
    private String password;
    private String email;
    private String nickName;
    private String headImgPath;
    private Integer id;
    private Integer connectId;
    private Integer state;

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getConnectId() {
        return connectId;
    }

    public void setConnectId(Integer connectId) {
        this.connectId = connectId;
    }

    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", nickName='" + nickName + '\'' +
                ", headImgPath='" + headImgPath + '\'' +
                ", id=" + id +
                ", connectId=" + connectId +
                ", state=" + state +
                '}';
    }

    public User(String username, String password, String email, String nickName, String headImgPath) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickName = nickName;
        this.headImgPath = headImgPath;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImgPath() {
        return headImgPath;
    }

    public void setHeadImgPath(String headImgPath) {
        this.headImgPath = headImgPath;
    }
}
