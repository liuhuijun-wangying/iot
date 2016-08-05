package com.iot.tcpserver.codec;

import com.iot.common.util.CompressUtil;
import com.iot.common.util.CryptUtil;
import com.iot.common.util.TextUtil;
import com.iot.tcpserver.ServerEnv;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BaseMsgDecoder extends ByteToMessageDecoder {

    private static final Logger log = LoggerFactory.getLogger(BaseMsgDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {

        int count = byteBuf.readableBytes();
        //cmd+msgId+compressType+isEncrypt+jsonStr
        // 2 +  8  +      1     +     1   +   n
        if(count<12){
            log.error("msg len <12, len=="+count);
            //TODO should we close ctx here???
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

            if(TextUtil.isEmpty(unCompressedBytes)){
                log.error("uncompress result is null, should`t reach here");
                throw new Exception("unreachable");
            }
            //再解密
            byte[] data;
            if(isEncrypt==1){
                byte[] aesKey = ctx.channel().attr(ServerEnv.KEY).get();
                if(TextUtil.isEmpty(aesKey)){
                    log.error("aes key is null");
                    ctx.close();
                    return;
                }
                data = CryptUtil.aesDecrypt(unCompressedBytes,aesKey);
            }else{
                data = unCompressedBytes;
            }

            if(TextUtil.isEmpty(data)){
                log.error("decrypt result is null, should`t reach here");
                throw new Exception("unreachable");
            }
            result.setData(data);
        }

        list.add(result);
    }
}
