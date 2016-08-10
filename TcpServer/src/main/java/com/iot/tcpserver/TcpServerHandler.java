package com.iot.tcpserver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iot.common.constant.Cmds;
import com.iot.common.constant.Topics;
import com.iot.common.kafka.BaseKafkaProducer;
import com.iot.common.kafka.KafkaMsg;
import com.iot.common.util.CryptUtil;
import com.iot.common.util.TextUtil;
import com.iot.tcpserver.client.ClientManager;
import com.iot.tcpserver.codec.BaseMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServerHandler extends SimpleChannelInboundHandler<BaseMsg> {

    private static final Logger log = LoggerFactory.getLogger(TcpServerHandler.class);
    private static final BaseMsg HEARTBEAT_MSG = new BaseMsg(Cmds.CMD_HEARTBEAT,0,null);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ClientManager.getInstance().putContext(ctx);
        //客户端连接之后推送RSA的公钥
        BaseMsg msg = new BaseMsg(Cmds.CMD_PUSH_RSA_PUB_KEY,0,ServerEnv.PUBLIC_KEY.getBytes("UTF-8"));
        ctx.writeAndFlush(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String clientId = ctx.channel().attr(ServerEnv.ID).get();
        String username = ctx.channel().attr(ServerEnv.USERNAME).get();
        ClientManager.getInstance().onLogout(username,clientId);
        ClientManager.getInstance().removeContext(ctx.channel().id().asLongText());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseMsg baseMsg) throws Exception {
        switch (baseMsg.getCmd()){
            case Cmds.CMD_HEARTBEAT://心跳包
                ctx.writeAndFlush(HEARTBEAT_MSG);
                break;
            case Cmds.CMD_SEND_AES_KEY:
                doDiscussKey(ctx,baseMsg);
                break;
            case Cmds.CMD_DEVICE_AUTH:
                doDeviceAuth(ctx,baseMsg);
                break;
            default:
                //log.info("========recv::"+baseMsg.toString());
                KafkaMsg kafkaMsg = new KafkaMsg(baseMsg.getMsgId(),ctx.channel().id().asLongText(),baseMsg.getData());
                //log.info("========send kafka::"+kafkaMsg.toString());
                BaseKafkaProducer.getInstance().send(getTopic(baseMsg.getCmd()),baseMsg.getCmd(),kafkaMsg);
                break;
        }
    }

    private void doDiscussKey(ChannelHandlerContext ctx, BaseMsg baseMsg) throws Exception {
        byte[] aesKey = CryptUtil.rsaDecryptByPrivate(baseMsg.getData(),ServerEnv.PRIVATE_KEY);
        //密钥协商完毕,成功返回1,否则返回2
        if(aesKey!=null && aesKey.length!=0){
            ctx.channel().attr(ServerEnv.KEY).set(aesKey);
            ctx.writeAndFlush(new BaseMsg(Cmds.CMD_SEND_AES_KEY,baseMsg.getMsgId(),new byte[]{1}));
        }else{
            ctx.writeAndFlush(new BaseMsg(Cmds.CMD_SEND_AES_KEY,baseMsg.getMsgId(),new byte[]{2}));
        }
    }

    //由于这个暂时还没有DB操作，所以直接在这处理了
    private void doDeviceAuth(ChannelHandlerContext ctx, BaseMsg baseMsg) throws Exception {
        if(TextUtil.isEmpty(baseMsg.getData())){
            //ctx.close();
            return;
        }

        JSONObject deviceAuthJson = JSON.parseObject(new String(baseMsg.getData()));
        String id = deviceAuthJson.getString("id");
        if(TextUtil.isEmpty(id)){
            ctx.writeAndFlush(new BaseMsg(Cmds.CMD_DEVICE_AUTH,baseMsg.getMsgId(),new byte[]{2}));
            return;
        }
        String oldId = ctx.channel().attr(ServerEnv.ID).get();
        if(oldId!=null){//has authed
            ctx.writeAndFlush(new BaseMsg(Cmds.CMD_DEVICE_AUTH,baseMsg.getMsgId(),new byte[]{2}));
            return;
        }

        ctx.channel().attr(ServerEnv.ID).set(id);
        ctx.channel().attr(ServerEnv.VERSION).set(deviceAuthJson.getString("version"));
        ctx.channel().attr(ServerEnv.TYPE).set(ServerEnv.CLIENT_TYPE_DEVICE);
        ctx.writeAndFlush(new BaseMsg(Cmds.CMD_DEVICE_AUTH,baseMsg.getMsgId(),new byte[]{1}));//auth ok
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
