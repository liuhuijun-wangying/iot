package com.iot.tcpserver.codec;

import com.iot.common.util.CompressUtil;
import com.iot.common.util.CryptUtil;
import com.iot.common.util.TextUtil;
import com.iot.tcpserver.ServerEnv;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseMsgEncoder extends MessageToByteEncoder<BaseMsg>{

    private static final Logger log = LoggerFactory.getLogger(BaseMsgEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, BaseMsg msg, ByteBuf out) throws Exception {

        out.writeShort(msg.getCmd());
        out.writeLong(msg.getMsgId());
        out.writeByte(msg.getCompressType());
        out.writeByte(msg.isEncrypt()?1:0);
        if(TextUtil.isEmpty(msg.getData())){
            ctx.flush();
            return;
        }

        //先加密
        byte[] encryptedBytes;
        if(msg.isEncrypt()){
            byte[] aesKey = ctx.channel().attr(ServerEnv.KEY).get();
            if(TextUtil.isEmpty(aesKey)){
                log.error("aes key is null");
                ctx.close();
                return;
            }
            encryptedBytes = CryptUtil.aesEncrypt(msg.getData(), aesKey);
        }else{
            encryptedBytes = msg.getData();
        }

        if(TextUtil.isEmpty(encryptedBytes)){
            log.error("encrypt result is null, should`t reach here");
            throw new Exception("unreachable");
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

        if(!TextUtil.isEmpty(compressedBytes)){
            out.writeBytes(compressedBytes);
        }else{
            log.error("compress result is null, should`t reach here");
            throw new Exception("unreachable");
        }
        ctx.flush();
    }
}
