package com.iot.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * Created by zc on 16-8-26.
 */
public class JsonUtil {

    public static JSONObject buildCommonResp(int code, String msg){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        return json;
    }

    public static JSONObject Bytes2Json(byte[] bs){
        if(TextUtil.isEmpty(bs)){
            return new JSONObject();
        }
        return JSON.parseObject(new String(bs, StandardCharsets.UTF_8));
    }

    public static JSONObject String2Json(String str){
        if(TextUtil.isEmpty(str)){
            return new JSONObject();
        }
        return JSON.parseObject(str);
    }

    public static byte[] Json2Bytes(JSONObject jsonObject){
        if (jsonObject==null){
            return new byte[]{};
        }
        return jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8);
    }

    public static String Json2String(JSONObject jsonObject){
        if (jsonObject==null){
            return "";
        }
        return jsonObject.toJSONString();
    }
}
