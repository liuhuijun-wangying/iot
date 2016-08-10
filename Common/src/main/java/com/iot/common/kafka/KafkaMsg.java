package com.iot.common.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;

/**
 * Created by zc on 16-8-8.
 */
public class KafkaMsg {

    private long msgId;
    private String channelId;
    private byte[] data;

    public KafkaMsg() {
    }

    public KafkaMsg(long msgId, String channelId, byte[] data) {
        this.msgId = msgId;
        this.channelId = channelId;
        this.data = data;
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
