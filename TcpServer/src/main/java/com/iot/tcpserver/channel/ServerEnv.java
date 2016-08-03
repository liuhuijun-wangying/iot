package com.iot.tcpserver.channel;

import io.netty.util.AttributeKey;

import java.security.PrivateKey;

//runtime environments
public class ServerEnv {

    //cmd
    public static final short CMD_HEARTBEAT = 0X00;
    public static final short CMD_PUSH_RSA_PUB_KEY = 0X01;
    public static final short CMD_SEND_AES_KEY = 0X02;

    public static final AttributeKey<byte[]> KEY = AttributeKey.newInstance("key");

    public static String PUBLIC_KEY;
    public static PrivateKey PRIVATE_KEY;
    public static int SERVER_PORT;
}
