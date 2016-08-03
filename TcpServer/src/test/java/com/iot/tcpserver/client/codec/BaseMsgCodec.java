package com.iot.tcpserver.client.codec;

import com.iot.tcpserver.client.ClientEnv;
import com.iot.tcpserver.codec.BaseMsg;
import com.iot.tcpserver.util.CompressUtil;
import com.iot.tcpserver.util.CryptUtil;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class BaseMsgCodec {

    public byte[] encode(BaseMsg msg) {
        if(msg == null){
            return null;
        }

        //先加密
        byte[] encryptedBytes;
        if(msg.isEncrypt()){
            encryptedBytes = CryptUtil.aesEncrypt(msg.getData(), ClientEnv.AES_KEY);
        }else{
            encryptedBytes = msg.getData();
        }

        //再压缩
        byte[] compressedBytes;
        switch (msg.getCompressType()){
            case BaseMsg.COMPRESS_GZIP:
                compressedBytes = CompressUtil.gzip(encryptedBytes);
                break;
            case BaseMsg.COMPRESS_ZIP:
                compressedBytes = CompressUtil.zip(encryptedBytes);
                break;
            default:
                compressedBytes = encryptedBytes;
        }

        ByteBuffer out = ByteBuffer.allocate(12+(compressedBytes==null?0:compressedBytes.length));
        out.putShort(msg.getCmd());
        out.putLong(msg.getMsgId());
        out.put(msg.getCompressType());
        out.put((byte)(msg.isEncrypt()?1:0));
        if(compressedBytes!=null){
            out.put(compressedBytes);
        }
        return out.array();
    }

    public BaseMsg decode(byte[] data) {
        if(data == null || data.length==0){
            return null;
        }

        int count = data.length;
        //cmd+msgId+compressType+isEncrypt+jsonStr
        // 2 +  8  +      1     +     1   +   n
        if(count<12){
            return null;
        }
        ByteBuffer buf = ByteBuffer.allocate(count);
        buf.put(data);
        buf.flip();


        short cmd = buf.getShort();
        long msgId = buf.getLong();
        byte compressType = buf.get();
        byte isEncrypt = buf.get();

        BaseMsg result = new BaseMsg(cmd, msgId, compressType, isEncrypt==1?true:false);

        if(count>12){//has jsonStr
            byte[] dataBytes = new byte[count-12];
            buf.get(dataBytes);

            //先解压
            byte[] unCompressedBytes;
            switch (compressType){
                case BaseMsg.COMPRESS_GZIP:
                    unCompressedBytes = CompressUtil.ungzip(dataBytes);
                    break;
                case BaseMsg.COMPRESS_ZIP:
                    unCompressedBytes = CompressUtil.unzip(dataBytes);
                    break;
                default:
                    unCompressedBytes = dataBytes;
            }
            //再解密
            byte[] msgDataBytes;
            if(isEncrypt==1){
                msgDataBytes = CryptUtil.aesDecrypt(unCompressedBytes,ClientEnv.AES_KEY);
            }else{
                msgDataBytes = unCompressedBytes;
            }
            result.setData(msgDataBytes);
        }
        return result;
    }
}
