package com.iot.common.util;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by zc on 16-8-26.
 */
public class RespUtil {

    public static JSONObject buildCommonResp(int code, String msg){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        return json;
    }
}
