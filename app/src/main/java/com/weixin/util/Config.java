package com.weixin.util;

/**
 * Created by hasee on 2016/1/25.
 */
public class Config {

    //requestCode
    public final static int IMAGE_REQUEST_CODE_CHANGE_SELF_ICON = 2014;


    //resultCode
    public final static Integer INSERT_SUCCESS = 2000;
    public final static Integer INSERT_FAIL = 2001;
    public final static Integer NETWORK_WRONG = 2002;

    public static Integer USER_NOT_EXSIT = 2004;
    public static Integer LOGIN_SUCCESS = 2005;
    public static Integer LOGIN_FAIL_PASSWORD_ERROR = 2006;
    public static Integer USERNAME_USED = 2003;

    public static Integer NOT_CONTACTS = 3000;
    public static Integer GET_CONTACTS_SUCCESS = 3001;
    public static Integer SEND_ADD_REQUST_SUCCESS = 3002;
    public static Integer SEND_ADD_REQUEST_FAIL = 3003;

    public static Integer SUCCESS = 6666;
    public static Integer FAIL = 4444;

    public final static String HEADER = "http://192.168.0.110:8080/yomserver";

    public final static String REGIST_USER_URL = HEADER + "/user/RegistServlet";
    public final static String LOGIN_USER_URL = HEADER + "/login/LoginServlet";
    public final static String GET_CONNECT_CONTACTS_URL = HEADER + "/contacts/GetContactsServlet";
    public final static String UPDATE_USER_DATA_URL = HEADER + "/user/UpdateUserServlet";
    public final static String SEARCH_USER_URL = HEADER + "/search/SearchAddFriendServlet";
    public final static String GET_USER_DETAIL_URL = HEADER+"/user/GetUserServlet";
    public final static String SEND_ADD_FRIEND_URL = HEADER+"/user/AddFriendServlet";
    public final static String GET_ALL_REQUEST_URL = HEADER+"/contact/GetAllRequest";



}
