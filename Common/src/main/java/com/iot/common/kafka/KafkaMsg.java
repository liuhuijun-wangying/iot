package com.iot.common.kafka;

/**
 * Created by zc on 16-8-8.
 */
public class KafkaMsg {

    private long msgId;
    private byte[] data;

    public KafkaMsg() {
    }

    public KafkaMsg(long msgId, byte[] data) {
        this.msgId = msgId;
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
}
