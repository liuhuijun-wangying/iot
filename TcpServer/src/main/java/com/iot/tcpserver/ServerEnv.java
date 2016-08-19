package com.iot.tcpserver;

import com.iot.tcpserver.client.Client;
import io.netty.util.AttributeKey;

import java.security.PrivateKey;

//runtime environments
public class ServerEnv {

    public static final AttributeKey<byte[]> KEY = AttributeKey.newInstance("clientKey");
    public static final AttributeKey<Client> CLIENT = AttributeKey.newInstance("client");

    public static String PUBLIC_KEY;
    public static PrivateKey PRIVATE_KEY;
}
