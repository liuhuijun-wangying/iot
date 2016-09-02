package com.iot.client;

import com.iot.client.netty.AppHandler;
import com.iot.client.netty.NettyClient;

public class AppMain {

    public static void main(String[] args) throws Exception {
        NettyClient.start(new AppHandler());
    }

}
