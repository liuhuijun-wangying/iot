package com.iot.tcpserver.codec;

import com.iot.tcpserver.channel.ServerEnv;
import com.iot.tcpserver.util.CompressUtil;
import com.iot.tcpserver.util.CryptUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

//TODO 加密、压缩的异常处理
public class BaseMsgEncoder extends MessageToByteEncoder<BaseMsg>{

    @Override
    protected void encode(ChannelHandlerContext ctx, BaseMsg msg, ByteBuf out) throws Exception {

        out.writeShort(msg.getCmd());
        out.writeLong(msg.getMsgId());
        out.writeByte(msg.getCompressType());
        out.writeByte(msg.isEncrypt()?1:0);

        //先加密
        byte[] encryptedBytes;
        if(msg.isEncrypt()){
            byte[] aesKey = ctx.channel().attr(ServerEnv.KEY).get();
            encryptedBytes = CryptUtil.aesEncrypt(msg.getData(), aesKey);
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

        if(compressedBytes!=null){
            out.writeBytes(compressedBytes);
        }

        ctx.flush();
    }
}
