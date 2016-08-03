package com.iot.tcpserver.channel;

import com.iot.tcpserver.codec.BaseMsg;
import com.iot.tcpserver.util.CryptUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServerHandler extends SimpleChannelInboundHandler<BaseMsg> {

    private static final BaseMsg HEARTBEAT_MSG = new BaseMsg(ServerEnv.CMD_HEARTBEAT,null);
    Logger log = LoggerFactory.getLogger(TcpServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //客户端连接之后推送RSA的公钥
        BaseMsg msg = new BaseMsg(ServerEnv.CMD_PUSH_RSA_PUB_KEY,ServerEnv.PUBLIC_KEY.getBytes("UTF-8"));
        ctx.writeAndFlush(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseMsg baseMsg) throws Exception {
        //System.out.println("========recv::"+baseMsg.toString());
        log.info("========recv::"+baseMsg.toString());
        switch (baseMsg.getCmd()){
            case ServerEnv.CMD_HEARTBEAT:
                ctx.writeAndFlush(HEARTBEAT_MSG);
                break;
            case ServerEnv.CMD_SEND_AES_KEY:
                byte[] aesKey = CryptUtil.rsaDecryptByPrivate(baseMsg.getData(),ServerEnv.PRIVATE_KEY);
                ctx.channel().attr(ServerEnv.KEY).set(aesKey);
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
