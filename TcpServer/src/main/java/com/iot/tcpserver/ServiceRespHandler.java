package com.iot.tcpserver;

import com.alibaba.fastjson.JSONObject;
import com.iot.common.constant.Cmds;
import com.iot.common.constant.RespCode;
import com.iot.common.kafka.KafkaMsg;
import com.iot.common.kafka.BaseKafkaConsumer;
import com.iot.common.util.TextUtil;
import com.iot.tcpserver.client.AppClient;
import com.iot.tcpserver.client.Client;
import com.iot.tcpserver.client.ClientManager;
import com.iot.tcpserver.codec.BaseMsg;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by zc on 16-8-8.
 */
public class ServiceRespHandler implements BaseKafkaConsumer.KafkaProcessor{

    @Override
    public void process(String topic, Short key, KafkaMsg value) {
        if(key==null || value==null){
            return;
        }

        //不是此server
        ChannelHandlerContext ctx = ClientManager.getInstance().getContext(value.getChannelId());
        if(ctx==null){
            return;
        }

        if(key == Cmds.CMD_APP_AUTH){
            //server需要特殊处理，记录app auth信息
            doAppAuth(value,ctx);
        }else{
            //原样返回
            ctx.writeAndFlush(new BaseMsg(key,value.getMsgId(),value.getData()));
        }
    }

    private void doAppAuth(KafkaMsg value, ChannelHandlerContext ctx){
        if(TextUtil.isEmpty(value.getData())){
            return;
        }

        JSONObject json = value.getJsonData();
        int statusCode = json.getIntValue("code");
        if(statusCode == RespCode.COMMON_OK){//login ok
            //TODO should we handle with old client???
            //Client oldClient = ctx.channel().attr(ServerEnv.CLIENT).get();

            String username = json.getString("username");
            Client client = new AppClient(json.getString("id"),json.getString("version"),username);
            ctx.channel().attr(ServerEnv.CLIENT).set(client);
            ClientManager.getInstance().onLogin(username,ctx);

            json.remove("id");
            json.remove("version");
            json.remove("username");
        }
        ctx.writeAndFlush(new BaseMsg(Cmds.CMD_APP_AUTH,value.getMsgId(),value.getData()));
    }
}
