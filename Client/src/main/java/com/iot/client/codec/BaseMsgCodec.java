package com.iot.client.codec;

import com.google.protobuf.ByteString;
import com.iot.client.ClientEnv;
import com.iot.common.model.BaseMsg;
import com.iot.common.util.CompressUtil;
import com.iot.common.util.CryptUtil;
import com.iot.common.util.TextUtil;

import java.nio.ByteBuffer;

public class BaseMsgCodec {

    private final BaseMsg.BaseMsgPb prototype = BaseMsg.BaseMsgPb.getDefaultInstance();

    public byte[] encode(BaseMsg.BaseMsgPbOrBuilder msg) throws Exception{
        if (msg==null){
            return null;
        }

        BaseMsg.BaseMsgPb.Builder result = null;
        if (msg instanceof BaseMsg.BaseMsgPb){
            result = ((BaseMsg.BaseMsgPb)msg).toBuilder();
        }else if (msg instanceof BaseMsg.BaseMsgPb.Builder){
            result = (BaseMsg.BaseMsgPb.Builder)msg;
        }

        if (result.getIsEncrypt()){
            if(!result.getData().isEmpty()){
                byte[] encryptedData = CryptUtil.aesEncrypt(result.getData().toByteArray(), ClientEnv.AES_KEY);
                result.setData(ByteString.copyFrom(encryptedData));
            }
        }
        return result.build().toByteArray();
    }

    public BaseMsg.BaseMsgPb decode(byte[] data) throws Exception{
        if (TextUtil.isEmpty(data)){
            return null;
        }
        BaseMsg.BaseMsgPb result = prototype.getParserForType().parseFrom(data);
        if (result.getIsEncrypt()){
            if(!result.getData().isEmpty()){
                byte[] decryptedData = CryptUtil.aesDecrypt(result.getData().toByteArray(),ClientEnv.AES_KEY);
                return result.toBuilder().setData(ByteString.copyFrom(decryptedData)).build();
            }
        }
        return result;
    }
}
