package com.iot.basesvr.service;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by zc on 16-8-26.
 */
public interface IMService {

    //TODO add user friend
    //JSONObject addFriend(String username1, String username2)throws Exception;

    JSONObject addDevice(String username, String deviceId)throws Exception;
    JSONObject delDevice(String username, String deviceId)throws Exception;
}
