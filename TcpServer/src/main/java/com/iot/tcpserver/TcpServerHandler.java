package com.iot.tcpserver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iot.common.constant.Cmds;
import com.iot.common.constant.RespCode;
import com.iot.common.constant.Topics;
import com.iot.common.kafka.BaseKafkaProducer;
import com.iot.common.kafka.KafkaMsg;
import com.iot.common.util.CryptUtil;
import com.iot.common.util.RespUtil;
import com.iot.common.util.TextUtil;
import com.iot.tcpserver.client.AppClient;
import com.iot.tcpserver.client.Client;
import com.iot.tcpserver.client.ClientManager;
import com.iot.tcpserver.client.DeviceClient;
import com.iot.tcpserver.codec.BaseMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TcpServerHandler extends SimpleChannelInboundHandler<BaseMsg> {

    private static final Logger log = LoggerFactory.getLogger(TcpServerHandler.class);
    private static final BaseMsg HEARTBEAT_MSG = new BaseMsg(Cmds.CMD_HEARTBEAT,0,null);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ClientManager.getInstance().putContext(ctx);
        //客户端连接之后推送RSA的公钥
        BaseMsg msg = new BaseMsg(Cmds.CMD_PUSH_RSA_PUB_KEY,0,ServerEnv.PUBLIC_KEY);
        ctx.writeAndFlush(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Client client = ctx.channel().attr(ServerEnv.CLIENT).get();
        if (client instanceof AppClient){
            ClientManager.getInstance().onLogout(((AppClient)client).getUsername(),client.getId());
        }
        ClientManager.getInstance().removeContext(ctx.channel().id().asLongText());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseMsg baseMsg) throws Exception {
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
            default:
                Client oldClient = ctx.channel().attr(ServerEnv.CLIENT).get();
                if(baseMsg.getCmd()>=200 && oldClient==null){//not login
                    JSONObject respJson = RespUtil.buildCommonResp(RespCode.COMMON_NOT_LOGIN,"you need login first");
                    BaseMsg respMsg = new BaseMsg(baseMsg.getCmd(), baseMsg.getMsgId(),
                            respJson.toJSONString().getBytes(StandardCharsets.UTF_8));
                    ctx.writeAndFlush(respMsg);
                    return;
                }
                KafkaMsg kafkaMsg = new KafkaMsg(ctx.channel().id().asLongText(),baseMsg.getMsgId(),baseMsg.getData());
                BaseKafkaProducer.getInstance().send(getTopic(baseMsg.getCmd()), baseMsg.getCmd(),kafkaMsg);
                break;
        }
    }

    private static void doDiscussKey(ChannelHandlerContext ctx, BaseMsg baseMsg) {
        JSONObject json;
        try{
            byte[] aesKey = CryptUtil.rsaDecryptByPrivate(baseMsg.getData(),ServerEnv.PRIVATE_KEY);
            if(!TextUtil.isEmpty(aesKey)){
                ctx.channel().attr(ServerEnv.KEY).set(aesKey);
                json = RespUtil.buildCommonResp(RespCode.COMMON_OK,"ok");
            }else{
                json = RespUtil.buildCommonResp(RespCode.COMMON_INVALID,"aes key is null");
            }
        }catch (Exception e){
            json = RespUtil.buildCommonResp(RespCode.COMMON_EXCEPTION,e.getMessage());
        }
        ctx.writeAndFlush(new BaseMsg(Cmds.CMD_SEND_AES_KEY, baseMsg.getMsgId()).setJsonData(json));
    }

    //由于这个暂时还没有DB操作，所以直接在这处理了
    private static void doDeviceAuth(ChannelHandlerContext ctx, BaseMsg baseMsg) {
        JSONObject deviceAuthJson = baseMsg.getJsonData();
        if(deviceAuthJson==null){
            //ctx.close();
            return;
        }

        JSONObject json;

        String id = deviceAuthJson.getString("id");
        if(TextUtil.isEmpty(id)){
            json = RespUtil.buildCommonResp(RespCode.COMMON_INVALID,"id is null");
        }else{
            //TODO should we handle with old client???
            //Client oldClient = ctx.channel().attr(ServerEnv.CLIENT).get();

            JSONArray abilites = deviceAuthJson.getJSONArray("abilities");
            Client client = new DeviceClient(id,deviceAuthJson.getString("version"),Arrays.asList(abilites.toArray(new String[]{})));
            ctx.channel().attr(ServerEnv.CLIENT).set(client);
            json = RespUtil.buildCommonResp(RespCode.COMMON_OK,"ok");
        }
        ctx.writeAndFlush(new BaseMsg(Cmds.CMD_DEVICE_AUTH, baseMsg.getMsgId()).setJsonData(json));
    }

    private static String getTopic(short cmd){
        if(cmd<100){
            return null;
        }
        return Topics.TOPIC_SERVICE;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("exceptionCaught",cause);
        ctx.close();
    }
}
