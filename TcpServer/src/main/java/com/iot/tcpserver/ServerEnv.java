package com.iot.tcpserver;

import io.netty.util.AttributeKey;

import java.security.PrivateKey;

//runtime environments
public class ServerEnv {

    public static final AttributeKey<byte[]> KEY = AttributeKey.newInstance("key");

    public static String PUBLIC_KEY;
    public static PrivateKey PRIVATE_KEY;
}
