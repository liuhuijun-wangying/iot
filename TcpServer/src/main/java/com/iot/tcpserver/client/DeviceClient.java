package com.iot.tcpserver.client;

import java.util.List;

/**
 * Created by zc on 16-8-19.
 */
public class DeviceClient extends Client {

    private List<String> abilities;

    public DeviceClient(String id, String version, List<String> abilities) {
        super(id, version);
        this.abilities = abilities;
    }

    public List<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }
}
