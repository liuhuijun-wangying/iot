package com.iot.common.constant;

/**
 * Created by zc on 16-8-19.
 */
public class RespCode {

    //COMMON
    public static final int COMMON_OK = 100;
    public static final int COMMON_EXCEPTION = 200;
    public static final int COMMON_NOT_LOGIN = 300;

    //dispatcher
    public static final int DISPATCHER_NO_SERVER = 1000;

    //account
    public static final int REG_USER_EXISTS = 1001;//注册，用户已存在
    public static final int LOGIN_WRONG_ACCOUNT = 1002;//登录，用户名或密码错误

    //im
    public static final int ADD_FRIEND_ALREADY = 2001;//添加好友,已经是好友
    public static final int DEL_FRIEND_NOT_EXISTS = 2002;//删除好友，还不是好友
}
