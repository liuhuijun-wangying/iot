package com.iot.basesvr.service;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by zc on 16-8-26.
 */
public interface IMService {

    JSONObject addFriend(String from, String to);
}
