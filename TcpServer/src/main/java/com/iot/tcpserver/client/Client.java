package com.iot.tcpserver.client;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by zc on 16-8-19.
 */
public class Client {

    private String id;
    private String version;

    public Client(String id, String version) {
        this.id = id;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
