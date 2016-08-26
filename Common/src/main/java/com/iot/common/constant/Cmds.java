package com.iot.common.constant;

/**
 * Created by zc on 16-8-5.
 */
public class Cmds {

    //cmd

    /* 0-100  基础服务 */
    public static final short CMD_HEARTBEAT = 0;
    public static final short CMD_PUSH_RSA_PUB_KEY = 1;
    public static final short CMD_SEND_AES_KEY = 2;
    /* 100-200  account相关 */
    public static final short CMD_APP_AUTH = 100;
    public static final short CMD_DEVICE_AUTH = 101;
    public static final short CMD_APP_REGISTER = 102;

    /* 200-300  im相关 */
    public static final short CMD_ADD_FRIEND = 200;
}
