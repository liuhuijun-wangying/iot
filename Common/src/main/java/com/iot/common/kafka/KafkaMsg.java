package com.iot.common.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iot.common.util.TextUtil;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by zc on 16-8-8.
 */
public class KafkaMsg {

    private String channelId;
    private long msgId;//BaseMsg里面的msgId
    private byte[] data;

    public KafkaMsg() {
    }

    public KafkaMsg(String channelId, long msgId) {
        this.msgId = msgId;
        this.channelId = channelId;
    }

    public KafkaMsg(String channelId, long msgId, byte[] data) {
        this.msgId = msgId;
        this.channelId = channelId;
        this.data = data;
    }

    public KafkaMsg setJsonData(JSONObject json){
        if(json==null){
            return this;
        }
        try {
            this.data = json.toJSONString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException cannotHappen) {
        }
        return this;
    }

    public JSONObject getJsonData(){
        if (TextUtil.isEmpty(data)){
            return null;
        }
        try {
            return JSON.parseObject(new String(data,"UTF-8"));
        } catch (UnsupportedEncodingException cannotHappen) {
            return null;
        }
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String toJsonString() {
        return JSON.toJSON(this).toString();
    }

    @Override
    public String toString() {
        return "KafkaMsg{" +
                "msgId=" + msgId +
                ", channelId='" + channelId + '\'' +
                ", data=" + new String(data) +
                '}';
    }
}
