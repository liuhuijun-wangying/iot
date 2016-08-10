package com.iot.tcpserver.codec;

import java.io.UnsupportedEncodingException;

public class BaseMsg {

    public static final byte COMPRESS_NONE = 0;
    public static final byte COMPRESS_GZIP = 1;
    public static final byte COMPRESS_ZIP = 2;

    private short cmd;
    private long msgId;
    private byte compressType;
    private boolean isEncrypt;
    private byte[] data;

    public BaseMsg(short cmd, long msgId, byte[] data) {
        this.cmd = cmd;
        this.msgId = msgId;
        this.compressType = COMPRESS_NONE;
        this.isEncrypt = false;
        this.data = data;
    }

    public BaseMsg(short cmd, long msgId, byte compressType, boolean isEncrypt) {
        this.cmd = cmd;
        this.msgId = msgId;
        this.compressType = compressType;
        this.isEncrypt = isEncrypt;
    }

    public BaseMsg(short cmd, boolean isEncrypt, byte[] data) {
        this.cmd = cmd;
        this.msgId = 0;
        this.compressType = COMPRESS_NONE;
        this.isEncrypt = isEncrypt;
        this.data = data;
    }

    public BaseMsg(short cmd, long msgId, byte compressType, boolean isEncrypt, byte[] data) {
        this.cmd = cmd;
        this.msgId = msgId;
        this.compressType = compressType;
        this.isEncrypt = isEncrypt;
        this.data = data;
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
            try {
                dataStr = new String(data,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                dataStr = "null";
                e.printStackTrace();
            }
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
