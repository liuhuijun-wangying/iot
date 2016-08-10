package com.iot.tcpserver;

import io.netty.util.AttributeKey;

import java.security.PrivateKey;

//runtime environments
public class ServerEnv {

    public static final AttributeKey<byte[]> KEY = AttributeKey.newInstance("clientKey");
    public static final AttributeKey<String> ID = AttributeKey.newInstance("clientId");
    public static final AttributeKey<String> VERSION = AttributeKey.newInstance("clientVersion");
    public static final AttributeKey<String> USERNAME = AttributeKey.newInstance("clientUsername");
    public static final AttributeKey<String> TYPE = AttributeKey.newInstance("clientType");
    public static final String CLIENT_TYPE_APP = "app";
    public static final String CLIENT_TYPE_DEVICE = "device";

    public static String PUBLIC_KEY;
    public static PrivateKey PRIVATE_KEY;
}
