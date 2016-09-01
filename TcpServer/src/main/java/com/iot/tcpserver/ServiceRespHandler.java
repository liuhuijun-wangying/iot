package com.iot.tcpserver;

import com.alibaba.fastjson.JSONObject;
import com.iot.common.constant.Cmds;
import com.iot.common.constant.RespCode;
import com.iot.common.constant.Topics;
import com.iot.common.kafka.BaseKafkaConsumer;
import com.iot.common.model.BaseMsg;
import com.iot.common.model.KafkaMsg;
import com.iot.common.util.JsonUtil;
import com.iot.tcpserver.client.AppClient;
import com.iot.tcpserver.client.Client;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zc on 16-8-8.
 */
public class ServiceRespHandler implements BaseKafkaConsumer.KafkaProcessor{

    private static final Logger logger = LoggerFactory.getLogger(ServiceRespHandler.class);

    @Override
    public void process(String topic, Integer key, KafkaMsg.KafkaMsgPb value) {
        if(key==null || value==null){
            return;
        }

        ChannelHandlerContext ctx = null;
        if (Topics.TOPIC_IM_RESP.equals(topic)){
            ctx = CtxPool.getClient(value.getClientId());
        }else if (Topics.TOPIC_SERVICE_RESP.equals(topic)){
            ctx = CtxPool.getContext(value.getChannelId());
        }

        if(ctx==null){//不是此server
            return;
        }

        if(key == Cmds.CMD_APP_AUTH){
            //server需要特殊处理，记录app auth信息
            doAppAuth(value,ctx);
            return;
        }

        //原样返回
        BaseMsg.BaseMsgPb.Builder baseMsg = BaseMsg.BaseMsgPb.newBuilder();
        if (key == Cmds.CMD_IM_PUSH){
            baseMsg.setIsEncrypt(true);
        }
        baseMsg.setMsgId(value.getMsgId());
        baseMsg.setCmd(key);
        baseMsg.setData(value.getData());
        baseMsg.setIsEncrypt(value.getIsEncrypt());
        ctx.writeAndFlush(baseMsg);

    }

    private void doAppAuth(KafkaMsg.KafkaMsgPb value, ChannelHandlerContext ctx){
        JSONObject json = JsonUtil.bytes2Json(value.getData().toByteArray());
        if (json==null){
            return;
        }
        int statusCode = json.getIntValue("code");
        if(statusCode == RespCode.COMMON_OK){//login ok
            String username = json.getString("username");
            ChannelHandlerContext oldCtx = CtxPool.getClient(username);
            if(oldCtx!=null && !oldCtx.channel().id().asLongText().equals(ctx.channel().id().asLongText())){
                oldCtx.writeAndFlush(BaseMsg.BaseMsgPb.newBuilder().setCmd(Cmds.CMD_ANOTHOR_LOGIN));
                try {
                    oldCtx.close().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.warn("old ctx has been closed due to another login");
            }

            Client client = new AppClient(username,json.getString("version"));
            ctx.channel().attr(ServerEnv.CLIENT).set(client);
            CtxPool.putClient(username,ctx);

            json.remove("version");
            json.remove("username");
        }
        BaseMsg.BaseMsgPb.Builder baseMsg = BaseMsg.BaseMsgPb.newBuilder();
        baseMsg.setMsgId(value.getMsgId());
        baseMsg.setCmd(Cmds.CMD_APP_AUTH);
        baseMsg.setData(value.getData());
        baseMsg.setIsEncrypt(value.getIsEncrypt());
        ctx.writeAndFlush(baseMsg);
    }
}
