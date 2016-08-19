package com.iot.tcpserver.client;

/**
 * Created by zc on 16-8-19.
 */
public class AppClient extends Client {

    private String username;

    public AppClient(String id, String version, String username) {
        super(id, version);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
