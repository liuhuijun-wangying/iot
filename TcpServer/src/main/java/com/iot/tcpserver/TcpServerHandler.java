package com.iot.tcpserver;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.iot.common.constant.Cmds;
import com.iot.common.constant.RespCode;
import com.iot.common.constant.Topics;
import com.iot.common.kafka.BaseKafkaProducer;
import com.iot.common.model.BaseMsg;
import com.iot.common.model.KafkaMsg;
import com.iot.common.util.JsonUtil;
import com.iot.tcpserver.client.Client;
import com.iot.tcpserver.service.NativeService;
import com.iot.tcpserver.util.Converter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServerHandler extends SimpleChannelInboundHandler<BaseMsg.BaseMsgPb> {

    private static final Logger log = LoggerFactory.getLogger(TcpServerHandler.class);
    private static BaseMsg.BaseMsgPb.Builder HEARTBEAT_MSG;

    public TcpServerHandler(){
        HEARTBEAT_MSG = BaseMsg.BaseMsgPb.newBuilder().setCmd(Cmds.CMD_HEARTBEAT);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        CtxPool.putContext(ctx);
        //客户端连接之后推送RSA的公钥
        BaseMsg.BaseMsgPb.Builder result = BaseMsg.BaseMsgPb.newBuilder();
        result.setCmd(Cmds.CMD_PUSH_RSA_PUB_KEY);
        result.setData(ByteString.copyFrom(ServerEnv.PUBLIC_KEY));
        ctx.writeAndFlush(result);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        CtxPool.removeContext(ctx);
        CtxPool.removeClient(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseMsg.BaseMsgPb baseMsg) throws Exception {
        switch (baseMsg.getCmd()){
            case Cmds.CMD_HEARTBEAT://心跳包
                ctx.writeAndFlush(HEARTBEAT_MSG);
                break;
            case Cmds.CMD_SEND_AES_KEY:
                NativeService.doDiscussKey(ctx, baseMsg);
                break;
            case Cmds.CMD_DEVICE_AUTH:
                NativeService.doDeviceAuth(ctx, baseMsg);
                break;
            case Cmds.CMD_LOGOUT:
                ctx.close();
                break;
            default:
                doDefault(ctx,baseMsg);
                break;
        }
    }

    private static void doDefault(ChannelHandlerContext ctx, BaseMsg.BaseMsgPb baseMsg){
        Client client = ctx.channel().attr(ServerEnv.CLIENT).get();
        if(baseMsg.getCmd()>=200 && client==null){//not login
            JSONObject respJson = JsonUtil.buildCommonResp(RespCode.COMMON_NOT_LOGIN,"you need login first");
            BaseMsg.BaseMsgPb.Builder result = Converter.req2Resp(baseMsg);
            result.setData(ByteString.copyFrom(JsonUtil.json2Bytes(respJson)));
            ctx.writeAndFlush(result);
            log.warn("user not login when do cmd="+baseMsg.getCmd());
            return;
        }

        if (baseMsg.getCmd()==Cmds.CMD_IM){//CMD_IM is 200
            //1. resp to client directly
            BaseMsg.BaseMsgPb.Builder imBuilder = Converter.req2Resp(baseMsg);
            ctx.writeAndFlush(imBuilder);
            //2. send to im server
            KafkaMsg.KafkaMsgPb.Builder imKafka = Converter.baseMsg2KafkaMsg(baseMsg);
            imKafka.setClientId(client.getId());
            BaseKafkaProducer.getInstance().send(Topics.TOPIC_IM,baseMsg.getCmd(),imKafka);
            return;
        }
        if (baseMsg.getCmd()==Cmds.CMD_IM_PUSH){
            //send to im server
            KafkaMsg.KafkaMsgPb.Builder imKafka = Converter.baseMsg2KafkaMsg(baseMsg);
            imKafka.setClientId(client.getId());
            BaseKafkaProducer.getInstance().send(Topics.TOPIC_IM,baseMsg.getCmd(),imKafka);
            return;
        }


        KafkaMsg.KafkaMsgPb.Builder kafkaMsg = Converter.baseMsg2KafkaMsg(baseMsg);
        kafkaMsg.setChannelId(ctx.channel().id().asLongText());
        if (client!=null){
            kafkaMsg.setClientId(client.getId());
        }
        BaseKafkaProducer.getInstance().send(Topics.TOPIC_SERVICE,baseMsg.getCmd(),kafkaMsg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("exceptionCaught",cause);
        BaseMsg.BaseMsgPb.Builder result = BaseMsg.BaseMsgPb.newBuilder();
        result.setCmd(Cmds.CMD_EXP);
        JSONObject json = JsonUtil.buildCommonResp(RespCode.COMMON_EXCEPTION,cause.getMessage());
        result.setData(ByteString.copyFrom(JsonUtil.json2Bytes(json)));
        ctx.writeAndFlush(result);
        //ctx.close();
    }
}
