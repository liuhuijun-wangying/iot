package com.iot.tcpserver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.iot.common.constant.Cmds;
import com.iot.common.constant.RespCode;
import com.iot.common.constant.Topics;
import com.iot.common.kafka.BaseKafkaProducer;
import com.iot.common.model.BaseMsg;
import com.iot.common.model.KafkaMsg;
import com.iot.common.util.CryptUtil;
import com.iot.common.util.JsonUtil;
import com.iot.common.util.TextUtil;
import com.iot.tcpserver.client.Client;
import com.iot.tcpserver.client.DeviceClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

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
                doDiscussKey(ctx, baseMsg);
                break;
            case Cmds.CMD_DEVICE_AUTH:
                doDeviceAuth(ctx, baseMsg);
                break;
            case Cmds.CMD_LOGOUT:
                ctx.close();
                break;
            default:
                handleDefault(ctx,baseMsg);
                break;
        }
    }

    private static void handleDefault(ChannelHandlerContext ctx, BaseMsg.BaseMsgPb baseMsg){
        Client client = ctx.channel().attr(ServerEnv.CLIENT).get();
        if(baseMsg.getCmd()>=200 && client==null){//not login
            JSONObject respJson = JsonUtil.buildCommonResp(RespCode.COMMON_NOT_LOGIN,"you need login first");
            BaseMsg.BaseMsgPb.Builder result = BaseMsg.BaseMsgPb.newBuilder();
            result.setCmd(baseMsg.getCmd());
            result.setMsgId(baseMsg.getMsgId());
            result.setData(ByteString.copyFrom(JsonUtil.json2Bytes(respJson)));
            ctx.writeAndFlush(result);
            log.warn("user not login when do cmd="+baseMsg.getCmd());
            return;
        }

        if (baseMsg.getCmd()==Cmds.CMD_IM){//CMD_IM is 200
            //1. resp to client directly
            BaseMsg.BaseMsgPb.Builder imBuilder = BaseMsg.BaseMsgPb.newBuilder();
            imBuilder.setCmd(Cmds.CMD_IM);
            imBuilder.setMsgId(baseMsg.getMsgId());
            ctx.writeAndFlush(imBuilder);
            //2. send to im server
            KafkaMsg.KafkaMsgPb.Builder imKafka = KafkaMsg.KafkaMsgPb.newBuilder();
            imKafka.setClientId(client.getId());
            imKafka.setData(baseMsg.getData());
            BaseKafkaProducer.getInstance().send(Topics.TOPIC_IM,baseMsg.getCmd(),imKafka);
            return;
        }
        if (baseMsg.getCmd()==Cmds.CMD_IM_PUSH){
            //send to im server
            KafkaMsg.KafkaMsgPb.Builder imKafka = KafkaMsg.KafkaMsgPb.newBuilder();
            imKafka.setMsgId(baseMsg.getMsgId());
            BaseKafkaProducer.getInstance().send(Topics.TOPIC_IM,baseMsg.getCmd(),imKafka);
            return;
        }

        KafkaMsg.KafkaMsgPb.Builder kafkaMsg = KafkaMsg.KafkaMsgPb.newBuilder();
        kafkaMsg.setMsgId(baseMsg.getMsgId());
        kafkaMsg.setData(baseMsg.getData());
        kafkaMsg.setChannelId(ctx.channel().id().asLongText());
        if (client!=null){
            kafkaMsg.setClientId(client.getId());
        }
        BaseKafkaProducer.getInstance().send(Topics.TOPIC_SERVICE,baseMsg.getCmd(),kafkaMsg);
    }

    private static void doDiscussKey(ChannelHandlerContext ctx, BaseMsg.BaseMsgPb baseMsg) {
        JSONObject json;
        try{
            byte[] aesKey = CryptUtil.rsaDecryptByPrivate(baseMsg.getData().toByteArray(),ServerEnv.PRIVATE_KEY);
            if(!TextUtil.isEmpty(aesKey)){
                ctx.channel().attr(ServerEnv.KEY).set(aesKey);
                json = JsonUtil.buildCommonResp(RespCode.COMMON_OK,"ok");
            }else{
                json = JsonUtil.buildCommonResp(RespCode.COMMON_EXCEPTION,"aes key is null");
            }
        }catch (Exception e){
            json = JsonUtil.buildCommonResp(RespCode.COMMON_EXCEPTION,e.getMessage());
        }

        BaseMsg.BaseMsgPb.Builder result = BaseMsg.BaseMsgPb.newBuilder();
        result.setCmd(Cmds.CMD_SEND_AES_KEY);
        result.setMsgId(baseMsg.getMsgId());
        result.setData(ByteString.copyFrom(JsonUtil.json2Bytes(json)));
        ctx.writeAndFlush(result);
    }

    //由于这个暂时还没有DB操作，所以直接在这处理了
    private static void doDeviceAuth(ChannelHandlerContext ctx, BaseMsg.BaseMsgPb baseMsg) {
        JSONObject deviceAuthJson = JsonUtil.bytes2Json(baseMsg.getData().toByteArray());
        if(deviceAuthJson==null){
            return;
        }

        JSONObject json;

        String id = deviceAuthJson.getString("id");
        if(TextUtil.isEmpty(id)){
            json = JsonUtil.buildCommonResp(RespCode.COMMON_EXCEPTION,"id is null");
        }else{
            ChannelHandlerContext oldCtx = CtxPool.getClient(id);
            if(oldCtx!=null && !oldCtx.channel().id().asLongText().equals(ctx.channel().id().asLongText())){
                oldCtx.writeAndFlush(BaseMsg.BaseMsgPb.newBuilder().setCmd(Cmds.CMD_ANOTHOR_LOGIN));
                CtxPool.removeClient(oldCtx);
                try {
                    oldCtx.close().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.warn("close old ctx due to new connect of device");
            }

            JSONArray abilites = deviceAuthJson.getJSONArray("abilities");
            Client client = new DeviceClient(id,deviceAuthJson.getString("version"),Arrays.asList(abilites.toArray(new String[]{})));
            ctx.channel().attr(ServerEnv.CLIENT).set(client);
            json = JsonUtil.buildCommonResp(RespCode.COMMON_OK,"ok");
            CtxPool.putClient(id,ctx);
        }

        BaseMsg.BaseMsgPb.Builder result = BaseMsg.BaseMsgPb.newBuilder();
        result.setCmd(Cmds.CMD_DEVICE_AUTH);
        result.setMsgId(baseMsg.getMsgId());
        result.setData(ByteString.copyFrom(JsonUtil.json2Bytes(json)));
        ctx.writeAndFlush(result);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("exceptionCaught",cause);
        BaseMsg.BaseMsgPb.Builder result = BaseMsg.BaseMsgPb.newBuilder();
        result.setCmd(Cmds.CMD_EXP);
        JSONObject json = JsonUtil.buildCommonResp(RespCode.COMMON_EXCEPTION,cause.getMessage());
        result.setData(ByteString.copyFrom(JsonUtil.json2Bytes(json)));
        ctx.writeAndFlush(result);
        ctx.close();
    }
}
