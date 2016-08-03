package com.iot.tcpserver.codec;

import com.iot.tcpserver.channel.ServerEnv;
import com.iot.tcpserver.util.CompressUtil;
import com.iot.tcpserver.util.CryptUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

//TODO 解密、解缩的异常处理
public class BaseMsgDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {

        int count = byteBuf.readableBytes();
        //cmd+msgId+compressType+isEncrypt+jsonStr
        // 2 +  8  +      1     +     1   +   n
        if(count<12){
            return;
        }
        short cmd = byteBuf.readShort();
        long msgId = byteBuf.readLong();
        byte compressType = byteBuf.readByte();
        byte isEncrypt = byteBuf.readByte();

        BaseMsg result = new BaseMsg(cmd, msgId, compressType, isEncrypt==1?true:false);

        if(count>12){//has jsonStr
            byte[] dataBytes = new byte[count-12];
            byteBuf.readBytes(dataBytes);

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
            byte[] data = null;
            if(isEncrypt==1){
                byte[] aesKey = ctx.channel().attr(ServerEnv.KEY).get();
                data = CryptUtil.aesDecrypt(unCompressedBytes,aesKey);
            }else{
                data = unCompressedBytes;
            }
            result.setData(data);
        }

        list.add(result);
    }
}
