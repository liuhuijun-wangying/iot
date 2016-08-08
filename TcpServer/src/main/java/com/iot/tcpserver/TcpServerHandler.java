package com.iot.tcpserver;

import com.iot.common.constant.Cmds;
import com.iot.common.constant.Topics;
import com.iot.common.kafka.BaseKafkaProducer;
import com.iot.common.kafka.KafkaMsg;
import com.iot.common.util.CryptUtil;
import com.iot.tcpserver.codec.BaseMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServerHandler extends SimpleChannelInboundHandler<BaseMsg> {

    private static final BaseMsg HEARTBEAT_MSG = new BaseMsg(Cmds.CMD_HEARTBEAT,null);
    private static final Logger log = LoggerFactory.getLogger(TcpServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //客户端连接之后推送RSA的公钥
        BaseMsg msg = new BaseMsg(Cmds.CMD_PUSH_RSA_PUB_KEY,ServerEnv.PUBLIC_KEY.getBytes("UTF-8"));
        ctx.writeAndFlush(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseMsg baseMsg) throws Exception {
        switch (baseMsg.getCmd()){
            case Cmds.CMD_HEARTBEAT://心跳包
                ctx.writeAndFlush(HEARTBEAT_MSG);
                break;
            case Cmds.CMD_SEND_AES_KEY:
                byte[] aesKey = CryptUtil.rsaDecryptByPrivate(baseMsg.getData(),ServerEnv.PRIVATE_KEY);
                //密钥协商完毕,成功返回1,否则返回0
                if(aesKey!=null && aesKey.length!=0){
                    ctx.channel().attr(ServerEnv.KEY).set(aesKey);
                    ctx.writeAndFlush(new BaseMsg(Cmds.CMD_SEND_AES_KEY,new byte[]{1}));
                }else{
                    ctx.writeAndFlush(new BaseMsg(Cmds.CMD_SEND_AES_KEY,new byte[]{0}));
                }
                break;
            default:
                log.info("========recv::"+baseMsg.toString());
                KafkaMsg kafkaMsg = new KafkaMsg(baseMsg.getMsgId(),baseMsg.getData());
                BaseKafkaProducer.getInstance().send(getTopic(baseMsg.getCmd()),baseMsg.getCmd(),kafkaMsg);
                break;
        }
    }

    private static String getTopic(short cmd){
        if(cmd<100){
            return null;
        }
        if(cmd<200){
            return Topics.TOPIC_ACCOUNT;
        }
        if(cmd<300){
            return Topics.TOPIC_IM;
        }
        return null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("exceptionCaught",cause);
        ctx.close();
    }
}
