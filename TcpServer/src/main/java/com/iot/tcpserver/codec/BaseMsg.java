package com.iot.tcpserver.codec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iot.common.util.TextUtil;

import java.nio.charset.StandardCharsets;

public class BaseMsg {

    public static final byte COMPRESS_NONE = 0;
    public static final byte COMPRESS_GZIP = 1;
    public static final byte COMPRESS_ZIP = 2;

    private short cmd;
    private long msgId;
    private byte compressType;
    private boolean isEncrypt;
    private byte[] data;

    public BaseMsg(short cmd, long msgId) {
        this(cmd,msgId,COMPRESS_NONE,false,null);
    }

    public BaseMsg(short cmd, long msgId, byte[] data) {
        this(cmd,msgId,COMPRESS_NONE,false,data);
    }

    public BaseMsg(short cmd, long msgId, byte compressType, boolean isEncrypt, byte[] data) {
        this.cmd = cmd;
        this.msgId = msgId;
        this.compressType = compressType;
        this.isEncrypt = isEncrypt;
        this.data = data;
    }

    public JSONObject getJsonData(){
        if(TextUtil.isEmpty(data)){
            return null;
        }
        return JSON.parseObject(new String(data, StandardCharsets.UTF_8));
    }

    public BaseMsg setJsonData(JSONObject json){
        if(json==null){
            return this;
        }
        this.data = json.toJSONString().getBytes(StandardCharsets.UTF_8);
        return this;
    }

    public short getCmd() {
        return cmd;
    }

    public void setCmd(short cmd) {
        this.cmd = cmd;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public byte getCompressType() {
        return compressType;
    }

    public void setCompressType(byte compressType) {
        this.compressType = compressType;
    }

    public boolean isEncrypt() {
        return isEncrypt;
    }

    public void setEncrypt(boolean encrypt) {
        isEncrypt = encrypt;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        String dataStr;
        if(data==null || data.length==0){
            dataStr = "null";
        }else{
            dataStr = new String(data,StandardCharsets.UTF_8);
        }
        return "BaseMsg{" +
                "cmd=" + cmd +
                ", msgId=" + msgId +
                ", compressType=" + compressType +
                ", isEncrypt=" + isEncrypt +
                ", data=" + dataStr +
                '}';
    }
}
