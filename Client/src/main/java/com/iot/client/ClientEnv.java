package com.iot.client;

import com.iot.common.util.CryptUtil;

public class ClientEnv {

    public static final boolean DEBUG = true;

    public static final String CLIENT_ID = "1234567890";
    public static final String DISPATCHER_ADDR = "http://127.0.0.1:9999/?id=";

    public static byte[] AES_KEY = CryptUtil.generateAESKey();

    /** idle seconds **/
    public static final int IDLE_TIME = 5;
}
