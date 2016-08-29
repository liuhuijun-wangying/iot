package com.iot.basesvr.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.iot.common.constant.RespCode;
import com.iot.common.util.JsonUtil;
import com.iot.common.util.TextUtil;
import com.iot.basesvr.service.IMService;
import org.springframework.stereotype.Service;

/**
 * Created by zc on 16-8-26.
 */
@Service
public class IMServiceImpl implements IMService {


    @Override
    public JSONObject addFriend(String username1, String username2) throws Exception{
        if(TextUtil.isEmpty(username1) || TextUtil.isEmpty(username2)){
            throw new NullPointerException("param is null");
        }

        return null;
    }

    @Override
    public JSONObject addDevice(String username, String deviceId) throws Exception {
        if(TextUtil.isEmpty(username) || TextUtil.isEmpty(deviceId)){
            throw new NullPointerException("param is null");
        }

        return null;
    }
}
